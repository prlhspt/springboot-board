package springbootboard.board.web;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import springbootboard.board.config.auth.LoginUser;
import springbootboard.board.config.auth.dto.SessionUser;

@Controller
public class HomeController {

    @GetMapping("/")
    public String home(@LoginUser SessionUser user, Model model) {

        if (user != null) {
            model.addAttribute("username", user.getName());
            return "auth/loginHome";
        }

        return "home";
    }

}
