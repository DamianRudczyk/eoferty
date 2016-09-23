package pl.eoferty.dao.query;

/**
 * Created by Damian on 2015-03-16.
 */

import java.util.HashMap;
import java.util.Map;

public final class GlobalFilterOperators implements FilterKeys {

    protected static GlobalFilterOperators instance;
    protected final Map<Object, FilterOperator> filterOperators;

    protected GlobalFilterOperators() {
        filterOperators = new HashMap<Object, FilterOperator>();
        initFilters();
    }

    public static GlobalFilterOperators getInstance() {
        if (instance == null) {
            instance = new GlobalFilterOperators();
        }
        return instance;
    }

    public Map<Object, FilterOperator> getFilterOperators() {
        return filterOperators;
    }

    protected void initFilters() {
        //COMMON
        filterOperators.put(ID, FilterOperator.EQUALS);
        filterOperators.put(CENA, FilterOperator.EQUALS);
        filterOperators.put(NAME, FilterOperator.LIKE);



    }
}
