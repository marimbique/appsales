package com.store.app.customer;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import com.store.app.customer.repository.CustomerRepository;
import com.store.app.customer.repository.CustomerAddressRepository;
import com.store.app.customer.repository.CustomerPaymentMethodRepository;

@SpringBootTest
@AutoConfigureMockMvc
class CustomerControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private CustomerRepository customerRepository;

	@Autowired
	private CustomerAddressRepository customerAddressRepository;

	@Autowired
	private CustomerPaymentMethodRepository customerPaymentMethodRepository;

	@BeforeEach
	void clearCustomers() {
		customerAddressRepository.deleteAll();
		customerPaymentMethodRepository.deleteAll();
		customerRepository.deleteAll();
	}

	@Test
	void createsCustomerAndMakesItAvailableFromReadEndpoints() throws Exception {
		MvcResult createResult = mockMvc.perform(post("/api/customers")
				.contentType(MediaType.APPLICATION_JSON)
				.content("""
						{
						  "customerCode": "CUST-001",
						  "firstName": "Ada",
						  "lastName": "Lovelace",
						  "email": "ada@example.com",
						  "phone": "+351 123456789",
						  "taxNumber": "123456789",
						  "birthDate": "1815-12-10"
						}
						"""))
				.andExpect(status().isCreated())
				.andExpect(header().exists("Location"))
				.andExpect(jsonPath("$.id").isNumber())
				.andExpect(jsonPath("$.customerCode").value("CUST-001"))
				.andExpect(jsonPath("$.active").value(true))
				.andReturn();

		String location = createResult.getResponse().getHeader("Location");

		mockMvc.perform(get(location))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.email").value("ada@example.com"));

		mockMvc.perform(get("/api/customers"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.length()").value(1))
				.andExpect(jsonPath("$[0].firstName").value("Ada"));
	}

	@Test
	void createsCustomerWithOptionalAddressesAndPaymentMethods() throws Exception {
		mockMvc.perform(post("/api/customers")
				.contentType(MediaType.APPLICATION_JSON)
				.content("""
						{
						  "customerCode": "CUST-004",
						  "firstName": "Katherine",
						  "lastName": "Johnson",
						  "email": "katherine.full@example.com",
						  "car": [{
						    "addressType": "HOME",
						    "street": "123 Main Street",
						    "postalCode": "1000-001",
						    "city": "Lisbon",
						    "country": "Portugal",
						    "isDefault": true
						  }],
						  "cpmr": [{
						    "paymentType": "CREDIT_CARD",
						    "cardHolder": "Katherine Johnson",
						    "cardLastDigits": "1234",
						    "expiryMonth": 12,
						    "expiryYear": 2030,
						    "isDefault": true
						  }]
						}
						"""))
				.andExpect(status().isCreated());

		Long customerId = customerRepository.findAll().get(0).getId();
		org.junit.jupiter.api.Assertions.assertEquals(1, customerAddressRepository.count());
		org.junit.jupiter.api.Assertions.assertEquals(1, customerPaymentMethodRepository.count());
		org.junit.jupiter.api.Assertions.assertEquals(customerId,
				customerAddressRepository.findAll().get(0).getCustomer().getId());
		org.junit.jupiter.api.Assertions.assertEquals(customerId,
				customerPaymentMethodRepository.findAll().get(0).getCustomer().getId());

		mockMvc.perform(get("/api/customers/{id}", customerId))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.email").value("katherine.full@example.com"))
				.andExpect(jsonPath("$.addresses.length()").value(1))
				.andExpect(jsonPath("$.addresses[0].street").value("123 Main Street"))
				.andExpect(jsonPath("$.paymentMethods.length()").value(1))
				.andExpect(jsonPath("$.paymentMethods[0].cardLastDigits").value("1234"));
	}

	@Test
	void rejectsInvalidCustomerData() throws Exception {
		mockMvc.perform(post("/api/customers")
				.contentType(MediaType.APPLICATION_JSON)
				.content("""
						{
						  "customerCode": "CUST-002",
						  "firstName": "Grace",
						  "lastName": "Hopper",
						  "email": "not-an-email"
						}
						"""))
				.andExpect(status().isBadRequest());
	}

	@Test
	void returnsNotFoundForUnknownCustomer() throws Exception {
		mockMvc.perform(get("/api/customers/999999"))
				.andExpect(status().isNotFound());
	}

	@Test
	void managesCustomerAddressesAndPaymentMethods() throws Exception {
		mockMvc.perform(post("/api/customers")
				.contentType(MediaType.APPLICATION_JSON)
				.content("""
						{
						  "customerCode": "CUST-003",
						  "firstName": "Katherine",
						  "lastName": "Johnson",
						  "email": "katherine@example.com"
						}
						"""))
				.andExpect(status().isCreated());
		Long customerId = customerRepository.findAll().get(0).getId();

		mockMvc.perform(post("/api/customers/{customerId}/addresses", customerId)
				.contentType(MediaType.APPLICATION_JSON)
				.content("""
						{
						  "addressType": "HOME",
						  "street": "123 Main Street",
						  "doorNumber": "4B",
						  "postalCode": "1000-001",
						  "city": "Lisbon",
						  "country": "Portugal",
						  "isDefault": true
						}
						"""))
				.andExpect(status().isCreated())
				.andExpect(jsonPath("$.addressType").value("HOME"))
				.andExpect(jsonPath("$.customerId").value(customerId));
		Long addressId = customerAddressRepository.findAll().get(0).getId();

		mockMvc.perform(get("/api/customers/{customerId}/addresses/{id}", customerId, addressId))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.street").value("123 Main Street"));
		mockMvc.perform(get("/api/customers/{customerId}/addresses", customerId))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.length()").value(1));
		mockMvc.perform(put("/api/customers/{customerId}/addresses/{id}", customerId, addressId)
				.contentType(MediaType.APPLICATION_JSON)
				.content("""
						{
						  "addressType": "WORK",
						  "street": "45 Office Avenue",
						  "postalCode": "1000-002",
						  "city": "Lisbon",
						  "country": "Portugal",
						  "isDefault": false
						}
						"""))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.addressType").value("WORK"));

		mockMvc.perform(post("/api/customers/{customerId}/payment-methods", customerId)
				.contentType(MediaType.APPLICATION_JSON)
				.content("""
						{
						  "paymentType": "CREDIT_CARD",
						  "cardHolder": "Katherine Johnson",
						  "cardLastDigits": "1234",
						  "expiryMonth": 12,
						  "expiryYear": 2030,
						  "isDefault": true
						}
						"""))
				.andExpect(status().isCreated())
				.andExpect(jsonPath("$.paymentType").value("CREDIT_CARD"))
				.andExpect(jsonPath("$.active").value(true));
		Long paymentMethodId = customerPaymentMethodRepository.findAll().get(0).getId();

		mockMvc.perform(get("/api/customers/{customerId}/payment-methods/{id}",
				customerId, paymentMethodId))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.cardLastDigits").value("1234"));
		mockMvc.perform(get("/api/customers/{customerId}/payment-methods", customerId))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.length()").value(1));
		mockMvc.perform(put("/api/customers/{customerId}/payment-methods/{id}", customerId, paymentMethodId)
				.contentType(MediaType.APPLICATION_JSON)
				.content("""
						{
						  "paymentType": "PAYPAL",
						  "isDefault": false,
						  "active": false
						}
						"""))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.paymentType").value("PAYPAL"))
				.andExpect(jsonPath("$.active").value(false));
	}
}
