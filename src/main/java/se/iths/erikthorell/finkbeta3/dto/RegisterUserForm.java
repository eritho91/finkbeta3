package se.iths.erikthorell.finkbeta3.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public class RegisterUserForm {

    @NotBlank(message = "Användarnamn får inte vara tomt")
    @Size(min = 3, max = 30, message = "Användarnamn måste vara mellan 3 och 30 tecken")
    @Pattern(
            regexp = "^[a-zA-Z0-9_-]+$",
            message = "Användarnamn får bara innehålla bokstäver, siffror, - och _"
    )
    private String username;

    @NotBlank(message = "Lösenord får inte vara tomt")
    @Size(min = 8, max = 100, message = "Lösenord måste vara minst 8 tecken")
    private String password;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}