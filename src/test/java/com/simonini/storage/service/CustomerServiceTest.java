package com.simonini.storage.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import com.simonini.storage.domain.Customer;
import com.simonini.storage.dto.CreateCustomerDTO;
import com.simonini.storage.dto.CustomerDTO;
import com.simonini.storage.dto.UpdateCustomerDTO;
import com.simonini.storage.repository.CustomerRepository;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

@ExtendWith(MockitoExtension.class)
public class CustomerServiceTest {

  @Mock private CustomerRepository customerRepository;

  private CustomerServiceImpl clientService;

  @BeforeEach
  void init() {
    clientService = new CustomerServiceImpl(customerRepository);
  }

  @Test
  public void should_get_client_page() {
    PageRequest pageRequest = getPageRequest();
    PageImpl<Customer> clientPage = getClientPage(pageRequest);

    when(customerRepository.findAll(null, pageRequest)).thenReturn(clientPage);

    Page<CustomerDTO> clientsPage = clientService.findAll(null, pageRequest);
    assertEquals(2, clientsPage.getTotalElements());

    assertArrayEquals(
        clientPage.get().map(CustomerDTO::from).toArray(), clientsPage.get().toArray());
  }

  @Test
  public void should_find_by_id() {
    long id = 1L;

    Customer customer = Customer.builder().id(id).name("clientName").build();
    when(customerRepository.findById(id)).thenReturn(Optional.of(customer));

    CustomerDTO customerDTO = clientService.findById(id);
    assertEquals(CustomerDTO.from(customer), customerDTO);
  }

  @Test
  public void should_not_find_by_id() {
    long id = 1L;
    when(customerRepository.findById(id)).thenReturn(Optional.empty());

    assertThrows(
        CustomerServiceImpl.CustomerNotFoundException.class, () -> clientService.findById(id));
  }

  @Test
  public void should_delete() {
    long id = 1L;
    assertDoesNotThrow(() -> clientService.delete(id));
  }

  @Test
  public void should_not_delete_not_found() {
    long id = 1L;
    doThrow(new EmptyResultDataAccessException(1)).when(customerRepository).deleteById(id);

    assertThrows(
        CustomerServiceImpl.CustomerNotFoundException.class, () -> clientService.delete(id));
  }

  @Test
  public void should_update_client() {
    long id = 1L;
    when(customerRepository.existsById(id)).thenReturn(true);
    CreateCustomerDTO createCustomerDTO = CreateCustomerDTO.builder().name("newName").build();

    Customer updatedCustomer = Customer.from(createCustomerDTO);
    updatedCustomer.setId(id);
    when(customerRepository.save(updatedCustomer)).thenReturn(updatedCustomer);

    CustomerDTO updatedDTO = clientService.updateClient(id, createCustomerDTO);
    assertEquals(CustomerDTO.from(updatedCustomer), updatedDTO);
  }

  @Test
  public void should_not_update_client_not_found() {
    long id = 1L;
    when(customerRepository.existsById(id)).thenReturn(false);

    assertThrows(
        CustomerServiceImpl.CustomerNotFoundException.class,
        () -> clientService.updateClient(id, CreateCustomerDTO.builder().name("newName").build()));
  }

  @Test
  public void should_patch_client() {
    long id = 1L;
    Customer foundCustomer = Customer.builder().name("foundClient").build();
    when(customerRepository.findById(id)).thenReturn(Optional.of(foundCustomer));

    UpdateCustomerDTO clientPatch = UpdateCustomerDTO.builder().name("new name").build();

    when(customerRepository.save(any())).thenReturn(foundCustomer);
    CustomerDTO patchedClient = clientService.patchClient(id, clientPatch);

    ArgumentCaptor<Customer> argument = ArgumentCaptor.forClass(Customer.class);
    verify(customerRepository).save(argument.capture());
    assertEquals(clientPatch.getName(), argument.getValue().getName());
    assertEquals(clientPatch.getName(), patchedClient.getName());
  }

  @Test
  public void should_not_patch_not_found() {
    long id = 1L;
    when(customerRepository.findById(id)).thenReturn(Optional.empty());

    assertThrows(
        CustomerServiceImpl.CustomerNotFoundException.class,
        () -> clientService.patchClient(id, UpdateCustomerDTO.builder().name("new name").build()));
  }

  private PageImpl<Customer> getClientPage(Pageable pageable) {
    return new PageImpl<>(getClients(), pageable, getClients().size());
  }

  private List<Customer> getClients() {
    return Arrays.asList(
        Customer.builder().id(1L).name("first client").build(),
        Customer.builder().id(2L).name("second client").build());
  }

  private PageRequest getPageRequest() {
    return PageRequest.of(0, 10);
  }
}
