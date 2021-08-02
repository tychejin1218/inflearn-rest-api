package com.study.inflearnrestapi.events;

import lombok.*;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import java.time.LocalDateTime;

@Builder @AllArgsConstructor @NoArgsConstructor
@Getter @Setter @EqualsAndHashCode(of = "id")
@Entity
public class Event {

    @Id @Generated
    private Integer id;
    private String name;
    private String description;
    private LocalDateTime beginEnrollmentDateTime;
    private LocalDateTime closeEnrollmentDateTime;
    private LocalDateTime endEventDateTime;
    private LocalDateTime beginEventDateTime;
    private String location;
    private int basePrice;
    private int maxPrice;
    private int limitOfEnrollment;
    private boolean offline;
    private boolean free;
    @Enumerated(EnumType.STRING)
    private EventStatus eventStatus;
}