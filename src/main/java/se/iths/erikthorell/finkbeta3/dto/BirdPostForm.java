package se.iths.erikthorell.finkbeta3.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

public class BirdPostForm {

    @NotBlank(message = "Art får inte vara tom")
    @Size(min = 2, max = 100, message = "Art måste vara mellan 2 och 100 tecken")
    private String species;

    @NotBlank(message = "Plats får inte vara tom")
    @Size(min = 2, max = 150, message = "Plats måste vara mellan 2 och 150 tecken")
    private String location;

    @NotNull(message = "Datum och tid måste fyllas i")
    @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm")
    private LocalDateTime observedAt;

    public String getSpecies() {
        return species;
    }

    public void setSpecies(String species) {
        this.species = species;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public LocalDateTime getObservedAt() {
        return observedAt;
    }

    public void setObservedAt(LocalDateTime observedAt) {
        this.observedAt = observedAt;
    }
}