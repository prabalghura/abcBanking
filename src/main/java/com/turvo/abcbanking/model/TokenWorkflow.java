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

import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * Model class to represent Token
 * 
 * @author Prabal Ghura
 *
 */
@Entity
@Table(name="TOKEN_WORKFLOW")
public class TokenWorkflow {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "ID", insertable = false, updatable = false)
    private Long id;
	
	@Column(name = "TOKEN_ID")
    private Long tokenId;
	
	@Column(name = "COUNTER_ID")
    private Long counterId;
	
	@Column(name = "STEP_ID")
    private Long stepId;
	
	@Column(name = "SERVED_BY")
    private String servedBy;
	
	@Column(name = "STATUS")
	@Enumerated(EnumType.STRING)
	private TokenWorklowStatus status;
	
	@Column(name = "COMMENTS")
	private String comments;
	
	@Column(name = "SERVED_DATE")
    @Temporal(TemporalType.TIMESTAMP)
    @JsonIgnore
    private Date servedDate;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getTokenId() {
		return tokenId;
	}

	public void setTokenId(Long tokenId) {
		this.tokenId = tokenId;
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

	public String getServedBy() {
		return servedBy;
	}

	public void setServedBy(String servedBy) {
		this.servedBy = servedBy;
	}

	public TokenWorklowStatus getStatus() {
		return status;
	}

	public void setStatus(TokenWorklowStatus status) {
		this.status = status;
	}

	public String getComments() {
		return comments;
	}

	public void setComments(String comments) {
		this.comments = comments;
	}

	public Date getServedDate() {
		return servedDate;
	}

	public void setServedDate(Date servedDate) {
		this.servedDate = servedDate;
	}
}
