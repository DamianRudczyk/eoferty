package pl.eoferty.dao.impl;

/**
 * Created by Damian on 2015-03-16.
 */

import pl.eoferty.dao.AbstractDao;
import pl.eoferty.dao.exceptions.DaoException;
import pl.eoferty.dao.query.*;

import javax.persistence.CacheStoreMode;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.*;
import java.io.Serializable;
import java.util.*;

/**
 *
 * @author Marek
 * @param <E>
 * @param <PK>
 */
public abstract class AbstractDaoImpl<E, PK extends Serializable> implements AbstractDao<E, PK> {

    protected final String ROOT_COLUMN_NAME = "ROOT_COLUMN";

    protected final String ACTIVE_FIELD = "active";

    @PersistenceContext
    protected EntityManager em;

    private final Class<E> entityClass;

    private final Map<String, ColumnMapping> filterKeysMapping;

    protected final Map<Object, FilterOperator> filterOperators;

    protected AbstractDaoImpl(Class<E> entityClass) {
        super();
        filterKeysMapping = new HashMap<String, ColumnMapping>();
        filterOperators = new HashMap<Object, FilterOperator>();
        filterOperators.putAll(GlobalFilterOperators.getInstance().getFilterOperators());
        this.entityClass = entityClass;
        initColumnsMapping();
    }

    protected abstract void initColumnsMapping();

    @Override
    public E save(final E instance) {
        try {
            em.persist(instance);
            em.flush();
            return instance;
        } catch (RuntimeException e) {
            throw new DaoException("Błąd podczas zapisu encji : " + e, "001");
        }
    }

    @Override
    public E find(PK id) {
        try {
            E result = em.find(entityClass, id);
            return result;
        } catch (RuntimeException e) {
            throw new DaoException("Błąd podczas pobierania encji za pomocą klucza głównego : " + e, "002");
        }
    }

    @Override
    public void delete(E instance) {
        try {
            em.remove(em.merge(instance));
            em.flush();
        } catch (RuntimeException e) {
            throw new DaoException("Błąd podczas usuwania encji : " + e, "003");
        }

    }

    @Override
    public E update(E instance) {
        try {
            E merge = em.merge(instance);
            em.flush();
            return merge;
        } catch (RuntimeException e) {
            throw new DaoException("Błąd podczas aktualizacji encji : " + e, "004");
        }
    }

    @Override
    public void refresh(E instance) {
        em.refresh(instance);
    }

    @Override
    public List<E> findAll() {
        String baseQuery = "SELECT o FROM " + entityClass.getName() + " o";
        try {
            TypedQuery<E> resultQuery = em.createQuery(baseQuery, entityClass);
            return resultQuery.getResultList();
        } catch (RuntimeException e) {
            throw new DaoException("Błąd podczas pobierania listy encji : " + e, "005");
        }
    }

    @Override
    public List<E> findAll(String baseQuery) {
        try {
            TypedQuery<E> resultQuery = em.createQuery(baseQuery, entityClass);
            return resultQuery.getResultList();
        } catch (RuntimeException e) {
            throw new DaoException("Błąd podczas pobierania listy encji : " + e, "006");
        }
    }

    @Override
    public List<E> findAll(String baseQuery, Map<String, Object> paramaters) {
        try {
            TypedQuery<E> resultQuery = em.createQuery(baseQuery, entityClass);
            for (String key : paramaters.keySet()) {
                resultQuery.setParameter(key, paramaters.get(key));
            }
            return resultQuery.getResultList();
        } catch (RuntimeException e) {
            throw new DaoException("Błąd podczas pobierania listy encji : " + e, "007");
        }
    }

    @Override
    public List<E> findByQueryParameters(QueryParameters queryParameters) {
        return findByQueryParameters(null, queryParameters);
    }

    @Override
    public List<E> findByQueryParameters(QueryParameters queryParameters, boolean useCache) {
        return findByQueryParameters(null, queryParameters, useCache, false);
    }

    @Override
    public Long getCountByQueryParameters(QueryParameters queryParameters) {
        return getCountByQueryParameters(null, queryParameters);
    }

    @Override
    public List<E> findByQueryParameters(List<JoinColumn> joinColumns, QueryParameters queryParameters) {
        return findByQueryParameters(joinColumns, queryParameters, true, false);
    }

    @Override
    public List<E> findDistinctByQueryParameters(List<JoinColumn> joinColumns, QueryParameters queryParameters) {
        return findByQueryParameters(joinColumns, queryParameters, true, true);
    }

    @Override
    public List<E> findByQueryParameters(List<JoinColumn> joinColumns, QueryParameters queryParameters, boolean useCache, boolean distinct) {
        try {
            CriteriaBuilder criteriaBuilder = em.getCriteriaBuilder();
            CriteriaQuery criteriaQuery = criteriaBuilder.createQuery(entityClass);
            Root<E> root = criteriaQuery.from(entityClass);
            if (distinct) {
                criteriaQuery.select(root).distinct(true);
            } else {
                criteriaQuery.select(root);
            }
            fillCriteria(criteriaQuery, root, joinColumns, queryParameters);
            TypedQuery<E> query = em.createQuery(criteriaQuery);
            query.setFirstResult(queryParameters.getFisrtResult());
            query.setMaxResults(queryParameters.getMaxResult());
            if (!useCache) {
                query.setHint("javax.persistence.cache.storeMode", CacheStoreMode.REFRESH);
            }
            return query.getResultList();
        } catch (RuntimeException e) {
            throw new DaoException("Błąd podczas pobierania listy encji : " + e, "008");
        }

    }

