package klee.mockito.examples.services;

import klee.mockito.examples.models.Exam;
import klee.mockito.examples.repositories.ExamRepositoryImpl;
import klee.mockito.examples.repositories.IExamRepository;
import klee.mockito.examples.repositories.IQuestionRepository;
import klee.mockito.examples.repositories.QuestionRepositoryImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ExamServiceImplSpyTest {
    @Spy
    QuestionRepositoryImpl questionRepository;
    @Spy
    ExamRepositoryImpl repository;

    @InjectMocks
    ExamServiceImpl service;

    @Test
    void testSpy() {
        /* Next 3 lines are replaced with @Spy and @InjectMocks annotations
        IExamRepository examRepository = spy(ExamRepositoryImpl.class);
        IQuestionRepository questionRepository = spy(QuestionRepositoryImpl.class);
        IExamService examService = new ExamServiceImpl(examRepository, questionRepository); */

        List<String> questions = Arrays.asList("arithmetic");
        doReturn(questions).when(questionRepository).findQuestionsByExamId(anyLong());

        Exam exam = service.findExamByNameWithQuestions("Math");
        assertEquals(5L, exam.getId());
        assertEquals("Math", exam.getName());
        assertEquals(1, exam.getQuestions().size());
        assertTrue(exam.getQuestions().contains("arithmetic"));
        verify(repository, times(1)).findAll();
        verify(questionRepository, times(1)).findQuestionsByExamId(anyLong());
    }
}
















