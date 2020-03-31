package com.cybr406.webclientdemo;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.springframework.http.HttpHeaders;
import org.springframework.util.MultiValueMap;

import javax.servlet.http.HttpServletRequest;
import java.io.UncheckedIOException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class RequestDetails {

    public static class Entry {

        private String name;

        private List<String> values;

        public Entry() {

        }

        public Entry(String name, List<String> values) {
            this.name = name;
            this.values = values;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public List<String> getValues() {
            return values;
        }

        public void setValues(List<String> values) {
            this.values = values;
        }
    }

    private static final ObjectMapper objectMapper;

    static {
        objectMapper = new ObjectMapper();
        objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
    }

    private String method;

    private List<Entry> parameters = new ArrayList<>();

    private List<Entry> headers = new ArrayList<>();

    private String body;

    public RequestDetails() {

    }

    public RequestDetails(HttpServletRequest request, MultiValueMap<String, String> parameters, String body) {
        this.method = request.getMethod();

        var httpHeaders = new HttpHeaders();
        Enumeration<String> reqHeaders =  request.getHeaderNames();
        while (reqHeaders.hasMoreElements()) {
            String key = reqHeaders.nextElement();
            Enumeration<String> reqValues = request.getHeaders(key);
            List<String> values = new ArrayList<>();
            while (reqValues.hasMoreElements()) {
                values.add(reqValues.nextElement());
            }
            httpHeaders.addAll(key, values);
        }

        this.headers = httpHeaders.entrySet().stream()
                .map(e-> new Entry(e.getKey(), e.getValue()))
                .collect(Collectors.toList());

        this.parameters = parameters.entrySet().stream()
                .map(e-> new Entry(e.getKey(), e.getValue()))
                .collect(Collectors.toList());

        this.body = body;
    }

    public boolean containsHeader(String name) {
        return headers.stream()
                .anyMatch(e -> Objects.equals(e.getName(), name));
    }

    public boolean containsHeaderValue(String name, String value) {
        return headers.stream()
                .filter(e -> Objects.equals(e.getName(), name))
                .flatMap(e -> e.getValues().stream())
                .anyMatch(v -> Objects.equals(v, value));
    }

    @Override
    public String toString() {
        try {
            return objectMapper.writeValueAsString(this);
        } catch (JsonProcessingException e) {
            throw new UncheckedIOException(e);
        }
    }

    public List<Entry> getParameters() {
        return parameters;
    }

    public void setParameters(List<Entry> parameters) {
        this.parameters = parameters;
    }

    public List<Entry> getHeaders() {
        return headers;
    }

    public void setHeaders(List<Entry> headers) {
        this.headers = headers;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }
}
