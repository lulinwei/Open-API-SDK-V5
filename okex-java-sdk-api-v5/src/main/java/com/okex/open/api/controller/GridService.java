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

    String[] positions = {
            "0.02", "0.02", "0.03",
            "0.04", "0.05", "0.06",
            "0.07", "0.08", "0.09",
            "0.1", "0.11", "0.12",
            "0.13", "0.14", "0.15",
            "0.16", "0.17", "0.18",
            "0.19", "0.2", "0.21",
            "0.22", "0.23", "0.24",
            "0.25", "0.26", "0.27",
            "0.28", "0.29", "0.3"
    };

    double[] profits = {
            50, 50, 50,
            40, 40, 40,
            30, 30, 30,
            20, 20, 20,
            20, 20, 20,
            20, 20, 20,
            20, 20, 20,
            20, 20, 20,
            20, 20, 20,
            20, 20, 20
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
                Double bePx = Double.valueOf(position.getBePx());

                JSONObject currentSubpositions = copytradingAPIService.currentSubpositions(instId, null, null, null, null, null, null);

                CurrentSubpositionsResponse currentSubpositionsResponse = JSON.toJavaObject(currentSubpositions, CurrentSubpositionsResponse.class);

                List<CurrentSubpositionsResponse.DataDTO> currentSubpositionsResponseData = currentSubpositionsResponse.getData();
//            log.info("明细仓位信息：{}", JSON.toJSONString(currentSubpositionsResponseData));
                log.info("整体盈利平仓间隔：{}", profits[currentSubpositionsResponseData.size()]);
                // 添加边界检查
// 添加边界检查
                int size = currentSubpositionsResponseData.size();
                log.info("带单数量：{}",size);
                if (size >= profits.length) {
                    size = profits.length - 1;
                }
                log.info("整体当前价格跟盈亏价格比较 :{}", currentPrice - bePx);
//            if (bePx - currentPrice > profits[currentSubpositionsResponseData.size()] && Double.valueOf(position.getUpl()) + Double.valueOf(position.getRealizedPnl()) > 10) {
                if (currentPrice - bePx >= profits[size]) {
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
                    // Using Java 8 streams
                    Optional<Double> minPriceOptional = currentSubpositionsResponseData.stream()
                            .filter(dataDTO -> dataDTO.getOpenAvgPx() != null && !dataDTO.getOpenAvgPx().isEmpty())
                            .map(dataDTO -> Double.parseDouble(dataDTO.getOpenAvgPx()))
                            .min(Double::compare);

                    double minPrice = minPriceOptional.orElse(0.0);

                    JSONObject candlesticks = this.marketDataAPIService.getCandlesticks(instId, null, null, "1m", "100");
                    CandlesticksResponse candlesticksResponse = JSON.toJavaObject(candlesticks, CandlesticksResponse.class);
                    List<List<String>> ca = candlesticksResponse.getData();
                    List<Double> prices = ca.stream().map(candlestick -> Double.parseDouble(candlestick.get(4))).collect(Collectors.toList());
                    IndicatorTool indicatorTool = new IndicatorTool();
                    double[] pricesArray = prices.stream().mapToDouble(Double::doubleValue).toArray();
                    boolean macdGoldenCross = indicatorTool.isMACDGoldenCross(pricesArray);
                    log.info("当前带单 minPrice :{}   MACD金叉：{}", minPrice, macdGoldenCross);
                    //获取当前价格低于订单价格 50补仓
                    if (currentPrice < minPrice) {
                        log.info("监测是否加仓中。。。。。最小价格跟当前价格相差：{}",minPrice - currentPrice);
                        if (minPrice - currentPrice > 50 && macdGoldenCross && ("").equals(liqPxStr)) {
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
                            log.info("当前价格高于订单价格 :{}", currentPrice - Double.parseDouble(openAvgPx));
                            //获取当前价格高于订单价格 50平仓
                            if (currentPrice - Double.parseDouble(openAvgPx) >= 50) {
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
            } else if (filteredPositions.size() == 0) {
                JSONObject candlesticks = this.marketDataAPIService.getCandlesticks(instId, null, null, "5m", "100");
                CandlesticksResponse candlesticksResponse = JSON.toJavaObject(candlesticks, CandlesticksResponse.class);
                List<List<String>> ca = candlesticksResponse.getData();
                List<Double> prices = ca.stream().map(candlestick -> Double.parseDouble(candlestick.get(4))).collect(Collectors.toList());
                IndicatorTool indicatorTool = new IndicatorTool();
                double[] pricesArray = prices.stream().mapToDouble(Double::doubleValue).toArray();
                double rsi = indicatorTool.rsi(pricesArray);
                log.info("RSI:{}", rsi);
                if (rsi < 40) {
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
