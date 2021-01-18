package subway.dto;

public class SectionResponse {

    private final Long upStationId;
    private final Long downStationId;
    private final int distance;

    public SectionResponse(Long upStationId, Long downStationId, int distance) {
        this.upStationId = upStationId;
        this.downStationId = downStationId;
        this.distance = distance;
    }
}
