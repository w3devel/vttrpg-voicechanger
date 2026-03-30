package com.vttrpg.voicechanger.relay.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class JoinController {

    @GetMapping("/join")
    public String join(@RequestParam(required = false) String token) {
        return "forward:/index.html";
    }
}
