package com.okex.open.api.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.okex.open.api.bean.copytrading.param.CloseSubposition;
import com.okex.open.api.bean.trade.param.ClosePositions;
import com.okex.open.api.bean.trade.param.PlaceOrder;
import com.okex.open.api.config.APIConfiguration;
import com.okex.open.api.controller.response.*;
import com.okex.open.api.enums.I18nEnum;
import com.okex.open.api.service.account.AccountAPIService;
import com.okex.open.api.service.account.impl.AccountAPIServiceImpl;
import com.okex.open.api.service.copytrading.CopytradingAPIService;
import com.okex.open.api.service.copytrading.impl.CopytradingAPIServiceImpl;
import com.okex.open.api.service.marketData.MarketDataAPIService;
import com.okex.open.api.service.marketData.impl.MarketDataAPIServiceImpl;
import com.okex.open.api.service.trade.TradeAPIService;
import com.okex.open.api.service.trade.impl.TradeAPIServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

//http://localhost:8080/api/test/hello
@Service
@Slf4j
public class GridService {


    private CopytradingAPIService copytradingAPIService;
    public APIConfiguration config;
    TradeAPIService tradeAPIService;
    public MarketDataAPIService marketDataAPIService;


    public GridService() {
        this.config = this.config();
        this.copytradingAPIService = new CopytradingAPIServiceImpl(this.config);
        tradeAPIService = new TradeAPIServiceImpl(this.config);
        marketDataAPIService = new MarketDataAPIServiceImpl(config);
        this.accountAPIService = new AccountAPIServiceImpl(this.config);
    }

    public String instId = "ETH-USDT-SWAP";
    private AccountAPIService accountAPIService;

