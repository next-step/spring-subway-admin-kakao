package subway.station;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import subway.DuplicateException;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class StationDao {
    private Long seq = 0L;
    private List<Station> stations = new ArrayList<>();
    private static StationDao singleInstance = new StationDao();

    private StationDao(){

    }

    public static StationDao getInstance(){
        return singleInstance;
    }

    public Station save(Station station) {
        if(hasDuplicateName(station.getName())){
            throw new DuplicateException();
        }

        Station persistStation = createNewObject(station);
        stations.add(persistStation);

        return persistStation;
    }

    public List<Station> findAll() {
        return stations;
    }

    public List<Station> findAllByLineId(Long id) {
        return stations.stream()
                .filter(station -> station.getId() == id)
                .collect(Collectors.toList());
    }

    public Optional<Station> findById(Long id){
        return stations.stream()
                .filter(station -> (station.getId() == id))
                .findFirst();
    }

    public void deleteById(Long id) {
        String sqlQuery = "delete from station where id = ?";
        jdbcTemplate.update(sqlQuery, id);
    }

    private Station createNewObject(Station station) {
        Field field = ReflectionUtils.findField(Station.class, "id");
        field.setAccessible(true);
        ReflectionUtils.setField(field, station, ++seq);
        return station;
    }

    private boolean hasDuplicateName(String name){
        for(Station station : stations){
            if(station.getName().equals(name)) return true;
        }
        return false;
    }
}
