package com.simonini.storage.integration;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.simonini.storage.StorageApplication;
import com.simonini.storage.dto.CreateCustomerDTO;
import com.simonini.storage.dto.CustomerDTO;
import com.simonini.storage.dto.UpdateCustomerDTO;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = {StorageApplication.class})
@AutoConfigureMockMvc
@Import(DeleteAllEndpoint.class)
public class CustomerIntegrationTest {

  private static final String CUSTOMERS_PATH = "/customers";
  private static final String CUSTOMERS_PATH_TEMPLATE = CUSTOMERS_PATH.concat("/%s");

  @Autowired private MockMvc mvc;
  @Autowired ObjectMapper objectMapper;

  @BeforeEach
  void setup() throws Exception {
    mvc.perform(delete("/clear").contentType(APPLICATION_JSON).accept(APPLICATION_JSON))
        .andExpect(status().is2xxSuccessful());
  }

  @Test
  public void should_create_and_list_all_clients() throws Exception {
    mvc.perform(get(CUSTOMERS_PATH).contentType(APPLICATION_JSON).accept(APPLICATION_JSON))
        .andExpect(status().is2xxSuccessful())
        .andExpect(jsonPath("$.content", hasSize(0)));

    CreateCustomerDTO customerDTO =
        CreateCustomerDTO.builder()
            .name("simonini")
            .document("454545")
            .birthDate(LocalDate.now())
            .build();

    saveCustomer(customerDTO);

    mvc.perform(get(CUSTOMERS_PATH).contentType(APPLICATION_JSON).accept(APPLICATION_JSON))
        .andExpect(status().is2xxSuccessful())
        .andExpect(jsonPath("$.content", hasSize(1)));
  }

  @Test
  public void should_delete_client() throws Exception {
    CreateCustomerDTO createCustomerDTO =
        CreateCustomerDTO.builder()
            .name("simonini")
            .document("454545")
            .birthDate(LocalDate.now())
            .build();

    CustomerDTO savedCustomer = saveCustomer(createCustomerDTO);

    deleteCustomer(savedCustomer.getId()).andExpect(status().is2xxSuccessful());

    deleteCustomer(savedCustomer.getId()).andExpect(status().isNotFound());
  }

  @Test
  public void should_find_client_by_id() throws Exception {
    CreateCustomerDTO createCustomerDTO =
        CreateCustomerDTO.builder()
            .name("simonini")
            .document("454545")
            .birthDate(LocalDate.now())
            .build();

    CustomerDTO savedCustomer = saveCustomer(createCustomerDTO);

    mvc.perform(
            get(String.format(CUSTOMERS_PATH_TEMPLATE, savedCustomer.getId()))
                .contentType(APPLICATION_JSON)
                .accept(APPLICATION_JSON))
        .andExpect(status().is2xxSuccessful())
        .andExpect(jsonPath("$.id", is(savedCustomer.getId().intValue())))
        .andExpect(jsonPath("$.name", is(savedCustomer.getName())))
        .andExpect(jsonPath("$.document", is(savedCustomer.getDocument())))
        .andExpect(jsonPath("$.age", is(savedCustomer.getAge())))
        .andExpect(jsonPath("$.birthDate", is(getStringDate(savedCustomer.getBirthDate()))));

    deleteCustomer(savedCustomer.getId()).andExpect(status().is2xxSuccessful());

    mvc.perform(
            get(String.format(CUSTOMERS_PATH_TEMPLATE, savedCustomer.getId()))
                .contentType(APPLICATION_JSON)
                .accept(APPLICATION_JSON))
        .andExpect(status().isNotFound());
  }

  @Test
  public void should_put_client() throws Exception {
    CreateCustomerDTO createCustomerDTO =
        CreateCustomerDTO.builder()
            .name("simonini")
            .document("454545")
            .birthDate(LocalDate.now())
            .build();

    CustomerDTO savedCustomer = saveCustomer(createCustomerDTO);

    CreateCustomerDTO updateCustomerDTO =
        CreateCustomerDTO.builder()
            .name("new name")
            .document("454545")
            .birthDate(LocalDate.now())
            .build();

    mvc.perform(
            put(String.format(CUSTOMERS_PATH_TEMPLATE, savedCustomer.getId()))
                .content(objectMapper.writeValueAsString(updateCustomerDTO))
                .contentType(APPLICATION_JSON)
                .accept(APPLICATION_JSON))
        .andExpect(status().is2xxSuccessful())
        .andExpect(jsonPath("$.id", is(savedCustomer.getId().intValue())))
        .andExpect(jsonPath("$.name", is(updateCustomerDTO.getName())))
        .andExpect(jsonPath("$.document", is(updateCustomerDTO.getDocument())))
        .andExpect(jsonPath("$.birthDate", is(getStringDate(updateCustomerDTO.getBirthDate()))));

    mvc.perform(
            get(String.format(CUSTOMERS_PATH_TEMPLATE, savedCustomer.getId()))
                .contentType(APPLICATION_JSON)
                .accept(APPLICATION_JSON))
        .andExpect(status().is2xxSuccessful())
        .andExpect(jsonPath("$.id", is(savedCustomer.getId().intValue())))
        .andExpect(jsonPath("$.name", is(updateCustomerDTO.getName())))
        .andExpect(jsonPath("$.document", is(updateCustomerDTO.getDocument())))
        .andExpect(jsonPath("$.birthDate", is(getStringDate(updateCustomerDTO.getBirthDate()))));
  }

