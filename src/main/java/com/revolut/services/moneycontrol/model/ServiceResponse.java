package com.revolut.services.moneycontrol.model;

import javax.xml.bind.annotation.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Service Response genric object to capture Response of a given Web Service.
 * @param <T> The Payload Object type
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ServiceResponse", propOrder = {"status", "errors",
        "payload"})
@XmlRootElement(name = "ServiceResponse")
public class ServiceResponse<T> implements Serializable {

    /**
     * class version ID for serialization
     */
    private static final long serialVersionUID = 1L;

    @XmlElement(name = "status", required = true)
    private Status status = Status.FAIL;

    @XmlElementWrapper(name = "errors")
    @XmlElement(name = "error")
    private List<Error> errors;

    private T payload;

    /**
     * no-arg constructor
     */
    public ServiceResponse() {
        super();
    }

    /**
     * @param payload given Payload Object
     */
    public ServiceResponse(T payload) {
        this.payload = payload;
    }

    /**
     * @return Payload Object
     */
    public T getPayload() {
        return payload;
    }

    /**
     * Sets the payload to ServiceResponse.
     *
     * @param payload given Payload Object
     * @return ServiceResponse
     */
    public ServiceResponse setPayload(T payload) {
        this.payload = payload;
        return this;
    }

    /**
     * @return List of Error
     */
    public List<Error> getErrors() {
        if (errors == null) {
            errors = new ArrayList<>();
        }
        return errors;
    }

    /**
     * Sets a list of errors to ServiceResponse.
     *
     * @param errors Errors List
     * @return ServiceResponse
     */
    public ServiceResponse setErrors(List<Error> errors) {
        getErrors().addAll(errors);
        return this;
    }

    /**
     * Adds an error instance to service response error list.
     *
     * @param error Error Object
     * @return ServiceResponse
     */
    public ServiceResponse addError(Error error) {
        getErrors().add(error);
        return this;
    }

    /**
     * Gets the status.
     *
     * @return Status Enum
     */
    public Status getStatus() {
        return status;
    }

    /**
     * Sets the status to ServiceResponse.
     *
     * @param status Status Enum
     * @return ServiceResponse
     */
    public ServiceResponse setStatus(Status status) {
        this.status = status;
        return this;
    }

    @Override
    public String toString() {
        return "ServiceResponse [status=" + status
                + ", errors=" + errors + ", payload=" + payload + "]";
    }

}
