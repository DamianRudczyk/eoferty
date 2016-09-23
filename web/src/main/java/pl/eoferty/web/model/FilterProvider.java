package pl.eoferty.web.model;

import java.util.Map;

/**
 * Created by Damian on 2015-03-17.
 */
public interface FilterProvider {

    public Map<String, Object> getFilter();

    public void setFilter(Map<String, Object> filters);

}