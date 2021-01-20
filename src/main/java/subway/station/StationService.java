package subway.station;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import subway.exceptions.DuplicateNameException;
import subway.exceptions.InvalidDeleteException;

import java.util.List;

@Service
public class StationService {

    StationDao stationDao;

    @Autowired
    public StationService(StationDao stationDao) {
        this.stationDao = stationDao;
    }

    public Station save(Station station) throws DuplicateNameException {
        try {
            return stationDao.save(station);
        } catch (DuplicateKeyException e) {
            throw new DuplicateNameException("중복된 이름의 Station은 추가할 수 없습니다.");
        }
    }

    public List<Station> findAll() {
        return stationDao.findAll();
    }

    public Station find(Long id) {
        return stationDao.findById(id);
    }

    public void delete(Long id) throws InvalidDeleteException {
        if (stationDao.deleteById(id) == 0) {
            throw new InvalidDeleteException("삭제하려는 station이 존재하지 않습니다.");
        }
    }


}
