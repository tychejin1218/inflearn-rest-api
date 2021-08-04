package com.study.inflearnrestapi.events;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.study.inflearnrestapi.common.TestDescription;
import org.hamcrest.Matchers;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.hateoas.MediaTypes;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@AutoConfigureMockMvc
@RunWith(SpringRunner.class)
@SpringBootTest
public class EventControllerTests {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @TestDescription("정상적으로 이벤트를 생성하는 테스트")
    @Test
    public void createEvent() throws Exception {

        EventDto event = EventDto.builder()
                .name("name")
                .description("description")
                .beginEnrollmentDateTime(LocalDateTime.of(2021, 8, 01, 8, 30, 00))
                .closeEnrollmentDateTime(LocalDateTime.of(2021, 8, 31, 5, 30, 00))
                .beginEventDateTime(LocalDateTime.of(2021, 8, 01, 8, 30, 00))
                .endEventDateTime(LocalDateTime.of(2021, 8, 31, 5, 30, 00))
                .location("location")
                .basePrice(1000)
                .maxPrice(2000)
                .limitOfEnrollment(1000)
                .build();

        mockMvc.perform(
                post("/api/events")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaTypes.HAL_JSON_VALUE)
                        .content(objectMapper.writeValueAsString(event)))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("id").exists())
                .andExpect(header().exists(HttpHeaders.LOCATION))
                .andExpect(header().string(HttpHeaders.CONTENT_TYPE, MediaTypes.HAL_JSON_VALUE))
                .andExpect(jsonPath("id").value(Matchers.not(100)))
                .andExpect(jsonPath("free").value(Matchers.not(true)))
                .andExpect(jsonPath("eventStatus").value(EventStatus.DRAFT.name()));
    }

    @TestDescription("입력 받을 수 없는 값을 사용한 경우에 에러가 발생하는 테스트")
    @Test
    public void createEvent_bad_request() throws Exception {

        Event event = Event.builder()
                .id(100)
                .name("name")
                .description("description")
                .beginEnrollmentDateTime(LocalDateTime.of(2021, 8, 01, 8, 30, 00))
                .closeEnrollmentDateTime(LocalDateTime.of(2021, 8, 31, 5, 30, 00))
                .beginEventDateTime(LocalDateTime.of(2021, 8, 01, 8, 30, 00))
                .endEventDateTime(LocalDateTime.of(2021, 8, 31, 5, 30, 00))
                .location("location")
                .basePrice(1000)
                .maxPrice(2000)
                .limitOfEnrollment(1000)
                .free(true)
                .offline(false)
                .eventStatus(EventStatus.PUBLISHED)
                .build();

        mockMvc.perform(post("/api/events")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaTypes.HAL_JSON_VALUE)
                .content(objectMapper.writeValueAsString(event)))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @TestDescription("입력 값이 비어있는 경우에 에러가 발생하는 테스트")
    @Test
    public void createEvent_bad_request_empty_input() throws Exception {

        EventDto eventDto = EventDto.builder().build();

        this.mockMvc.perform(post("/api/events")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaTypes.HAL_JSON_VALUE)
                .content(this.objectMapper.writeValueAsString(eventDto)))
                .andExpect(status().isBadRequest());
    }

    @TestDescription("입력 값이 잘못된 경우 에러가 발생하는 테스트")
    @Test
    public void createEvent_bad_request_wrong_input() throws Exception {

        EventDto eventDto = EventDto.builder()
                .name("name")
                .description("description")
                .beginEnrollmentDateTime(LocalDateTime.of(2021, 8, 31, 8, 30, 00))
                .closeEnrollmentDateTime(LocalDateTime.of(2021, 8, 01, 5, 30, 00))
                .beginEventDateTime(LocalDateTime.of(2021, 8, 31, 8, 30, 00))
                .endEventDateTime(LocalDateTime.of(2021, 8, 01, 5, 30, 00))
                .location("location")
                .basePrice(2000)
                .maxPrice(1000)
                .limitOfEnrollment(1000)
                .build();

        this.mockMvc.perform(post("/api/events")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaTypes.HAL_JSON_VALUE)
                .content(this.objectMapper.writeValueAsString(eventDto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$[0].objectName").exists())
                .andExpect(jsonPath("$[0].defaultMessage").exists())
                .andExpect(jsonPath("$[0].code").exists());
    }
}