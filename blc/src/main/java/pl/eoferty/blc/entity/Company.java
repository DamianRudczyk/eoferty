package pl.eoferty.blc.entity;

import javax.persistence.*;

/**
 * Created by Damian on 2016-09-21.
 */
@Entity
@Table(name="company")
public class Company {

	@Id
	@GeneratedValue(generator = "COMPANY_SEQ")
	@SequenceGenerator(name = "COMPANY_SEQ", sequenceName = "COMPANY_SEQ", allocationSize = 1)
	private Long id;

	@Column(name="name")
	private String name;

	@Column(name="description")
	private String description;


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
}
