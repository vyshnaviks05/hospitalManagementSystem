package com.vyshnavi.dev.hospitalManagement.controller;

import com.vyshnavi.dev.hospitalManagement.dto.DoctorRequestDto;
import com.vyshnavi.dev.hospitalManagement.dto.DoctorResponseDto;
import com.vyshnavi.dev.hospitalManagement.service.DoctorService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

@RestController
@RequestMapping("/api/doctors")
@RequiredArgsConstructor
public class DoctorController {

    private final DoctorService doctorService;

    @PostMapping
    public ResponseEntity<DoctorResponseDto> createDoctor(
            @Valid @RequestBody DoctorRequestDto requestDto) {

        DoctorResponseDto createdDoctor =
                doctorService.createDoctor(requestDto);

        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(createdDoctor.getId())
                .toUri();

        return ResponseEntity.created(location)
                .body(createdDoctor);
    }

    @GetMapping("/{id}")
    public ResponseEntity<DoctorResponseDto> getDoctor(
            @PathVariable Long id) {

        return ResponseEntity.ok(
                doctorService.getDoctorById(id)
        );
    }

    @GetMapping
    public ResponseEntity<Page<DoctorResponseDto>> getAllDoctors(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDirection) {

        return ResponseEntity.ok(
                doctorService.getAllDoctors(
                        page,
                        size,
                        sortBy,
                        sortDirection
                )
        );
    }

    @GetMapping("/search")
    public ResponseEntity<Page<DoctorResponseDto>>
    searchDoctorsBySpecialization(
            @RequestParam String specialization,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDirection) {

        return ResponseEntity.ok(
                doctorService.searchDoctorsBySpecialization(
                        specialization,
                        page,
                        size,
                        sortBy,
                        sortDirection
                )
        );
    }

    @PutMapping("/{id}")
    public ResponseEntity<DoctorResponseDto> updateDoctor(
            @PathVariable Long id,
            @Valid @RequestBody DoctorRequestDto requestDto) {

        return ResponseEntity.ok(
                doctorService.updateDoctor(id, requestDto)
        );
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteDoctor(
            @PathVariable Long id) {

        doctorService.deleteDoctor(id);

        return ResponseEntity.noContent().build();
    }
}