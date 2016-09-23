package pl.eoferty.web.model;

/**
 * Created by Damian on 2015-03-17.
 */
/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortMeta;
import org.primefaces.model.SortOrder;
import pl.eoferty.dao.query.FilterOption;
import pl.eoferty.dao.query.QueryParameters;
import pl.eoferty.dao.query.SortDirection;
import pl.eoferty.dao.query.SortOption;
import pl.eoferty.web.utils.FacesContextUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 *
 * @author Marek
 * @param <ViewRow>
 * @param <DatabaseRow>
 */
public abstract class TableDataModel<ViewRow, DatabaseRow> extends LazyDataModel<ViewRow> {

    private static final String SORT_SPLIT_CHAR = "/";

    private static final long serialVersionUID = -6074599821435569629L;

    private Class<? extends ControllerListLoader> clazz;

    private FilterProvider filterableBackingBean;

    private int rowCount;

    private String localSortField;
    private SortOrder localSortOrder;
    private Map<String, Object> localFilters;
    private static final int NOT_USED = 0;

    private List<DateLoadedListener<ViewRow>> listerens;

    public TableDataModel(FilterProvider filterableBackingBean,
                          Class<? extends ControllerListLoader> clazz) {
        this(clazz);
        this.filterableBackingBean = filterableBackingBean;
        listerens = new ArrayList<DateLoadedListener<ViewRow>>();
    }

    public TableDataModel(Class<? extends ControllerListLoader> clazz) {
        this.clazz = clazz;
    }

    protected abstract ViewRow convertEntity(DatabaseRow databaseRow);

    private List<ViewRow> convertEntityList(List<DatabaseRow> databaseRows) {
        List<ViewRow> result = new ArrayList<ViewRow>(databaseRows.size());
        for (DatabaseRow row : databaseRows) {
            result.add(convertEntity(row));
        }
        return result;
    }

    @Override
    public int getRowCount() {
        return rowCount;
    }

    @Override
    public void setRowCount(int rowCount) {
        this.rowCount = rowCount;
    }

    @Override
    public List<ViewRow> load(int first, int pageSize, String sortField, SortOrder sortOrder, Map<String, Object> filters) {
        Map<String, Object> filterValues = changeMapSignature(filters);
        if (filterableBackingBean != null) {
            filterValues.putAll(filterableBackingBean.getFilter());
        }
        for (Map.Entry<String, Object> object : filterValues.entrySet()) {
            try {
                String value = (String) (object.getValue());
                if (value != null && value.equals("emptyValue")) {
                    object.setValue("");
                }
            } catch (ClassCastException ex) {
                //nie można zrzutowac na stringa

            }

        }

        localFilters = filters;
        localSortField = sortField;
        localSortOrder = sortOrder;

        QueryParameters queryParameters = convert(first, pageSize, sortField, sortOrder, filterValues);
        ControllerListLoader<DatabaseRow> controller = getController();
        List<DatabaseRow> data = controller.loadData(queryParameters);
        // przetłumaczenie encji na obiekty widoków
        List<ViewRow> convertedEntities = convertEntityList(data);
        Long longSize = controller.getRowsCount(queryParameters);
        setRowCount(longSize.intValue());
        notifyLoaded(convertedEntities, longSize.intValue());
        return convertedEntities;
    }


    protected Map<String, Object> changeMapSignature(Map<String, Object> map) {
        Map<String, Object> newMap = new HashMap<String, Object>();
        newMap.putAll(map);
        return newMap;
    }

    protected ControllerListLoader<DatabaseRow> getController() {
        return FacesContextUtils.getBean(clazz);
    }

    private QueryParameters convert(int first, int pageSize, String sortField, SortOrder sortOrder,
                                    Map<String, Object> filters) {
        List<SortMeta> sorts = new ArrayList<SortMeta>();
        String[] sortFields;
        if (sortField != null) {
            sortFields = sortField.split(SORT_SPLIT_CHAR);
        } else {
            sortFields = new String[]{sortField};
        }
        SortOrder sortSingleOrder = sortOrder;
        for (String sortSingleField : sortFields) {
            if (sortSingleField != null && !sortSingleField.isEmpty()) {
                SortMeta sort = new SortMeta();
                sort.setSortField(sortSingleField);
                sort.setSortOrder(sortSingleOrder);
                sorts.add(sort);
            }
            sortSingleOrder = SortOrder.ASCENDING;
        }

        return convert(first, pageSize, sorts, filters);
    }

    private QueryParameters convert(int first, int pageSize, List<SortMeta> multiSortMeta, Map<String, Object> filters) {
        QueryParameters queryParameters = new QueryParameters();
        queryParameters.setFisrtResult(first);
        queryParameters.setMaxResult(pageSize);

        List<SortOption> sorts = new ArrayList<SortOption>();
        for (int i = 0; i < multiSortMeta.size(); ++i) {
            SortMeta sortMeta = multiSortMeta.get(i);
            if (sortMeta != null) {
                String sortField = sortMeta.getSortField();
                if (sortField != null && !sortField.isEmpty()) {
                    SortOption sort = new SortOption();
                    sort.setSortField(sortField);
                    sort.setSortDirection(convert(sortMeta.getSortOrder()));
                    sort.setRank(i);
                    sorts.add(sort);
                }
            }
        }
        queryParameters.setSorts(sorts);

        List<String> filterFields = new ArrayList<String>(filters.keySet());
        List<FilterOption> filterOptions = new ArrayList<FilterOption>();
        for (String filterField : filterFields) {
            if (filterField != null && !filterField.isEmpty()) {
                Object filterValue = filters.get(filterField);
                if (filterValue != null) {
                    if (isObjectNotEmpty(filterValue)) {
                        FilterOption filterOption = new FilterOption();
                        filterOption.setField(filterField);
                        filterOption.setValue(filterValue);
                        filterOptions.add(filterOption);
                    }
                }

            }
        }
        queryParameters.setFilters(filterOptions);

        return queryParameters;
    }

    private SortDirection convert(SortOrder sortOrder) {
        switch (sortOrder) {
            case ASCENDING:
                return SortDirection.ASCENDING;
            case DESCENDING:
                return SortDirection.DESCENDING;
            case UNSORTED:
                return SortDirection.UNSORTED;
            default:
                throw new IllegalArgumentException("Unknown sort order");
        }
    }

    private boolean isObjectNotEmpty(Object obj) {
        if (obj == null) {
            return false;
        }
        if (obj instanceof String && ((String) obj).isEmpty()) {
            return false;
        }
        if (obj instanceof List && ((List) obj).isEmpty()) {
            return false;
        }
        if (obj instanceof Object[] && ((Object[]) obj).length == 0) {
            return false;
        }
        if (obj instanceof String[] && ((String[]) obj).length == 0) {
            return false;
        }
        return true;
    }

    public void addDateLoadedListener(DateLoadedListener<ViewRow> dateLoadedListener) {
        listerens.add(dateLoadedListener);
    }

    public void removeDateLoadedListener(DateLoadedListener<ViewRow> dateLoadedListener) {
        listerens.remove(dateLoadedListener);
    }

    private void notifyLoaded(List<ViewRow> rows, int count) {
        DateLoadedEvent event = new DateLoadedEvent();
        event.setData(rows);
        event.setCount(count);
        for (DateLoadedListener listener : listerens) {
            listener.load(event);
        }
    }
}
