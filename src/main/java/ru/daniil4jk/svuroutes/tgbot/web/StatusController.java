package ru.daniil4jk.svuroutes.tgbot.web;

import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.PrintWriter;
import java.io.StringWriter;

@Setter
@Slf4j
@RestController
@RequestMapping("/status")
public class StatusController {
    private Throwable lastThrowable;

    @GetMapping
    public String getStatus() {
        if (lastThrowable == null) {
            return "ok";
        } else {
            return getStackStaceInString(lastThrowable);
        }
    }

    private String getStackStaceInString(Throwable e) {
        var stringWriter = new StringWriter();
        var printWriter = new PrintWriter(stringWriter);
        e.printStackTrace(printWriter);
        return stringWriter.toString();
    }
}
