package com.aleiye.lassock.live.hill.source.snmp;

import com.aleiye.event.constants.EventKey;
import com.aleiye.event.factory.AleiyeParsedEventFactory;
import com.aleiye.lassock.api.Course;
import com.aleiye.lassock.api.CourseType;
import com.aleiye.lassock.api.SnmpPortStatisticalIndicators;
import com.aleiye.lassock.live.model.Mushroom;
import com.aleiye.lassock.live.model.MushroomBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.snmp4j.PDU;
import org.snmp4j.Snmp;
import org.snmp4j.Target;
import org.snmp4j.smi.VariableBinding;

import java.math.BigDecimal;
import java.util.*;

/**
 * Created by root on 2016/5/5.
 */
public class SnmpPortInfoSource extends SnmpStandardSource{

    private static final Logger LOGGER = LoggerFactory.getLogger(SnmpDriverBaseSource.class);

    // 上一次结果
    private Map<String, Long> tempOidval = new HashMap<String, Long>();

    //端口名称
    private String preNameOid;
    private String sufNameOid;

    //端口ip
    private String prefixOid;
    private String suffixOid;

    //接口接收到的字节数
    private String preInOid;
    private String sufInOid;

    //接口发出的字节数
    private String preOutOid;
    private String sufOutOid;

    //传输速率
    private String preSpeedOid;
    private String sufSpeedOid;

    //接收丢包的数目
    private String preInDiscardsOid;
    private String sufInDiscardsOid;

    //接收丢包的数目
    private String preOutDiscardsOid;
    private String sufOutDiscardsOid;

    //接收错误
    private String preInErrorOid;
    private String sufInErrorOid;

    //发送错误
    private String preOutErrorOid;
    private String sufOutErrorOid;

    //接收多点发送包
    private String preInNUCastOid;
    private String sufInNUCastOid;

    //发送多点发送包
    private String preOutNUCastOid;
    private String sufOutNUCastOid;

    //系统启动时间
    private String sysUpTimeOid;

    private long time = 0;

    private Map<String, Long> oid_value = new HashMap<String, Long>();

