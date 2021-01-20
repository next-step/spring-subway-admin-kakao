package subway.section.dao;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import subway.line.dao.LineDao;
import subway.line.domain.Line;
import subway.section.domain.Section;
import subway.section.query.SectionQuery;
import subway.station.dao.StationDao;
import subway.station.domain.Station;

import java.sql.PreparedStatement;
import java.util.List;

@Repository
public class SectionDao {
    private JdbcTemplate jdbcTemplate;
    private LineDao lineDao;
    private StationDao stationDao;

    public SectionDao(JdbcTemplate jdbcTemplate, LineDao lineDao, StationDao stationDao) {
        this.jdbcTemplate = jdbcTemplate;
        this.lineDao = lineDao;
        this.stationDao = stationDao;
    }

    private final RowMapper<Section> sectionRowMapper = (resultSet, rowNum) -> {
        Section section = new Section(
                resultSet.getLong("id"),
                lineDao.findById(resultSet.getLong("line_id")),
                stationDao.findById(resultSet.getLong("up_station_id"))
                        .orElse(new Station(Line.HEAD, Line.TERMINAL_NAME)),
                stationDao.findById(resultSet.getLong("down_station_id"))
                        .orElse(new Station(Line.TAIL, Line.TERMINAL_NAME)),
                resultSet.getInt("distance")
        );
        return section;
    };

    public Section save(Section section){

        KeyHolder keyHoler = new GeneratedKeyHolder();

        jdbcTemplate.update(e -> {
            PreparedStatement preparedStatement = e.prepareStatement(
                    SectionQuery.INSERT, java.sql.Statement.RETURN_GENERATED_KEYS);
            preparedStatement.setLong(1, section.getLine().getId());
            preparedStatement.setLong(2, section.getUpStation().getId());
            preparedStatement.setLong(3, section.getDownStation().getId());
            preparedStatement.setLong(4, section.getDistance());
            return preparedStatement;
        }, keyHoler);

        Long id = (long) keyHoler.getKey();
        return new Section(id, section.getLine(), section.getUpStation(), section.getDownStation(), section.getDistance());
    }

    public void deleteById(Long id) {
        jdbcTemplate.update(SectionQuery.DELETE_BY_ID, id);
    }

    public List<Section> findSectionsByLineId(Long lineId){
        return jdbcTemplate.query(SectionQuery.SELECT_BY_LINE, sectionRowMapper, lineId);
    }


    public Section findSectionByLineIdAndDownStationId(Long lineId, Long stationId) {
        return jdbcTemplate.queryForObject(SectionQuery.SELECT_BY_LINE_AND_DOWNSTATION, sectionRowMapper, lineId, stationId);
    }

    public Section findSectionByLineIdAndUpStationId(Long lineId, Long stationId) {
        return jdbcTemplate.queryForObject(SectionQuery.SELECT_BY_LINE_AND_UPSTATION, sectionRowMapper, lineId, stationId);
    }

    public int countByLineId(Long id) {
        return jdbcTemplate.queryForObject(SectionQuery.COUNT_BY_LINE, Integer.class, id);
    }
}
