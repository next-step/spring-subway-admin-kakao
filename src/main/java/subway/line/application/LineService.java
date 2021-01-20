package subway.line.application;

import org.springframework.dao.IncorrectUpdateSemanticsDataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import subway.exception.LineNotFoundException;
import subway.line.domain.Line;
import subway.line.domain.LineCreateValue;
import subway.line.domain.LineDao;
import subway.line.presentation.LineRequest;
import subway.line.presentation.LineResponse;
import subway.section.application.SectionService;
import subway.section.domain.SectionCreateValue;

import java.util.List;

import static java.util.stream.Collectors.toList;

@Service
@Transactional
public class LineService {

    private final LineDao lineDao;
    private final SectionService sectionService;

    public LineService(LineDao lineDao, SectionService sectionService) {
        this.lineDao = lineDao;
        this.sectionService = sectionService;
    }

    public LineResponse create(LineCreateValue lineCreateValue, SectionCreateValue.Pending pendingSectionValue) {
        if (lineDao.existBy(lineCreateValue.getName())) {
            throw new IllegalArgumentException("이미 등록된 지하철 노선 입니다.");
        }

        Line newLine = lineDao.save(lineCreateValue.toEntity());
        sectionService.createSection(pendingSectionValue.toCreateValue(newLine.getId()));
        return LineResponse.from(newLine, sectionService.getStationsOf(newLine.getId()));
    }

    @Transactional(readOnly = true)
    public LineResponse findBy(Long id) {
        Line line = lineDao.findById(id).orElseThrow(() -> new LineNotFoundException(id));
        return LineResponse.from(line, sectionService.getStationsOf(line.getId()));
    }

    @Transactional(readOnly = true)
    public List<LineResponse> findAll() {
        return lineDao.findAll()
                .stream()
                .map(line -> LineResponse.from(line, sectionService.getStationsOf(line.getId())))
                .collect(toList());
    }

    public void update(LineRequest request, Long id) {
        try {
            lineDao.update(request.toCreateValue().toEntity(id));
        } catch (IncorrectUpdateSemanticsDataAccessException e) {
            throw new LineNotFoundException(id);
        }
    }

    public void delete(Long id) {
        lineDao.deleteById(id);
        sectionService.removeSectionsByLine(id);
    }
}
