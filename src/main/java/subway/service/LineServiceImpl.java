package subway.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import subway.dao.LineDao;
import subway.dao.SectionDao;
import subway.domain.Line;
import subway.domain.Section;
import subway.domain.Sections;
import subway.domain.Station;

import java.util.List;

@Service
public class LineServiceImpl implements LineService {
    private final LineDao lineDao;
    private final SectionDao sectionDao;

    public LineServiceImpl(LineDao lineDao, SectionDao sectionDao) {
        this.lineDao = lineDao;
        this.sectionDao = sectionDao;
    }

    @Override
    @Transactional
    public Line save(Line line, Section section) {
        Line newLine = lineDao.save(line);
        saveSection(newLine, new Section(section.getSectionId(), section.getUpStation(), section.getDownStation(), section.getDistance(), newLine.getId()));
        return newLine;
    }

    @Override
    @Transactional
    public void deleteById(Long lineId) {
        sectionDao.deleteSectionByLineId(lineId);
        lineDao.deleteById(lineId);
    }

    @Override
    public List<Line> findAll() {
        return lineDao.findAll();
    }

    @Override
    public Line findOne(Long lineId) {
        return lineDao.findOne(lineId);
    }

    @Override
    public void update(Line line) {
        lineDao.update(line);
    }

    @Override
    public Sections getSectionsByLineId(Long lineId) {
        return sectionDao.getSectionsByLineId(lineId);
    }

    @Override
    public void saveSection(Section section) {
        saveSection(lineDao.findOne(section.getLineId()), section);
    }

    @Override
    @Transactional
    public void saveSection(Line line, Section section) {
        line.addSection(section);
        sectionDao.deleteSectionByLineId(line.getId());
        sectionDao.saveSections(line.getSections());
    }

    @Override
    @Transactional
    public void deleteSection(Long lineId, Long stationId) {
        Line line = lineDao.findOne(lineId);
        line.deleteSection(new Station(stationId));
        sectionDao.deleteSectionByLineId(line.getId());
        sectionDao.saveSections(line.getSections());
    }
}
