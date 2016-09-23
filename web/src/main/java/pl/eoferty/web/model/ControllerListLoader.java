package pl.eoferty.web.model;

import pl.eoferty.dao.query.QueryParameters;

import java.util.List;

/**
 * Created by Damian on 2015-03-16.
 */
public interface ControllerListLoader<E> {

    public List<E> loadData(QueryParameters queryParameters);

    public Long getRowsCount(QueryParameters queryParameters);

}