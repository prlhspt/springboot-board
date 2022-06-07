package springbootboard.board.web;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@SpringBootTest
class AuthControllerTest {

    @Autowired AuthService authService;
    @Autowired MockMvc mvc;

    @WithMockUser(roles = "ADMIN")
    @Test
    @DisplayName("어드민 페이지 접근 권한 테스트")
    void adminPageTest() throws Exception {
        mvc.perform(MockMvcRequestBuilders
                        .get("/admin"))
                .andExpect(status().isOk());
    }
}