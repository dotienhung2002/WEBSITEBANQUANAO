package com.application.fusamate.rest.controller;

import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@RestController
public class ApiErrorController implements ErrorController {
    @RequestMapping("/error")
    public void globalError(HttpServletResponse response , HttpServletRequest request){
        System.out.println("apiiiįinvokeinvokeinvokeinvokeinvokeinvokeinvoke");
        throw new ResponseStatusException(HttpStatus.valueOf(response.getStatus()));
    }
}
