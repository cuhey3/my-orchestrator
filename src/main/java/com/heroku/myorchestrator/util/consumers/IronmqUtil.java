package com.heroku.myorchestrator.util.consumers;

import com.heroku.myorchestrator.config.enumerate.Kind;
import com.heroku.myorchestrator.config.enumerate.QueueType;
import org.apache.camel.Exchange;
import org.apache.camel.Expression;

public class IronmqUtil {

    private static final String IRONMQ_CLIENT_BEAN_NAME = "myironmq";

    public static Expression affectQueueUri() {
        return new Expression() {
            @Override
            public <T> T evaluate(Exchange exchange, Class<T> type) {
                String kindString = exchange.getIn().getBody(String.class);
                Kind kind = Kind.valueOf(kindString);
                exchange.getIn()
                        .setBody(new KindUtil().kind(kind).preMessage());
                return (T) String.format("ironmq:%s?client=%s",
                        "snapshot_" + kindString, IRONMQ_CLIENT_BEAN_NAME);
            }
        };
    }

    private String type, kind;
    private int timeout;

    public IronmqUtil() {
        this.timeout = 60;
    }

    public IronmqUtil type(QueueType type) {
        this.type = type.expression();
        return this;
    }

    public IronmqUtil kind(Kind kind) {
        this.kind = kind.expression();
        return this;
    }

    public IronmqUtil timeout(int timeout) {
        this.timeout = timeout;
        return this;
    }

    public IronmqUtil snapshot() {
        this.type = QueueType.SNAPSHOT.expression();
        return this;
    }

    public IronmqUtil diff() {
        this.type = QueueType.DIFF.expression();
        return this;
    }

    public IronmqUtil completion() {
        this.type = QueueType.COMPLETION.expression();
        this.kind = "all";
        return this;
    }

    public IronmqUtil changed() {
        this.type = QueueType.CHANGED.expression();
        this.kind = "all";
        return this;
    }

    public String consumeUri() {
        return String.format("ironmq:%s"
                + "?client=%s"
                + "&timeout=%s"
                + "&maxMessagesPerPoll=100",
                type + "_" + kind, IRONMQ_CLIENT_BEAN_NAME, timeout);
    }

    public String postUri() {
        return String.format("ironmq:%s?client=%s",
                type + "_" + kind, IRONMQ_CLIENT_BEAN_NAME);
    }

}
