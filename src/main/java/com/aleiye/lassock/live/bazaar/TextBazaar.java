package com.aleiye.lassock.live.bazaar;

import com.aleiye.lassock.conf.Context;
import com.aleiye.lassock.live.basket.Basket;
import com.aleiye.lassock.live.model.Mushroom;
import com.aleiye.lassock.util.ConfigUtils;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;

/**
 *
 * Text输出消费端，主要用来消费telnet采集的数据
 * 把Telnet命令和返回值采集保存在本地
 *
 * Created by root on 2016/5/10.
 */
public class TextBazaar extends AbstractBazaar{

    private static final Logger _LOG = LoggerFactory.getLogger(TextBazaar.class);

    //文件保存路径
    private String path ;

    //换行符
    private String lineSeparator = System.getProperty("line.separator", "\n");

    @Override
    public void process() throws Exception {

        Basket channel = getBasket();
        Mushroom event = null;

        try{
            event = channel.take();

            File telnetFile = new File(path);

            if (event != null) {
                String body =  new String(event.getBody(), "UTF-8");

                FileUtils.writeStringToFile(telnetFile, body + lineSeparator, true);
            }

            event.incrementCompleteCount();

        }catch (InterruptedException e){
            _LOG.error("get message error", e);
        }catch (Exception e){
            e.printStackTrace();
            event.incrementFailedCount();
            _LOG.error("put data into text error", e);
        }
    }

    @Override
    public void configure(Context context) {
        path = context.getString("path");
    }
}
