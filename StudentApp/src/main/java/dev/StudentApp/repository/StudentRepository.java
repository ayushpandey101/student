package dev.StudentApp.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import dev.StudentApp.entity.Student;

public interface StudentRepository
        extends JpaRepository<Student, Long> {

}