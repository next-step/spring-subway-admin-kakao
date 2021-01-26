package subway.station;

import org.springframework.stereotype.Service;
import subway.exception.exceptions.DuplicateException;
import subway.exception.exceptions.DuplicateExceptionEnum;

import java.util.List;

@Service
public class StationService {

    private static final int MUST_DELETE_COUNT = 1;

    private final StationDao stationDao;

    public StationService(StationDao stationDao) {
        this.stationDao = stationDao;
    }

    public Station save(StationRequest stationRequest) {
        validateDuplicateStationName(stationRequest.getName());
        return stationDao.save(stationRequest.toStation());
    }

    private void validateDuplicateStationName(String name) {
        if (stationDao.checkExistByName(name)) {
            throw new DuplicateException(DuplicateExceptionEnum.DUPLICATE_STATION_NAME);
        }
    }

    public List<Station> findAll() {
        return stationDao.findAll();
    }

    public boolean deleteById(long id) {
        if(stationDao.deleteById(id) == MUST_DELETE_COUNT) {
            return true;
        }
        return false;
    }
}
