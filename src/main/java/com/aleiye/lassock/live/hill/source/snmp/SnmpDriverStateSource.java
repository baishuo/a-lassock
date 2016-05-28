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

        AleiyeParsedEventFactory.Builder factory = AleiyeParsedEventFactory.builder();

        while (iterator.hasNext()) {
            Map.Entry<String, String> entry = iterator.next();
            switch (this.param.getCollectType()){
                case CPUTYPE :
                    factory.addParsedField(SnmpPortStatisticalIndicators.CPU.getName(), entry.getValue());
                    break;
                case  TEMPERATURETYPE :
                    factory.addParsedField(SnmpPortStatisticalIndicators.TEMPERATURE.getName(), entry.getValue());
                    break;
                case MEMORYTYPE :
                    factory.addParsedField(SnmpPortStatisticalIndicators.MEMORY.getName(), entry.getValue());
                    break;
            }
            factory.addParsedField(SnmpPortStatisticalIndicators.CURRENT_TIME.getName(), curTime);
            factory.addParsedField(SnmpPortStatisticalIndicators.DRIVER_IP.getName(), this.param.getHost());
            factory.addParsedField(SnmpPortStatisticalIndicators.DRIVER_NAME.getName(), this.param.getDriverName());
            Mushroom generalMushroom = MushroomBuilder.withBody(factory.build(), null);
            generalMushroom.getHeaders().put(EventKey.DATA_TYPE_NAME, CourseType.SNMP_DRIVERSTATE.toString());
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
