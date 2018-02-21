package com.turvo.abcbanking.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.Length;

/**
 * Model class to represent Customer
 * 
 * @author Prabal Ghura
 *
 */
@Entity
@Table(name="CUSTOMER")
public class Customer {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "ACCOUNT_NUMBER", insertable = false, updatable = false)
    private Long accountNumber;
	
	@NotNull
	@Length(max = 50)
	@Column(name = "NAME")
	private String name;
	
	@Length(max = 50)
	@Column(name = "PHONE_NUMBER")
	private String phoneNumber;
	
	@Length(max = 100)
	@Column(name = "ADDRESS")
	private String address;
	
	@NotNull
	@Column(name = "TYPE")
	private CustomerType type;

	public Long getAccountNumber() {
		return accountNumber;
	}

	public void setAccountNumber(Long accountNumber) {
		this.accountNumber = accountNumber;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getPhoneNumber() {
		return phoneNumber;
	}

	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public CustomerType getType() {
		return type;
	}

	public void setType(CustomerType type) {
		this.type = type;
	}
}
