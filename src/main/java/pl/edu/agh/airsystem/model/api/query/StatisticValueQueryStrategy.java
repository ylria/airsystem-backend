package pl.edu.agh.airsystem.model.api.query;


import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum StatisticValueQueryStrategy {
    LATEST("latest");

    private String code;
}