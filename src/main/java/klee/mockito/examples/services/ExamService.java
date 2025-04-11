package klee.mockito.examples.services;

import klee.mockito.examples.models.Exam;

public interface ExamService {
    Exam findExamByName(String name);
}
