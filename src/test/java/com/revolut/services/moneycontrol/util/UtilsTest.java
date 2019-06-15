package com.revolut.services.moneycontrol.util;

import com.revolut.services.moneycontrol.model.Status;
import com.sun.net.httpserver.HttpExchange;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.time.Instant;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;

@RunWith(MockitoJUnitRunner.class)
public class UtilsTest {

    @Mock
    private HttpExchange httpExchange;
    @Mock
    private OutputStream os;

    @Test
    public void generateResponseNotNullMessage() {
        Mockito.when(httpExchange.getResponseBody()).thenReturn(os);
        Utils.generateResponse(httpExchange, "Test", Status.OK);
        Assert.assertNotNull(httpExchange);
    }

    /**
     * Extract Payload from HttpRequest
     *
     * @param httpExchange HttpExchange Object
     * @return Optional String - Payload
     */
    private static Optional<String> extractPayload(HttpExchange httpExchange) {
        InputStream payloadStream;
        if (Objects.nonNull(httpExchange) && Objects.nonNull(payloadStream = httpExchange.getRequestBody())) {
            return Optional.of(Utils.processStream(payloadStream));
        }
        return Optional.empty();
    }

    @Test
    public void extractPayloadNullHttpExchange() {
        Optional<String> payloadOptional = extractPayload(null);
        Assert.assertNotNull(payloadOptional);
        Assert.assertFalse(payloadOptional.isPresent());
    }

    @Test
    public void extractPayloadNull() {
        Optional<String> payloadOptional = extractPayload(httpExchange);
        Assert.assertNotNull(payloadOptional);
        Assert.assertFalse(payloadOptional.isPresent());
    }

    @Test
    public void extractPayload() {
        String str = "{\n" +
                "\t\"accountNumber\":\"1234567\",\n" +
                "\t\"name\":\"Person1\",\n" +
                "\t\"balance\" : 100.00,\n" +
                "\t\"currency\" : \"AUD\"\n" +
                "}";
        InputStream is = new ByteArrayInputStream( str.getBytes() );
        Mockito.when(httpExchange.getRequestBody()).thenReturn(is);
        Optional<String> payloadOptional = extractPayload(httpExchange);
        Assert.assertNotNull(payloadOptional);
        Assert.assertTrue(payloadOptional.isPresent());
    }

    @Test
    public void getInstantTest() {
        final Instant instant = Utils.getInstant("2019-06-13 00:00:00.0");
        Assert.assertNotNull(instant);
        Assert.assertEquals("2019-06-13T00:00:00Z", instant.toString());
    }

    @Test
    public void getDateStringTest() {
        final Instant instant = Utils.getInstant("2019-06-13 08:27:13.3");
        final String instantString = Utils.getDateString(instant);
        Assert.assertNotNull(instantString);
        Assert.assertEquals("2019-06-13 08:27:13.3", instantString);
    }

    @Test
    public void readFileTestSuccess() throws IOException {
        final Stream<String> instant = Utils.readFile("scripts/tables.sql");
        Assert.assertNotNull(instant);
    }

    @Test
    public void readFileTestFailNullFileName() throws IOException {
        final Stream<String> instant = Utils.readFile(null);
        Assert.assertNotNull(instant);
        Assert.assertFalse(instant.anyMatch(h -> true));
    }

    @Test
    public void readFileTestFailEmptyFileName() throws IOException {
        final Stream<String> instant = Utils.readFile("");
        Assert.assertNotNull(instant);
        Assert.assertFalse(instant.anyMatch(h -> true));
    }

    @Test(expected = IOException.class)
    public void readFileTestFailure() throws IOException {
        Utils.readFile("script/tables.sql");
    }
}
