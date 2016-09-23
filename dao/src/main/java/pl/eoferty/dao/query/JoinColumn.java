package pl.eoferty.dao.query;

import javax.persistence.criteria.JoinType;

/**
 * Created by Damian on 2015-03-16.
 */
public class JoinColumn {

    private final String fieldName;
    private final JoinType joinType;

    public JoinColumn(String fieldName, JoinType joinType) {
        this.fieldName = fieldName;
        this.joinType = joinType;
    }

    public String getFieldName() {
        return fieldName;
    }

    public JoinType getJoinType() {
        return joinType;
    }

}