package com.aleiye.lassock;

import com.aleiye.lassock.lang.Sistem;
import com.aleiye.lassock.util.SigarUtils;
import com.aleiye.lassock.util.StatusUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Lassock启动程序
 *
 * @author ruibing.zhao
 * @version 1.0
 * @since 2016年2月17日
 */
public class AleiyeLassock {

    private static Logger logger = LoggerFactory.getLogger(AleiyeLassock.class);

    public static void main(String[] args) {
        try {
            //获取机器名
            String hostName = args[0];
            // 将本机IP存入 System
            System.setProperty("local.host", SigarUtils.getIP());
            System.setProperty("local.hostName", hostName);
            // 采集器系统信息
            Class.forName(Sistem.class.getName());
            // Startable
            final LassockStartable startable = new LassockStartable();
            //启动ip变更的监听
            final String lastIp = SigarUtils.getIP();
            Timer timer = new Timer();
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    if (!lastIp.equals(SigarUtils.getIP())) {
                        StatusUtils.markStatusChange();
                        System.exit(0);
                    }
                }
            }, 10000, 10000);
            startable.startup();
            Runtime.getRuntime().addShutdownHook(new Thread() {
                @Override
                public void run() {
                    startable.shutdown();
                }
            });
            startable.awaitShutdown();
            System.exit(0);
        } catch (Exception e) {
            logger.error("Lassock startup failure!", e);
            System.exit(1);
        }
    }
}
