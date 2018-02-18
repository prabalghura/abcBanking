package com.turvo.abcbanking.model;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;

import org.hibernate.annotations.UpdateTimestamp;

import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * Model class to represent Counter
 * 
 * @author Prabal Ghura
 *
 */
@Entity
@Table(name="COUNTER")
public class Counter {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "ID", insertable = false, updatable = false)
	@JsonIgnore
    private Long id;
	
	@Column(name = "BRANCH_ID")
	@JsonIgnore
	private Long branchId;
	
	@NotNull
	@Column(name = "CURRENT_OPERATOR")
	private String currentOperator;
	
	@Column(name = "DISPLAY_ID")
	private Integer number;
	
	@NotNull
	@Column(name = "TYPE")
	@Enumerated(EnumType.STRING)
	private CustomerType servicingType;
	
	@Column(name = "LAST_MODIFIED_BY")
	@JsonIgnore
	private String lastModifiedBy;
	
	@UpdateTimestamp
	@Column(name = "MODIFIED_DATE")
    @Temporal(TemporalType.TIMESTAMP)
    @JsonIgnore
    private Date modifiedDate;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getBranchId() {
		return branchId;
	}

	public void setBranchId(Long branchId) {
		this.branchId = branchId;
	}

	public String getCurrentOperator() {
		return currentOperator;
	}

	public void setCurrentOperator(String currentOperator) {
		this.currentOperator = currentOperator;
	}

	public Integer getNumber() {
		return number;
	}

	public void setNumber(Integer number) {
		this.number = number;
	}

	public CustomerType getServicingType() {
		return servicingType;
	}

	public void setServicingType(CustomerType servicingType) {
		this.servicingType = servicingType;
	}

	public String getLastModifiedBy() {
		return lastModifiedBy;
	}

	public void setLastModifiedBy(String lastModifiedBy) {
		this.lastModifiedBy = lastModifiedBy;
	}

	public Date getModifiedDate() {
		return modifiedDate;
	}

	public void setModifiedDate(Date modifiedDate) {
		this.modifiedDate = modifiedDate;
	}
}
