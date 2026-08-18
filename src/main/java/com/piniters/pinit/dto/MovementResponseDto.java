package com.piniters.pinit.dto;

import com.piniters.pinit.entity.MovementRecord;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class MovementResponseDto {
    private Double latitude;
    private Double longitude;
    private LocalDateTime recordedAt;

    public MovementResponseDto(MovementRecord record) {
        this.latitude = record.getLatitude();
        this.longitude = record.getLongitude();
        this.recordedAt = record.getRecordedAt();
    }
}