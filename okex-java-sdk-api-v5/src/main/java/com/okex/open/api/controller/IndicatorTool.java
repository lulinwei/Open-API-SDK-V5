package com.okex.open.api.controller;

import com.tictactec.ta.lib.Core;
import com.tictactec.ta.lib.MInteger;//package com.okex.open.api.controller;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

//
//import com.tictactec.ta.lib.Core;
//import com.tictactec.ta.lib.MAType;
//import com.tictactec.ta.lib.MInteger;
//import com.tictactec.ta.lib.RetCode;
//import io.gate.gateapi.models.FuturesCandlestick;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.stereotype.Component;
//
//import java.math.BigDecimal;
//import java.util.ArrayList;
//import java.util.Arrays;
//import java.util.Collections;
//import java.util.List;
//
@Slf4j
@Component
public class IndicatorTool {
//
//
//    public int closePriceIndex = 0;
//    public double[] sma(List<FuturesCandlestick>  futuresCandlesticks, int period) {
//
//        Core talib = new Core();
//        // Prepare input array for close prices
//        double[] closePrices = futuresCandlesticks.stream().mapToDouble(bar -> Double.parseDouble(bar.getC())).toArray();
//
//        // 分配结果数组（大小应该与输入数据相同或更大）
//        double[] smaValues = new double[closePrices.length];
//        MInteger begin = new MInteger();
//        MInteger length = new MInteger();
//
//        RetCode retCode = talib.sma(0, closePrices.length - 1, closePrices, period, begin, length, smaValues);
//
//        if (retCode == RetCode.Success) {
////            log.info("SMA calculated successfully for period: {}", period);
////            log.info("Begin: {}, Length: {}", begin.value, length.value);
//            // Now do something with the SMA values...
////            for (int i = begin.value; i < length.value; i++) {
////                log.info("smaValues[{}] = {}", i, smaValues[i]);
////            }
//            // 返回计算得到的 SMA 值数组
//            double[] result = new double[length.value];
//            System.arraycopy(smaValues, 0, result, 0, length.value);
//            return result;
//        } else {
//            log.error("Failed to calculate SMA for period {}: {}", period, retCode);
//            return new double[0]; // 返回空数组或抛出异常
//        }
//    }
//    public BollingerBands calBoll(List<FuturesCandlestick> futuresCandlesticks) {
//        Core talib = new Core();
//
//        // Prepare input array for close prices
//        double[] closePrices = futuresCandlesticks.stream()
//                .mapToDouble(bar -> Double.valueOf(bar.getC()))
//                .toArray();
//
//        // Output arrays for upper band, middle band, and lower band
//        double[] upperBand = new double[closePrices.length];
//        double[] middleBand = new double[closePrices.length];
//        double[] lowerBand = new double[closePrices.length];
//
//        // Calculate Bollinger Bands
//        int timePeriod = 20;
//        double nbDevUp = 2.0;
//        double nbDevDn = 2.0;
//        MAType maType = MAType.Sma;
//
//        MInteger begin = new MInteger();
//        MInteger length = new MInteger();
//
//        RetCode retCode = talib.bbands(0, closePrices.length - 1, closePrices,
//                timePeriod, nbDevUp, nbDevDn, maType, begin, length,
//                upperBand, middleBand, lowerBand);
//
//        if (retCode == RetCode.Success) {
////            log.info("Bollinger Bands calculated successfully.");
//            // 截取有效部分（从begin到length）
//            int validLength = length.value;
//            double[] validUpper = new double[validLength];
//            double[] validMiddle = new double[validLength];
//            double[] validLower = new double[validLength];
//
//            System.arraycopy(upperBand, 0, validUpper, 0, validLength);
//            System.arraycopy(middleBand,0, validMiddle, 0, validLength);
//            System.arraycopy(lowerBand,0, validLower, 0, validLength);
//
//            return new BollingerBands(validUpper, validMiddle, validLower);
//        } else {
//            log.error("Failed to calculate Bollinger Bands: {}", retCode);
//            return new BollingerBands(new double[0], new double[0], new double[0]); // 返回空结果避免 null
//        }
//    }
//    //增加一个计算atr的方法
//    // 增加一个计算atr的方法
//    public double[] atr(List<FuturesCandlestick> futuresCandlesticks, int period) {
//        if (futuresCandlesticks == null || futuresCandlesticks.size() < period + 1) {
//            return new double[0];
//        }
//
//        Core talib = new Core();
//
//        // Prepare input arrays for high, low and close prices
//        double[] highPrices = futuresCandlesticks.stream()
//                .mapToDouble(bar -> Double.valueOf(bar.getH()))
//                .toArray();
//        double[] lowPrices = futuresCandlesticks.stream()
//                .mapToDouble(bar -> Double.valueOf(bar.getL()))
//                .toArray();
//        double[] closePrices = futuresCandlesticks.stream()
//                .mapToDouble(bar -> Double.valueOf(bar.getC()))
//                .toArray();
//
//        // Output array for ATR values
//        double[] atrValues = new double[closePrices.length];
//        MInteger begin = new MInteger();
//        MInteger length = new MInteger();
//
//        // Calculate ATR using TA-Lib
//        RetCode retCode = talib.atr(0, closePrices.length - 1, highPrices, lowPrices, closePrices, period, begin, length, atrValues);
//
//        if (retCode == RetCode.Success) {
//            // Extract valid ATR values
//            double[] result = new double[length.value];
//            System.arraycopy(atrValues, 0, result, 0, length.value);
//            return result;
//        } else {
//            log.error("Failed to calculate ATR: {}", retCode);
//            return new double[0];
//        }
//    }
//
    public boolean isMACDGoldenCross(double[] closePrices) {
        Core talib = new Core();
        int lookback = talib.macdLookback(12, 26, 9);
//        if (closePriceIndex < lookback) return false;

        double[] outMACD = new double[closePrices.length-lookback];
        double[] outSignal = new double[closePrices.length-lookback];
        double[] outHist = new double[closePrices.length-lookback];
        MInteger begin = new MInteger();
        MInteger length = new MInteger();

        talib.macd(0, closePrices.length - 1, closePrices, 12, 26, 9, begin, length, outMACD, outSignal, outHist);

//        int macdEnd = begin.value + length.value - 1;
        int macdEnd = outMACD.length-1;

        if (macdEnd < 1) return false;
//        log.info("指标信息： macd:{}  signal:{} / macd:{}  signal:{}  ",outMACD[macdEnd - 1],outSignal[macdEnd - 1],outMACD[macdEnd],outSignal[macdEnd]);
        return outMACD[macdEnd - 1] < outSignal[macdEnd - 1] && outMACD[macdEnd] >= outSignal[macdEnd];
    }
//
//
//
//    public boolean isMACDDeathCross(double[] closePrices) {
//        Core talib = new Core();
//        int lookback = talib.macdLookback(12, 26, 9);
////        if (closePriceIndex < lookback) return false;
//
//        double[] outMACD = new double[closePrices.length-lookback];
//        double[] outSignal = new double[closePrices.length-lookback];
//        double[] outHist = new double[closePrices.length-lookback];
//        MInteger begin = new MInteger();
//        MInteger length = new MInteger();
//
//        talib.macd(0, closePrices.length - 1, closePrices, 12, 26, 9, begin, length, outMACD, outSignal, outHist);
//
////        int macdEnd = begin.value + length.value - 1;
//        int macdEnd = outMACD.length - 1;
//        if (macdEnd < 1) return false;
////        log.info("死叉 {}  {} {}  {}  ",outMACD[macdEnd - 1],outSignal[macdEnd - 1],outMACD[macdEnd],outSignal[macdEnd]);
//
//        return outMACD[macdEnd - 1] > outSignal[macdEnd - 1] && outMACD[macdEnd] <= outSignal[macdEnd];
//    }
//
//    public boolean isMACDDeathCross(List<FuturesCandlestick> futuresCandlesticks) {
//        double[] closePrices = futuresCandlesticks.stream().mapToDouble(bar -> Double.valueOf(bar.getC())).toArray();
//        Core talib = new Core();
//        int lookback = talib.macdLookback(12, 26, 9);
////        if (closePriceIndex < lookback) return false;
//
//        double[] outMACD = new double[closePrices.length-lookback];
//        double[] outSignal = new double[closePrices.length-lookback];
//        double[] outHist = new double[closePrices.length-lookback];
//        MInteger begin = new MInteger();
//        MInteger length = new MInteger();
//
//        talib.macd(0, closePrices.length - 1, closePrices, 12, 26, 9, begin, length, outMACD, outSignal, outHist);
//
////        int macdEnd = begin.value + length.value - 1;
//        int macdEnd = outMACD.length - 1;
//        if (macdEnd < 1) return false;
//        log.info("死叉 {}  {} {}  {}  ",outMACD[macdEnd - 1],outSignal[macdEnd - 1],outMACD[macdEnd],outSignal[macdEnd]);
//
//        return outMACD[macdEnd - 1] > outSignal[macdEnd - 1] && outMACD[macdEnd] <= outSignal[macdEnd];
//    }
//    public boolean isMACDGoldenCross(List<FuturesCandlestick> futuresCandlesticks) {
//        double[] closePrices = futuresCandlesticks.stream().mapToDouble(bar -> Double.valueOf(bar.getC())).toArray();
//        Core talib = new Core();
//        int lookback = talib.macdLookback(12, 26, 9);
////        if (closePriceIndex < lookback) return false;
//
//        double[] outMACD = new double[closePrices.length-lookback];
//        double[] outSignal = new double[closePrices.length-lookback];
//        double[] outHist = new double[closePrices.length-lookback];
//        MInteger begin = new MInteger();
//        MInteger length = new MInteger();
//
//        talib.macd(0, closePrices.length - 1, closePrices, 12, 26, 9, begin, length, outMACD, outSignal, outHist);
//
////        int macdEnd = begin.value + length.value - 1;
//        int macdEnd = outMACD.length-1;
//
//        if (macdEnd < 1) return false;
//        log.info("指标信息： macd:{}  signal:{} / macd:{}  signal:{}  ",outMACD[macdEnd - 1],outSignal[macdEnd - 1],outMACD[macdEnd],outSignal[macdEnd]);
//        return outMACD[macdEnd - 1] < outSignal[macdEnd - 1] && outMACD[macdEnd] >= outSignal[macdEnd];
//    }
//
//    public boolean isMACDGoldenCrossTest(List<FuturesCandlestick> futuresCandlesticks) {
//        double[] closePrices = futuresCandlesticks.stream().mapToDouble(bar -> Double.valueOf(bar.getC())).toArray();
//        Core talib = new Core();
//        int lookback = talib.macdLookback(12, 26, 9);
////        if (closePriceIndex < lookback) return false;
//
//        double[] outMACD = new double[closePrices.length-lookback];
//        double[] outSignal = new double[closePrices.length-lookback];
//        double[] outHist = new double[closePrices.length-lookback];
//        MInteger begin = new MInteger();
//        MInteger length = new MInteger();
//
//        talib.macd(0, closePrices.length - 1, closePrices, 12, 26, 9, begin, length, outMACD, outSignal, outHist);
//
////        int macdEnd = begin.value + length.value - 1;
//        int macdEnd = outMACD.length-1;
//
//        if (macdEnd < 1) return false;
//
//        for (int i = 0; i < outMACD.length; i++) {
//            System.out.println(i+"  "+i+"  "+outMACD[i] + "  " + outSignal[i]);
//            if(i>0){
//                boolean jincha = outMACD[i - 1] < outSignal[i - 1] && outMACD[i] >= outSignal[i];
//
//                boolean sicha = outMACD[macdEnd - 1] > outSignal[macdEnd - 1] && outMACD[macdEnd] <= outSignal[macdEnd];
//
//                System.out.println(jincha  +"   "+ sicha);
//            }
//
//
//
//        }
//        return outMACD[macdEnd - 1] < outSignal[macdEnd - 1] && outMACD[macdEnd] >= outSignal[macdEnd];
//    }
//
//    public double rsi(List<FuturesCandlestick> futuresCandlesticks) {
//        double[] closePrices=futuresCandlesticks.stream().mapToDouble(bar -> Double.valueOf(bar.getC())).toArray();
//        Core talib = new Core();
//        double[] outRSI = new double[closePrices.length];
//        MInteger begin = new MInteger();
//        MInteger length = new MInteger();
//        talib.rsi(0, closePrices.length - 1, closePrices, 14, begin, length, outRSI);
//        return outRSI[length.value - 1];
//    }
//
//    public boolean macd(double[] closePrices) {
//        Core talib = new Core();
//        int lookback = talib.macdLookback(12, 26, 9);
////        if (closePriceIndex < lookback) return false;
//
//        double[] outMACD = new double[closePrices.length-lookback];
//        double[] outSignal = new double[closePrices.length-lookback];
//        double[] outHist = new double[closePrices.length-lookback];
//        MInteger begin = new MInteger();
//        MInteger length = new MInteger();
//
//        talib.macd(0, closePrices.length - 1, closePrices, 12, 26, 9, begin, length, outMACD, outSignal, outHist);
//
////        int macdEnd = begin.value + length.value - 1;
//        int macdEnd = outMACD.length-1;
//
//        if (macdEnd < 1) return false;
////        log.info("指标信息： macd:{}  signal:{} / macd:{}  signal:{}  ",outMACD[macdEnd - 1],outSignal[macdEnd - 1],outMACD[macdEnd],outSignal[macdEnd]);
//        return outMACD[macdEnd - 1] < outSignal[macdEnd - 1] && outMACD[macdEnd] >= outSignal[macdEnd];
//    }
//
//
//    public double cci(List<FuturesCandlestick> futuresCandlesticks) {
//
//        if (futuresCandlesticks.size() < 14) {
//            System.out.println("Not enough data to calculate CCI.");
////            return;
//        }
//
//        Core talib = new Core();
//
//        MInteger begin = new MInteger();
//        MInteger length = new MInteger();
//
//        // Prepare input arrays for high, low and close prices
//        double[] highPrices = futuresCandlesticks.stream().mapToDouble(bar -> Double.valueOf(bar.getH())).toArray();
//        double[] lowPrices = futuresCandlesticks.stream().mapToDouble(bar -> Double.valueOf(bar.getL())).toArray();
//        double[] closePrices = futuresCandlesticks.stream().mapToDouble(bar -> Double.valueOf(bar.getC())).toArray();
//
//        // Output array for CCI values
//        double[] cciValues = new double[closePrices.length];
//
//        int timePeriod = 14; // Common period used for CCI
//
//        // Calculate CCI using TA-Lib
//        RetCode retCode = talib.cci(0, closePrices.length - 1, highPrices, lowPrices, closePrices, timePeriod, begin, length, cciValues);
//
//        if (retCode == RetCode.Success) {
//            System.out.println("CCI calculated successfully.");
//            // Now do something with the CCI values...
//            for (int i = begin.value; i <  length.value; i++) {
//                System.out.println("CCI[" + i + "] = " + cciValues[i]);
////                System.out.println("time " + gateTool2.doubleToTimeStr(futuresCandlesticks.get(i).getT()));
//            }
//        } else {
//            System.err.println("Failed to calculate CCI: " + retCode);
//        }
//
//
//        return cciValues[length.value - 1];
//    }
//
//
//    public void sma(List<FuturesCandlestick> futuresCandlesticks){
//
////        List<FuturesCandlestick> futuresCandlesticks = gateTool2.futuresCandlesticks("ETH_USDT", "5m");
//
//        /**
//         * The total number of periods to generate data for.
//         */
//         int TOTAL_PERIODS = 100; //1 .数组长度100
//
//        /**
//         * The number of periods to average together.
//         */
//
//
//        // Prepare input array for close prices
//        double[] closePrice = futuresCandlesticks.stream().mapToDouble(bar -> Double.valueOf(bar.getC())).toArray();
//
////        double[] closePrice = new double[TOTAL_PERIODS];
//        double[] out = new double[closePrice.length];
//        MInteger begin = new MInteger();
//        MInteger length = new MInteger();
//
//        int PERIODS_AVERAGE = 30;  //2. MA移动周期窗口,这里30代表30日均线
//        Core c = new Core();  //下面这个是获取Core实例执行sma(移动平均线)函数 , 一共7个参数 , 这里可以不用管, 因为前面已经定义了
//        RetCode retCode = c.sma(0, closePrice.length - 1, closePrice, PERIODS_AVERAGE, begin, length, out);
//        //说明,第一个是开始下标,结束的下标, 外界输入的数组样本 , 然后是移动平均周期 , 接着是有效数据开始于 , 有效数据长度 ,输出的样本数组　　　　　　　　　　
//        //注意, 结果存放在out数组中, 但是有效数字是从begin.value开始的, 也就是前几个数字, 是无效, 所以循环并不是从index 0开始
//        if (retCode == RetCode.Success) {
//            System.out.println("Output Start Period: " + begin.value);
//            System.out.println("Output End Period: " + (begin.value + length.value - 1));
//
//            for (int i = begin.value; i < begin.value + length.value; i++) {
//                StringBuilder line = new StringBuilder();
//                line.append("Period #");
//                line.append(i);
//                line.append(" close=");
//                line.append(closePrice[i]);
//                line.append(" mov_avg=");
//                line.append(out[i - begin.value]);
//                System.out.println(line.toString());
//            }
//        }
//        else {
//            System.out.println("Error");
//        }
//
//    }
//
//    /**
//     * 获取kdj(使用默认的9,3,3)
//     *
//     * @return
//     */
//    public static List<double[]> kdj(List<FuturesCandlestick> futuresCandlesticks ) {
//
//// 最高
//        double[] inHigh = futuresCandlesticks.stream().mapToDouble(bar -> Double.valueOf(bar.getH())).toArray();
//        // 最低
//        double[] inLow = futuresCandlesticks.stream().mapToDouble(bar -> Double.valueOf(bar.getL())).toArray();
//        // 收盘
//        double[] inClose = futuresCandlesticks.stream().mapToDouble(bar -> Double.valueOf(bar.getC())).toArray();
//
//
//
//        // 输出的k（最后16位没有值）
//        double[] k = new double[futuresCandlesticks.size() - 16];
//        // 输出的d（最后16位没有值）
//        double[] d = new double[futuresCandlesticks.size() - 16];
//        // 手动计算j值(3*k - 2*d)
//        double[] j = new double[futuresCandlesticks.size() - 16];
//
//
//        List<double[]> kdjList = null;
//
//        Core core = new Core();
//
//        // kd 直接截取
//        RetCode code = core.stoch(0, inHigh.length - 1, inHigh, inLow, inClose, 9, 5, MAType.Ema, 5, MAType.Ema, new MInteger(), new MInteger(), k, d);
//
//        if (code == RetCode.Success) {
//            // 计算j值(保留2位)
//            for (int i = 0; i < d.length; i++) {
//                BigDecimal b1 = new BigDecimal(k[i]).multiply(new BigDecimal(3));
//                BigDecimal b2 = new BigDecimal(d[i]).multiply(new BigDecimal(2));
//                j[i] = b1.subtract(b2).setScale(2, BigDecimal.ROUND_DOWN).doubleValue();
//            }
//
//            // 计算k值(保留2位)
//            k = Arrays.stream(k)
//                    .map(e -> new BigDecimal(e)
//                            .setScale(2, BigDecimal.ROUND_DOWN).doubleValue())
//                    .toArray();
//            // 计算d值(保留2位)
//            d = Arrays.stream(d)
//                    .map(e -> new BigDecimal(e)
//                            .setScale(2, BigDecimal.ROUND_DOWN).doubleValue())
//                    .toArray();
//
//            kdjList = new ArrayList();
//            kdjList.add(k);
//            kdjList.add(d);
//            kdjList.add(j);
//        }
//        return kdjList == null ? Collections.emptyList() : kdjList;
//    }
//    public void boll(List<FuturesCandlestick> futuresCandlesticks) {
//        Core talib = new Core();
//
//        // Prepare input array for close prices
//        double[] closePrices = futuresCandlesticks.stream().mapToDouble(bar -> Double.valueOf(bar.getC())).toArray();
//
//        // Output arrays for upper band, middle band, and lower band
//        double[] upperBand = new double[closePrices.length];
//        double[] middleBand = new double[closePrices.length];
//        double[] lowerBand = new double[closePrices.length];
//
//        // Calculate Bollinger Bands
//        int timePeriod = 20;
//        double nbDevUp = 2.0;
//        double nbDevDn = 2.0;
//        MAType maType = MAType.Sma;
//
//
//        MInteger begin = new MInteger();
//        MInteger length = new MInteger();
//
//        RetCode retCode = talib.bbands(0, closePrices.length - 1, closePrices, timePeriod, nbDevUp, nbDevDn, maType, begin, length, upperBand, middleBand, lowerBand);
//
//        if (retCode == RetCode.Success) {
//            System.out.println("Bollinger Bands calculated successfully.");
//            // Now do something with the Bollinger Bands values...
//            for (int i = begin.value; i < length.value; i++) {
//                System.out.println("Upper Band[" + i + "] = " + upperBand[i]);
//                System.out.println("Middle Band[" + i + "] = " + middleBand[i]);
//                System.out.println("Lower Band[" + i + "] = " + lowerBand[i]);
//            }
//        } else {
//            System.err.println("Failed to calculate Bollinger Bands: " + retCode);
//        }
//    }
//
//
//    public static void main(String[] args) {
//
//
////        GateTool gateTool=new GateTool();
//        GateTool gateUtils=new GateTool("2","3","3");
//        IndicatorTool indicatorTool =new IndicatorTool();
////        double[] close = new double[100];
//
////        List<Double> eth_usdt = gateTool.getClose("ETH_USDT","5m");
////        for (int i = 0; i < eth_usdt.size(); i++) {
////            close[i]=eth_usdt.get(i);
////        }
////        List<FuturesCandlestick> futuresCandlesticks = gateTool.futuresCandlesticks("ETH_USDT","5m");
//
////        taLibTool.cci(futuresCandlesticks);
////        indicatorTool.rsi(futuresCandlesticks);
//
////        System.out.println(taLibTool.isMACDGoldenCrossTest(futuresCandlesticks));
////        System.out.println(taLibTool.isMACDDeathCross(futuresCandlesticks));
//
//
//
////        System.out.println(taLibTool.calculateCci(futuresCandlesticks));
////        taLibTool.boll(futuresCandlesticks);
//
////        System.out.println(taLibTool.isMACDGoldenCross(close));
//    }
}
