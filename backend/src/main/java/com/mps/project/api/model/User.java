package com.mps.project.api.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Column(unique = true)
    @Size(min = 5, max = 20)
    @NotBlank(message = "Please provide your username")
    private String username;

    @NotNull
    @NotBlank(message = "Please provide the password")
    private String password;

    @NotNull
    @NotBlank(message = "Please provide the first name")
    private String firstName;

    @NotNull
    @NotBlank(message = "Please provide the last name")
    private String lastName;

    @ManyToOne(optional = false)
    @JoinColumn(name = "org_id", referencedColumnName = "id")
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Organization organization;

    private String role;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    @NotNull
    @Transient
    private String passwordConfirm;

    public Organization getOrganization() {
        return organization;
    }

    public void setOrganization(Organization organization) {
        this.organization = organization;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public Long getId() {
        return id;
    }

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

    public String getPasswordConfirm() {
        return passwordConfirm;
    }

    public void setPasswordConfirm(String passwordConfirm) {
        this.passwordConfirm = passwordConfirm;
    }
}
