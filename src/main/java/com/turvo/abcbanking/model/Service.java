package com.turvo.abcbanking.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

import org.hibernate.annotations.CreationTimestamp;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

/**
 * Model class to represent Service
 * 
 * @author Prabal Ghura
 *
 */

@Entity
@Table(name = "SERVICE")
public class Service {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "ID", insertable = false, updatable = false)
    private Long id;
	
	@Column(name = "NAME")
	private String name;
	
	@Column(name = "CREATED_BY")
	@JsonIgnore
	private String createdBy;
	
	@Column(name = "CREATED_DATE", updatable = false)
    @Temporal(TemporalType.TIMESTAMP)
    @JsonIgnore
    @CreationTimestamp
    private Date createdDate;
	
	@Transient
	@JsonInclude(Include.NON_EMPTY)
    List<ServiceStep> steps = new ArrayList<>();

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

	public String getCreatedBy() {
		return createdBy;
	}

	public void setCreatedBy(String createdBy) {
		this.createdBy = createdBy;
	}

	public Date getCreatedDate() {
		return createdDate;
	}

	public void setCreatedDate(Date createdDate) {
		this.createdDate = createdDate;
	}

	public List<ServiceStep> getSteps() {
		return steps;
	}

	public void setSteps(List<ServiceStep> steps) {
		this.steps = steps;
	}
}
