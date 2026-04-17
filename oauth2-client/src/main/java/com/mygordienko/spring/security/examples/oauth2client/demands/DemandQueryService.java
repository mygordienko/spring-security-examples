package com.mygordienko.spring.security.examples.oauth2client.demands;

import com.mygordienko.spring.security.examples.oauth2client.common.api.OffsetPageResponse;
import com.mygordienko.spring.security.examples.oauth2client.demands.port.DemandPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DemandQueryService {

    private final DemandPort demandPort;

    public OffsetPageResponse<Demand> findDemands(long offset, int limit) {
        return demandPort.findDemands(offset, limit);
    }
}
