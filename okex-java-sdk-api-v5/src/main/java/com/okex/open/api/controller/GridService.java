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

    String[] positionsOld = {
            "0.02", "0.02", "0.03",
            "0.04", "0.05", "0.06",
            "0.07", "0.08", "0.09",
            "0.1", "0.11", "0.12",
            "0.13", "0.14", "0.15",
            "0.16", "0.17", "0.18",
            "0.19", "0.2", "0.21",
            "0.22", "0.23", "0.24",
            "0.25", "0.25", "0.25",
            "0.25", "0.25", "0.25"
    };

    String[] positions = {
            "0.02", "0.04", "0.06",
            "0.08", "0.1", "0.12",
            "0.14", "0.16", "0.18",
            "0.2", "0.22", "0.24",
            "0.26", "0.28", "0.30",
            "0.32", "0.34", "0.36",
            "0.38", "0.40", "0.42"
    };

    double[] profits = {
            50, 50, 50,
            50, 50, 50,
            50, 50, 50,
            50, 50, 50,
            50, 50, 50,
            50, 50, 50,
            50, 50, 50,
            50, 50, 50,
            50, 50, 50,
            50, 50, 50
    };

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
        try {
            JSONObject ticker = this.marketDataAPIService.getTicker(instId);
            TickerResponse tickerResponse = JSON.toJavaObject(ticker, TickerResponse.class);
            String last = tickerResponse.getData().get(0).getLast();
            double currentPrice = Double.parseDouble(last);

            log.info("当前价格：{}", currentPrice);

            JSONObject positions = this.accountAPIService.getPositions("SWAP", instId, null);
            PositionsResponse positionsResponse = JSON.toJavaObject(positions, PositionsResponse.class);
            List<PositionsResponse.DataDTO> postions = positionsResponse.getData();
            List<PositionsResponse.DataDTO> filteredPositions = postions.stream()
                    .filter(position -> !"0".equals(position.getPos()))
                    .collect(Collectors.toList());

            if (filteredPositions.size() > 0) {
                //整体盈利 一键平仓
                PositionsResponse.DataDTO position = postions.get(0);
                log.info("整体汇总仓位信息：{}", JSON.toJSONString(position));
                String liqPxStr = position.getLiqPx();
                Double liqPx = Double.valueOf(("").equals(liqPxStr) ? "10000" : liqPxStr);
                Double bePxNum = Double.valueOf(position.getBePx());
//未实现收益
                String upl = position.getUpl();
                String realizedPnl = position.getRealizedPnl();
                String avgPx = position.getAvgPx();
                Double uplNum = Double.valueOf(upl);
                Double realizedPnlNum = Double.valueOf(realizedPnl);
                Double avgPxNum = Double.valueOf(avgPx);

                JSONObject currentSubpositions = copytradingAPIService.currentSubpositions(instId, null, null, null, null, null, null);

                CurrentSubpositionsResponse currentSubpositionsResponse = JSON.toJavaObject(currentSubpositions, CurrentSubpositionsResponse.class);

                List<CurrentSubpositionsResponse.DataDTO> currentSubpositionsResponseData = currentSubpositionsResponse.getData();
//            log.debug("明细仓位信息：{}", JSON.toJSONString(currentSubpositionsResponseData));

                // 添加边界检查
// 添加边界检查
                int size = currentSubpositionsResponseData.size();
                log.info("带单数量：{}", size);
                if (size >= profits.length) {
                    size = profits.length - 1;
                }
                log.info("整体整体盈亏情况 :{}  盈亏平衡价格：{}", uplNum + realizedPnlNum, bePxNum);
                double profiInterval = profits[currentSubpositionsResponseData.size()];
                log.info("整体盈利平仓间隔：{} 预计平仓价格：{}", profiInterval, profiInterval + bePxNum);
//            if (bePx - currentPrice > profits[currentSubpositionsResponseData.size()] && Double.valueOf(position.getUpl()) + Double.valueOf(position.getRealizedPnl()) > 10) {
//                if (uplNum + realizedPnlNum > 0) {
                if (false) {

                    if (uplNum + realizedPnlNum > 0 && uplNum + realizedPnlNum < 0.08) {
                        log.info("盈亏平衡价格核对。。。当前价格：{}。平衡价格：{}", currentPrice, bePxNum);
                    }
                    log.info("进入整体盈亏平衡中。。。当前价格：{}。平衡价格：{}", currentPrice, bePxNum);

                    if (currentPrice - bePxNum >= profits[size]) {
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
                    }

                } else {
                    // Using Java 8 streams
                    Optional<Double> minPriceOptional = currentSubpositionsResponseData.stream()
                            .filter(dataDTO -> dataDTO.getOpenAvgPx() != null && !dataDTO.getOpenAvgPx().isEmpty())
                            .map(dataDTO -> Double.parseDouble(dataDTO.getOpenAvgPx()))
                            .min(Double::compare);

                    double orderMinPrice = minPriceOptional.orElse(0.0);

                    JSONObject candlesticks = this.marketDataAPIService.getCandlesticks(instId, null, null, "1m", "100");
                    CandlesticksResponse candlesticksResponse = JSON.toJavaObject(candlesticks, CandlesticksResponse.class);
                    List<List<String>> ca = candlesticksResponse.getData();
                    List<Double> prices = ca.stream().map(candlestick -> Double.parseDouble(candlestick.get(4))).collect(Collectors.toList());
                    List<Double> pricesLow = ca.stream().map(candlestick -> Double.parseDouble(candlestick.get(4))).collect(Collectors.toList());
                    //求pricesLow的最低 价格
                    double minPriceLow = pricesLow.stream().min(Double::compare).orElse(0.0);
                    log.info("最近100跟k线最低价格：{} 回调：{}", minPriceLow,currentPrice-minPriceLow);
                    IndicatorTool indicatorTool = new IndicatorTool();
                    double[] pricesArray = prices.stream().mapToDouble(Double::doubleValue).toArray();
                    boolean macdGoldenCross = indicatorTool.isMACDGoldenCross(pricesArray);
                    log.info("当前带单 orderMinPrice :{}   MACD金叉：{}", orderMinPrice, macdGoldenCross);
                    //获取当前价格低于订单价格 50补仓
                    if (currentPrice < orderMinPrice) {
                        log.info("监测是否加仓中。。。。。预计补仓价格：{} 最小价格跟当前价格相差：{}", orderMinPrice - 50, orderMinPrice - currentPrice);
                        if (orderMinPrice - currentPrice > 50 && currentPrice-minPriceLow>15 && ("").equals(liqPxStr)) {
                            //求 currentSubpositionsResponseData的最低价格
                            PlaceOrder placeOrder = new PlaceOrder();
                            placeOrder.setInstId("ETH-USDT-SWAP");
                            placeOrder.setTdMode("cross");
//        placeOrder.setCcy("USDT");
//                    placeOrder.setClOrdId("RK00003");
                            // Replace the fixed ClOrdId with current timestamp
                            placeOrder.setClOrdId("RK" + System.currentTimeMillis());

//        placeOrder.setTag("");
                            placeOrder.setSide("buy");
                            placeOrder.setPosSide("long");
//        placeOrder.setOrdType("limit");
                            placeOrder.setOrdType("market");
//                        int size = currentSubpositionsResponseData.size();
                            if (size >= this.positions.length) {
                                size = this.positions.length - 1;
                            }
                            placeOrder.setSz(this.positions[size]);
                            placeOrder.setQuickMgnType("");

//        placeOrder.setPx("1500");
//        placeOrder.setReduceOnly(false);
//        placeOrder.setTgtCcy("");
//        placeOrder.setBanAmend(false);

                            JSONObject result = tradeAPIService.placeOrder(placeOrder);
                            PlaceOrderResponse placeOrder2 = JSON.toJavaObject(result, PlaceOrderResponse.class);
                            log.info("下单结果：{}", JSON.toJSONString(placeOrder2));
                        }

                    } else {
                        log.info("监测是否平仓中。。。。。");
                        //currentSubpositionsResponseData过滤掉大于当前价格的订单
                        // Filter positions where open average price is less than or equal to current price
                        List<CurrentSubpositionsResponse.DataDTO> currentSubpositionsResponseData2 = currentSubpositionsResponseData.stream()
                                .filter(dataDTO -> dataDTO.getOpenAvgPx() != null && !dataDTO.getOpenAvgPx().isEmpty())
                                .filter(dataDTO -> {
                                    double openAvgPx = Double.parseDouble(dataDTO.getOpenAvgPx());
                                    return openAvgPx <= currentPrice;
                                })
                                .collect(Collectors.toList());

                        for (int i = 0; i < currentSubpositionsResponseData2.size(); i++) {
                            CurrentSubpositionsResponse.DataDTO dataDTO = currentSubpositionsResponseData2.get(i);
                            log.info("明细仓位：{} 信息：{}", i + 1, JSON.toJSONString(dataDTO));
                            String subPosId = dataDTO.getSubPosId();
                            String openAvgPx = dataDTO.getOpenAvgPx();
                            String openTime = dataDTO.getOpenTime();
                            log.info("当前价格高于订单价格 :{}", currentPrice - Double.parseDouble(openAvgPx));
                            //List<List<String>> ca 取最高价格 并且时间大于openTime
                            List<Double> pricesFilter = ca.stream()
                                    .filter(candlestick -> Long.parseLong(candlestick.get(0)) > Long.parseLong(openTime))
                                    .map(candlestick -> Double.parseDouble(candlestick.get(4)))
                                    .collect(Collectors.toList());
                            if (!pricesFilter.isEmpty()) {
                                double maxPrice = prices.stream().max(Double::compareTo).get();
                                log.info("当前最高价格 :{}", maxPrice);
                                //获取当前价格高于订单价格 50平仓
                                if (currentPrice - Double.parseDouble(openAvgPx) >= 40) {
                                    log.info("追踪止盈中。。。。当前回测：{}", maxPrice - currentPrice);
                                    //当最高价格回测10 则平仓
                                    if (maxPrice - currentPrice >= 10) {
                                        CloseSubposition closeSubposition = new CloseSubposition();
                                        closeSubposition.setSubPosId(subPosId);
                                        closeSubposition.setTag("");
                                        closeSubposition.setInstType("");
//                                closeSubposition.setSubPosType("");
                                        closeSubposition.setOrdType("");
                                        closeSubposition.setPx("");
                                        JSONObject jsonObject = copytradingAPIService.closeSubposition(closeSubposition);
                                        log.info("平仓结果：{}", JSON.toJSONString(jsonObject));
                                    }

                                }

                            }

                        }
                    }
                }
            } else if (filteredPositions.size() == 0) {
                JSONObject candlesticks = this.marketDataAPIService.getCandlesticks(instId, null, null, "5m", "100");
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
                if (currentPrice < lowerBand[lowerBand.length - 1] && currentPrice < 3300) {
                    PlaceOrder placeOrder = new PlaceOrder();
                    placeOrder.setInstId("ETH-USDT-SWAP");
                    placeOrder.setTdMode("cross");
//        placeOrder.setCcy("USDT");
//                    placeOrder.setClOrdId("RK00003");
                    // Replace the fixed ClOrdId with current timestamp
                    placeOrder.setClOrdId("RK" + System.currentTimeMillis());

//        placeOrder.setTag("");
                    placeOrder.setSide("buy");
                    placeOrder.setPosSide("long");
//        placeOrder.setOrdType("limit");
                    placeOrder.setOrdType("market");
                    placeOrder.setSz(this.positions[0]);
                    placeOrder.setQuickMgnType("");

//        placeOrder.setPx("1500");
//        placeOrder.setReduceOnly(false);
//        placeOrder.setTgtCcy("");
//        placeOrder.setBanAmend(false);

                    JSONObject result = tradeAPIService.placeOrder(placeOrder);
                    PlaceOrderResponse placeOrder2 = JSON.toJavaObject(result, PlaceOrderResponse.class);
                    log.info("首单 下单结果：{}", JSON.toJSONString(placeOrder2));
                }
            }
        } catch (Exception e) {
            log.error("执行网格交易时发生错误: ", e);
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
}
