package pl.eoferty.dao.query;

/**
 * Created by Damian on 2015-03-16.
 */
public enum FilterOperator {

    EQUALS,
    LESSER,
    GREATER,
    LIKE,
    LIKE_NO_CASE,
    GREATER_EQUALS,
    NOT_EQUALS,
    LESSER_EQUALS,
    IS_NULL,
    IS_NOT_NULL,
    IS_EMPTY,
    IS_NOT_EMPTY,
    IN,
    CONTAIN,
    CONTAIN_NO_CASE,
    START_WITH,
    START_WITH_NO_CASE,
    END_WITH,
    END_WITH_NO_CASE;
}