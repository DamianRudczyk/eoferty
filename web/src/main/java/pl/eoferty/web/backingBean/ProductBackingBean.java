package pl.eoferty.web.backingBean;


import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortOrder;
import pl.eoferty.blc.entity.Product;
import pl.eoferty.manager.ProductService;
import pl.eoferty.web.model.FilterProvider;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Damian on 2016-09-21.
 */

@ManagedBean(name="productBackingBean")
@ViewScoped
public class ProductBackingBean implements FilterProvider, Serializable {

	@EJB
	ProductService productService;

	private Product product;
	private LazyDataModel<Product> model;
	private Boolean ifSubmit;

	@PostConstruct
	public void init() {
		this.product = new Product();
		this.ifSubmit = false;
		setFilter(new HashMap<String, Object>());
		model = new LazyDataModel<Product>() {
			@Override
			public List<Product> load(int first, int pageSize, String sortField, SortOrder sortOrder, Map<String, Object> filters) {
				model.setRowCount(productService.count(filters));
				return productService.getResultList(first, pageSize, sortField, sortOrder, filters);
			}
			@Override
			public Object getRowKey(Product provider) {
				return provider.getId();
			}
		};
	}

	@Override
	public Map<String, Object> getFilter() {
		return null;
	}

	@Override
	public void setFilter(Map<String, Object> filters) {
	}

	public void addProduct() throws Exception {
		try {
			this.productService.save(this.product);
		} catch (Exception e) {
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_FATAL, "Error","Wystąpił błąd podczas dodawania produktu"));
			throw new Exception(e);
		}

		this.ifSubmit = true;
		//RequestContext.getCurrentInstance().execute("updateAddProductForm()");
		FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Info","Dodano produkt"));

	}



	//====================== set & get =========================
	public Product getProduct() {
		return product;
	}

	public void setProduct(Product product) {
		this.product = product;
	}

	public LazyDataModel<Product> getModel() {
		return model;
	}

	public void setModel(LazyDataModel<Product> model) {
		this.model = model;
	}

	public Boolean getIfSubmit() {
		return ifSubmit;
	}

	public void setIfSubmit(Boolean ifSubmit) {
		this.ifSubmit = ifSubmit;
	}

	public Boolean ifSubmit() {
		return ifSubmit;
	}
}
