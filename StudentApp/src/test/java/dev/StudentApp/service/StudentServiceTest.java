package dev.StudentApp.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import dev.StudentApp.entity.Student;
import dev.StudentApp.repository.StudentRepository;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class StudentServiceTest {

    @Mock
    private StudentRepository studentRepository;

    @InjectMocks
    private StudentService studentService;

    @Test
    void saveStudentReturnsSavedStudent() {
        Student input = new Student();
        Student saved = new Student();
        when(studentRepository.save(input)).thenReturn(saved);

        assertSame(saved, studentService.saveStudent(input));
        verify(studentRepository).save(input);
    }

    @Test
    void getAllStudentsReturnsRepositoryResults() {
        List<Student> students = List.of(new Student(), new Student());
        when(studentRepository.findAll()).thenReturn(students);

        assertEquals(students, studentService.getAllStudents());
        verify(studentRepository).findAll();
    }

    @Test
    void deleteStudentDeletesById() {
        Long studentId = 12L;

        studentService.deleteStudent(studentId);

        verify(studentRepository).deleteById(studentId);
    }
}
