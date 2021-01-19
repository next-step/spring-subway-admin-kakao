package subway.line;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import subway.exceptions.DuplicateLineNameException;
import subway.exceptions.InvalidLineArgumentException;

import java.util.List;

@Repository
public class LineDao {

    private JdbcTemplate jdbcTemplate;

    public LineDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private final RowMapper<Line> lineRowMapper = (resultSet, rowNum) -> {
        Line line = new Line(
                resultSet.getLong("id"),
                resultSet.getString("name"),
                resultSet.getString("color"),
                resultSet.getLong("start_station_id"),
                resultSet.getLong("end_station_id")
        );
        return line;
    };

    public Long save(Line line) {
        String sql = "insert into LINE(name, color, start_station_id, end_station_id) VALUES(?,?,?,?)";
        Long lineId;
        try {
            jdbcTemplate.update(sql, line.getName(), line.getColor(), line.getStartStationId(), line.getEndStationId());
            lineId = jdbcTemplate.queryForObject("select id from LINE where name = ?", Long.class, line.getName());
        } catch (Exception e) {
            throw new DuplicateLineNameException("중복된 이름의 노선입니다.");
        }
        return lineId;
    }

    public Line findById(Long id) {
        String sql = "select * from line where id = ?";
        Line line = jdbcTemplate.queryForObject(sql, lineRowMapper, id);
        if(line == null) {
            throw new InvalidLineArgumentException("해당하는 노선이 존재하지 않습니다.");
        }
        return line;
    }

    public List<Line> findAll() {
        String sql = "select * from line";
        return jdbcTemplate.query(sql, lineRowMapper);
    }

    public Line updateLine(Line line) {
        String sql = "update line set name = ?, color = ? where id = ?";
        jdbcTemplate.update(sql, line.getName(), line.getColor(), line.getId());
        return findById(line.getId());
    }

    public Line updateLineStartStation(Long lineId, Long stationId) {
        String sql = "update line set start_station_id = ? where id = ?";
        jdbcTemplate.update(sql, Long.valueOf(stationId), Long.valueOf(lineId));
        return findById(lineId);
    }

    public Line updateLineEndStation(Long lineId, Long stationId) {
        String sql = "update line set end_station_id = ? where id = ?";
        jdbcTemplate.update(sql, Long.valueOf(stationId), Long.valueOf(lineId));
        return findById(lineId);
    }

    public int deleteById(Long id) {
        String sql = "delete from line where id = ?";
        return jdbcTemplate.update(sql, Long.valueOf(id));
    }
}
