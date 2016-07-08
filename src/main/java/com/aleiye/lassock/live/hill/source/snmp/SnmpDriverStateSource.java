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

        double result = 0.0;

        String message ;

        String host = this.param.getHost();
        String driverName = this.param.getDriverName();
        String devName = this.param.getDevName();

        while (iterator.hasNext()) {
            Map.Entry<String, String> entry = iterator.next();
            result += Double.parseDouble(entry.getValue());
        }

        result /= driverMap.size();

        switch (this.param.getCollectType()){
            case CPUTYPE :
                factory.addParsedField(SnmpPortStatisticalIndicators.CPU.getName(), result);
                factory.addParsedField(SnmpPortStatisticalIndicators.DRIVER_STATE_TYPE.getName(),
                        SnmpPortStatisticalIndicators.CPU.getName());
                break;
            case  TEMPERATURETYPE :
                factory.addParsedField(SnmpPortStatisticalIndicators.TEMPERATURE.getName(), result);
                factory.addParsedField(SnmpPortStatisticalIndicators.DRIVER_STATE_TYPE.getName(),
                        SnmpPortStatisticalIndicators.TEMPERATURE.getName());
                break;
            case MEMORYTYPE :
                factory.addParsedField(SnmpPortStatisticalIndicators.MEMORY.getName(), result);
                factory.addParsedField(SnmpPortStatisticalIndicators.DRIVER_STATE_TYPE.getName(),
                        SnmpPortStatisticalIndicators.MEMORY.getName());
                break;
        }


        factory.addParsedField(SnmpPortStatisticalIndicators.DEV_NAME.getName(), devName);
        factory.addParsedField(SnmpPortStatisticalIndicators.CURRENT_TIME.getName(), curTime);
        factory.addParsedField(SnmpPortStatisticalIndicators.DRIVER_IP.getName(), host);
        factory.addParsedField(SnmpPortStatisticalIndicators.DRIVER_NAME.getName(), driverName);

        message = result +
                SnmpPortStatisticalIndicators.FIELD_SEPARATOR.getName() +
                devName +
                SnmpPortStatisticalIndicators.FIELD_SEPARATOR.getName() +
                curTime +
                SnmpPortStatisticalIndicators.FIELD_SEPARATOR.getName() +
                host +
                SnmpPortStatisticalIndicators.FIELD_SEPARATOR.getName() +
                driverName;

        factory.addParsedField(EventKey.MESSAGE, message);



        Mushroom generalMushroom = MushroomBuilder.withBody(factory.build(), null);
        generalMushroom.getHeaders().put(EventKey.DATA_TYPE_NAME, "a_"+CourseType.SNMP_DRIVERSTATE.toString());
        putMushroom(generalMushroom);



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
