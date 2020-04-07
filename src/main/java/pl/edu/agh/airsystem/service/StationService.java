package pl.edu.agh.airsystem.service;

import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import pl.edu.agh.airsystem.exception.NotUsersStationException;
import pl.edu.agh.airsystem.model.api.stations.BriefStationResponse;
import pl.edu.agh.airsystem.model.api.stations.LocationChangeRequest;
import pl.edu.agh.airsystem.model.api.stations.StationResponse;
import pl.edu.agh.airsystem.model.database.Location;
import pl.edu.agh.airsystem.model.database.Station;
import pl.edu.agh.airsystem.model.database.StationClient;
import pl.edu.agh.airsystem.repository.StationRepository;

import java.util.ArrayList;
import java.util.List;

@Service
@AllArgsConstructor
public class StationService {
    private final StationRepository stationRepository;
    private final ResourceFinder resourceFinder;
    private final AuthorizationService authorizationService;

    public ResponseEntity<List<BriefStationResponse>> getStations() {
        List<BriefStationResponse> response = new ArrayList<>();
        stationRepository.findAll().forEach(station -> response.add(new BriefStationResponse(station)));
        return ResponseEntity.ok().body(response);
    }

    public ResponseEntity<StationResponse> getStation(Long stationId) {
        return ResponseEntity.ok()
                .body(new StationResponse(resourceFinder.findStation(stationId)));
    }

    public ResponseEntity<?> setStationLocation(
            Long stationId,
            LocationChangeRequest locationChangeRequest) {
        StationClient loggedStation = authorizationService.checkAuthenticationAndGetStationClient();

        Station station = resourceFinder.findStation(stationId);

        if (station.getStationClient().getId() != loggedStation.getId()) {
            throw new NotUsersStationException();
        }

        Location location = new Location(
                locationChangeRequest.getLatitude(),
                locationChangeRequest.getLongitude());

        station.setLocation(location);
        stationRepository.save(station);

        return ResponseEntity.ok().build();
    }
}
