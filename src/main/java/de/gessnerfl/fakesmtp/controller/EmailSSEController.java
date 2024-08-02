package de.gessnerfl.fakesmtp.controller;

import de.gessnerfl.fakesmtp.service.EmailSSEService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@RestController
public class EmailSSEController {
    EmailSSEService emailSSEService;

    @Autowired
    public EmailSSEController(EmailSSEService emailSSEService) {
        this.emailSSEService = emailSSEService;
    }

    @GetMapping(path = "api/events/emails", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter eventStream(@RequestParam(value = "timeout", required = false) Long timeout) {
        return emailSSEService.subscribe(timeout);
    }
}