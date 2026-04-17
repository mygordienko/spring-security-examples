package com.mygordienko.spring.security.examples.oauth2resourceserver.demands.port;

import com.mygordienko.spring.security.examples.oauth2resourceserver.demands.Demand;
import java.util.List;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

public interface DemandRepository extends CrudRepository<Demand, Long> {

    @Query("""
            SELECT id, timestamp, author_email, type
            FROM demands
            ORDER BY timestamp DESC, id DESC
            LIMIT :limit OFFSET :offset
            """)
    List<Demand> findPage(@Param("limit") int limit, @Param("offset") long offset);
}
