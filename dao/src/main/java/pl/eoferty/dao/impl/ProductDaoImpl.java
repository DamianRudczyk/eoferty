package pl.eoferty.dao.impl;

import org.primefaces.model.SortOrder;
import pl.eoferty.blc.entity.Product;
import pl.eoferty.dao.ProductDao;
import pl.eoferty.dao.query.FilterOperator;
import pl.eoferty.dao.query.GlobalFilterOperators;

import javax.annotation.PostConstruct;
import javax.ejb.Stateless;
import javax.persistence.criteria.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Damian on 2015-03-17.
 */
@Stateless
public class ProductDaoImpl extends AbstractDaoImpl<Product, Long> implements ProductDao {

	public ProductDaoImpl() {
		super(Product.class);
	}

	private final Map<String, FilterOperator> filterOperator = new HashMap<String, FilterOperator>();

	@Override
	protected void initColumnsMapping() {
	}

	@PostConstruct
	private void init() {
		filterOperator.put(GlobalFilterOperators.NAME, FilterOperator.LIKE);
	}

	@Override
	public int count(Map<String, Object> filters) {
		CriteriaBuilder criteriaBuilder = em.getCriteriaBuilder();
		CriteriaQuery<Long> criteriaQuery = criteriaBuilder.createQuery(Long.class);
		Root<Product> from = criteriaQuery.from(Product.class);
		criteriaQuery.where(getFilterCondition(criteriaBuilder, from, filters));
		criteriaQuery.select(criteriaBuilder.count(from));
		return em.createQuery(criteriaQuery).getSingleResult().intValue();
	}

	@Override
	public List<Product> getResultList(int first, int pageSize, String sortField, SortOrder sortOrder, Map<String, Object> filters) {
		CriteriaBuilder criteriaBuilder = em.getCriteriaBuilder();
		CriteriaQuery<Product> criteriaQuery = criteriaBuilder.createQuery(Product.class);
		Root<Product> from = criteriaQuery.from(Product.class);
		criteriaQuery.where(getFilterCondition(criteriaBuilder, from, filters));
		if (sortField != null) {
			if (sortOrder == SortOrder.ASCENDING) {
				criteriaQuery.orderBy(criteriaBuilder.asc(from.get(sortField)));
			} else if (sortOrder == SortOrder.DESCENDING) {
				criteriaQuery.orderBy(criteriaBuilder.desc(from.get(sortField)));
			}
		}
		return em.createQuery(criteriaQuery).setFirstResult(first).setMaxResults(pageSize).getResultList();
	}

	private Predicate getFilterCondition(CriteriaBuilder criteriaBuilder, Root<Product> from, Map<String, Object> filters) {
		Predicate filterCondition = criteriaBuilder.conjunction();
		for (Map.Entry<String, Object> filter : filters.entrySet()) {
			if (!filter.getValue().equals("")) {
				Path<String> path = from.get(filter.getKey());
				switch (filterOperator.get(filter.getKey().toUpperCase())) {
					case EQUALS:
						filterCondition = criteriaBuilder.and(filterCondition, criteriaBuilder.equal(path, filter.getValue()));
						break;
					case LIKE:
						filterCondition = criteriaBuilder.and(filterCondition, criteriaBuilder.like(path, filter.getValue().toString()));
						break;
					case NOT_EQUALS:
						filterCondition = criteriaBuilder.and(filterCondition, criteriaBuilder.notEqual(path, filter.getValue()));
						break;
				}
			}
		}
		return filterCondition;
	}
}