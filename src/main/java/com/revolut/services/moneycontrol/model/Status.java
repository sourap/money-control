package com.revolut.services.moneycontrol.model;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;

/**
 * Status Enum to Capture All HTTP Status Codes
 */
@XmlType(name = "Status")
@XmlEnum
public enum Status {

    OK(200, "Request succeeded"), CREATED(201, "Resource created"), ACCEPTED(202, "Request accepted"), NO_CONTENT(204, "No content found"), PARTIAL(206, "Request partially succeeded"),
    MOVED_PERMANENT(301, "Moved permanently"), FOUND(302, "Found"), SEE_OTHER(303, "See Other"), NOT_MODIFIED(304, "Not Modified"), TEMPORARY_REDIRECT(307, "temporary Redirect"),
    BAD_REQUEST(400, "Bad Request"), UNAUTHORIZED(401, "Unauthorized"), FORBIDDEN(403, "Forbidden"), NOT_FOUND(404, "Not found"),
    METHOD_NOT_ALLOWED(405, "Method not allowed"), NOT_ACCEPTABLE(406, "Not acceptable media type"), REQUEST_TIMEOUT(408, "Request timeout"), CONFLICT(409, "Request Conflict"),
    REQUEST_ENTITY_TOO_LARGE(413, "Request entity too large"), UNSUPPORTED_MEDIA_TYPE(415, "Unsupported media type"), UNPROCESSABLE_ENTITY(422, "Unprocessable Entity"), FAIL(500, "Server error"), BAD_GATEWAY(502, "Bad gateway"), SERVICE_UNAVAILABLE(503, "Service unavailable"),
    GATEWAY_TIMEOUT(504, "Gateway Timeout");

    private final String description;
    private int code;

    Status(int code, String description) {
        this.description = description;
        this.code = code;
    }

    /**
     * Returns the description for the status.
     *
     * @return description of given enum
     */
    public String getDescription() {
        return description;
    }

    /**
     * Returns the code for the status.
     *
     * @return code for given enum
     */
    public int getCode() {
        return code;
    }

}