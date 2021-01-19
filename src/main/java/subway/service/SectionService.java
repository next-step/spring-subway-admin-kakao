package subway.service;


import subway.domain.Section;
import subway.dto.SectionRequest;
import subway.domain.Sections;

import java.util.List;

public interface SectionService {
    Section save(Section section);

    Sections getSectionsByLineId(Long lineId);

    boolean deleteSectionById(Long sectionId);

    void deleteSectionByLineId(Long lineId);

    boolean deleteSection(Long lineId, Long stationId);

    boolean saveSection(Section section);
}
