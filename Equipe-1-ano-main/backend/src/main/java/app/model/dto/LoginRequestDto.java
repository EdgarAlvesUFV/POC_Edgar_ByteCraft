package app.model.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;

public record LoginRequestDto(
        @NotBlank
        @Email
        String email,

        @NotEmpty
        String senha)
{

}