package asistenciaescolar.asistenciaescolar.Controller;


import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;



@Controller
public class AuthController {

    @GetMapping("/login")
    public String login() {
        return "redirect:/login.html";
    }

    @GetMapping("/dashboard")
    public String dashboard() {
        return "redirect:/dashboard.html";
    }
}
