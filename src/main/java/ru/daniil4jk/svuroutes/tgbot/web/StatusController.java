package ru.daniil4jk.svuroutes.tgbot.web;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.io.PrintWriter;
import java.io.StringWriter;


@Slf4j
@RestController
@RequestMapping("/status")
public class StatusController {
    private ExceptionWrapper e;
    private boolean throwableExist;

    @GetMapping
    public String getStatus() throws ExceptionWrapper {
        if (throwableExist) {
            throw e;
        } else {
            return "OK";
        }
    }

    public void setLastThrowable(Throwable e) {
        this.e = new ExceptionWrapper(e);
        throwableExist = true;
    }

    @ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR)
    public static class ExceptionWrapper extends Exception {
        public ExceptionWrapper(Throwable e) {
            super(e);
        }
    }
}
