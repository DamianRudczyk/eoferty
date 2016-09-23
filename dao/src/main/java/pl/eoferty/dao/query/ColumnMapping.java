package pl.eoferty.dao.query;

/**
 * Created by Damian on 2015-03-16.
 */
public class ColumnMapping {

    private final String joinColumnName;
    private final Object joinFieldName;

    public ColumnMapping(String joinColumnName, Object joinFieldName) {
        this.joinColumnName = joinColumnName;
        this.joinFieldName = joinFieldName;
    }

    public String getJoinColumnName() {
        return joinColumnName;
    }

    public Object getJoinFieldName() {
        return joinFieldName;
    }

}