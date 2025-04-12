package klee.mockito.examples.repositories;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class QuestionRepositoryImpl implements IQuestionRepository{
    @Override
    public List<String> findQuestionsByExamId(Long id) {
        System.out.println("QuestionRepositoryImpl.findQuestionsByExamId");
        try {
            TimeUnit.SECONDS.sleep(2);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        return Arrays.asList(
                "arithmetic",
                "integrals",
                "derivatives",
                "trigonometry",
                "geometry"
        );
    }

    @Override
    public void saveMany(List<String> questions) {
        System.out.println("QuestionRepositoryImpl.saveMany");
    }
}
