package com.vttrpg.voicechanger.relay.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class JoinController {

    @GetMapping("/join")
    public String join(@RequestParam(required = false) String token) {
        // token is read by client-side JavaScript from the URL query string;
        // the controller only needs to forward to the static page.
        return "forward:/index.html";
    }
}
