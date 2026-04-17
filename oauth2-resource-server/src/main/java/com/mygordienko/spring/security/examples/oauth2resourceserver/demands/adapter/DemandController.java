package com.mygordienko.spring.security.examples.oauth2resourceserver.demands.adapter;

import com.mygordienko.spring.security.examples.oauth2resourceserver.common.api.OffsetPageResponse;
import com.mygordienko.spring.security.examples.oauth2resourceserver.demands.Demand;
import com.mygordienko.spring.security.examples.oauth2resourceserver.demands.DemandQueryService;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Validated
@RestController
@RequiredArgsConstructor
public class DemandController {

    private final DemandQueryService demandQueryService;

    @GetMapping("/demands")
    public OffsetPageResponse<Demand> getDemands(
            @RequestParam(defaultValue = "0") @Min(0) long offset,
            @RequestParam(defaultValue = "20") @Min(1) @Max(100) int limit
    ) {
        return demandQueryService.findDemands(offset, limit);
    }
}
