package klee.mockito.examples.services;

import klee.mockito.examples.models.Exam;
import klee.mockito.examples.repositories.IExamRepository;
import klee.mockito.examples.repositories.IQuestionRepository;

import java.util.List;
import java.util.Optional;

public class ExamServiceImpl implements IExamService {
    private final IExamRepository examRepository;
    private final IQuestionRepository questionRepository;

    public ExamServiceImpl(IExamRepository examRepository, IQuestionRepository questionRepository) {
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

    @Override
    public Exam save(Exam exam) {
        if (!exam.getQuestions().isEmpty())
            questionRepository.saveMany(exam.getQuestions());

        return examRepository.save(exam);
    }
}
