package subway.section;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import subway.exceptions.InvalidSectionException;

import java.sql.PreparedStatement;
import java.util.List;

@Repository
public class SectionDao {

    private final JdbcTemplate jdbcTemplate;

    public SectionDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private final RowMapper<Section> sectionRowMapper = (resultSet, rowNum) -> {
        Section section = new Section(
                resultSet.getLong("id"),
                resultSet.getLong("up_station_id"),
                resultSet.getLong("down_station_id"),
                resultSet.getInt("distance")
        );
        return section;
    };

    private final RowMapper<Section> sectionOnlyStationRowMapper = (resultSet, rowNum) -> {
        Section section = new Section(
                resultSet.getLong("up_station_id"),
                resultSet.getLong("down_station_id")
        );
        return section;
    };

    public long save(long lineId, SectionRequest sectionRequest) {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement pstmt = connection.prepareStatement(
                    "insert into SECTION(line_id, up_station_id, down_station_id, distance) VALUES(?, ?, ?, ?)",
                    new String[] {"id"}
                    );
            pstmt.setLong(1, lineId);
            pstmt.setLong(2, sectionRequest.getUpStationId());
            pstmt.setLong(3, sectionRequest.getDownStationId());
            pstmt.setLong(4, sectionRequest.getDistance());
            return pstmt;
        }, keyHolder);
        return keyHolder.getKey().longValue();
    }

    public Section findById(long id) {
        try {
            return jdbcTemplate.queryForObject(
                    "select id, up_station_id, down_station_id, distance from SECTION where id = ?",
                    sectionRowMapper, id
            );
        } catch (EmptyResultDataAccessException e) {
            throw new InvalidSectionException("구간을 찾을 수 없습니다: " + e.getMessage());
        }
    }

    public int countByLineId(long id) {
        return jdbcTemplate.queryForObject("select count(*) from section where line_id = ?", Integer.class, id);
    }

    public int countByLineIdAndStationId(long lineId, long stationId) {
        return jdbcTemplate.queryForObject(
                "select count(*) from SECTION where line_id = ? and (up_station_id = ? or down_station_id = ?)",
                Integer.class, lineId, stationId, stationId
        );
    }

    public long findSectionIdByUpStationId(long lineId, long upStationId) {
        try {
            return jdbcTemplate.queryForObject(
                    "select id from section where line_id = ? and up_station_id = ?",
                    Long.class, lineId, upStationId
            );
        } catch (EmptyResultDataAccessException e) {
            return 0L;
        }
    }

    public long findSectionIdByDownStationId(long lineId, long downStationId) {
        try {
            return jdbcTemplate.queryForObject(
                    "select id from section where line_id = ? and down_station_id = ?",
                    Long.class, lineId, downStationId
            );
        } catch (EmptyResultDataAccessException e) {
            throw new InvalidSectionException("두 역 모두 해당 노선에 포함된 역이 아닙니다: " + e.getMessage());
        }
    }

    public void updateSection(Section section) {
        jdbcTemplate.update(
                "update SECTION set up_station_id = ?, down_station_id = ?, distance = ? where id = ?",
                section.getUpStationId(), section.getDownStationId(), section.getDistance(), section.getId()
        );
    }

    public void deleteByLineIdAndDownStationId(long lineId, long stationId) {
        jdbcTemplate.update("delete from SECTION where line_id = ? and down_station_id = ?", lineId, stationId);
    }

    public void deleteByLineIdAndUpStationId(long lineId, long stationId) {
        jdbcTemplate.update("delete from SECTION where line_id = ? and up_station_id = ?", lineId, stationId);
    }

    public void deleteById(long lineId, long stationId) {
        Section upStationSection = jdbcTemplate.queryForObject(
                "select * from SECTION where line_id = ? and up_station_id = ?",
                sectionRowMapper, lineId, stationId
        );
        Section downStationSection = jdbcTemplate.queryForObject(
                "select * from SECTION where line_id = ? and down_station_id = ?",
                sectionRowMapper, lineId, stationId
        );

        jdbcTemplate.update("delete from SECTION where line_id = ? and up_station_id = ?", lineId, stationId);
        jdbcTemplate.update("delete from SECTION where line_id = ? and down_station_id = ?", lineId, stationId);
        jdbcTemplate.update(
                "insert into SECTION(line_id, up_station_id, down_station_id, distance) VALUES(?,?,?,?)",
                lineId, downStationSection.getUpStationId(), upStationSection.getDownStationId(),
                upStationSection.getDistance() + downStationSection.getDistance()
        );
    }

    public int deleteAllByLineId(long id) {
        return jdbcTemplate.update("delete from section where line_id = ?", id);
    }

    public List<Section> findAllSections(long lineId, long startStationId) {
        return jdbcTemplate.query("WITH RECURSIVE findAllSections(up_station_id, down_station_id) AS" +
                " (SELECT up_station_id, down_station_id from SECTION " +
                "where line_id = ? and up_station_id = ? " +
                "union all " +
                "select s.up_station_id, s.down_station_id from SECTION s " +
                "inner join findAllSections on findAllSections.down_station_id = s.up_station_id) " +
                "select * from findAllSections",
                sectionOnlyStationRowMapper, lineId, startStationId
        );
    }
}
