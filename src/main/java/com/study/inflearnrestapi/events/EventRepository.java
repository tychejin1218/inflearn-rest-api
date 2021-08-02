package com.study.inflearnrestapi.events;

import org.springframework.data.jpa.repository.JpaRepository;

public interface EventRepository extends JpaRepository<Event, Integer> {
}
