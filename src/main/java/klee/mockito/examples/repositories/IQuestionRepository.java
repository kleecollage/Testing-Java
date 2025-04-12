package klee.mockito.examples.repositories;

import java.util.List;

public interface IQuestionRepository {
    List<String> findQuestionsByExamId(Long id);
}
