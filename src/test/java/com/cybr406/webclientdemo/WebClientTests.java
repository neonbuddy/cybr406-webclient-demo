package com.cybr406.webclientdemo;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClient;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class WebClientTests {

	@LocalServerPort
	private int port;

	@Autowired
	private ObjectMapper objectMapper;

	@Test
	public void testExampleDotCom() {
		WebClient webClient = WebClient.create();
		String content = webClient.get()
				.uri("http://example.com")
				.retrieve()
				.bodyToMono(String.class)
				.block();

		System.out.println(content);

		assertNotNull(content);
	}

	@Test
		public void testSimpleGet() {
		WebClient webClient = WebClient.create("http://localhost:" + port);

		RequestDetails requestDetails = webClient.get()
				.uri("/get")
				.retrieve()
				.bodyToMono(RequestDetails.class)
				.block();

		System.out.println(requestDetails);

		assertNotNull(requestDetails);
		assertEquals("GET", requestDetails.getMethod());
		assertEquals(0, requestDetails.getParameters().size());
		assertEquals(0, requestDetails.getParameters().size());
	}

	@Test
	public void testSimplePost() throws Exception {
		WebClient webClient = WebClient.create("http://localhost:" + port);

		RequestDetails requestDetails = webClient.post()
				.uri("/post")
				.headers(headers -> headers.setContentType(MediaType.APPLICATION_JSON))
				.bodyValue(new ExampleData("Test Key", "Test Value"))
				.retrieve()
				.bodyToMono(RequestDetails.class)
				.block();

		System.out.println(requestDetails);
		assertNotNull(requestDetails);
		assertNotNull(requestDetails.getBody());

		ExampleData exampleData = objectMapper.readValue(requestDetails.getBody(), ExampleData.class);
		assertEquals("Test Key", exampleData.getKey());
		assertEquals("Test Value", exampleData.getValue());
	}

	@Test
	public void testDefaultHeaders() {
		WebClient webClient = WebClient.builder()
				.baseUrl("http://localhost:" + port)
				.defaultHeader("test-header", "test-value1", "test-value2")
				.defaultCookie("test-cookie", "test-cookie1", "test-cookie2")
				.build();

		RequestDetails requestDetails = webClient.get()
				.uri("/get")
				.retrieve()
				.bodyToMono(RequestDetails.class)
				.block();

		System.out.println(requestDetails);

		assertNotNull(requestDetails);
		assertTrue(requestDetails.containsHeaderValue("test-header", "test-value1"));
		assertTrue(requestDetails.containsHeaderValue("test-header", "test-value2"));
		assertTrue(requestDetails.containsHeaderValue("cookie", "test-cookie=test-cookie1"));
		assertTrue(requestDetails.containsHeaderValue("cookie", "test-cookie=test-cookie2"));
		assertFalse(requestDetails.containsHeader("does-not-exist"));
	}

	@Test
	public void testWithoutHttpBasic() {
		WebClient webClient = WebClient.create("http://localhost:" + port);
		ClientResponse response = webClient.get()
				.uri("/secure/get")
				.exchange()
				.block();
		assertNotNull(response);
		assertEquals(HttpStatus.UNAUTHORIZED, response.statusCode());
	}

	@Test
	public void testWithHttpBasic() {
		WebClient webClient = WebClient.create("http://localhost:" + port);
		ClientResponse response = webClient.get()
				.uri("/secure/get")
				.headers(headers -> headers.setBasicAuth("admin", "admin"))
				.exchange()
				.block();

		assertNotNull(response);
		System.out.println(response.bodyToMono(RequestDetails.class).block());
		assertEquals(HttpStatus.OK, response.statusCode());
	}



}
