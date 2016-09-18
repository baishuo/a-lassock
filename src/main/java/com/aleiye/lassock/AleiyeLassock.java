package com.aleiye.lassock;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.aleiye.lassock.lang.Sistem;
import com.aleiye.lassock.util.SigarUtils;

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
            // 开启
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
