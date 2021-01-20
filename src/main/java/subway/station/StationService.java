package subway.station;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import subway.exception.InvalidIdException;

@Service
public class StationService {
    private StationDao stationDao;

    @Autowired
    public StationService(StationDao stationDao) {
        this.stationDao = stationDao;
    }

    public Station createStation(StationRequest stationRequest) {
        Station station = new Station(stationRequest.getName());
        return stationDao.save(station);
    }

    public Stations findAll() {
        return new Stations(stationDao.findAll());
    }

    public Station find(Long id) {
        return stationDao.getById(id);
    }

    public boolean deleteStation(Long id) {
        if(!stationDao.contain(id)) {
            throw new InvalidIdException(InvalidIdException.INVALID_SECTION_ID_ERROR + id);
        }
        return stationDao.deleteById(id);
    }

}
