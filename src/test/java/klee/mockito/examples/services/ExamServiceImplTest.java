package klee.mockito.examples.services;

import klee.mockito.examples.models.Exam;
import klee.mockito.examples.repositories.ExamRepository;
import klee.mockito.examples.repositories.ExamRepositoryImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ExamServiceImplTest {
    ExamRepository repository;
    ExamService service;

    @BeforeEach
    void setUp() {
        repository = mock(ExamRepository.class);
        service = new ExamServiceImpl(repository);
    }

    @Test
    void findExamByName() {
        // WHIT MOCKITO WE CAN DO TESTS WITHOUT THE IMPLEMENTATION CLASSES //
        // ExamRepository repository = new ExamRepositoryImp();
        // ExamRepository repository = mock(ExamRepositoryImpl.class); // ExamRepositoryImpl is simulated, we aren't implementing the actual method
        List<Exam> data = Arrays.asList(
                new Exam(5L, "Math"),
                new Exam(6L, "English"),
                new Exam(7L, "History")
        );

        when(repository.findAll()).thenReturn(data);
        Optional<Exam> exam = service.findExamByName("Math");

        assertTrue(exam.isPresent());
        assertEquals(5L, exam.orElseThrow().getId());
        assertEquals("Math", exam.orElseThrow().getName());
    }

    @Test
    void findExamByNameEmptyList() {
        List<Exam> data = Collections.emptyList();

        when(repository.findAll()).thenReturn(data);
        Optional<Exam> exam = service.findExamByName("Maths");

        assertFalse(exam.isPresent());
    }
}