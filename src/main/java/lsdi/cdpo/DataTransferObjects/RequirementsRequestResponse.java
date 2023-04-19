package lsdi.cdpo.DataTransferObjects;

import jakarta.annotation.Nullable;
import lombok.Data;

@Data
public class RequirementsRequestResponse {
    private Double minBattery = 0.0;
    private Double minCpu = 100.0;
    private Double minRam = 100.0;
    private LocationAreaRequestResponse locationArea;
}
