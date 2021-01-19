package subway.section;

import subway.station.Station;

import java.util.*;
import java.util.stream.Collectors;

public class Sections {
    private List<Section> sections;

    public Sections(List<Section> sections) {
        this.sections = sections;
        sortByOrder();
    }

    private void sortByOrder() {
        Map<Long, Section> connection = generateConnection();
        Long currentStation = findFirstStation();
        List<Section> orderedSections = new ArrayList<>();
        for (int i = 0; i < sections.size(); ++i) {
            Section currentSection = connection.get(currentStation);
            orderedSections.add(currentSection);
            currentStation = currentSection.getDownStationId();
        }
        this.sections = orderedSections;
    }

    public Long findFirstStation() {
        List<Long> upStations = sections.stream().map(Section::getUpStationId).collect(Collectors.toList());
        List<Long> downStations = sections.stream().map(Section::getDownStationId).collect(Collectors.toList());
        return upStations.stream().filter(station -> !downStations.contains(station)).findFirst().orElseThrow(IllegalArgumentException::new);
    }

    private Map<Long, Section> generateConnection() {
        Map<Long, Section> connection = new HashMap<>();
        for (Section section : sections) {
            connection.put(section.getUpStationId(), section);
        }
        return connection;
    }

    public List<Station> getAllStations() {
        List<Station> stations = sections.stream()
                .map(Section::getUpStation)
                .collect(Collectors.toList());
        Station lastStation = sections.get(sections.size() - 1).getDownStation();
        stations.add(lastStation);
        return stations;
    }

    public void validateSectionRequest(Section section) {
        long containedNumber = getAllStations().stream()
                .map(Station::getId)
                .filter(id -> id.equals(section.getUpStationId()) || id.equals(section.getDownStationId()))
                .count();
        if (containedNumber != 1) {
            throw new IllegalArgumentException();
        }
    }

    public boolean checkSplit(Section section) {
        return !(sections.get(0).getUpStationId().equals(section.getDownStationId()) ||
                sections.get(sections.size() - 1).getDownStationId().equals(section.getUpStationId()));
    }

    public Section findSectionToSplit(Section newSection) {
        Section sectionToSplit = getSectionFromUpStationId(newSection.getUpStationId());
        if(sectionToSplit == null) {
            return getSectionFromDownStationId(newSection.getDownStationId());
        }
        return sectionToSplit;
    }

    public boolean contain(Long stationId) {
        return getAllStations().stream()
                .anyMatch(station -> station.getId().equals(stationId));
    }

    public Section getSectionFromUpStationId(Long stationId) {
        return sections.stream()
                .filter(section -> section.getUpStationId().equals(stationId))
                .findFirst()
                .orElse(null);
    }

    public Section getSectionFromDownStationId(Long stationId) {
        return sections.stream()
                .filter(section -> section.getDownStationId().equals(stationId))
                .findFirst()
                .orElse(null);
    }

    public void validateDeleteSection(Long stationId) {
        if(sections.size() == 1 || !contain(stationId)) {
            throw new IllegalArgumentException();
        }
    }
}
