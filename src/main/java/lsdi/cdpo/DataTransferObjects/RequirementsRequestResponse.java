package lsdi.cdpo.DataTransferObjects;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import org.springframework.lang.Nullable;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
public class RequirementsRequestResponse {
    @Nullable
    @JsonProperty("location_area")
    private LocationAreaRequestResponse locationArea;
    @Nullable
    @JsonProperty("start_time")
    private LocalTime startTime;
    @Nullable
    @JsonProperty("end_time")
    private LocalTime endTime;
    @Nullable
    @JsonProperty("start_date")
    private LocalDate startDate;
    @Nullable
    @JsonProperty("end_date")
    private LocalDate endDate;
}
