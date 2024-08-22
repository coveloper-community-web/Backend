package com.covelopment.coveloper.controller;

import com.covelopment.coveloper.controller.MemberController;
import com.covelopment.coveloper.service.MemberService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

@WebMvcTest(MemberController.class)
public class MemberControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private MemberService memberService; // Mock 빈 주입

    @Test
    @WithMockUser // 테스트를 위한 가짜 사용자
    public void testLogin() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/api/members/login")
                        .contentType("application/json")
                        .content("{\"email\":\"test@example.com\", \"password\":\"password\"}")
                        .with(SecurityMockMvcRequestPostProcessors.csrf()))  // CSRF 토큰 추가
                .andExpect(MockMvcResultMatchers.status().isOk());
    }
}
