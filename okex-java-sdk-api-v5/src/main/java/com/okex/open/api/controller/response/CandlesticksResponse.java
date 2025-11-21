package com.okex.open.api.controller.response;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor
@Data
public class CandlesticksResponse {

    private String code;
    private String msg;
    private List<List<String>> data;
}
