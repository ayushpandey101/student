package dev.StudentApp.controller;

import dev.StudentApp.entity.Student;
import dev.StudentApp.service.StudentService;

import jakarta.validation.Valid;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Controller
public class StudentController {

    private final StudentService studentService;

    public StudentController(StudentService studentService) {
        this.studentService = studentService;
    }

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    // Display Registration Form
    @GetMapping("/")
    public String showForm(Model model) {

        model.addAttribute("student", new Student());

        model.addAttribute(
            "students",
            studentService.getAllStudents()
        );

        return "students";
    }

    // Save Student Details
    @PostMapping("/save")
    public String saveStudent(
            @Valid @ModelAttribute("student") Student student,
            BindingResult result,
            Model model) {

        if (result.hasErrors()) {

            model.addAttribute(
                "students",
                studentService.getAllStudents()
            );

            return "students";
        }

        studentService.saveStudent(student);

        return "redirect:/";
    }

    // Delete Student
    @GetMapping("/delete/{id}")
    public String deleteStudent(@PathVariable Long id) {

        studentService.deleteStudent(id);

        return "redirect:/";
    }
}
