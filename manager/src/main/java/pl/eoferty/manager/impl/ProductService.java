package pl.eoferty.manager.impl;

import org.primefaces.model.SortOrder;
import pl.eoferty.blc.entity.Product;
import pl.eoferty.dao.ProductDao;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import java.util.List;
import java.util.Map;

/**
 * Created by Damian on 2016-09-21.
 */
@Stateless
public class ProductService implements pl.eoferty.manager.ProductService {

	@EJB
	ProductDao productDao;

	@Override
	public void save(Product product) throws Exception {
		productDao.save(product);
	}

	@Override
	public int count(Map<String, Object> filters) {
		return productDao.count(filters);
	}

	@Override
	public List<Product> getResultList(int first, int pageSize, String sortField, SortOrder sortOrder, Map<String, Object> filters) {
		return productDao.getResultList(first, pageSize, sortField, sortOrder, filters);
	}
}