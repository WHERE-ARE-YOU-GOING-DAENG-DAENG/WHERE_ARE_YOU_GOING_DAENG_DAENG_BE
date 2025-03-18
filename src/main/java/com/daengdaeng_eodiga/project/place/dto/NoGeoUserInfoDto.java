package com.daengdaeng_eodiga.project.place.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class NoGeoUserInfoDto {
    private Double latitude;
    private Double longitude;
    private String PlaceName;
}
