package springbootboard.board.web.handler;

import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;
import springbootboard.board.web.dto.MemberLoginDto;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
public class CustomAuthenticationFailureHandler implements AuthenticationFailureHandler {

    private final String DEFAULT_FAILURE_URL = "/auth/login?error=true";

    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException, ServletException {
        String username = request.getParameter("username");

        MemberLoginDto memberLoginDto = MemberLoginDto.builder()
                .username(username)
                .build();

        request.setAttribute("memberLoginDto", memberLoginDto);

        request.getRequestDispatcher(DEFAULT_FAILURE_URL).forward(request,response);

    }
}
