package com.example.LoginPUC.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public class ResetPasswordDTO {

    @NotBlank(message = "Token ausente")
    private String token;

    @NotBlank(message = "Informe uma senha")
    @Pattern(
            regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[^a-zA-Z0-9]).{8,}$",
            message = "A senha deve ter no mínimo 8 caracteres, com letra maiúscula, minúscula, número e caractere especial"
    )
    private String password;

    @NotBlank(message = "Confirme a senha")
    private String confirmPassword;

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getConfirmPassword() {
        return confirmPassword;
    }

    public void setConfirmPassword(String confirmPassword) {
        this.confirmPassword = confirmPassword;
    }
}
