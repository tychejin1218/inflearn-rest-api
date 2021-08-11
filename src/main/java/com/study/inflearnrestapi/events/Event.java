package com.study.inflearnrestapi.events;

import com.study.inflearnrestapi.accounts.Account;
import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Getter @Setter @EqualsAndHashCode(of = "id")
@Builder @NoArgsConstructor @AllArgsConstructor
public class Event {

    @Id @GeneratedValue
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
    private EventStatus eventStatus = EventStatus.DRAFT;
    @ManyToOne
    private Account manager;

    public void update(){
        // Update free
        if(this.basePrice == 0 && this.maxPrice == 0){
            this.free = true;
        } else {
            this.free = false;
        }

        // Update offline
        if(this.location == null || this.location.isBlank()){
            this.offline = false;
        } else {
            this.offline = true;
        }
    }
}