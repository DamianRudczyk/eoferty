package pl.eoferty.web.utils;

import javax.el.ELContext;
import javax.faces.context.FacesContext;

/**
 * Created by Damian on 2015-03-17.
 */
public class FacesContextUtils {

    @SuppressWarnings("unchecked")
    public static <T> T getBean(final String name, final Class<T> clazz) {
        ELContext elContext = FacesContext.getCurrentInstance().getELContext();
        return (T) FacesContext.getCurrentInstance().getApplication().getELResolver()
                .getValue(elContext, null, name);
    }

    public static <T> T getBean(final Class<T> clazz) {
        String className = clazz.getSimpleName();
        // przepisz pierwsza litere na mala
        className = firstCharToLowerCase(className);
        return getBean(className, clazz);
    }

    public static String firstCharToLowerCase(String line) {
        return Character.toLowerCase(line.charAt(0)) + line.substring(1);
    }

}