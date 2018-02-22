package com.turvo.abcbanking.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

import javax.persistence.Column;
import javax.persistence.Entity;
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

/**
 * Model class to represent Branch
 * 
 * @author Prabal Ghura
 *
 */
@Entity
@Table(name="BRANCH")
public class Branch {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "ID", insertable = false, updatable = false)
    private Long id;
	
	@NotNull
	@Column(name = "NAME")
	private String name;
	
	@NotNull
	@Column(name = "MANAGER")
	private String managerId;
	
	@Column(name = "LAST_MODIFIED_BY")
	@JsonIgnore
	private String lastModifiedBy;
	
	@UpdateTimestamp
	@Column(name = "MODIFIED_DATE")
    @Temporal(TemporalType.TIMESTAMP)
    @JsonIgnore
    private Date modifiedDate;
	
	/**
	 * Derived list based on branch-counter-serviceStep-service relationship
	 */
	@Transient
	@JsonInclude(Include.NON_EMPTY)
	private List<Service> regularServices = new ArrayList<>();
	
	/**
	 * Derived list based on branch-counter-serviceStep-service relationship
	 */
	@Transient
	@JsonInclude(Include.NON_EMPTY)
	private List<Service> premiumServices = new ArrayList<>();
	
	/**
	 * Derived list made from regularCounters and premiumCounters map
	 */
	@Transient
	@JsonInclude(Include.NON_EMPTY)
	private List<Counter> counters = new ArrayList<>();
	
	/**
	 * Branch specific token generator number to be assigned to new tokens, implementation is synchronized
	 */
	@Transient
	@JsonIgnore
	private Integer tokenNumber;
	
	@Transient
	@JsonIgnore
	private ConcurrentHashMap<Integer, Counter> regularCounters = new ConcurrentHashMap<>();
	
	@Transient
	@JsonIgnore
	private ConcurrentHashMap<Integer, Counter> premiumCounters = new ConcurrentHashMap<>();
	
	public List<Service> getRegularServices() {
		return regularServices;
	}

	public void setRegularServices(List<Service> regularServices) {
		this.regularServices = regularServices;
	}

	public List<Service> getPremiumServices() {
		return premiumServices;
	}
	
	public void setPremiumServices(List<Service> premiumServices) {
		this.premiumServices = premiumServices;
	}

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

	public String getManagerId() {
		return managerId;
	}

	public void setManagerId(String managerId) {
		this.managerId = managerId;
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
	
	/**
	 * Creating list from Maps for Json response
	 * 
	 * @return list of counters
	 */
	public List<Counter> getRegularCounters() {
		List<Counter> counters1 = new ArrayList<>();
		for (Map.Entry<Integer, Counter> entry : regularCounters.entrySet()) { 
			counters1.add(entry.getValue());
		}
		return counters1;
	}
	
	/**
	 * Creating list from Maps for Json response
	 * 
	 * @return list of counters
	 */
	public List<Counter> getPremiumCounters() {
		List<Counter> counters1 = new ArrayList<>();
		for (Map.Entry<Integer, Counter> entry : premiumCounters.entrySet()) { 
			counters1.add(entry.getValue());
		}
		return counters1;
	}
	
	/**
	 * Merging both counter lists to return final JSON response
	 * 
	 * @return list of counters
	 */
	public List<Counter> getCounters() {
		this.counters.clear();
		this.counters.addAll(getRegularCounters());
		this.counters.addAll(getPremiumCounters());
		return this.counters;
	}
	
	/**
	 * Initializing token generator from DB branch load
	 * 
	 * @param tokenNumber
	 */
	public synchronized void setTokenNumber(Integer tokenNumber) {
		this.tokenNumber = tokenNumber;
	}

	/**
	 * Updating a token in the concurrent map
	 * 
	 * @param counter
	 */
	public void updateCounter(Counter counter) {
		if(counter.getServicingType() == CustomerType.REGULAR)
			regularCounters.put(counter.getNumber(), counter);
		else if(counter.getServicingType() == CustomerType.PREMIUM)
			premiumCounters.put(counter.getNumber(), counter);
	}
	
	/**
	 * Getting a counter from either counter map
	 * 
	 * @param number
	 * @return counter
	 */
	public Counter getCounter(Integer number) {
		Counter counter = regularCounters.get(number);
		if(Objects.isNull(counter))
			return premiumCounters.get(number);
		else
			return counter;
	}
	
	/**
	 * Get a token number (branch specific) for assigning to a new token generated in the branch.  
	 * 
	 * @return tokenNumber
	 */
	public synchronized Integer getTokenNumber() {
		return ++tokenNumber;
	}
}
