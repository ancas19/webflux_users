package com.ancas.reactive.ws.users.infrastructure.driven_adapters.database.config;

import org.h2.tools.Server;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.context.event.ContextClosedEvent;
import org.springframework.context.event.EventListener;

import java.sql.SQLException;

@Configuration
//@Profile({"dev","test"})
@Profile("!prod & !production")
public class H2ConsoleConfiguration {

    private final String WEB_PORT="8082";
    private Server webServer;

    @EventListener(ApplicationStartedEvent.class)
    public void start() throws SQLException {
        this.webServer = Server.createWebServer("-webPort", WEB_PORT).start();
    }

    @EventListener(ContextClosedEvent.class)
    public void stop() {
        if (this.webServer != null) {
            this.webServer.stop();
        }
    }
}
