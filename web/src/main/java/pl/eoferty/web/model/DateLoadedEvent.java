package pl.eoferty.web.model;

import java.util.List;

/**
 * Created by Damian on 2015-03-17.
 */
public class DateLoadedEvent<T> {

    private List<T> data;
    private int count;

    public List<T> getData() {
        return data;
    }

    public void setData(List<T> data) {
        this.data = data;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

}