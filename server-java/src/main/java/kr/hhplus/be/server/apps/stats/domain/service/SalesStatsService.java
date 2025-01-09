package kr.hhplus.be.server.apps.stats.domain.service;

import kr.hhplus.be.server.apps.stats.domain.repository.SalesStatsRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SalesStatsService {
    private final SalesStatsRepository salesStatsRepository;
    public List<Long> getTopSellingProductIds(LocalDate nowDate, int topN) {
        LocalDate startDate = nowDate.minusDays(3);

        return salesStatsRepository.findTopSellingProductIds(startDate, topN);
    }

}
