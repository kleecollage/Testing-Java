package klee.mockito.examples.services;

import klee.mockito.examples.models.Exam;
import klee.mockito.examples.repositories.ExamRepository;
import klee.mockito.examples.repositories.QuestionRepository;

import javax.swing.text.html.Option;
import java.util.List;
import java.util.Optional;

public class ExamServiceImpl implements ExamService{
    private final ExamRepository examRepository;
    private final QuestionRepository questionRepository;

    public ExamServiceImpl(ExamRepository examRepository, QuestionRepository questionRepository) {
        this.examRepository = examRepository;
        this.questionRepository = questionRepository;
    }

    @Override
    public Optional<Exam> findExamByName(String name) {
        return examRepository.findAll()
                .stream()
                .filter(e -> e.getName().contains(name))
                .findFirst();
    }

    @Override
    public Exam findExamByNameWithQuestions(String name) {
        Optional<Exam> examOptional = findExamByName(name);
        Exam exam = null;
        if (examOptional.isPresent()) {
            exam = examOptional.orElseThrow();
            List<String> questions = questionRepository.findQuestionsByExamId(exam.getId());
            exam.setQuestions(questions);
        }
        return exam;
    }
}
