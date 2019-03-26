package com.uttpal.schedular.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * @author Uttpal
 */
@ToString
@Getter
@Setter
public class Delivery {

    private String topic;
    private String webHookUrl;

    public Delivery(String topic, String webHookUrl) {
        this.topic = topic;
        this.webHookUrl = webHookUrl;
    }

    public Delivery() {
    }
}
