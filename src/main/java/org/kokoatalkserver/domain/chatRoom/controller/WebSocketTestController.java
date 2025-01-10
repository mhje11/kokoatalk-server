package org.kokoatalkserver.domain.chatRoom.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class WebSocketTestController {


    @GetMapping("/test")
    public String websocketTest() {
        return "test/websocketTest";
    }
}
