package com.turvo.abcbanking.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

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
import javax.persistence.Transient;

import org.hibernate.annotations.CreationTimestamp;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

/**
 * Model class to represent Token
 * 
 * @author Prabal Ghura
 *
 */
@Entity
@Table(name="TOKEN")
public class Token {
	
	@Id
	@JsonIgnore
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "ID", insertable = false, updatable = false)
    private Long id;
	
	@Column(name = "ACCOUNT_NUMBER")
    private Long accountNumber;
	
	@Column(name = "DISPLAY_ID")
	private Integer number;
	
	@Column(name = "STATUS")
	@JsonIgnore
	@Enumerated(EnumType.STRING)
	private TokenStatus status;
	
	@Column(name = "CREATED_DATE", updatable = false)
    @Temporal(TemporalType.TIMESTAMP)
	@CreationTimestamp
    @JsonIgnore
    private Date createdDate;
	
	@Transient
	@JsonIgnore
	private Long branchId;
	
	@Transient
	@JsonIgnore
	private Integer counterNumber;
	
	@Transient
	@JsonIgnore
	private CustomerType type;
	
	@Transient
	@JsonInclude(Include.NON_EMPTY)
    private List<TokenWorkflow> steps = new ArrayList<>();

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getAccountNumber() {
		return accountNumber;
	}

	public void setAccountNumber(Long accountNumber) {
		this.accountNumber = accountNumber;
	}

	public Integer getNumber() {
		return number;
	}

	public void setNumber(Integer number) {
		this.number = number;
	}

	public TokenStatus getStatus() {
		return status;
	}

	public void setStatus(TokenStatus status) {
		this.status = status;
	}

	public Date getCreatedDate() {
		return createdDate;
	}

	public void setCreatedDate(Date createdDate) {
		this.createdDate = createdDate;
	}

	public List<TokenWorkflow> getSteps() {
		return steps;
	}

	public void setSteps(List<TokenWorkflow> steps) {
		this.steps = steps;
	}
	
	public List<TokenWorkflow> serviceAndGetNextPendingWorkFlowStep(String comments, String operatorId) {
		List<TokenWorkflow> steps1 = new ArrayList<>();
		for(TokenWorkflow step: steps) {
			if(step.getStatus() == TokenWorklowStatus.ASSIGNED) {
				step.setComments(comments);
				step.setServedBy(operatorId);
				step.setStatus(TokenWorklowStatus.COMPLETED);
				steps1.add(step);
			}
			if(step.getStatus() == TokenWorklowStatus.PENDING) {
				step.setStatus(TokenWorklowStatus.ASSIGNED);
				steps1.add(step);
				break;
			}
		}
		return steps1;
	}

	public Integer getCounterNumber() {
		return counterNumber;
	}

	public void setCounterNumber(Integer counterNumber) {
		this.counterNumber = counterNumber;
	}

	public Long getBranchId() {
		return branchId;
	}

	public void setBranchId(Long branchId) {
		this.branchId = branchId;
	}

	public CustomerType getType() {
		return type;
	}

	public void setType(CustomerType type) {
		this.type = type;
	}
}
