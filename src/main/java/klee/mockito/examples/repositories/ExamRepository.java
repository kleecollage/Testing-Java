package klee.mockito.examples.repositories;

import klee.mockito.examples.models.Exam;

import java.util.List;

public interface ExamRepository {
    List<Exam> findAll();
}
