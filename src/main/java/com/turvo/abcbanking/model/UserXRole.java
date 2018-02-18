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
 * Model class to represent User Role Relationship
 * 
 * @author Prabal Ghura
 *
 */
@Entity
@Table(name = "USERXROLE")
public class UserXRole {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "ID", insertable = false, updatable = false)
    private Long id;
	
	@Column(name = "USER_ID")
	private String userId;
	
	@Column(name = "ROLE_ID")
	private Integer roleId;
	
	@Column(name = "CREATED_BY")
	@JsonIgnore
	private String createdBy;
	
	@Column(name = "CREATED_DATE", updatable = false)
    @Temporal(TemporalType.TIMESTAMP)
    @JsonIgnore
    @CreationTimestamp
    private Date createdDate;

	public UserXRole() {
		super();
	}

	/**
	 * @param userId
	 * @param roleId
	 * @param createdBy
	 */
	public UserXRole(String userId, Integer roleId, String createdBy) {
		super();
		this.userId = userId;
		this.roleId = roleId;
		this.createdBy = createdBy;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public Integer getRoleId() {
		return roleId;
	}

	public void setRoleId(Integer roleId) {
		this.roleId = roleId;
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
