
package springbootboard.board.web;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import springbootboard.board.service.AuthService;
import springbootboard.board.web.dto.MemberLoginDto;
import springbootboard.board.web.dto.MemberSaveDto;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

@RequiredArgsConstructor
@RequestMapping("/auth")
@Slf4j
@Controller
public class AuthController {

    private final AuthService authService;

    @GetMapping("/signup")
    public String signupForm(@ModelAttribute("memberSaveDto") MemberSaveDto memberSaveDto) {
        return "auth/signup";
    }

    @PostMapping("/signup")
    public String signup(@Valid @ModelAttribute MemberSaveDto memberSaveDto, BindingResult bindingResult,
                         RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            log.info("errors={}", bindingResult);
            return "auth/signup";
        }

        authService.signup(memberSaveDto, bindingResult);

        if (bindingResult.hasErrors()) {
            log.info("errors={}", bindingResult);
            return "auth/signup";
        }

        if (bindingResult.hasGlobalErrors())

        redirectAttributes.addAttribute("signup", true);
        return "redirect:/";
    }

    @GetMapping("/login")
    public String loginForm(@ModelAttribute("memberLoginDto") MemberLoginDto memberLoginDto,
                            HttpServletRequest request) {

        String uri = request.getHeader("Referer");
        if (uri != null && !uri.contains("/login")) {
            request.getSession().setAttribute("prevPage", uri);
        }

        return "auth/login";
    }

    @PostMapping("/login")
    public void login(@ModelAttribute("memberLoginDto") MemberLoginDto memberLoginDto) {
    }

}