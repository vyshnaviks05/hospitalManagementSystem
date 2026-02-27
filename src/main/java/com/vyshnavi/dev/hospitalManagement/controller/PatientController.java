package com.vyshnavi.dev.hospitalManagement.controller;

import com.vyshnavi.dev.hospitalManagement.dto.PatientRequestDto;
import com.vyshnavi.dev.hospitalManagement.dto.PatientResponseDto;
import com.vyshnavi.dev.hospitalManagement.entity.Patient;
import com.vyshnavi.dev.hospitalManagement.mapper.PatientMapper;
import com.vyshnavi.dev.hospitalManagement.service.PatientService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/patients")
@RequiredArgsConstructor
public class PatientController {

    private final PatientService patientService;
    private final PatientMapper patientMapper;

    @PostMapping
    public ResponseEntity<PatientResponseDto> createPatient(@Valid @RequestBody PatientRequestDto dto) {
        Patient toCreate = patientMapper.toEntity(dto);
        Patient created = patientService.createPatient(toCreate);

        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(created.getId())
                .toUri();

        return ResponseEntity.created(location).body(patientMapper.toResponseDto(created));
    }

    @GetMapping("/{id}")
    public ResponseEntity<PatientResponseDto> getPatient(@PathVariable Long id) {
        Patient patient = patientService.getPatientById(id);
        return ResponseEntity.ok(patientMapper.toResponseDto(patient));
    }

    @GetMapping
    public ResponseEntity<List<PatientResponseDto>> getAllPatients() {
        List<PatientResponseDto> dtos = patientService.getAllPatients()
                .stream()
                .map(patientMapper::toResponseDto)
                .collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }

    @PutMapping("/{id}")
    public ResponseEntity<PatientResponseDto> updatePatient(
            @PathVariable Long id,
            @Valid @RequestBody PatientRequestDto dto) {
        Patient updatedEntity = patientMapper.toEntity(dto);
        Patient saved = patientService.updatePatient(id, updatedEntity);
        return ResponseEntity.ok(patientMapper.toResponseDto(saved));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePatient(@PathVariable Long id) {
        patientService.deletePatient(id);
        return ResponseEntity.noContent().build();
    }
}
