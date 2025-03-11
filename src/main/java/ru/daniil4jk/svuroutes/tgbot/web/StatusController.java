package ru.daniil4jk.svuroutes.tgbot.web;

import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.PrintWriter;
import java.io.StringWriter;


@Slf4j
@RestController
@RequestMapping("/status")
public class StatusController {
    private String stackTraceString;
    private boolean throwableExist;

    @GetMapping
    public String getStatus() {
        if (throwableExist) {
            return stackTraceString;
        } else {
            return "ok";
        }
    }

    public void setLastThrowable(Throwable e) {
        throwableExist = true;
        var stringWriter = new StringWriter();
        var printWriter = new PrintWriter(stringWriter);
        e.printStackTrace(printWriter);
        stackTraceString = stringWriter.toString();
    }
}
