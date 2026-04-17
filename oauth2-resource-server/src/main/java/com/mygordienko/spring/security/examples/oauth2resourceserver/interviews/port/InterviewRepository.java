package com.mygordienko.spring.security.examples.oauth2resourceserver.interviews.port;

import com.mygordienko.spring.security.examples.oauth2resourceserver.interviews.Interview;
import java.util.List;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

public interface InterviewRepository extends CrudRepository<Interview, Long> {

    @Query("""
            SELECT id, timestamp, manager_email, applicant_email, result
            FROM interviews
            ORDER BY timestamp DESC, id DESC
            LIMIT :limit OFFSET :offset
            """)
    List<Interview> findPage(@Param("limit") int limit, @Param("offset") long offset);
}
