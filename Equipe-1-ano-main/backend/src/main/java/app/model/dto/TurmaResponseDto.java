package app.model.dto;

import jakarta.validation.constraints.NotBlank;

public record TurmaResponseDto(
          @NotBlank(message = "id é obrigatório")
          long id,

          @NotBlank(message = "nome é obrigatório")
          String nome,

          int ano,

          @NotBlank(message = "turno é obrigatório")
          String turno) {

}
