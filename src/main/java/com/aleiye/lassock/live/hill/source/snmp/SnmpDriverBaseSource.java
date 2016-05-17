package com.aleiye.lassock.live.hill.source.snmp;

import com.aleiye.lassock.api.Course;
import com.aleiye.lassock.api.SnmpPortStatisticalIndicators;
import com.aleiye.lassock.live.model.Mushroom;
import com.aleiye.lassock.live.model.MushroomBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.snmp4j.PDU;
import org.snmp4j.Snmp;
import org.snmp4j.Target;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Created by root on 2016/5/4.
 */
public class SnmpDriverBaseSource extends SnmpStandardSource{

    private static final Logger LOGGER = LoggerFactory.getLogger(SnmpDriverBaseSource.class);

    //端口名称
    private String preNameOid;
    private String sufNameOid;

    //端口描述
    private String preDesOid;
    private String sufDesOid;

    //端口ip
    private String prefixOid;
    private String suffixOid;

    //端口配置状态
    private String preConStateOid;
    private String sufConStateOid;

    //端口MTU
    private String preMtuOid;
    private String sufMtuOid;

    /**
     * 设备基本信息
     * @param snmp
     * @param target
     * @throws Exception
     */
    private void driverBase(Snmp snmp, Target target) throws Exception {

        //端口名称
        Map<String, String> portNameMap = mapSwitch(query(snmp, target, preNameOid, sufNameOid));
        if (portNameMap.size() == 0) {
            LOGGER.warn("No such instances|objects target:" + target.getAddress() + " type:" + PDU.GETBULK + " OID:"
                    + preNameOid + "-" + sufNameOid);
            return;
        }

        //端口描述
        Map<String, String> portDesMap = mapSwitch(query(snmp, target, preDesOid, sufDesOid));

        // 查找端口-IP映射
        Map<String, String> portIpMap = portIpMap(snmp, target);

        //端口配置状态
        Map<String, String> portConStateMap = mapSwitch(query(snmp, target, preConStateOid, sufConStateOid));

        //MTU
        Map<String, String> portMtuMap = mapSwitch(query(snmp, target, preMtuOid, sufMtuOid));

        Iterator<Map.Entry<String, String>> iterator = portNameMap.entrySet().iterator();
        Long curTime = System.currentTimeMillis();
        while (iterator.hasNext()) {
            Map.Entry<String, String> entry = iterator.next();
            String port = entry.getKey();
            Map<String, Object> map = new HashMap<String, Object>();
            String Mtu =  portMtuMap.get(port);
            map.put(SnmpPortStatisticalIndicators.DRIVER_IP.getName(), this.param.getHost());
            map.put(SnmpPortStatisticalIndicators.DRIVER_NAME.getName(), this.param.getDriverName());
            map.put(SnmpPortStatisticalIndicators.DRIVER_MODEL.getName(), this.param.getDriverModel());
            map.put(SnmpPortStatisticalIndicators.CURRENT_TIME.getName(), curTime);
            map.put(SnmpPortStatisticalIndicators.PORT_NAME.getName(), entry.getValue());
            map.put(SnmpPortStatisticalIndicators.PORT_DES.getName(), portDesMap.get(port));
            map.put(SnmpPortStatisticalIndicators.PORT_IP.getName(), portIpMap.get(port));
            map.put(SnmpPortStatisticalIndicators.CON_STATE.getName(), portConStateMap.get(port));
            map.put(SnmpPortStatisticalIndicators.PORT_MTU.getName(), Mtu != null ? Long.parseLong(Mtu) : Mtu);

            Mushroom generalMushroom = MushroomBuilder.withBody(map, null);
            generalMushroom.getHeaders().put("target", this.param.getHost());
            putMushroom(generalMushroom);
        }

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
        preNameOid = param.getOids().get(0);
        sufNameOid = param.getOids().get(1);
        preDesOid = param.getOids().get(2);
        sufDesOid = param.getOids().get(3);
        prefixOid = param.getOids().get(4);
        suffixOid = param.getOids().get(5);
        preConStateOid = param.getOids().get(6);
        sufConStateOid = param.getOids().get(7);
        preMtuOid = param.getOids().get(8);
        sufMtuOid = param.getOids().get(9);

    }

    @Override
    protected void doSend() throws Exception {
        driverBase(this.snmp, this.target);
    }
}
