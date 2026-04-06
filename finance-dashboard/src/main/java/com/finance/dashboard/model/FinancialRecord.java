package com.finance.dashboard.model;

import com.fasterxml.jackson.annotation.JsonView;
import com.finance.dashboard.view.Views;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Getter 
@Setter 
@NoArgsConstructor 
@AllArgsConstructor 
@Builder
public class FinancialRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @io.swagger.v3.oas.annotations.media.Schema(accessMode = io.swagger.v3.oas.annotations.media.Schema.AccessMode.READ_ONLY)
    @JsonView(Views.Public.class)
    private Long id;
 
    @Positive
    @JsonView(Views.Public.class)
    private Double amount;
 
    @Enumerated(EnumType.STRING)
    @JsonView(Views.Public.class)
    private RecordType type;
 
    @NotBlank
    @JsonView(Views.Public.class)
    private String category;
 
    @NotNull
    @JsonView(Views.Public.class)
    private LocalDate date;
 
    @JsonView(Views.Public.class)
    private String description;

    @JsonView(Views.Internal.class)
    private boolean deleted = false;

    @ManyToOne
    @JsonView(Views.Public.class)
    private User user;
}