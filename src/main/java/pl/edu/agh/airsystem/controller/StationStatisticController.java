package pl.edu.agh.airsystem.controller;

import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pl.edu.agh.airsystem.converter.StatisticValueQueryConverter;
import pl.edu.agh.airsystem.model.api.query.StatisticValueQueryRequest;
import pl.edu.agh.airsystem.model.api.response.Response;
import pl.edu.agh.airsystem.model.api.statistic.AddStatisticRequest;
import pl.edu.agh.airsystem.model.api.statistic.AddToStatisticRequest;
import pl.edu.agh.airsystem.service.StationStatisticService;

@RestController
@AllArgsConstructor
@RequestMapping("/api/stations")
public class StationStatisticController {
    private final StationStatisticService stationStatisticService;
    private final StatisticValueQueryConverter statisticValueQueryConverter;

    @PostMapping("{stationId}/statistics")
    public ResponseEntity<? extends Response> addStatistic(
            @PathVariable(value = "stationId") String stationId,
            @RequestBody AddStatisticRequest addStatisticRequest) {
        return stationStatisticService.addStatistic(stationId, addStatisticRequest);
    }

    @GetMapping("{stationId}/statistics")
    public ResponseEntity<? extends Response> getStatistics(
            @PathVariable(value = "stationId") String stationId,
            StatisticValueQueryRequest statisticQueryRequest) {
        return stationStatisticService.getStatistics(stationId,
                statisticValueQueryConverter.convert(statisticQueryRequest));
    }

    @DeleteMapping("{stationId}/statistics/{statisticId}")
    public ResponseEntity<? extends Response> removeStatistic(
            @PathVariable(value = "stationId") String stationId,
            @PathVariable(value = "statisticId") String statisticId) {
        return stationStatisticService.removeStatistic(stationId, statisticId);
    }

    @GetMapping("{stationId}/statistics/{statisticId}")
    public ResponseEntity<? extends Response> getStatistic(
            @PathVariable(value = "stationId") String stationId,
            @PathVariable(value = "statisticId") String statisticId,
            StatisticValueQueryRequest statisticQueryRequest) {
        return stationStatisticService.getStatistic(stationId, statisticId,
                statisticValueQueryConverter.convert(statisticQueryRequest));
    }

    @PostMapping("{stationId}/statistics/{statisticId}/values")
    public ResponseEntity<? extends Response> addValueToStatistic(
            @PathVariable(value = "stationId") String stationId,
            @PathVariable(value = "statisticId") String statisticId,
            @RequestBody AddToStatisticRequest addToStatisticsRequest) {
        return stationStatisticService.addToStatistic(stationId, statisticId, addToStatisticsRequest);
    }

}
