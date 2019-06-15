package com.revolut.services.moneycontrol.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.revolut.services.moneycontrol.model.Error;
import com.revolut.services.moneycontrol.model.ServiceResponse;
import com.revolut.services.moneycontrol.model.Status;
import com.sun.net.httpserver.HttpExchange;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Objects;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Stream;

/**
 * Utility Methods for Money Control
 */
public class Utils {

    private static final Logger LOGGER = Logger.getLogger(Utils.class.getName());
    private static final DateTimeFormatter PATTERN = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.S");
    private static final ObjectMapper MAPPER = new ObjectMapper();

    /**
     * Generate Http Response with given Message and HttpCode
     *
     * @param httpExchange HttpExchange Object
     * @param message Message String
     * @param httpStatus HttpCode
     */
    public static void generateResponse(HttpExchange httpExchange, String message, Status httpStatus) {
        OutputStream os = null;
        try {
            if (Objects.nonNull(httpExchange) && Objects.nonNull(message)) {
                httpExchange.sendResponseHeaders(httpStatus.getCode(), 0);
                os = httpExchange.getResponseBody();
                os.write(createServiceResponse(message, httpStatus.getCode()).getBytes());
            }
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Exception encountered in generateResponse : ", e);
        } finally {
            try {
                if (Objects.nonNull(os))
                    os.close();
            } catch (IOException e) {
                LOGGER.log(Level.SEVERE, "Exception encountered in generateResponse while handling close stream: ", e);
            }
        }
    }

    /**
     * Create ServiceResponse Object from response message and HttpCode
     *
     * @param message Message
     * @param httpCode HttpCode
     * @return String representation of ServiceResponse object
     * @throws JsonProcessingException JsonProcessingException
     */
    private static String createServiceResponse(String message, int httpCode) throws JsonProcessingException {
        ServiceResponse<Object> resp = new ServiceResponse<>();
        resp.setStatus(Arrays.stream(Status.values()).filter(s -> s.getCode() == httpCode).findAny().get());
        if (httpCode != 200) {
            resp.addError(new Error(message));
        } else {
            resp.setPayload(message);
        }
        ObjectMapper mapper = new ObjectMapper();
        return mapper.writeValueAsString(resp);
    }

    /**
     * Extract Payload from HttpRequest
     *
     * @param httpExchange HttpExchange Object
     * @return Optional String - Payload
     */
    public static <T> T extractPayload(HttpExchange httpExchange, Class<T> valueType) throws IOException {
        InputStream payloadStream;
        if (Objects.nonNull(httpExchange) && Objects.nonNull(payloadStream = httpExchange.getRequestBody())) {
            ObjectMapper mapper = new ObjectMapper();
            String content = processStream(payloadStream);
            if(Objects.isNull(content) || content.trim().length() ==0)
                return null;
            return mapper.readValue(content, valueType);
        }
        return null;
    }

    /**
     * Extracts Value for a given Path Param
     *
     * @param httpExchange HttpExchange Object
     * @param field Field
     * @return Optional String - Path Param Value
     */
    public static Optional<String> extractPathParamValue(HttpExchange httpExchange, String field) {
        String path;
        if (Objects.nonNull(field) && Objects.nonNull(httpExchange) && Objects.nonNull(httpExchange.getRequestURI()) &&
                Objects.nonNull(path = httpExchange.getRequestURI().getPath())) {
            String queryParamValues[] = path.split("/");
            int index = 0;
            while (index < queryParamValues.length) {
                if (field.equals(queryParamValues[index]) && queryParamValues.length > (index + 1)) {
                    return Optional.of(queryParamValues[index + 1]);
                }
                index++;
            }
        }
        return Optional.empty();
    }

    /**
     * Process Stream of Http Request Payload
     *
     * @param inputStream given InputStream
     * @return request Payload
     */
    public static String processStream(InputStream inputStream) {
        StringBuilder output = new StringBuilder();
        try (InputStreamReader isr = new InputStreamReader(inputStream);
             BufferedReader br = new BufferedReader(isr)) {

            String content;
            while ((content = br.readLine()) != null) {
                output.append(content);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return output.toString();
    }


    /**
     * Read and transform a given file's content into Stream of String
     *
     * @param fileName given File
     * @return Stream of String
     * @throws IOException IOException
     */
    public static Stream<String> readFile(String fileName) throws IOException {
        if (Objects.nonNull(fileName) && fileName.length() > 0) {
            Path path = Paths.get(fileName);
            return Files.lines(path);
        } else {
            return Stream.empty();
        }
    }

    /**
     * Convert a given Date PATTERN into Instant
     *
     * @param date in String format
     * @return Instant
     */
    public static Instant getInstant(String date) {
        return LocalDateTime.parse(date, PATTERN).toInstant(ZoneOffset.UTC);
    }

    /**
     * Convert a given Instant into String Date PATTERN
     *
     * @param creationDate Instant
     * @return  String Date PATTERN
     */
    public static String getDateString(Instant creationDate) {
        return PATTERN.format(creationDate.atOffset(ZoneOffset.UTC));
    }

    /**
     * Convert an Object into JSON String
     *
     * @param object given Object
     * @return JSON representation in String
     * @throws JsonProcessingException JsonProcessingException
     */
    public static String writeObjAsString(Object object) throws JsonProcessingException {
        return MAPPER.writeValueAsString(object);
    }

    /**
     * Convert a given JSON string into object of given Class
     *
     * @param str JSON string
     * @param clazz ClassName
     * @return Class Object
     * @throws IOException IOException
     */
    public static <T> T readObject(String str, Class<T> clazz) throws IOException {
        return MAPPER.readValue(str, clazz);
    }
}
