package com.mygordienko.spring.security.examples.oauth2resourceserver.demands;

import com.mygordienko.spring.security.examples.oauth2resourceserver.common.api.OffsetPageResponse;
import com.mygordienko.spring.security.examples.oauth2resourceserver.demands.port.DemandRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DemandQueryService {

    private final DemandRepository demandRepository;

    public OffsetPageResponse<Demand> findDemands(long offset, int limit) {
        List<Demand> demands = demandRepository.findPage(limit, offset);
        return new OffsetPageResponse<>(demands, offset, limit, demandRepository.count());
    }
}
