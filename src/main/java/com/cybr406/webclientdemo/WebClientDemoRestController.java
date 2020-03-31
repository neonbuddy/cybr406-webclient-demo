package com.cybr406.webclientdemo;

import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@RestController
public class WebClientDemoRestController {

    @GetMapping("/get")
    public RequestDetails get(@RequestParam MultiValueMap<String, String> parameters, HttpServletRequest request) {
        return new RequestDetails(request, parameters, null);
    }

    @PostMapping("/post")
    public RequestDetails post(
            @RequestBody String body,
            @RequestParam MultiValueMap<String, String> parameters,
            HttpServletRequest request) {
        return new RequestDetails(request, parameters, body);
    }

    @GetMapping("/secure/get")
    public RequestDetails secureGet(@RequestParam MultiValueMap<String, String> parameters, HttpServletRequest request) {
        return new RequestDetails(request, parameters, null);
    }

    @PostMapping("/secure/post")
    public RequestDetails securePost(
            @RequestBody String body,
            @RequestParam MultiValueMap<String, String> parameters,
            HttpServletRequest request) {
        return new RequestDetails(request, parameters, body);
    }

}
