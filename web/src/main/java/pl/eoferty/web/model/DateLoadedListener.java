package pl.eoferty.web.model;

/**
 * Created by Damian on 2015-03-17.
 */
public interface DateLoadedListener<T> {

    public void load(DateLoadedEvent<T> event);

}
