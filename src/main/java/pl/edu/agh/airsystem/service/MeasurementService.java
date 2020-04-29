package pl.edu.agh.airsystem.service;

import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import pl.edu.agh.airsystem.exception.NotUsersStationException;
import pl.edu.agh.airsystem.model.api.DataResponse;
import pl.edu.agh.airsystem.model.api.measurement.NewMeasurementRequest;
import pl.edu.agh.airsystem.model.database.Measurement;
import pl.edu.agh.airsystem.model.database.Sensor;
import pl.edu.agh.airsystem.model.database.StationClient;
import pl.edu.agh.airsystem.util.MeasurementUtilsService;

import java.time.LocalDateTime;

@Service
@AllArgsConstructor
public class MeasurementService {
    private final ResourceFinder resourceFinder;
    private final AuthorizationService authorizationService;
    private final MeasurementUtilsService measurementUtilsService;

    public ResponseEntity<DataResponse> addMeasurement(
            Long stationId,
            String sensorId,
            NewMeasurementRequest newMeasurementRequest) {
        StationClient stationClient = authorizationService.checkAuthenticationAndGetStationClient();

        Sensor sensor = resourceFinder.findSensorInStation(stationId, sensorId);

        if (sensor.getStation().getStationClient().getId() != stationClient.getId()) {
            throw new NotUsersStationException();
        }

        Measurement measurement = new Measurement(
                sensor,
                LocalDateTime.now(),
                newMeasurementRequest.getValue());
        measurementUtilsService.addNewMeasurement(sensor, measurement);


        return ResponseEntity.ok().build();
    }
}

