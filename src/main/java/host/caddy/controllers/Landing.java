package host.caddy.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class Landing {

    @GetMapping("/")
    public String index() {
        return "temp";
    }
}
