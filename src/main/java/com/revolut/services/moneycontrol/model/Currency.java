package com.revolut.services.moneycontrol.model;

public enum Currency {
    USD("USD"),
    GBP("GBP");

    private final String description;

    Currency(String description) {
        this.description = description;
    }

    /**
     * Returns the description for the currency.
     *
     * @return description of given enum
     */
    public String getDescription() {
        return description;
    }
}
