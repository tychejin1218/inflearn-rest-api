package com.study.inflearnrestapi.events;

import com.study.inflearnrestapi.accounts.Account;
import com.study.inflearnrestapi.accounts.AccountRepository;
import com.study.inflearnrestapi.accounts.AccountRole;
import com.study.inflearnrestapi.accounts.AccountService;
import com.study.inflearnrestapi.common.AppProperties;
import com.study.inflearnrestapi.common.BaseTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.MediaTypes;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.oauth2.common.util.Jackson2JsonParser;
import org.springframework.test.web.servlet.ResultActions;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.stream.IntStream;

import static org.springframework.restdocs.headers.HeaderDocumentation.*;
import static org.springframework.restdocs.hypermedia.HypermediaDocumentation.linkWithRel;
import static org.springframework.restdocs.hypermedia.HypermediaDocumentation.links;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

public class EventControllerTests extends BaseTest {

    @Autowired
    EventRepository eventRepository;

    @Autowired
    AccountService accountService;

    @Autowired
    AccountRepository accountRepository;

    @Autowired
    AppProperties appProperties;

    @BeforeEach
    public void setUp() {
        this.eventRepository.deleteAll();
        this.accountRepository.deleteAll();
    }

    @DisplayName("정상적으로 이벤트를 생성하는 테스트")
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