    private void portInfo(Snmp snmp, Target target) throws Exception {

        //监控时间
        VariableBinding sysUpTimeVariable = query(snmp, target, sysUpTimeOid, PDU.GET);
        Long nowTime = sysUpTimeVariable.getVariable().toLong();

        //端口名称
        Map<String, String> portNameMap = mapSwitch(query(snmp, target, preNameOid, sufNameOid));
        if (portNameMap.size() == 0) {
            LOGGER.warn("No such instances|objects target:" + target.getAddress() + " type:" + PDU.GETBULK + " OID:"
                    + preNameOid + "-" + sufNameOid);
            return;
        }

        // 查找端口-IP映射
        Map<String, String> portIpMap = portIpMap(snmp, target);

        Map<String, String> inByteMap = query(snmp, target, preInOid, sufInOid);

        Map<String, String> outByteMap = query(snmp, target, preOutOid, sufOutOid);

        //接口接收到的总字节数
        Map<String, Long> inMap = calculationDif(inByteMap);

        //in转化实体对象
        Map<String, FlowData> inFlowMap = tidyFlowData(inByteMap);

        //接口发出的总字节数
        Map<String, Long> outMap = calculationDif(outByteMap);

        //out转化实体对象
        Map<String, FlowData> outFlowMap = tidyFlowData(outByteMap);

        //接口传输速率
        Map<String, String> speedMap = mapSwitch(query(snmp, target, preSpeedOid, sufSpeedOid));

        //接口丢弃接收包的数目
        Map<String, Long> inDiscardsMap = calculationDif(query(snmp, target, preInDiscardsOid, sufInDiscardsOid));

        //接口丢弃发送包的数目
        Map<String, Long> outDiscardsMap = calculationDif(query(snmp, target, preOutDiscardsOid, sufOutDiscardsOid));

        //接口出错丢弃接收包的数目
        Map<String, Long> inErrorMap = calculationDif(query(snmp, target, preInErrorOid, sufInErrorOid));

        //接口出错丢弃发送包的数目
        Map<String, Long> outErrorMap = calculationDif(query(snmp, target, preOutErrorOid, sufOutErrorOid));

        //接口发送多点发送包数目
        Map<String, Long> inNUCastMap = calculationDif(query(snmp, target, preInNUCastOid, sufInNUCastOid));

        //接口接收多点发送包数目
        Map<String, Long> outNUCastMap = calculationDif(query(snmp, target, preOutNUCastOid, sufOutNUCastOid));


        if(time == 0 || oid_value.size() == 0){//把本次结果记录下来
            time = nowTime;
        }else{
            //间隔时间 秒
            long timeInterval = (nowTime - time) / 100;
            time = nowTime;
            Long curTime = System.currentTimeMillis();

            AleiyeParsedEventFactory.Builder factory = AleiyeParsedEventFactory.builder();

            for (Map.Entry<String, String> entry : portNameMap.entrySet()) {
                String port = entry.getKey();
                long speed = Long.parseLong(speedMap.get(port));

                String message;

                String driverIp = this.param.getHost();
                String driverName = this.param.getDriverName();
                String portName = entry.getValue();
                String portIp = portIpMap.get(port) != null ? portIpMap.get(port) : "";


                factory.addParsedField(SnmpPortStatisticalIndicators.DRIVER_IP.getName(), driverIp);
                factory.addParsedField(SnmpPortStatisticalIndicators.DRIVER_NAME.getName(), driverName != null ? driverName : "");
                factory.addParsedField(SnmpPortStatisticalIndicators.SYSUPTIME.getName(), timeInterval);
                factory.addParsedField(SnmpPortStatisticalIndicators.PORT_NAME.getName(), portName);
                factory.addParsedField(SnmpPortStatisticalIndicators.PORT_IP.getName(), portIp);
                factory.addParsedField(SnmpPortStatisticalIndicators.CURRENT_TIME.getName(), curTime);

                Long inValue = inMap.get(port);
                Long outValue = outMap.get(port);

                Double halfDuplexEthernet = new BigDecimal(calculationHalfDuplexEthernet(inValue, outValue, speed, timeInterval))
                        .setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
                Double fullDuplexEthernet = new BigDecimal(calculationFullDuplexEthernet(inValue, outValue, speed, timeInterval))
                        .setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
                Double portReceive = new BigDecimal(calculationPortRate(inValue, speed, timeInterval))
                        .setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
                Double portSend = new BigDecimal(calculationPortRate(outValue, speed, timeInterval))
                        .setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
                Double reveivePacketLoss = new BigDecimal(calculation(inDiscardsMap.get(port), timeInterval))
                        .setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
                Double sendPacketLoss = new BigDecimal(calculation(outDiscardsMap.get(port), timeInterval))
                        .setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
                Double reveiveError = new BigDecimal(calculation(inErrorMap.get(port), timeInterval))
                        .setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
                Double sendError = new BigDecimal(calculation(outErrorMap.get(port), timeInterval))
                        .setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
                Double reveiveBroadcast = new BigDecimal(calculation(inNUCastMap.get(port), timeInterval))
                        .setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
                Double sendBroadcast = new BigDecimal(calculation(outNUCastMap.get(port), timeInterval))
                        .setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();

                factory.addParsedField(SnmpPortStatisticalIndicators.HALF_DUPLEX_ETHERNET.getName(), halfDuplexEthernet);
                factory.addParsedField(SnmpPortStatisticalIndicators.FULL_DUPLEX_ETHERNET.getName(), fullDuplexEthernet);

                factory.addParsedField(SnmpPortStatisticalIndicators.PORT_RECEIVE.getName(), portReceive);
                factory.addParsedField(SnmpPortStatisticalIndicators.PORT_SEND.getName(), portSend);

                factory.addParsedField(SnmpPortStatisticalIndicators.REVEIVE_PACKET_LOSS.getName(), reveivePacketLoss);
                factory.addParsedField(SnmpPortStatisticalIndicators.SEND_PACKET_LOSS.getName(), sendPacketLoss);

                factory.addParsedField(SnmpPortStatisticalIndicators.REVEIVE_ERROR.getName(), reveiveError);
                factory.addParsedField(SnmpPortStatisticalIndicators.SEND_ERROR.getName(), sendError);

                factory.addParsedField(SnmpPortStatisticalIndicators.REVEIVE_BROADCAST.getName(), reveiveBroadcast);
                factory.addParsedField(SnmpPortStatisticalIndicators.SEND_BROADCAST.getName(), sendBroadcast);


                FlowData inFlow = inFlowMap.get(port);
                Double inSecSpeed = inFlow.getSecSpeed();
                Double periodFlow = inFlow.getPeriodFlow();
                Double inflow = inFlow.getFlow();
                Double lastFlow = inFlow.getLastFlow();

                factory.addParsedField(SnmpPortStatisticalIndicators.IN_SEC_SPEED.getName(), inSecSpeed);
                factory.addParsedField(SnmpPortStatisticalIndicators.IN_PERIOD_FLOW.getName(), periodFlow);
                factory.addParsedField(SnmpPortStatisticalIndicators.IN_FLOW.getName(), inflow);
                factory.addParsedField(SnmpPortStatisticalIndicators.IN_LAST_FLOW.getName(), lastFlow);

                FlowData outFlow = outFlowMap.get(port);
                Double outSecSpeed = outFlow.getSecSpeed();
                Double outPeriodFlow = outFlow.getPeriodFlow();
                Double outflow = outFlow.getFlow();
                Double outLastFlow = outFlow.getLastFlow();

                factory.addParsedField(SnmpPortStatisticalIndicators.OUT_SEC_SPEED.getName(), outSecSpeed);
                factory.addParsedField(SnmpPortStatisticalIndicators.OUT_PERIOD_FLOW.getName(), outPeriodFlow);
                factory.addParsedField(SnmpPortStatisticalIndicators.OUT_FLOW.getName(), outflow);
                factory.addParsedField(SnmpPortStatisticalIndicators.OUT_LAST_FLOW.getName(), outLastFlow);


                message = driverIp +
                        SnmpPortStatisticalIndicators.FIELD_SEPARATOR.getName() +
                        driverName +
                        SnmpPortStatisticalIndicators.FIELD_SEPARATOR.getName() +
                        timeInterval +
                        SnmpPortStatisticalIndicators.FIELD_SEPARATOR.getName() +
                        portName +
                        SnmpPortStatisticalIndicators.FIELD_SEPARATOR.getName() +
                        portIp +
                        SnmpPortStatisticalIndicators.FIELD_SEPARATOR.getName() +
                        curTime +
                        SnmpPortStatisticalIndicators.FIELD_SEPARATOR.getName() +
                        halfDuplexEthernet +
                        SnmpPortStatisticalIndicators.FIELD_SEPARATOR.getName() +
                        fullDuplexEthernet +
                        SnmpPortStatisticalIndicators.FIELD_SEPARATOR.getName() +
                        portReceive +
                        SnmpPortStatisticalIndicators.FIELD_SEPARATOR.getName() +
                        portSend +
                        SnmpPortStatisticalIndicators.FIELD_SEPARATOR.getName() +
                        reveivePacketLoss +
                        SnmpPortStatisticalIndicators.FIELD_SEPARATOR.getName() +
                        sendPacketLoss +
                        SnmpPortStatisticalIndicators.FIELD_SEPARATOR.getName() +
                        reveiveError +
                        SnmpPortStatisticalIndicators.FIELD_SEPARATOR.getName() +
                        sendError +
                        SnmpPortStatisticalIndicators.FIELD_SEPARATOR.getName() +
                        reveiveBroadcast +
                        SnmpPortStatisticalIndicators.FIELD_SEPARATOR.getName() +
                        sendBroadcast +
                        SnmpPortStatisticalIndicators.FIELD_SEPARATOR.getName() +
                        inSecSpeed +
                        SnmpPortStatisticalIndicators.FIELD_SEPARATOR.getName() +
                        periodFlow +
                        SnmpPortStatisticalIndicators.FIELD_SEPARATOR.getName() +
                        inflow +
                        SnmpPortStatisticalIndicators.FIELD_SEPARATOR.getName() +
                        lastFlow +
                        SnmpPortStatisticalIndicators.FIELD_SEPARATOR.getName() +
                        outSecSpeed +
                        SnmpPortStatisticalIndicators.FIELD_SEPARATOR.getName() +
                        outPeriodFlow +
                        SnmpPortStatisticalIndicators.FIELD_SEPARATOR.getName() +
                        outflow +
                        SnmpPortStatisticalIndicators.FIELD_SEPARATOR.getName() +
                        outLastFlow;

                factory.addParsedField("a_message", message);

                Mushroom generalMushroom = MushroomBuilder.withBody(factory.build(), null);
                generalMushroom.getHeaders().put(EventKey.DATA_TYPE_NAME, "a_"+CourseType.SNMP_PORTINFO.toString());
                putMushroom(generalMushroom);

            }

        }


    }

