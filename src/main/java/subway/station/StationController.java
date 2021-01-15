package subway.station;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;
import java.util.stream.Collectors;

@RestController
public class StationController {

    private StationDao stationDao;

    public StationController(StationDao stationDao) {
        this.stationDao = stationDao;
    }

    @PostMapping("/stations")
    public ResponseEntity<StationResponse> createStation(@RequestBody StationRequest stationRequest) {
        Station station = new Station(stationRequest.getName());
        Station newStation = stationDao.save(station);
        StationResponse stationResponse = new StationResponse(newStation.getId(), newStation.getName());
        return ResponseEntity.created(URI.create("/stations/" + newStation.getId())).body(stationResponse);
    }

    @GetMapping(value = "/stations", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<StationResponse>> showStations() {
        return ResponseEntity.ok(stationDao.findAll()
                .stream()
                .map(station -> new StationResponse(station.getId(), station.getName()))
                .collect(Collectors.toList()));
    }

    @GetMapping(value = "/stations/{stationId}")
    public ResponseEntity<StationResponse> showStation(@PathVariable Long stationId) {
        Station station = stationDao.findOne(stationId);
        return ResponseEntity.ok(new StationResponse(station.getId(), station.getName()));
    }

    @DeleteMapping("/stations/{stationId}")
    public ResponseEntity deleteStation(@PathVariable Long stationId) {
        int result = stationDao.deleteById(stationId);
        if(result == 0)
            return ResponseEntity.badRequest().build();

        return ResponseEntity.ok().build();
    }


}