    @Override
    public Long getCountByQueryParameters(List<JoinColumn> joinColumns, QueryParameters queryParameters) {
        CriteriaBuilder criteriaBuilder = em.getCriteriaBuilder();
        CriteriaQuery criteriaQuery = criteriaBuilder.createQuery(entityClass);
        Root<E> root = criteriaQuery.from(entityClass);
        criteriaQuery.select(criteriaBuilder.count(root));
        fillCriteria(criteriaQuery, root, joinColumns, queryParameters);
        TypedQuery<Long> query = em.createQuery(criteriaQuery);
        return query.getSingleResult();
    }

    private void fillCriteria(CriteriaQuery query, Root<E> root, List<JoinColumn> joinColumns, QueryParameters queryParameters) {
        Map<String, Join> joins = new HashMap<String, Join>();
        if (joinColumns != null && joinColumns.size() > 0) {
            for (JoinColumn joinColumn : joinColumns) {
                joins.put(joinColumn.getFieldName(), root.join(joinColumn.getFieldName(), joinColumn.getJoinType()));
            }
        }
        completeWherePart(queryParameters.getFilters(), root, joins, query);
        completeSortPart(queryParameters.getSorts(), root, joins, query);
    }

    protected void completeSortPart(List<SortOption> sorts, Root<E> root, Map<String, Join> joins, CriteriaQuery query) {
        if (sorts != null && sorts.size() > 0) {
            List<Order> orders = new ArrayList<Order>();
            for (SortOption s : sorts) {
                if (filterKeysMapping.containsKey(s.getSortField())) {
                    ColumnMapping columnMapping = filterKeysMapping.get(s.getSortField());
                    if (columnMapping.getJoinFieldName() instanceof String) {
                        orders.add(createOrder(root, joins, columnMapping.getJoinColumnName(), (String) columnMapping.getJoinFieldName(), s.getSortDirection()));
                    } else if (columnMapping.getJoinFieldName() instanceof String[]) {
                        String[] fieldNames = (String[]) columnMapping.getJoinFieldName();
                        for (String fieldName : fieldNames) {
                            if (filterKeysMapping.containsKey(fieldName)) {
                                ColumnMapping columnMapping2 = filterKeysMapping.get(fieldName);
                                orders.add(createOrder(root, joins, columnMapping2.getJoinColumnName(), (String) columnMapping2.getJoinFieldName(), s.getSortDirection()));
                            }
                        }
                    }
                }
            }
            query.orderBy(orders);
        }
    }

    protected void completeWherePart(List<FilterOption> filters, Root<E> root, Map<String, Join> joins, CriteriaQuery query) {
        CriteriaBuilder cBuilder = em.getCriteriaBuilder();
        if (filters != null && filters.size() > 0) {
            Predicate where = cBuilder.conjunction();
            for (FilterOption f : filters) {
                Path path = null;
                if (filterKeysMapping.containsKey(f.getField())) {
                    ColumnMapping columnMapping = filterKeysMapping.get(f.getField());
                    if (columnMapping.getJoinFieldName() instanceof String) {
                        path = createPath(root, joins, columnMapping.getJoinColumnName(), (String) columnMapping.getJoinFieldName());
                        Predicate predicate = createPredicate(path, f.getField(), f.getValue());
                        where = cBuilder.and(where, predicate);
                    } else if (columnMapping.getJoinFieldName() instanceof String[]) {
                        String[] fieldNames = (String[]) columnMapping.getJoinFieldName();
                        List<Predicate> predicates = new ArrayList<Predicate>();
                        for (String fieldName : fieldNames) {
                            if (filterKeysMapping.containsKey(fieldName)) {
                                ColumnMapping columnMapping2 = filterKeysMapping.get(fieldName);
                                path = createPath(root, joins, columnMapping2.getJoinColumnName(), (String) columnMapping2.getJoinFieldName());
                                Predicate predicate = createPredicate(path, fieldName, f.getValue());
                                predicates.add(predicate);
                            }
                        }
                        predicates.toArray(new Predicate[predicates.size()]);
                        where = cBuilder.and(where, cBuilder.or(predicates.toArray(new Predicate[predicates.size()])));
                    }
                }
            }
            query.where(where);
        }
    }

    private Order createOrder(Root<E> root, Map<String, Join> joins, String columnName, String fieldName, SortDirection sortDirection) {
        Path path = createPath(root, joins, columnName, fieldName);
        switch (sortDirection) {
            case ASCENDING:
                return em.getCriteriaBuilder().asc(path);
            case DESCENDING:
                return em.getCriteriaBuilder().desc(path);
            default:
                return em.getCriteriaBuilder().asc(path);
        }
    }

