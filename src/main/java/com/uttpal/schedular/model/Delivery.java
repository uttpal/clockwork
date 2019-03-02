package com.uttpal.schedular.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

/**
 * @author Uttpal
 */
@ToString
@AllArgsConstructor
@Getter
public class Delivery {

    private String topic;
    private String webHookUrl;
}
