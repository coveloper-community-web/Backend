package com.covelopment.coveloper.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MemberDTO {
    @NotBlank
    @Size(max = 50)
    @Email(message = "Invalid email format")
    private String email;

    @NotBlank
    @Size(max = 100)
    private String password;

    @NotBlank
    @Size(max = 50)
    private String nickname;

    @NotBlank
    @Size(max = 50)
    private String name;

    @NotBlank
    private String track1;

    @NotBlank
    private String track2;
}
