package com.naloga.rest.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.naloga.rest.model.Datum;
import com.naloga.rest.repository.DateRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.hateoas.MediaTypes;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultMatcher;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.Arrays;
import java.util.Date;

import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.hamcrest.core.Is.is;
@RunWith(SpringRunner.class)
@WebMvcTest(DateController.class)
@WebAppConfiguration
public class DateControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @MockBean
    private DateRepository dateRepository;

    DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");

    @Test
    public void getDatesTest() throws Exception {
        given(dateRepository.findAll()).willReturn(
                Arrays.asList(
                        new Datum(1L, formatter.parse("2018-10-10")),
                        new Datum(2L, formatter.parse("2018-10-11"))
                )
        );

        mockMvc.perform(get("/dates").accept(MediaTypes.HAL_JSON_VALUE))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect((ResultMatcher) jsonPath("$._embedded.datumList[0].id", is(1)));
    }

    @Test
    public void getDateTest() throws Exception {
        Datum datum = new Datum(1L, formatter.parse("2018-10-10"));

        given(dateRepository.findById(datum.getId())).willReturn(java.util.Optional.of(datum));
        mockMvc.perform(get("/dates/{id}", 1)
                .contentType(MediaType.APPLICATION_JSON)
                .characterEncoding("utf-8"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)));

    }

    @Test
    public void createDateTest() throws Exception {
        Datum datum = new Datum();
        datum.setDate(formatter.parse("2018-10-10"));

        mockMvc.perform(post("/dates")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(datum)))
                .andDo(print())
                .andExpect(status().isCreated());
    }

    @Test
    public void updateTest() throws Exception {


        Datum datum = new Datum();
        datum.setId((long) 1);
        datum.setDate(formatter.parse("2018-10-10"));
        given(dateRepository.findById(datum.getId())).willReturn(java.util.Optional.of(datum));

        Datum datum2 = new Datum();
        datum.setDate(formatter.parse("2018-10-12"));

        mockMvc.perform(put("/dates/{id}", 1)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(datum2)))
                .andDo(print())
                .andExpect(status().isCreated());
    }

    @Test
    public void deleteTest() throws Exception {
        mockMvc.perform(delete("/dates/{id}", 1)
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isNoContent());
    }
}
