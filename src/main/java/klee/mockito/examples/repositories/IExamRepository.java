package klee.mockito.examples.repositories;

import klee.mockito.examples.models.Exam;

import java.util.List;

public interface IExamRepository {
    Exam save(Exam exam);
    List<Exam> findAll();
}
