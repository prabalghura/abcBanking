package com.turvo.abcbanking.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

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
import javax.validation.constraints.NotNull;

import org.hibernate.annotations.UpdateTimestamp;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.turvo.abcbanking.exception.BusinessRuntimeException;
import com.turvo.abcbanking.utils.ApplicationConstants;

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
	
	@Transient
	@JsonInclude(Include.NON_EMPTY)
	private List<ServiceStep> steps = new ArrayList<>();
	
	@Transient
	@JsonInclude(Include.NON_EMPTY)
    private ConcurrentLinkedQueue<Token> tokens = new ConcurrentLinkedQueue<>();

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

	public List<ServiceStep> getSteps() {
		return steps;
	}

	public void setSteps(List<ServiceStep> steps) {
		this.steps = steps;
	}

	public Queue<Token> getTokens() {
		return tokens;
	}
	
	public Token pullToken() {
		Token token = tokens.poll();
		if(Objects.isNull(token))
			throw new BusinessRuntimeException(ApplicationConstants.ERR_EMPTY_COUNTER_QUEUE);
		return token;
	}
	
	public Token hasToken(Integer tokenNumber) {
		for(Token token: tokens) {
			if(token.getNumber() == tokenNumber)
				return token;
		}
		return null;
	}
	
	public Counter removeToken(Token token) {
		tokens.remove(token);
		return this;
	}

	public void addToken(Token token) {
		token.setCounterNumber(number);
		token.setBranchId(branchId);
		this.tokens.offer(token);
	}
}
