package com.mygordienko.spring.security.examples.oauth2client.demands.port;

import com.mygordienko.spring.security.examples.oauth2client.common.api.OffsetPageResponse;
import com.mygordienko.spring.security.examples.oauth2client.demands.Demand;

public interface DemandPort {

    OffsetPageResponse<Demand> findDemands(long offset, int limit);
}
