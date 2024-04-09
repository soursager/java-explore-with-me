package ru.practicum.ewm.user.dto;

import lombok.Builder;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

@Data
@Builder
public class UserDto {
    @NotBlank
    @Email
    @Length(min = 6, max = 254)
    private String email;

    private Long id;

    @NotBlank
    @Length(min = 2, max = 250)
    private String name;
}
