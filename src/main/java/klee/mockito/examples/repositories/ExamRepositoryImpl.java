package klee.mockito.examples.repositories;

import klee.mockito.examples.models.Exam;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;

// WITH MOCKITO THIS CLASS IS UNNECESSARY (for testing)//
public class ExamRepositoryImpl implements IExamRepository {
    @Override
    public Exam save(Exam exam) {
        System.out.println("ExamRepositoryImpl.save");
        return new Exam(null, "Physics");
    }

    @Override
    public List<Exam> findAll() {
        System.out.println("ExamRepositoryImpl.findAll");
        try {
            TimeUnit.SECONDS.sleep(3);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        return Arrays.asList(
                new Exam(5L, "Math"),
                new Exam(6L, "English"),
                new Exam(7L, "History")
        );
        // return Collections.emptyList();
    }
}