    public String getGrid() {


        JSONObject ticker = this.marketDataAPIService.getTicker(instId);
        TickerResponse tickerResponse = JSON.toJavaObject(ticker, TickerResponse.class);
        String last = tickerResponse.getData().get(0).getLast();
        double currentPrice = Double.parseDouble(last);

        log.info("当前价格：{}", currentPrice);

        JSONObject positions = this.accountAPIService.getPositions("SWAP", instId, null);
        PositionsResponse positionsResponse = JSON.toJavaObject(positions, PositionsResponse.class);
        List<PositionsResponse.DataDTO> data = positionsResponse.getData();
        if (data.size() > 0) {
            //整体盈利 一键平仓
            PositionsResponse.DataDTO position = data.get(0);
            if (Double.valueOf(position.getPnl()) > 10) {
                ClosePositions closePositions = new ClosePositions();
                closePositions.setInstId(instId);
                closePositions.setPosSide("long");
                closePositions.setMgnMode("cross");
                closePositions.setCcy("");
                closePositions.setClOrdId("");
                closePositions.setTag("");
                closePositions.setAutoCxl("false");
                JSONObject result = tradeAPIService.closePositions(closePositions);
                log.info("整体平仓结果：{}", JSON.toJSONString(result));
            } else {
                JSONObject currentSubpositions = copytradingAPIService.currentSubpositions(instId, null, null, null, null, null, null);

                CurrentSubpositionsResponse currentSubpositionsResponse = JSON.toJavaObject(currentSubpositions, CurrentSubpositionsResponse.class);

                List<CurrentSubpositionsResponse.DataDTO> currentSubpositionsResponseData = currentSubpositionsResponse.getData();

                // Using Java 8 streams
                Optional<Double> minPriceOptional = currentSubpositionsResponseData.stream()
                        .filter(dataDTO -> dataDTO.getOpenAvgPx() != null && !dataDTO.getOpenAvgPx().isEmpty())
                        .map(dataDTO -> Double.parseDouble(dataDTO.getOpenAvgPx()))
                        .min(Double::compare);

                double minPrice = minPriceOptional.orElse(0.0);

                JSONObject candlesticks = this.marketDataAPIService.getCandlesticks(instId, null, null, "1m", "100");
                CandlesticksResponse candlesticksResponse = JSON.toJavaObject(candlesticks, CandlesticksResponse.class);
                List<List<String>> ca = candlesticksResponse.getData();
                List<Double> prices = ca.stream()
                        .map(candlestick -> Double.parseDouble(candlestick.get(4)))
                        .collect(Collectors.toList());
                IndicatorTool indicatorTool = new IndicatorTool();
                double[] pricesArray = prices.stream()
                        .mapToDouble(Double::doubleValue)
                        .toArray();
                boolean macdGoldenCross = indicatorTool.isMACDGoldenCross(pricesArray);
                //获取当前价格低于订单价格 50开仓
                if (minPrice - currentPrice > 50 && macdGoldenCross) {
                    //求 currentSubpositionsResponseData的最低价格
                    PlaceOrder placeOrder = new PlaceOrder();
                    placeOrder.setInstId("ETH-USDT-SWAP");
                    placeOrder.setTdMode("cross");
//        placeOrder.setCcy("USDT");
                    placeOrder.setClOrdId("RK00003");
//        placeOrder.setTag("");
                    placeOrder.setSide("buy");
                    placeOrder.setPosSide("long");
//        placeOrder.setOrdType("limit");
                    placeOrder.setOrdType("market");
                    placeOrder.setSz("0.01");
                    placeOrder.setQuickMgnType("");

//        placeOrder.setPx("1500");
//        placeOrder.setReduceOnly(false);
//        placeOrder.setTgtCcy("");
//        placeOrder.setBanAmend(false);


                    JSONObject result = tradeAPIService.placeOrder(placeOrder);
                    PlaceOrderResponse placeOrder2 = JSON.toJavaObject(result, PlaceOrderResponse.class);
                    log.info("下单结果：{}", JSON.toJSONString(placeOrder2));
                } else {
                    for (int i = 0; i < currentSubpositionsResponseData.size(); i++) {
                        CurrentSubpositionsResponse.DataDTO dataDTO = currentSubpositionsResponseData.get(i);
                        String subPosId = dataDTO.getSubPosId();
                        String openAvgPx = dataDTO.getOpenAvgPx();

                        //获取当前价格高于订单价格 50平仓
                        if (currentPrice - Double.parseDouble(openAvgPx) > 50) {
                            CloseSubposition closeSubposition = new CloseSubposition();
                            closeSubposition.setSubPosId(subPosId);
                            closeSubposition.setTag("");
                            closeSubposition.setInstType("");
                            closeSubposition.setSubPosType("");
                            closeSubposition.setOrdType("");
                            closeSubposition.setPx("");
                            JSONObject jsonObject = copytradingAPIService.closeSubposition(closeSubposition);
                            log.info("平仓结果：{}", JSON.toJSONString(jsonObject));
                        }
                    }
                }

            }

        } else if (data.size() == 0) {
        }


        return "getGrid";
    }

    public void closePositions() {

        ClosePositions closePositions = new ClosePositions();
        closePositions.setInstId("BTC-USD-SWAP");
        closePositions.setPosSide("long");
        closePositions.setMgnMode("cross");
        closePositions.setCcy("");
        closePositions.setClOrdId("");
        closePositions.setTag("");
        closePositions.setAutoCxl("false");
        JSONObject result = tradeAPIService.closePositions(closePositions);


    }

    public APIConfiguration config() {
        APIConfiguration config = new APIConfiguration();

        //传入https://www.okx.com 或 https://aws.okx.com
        //you can set the domain as https://www.okx.com or https://aws.okx.com
        config.setDomain("https://www.okx.com");


        config.setApiKey("b7959169-9e2a-44c7-bcee-84944cb8b850");
        config.setSecretKey("779E65D4653FAE85E620AE1E7CA2F8B4");
        config.setPassphrase("Ropeok@123");

        //请求模拟盘的接口需要传入1，否则传入0
        //if you want to request the endpoint in demo trading,please input 1,otherwise,please input 0
//        config.setxSimulatedTrading("0");

        //请求模拟盘的接口需要传入1，否则传入0
        //if you want to request the endpoint in demo trading,please input 1,otherwise,please input 0
        config.setxSimulatedTrading("1");


        config.setPrint(true);
        /* config.setI18n(I18nEnum.SIMPLIFIED_CHINESE);*/
        config.setI18n(I18nEnum.ENGLISH);
        return config;
    }
}