  @Test
  public void should_not_put_not_found() throws Exception {
    CreateCustomerDTO updateData =
        CreateCustomerDTO.builder()
            .name("new name")
            .document("454545")
            .birthDate(LocalDate.now())
            .build();

    mvc.perform(
            put(String.format(CUSTOMERS_PATH_TEMPLATE, 1))
                .content(objectMapper.writeValueAsString(updateData))
                .contentType(APPLICATION_JSON)
                .accept(APPLICATION_JSON))
        .andExpect(status().isNotFound());
  }

  @Test
  public void should_path_client() throws Exception {
    CreateCustomerDTO createCustomerDTO =
        CreateCustomerDTO.builder()
            .name("simonini")
            .document("454545")
            .birthDate(LocalDate.now())
            .build();

    CustomerDTO savedCustomer = saveCustomer(createCustomerDTO);

    UpdateCustomerDTO patchName = UpdateCustomerDTO.builder().name("new name").build();

    UpdateCustomerDTO patchDocument = UpdateCustomerDTO.builder().document("new document").build();

    UpdateCustomerDTO patchBirthDate =
        UpdateCustomerDTO.builder().birthDate(LocalDate.now()).build();

    mvc.perform(
            patch(String.format(CUSTOMERS_PATH_TEMPLATE, savedCustomer.getId()))
                .content(objectMapper.writeValueAsString(patchName))
                .contentType(APPLICATION_JSON)
                .accept(APPLICATION_JSON))
        .andExpect(status().is2xxSuccessful())
        .andExpect(jsonPath("$.id", is(savedCustomer.getId().intValue())))
        .andExpect(jsonPath("$.name", is(patchName.getName())))
        .andExpect(jsonPath("$.document", is(createCustomerDTO.getDocument())))
        .andExpect(jsonPath("$.birthDate", is(getStringDate(createCustomerDTO.getBirthDate()))));

    mvc.perform(
            patch(String.format(CUSTOMERS_PATH_TEMPLATE, savedCustomer.getId()))
                .content(objectMapper.writeValueAsString(patchDocument))
                .contentType(APPLICATION_JSON)
                .accept(APPLICATION_JSON))
        .andExpect(status().is2xxSuccessful())
        .andExpect(jsonPath("$.id", is(savedCustomer.getId().intValue())))
        .andExpect(jsonPath("$.name", is(patchName.getName())))
        .andExpect(jsonPath("$.document", is(patchDocument.getDocument())))
        .andExpect(jsonPath("$.birthDate", is(getStringDate(createCustomerDTO.getBirthDate()))));

    mvc.perform(
            patch(String.format(CUSTOMERS_PATH_TEMPLATE, savedCustomer.getId()))
                .content(objectMapper.writeValueAsString(patchBirthDate))
                .contentType(APPLICATION_JSON)
                .accept(APPLICATION_JSON))
        .andExpect(status().is2xxSuccessful())
        .andExpect(jsonPath("$.id", is(savedCustomer.getId().intValue())))
        .andExpect(jsonPath("$.name", is(patchName.getName())))
        .andExpect(jsonPath("$.document", is(patchDocument.getDocument())))
        .andExpect(jsonPath("$.birthDate", is(getStringDate(patchBirthDate.getBirthDate()))));
  }

  @Test
  public void should_not_patch_not_found() throws Exception {
    UpdateCustomerDTO updateData = UpdateCustomerDTO.builder().name("new name").build();

    mvc.perform(
            patch(String.format(CUSTOMERS_PATH_TEMPLATE, 1))
                .content(objectMapper.writeValueAsString(updateData))
                .contentType(APPLICATION_JSON)
                .accept(APPLICATION_JSON))
        .andExpect(status().isNotFound());
  }

  private CustomerDTO saveCustomer(CreateCustomerDTO createCustomerDTO) throws Exception {
    String response =
        mvc.perform(
                post(CUSTOMERS_PATH)
                    .content(objectMapper.writeValueAsString(createCustomerDTO))
                    .contentType(APPLICATION_JSON)
                    .accept(APPLICATION_JSON))
            .andExpect(status().is2xxSuccessful())
            .andExpect(jsonPath("$.id").exists())
            .andExpect(jsonPath("$.age").exists())
            .andExpect(jsonPath("$.name", is(createCustomerDTO.getName())))
            .andExpect(jsonPath("$.document", is(createCustomerDTO.getDocument())))
            .andExpect(jsonPath("$.birthDate", is(getStringDate(createCustomerDTO.getBirthDate()))))
            .andReturn()
            .getResponse()
            .getContentAsString();
    return objectMapper.readValue(response, CustomerDTO.class);
  }

  private ResultActions deleteCustomer(Long id) throws Exception {
    return mvc.perform(
        delete(String.format(CUSTOMERS_PATH_TEMPLATE, id))
            .contentType(APPLICATION_JSON)
            .accept(APPLICATION_JSON));
  }

  private String getStringDate(LocalDate date) {
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    return date.format(formatter);
  }
}
