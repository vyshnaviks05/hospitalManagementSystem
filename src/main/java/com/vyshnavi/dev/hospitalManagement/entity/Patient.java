package com.vyshnavi.dev.hospitalManagement.entity;

import com.vyshnavi.dev.hospitalManagement.entity.type.BloodGroupType;
import com.vyshnavi.dev.hospitalManagement.entity.type.GenderType;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.CreationTimestamp;
import jakarta.persistence.Column;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(
        name = "patient",
        uniqueConstraints = {
                @UniqueConstraint(name = "uq_patient_name_birthdate", columnNames = {"name", "birth_date"})
        }
)
@Getter
@Setter
@ToString
public class Patient {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 40)
    private String name;

    @Column(name = "birth_date")
    @ToString.Exclude
    private LocalDate birthDate;

    @Column(nullable = false, unique = true)
    private String email;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private GenderType gender;

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;

    @Enumerated(EnumType.STRING)
    private BloodGroupType bloodGroup;

    @OneToOne(
            cascade = {CascadeType.PERSIST, CascadeType.REMOVE},
            orphanRemoval = true,
            fetch = FetchType.LAZY
    )
    @JoinColumn(name = "insurance_id")
    @ToString.Exclude
    private Insurance insurance;

    // LAZY fetch avoids loading all appointments unless explicitly needed
    @OneToMany(mappedBy = "patient", cascade = CascadeType.REMOVE, orphanRemoval = true, fetch = FetchType.LAZY)
    @ToString.Exclude
    private List<Appointment> appointments = new ArrayList<>();

    @UpdateTimestamp
    @Column(nullable = false)
    private LocalDateTime updatedAt;
}
