package klee.mockito.examples.services;

import klee.mockito.examples.models.Exam;
import klee.mockito.examples.repositories.ExamRepositoryImpl;
import klee.mockito.examples.repositories.IExamRepository;
import klee.mockito.examples.repositories.IQuestionRepository;
import klee.mockito.examples.repositories.QuestionRepositoryImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
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
    QuestionRepositoryImpl questionRepository;
    @Mock
    ExamRepositoryImpl repository;

    @InjectMocks
    ExamServiceImpl service;

    @Captor
    ArgumentCaptor<Long> captor;

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

    @Test
    void testHandleException() {
        when(repository.findAll()).thenReturn(Data.EXAMS_ID_NULL);
        when(questionRepository.findQuestionsByExamId(isNull())).thenThrow(IllegalArgumentException.class); // null = parameter not allowed
        Exception exception = assertThrows(IllegalArgumentException.class, () ->
                service.findExamByNameWithQuestions("Math"));
        assertEquals(IllegalArgumentException.class, exception.getClass());
        verify(repository, times(1)).findAll();
        verify(questionRepository, times(1)).findQuestionsByExamId(isNull());
    }

    @Test
    void testArgumentMatchers() {
        when(repository.findAll()).thenReturn(Data.EXAMS);
        when(questionRepository.findQuestionsByExamId(anyLong())).thenReturn(Data.QUESTIONS);
        service.findExamByNameWithQuestions("Math");
        verify(repository, times(1)).findAll();
        // verify(questionRepository).findQuestionsByExamId(argThat(arg -> arg != null && arg.equals(5L)));
        // verify(questionRepository).findQuestionsByExamId(argThat(arg -> arg != null && arg >= 5L));
        verify(questionRepository).findQuestionsByExamId(eq(5L));
    }

    @Test
    @Disabled // this test is programmed to fail
    void testArgumentMatchers2() {
        when(repository.findAll()).thenReturn(Data.EXAMS_ID_NEGATIVE);
        when(questionRepository.findQuestionsByExamId(anyLong())).thenReturn(Data.QUESTIONS);
        service.findExamByNameWithQuestions("Math");
        verify(repository, times(1)).findAll();
        verify(questionRepository).findQuestionsByExamId(argThat(new MyArgsMatchers()));
    }

    @Test
    void testArgumentMatchers3() {
        when(repository.findAll()).thenReturn(Data.EXAMS);
        when(questionRepository.findQuestionsByExamId(anyLong())).thenReturn(Data.QUESTIONS);
        service.findExamByNameWithQuestions("Math");
        verify(repository, times(1)).findAll();
        verify(questionRepository).findQuestionsByExamId(argThat((aLong) -> aLong != null && aLong > 0));
    }

    @Test
    void testArgumentCaptor() {
        when(repository.findAll()).thenReturn(Data.EXAMS);
        // when(questionRepository.findQuestionsByExamId(anyLong())).thenReturn(Data.QUESTIONS); // unreached
        service.findExamByNameWithQuestions("Math");
        // Next line is replaced whit @Captor annotation
        // ArgumentCaptor<Long> captor = ArgumentCaptor.forClass(Long.class);
        verify(questionRepository).findQuestionsByExamId(captor.capture());
        assertEquals(5L, captor.getValue());
    }

    @Test
    void testDoThrow() {
        Exam exam = Data.EXAM;
        exam.setQuestions(Data.QUESTIONS);
        // saveMany() is a void method and doThrow is for void methods //
        doThrow(IllegalArgumentException.class).when(questionRepository).saveMany(anyList());
        assertThrows(IllegalArgumentException.class, () -> service.save(exam));
    }

    @Test
    void testDoAnswer() {
        when(repository.findAll()).thenReturn(Data.EXAMS);
        // when(questionRepository.findQuestionsByExamId(anyLong())).thenReturn(Data.QUESTIONS);
        doAnswer(invocation -> {
            Long id = invocation.getArgument(0);
            return id == 5L ? Data.QUESTIONS : Collections.emptyList();
        }).when(questionRepository).findQuestionsByExamId(anyLong());

        Exam exam = service.findExamByNameWithQuestions("Math");
        assertTrue(exam.getQuestions().contains("geometry"));
        assertEquals(5, exam.getQuestions().size());
        assertEquals(5L, exam.getId());
        verify(questionRepository, times(1)).findQuestionsByExamId(anyLong());
    }

    @Test
    void testDoAnswerSaveExam() {
        Exam newExam = Data.EXAM;
        newExam.setQuestions(Data.QUESTIONS);
        doAnswer(new Answer<Exam>() {
            Long sequence = 8L;
            @Override
            public Exam answer(InvocationOnMock invocationOnMock) throws Throwable {
                Exam exam = invocationOnMock.getArgument(0);
                exam.setId(sequence++);
                return exam;
            }
        }).when(repository).save(any(Exam.class));
        Exam exam = service.save(newExam);
        assertNotNull(exam.getId());
        assertEquals(8L, exam.getId());
        assertEquals("Physics", exam.getName());
        verify(repository).save(any(Exam.class));
        verify(questionRepository).saveMany(anyList());
    }

    @Test
    void testDoCallRealMethod() {
        when(repository.findAll()).thenReturn(Data.EXAMS);
        // when(questionRepository.findQuestionsByExamId(anyLong())).thenReturn(Data.QUESTIONS);
        doCallRealMethod().when(questionRepository).findQuestionsByExamId(anyLong());
        Exam exam = service.findExamByNameWithQuestions("Math");
        assertEquals(5L, exam.getId());
        assertEquals("Math", exam.getName());
    }

    public static class MyArgsMatchers implements ArgumentMatcher<Long> {
        private Long argument;

        @Override
        public boolean matches(Long aLong) {
            this.argument = aLong;
            return aLong != null && aLong > 0;
        }

        @Override
        public String toString() {
            return "this is a custom error message that mockito prints if the test fails '" +
                    argument + "' should be a positive integer";
        }
    }
}














