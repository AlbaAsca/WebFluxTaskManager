package org.ascaso.webfluxtaskmanager.config.filter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

@Component
public class LoggingFilter implements WebFilter {

    private static final Logger logger = LoggerFactory.getLogger(LoggingFilter.class);

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        String requestPath = exchange.getRequest().getPath().value();
        String method = exchange.getRequest().getMethod().name();
        logger.info("Incoming request: {} {}", method, requestPath);

        return chain.filter(exchange);
    }
}
