package com.okex.open.api.controller;

import lombok.Data;

@Data
public class OkexApiUser {
    String apiKey;
    String secretKey;
    String passPhrase;

    public OkexApiUser(String number, String number1, String number2) {
        this.apiKey = number;
        this.secretKey = number1;
        this.passPhrase = number2;
    }
}
