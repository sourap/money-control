package com.revolut.services.moneycontrol.server;

public class BasicEvent {
    BasicExchangeImpl exchange;

    protected BasicEvent(BasicExchangeImpl var1) {
        this.exchange = var1;
    }
}