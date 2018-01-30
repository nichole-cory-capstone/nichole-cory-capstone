package host.caddy.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class LandingController {

    @GetMapping("/")
    public String index() {
       return "index";
    }

    @GetMapping("/about")
    public String about() {
        return "aboutus";
    }

    @GetMapping("/saved")
    public String saved() {
        return "userstrips";
    }
}
