package com.vyshnavi.dev.hospitalManagement.dto;

import com.vyshnavi.dev.hospitalManagement.entity.type.BloodGroupType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BloodGroupCountDto {

    private BloodGroupType bloodGroupType;
    private Long count;
}
