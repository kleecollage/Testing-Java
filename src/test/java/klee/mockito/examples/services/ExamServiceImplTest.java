package klee.mockito.examples.services;

import klee.mockito.examples.models.Exam;
import klee.mockito.examples.repositories.IExamRepository;
import klee.mockito.examples.repositories.IQuestionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.stubbing.Answer;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ExamServiceImplTest {
    @Mock
    IQuestionRepository questionRepository;
    @Mock
    IExamRepository repository;

    @InjectMocks
    ExamServiceImpl service;

    // All Setup is replaced with @ExtendWith(MockitoExtension.class) annotation
    @BeforeEach
    void setUp() {
        // Replace next line with annotation @ExtendWith(MockitoExtension.class)
        // MockitoAnnotations.openMocks(this);

        // Replace next lines with Mockito Annotations @Mock and @InjectMocks//
        // repository = mock(ExamRepository.class); // Mock1
        // questionRepository = mock(QuestionRepository.class); // Mock2
        // service = new ExamServiceImpl(repository, questionRepository); // Inject Mock1 and Mock2
    }

    @Test
    void findExamByName() {
        // WHIT MOCKITO WE CAN DO TESTS WITHOUT THE IMPLEMENTATIONS CLASSES //
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
    * but only if id '5' match with our 'Math' param (avoiding this condition with anyLong())
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

    // Testing if methods findAll() and findQuestionsByExamId() are getting called
    @Test
    void testQuestionsExamVerify() {
        when(repository.findAll()).thenReturn(Data.EXAMS);
        when(questionRepository.findQuestionsByExamId(anyLong())).thenReturn(Data.QUESTIONS);
        service.findExamByNameWithQuestions("Math");
        verify(repository, times(1)).findAll();
        verify(questionRepository, times(1)).findQuestionsByExamId(anyLong());
    }

    // GIVEN these conditions, WHEN execute these methods, THEN expect these results
    @Test
    void testNotExistExamVerify() {
        // GIVEN
        when(repository.findAll()).thenReturn(Data.EXAMS);
        // when(questionRepository.findQuestionsByExamId(anyLong())).thenReturn(Data.QUESTIONS); // not reached (unnecessary stub)
        // WHEN
        Exam exam = service.findExamByNameWithQuestions("Math not");
        // THEN
        assertNull(exam);
    }

    // GIVEN these conditions, WHEN execute these methods, THEN expect these results
    @Test
    void testSaveExam() {
        // GIVEN
        Exam newExam = Data.EXAM;
        newExam.setQuestions(Data.QUESTIONS);
        // simulation of auto incremental ID
        when(repository.save(any(Exam.class))).then(new Answer<Exam>() {
            Long sequence = 8L;
            @Override
            public Exam answer(InvocationOnMock invocationOnMock) throws Throwable {
                Exam exam = invocationOnMock.getArgument(0);
                exam.setId(sequence++);
                return exam;
            }
        });
        // WHEN
        Exam exam = service.save(newExam);
        // THEN
        assertNotNull(exam.getId());
        assertEquals(8L, exam.getId());
        assertEquals("Physics", exam.getName());
        verify(repository).save(any(Exam.class));
        verify(questionRepository).saveMany(anyList());
    }
}














