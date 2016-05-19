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
public class SnmpDriverStateSource extends SnmpStandardSource{

    private static final Logger LOGGER = LoggerFactory.getLogger(SnmpDriverStateSource.class);

    private final String CPUTYPE = "cpu";
    private final String MEMORYTYPE = "memory";
    private final String TEMPERATURETYPE = "temperature";

    //oid
    private String preOid;
    private String sufOid;


    /**
     * 设备状态信息
     * @param snmp
     * @param target
     * @throws Exception
     */
    private void driverState(Snmp snmp, Target target) throws Exception {
        //扫描设备
        Map<String, String> driverMap = query(snmp, target, preOid, sufOid);
        if (driverMap.size() == 0) {
            LOGGER.warn("No such instances|objects target:" + target.getAddress() + " type:" + PDU.GETBULK + " OID:"
                    + preOid + "-" + sufOid);
            return;
        }

        Iterator<Map.Entry<String, String>> iterator = driverMap.entrySet().iterator();
        Long curTime = System.currentTimeMillis();
        while (iterator.hasNext()) {
            Map.Entry<String, String> entry = iterator.next();
            Map<String, Object> map = new HashMap<String, Object>();
            switch (this.param.getCollectType()){
                case CPUTYPE :
                    map.put(SnmpPortStatisticalIndicators.CPU.getName(), entry.getValue());
                    break;
                case  TEMPERATURETYPE :
                    map.put(SnmpPortStatisticalIndicators.TEMPERATURE.getName(), entry.getValue());
                    break;
                case MEMORYTYPE :
                    map.put(SnmpPortStatisticalIndicators.MEMORY.getName(), entry.getValue());
                    break;
            }
            map.put(SnmpPortStatisticalIndicators.CURRENT_TIME.getName(), curTime);
            map.put(SnmpPortStatisticalIndicators.DRIVER_IP.getName(), this.param.getHost());
            map.put(SnmpPortStatisticalIndicators.DRIVER_NAME.getName(), this.param.getDriverName());
            Mushroom generalMushroom = MushroomBuilder.withBody(map, null);
            generalMushroom.getHeaders().put("target", this.param.getHost());
            putMushroom(generalMushroom);
        }

    }

    @Override
    protected void doConfigure(Course context) throws Exception {
        super.doConfigure(context);
        preOid = param.getOids().get(0);
        sufOid = param.getOids().get(1);

    }

    @Override
    protected void doSend() throws Exception {
        driverState(this.snmp, this.target);
    }
}
