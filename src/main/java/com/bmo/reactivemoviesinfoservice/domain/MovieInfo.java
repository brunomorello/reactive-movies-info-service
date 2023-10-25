package com.bmo.reactivemoviesinfoservice.domain;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Document
public class MovieInfo {
    @Id
    private String id;

    @NotBlank(message = "name must not be blank")
    private String name;

    @Positive(message = "year must be a positive number")
    private Integer year;

    private List<@NotBlank(message = "cast must not be blank") String> cast;

    @NotNull(message = "releaseDate cannot be null")
    private LocalDate releaseDate;

}
