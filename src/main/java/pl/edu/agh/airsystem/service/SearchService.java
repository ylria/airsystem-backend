package pl.edu.agh.airsystem.service;

import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.edu.agh.airsystem.assembler.BriefStationResponseAssembler;
import pl.edu.agh.airsystem.model.api.response.DataResponse;
import pl.edu.agh.airsystem.model.api.stations.BriefStationResponse;
import pl.edu.agh.airsystem.model.database.Location;
import pl.edu.agh.airsystem.model.database.Station;
import pl.edu.agh.airsystem.repository.StationRepository;

import java.util.ArrayList;
import java.util.List;

import static java.util.stream.Collectors.toList;
import static pl.edu.agh.airsystem.util.GeographicUtils.distance;

@Service
@Transactional
@AllArgsConstructor
public class SearchService {
    private final StationRepository stationRepository;
    private final BriefStationResponseAssembler briefStationResponseAssembler;

    public ResponseEntity<DataResponse> getStations(
            double latitude, double longitude, double radius) {
        List<Station> stations = new ArrayList<>();
        stationRepository.findAll().forEach(stations::add);

        Location location = new Location(latitude, longitude);

        List<BriefStationResponse> response = stations.stream()
                .filter(e -> e.getLocation() != null)
                .filter(e -> distance(location, e.getLocation()) < radius)
                .map(briefStationResponseAssembler::assemble)
                .collect(toList());

        return ResponseEntity.ok().body(DataResponse.of(response));
    }

}
