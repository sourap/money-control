package com.revolut.services.moneycontrol.model;

import javax.xml.bind.annotation.*;
import java.io.Serializable;

/**
 *Error Object to capture errors from a WebService processing
 */
@SuppressWarnings("restriction")
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(propOrder = {"description"})
@XmlRootElement(name = "Error")
public class Error implements Serializable {

    private static final long serialVersionUID = 1L;

    @XmlElement(name = "description")
    private String description;

    public Error(String description) {
        this.description = description;
    }

    public Error() {
        super();
    }


    public String getDescription() {
        return description;
    }

    @Override
    public String toString() {
        return "Error{" +
                "description='" + description + '\'' +
                '}';
    }
}