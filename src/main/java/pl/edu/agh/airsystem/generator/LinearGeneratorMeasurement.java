package pl.edu.agh.airsystem.generator;

import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.TaskScheduler;
import pl.edu.agh.airsystem.model.database.Measurement;
import pl.edu.agh.airsystem.repository.MeasurementRepository;
import pl.edu.agh.airsystem.repository.SensorRepository;
import pl.edu.agh.airsystem.util.MeasurementUtilsService;
import pl.edu.agh.airsystem.util.SensorUtilsService;

import java.time.Duration;
import java.time.Instant;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.Optional;

import static pl.edu.agh.airsystem.util.RandomUtils.randomBetween;

@Slf4j
public class LinearGeneratorMeasurement
        implements GeneratorMeasurementInstance {
    private final double min;
    private final double max;
    private final double minStep;
    private final double maxStep;
    private final Duration stepAfterMin;
    private final Duration stepAfterMax;
    private int direction = 1;
    private double currentValue;

    public LinearGeneratorMeasurement(double min, double max,
                                      double minStep, double maxStep,
                                      Duration stepAfterMin, Duration stepAfterMax) {
        this.min = min;
        this.max = max;
        this.minStep = minStep;
        this.maxStep = maxStep;
        this.stepAfterMin = stepAfterMin;
        this.stepAfterMax = stepAfterMax;
        currentValue = randomBetween(min, max);
    }

    public Duration getTimeStep() {
        return Duration.ofSeconds((long) randomBetween(stepAfterMin.getSeconds(), stepAfterMax.getSeconds()));
    }

    public long getValueStep() {
        return (long) randomBetween(minStep, maxStep);
    }

    private void generateNextValue() {
        currentValue += getValueStep() * direction;
        if (currentValue > max) {
            currentValue = max;
            direction = -1;
        } else if (currentValue < min) {
            currentValue = min;
            direction = 1;
        }
    }


    void generateAndAddNewMeasurement(MeasurementRepository measurementRepository,
                                      MeasurementUtilsService measurementUtilsService,
                                      SensorRepository sensorRepository, long sensorDbId, Instant current) {
        Measurement measurement = new Measurement(
                null,
                current,
                currentValue);
        measurementUtilsService.addNewMeasurement(sensorDbId, measurement);
    }

    @Override
    public void catchUpMeasurements(long sensorDbId,
                                    SensorRepository sensorRepository,
                                    SensorUtilsService sensorUtilsService,
                                    MeasurementRepository repository,
                                    MeasurementUtilsService measurementUtilsService,
                                    TaskScheduler taskScheduler) {
        Instant from;
        Instant current;
        Instant to = Instant.now();

        Optional<Measurement> measurement = sensorUtilsService.findLatestMeasurementInSensor(sensorDbId);
        if (measurement.isPresent()) {
            from = measurement.get().getTimestamp();
        } else {
            from = Instant.now().minus(1, ChronoUnit.DAYS);
        }
        current = Instant.from(from).plus(getTimeStep());
        while (current.isBefore(to)) {
            generateNextValue();
            generateAndAddNewMeasurement(repository, measurementUtilsService, sensorRepository, sensorDbId, current);
            current = current.plus(getTimeStep());
        }
    }

    @Override
    public void startMeasurementsGenerator(long sensorDbId,
                                           SensorRepository sensorRepository,
                                           SensorUtilsService sensorUtilsService,
                                           MeasurementRepository repository,
                                           MeasurementUtilsService measurementUtilsService,
                                           TaskScheduler taskScheduler) {
        scheduleNextIteration(sensorDbId, sensorRepository, sensorUtilsService, repository, measurementUtilsService, taskScheduler);
    }

    private void generatorIteration(long sensorDbId,
                                    SensorRepository sensorRepository,
                                    SensorUtilsService sensorUtilsService,
                                    MeasurementRepository repository,
                                    MeasurementUtilsService measurementUtilsService,
                                    TaskScheduler taskScheduler) {
        generateNextValue();
        generateAndAddNewMeasurement(repository, measurementUtilsService, sensorRepository, sensorDbId, Instant.now());
        scheduleNextIteration(sensorDbId, sensorRepository, sensorUtilsService, repository, measurementUtilsService, taskScheduler);
    }

    private void scheduleNextIteration(long sensorDbId,
                                       SensorRepository sensorRepository,
                                       SensorUtilsService sensorUtilsService,
                                       MeasurementRepository repository,
                                       MeasurementUtilsService measurementUtilsService,
                                       TaskScheduler taskScheduler) {
        Instant now = Instant.now();
        Instant next = now.plus(getTimeStep());

        taskScheduler.schedule(() -> generatorIteration(sensorDbId, sensorRepository, sensorUtilsService, repository, measurementUtilsService, taskScheduler),
                next.atZone(ZoneId.systemDefault()).toInstant());
    }

}
