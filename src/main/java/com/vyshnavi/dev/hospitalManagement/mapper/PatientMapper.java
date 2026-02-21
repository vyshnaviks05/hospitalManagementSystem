package com.vyshnavi.dev.hospitalManagement.mapper;

import com.vyshnavi.dev.hospitalManagement.dto.PatientRequestDto;
import com.vyshnavi.dev.hospitalManagement.dto.PatientResponseDto;
import com.vyshnavi.dev.hospitalManagement.entity.Patient;
import org.springframework.stereotype.Component;

@Component
public class PatientMapper {

    public Patient toEntity(PatientRequestDto d){
        if(d == null) return null;
        Patient p = new Patient();
        p.setName(d.getName());
        p.setBirthDate(d.getBirthDate());
        p.setEmail(d.getEmail());
        p.setGender(d.getGender());
        p.setBloodGroup(d.getBloodGroup());
        return p;
    }

    public PatientResponseDto toResponseDto(Patient p){
        if(p == null) return null;
        return new PatientResponseDto(p.getId(), p.getName(), p.getBirthDate(), p.getEmail(), p.getGender(), p.getBloodGroup());
    }
}
