package com.mystere.mercadopago.controller;

import com.fasterxml.jackson.annotation.JsonProperty;

public class PreferenceResponse {

    private String id;

    @JsonProperty("initPoint")
    private String initPoint;

    public PreferenceResponse(String id, String initPoint) {
        this.id = id;
        this.initPoint = initPoint;
    }

    public String getId() {
        return id;
    }

    public String getInitPoint() {
        return initPoint;
    }
}
