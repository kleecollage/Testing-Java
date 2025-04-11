package klee.mockito.examples.repositories;

import klee.mockito.examples.models.Exam;

import java.util.Arrays;
import java.util.List;

public class ExamRepositoryImp implements ExamRepository{
    @Override
    public List<Exam> findAll() {
        return Arrays.asList(
                new Exam(5L, "Maths"),
                new Exam(6L, "English"),
                new Exam(7L, "History")
        );
    }
}
