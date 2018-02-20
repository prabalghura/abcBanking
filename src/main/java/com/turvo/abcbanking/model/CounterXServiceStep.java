package com.turvo.abcbanking.model;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.hibernate.annotations.CreationTimestamp;

import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * Model class to represent Counter ServiceStep Relationship
 * 
 * @author Prabal Ghura
 *
 */
@Entity
@Table(name = "COUNTERXSERVICE_STEP")
public class CounterXServiceStep {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "ID", insertable = false, updatable = false)
    private Long id;
	
	@Column(name = "COUNTER_ID")
	private Long counterId;
	
	@Column(name = "STEP_ID")
	private Long stepId;
	
	@Column(name = "CREATED_BY")
	@JsonIgnore
	private String createdBy;
	
	@Column(name = "CREATED_DATE", updatable = false)
    @Temporal(TemporalType.TIMESTAMP)
    @JsonIgnore
    @CreationTimestamp
    private Date createdDate;

	public CounterXServiceStep() {
		super();
	}
	
	/**
	 * @param counterId
	 * @param stepId
	 * @param createdBy
	 */
	public CounterXServiceStep(Long counterId, Long stepId, String createdBy) {
		super();
		this.counterId = counterId;
		this.stepId = stepId;
		this.createdBy = createdBy;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getCounterId() {
		return counterId;
	}

	public void setCounterId(Long counterId) {
		this.counterId = counterId;
	}

	public Long getStepId() {
		return stepId;
	}

	public void setStepId(Long stepId) {
		this.stepId = stepId;
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
}
