package com.study.inflearnrestapi.config;

import com.study.inflearnrestapi.accounts.Account;
import com.study.inflearnrestapi.accounts.AccountRole;
import com.study.inflearnrestapi.accounts.AccountService;
import com.study.inflearnrestapi.common.BaseControllerTest;
import com.study.inflearnrestapi.common.TestDescription;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Set;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class AuthServerConfigTest extends BaseControllerTest {

    @Autowired
    AccountService accountService;

    @TestDescription("인증 토큰을 발급 받는 테스트")
    @Test
    public void getAuthToken() throws Exception {

        String username = "admin@email.com";
        String password = "password1!";
        Account account = Account.builder()
                .email(username)
                .password(password)
                .roles(Set.of(AccountRole.ADMIN, AccountRole.USER))
                .build();

        String clientId = "myApp";
        String clientSecret = "password1!";

        this.mockMvc.perform(post("/oauth/token")
                .with(httpBasic(clientId, clientSecret))
                .param("username", username)
                .param("password", password)
                .param("grant_type", "password"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("access_token").exists());
    }
}