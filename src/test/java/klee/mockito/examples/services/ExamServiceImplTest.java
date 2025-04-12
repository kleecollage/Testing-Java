package klee.mockito.examples.services;

import klee.mockito.examples.models.Exam;
import klee.mockito.examples.repositories.ExamRepository;
import klee.mockito.examples.repositories.ExamRepositoryImpl;
import klee.mockito.examples.repositories.QuestionRepository;
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
    QuestionRepository questionRepository;

    @BeforeEach
    void setUp() {
        repository = mock(ExamRepository.class);
        questionRepository = mock(QuestionRepository.class);
        service = new ExamServiceImpl(repository, questionRepository);
    }

    @Test
    void findExamByName() {
        // WHIT MOCKITO WE CAN DO TESTS WITHOUT THE IMPLEMENTATION CLASSES //
        // ExamRepository repository = new ExamRepositoryImp();
        // ExamRepository repository = mock(ExamRepositoryImpl.class); // ExamRepositoryImpl is simulated, we aren't implementing the actual methods
        when(repository.findAll()).thenReturn(Data.EXAMS);
        Optional<Exam> exam = service.findExamByName("Math");

        assertTrue(exam.isPresent());
        assertEquals(5L, exam.orElseThrow().getId());
        assertEquals("Math", exam.orElseThrow().getName());
    }

    @Test
    void findExamByNameEmptyList() {
        List<Exam> data = Collections.emptyList();
        when(repository.findAll()).thenReturn(data);
        Optional<Exam> exam = service.findExamByName("Math");
        assertFalse(exam.isPresent());
    }

    /* HOW THIS WORKS?
    * After whe create our dummy data to use with mocks
    * we simulate the functions that are involved in the method we are testing with dummy data in return
    * So here when we are testing the findExamByNameWithQuestions() method,
    * this method call first the findExamByName() method, which contains the findAll() method (First mock)
    * then the code continues to an if sentence, who has the findQuestionsByExamId() method (Second mock)
    * Our parameter "Math" return the dummy data 'EXAMS' in the findExamByName(name) method,
    * findQuestionsByExamId(exam.getId()) return the dummy data 'QUESTIONS',
    * but only if id '5' match with our 'Math' param
    * So if any of these two params are wrong the dummy data is not returned and the Test fails
    * Every day im asking why I choose this profession... ;-;
    * */
    @Test
    void testQuestionsExam() {
        when(repository.findAll()).thenReturn(Data.EXAMS);
        when(questionRepository.findQuestionsByExamId(anyLong())).thenReturn(Data.QUESTIONS);
        Exam exam = service.findExamByNameWithQuestions("Math");
        assertEquals(5, exam.getQuestions().size());
        assertTrue(exam.getQuestions().contains("geometry"));
    }
}














