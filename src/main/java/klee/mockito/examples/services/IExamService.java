package klee.mockito.examples.services;

import klee.mockito.examples.models.Exam;

import java.util.Optional;

public interface IExamService {
    Optional<Exam> findExamByName(String name);
    Exam findExamByNameWithQuestions(String name);
}
