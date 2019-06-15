package com.revolut.services.moneycontrol.app;

import java.util.concurrent.CountDownLatch;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Launcher for Http based app for Money Transfer
 */
public class Launcher {

    private static final Logger LOGGER = Logger.getLogger(Launcher.class.getName());

    public static void main(String[] args) throws InterruptedException {
        Application application = Application.getInstance();
        final CountDownLatch latch = new CountDownLatch(1);

        Runtime.getRuntime().addShutdownHook(new Thread("shutdown-hook") {
            @Override
            public void run() {
                try {
                    application.stopServer();
                } catch (Exception e) {
                    LOGGER.log(Level.SEVERE, "Exception encountered while shutting Down Server.");
                }
                latch.countDown();
            }
        });
        application.startServer();
        latch.await();
    }
}