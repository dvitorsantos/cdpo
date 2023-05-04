package lsdi.cdpo.DataTransferObjects;

import lombok.Data;

@Data
public class LocationAreaRequestResponse {
    private String radius;
    private String latitude;
    private String longitude;
}
