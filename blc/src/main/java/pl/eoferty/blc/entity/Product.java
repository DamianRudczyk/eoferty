package pl.eoferty.blc.entity;

import javax.persistence.*;
import java.math.BigDecimal;

/**
 * Created by Damian on 2016-09-21.
 */
@Entity
@Table(name="product")
public class Product {

	@Id
	@GeneratedValue(generator = "PRODUCT_SEQ")
	@SequenceGenerator(name = "PRODUCT_SEQ", sequenceName = "PRODUCT_SEQ", allocationSize = 1)
	private Long id;

	@Column(name="name")
	private String name;

	@Column(name="description")
	private String description;

	@Column(name="weight")
	private Long weight;

	@Column(name="price")
	private BigDecimal price;

	@Column(name="quantity")
	private Integer quantity;

	@Column(name="rating")
	private Integer rating;


	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Long getWeight() {
		return weight;
	}

	public void setWeight(Long weight) {
		this.weight = weight;
	}

	public BigDecimal getPrice() {
		return price;
	}

	public void setPrice(BigDecimal price) {
		this.price = price;
	}

	public Integer getQuantity() {
		return quantity;
	}

	public void setQuantity(Integer quantity) {
		this.quantity = quantity;
	}

	public Integer getRating() {
		return rating;
	}

	public void setRating(Integer rating) {
		this.rating = rating;
	}

}
