package pl.edu.agh.airsystem.model.api.sensors;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class SensorResponse {
    private String id;
    private String type;
    private List<? extends MeasurementResponse> values;
    private Double status;
}