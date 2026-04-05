package com.ratelimit.user.controller;

import com.ratelimit.user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Instant;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserController.class)
public class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private UserService userService;

//    @Test
//    void getUser_returnsDto() throws Exception {
//        Mockito.when(userService.findOrCreateByKeycloakId("kc-1"))
//                .thenReturn(new UserDTO("kc-1", Instant.parse("2020-01-01T00:00:00Z")));
//
//        mockMvc.perform(get("/user").param("keycloakId", "kc-1"))
//                .andExpect(status().isOk())
//                .andExpect(content().json("{\"keycloakId\":\"kc-1\",\"createdAt\":\"2020-01-01T00:00:00Z\"}"));
//    }
}

