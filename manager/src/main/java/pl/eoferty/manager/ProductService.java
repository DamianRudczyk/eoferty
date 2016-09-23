package pl.eoferty.manager;

import org.primefaces.model.SortOrder;
import pl.eoferty.blc.entity.Product;

import javax.ejb.Local;
import java.util.List;
import java.util.Map;

/**
 * Created by Damian on 2015-03-17.
 */
@Local
public interface ProductService {

	public void save(Product product) throws Exception;

	public int count(Map<String, Object> filters);

	public List<Product> getResultList(int first, int pageSize, String sortField, SortOrder sortOrder, Map<String, Object> filters);

}