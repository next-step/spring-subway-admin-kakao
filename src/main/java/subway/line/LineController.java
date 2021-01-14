package subway.line;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import subway.station.Station;
import subway.station.StationController;
import subway.station.StationDao;

import java.net.URI;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
public class LineController {

    private LineDao lineDao;
    private StationDao stationDao;

    public LineController(){
        this.lineDao = LineDao.getInstance();
        this.stationDao = StationDao.getInstance();
    }

    @PostMapping(value = "/lines")
    public ResponseEntity<LineResponse> createLine(@RequestBody LineRequest lineRequest) {
        Line line = new Line(lineRequest);
        Station upStation = stationDao.findById(lineRequest.getUpStationId()).get();
        Station downStation = stationDao.findById(lineRequest.getDownStationId()).get();
        line.addStation(upStation);
        line.addStation(downStation);
        Line newLine = null;
        try {
            newLine = lineDao.save(line);
        } catch(IllegalArgumentException iae) {
            return ResponseEntity.badRequest().build();
        }
        LineResponse lineResponse = new LineResponse(newLine.getId(),newLine.getName(),newLine.getColor(),newLine.getStations());
        return ResponseEntity.created(URI.create("/lines/" + newLine.getId())).body(lineResponse);
    }

    @GetMapping(value = "/lines", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<LineResponse>> showLines() {
        List<Line> lines = lineDao.findAll();
        List<LineResponse> lineResponses = lines.stream().map(LineResponse::new).collect(Collectors.toList());
        return ResponseEntity.ok().body(lineResponses);
    }

    @DeleteMapping("/lines/{id}")
    public ResponseEntity deleteLine(@PathVariable Long id) {
        lineDao.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping(value = "/lines/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<LineResponse> showLine(@PathVariable Long id) {
        Optional<Line> lineOptional = lineDao.findById(id);
        LineResponse lineResponse = new LineResponse(lineOptional.get());
        return ResponseEntity.ok(lineResponse);
    }

    @PutMapping(value = "/lines/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity updateLine(@PathVariable Long id, @RequestBody LineRequest lineRequest) {
        lineDao.update(id, lineRequest);
        return ResponseEntity.ok().build();
    }
}
