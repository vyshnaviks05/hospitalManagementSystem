package com.vyshnavi.dev.hospitalManagement.controller;

import com.vyshnavi.dev.hospitalManagement.dto.DoctorRequestDto;
import com.vyshnavi.dev.hospitalManagement.dto.DoctorResponseDto;
import com.vyshnavi.dev.hospitalManagement.entity.Doctor;
import com.vyshnavi.dev.hospitalManagement.mapper.DoctorMapper;
import com.vyshnavi.dev.hospitalManagement.service.DoctorService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/doctors")
@RequiredArgsConstructor
public class DoctorController {

    private final DoctorService doctorService;
    private final DoctorMapper doctorMapper;

    // POST /api/doctors
    // Creates a new doctor. Returns 201 Created with Location header.
    @PostMapping
    public ResponseEntity<DoctorResponseDto> createDoctor(@Valid @RequestBody DoctorRequestDto dto) {
        Doctor created = doctorService.createDoctor(doctorMapper.toEntity(dto));

        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(created.getId())
                .toUri();

        return ResponseEntity.created(location).body(doctorMapper.toResponseDto(created));
    }

    // GET /api/doctors/{id}
    // Returns a single doctor by ID. Returns 404 if not found (handled by GlobalExceptionHandler).
    @GetMapping("/{id}")
    public ResponseEntity<DoctorResponseDto> getDoctor(@PathVariable Long id) {
        return ResponseEntity.ok(doctorMapper.toResponseDto(doctorService.getDoctorById(id)));
    }

    // GET /api/doctors
    // Returns all doctors.
    @GetMapping
    public ResponseEntity<List<DoctorResponseDto>> getAllDoctors() {
        List<DoctorResponseDto> dtos = doctorService.getAllDoctors()
                .stream()
                .map(doctorMapper::toResponseDto)
                .collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }

    // PUT /api/doctors/{id}
    // Updates an existing doctor. Returns 404 if not found.
    @PutMapping("/{id}")
    public ResponseEntity<DoctorResponseDto> updateDoctor(
            @PathVariable Long id,
            @Valid @RequestBody DoctorRequestDto dto) {
        Doctor updated = doctorService.updateDoctor(id, doctorMapper.toEntity(dto));
        return ResponseEntity.ok(doctorMapper.toResponseDto(updated));
    }

    // DELETE /api/doctors/{id}
    // Deletes a doctor. Returns 204 No Content.
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteDoctor(@PathVariable Long id) {
        doctorService.deleteDoctor(id);
        return ResponseEntity.noContent().build();
    }
}
