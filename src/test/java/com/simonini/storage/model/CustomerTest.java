package com.simonini.storage.model;

import static com.simonini.storage.utils.TestUtils.getBirthDate;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

import com.simonini.storage.domain.Customer;
import com.simonini.storage.dto.CreateCustomerDTO;
import com.simonini.storage.dto.UpdateCustomerDTO;
import java.time.LocalDate;
import org.junit.jupiter.api.Test;

public class CustomerTest {

  @Test
  public void should_build_client_from_dto() {

    Long age = 10L;
    CreateCustomerDTO createCustomerDTO =
        CreateCustomerDTO.builder()
            .name("name")
            .document("123123")
            .birthDate(getBirthDate(age))
            .build();

    Customer customer = Customer.from(createCustomerDTO);

    assertEquals(createCustomerDTO.getName(), customer.getName());
    assertEquals(createCustomerDTO.getDocument(), customer.getDocument());
    assertEquals(createCustomerDTO.getBirthDate(), customer.getBirthDate());
  }

  @Test
  public void should_patch_client() {
    Long initialAge = 11L;
    Long finalAge = 15L;

    String name = "name";
    String document = "document";
    LocalDate birthDate = getBirthDate(initialAge);

    Customer customer =
        Customer.builder().name(name).document(document).birthDate(birthDate).build();

    UpdateCustomerDTO patchName = UpdateCustomerDTO.builder().name("other_name").build();

    UpdateCustomerDTO patchDocument = UpdateCustomerDTO.builder().document("new document").build();

    UpdateCustomerDTO patchBirthDate =
        UpdateCustomerDTO.builder().birthDate(LocalDate.now()).build();

    assertNotEquals(customer.getName(), patchName.getName());
    assertNotEquals(customer.getDocument(), patchName.getDocument());
    assertNotEquals(customer.getBirthDate(), patchName.getBirthDate());

    customer.patch(patchName);

    assertEquals(customer.getName(), patchName.getName());
    assertEquals(document, customer.getDocument());
    assertEquals(birthDate, customer.getBirthDate());

    customer.patch(patchDocument);

    assertEquals(customer.getName(), patchName.getName());
    assertEquals(patchDocument.getDocument(), customer.getDocument());
    assertEquals(birthDate, customer.getBirthDate());

    customer.patch(patchBirthDate);

    assertEquals(customer.getName(), patchName.getName());
    assertEquals(patchDocument.getDocument(), customer.getDocument());
    assertEquals(patchBirthDate.getBirthDate(), customer.getBirthDate());
  }
}