    /**
     * 流量信息
     *
     * @author ruibing.zhao
     * @since 2015年8月28日
     * @version 2.1.2
     */
    public static class FlowData {
        private String oid;
        private int port;
        // 周期流量
        private double periodFlow;
        // 每秒速率
        private double secSpeed;
        // 流量
        private double flow;
        // 上次注量
        private double lastFlow;

        public String getOid() {
            return oid;
        }

        public void setOid(String oid) {
            this.oid = oid;
        }

        public int getPort() {
            return port;
        }

        public void setPort(int port) {
            this.port = port;
        }

        public double getPeriodFlow() {
            return periodFlow;
        }

        public void setPeriodFlow(double periodFlow) {
            this.periodFlow = periodFlow;
        }

        public double getSecSpeed() {
            return secSpeed;
        }

        public void setSecSpeed(double secSpeed) {
            this.secSpeed = secSpeed;
        }

        public double getFlow() {
            return flow;
        }

        public void setFlow(double flow) {
            this.flow = flow;
        }

        public double getLastFlow() {
            return lastFlow;
        }

        public void setLastFlow(double lastFlow) {
            this.lastFlow = lastFlow;
        }

    }


    /**
     * 将(oid->value)整理为(端口 -> 当前OID，每秒速率，分钟总和，当前流量，上一次流量)
     *
     * @param map key:端口，value:当前OID，每秒速率，分钟总和，当前流量，上一次流量
     * @return
     */
    private Map<String, FlowData> tidyFlowData(Map<String, String> map) {
        Map<String, FlowData> result = new HashMap<String, FlowData>(map.size());
        Iterator<Map.Entry<String, String>> iterator = map.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<String, String> entry = iterator.next();
            String oid = entry.getKey();
            if (entry.getValue().matches("\\d+")) {
                long nowFlowVal = Long.parseLong(entry.getValue()) * 8;// 当前流量值
                Long lastFlowVal = tempOidval.get(oid);// 前一分钟流量值
                tempOidval.put(oid, nowFlowVal);
                if (lastFlowVal != null) {
                    String[] arr = oid.split("\\.");
                    String portStr = arr[arr.length - 1];// 端口号
                    long value = calFlow(nowFlowVal, lastFlowVal);// 分钟总和
                    long perValue = value / 60;// 每秒速率
                    FlowData fd = new FlowData();
                    fd.setOid(oid);
                    fd.setPort(Integer.parseInt(portStr));
                    fd.setFlow(nowFlowVal / (double) 1000);
                    fd.setLastFlow(lastFlowVal / (double) 1000);
                    fd.setPeriodFlow(value / (double) 1000);
                    fd.setSecSpeed(perValue / (double) 1000);
                    result.put(portStr, fd);
                }
            }
        }
        return result;
    }

    /**
     * 根据当前采集到的流量值和前1分钟采集到的流量值计算这一分钟的流量
     *
     * @param nowVal
     * @param lastVal
     * @return
     */
    private long calFlow(long nowVal, long lastVal) {
        long value = nowVal - lastVal;
        if (value < 0) {
            value = 4294967296l * 8 - lastVal + nowVal;
        }
        return value;
    }


    /**
     * 计算一段时间的差值
     * @param map
     * @return
     */
    private Map<String, Long> calculationDif(Map<String, String> map){
        Map<String, Long> difMap = new HashMap<String, Long>();
        for (Map.Entry<String, String> entry : map.entrySet()) {
            String oid = entry.getKey();
            String[] arr = oid.split("\\.");
            String port = arr[arr.length - 1];
            String stringValue = entry.getValue();
            Long nowValue ;
            if(stringValue != null && !stringValue.equals("")){
                nowValue = Long.parseLong(stringValue);
            }else{
                nowValue = 0l;
            }
            Long lastValue = oid_value.get(oid);
            oid_value.put(oid, nowValue);
            if(lastValue != null && !lastValue.equals("")){
                long value = nowValue - lastValue;
                if (value < 0) {
                    value = 4294967296l - lastValue + nowValue;
                }
                difMap.put(port, value);
            }
        }
        return difMap;
    }


    /**
     * 计算单位时间效率
     * @return
     */
    private Double calculation(long difValue, long time){
        return  (double)difValue * 100 / time;
    }

    /**
     * 计算半双工以太网利用率
     * @param difInValue
     * @param difOutValue
     * @param speedValue
     * @param time
     * @return
     */
    private Double calculationHalfDuplexEthernet(long difInValue, long difOutValue, long speedValue, long time){
        return  speedValue == 0 ? 0.00 : (double)(difInValue + difOutValue) * 8 * 100 / (time * speedValue);
    }

    /**
     * 计算全双工以太网利用率
     * @param difInValue
     * @param difOutValue
     * @param speedValue
     * @param time
     * @return
     */
    private Double calculationFullDuplexEthernet(long difInValue, long difOutValue, long speedValue, long time){
        return  speedValue == 0 ? 0.00 : (double)(difInValue > difOutValue ? difInValue : difOutValue) * 8 * 100 / (time * speedValue);
    }

    /**
     * 计算端口接收和发送率
     * @param difValue
     * @param speedValue
     * @param time
     * @return
     */
    private Double calculationPortRate(long difValue, long speedValue, long time){
        return  speedValue == 0 ? 0.00 : (double)difValue  * 8 * 100 / (time * speedValue);
    }


    /**
     * 获取port-ip映射
     *
     * @param snmp
     * @param target
     * @return
     * @throws Exception
     */
    private Map<String, String> portIpMap(Snmp snmp, Target target) throws Exception {
        Map<String, String> map = query(snmp, target, prefixOid, suffixOid);
        Map<String, String> result = new HashMap<String, String>(map.size());
        Iterator<Map.Entry<String, String>> iterator = map.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<String, String> entry = iterator.next();
            result.put(entry.getValue(), entry.getKey().replace(prefixOid + ".", ""));
        }
        return result;
    }

    @Override
    protected void doConfigure(Course context) throws Exception {
        super.doConfigure(context);
        preInOid = param.getOids().get(0);
        sufInOid = param.getOids().get(1);
        preOutOid = param.getOids().get(2);
        sufOutOid = param.getOids().get(3);
        preSpeedOid = param.getOids().get(4);
        sufSpeedOid = param.getOids().get(5);
        preInDiscardsOid = param.getOids().get(6);
        sufInDiscardsOid = param.getOids().get(7);
        preOutDiscardsOid = param.getOids().get(8);
        sufOutDiscardsOid = param.getOids().get(9);
        preInErrorOid = param.getOids().get(10);
        sufInErrorOid = param.getOids().get(11);
        preOutErrorOid = param.getOids().get(12);
        sufOutErrorOid = param.getOids().get(13);
        preInNUCastOid = param.getOids().get(14);
        sufInNUCastOid = param.getOids().get(15);
        preOutNUCastOid = param.getOids().get(16);
        sufOutNUCastOid = param.getOids().get(17);
        preNameOid = param.getOids().get(18);
        sufNameOid = param.getOids().get(19);
        prefixOid = param.getOids().get(20);
        suffixOid = param.getOids().get(21);
        sysUpTimeOid = param.getOids().get(22);

    }

    @Override
    protected void doSend() throws Exception {
        portInfo(this.snmp, this.target);
    }
}
