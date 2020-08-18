package pl.edu.agh.airsystem.model.api.statistic;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class StatisticResponse {
    private String id;
    private final List<? extends StatisticValueResponse> values;
}