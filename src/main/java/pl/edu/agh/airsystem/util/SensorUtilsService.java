package pl.edu.agh.airsystem.util;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.edu.agh.airsystem.model.database.Measurement;
import pl.edu.agh.airsystem.model.database.Sensor;
import pl.edu.agh.airsystem.repository.SensorRepository;

import java.util.Optional;

@Service
@Transactional
@AllArgsConstructor
public class SensorUtilsService {
    private SensorRepository sensorRepository;

    public Optional<Measurement> findLatestMeasurementInSensor(Sensor sensor) {
        return Optional.ofNullable(sensor.getLatestMeasurement());
    }

    public Optional<Measurement> findLatestMeasurementInSensor(long sensorDbId) {
        return sensorRepository.findById(sensorDbId).map(Sensor::getLatestMeasurement);
    }

}