    private Path createPath(Root<E> root, Map<String, Join> joins, String joinColumnName, String joinFieldName) {
        if (joinColumnName.equals(ROOT_COLUMN_NAME)) {
            return root.get(joinFieldName);
        } else {
            if (joins.containsKey(joinColumnName)) {
                Join join = joins.get(joinColumnName);
                return join.get(joinFieldName);
            }
        }
        return null;
    }

    protected CriteriaQuery<E> createCriteriQuery() {
        CriteriaBuilder criteriaBuilder = em.getCriteriaBuilder();
        return criteriaBuilder.createQuery(entityClass);
    }

    public void putColumnMapping(String columnName, ColumnMapping columnMapping) {
        filterKeysMapping.put(columnName, columnMapping);
    }

    private Predicate createPredicate(Path path, String field, Object value) {
        CriteriaBuilder cBuilder = em.getCriteriaBuilder();
        if (filterOperators.containsKey(field)) {
            Predicate predicate = null;
            switch (filterOperators.get(field)) {
                case EQUALS:
                    predicate = cBuilder.equal(path, value);
                    break;
                case LESSER:
                    predicate = cBuilder.lessThan(path, (Date) value);
                    break;
                case GREATER:
                    predicate = cBuilder.greaterThan(path, (Date) value);
                    break;
                case LIKE:
                   // predicate = cBuilder.like(path, OracleHelper.convertMask((String) value));
                    break;
                case LIKE_NO_CASE:
                   // predicate = cBuilder.like(cBuilder.upper(path), OracleHelper.convertMask((String) value).toUpperCase());
                    break;
                case GREATER_EQUALS:
                    predicate = cBuilder.greaterThanOrEqualTo(path, (Date) value);
                    break;
                case NOT_EQUALS:
                    predicate = cBuilder.notEqual(path, value);
                    break;
                case LESSER_EQUALS:
                    predicate = cBuilder.lessThanOrEqualTo(path, (Date) value);
                    break;
                case IS_NULL:
                    predicate = cBuilder.isNull(path);
                    break;
                case IS_NOT_NULL:
                    predicate = cBuilder.isNotNull(path);
                    break;
                case IS_EMPTY:
                    predicate = cBuilder.isEmpty(path);
                    break;
                case IS_NOT_EMPTY:
                    cBuilder.isNotEmpty(path);
                    break;
                case IN:
                    if (value instanceof Object[]) {
                        List<Object> valList = Arrays.asList((Object[]) value);
                        predicate = path.in(valList);
                    }
                    if (value instanceof List) {
                        predicate = path.in((List) value);
                    }

                    break;
                case CONTAIN:
                    predicate = cBuilder.like(path, "%" + value + "%");
                    break;
                case CONTAIN_NO_CASE:
                    predicate = cBuilder.like(cBuilder.upper(path), ("%" + value + "%").toUpperCase());
                    break;
                case START_WITH:
                    predicate = cBuilder.like(path, value + "%");
                    break;
                case START_WITH_NO_CASE:
                    predicate = cBuilder.like(cBuilder.upper(path), (value + "%").toUpperCase());
                    break;
                case END_WITH:
                    predicate = cBuilder.like(path, "%" + value);
                    break;
                case END_WITH_NO_CASE:
                    predicate = cBuilder.like(cBuilder.upper(path), ("%" + value).toUpperCase());
                    break;
                default:
                    predicate = cBuilder.equal(path, value);

            }
            return predicate;
        }

        return cBuilder.equal(path, value);
    }

    @Override
    public Long getDistinctCountByColumnName(String filterKey) {
        if(!filterKeysMapping.containsKey(filterKey)) {
            return 0L;
        }
        String columnName = (String) filterKeysMapping.get(filterKey).getJoinFieldName();
        CriteriaBuilder criteriaBuilder = em.getCriteriaBuilder();
        CriteriaQuery criteriaQuery = criteriaBuilder.createQuery(entityClass);
        Root<E> root = criteriaQuery.from(entityClass);
        criteriaQuery.select(criteriaBuilder.countDistinct(root.get(columnName)));
        TypedQuery<Long> query = em.createQuery(criteriaQuery);
        return query.getSingleResult().longValue();
    }

    @Override
    public List<String> getColumnValuesAsString(String filterKey, boolean distinct) {
        if(!filterKeysMapping.containsKey(filterKey)) {
            return new ArrayList<String>();
        }
        String columnName = (String) filterKeysMapping.get(filterKey).getJoinFieldName();
        CriteriaBuilder criteriaBuilder = em.getCriteriaBuilder();
        CriteriaQuery criteriaQuery = criteriaBuilder.createQuery(entityClass);
        Root<E> root = criteriaQuery.from(entityClass);
        criteriaQuery.select(root.get(columnName)).distinct(distinct);
        TypedQuery<String> query = em.createQuery(criteriaQuery);
        return query.getResultList();
    }

}
