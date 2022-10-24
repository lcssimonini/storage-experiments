package com.simonini.storage.service;

import com.simonini.storage.domain.Customer;
import com.simonini.storage.dto.CreateCustomerDTO;
import com.simonini.storage.dto.CustomerDTO;
import com.simonini.storage.dto.UpdateCustomerDTO;
import com.simonini.storage.repository.CustomerRepository;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.ResponseStatus;

@Slf4j
@Service
@RequiredArgsConstructor
public class CustomerServiceImpl implements CustomerService {

  private final CustomerRepository repository;

  @Override
  public Page<CustomerDTO> findAll(Specification<Customer> clientSpec, Pageable pageable) {
    log.debug("Searching clients. Page: {}, Specification: {}", pageable, clientSpec);
    Page<Customer> clientPage = repository.findAll(clientSpec, pageable);
    return getClientDTOPage(pageable, clientPage);
  }

  @Override
  public CustomerDTO findById(Long id) {
    log.debug("Attempt to find client with id: {}", id);
    return repository
        .findById(id)
        .map(CustomerDTO::from)
        .orElseThrow(() -> new CustomerNotFoundException(id));
  }

  @Override
  public void delete(Long id) {
    log.debug("Attempt to delete client with id: {}", id);
    try {
      repository.deleteById(id);
    } catch (EmptyResultDataAccessException e) {
      log.info("Client with id: {} does not exist", id);
      throw new CustomerNotFoundException(id);
    }
  }

  @Override
  public CustomerDTO updateClient(Long id, CreateCustomerDTO createCustomerDTO) {
    log.debug("Attempt to update client with id: {}, and data: {}", id, createCustomerDTO);
    if (repository.existsById(id)) {
      Customer customer = Customer.from(createCustomerDTO);
      customer.setId(id);
      return CustomerDTO.from(repository.save(customer));
    } else {
      throw new CustomerNotFoundException(id);
    }
  }

  @Override
  public CustomerDTO patchClient(Long id, UpdateCustomerDTO updateCustomerDTO) {
    log.debug("Attempt to patch client with id: {}, and data: {}", id, updateCustomerDTO);
    return repository
        .findById(id)
        .map(
            found -> {
              found.patch(updateCustomerDTO);
              return repository.save(found);
            })
        .map(CustomerDTO::from)
        .orElseThrow(() -> new CustomerNotFoundException(id));
  }

  @Override
  public CustomerDTO createClient(CreateCustomerDTO createCustomerDTO) {
    log.debug("Attempt to save client: {}", createCustomerDTO);
    return CustomerDTO.from(repository.save(Customer.from(createCustomerDTO)));
  }

  private PageImpl<CustomerDTO> getClientDTOPage(Pageable pageable, Page<Customer> clientPage) {
    return new PageImpl<>(
        clientPage.getContent().stream().map(CustomerDTO::from).collect(Collectors.toList()),
        pageable,
        clientPage.getTotalElements());
  }

  @ResponseStatus(value = HttpStatus.NOT_FOUND, reason = "Customer does not exist")
  public static class CustomerNotFoundException extends RuntimeException {

    public CustomerNotFoundException(Long id) {
      super(String.format("Object with id %s does not exist", id));
    }
  }
}
