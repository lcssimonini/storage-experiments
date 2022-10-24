package com.simonini.storage.service;

import com.simonini.storage.domain.Customer;
import com.simonini.storage.dto.CreateCustomerDTO;
import com.simonini.storage.dto.CustomerDTO;
import com.simonini.storage.dto.UpdateCustomerDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

public interface CustomerService {

  CustomerDTO createClient(CreateCustomerDTO createCustomerDTO);

  Page<CustomerDTO> findAll(Specification<Customer> clientSpec, Pageable pageable);

  CustomerDTO findById(Long id);

  void delete(Long id);

  CustomerDTO updateClient(Long id, CreateCustomerDTO createCustomerDTO);

  CustomerDTO patchClient(Long id, UpdateCustomerDTO updateCustomerDTO);
}
