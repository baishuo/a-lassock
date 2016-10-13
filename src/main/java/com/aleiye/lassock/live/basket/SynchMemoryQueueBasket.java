package com.aleiye.lassock.live.basket;

import com.aleiye.lassock.conf.Context;
import com.aleiye.lassock.live.model.Mushroom;

import java.util.concurrent.SynchronousQueue;

/**
 * Created by weiwentao on 16/9/21.
 */
public class SynchMemoryQueueBasket extends AbstractBasket {

    private SynchronousQueue<Mushroom> queue = new SynchronousQueue<Mushroom>(true);

    @Override
    public void push(Mushroom mushroom) throws InterruptedException {
        queue.put(mushroom);
    }

    @Override
    public Mushroom take() throws InterruptedException {
        return queue.take();
    }

    @Override
    public void configure(Context context) {

    }
}
