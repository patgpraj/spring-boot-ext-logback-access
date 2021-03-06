package net.rakugakibox.springbootext.logback.access;

import ch.qos.logback.access.spi.IAccessEvent;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Callable;
import javax.servlet.http.HttpServletResponse;
import static net.rakugakibox.springbootext.logback.access.test.AccessEventAssert.assertThat;
import net.rakugakibox.springbootext.logback.access.test.NamedEventQueues;
import net.rakugakibox.springbootext.logback.access.test.NamedEventQueuesRule;
import static org.assertj.core.api.Assertions.assertThat;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestRule;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.boot.test.TestRestTemplate;
import org.springframework.boot.test.WebIntegrationTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

/**
 * The base class to test general cases.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration
@WebIntegrationTest(
        value = "logback.access.config=classpath:logback-access-test.named-event-queue.xml",
        randomPort = true
)
public abstract class AbstractGeneralTest {

    /**
     * The server port.
     */
    @Value("${local.server.port}")
    private int port;

    /**
     * The REST template.
     */
    @Autowired
    private RestTemplate rest;

    /**
     * Creates a test rule.
     *
     * @return a test rule.
     */
    @Rule
    public TestRule rule() {
        return new NamedEventQueuesRule();
    }

    /**
     * Tests the basic attributes.
     */
    @Test
    public void testBasicAttributes() {

        RequestEntity<Void> request = RequestEntity
                .get(url("/text").build().toUri())
                .build();

        LocalDateTime startTime = LocalDateTime.now();
        ResponseEntity<String> response = rest.exchange(request, String.class);
        IAccessEvent event = NamedEventQueues.pop();
        LocalDateTime endTime = LocalDateTime.now();

        assertThat(response.getBody())
                .isEqualTo("text");

        assertThat(event)
                .hasTimestamp(startTime, endTime)
                .hasServerName("localhost")
                .hasLocalPort(port)
                .hasRemoteAddr("127.0.0.1")
                .hasRemoteHost("127.0.0.1")
                .hasRemoteUser(null)
                .hasProtocol("HTTP/1.1")
                .hasMethod(HttpMethod.GET)
                .hasRequestUri("/text")
                .hasQueryString("")
                .hasRequestUrl(HttpMethod.GET, "/text", "HTTP/1.1")
                .hasStatusCode(HttpStatus.OK)
                .hasContentLength(response.getBody().getBytes().length)
                .hasElapsedTime(startTime, endTime)
                .hasElapsedSeconds(startTime, endTime)
                .hasThreadName();

    }

    /**
     * Tests the basic attributes (async).
     */
    @Test
    public void testBasicAttributesAsync() {

        RequestEntity<Void> request = RequestEntity
                .get(url("/text-async").build().toUri())
                .build();

        LocalDateTime startTime = LocalDateTime.now();
        ResponseEntity<String> response = rest.exchange(request, String.class);
        IAccessEvent event = NamedEventQueues.pop();
        LocalDateTime endTime = LocalDateTime.now();

        assertThat(response.getBody())
                .isEqualTo("text");

        assertThat(event)
                .hasTimestamp(startTime, endTime)
                .hasServerName("localhost")
                .hasLocalPort(port)
                .hasRemoteAddr("127.0.0.1")
                .hasRemoteHost("127.0.0.1")
                .hasRemoteUser(null)
                .hasProtocol("HTTP/1.1")
                .hasMethod(HttpMethod.GET)
                .hasRequestUri("/text-async")
                .hasQueryString("")
                .hasRequestUrl(HttpMethod.GET, "/text-async", "HTTP/1.1")
                .hasStatusCode(HttpStatus.OK)
                .hasContentLength(response.getBody().getBytes().length)
                .hasElapsedTime(startTime, endTime)
                .hasElapsedSeconds(startTime, endTime)
                .hasThreadName();

    }

    /**
     * Tests the query string.
     */
    @Test
    public void testQueryString() {

        RequestEntity<Void> request = RequestEntity
                .get(url("/text").query("query").build().toUri())
                .build();

        ResponseEntity<String> response = rest.exchange(request, String.class);
        IAccessEvent event = NamedEventQueues.pop();

        assertThat(response.getBody())
                .isEqualTo("text");

        assertThat(event)
                .hasQueryString("?query")
                .hasRequestUrl(HttpMethod.GET, "/text?query", "HTTP/1.1");

    }

    /**
     * Tests the request header.
     */
    @Test
    public void testRequestHeader() {

        RequestEntity<Void> request = RequestEntity
                .get(url("/text").build().toUri())
                .header("X-Test-Header", "header")
                .build();

        ResponseEntity<String> response = rest.exchange(request, String.class);
        IAccessEvent event = NamedEventQueues.pop();

        assertThat(response.getBody())
                .isEqualTo("text");

        assertThat(event)
                .hasRequestHeader("X-Test-Header", "header");

    }

    /**
     * Tests the request parameter.
     */
    @Test
    public void testRequestParameter() {

        RequestEntity<Void> request = RequestEntity
                .get(url("/text").queryParam("param", "value1", "value2").build().toUri())
                .build();

        ResponseEntity<String> response = rest.exchange(request, String.class);
        IAccessEvent event = NamedEventQueues.pop();

        assertThat(response.getBody())
                .isEqualTo("text");

        assertThat(event)
                .hasQueryString("?param=value1&param=value2")
                .hasRequestUrl(HttpMethod.GET, "/text?param=value1&param=value2", "HTTP/1.1")
                .hasRequestParameter("param", "value1", "value2");

    }

    /**
     * Tests the content length when "Content-Length" response header is contained.
     */
    @Test
    public void testContentLengthWhenHeaderIsContained() {

        RequestEntity<Void> request = RequestEntity
                .get(url("/text").build().toUri())
                .build();

        ResponseEntity<String> response = rest.exchange(request, String.class);
        IAccessEvent event = NamedEventQueues.pop();

        assertThat(response.getHeaders())
                .containsKey(HttpHeaders.CONTENT_LENGTH);
        assertThat(response.getBody())
                .isEqualTo("text");

        assertThat(event)
                .hasContentLength(response.getBody().getBytes().length);

    }

    /**
     * Tests the content length when "Content-Length" response header is not contained.
     */
    @Test
    public void testContentLengthWhenHeaderIsNotContained() {

        RequestEntity<Void> request = RequestEntity
                .get(url("/json").build().toUri())
                .build();

        ResponseEntity<String> response = rest.exchange(request, String.class);
        IAccessEvent event = NamedEventQueues.pop();

        assertThat(response.getHeaders())
                .doesNotContainKey(HttpHeaders.CONTENT_LENGTH);
        assertThat(response.getBody())
                .containsSequence("json-key", ":", "json-value");

        assertThat(event)
                .hasContentLength(response.getBody().getBytes().length);

    }

    /**
     * Tests the response header.
     */
    @Test
    public void testResponseHeader() {

        RequestEntity<Void> request = RequestEntity
                .get(url("/text-with-header").build().toUri())
                .build();

        ResponseEntity<String> response = rest.exchange(request, String.class);
        IAccessEvent event = NamedEventQueues.pop();

        assertThat(response.getBody())
                .isEqualTo("text");

        assertThat(event)
                .hasResponseHeader("X-Test-Header", "header");

    }

    /**
     * Starts building the URL.
     *
     * @param path the path of URL.
     * @return a URI components builder.
     */
    private UriComponentsBuilder url(String path) {
        return UriComponentsBuilder.newInstance()
                .scheme("http")
                .host("localhost")
                .port(port)
                .path(path);
    }

    /**
     * The context configuration.
     */
    @Configuration
    @EnableAutoConfiguration
    public static class TestContextConfiguration {

        /**
         * Creates a REST template.
         *
         * @return a REST template.
         */
        @Bean
        public RestTemplate testRestTemplate() {
            return new TestRestTemplate();
        }

        /**
         * Creates a controller.
         *
         * @return a controller.
         */
        @Bean
        public TestController testController() {
            return new TestController();
        }

    }

    /**
     * The controller.
     */
    @RestController
    public static class TestController {

        /**
         * Gets the text.
         *
         * @return the text.
         */
        @RequestMapping(path = "/text", method = RequestMethod.GET, produces = MediaType.TEXT_PLAIN_VALUE)
        public String getText() {
            return "text";
        }

        /**
         * Gets the text (async).
         *
         * @return the text.
         */
        @RequestMapping(path = "/text-async", method = RequestMethod.GET, produces = MediaType.TEXT_PLAIN_VALUE)
        public Callable<String> getTextAsync() {
            return this::getText;
        }

        /**
         * Gets the text with header.
         *
         * @param response the HTTP response.
         * @return the text with header.
         */
        @RequestMapping(path = "/text-with-header", method = RequestMethod.GET, produces = MediaType.TEXT_PLAIN_VALUE)
        public String getTextWithHeader(HttpServletResponse response) {
            response.addHeader("X-Test-Header", "header");
            return getText();
        }

        /**
         * Gets the JSON.
         *
         * @return the JSON.
         */
        @RequestMapping(path = "/json", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
        public Map<String, Object> getJson() {
            Map<String, Object> map = new HashMap<>();
            map.put("json-key", "json-value");
            return map;
        }

    }

}
