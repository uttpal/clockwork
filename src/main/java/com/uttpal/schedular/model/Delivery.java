package com.uttpal.schedular.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.validation.constraints.NotEmpty;

/**
 * @author Uttpal
 */
@ToString
@Getter
@Setter
public class Delivery {

    @NotEmpty
    private String topic;

    public Delivery(String topic) {
        this.topic = topic;
    }

    public Delivery() {
    }
}
