package com.okex.open.api.controller.response;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor
@Data
public class PositionsResponse {

    private String code;
    private List<DataDTO> data;
    private String msg;

    @NoArgsConstructor
    @Data
    public static class DataDTO {
        private String adl;
        private String availPos;
        private String avgPx;
        private String baseBal;
        private String baseBorrowed;
        private String baseInterest;
        private String bePx;
        private String bizRefId;
        private String bizRefType;
        private String cTime;
        private String ccy;
        private String clSpotInUseAmt;
        private List<?> closeOrderAlgo;
        private String deltaBS;
        private String deltaPA;
        private String fee;
        private String fundingFee;
        private String gammaBS;
        private String gammaPA;
        private String hedgedPos;
        private String idxPx;
        private String imr;
        private String instId;
        private String instType;
        private String interest;
        private String last;
        private String lever;
        private String liab;
        private String liabCcy;
        private String liqPenalty;
        private String liqPx;
        private String margin;
        private String markPx;
        private String maxSpotInUseAmt;
        private String mgnMode;
        private String mgnRatio;
        private String mmr;
        private String notionalUsd;
        private String optVal;
        private String pendingCloseOrdLiabVal;
        private String pnl;
        private String pos;
        private String posCcy;
        private String posId;
        private String posSide;
        private String quoteBal;
        private String quoteBorrowed;
        private String quoteInterest;
        private String realizedPnl;
        private String spotInUseAmt;
        private String spotInUseCcy;
        private String thetaBS;
        private String thetaPA;
        private String tradeId;
        private String uTime;
        private String upl;
        private String uplLastPx;
        private String uplRatio;
        private String uplRatioLastPx;
        private String usdPx;
        private String vegaBS;
        private String vegaPA;
        private String nonSettleAvgPx;
        private String settledPnl;
    }
}
