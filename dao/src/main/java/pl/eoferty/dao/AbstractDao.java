package pl.eoferty.dao;

import pl.eoferty.dao.query.JoinColumn;
import pl.eoferty.dao.query.QueryParameters;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * Created by Damian on 2015-03-16.
 */
public interface AbstractDao<E, PK extends Serializable> {

    public E save(E instance);

    public E find(PK id);

    public void delete(E instance);

    public E update(E instance);

    public List<E> findAll();

    public List<E> findAll(String baseQuery);

    public List<E> findAll(String baseQuery, Map<String, Object> paramaters);

    public List<E> findByQueryParameters(QueryParameters queryParameters);

    public List<E> findByQueryParameters(QueryParameters queryParameters, boolean useCache);

    public Long getCountByQueryParameters(QueryParameters queryParameters);

    public List<E> findByQueryParameters(List<JoinColumn> joinColumns, QueryParameters queryParameters);

    public List<E> findByQueryParameters(List<JoinColumn> joinColumns, QueryParameters queryParameters, boolean useCache, boolean distinct);

    public Long getCountByQueryParameters(List<JoinColumn> joinColumns, QueryParameters queryParameters);

    public List<E> findDistinctByQueryParameters(List<JoinColumn> joinColumns, QueryParameters queryParameters);

    public void refresh(E instance);

    public Long getDistinctCountByColumnName(String filterKey);

    public List<String> getColumnValuesAsString(String filterKey, boolean distinct);


}