package com.heroku.myapp.myorchestrator.consumers.common;

import com.heroku.myapp.commons.consumers.QueueConsumer;
import org.springframework.stereotype.Component;

@Component
public class ExceptionQueueConsumer extends QueueConsumer {

    public ExceptionQueueConsumer() {
        route().exception();
    }

    @Override
    public void configure() {
        from(ironmq().exception().kindString("in").consumeUri())
                .routeId(route().id())
                .to(ironmq().exception().kindString("out").postUri());
    }
}
