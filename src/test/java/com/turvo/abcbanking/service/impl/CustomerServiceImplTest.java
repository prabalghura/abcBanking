package com.turvo.abcbanking.service.impl;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyLong;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.AdditionalAnswers;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;

import com.turvo.abcbanking.exception.BusinessRuntimeException;
import com.turvo.abcbanking.model.Customer;
import com.turvo.abcbanking.model.CustomerType;
import com.turvo.abcbanking.repository.CustomerRepository;
import com.turvo.abcbanking.service.CustomerService;
import com.turvo.abcbanking.utils.ApplicationConstants;

/**
 * Test class for Customer service
 * 
 * @author Prabal Ghura
 *
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class CustomerServiceImplTest {

	@Autowired
	CustomerService customerService;
	
	@MockBean
	CustomerRepository customerRepository;
	
	@Rule
	public ExpectedException exception = ExpectedException.none();
	
	// These are configurable settings
	
	// stubbedCustomerAccountNumber and nonExistingCustomerAccountNumber should be mutually exclusive
	Long stubbedCustomerAccountNumber = 1L;
	Long nonExistingCustomerAccountNumber = 2L;
	
	CustomerType stubbedCustomerType = CustomerType.REGULAR;
	
	/**
	 * Stubbing all dependencies at one place
	 */
	@Before
	public final void stubDependencies() {
		Customer customer = new Customer();
		customer.setAccountNumber(stubbedCustomerAccountNumber);
		customer.setType(stubbedCustomerType);
		
		Mockito.when(customerRepository.findOne(anyLong())).thenReturn(null);
		Mockito.when(customerRepository.findOne(stubbedCustomerAccountNumber)).thenReturn(customer);
		Mockito.when(customerRepository.save(any(Customer.class))).then(AdditionalAnswers.returnsFirstArg());
	}
	
	/**
	 * Test method for {@link com.turvo.abcbanking.service.impl.CustomerServiceImpl#getCustomer(java.lang.Long)}.
	 */
	@Test
	public final void testGetCustomer_Existing() {
		Customer customer = customerService.getCustomer(stubbedCustomerAccountNumber);
		
		Assert.assertNotNull("Stubbed customer should not be null", customer);
		Assert.assertTrue("Stubbed customer should have stubbed Customer Type", customer.getType() == stubbedCustomerType);
	}
	
	/**
	 * Test method for {@link com.turvo.abcbanking.service.impl.CustomerServiceImpl#getCustomer(java.lang.Long)}.
	 */
	@Test
	public final void testGetCustomer_NonExisting() {
		exception.expect(BusinessRuntimeException.class);
		exception.expectMessage(ApplicationConstants.ERR_CUSTOMER_NOT_EXIST);
		customerService.getCustomer(nonExistingCustomerAccountNumber);
	}

	/**
	 * Test method for {@link com.turvo.abcbanking.service.impl.CustomerServiceImpl#createNewCustomer(com.turvo.abcbanking.model.Customer)}.
	 */
	@Test
	public final void testCreateNewCustomer() {
		Customer customer = new Customer();
		customer.setAccountNumber(nonExistingCustomerAccountNumber);
		customer.setType(stubbedCustomerType);
		customer = customerService.createNewCustomer(customer);
		
		Assert.assertNotNull("Customer should be created", customer);
		Assert.assertTrue("Created customer should have assigned account number", customer.getAccountNumber() == nonExistingCustomerAccountNumber);
		Assert.assertTrue("Created customer should have assigned customer type", customer.getType() == stubbedCustomerType);
	}
}
