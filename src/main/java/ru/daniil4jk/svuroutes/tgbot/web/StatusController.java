package ru.daniil4jk.svuroutes.tgbot.web;

import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.io.PrintWriter;
import java.io.StringWriter;


@Setter
@Slf4j
@RestController
@RequestMapping("/status")
public class StatusController {
    private Exception lastThrowable = null;

    @GetMapping
    public ResponseEntity<String> getStatus() {
        if (lastThrowable != null) {
            return ResponseEntity.internalServerError()
                    .body(lastThrowable.getMessage());
        } else {
            return ResponseEntity.ok("OK");
        }
    }
}
