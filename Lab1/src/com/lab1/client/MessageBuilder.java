package com.lab1.client;

public class MessageBuilder {
    private String type;
    private String domain;
    private String action;
    private String info;

    public MessageBuilder type(String type) {
        this.type = type;
        return this;
    }

    public MessageBuilder domain(String domain) {
        this.domain = domain;
        return this;
    }

    public MessageBuilder action(String action) {
        this.action = action;
        return this;
    }

    public MessageBuilder info(String info) {
        this.info = info;
        return this;
    }

    public String build() {
        StringBuilder message = new StringBuilder();
        message.append(type + "\n");
        message.append(domain != null ? domain + "\n" : "");
        message.append(action != null ? action + "\n" : "");
        message.append(info != null ? info + "\n" : "");
        message.append("\n");
        return message.toString();
    }
}
