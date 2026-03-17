package com.okex.open.api.controller.response;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor
@Data
public class InstrumentsResponse {


    private String code;
    private String msg;
    private List<DataDTO> data;

    @NoArgsConstructor
    @Data
    public static class DataDTO {
        private String alias;
        private String auctionEndTime;
        private String baseCcy;
        private String category;
        private String ctMult;
        private String ctType;
        private String ctVal;
        private String ctValCcy;
        private String contTdSwTime;
        private String expTime;
        private Boolean futureSettlement;
        private String groupId;
        private String instFamily;
        private String instId;
        private String instType;
        private String lever;
        private String listTime;
        private String lotSz;
        private String maxIcebergSz;
        private String maxLmtAmt;
        private String maxLmtSz;
        private String maxMktAmt;
        private String maxMktSz;
        private String maxStopSz;
        private String maxTriggerSz;
        private String maxTwapSz;
        private String minSz;
        private String optType;
        private String openType;
        private String preMktSwTime;
        private String quoteCcy;
        private List<String> tradeQuoteCcyList;
        private String settleCcy;
        private String state;
        private String ruleType;
        private String stk;
        private String tickSz;
        private String uly;
        private Integer instIdCode;
        private String instCategory;
        private List<UpcChgDTO> upcChg;

        @NoArgsConstructor
        @Data
        public static class UpcChgDTO {
            private String param;
            private String newValue;
            private String effTime;
        }
    }
}