        ResultActions resultActions = mockMvc.perform(
                post("/api/events")
                        .header(HttpHeaders.AUTHORIZATION, getBearerToken(true))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaTypes.HAL_JSON_VALUE)
                        .content(objectMapper.writeValueAsString(event)))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("id").exists())
                .andExpect(header().exists(HttpHeaders.LOCATION))
                .andExpect(header().string(HttpHeaders.CONTENT_TYPE, MediaTypes.HAL_JSON_VALUE))
                .andExpect(jsonPath("free").value(false))
                .andExpect(jsonPath("offline").value(true))
                .andExpect(jsonPath("eventStatus").value(EventStatus.DRAFT.name()))
                .andDo(document("create-event",
                        links(
                                linkWithRel("self").description("link to self"),
                                linkWithRel("query-events").description("link to query events"),
                                linkWithRel("update-event").description("link to update an existing event"),
                                linkWithRel("profile").description("link to update an existing event")
                        ),
                        requestHeaders(
                                headerWithName(HttpHeaders.ACCEPT).description("accept header"),
                                headerWithName(HttpHeaders.CONTENT_TYPE).description("content_type header")
                        ),
                        relaxedRequestFields(
                                fieldWithPath("name").description("name of new event"),
                                fieldWithPath("description").description("description of new event"),
                                fieldWithPath("beginEnrollmentDateTime").description("date time of begin of new event"),
                                fieldWithPath("closeEnrollmentDateTime").description("date time of close of new event"),
                                fieldWithPath("beginEventDateTime").description("date time of begin of new event"),
                                fieldWithPath("endEventDateTime").description("date time of end of new event"),
                                fieldWithPath("location").description("location of new event"),
                                fieldWithPath("basePrice").description("base price of new event"),
                                fieldWithPath("maxPrice").description("max price of new event"),
                                fieldWithPath("limitOfEnrollment").description("limit of enrollment of event")
                        ),
                        responseHeaders(
                                headerWithName(HttpHeaders.LOCATION).description("location header"),
                                headerWithName(HttpHeaders.CONTENT_TYPE).description("content_type header")
                        ),
                        relaxedResponseFields(
                                fieldWithPath("id").description("identifier of new event"),
                                fieldWithPath("name").description("Name of new event"),
                                fieldWithPath("description").description("description of new event"),
                                fieldWithPath("beginEnrollmentDateTime").description("date time of begin of new event"),
                                fieldWithPath("closeEnrollmentDateTime").description("date time of close of new event"),
                                fieldWithPath("beginEventDateTime").description("date time of begin of new event"),
                                fieldWithPath("endEventDateTime").description("date time of end of new event"),
                                fieldWithPath("location").description("location of new event"),
                                fieldWithPath("basePrice").description("base price of new event"),
                                fieldWithPath("maxPrice").description("max price of new event"),
                                fieldWithPath("limitOfEnrollment").description("limit of enrollment"),
                                fieldWithPath("free").description("it tells if this event is free or not"),
                                fieldWithPath("offline").description("it tells if this event is offline event or not"),
                                fieldWithPath("eventStatus").description("event status"),
                                fieldWithPath("_links.self.href").description("link to self"),
                                fieldWithPath("_links.query-events.href").description("link to query event list"),
                                fieldWithPath("_links.update-event.href").description("link to update existing event"),
                                fieldWithPath("_links.profile.href").description("link to profile")
                        )
                ));
    }

    @DisplayName("입력 받을 수 없는 값을 사용한 경우에 에러가 발생하는 테스트")
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
                .header(HttpHeaders.AUTHORIZATION, getBearerToken(true))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaTypes.HAL_JSON_VALUE)
                .content(objectMapper.writeValueAsString(event)))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @DisplayName("입력 값이 비어있는 경우에 에러가 발생하는 테스트")
    @Test
    public void createEvent_bad_request_empty_input() throws Exception {

        EventDto eventDto = EventDto.builder().build();

        this.mockMvc.perform(post("/api/events")
                .header(HttpHeaders.AUTHORIZATION, getBearerToken(true))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaTypes.HAL_JSON_VALUE)
                .content(this.objectMapper.writeValueAsString(eventDto)))
                .andExpect(status().isBadRequest());
    }

    @DisplayName("입력 값이 잘못된 경우 에러가 발생하는 테스트")
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
                .header(HttpHeaders.AUTHORIZATION, getBearerToken(true))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaTypes.HAL_JSON_VALUE)
                .content(this.objectMapper.writeValueAsString(eventDto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("errors[0].objectName").exists())
                .andExpect(jsonPath("errors[0].defaultMessage").exists())
                .andExpect(jsonPath("errors[0].code").exists())
                .andExpect(jsonPath("_links.index").exists())
        ;
    }

    @DisplayName("30개의 이벤트를 10개씩 두번째 페이지 조회하기")
    @Test
    public void queryEvents() throws Exception {

        //Given
        IntStream.range(0, 30).forEach(i -> {
            this.generateEvent(i);
        });

        //When & Then
        this.mockMvc.perform(get("/api/events")
                .param("page", "1")
                .param("size", "10")
                .param("sort", "name,Desc"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("page").exists())
                .andExpect(jsonPath("_embedded.eventList[0]._links.self").exists())
                .andExpect(jsonPath("_links.self").exists())
                .andExpect(jsonPath("_links.profile").exists())
                .andDo(document("query-events"))
        ;
    }

    @DisplayName("30개의 이벤트를 10개씩 두번째 페이지 조회하기_인증")
    @Test
    public void queryEventsWithAuthentication() throws Exception {

        // Given
        IntStream.range(0, 30).forEach(this::generateEvent);

        // When & Then
        this.mockMvc.perform(get("/api/events")
                .header(HttpHeaders.AUTHORIZATION, getBearerToken(true))
                .param("page", "1")
                .param("size", "10")
                .param("sort", "name,DESC"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("page").exists())
                .andExpect(jsonPath("_embedded.eventList[0]._links.self").exists())
                .andExpect(jsonPath("_links.self").exists())
                .andExpect(jsonPath("_links.profile").exists())
                .andExpect(jsonPath("_links.create-event").exists())
                .andDo(document("query-events"))
        ;
    }

    @DisplayName("기존의 이벤트를 하나 조회하기")
    @Test
    public void getEvent() throws Exception {

        //Given
        Account account = this.createAccount();
        Event event = this.generateEvent(100, account);

        //When & Then
        this.mockMvc.perform(get("/api/events/{id}", event.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("name").exists())
                .andExpect(jsonPath("id").exists())
                .andExpect(jsonPath("_links.self").exists())
                .andExpect(jsonPath("_links.profile").exists())
        ;
    }

    @DisplayName("없는 이벤트는 조회했을 때 404 응답받기")
    @Test
    public void getEvent404() throws Exception {

        // When & Then
        this.mockMvc.perform(get("/api/events/99999"))
                .andExpect(status().isNotFound())
        ;
    }

    @DisplayName("이벤트를 정상적으로 수정하기")
    @Test
    public void updateEvent() throws Exception {
        // Given
        Account account = this.createAccount();
        Event event = this.generateEvent(100, account);

        EventDto eventDto = this.modelMapper.map(event, EventDto.class);
        String eventName = "Updated Event";
        eventDto.setName(eventName);

        // When & Then
        this.mockMvc.perform(put("/api/events/{id}", event.getId())
                .header(HttpHeaders.AUTHORIZATION, getBearerToken(false))
                .contentType(MediaType.APPLICATION_JSON)
                .content(this.objectMapper.writeValueAsString(eventDto)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("name").value(eventName))
                .andExpect(jsonPath("_links.self").exists())
                .andDo(document("update-event"))
        ;
    }

    @DisplayName("입력값이 비어있는 경우에 이벤트 수정 실패")
    @Test
    public void updateEvent400Empty() throws Exception {
        // Given
        Event event = this.generateEvent(100);

        EventDto eventDto = new EventDto();

        // When & Then
        this.mockMvc.perform(put("/api/events/{id}", event.getId())
                .header(HttpHeaders.AUTHORIZATION, getBearerToken(true))
                .contentType(MediaType.APPLICATION_JSON)
                .content(this.objectMapper.writeValueAsString(eventDto)))
                .andDo(print())
                .andExpect(status().isBadRequest())
        ;
    }

    @DisplayName("입력값이 잘못된 경우에 이벤트 수정 실패")
    @Test
    public void updateEvent400Wrong() throws Exception {
        // Given
        Event event = this.generateEvent(100);

        EventDto eventDto = this.modelMapper.map(event, EventDto.class);
        eventDto.setBasePrice(20000);
        eventDto.setMaxPrice(10000);

        // When & Then
        this.mockMvc.perform(put("/api/events/{id}", event.getId())
                .header(HttpHeaders.AUTHORIZATION, getBearerToken(true))
                .contentType(MediaType.APPLICATION_JSON)
                .content(this.objectMapper.writeValueAsString(eventDto)))
                .andDo(print())
                .andExpect(status().isBadRequest())
        ;
    }

    @DisplayName("존재하지 않는 이벤트 수정 실패")
    @Test
    public void updateEvent404() throws Exception {
        // Given
        Event event = this.generateEvent(100);
        EventDto eventDto = this.modelMapper.map(event, EventDto.class);

        // When & Then
        this.mockMvc.perform(put("/api/events/99999")
                .header(HttpHeaders.AUTHORIZATION, getBearerToken(true))
                .contentType(MediaType.APPLICATION_JSON)
                .content(this.objectMapper.writeValueAsString(eventDto)))
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    private Event generateEvent(int index, Account account) {
        Event event = buildEvent(index);
        event.setManager(account);
        return this.eventRepository.save(event);
    }

    private Event generateEvent(int index) {
        Event event = buildEvent(index);
        return this.eventRepository.save(event);
    }

    private Event buildEvent(int index) {
        return Event.builder()
                .name("name_" + index)
                .description("description_" + index)
                .beginEnrollmentDateTime(LocalDateTime.of(2021, 8, 01, 8, 30, 00))
                .closeEnrollmentDateTime(LocalDateTime.of(2021, 8, 31, 5, 30, 00))
                .beginEventDateTime(LocalDateTime.of(2021, 8, 01, 8, 30, 00))
                .endEventDateTime(LocalDateTime.of(2021, 8, 31, 5, 30, 00))
                .location("location")
                .basePrice(1000)
                .maxPrice(2000)
                .limitOfEnrollment(1000)
                .free(false)
                .offline(true)
                .eventStatus(EventStatus.DRAFT)
                .build();
    }

    private String getBearerToken(boolean needToCreateAccount) throws Exception {
        return "Bearer " + getAccessToken(needToCreateAccount);
    }

    public String getAccessToken(boolean needToCreateAccount) throws Exception {

        if (needToCreateAccount) {
            createAccount();
        }

        ResultActions perform = this.mockMvc.perform(post("/oauth/token")
                .with(httpBasic(appProperties.getClientId(), appProperties.getClientSecret()))
                .param("username", appProperties.getAdminUsername())
                .param("password", appProperties.getAdminPassword())
                .param("grant_type", "password"));

        var responseBody = perform.andReturn().getResponse().getContentAsString();
        Jackson2JsonParser parser = new Jackson2JsonParser();
        return parser.parseMap(responseBody).get("access_token").toString();
    }

    public Account createAccount(){
        Account account = Account.builder()
                .email(appProperties.getAdminUsername())
                .password(appProperties.getAdminPassword())
                .roles(Set.of(AccountRole.ADMIN, AccountRole.USER))
                .build();
        return this.accountService.saveAccount(account);
    }
}