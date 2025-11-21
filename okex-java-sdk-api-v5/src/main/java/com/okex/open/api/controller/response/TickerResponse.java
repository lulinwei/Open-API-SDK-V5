package com.okex.open.api.controller.response;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor
@Data
public class TickerResponse {

    private String code;
    private String msg;
    private List<DataDTO> data;

    @NoArgsConstructor
    @Data
    public static class DataDTO {
        private String instType;
        private String instId;
        private String last;
        private String lastSz;
        private String askPx;
        private String askSz;
        private String bidPx;
        private String bidSz;
        private String open24h;
        private String high24h;
        private String low24h;
        private String volCcy24h;
        private String vol24h;
        private String ts;
        private String sodUtc0;
        private String sodUtc8;
    }
}
