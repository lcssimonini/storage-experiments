package com.simonini.storage.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.LocalDate;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateCustomerDTO {

  @NotBlank(message = "name cannot be blank")
  private String name;

  @NotBlank(message = "document cannot be blank")
  private String document;

  @NotNull(message = "a date in the format dd/MM/yyyy must be provided")
  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy")
  private LocalDate birthDate;
}
