package pl.eoferty.dao.query;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Damian on 2015-03-16.
 */
public class QueryParameters {

    private List<FilterOption> filters;
    private List<SortOption> sorts;
    private int fisrtResult;
    private int maxResult;

    public QueryParameters() {
        filters = new ArrayList<FilterOption>();
        sorts = new ArrayList<SortOption>();
    }

    public List<FilterOption> getFilters() {
        return filters;
    }

    public void setFilters(List<FilterOption> filters) {
        this.filters = filters;
    }

    public List<SortOption> getSorts() {
        return sorts;
    }

    public void setSorts(List<SortOption> sorts) {
        this.sorts = sorts;
    }

    public int getFisrtResult() {
        return fisrtResult;
    }

    public void setFisrtResult(int fisrtResult) {
        this.fisrtResult = fisrtResult;
    }

    public int getMaxResult() {
        return maxResult;
    }

    public void setMaxResult(int maxResult) {
        this.maxResult = maxResult;
    }

}
