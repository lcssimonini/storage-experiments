package com.simonini.storage.dto;

import static com.simonini.storage.utils.TestUtils.getBirthDate;
import static org.junit.jupiter.api.Assertions.assertEquals;

import com.simonini.storage.domain.Customer;
import org.junit.jupiter.api.Test;

public class CustomerDTOTest {

  @Test
  public void should_build_dto_with_age() {
    Long age = 10L;
    CustomerDTO customerDTO =
        CustomerDTO.builder().name("name").document("123123").birthDate(getBirthDate(age)).build();

    assertEquals(age.toString(), customerDTO.getAge());
    assertEquals("name", customerDTO.getName());
    assertEquals("123123", customerDTO.getDocument());
  }

  @Test
  public void should_return_zero_for_null_birth_date() {
    CustomerDTO customerDTO = CustomerDTO.builder().name("name").document("123123").build();

    assertEquals("0", customerDTO.getAge());
  }

  @Test
  public void should_build_from_client() {
    Long id = 1L;
    Long age = 10L;
    Customer customer =
        Customer.builder()
            .id(id)
            .name("name")
            .document("document")
            .birthDate(getBirthDate(age))
            .build();

    CustomerDTO customerDTO = CustomerDTO.from(customer);
    assertEquals(customer.getId(), customerDTO.getId());
    assertEquals(customer.getName(), customerDTO.getName());
    assertEquals(customer.getDocument(), customerDTO.getDocument());
    assertEquals(customer.getBirthDate(), customerDTO.getBirthDate());
  }
}
