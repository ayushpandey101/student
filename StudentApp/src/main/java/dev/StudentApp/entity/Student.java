package dev.StudentApp.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

@Entity
@Table(name = "students")
public class Student {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Name is required")
    @Size(min = 2, max = 80, message = "Name must be between 2 and 80 characters")
    @Pattern(
        regexp = "^[\\p{L}][\\p{L}\\p{M} .'-]*$",
        message = "Name may contain letters, spaces, apostrophes, periods, and hyphens"
    )
    private String name;

    @NotBlank(message = "Email is required")
    @Email(message = "Enter a valid email")
    @Size(max = 254, message = "Email must be 254 characters or fewer")
    private String email;

    @NotBlank(message = "Please select a course")
    @Pattern(
        regexp = "^(Java|Spring Boot|Python|Angular)$",
        message = "Select a course from the list"
    )
    private String course;

    // Default constructor
    public Student() {
    }

    // Getters and Setters

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getCourse() {
        return course;
    }

    public void setCourse(String course) {
        this.course = course;
    }
}