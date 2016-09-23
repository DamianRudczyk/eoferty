package pl.eoferty.dao.query;

/**
 * Created by Damian on 2015-03-16.
 */
public class SortOption {

    private String sortField;
    private SortDirection sortDirection;
    private int rank;

    public String getSortField() {
        return sortField;
    }

    public void setSortField(String sortField) {
        this.sortField = sortField;
    }

    public SortDirection getSortDirection() {
        return sortDirection;
    }

    public void setSortDirection(SortDirection sortDirection) {
        this.sortDirection = sortDirection;
    }

    public int getRank() {
        return rank;
    }

    public void setRank(int rank) {
        this.rank = rank;
    }

}