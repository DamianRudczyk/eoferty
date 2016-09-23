package pl.eoferty.dao.query;

/**
 * Created by Damian on 2015-03-16.
 */
public class FilterOption {

    private String field;
    private Object value;

    public FilterOption() {
    }

    public FilterOption(String field, Object value) {
        this.field = field;
        this.value = value;
    }

    public String getField() {
        return field;
    }

    public void setField(String field) {
        this.field = field;
    }

    public Object getValue() {
        return value;
    }

    public void setValue(Object value) {
        this.value = value;
    }

}