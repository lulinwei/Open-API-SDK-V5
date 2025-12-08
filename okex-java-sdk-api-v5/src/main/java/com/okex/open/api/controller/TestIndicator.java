package com.okex.open.api.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.okex.open.api.config.APIConfiguration;
import com.okex.open.api.controller.response.CandlesticksResponse;
import com.okex.open.api.enums.I18nEnum;
import com.okex.open.api.service.marketData.impl.MarketDataAPIServiceImpl;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
public class TestIndicator {

    public String instId = "ETH-USDT-SWAP";
    public APIConfiguration config() {
        APIConfiguration config = new APIConfiguration();

        //传入https://www.okx.com 或 https://aws.okx.com
        //you can set the domain as https://www.okx.com or https://aws.okx.com
        config.setDomain("https://www.okx.com");

//        config.setApiKey("b7959169-9e2a-44c7-bcee-84944cb8b850");
//        config.setSecretKey("779E65D4653FAE85E620AE1E7CA2F8B4");
//        //请求模拟盘的接口需要传入1，否则传入0
//        //if you want to request the endpoint in demo trading,please input 1,otherwise,please input 0
//        config.setxSimulatedTrading("1");

        config.setApiKey("2598faab-8351-4e0d-8fd3-d91ea92bb693");
        config.setSecretKey("5E2CA393CF4466489F2FAFAF77FCD3F2");
        //请求模拟盘的接口需要传入1，否则传入0
        //if you want to request the endpoint in demo trading,please input 1,otherwise,please input 0
        config.setxSimulatedTrading("0");

        config.setPassphrase("Ropeok@123");

        config.setPrint(false);
        /* config.setI18n(I18nEnum.SIMPLIFIED_CHINESE);*/
        config.setI18n(I18nEnum.ENGLISH);
        return config;
    }

    public static void main(String[] args) {
        System.setProperty("proxyHost", "127.0.0.1");
        System.setProperty("proxyPort", "10809");
        System.out.println(System.currentTimeMillis());
        MarketDataAPIServiceImpl marketDataAPIService = new MarketDataAPIServiceImpl(new TestIndicator().config());
        JSONObject candlesticks =marketDataAPIService.getCandlesticks("ETH-USDT-SWAP", null, null, "5m", "100");
        CandlesticksResponse candlesticksResponse = JSON.toJavaObject(candlesticks, CandlesticksResponse.class);



        List<List<String>> ca = candlesticksResponse.getData();
        List<List<String>> sortedCandlesticks = ca.stream().sorted((c1, c2) -> Long.compare(Long.parseLong(c1.get(0)), Long.parseLong(c2.get(0))))
                .collect(Collectors.toList());
        List<Double> prices = sortedCandlesticks.stream().map(candlestick -> Double.parseDouble(candlestick.get(4))).collect(Collectors.toList());
        IndicatorTool indicatorTool = new IndicatorTool();
        double[] pricesArray = prices.stream().mapToDouble(Double::doubleValue).toArray();
        double rsi = indicatorTool.rsi(pricesArray);
        BollingerBands bollingerBands = indicatorTool.calBoll(pricesArray);
        double[] lowerBand = bollingerBands.getLowerBand();
        log.info("下轨：{}", lowerBand[lowerBand.length - 1]);
        double[] middleBand = bollingerBands.getMiddleBand();
        double[] upperBand = bollingerBands.getUpperBand();
        log.info("中轨：{}", middleBand[middleBand.length - 1]);
        log.info("上轨：{}", upperBand[upperBand.length - 1]);
        log.info("RSI:{}", rsi);
    }
}
