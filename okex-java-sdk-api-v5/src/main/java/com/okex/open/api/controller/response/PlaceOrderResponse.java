package com.okex.open.api.controller.response;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor
@Data
public class PlaceOrderResponse {


    private String inTime;
    private String msg;
    private String code;
    private List<DataDTO> data;
    private String outTime;

    @NoArgsConstructor
    @Data
    public static class DataDTO {
        private String clOrdId;
        private String ordId;
        private String sCode;
        private String sMsg;
        private String tag;
        private String ts;
    }
}
