package com.okex.open.api.controller.response;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor
@Data
public class CurrentSubpositionsResponse {


    private String code;
    private List<DataDTO> data;
    private String msg;

    @NoArgsConstructor
    @Data
    public static class DataDTO {
        private String algoId;
        private String ccy;
        private String instId;
        private String instType;
        private String lever;
        private String margin;
        private String markPx;
        private String mgnMode;
        private String openAvgPx;
        private String openOrdId;
        private String openTime;
        private String posSide;
        private String slOrdPx;
        private String slTriggerPx;
        private String subPos;
        private String subPosId;
        private String tpOrdPx;
        private String tpTriggerPx;
        private String uniqueCode;
        private String upl;
        private String uplRatio;
        private String availSubPos;
    }
}
