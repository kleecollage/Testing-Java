package klee.mockito.examples.repositories;

import klee.mockito.examples.models.Exam;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;

// WITH MOCKITO THIS CLASS IS UNNECESSARY //
public class ExamRepositoryImpl implements IExamRepository {
    @Override
    public Exam save(Exam exam) {
        return null;
    }

    @Override
    public List<Exam> findAll() {
        try {
            System.out.println("ExamRepositoryImpl.findAll");
            TimeUnit.SECONDS.sleep(5);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        /*return Arrays.asList(
                new Exam(5L, "Maths"),
                new Exam(6L, "English"),
                new Exam(7L, "History")
        );*/
        return Collections.emptyList();
    }
}
