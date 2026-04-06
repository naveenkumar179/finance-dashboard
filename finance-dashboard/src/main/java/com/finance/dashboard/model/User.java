package com.finance.dashboard.model;

import com.fasterxml.jackson.annotation.JsonView;
import com.finance.dashboard.view.Views;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

@Entity
@Table(name = "users")
@Getter 
@Setter 
@NoArgsConstructor 
@AllArgsConstructor 
@Builder
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonView(Views.Public.class)
    private Long id;

    @NotBlank
    @JsonView(Views.Public.class)
    private String name;

    @Email
    @Column(unique = true)
    @JsonView(Views.Public.class)
    private String email;

    @NotBlank
    @JsonView(Views.Internal.class)
    private String password;

    @Enumerated(EnumType.STRING)
    @JsonView(Views.Internal.class)
    private Role role;

    @Enumerated(EnumType.STRING)
    @JsonView(Views.Internal.class)
    private Status status;
}