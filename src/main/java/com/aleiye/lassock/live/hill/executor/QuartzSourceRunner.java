package com.aleiye.lassock.live.hill.executor;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.SchedulerException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.aleiye.lassock.lifecycle.LifecycleState;
import com.aleiye.lassock.live.hill.executor.tool.SourceScheduler;
import com.aleiye.lassock.live.hill.source.EventTrackSource;
import com.aleiye.lassock.live.hill.source.Sign;
import com.aleiye.lassock.live.hill.source.SourceRunner;

public class QuartzSourceRunner extends SourceRunner {
	private static final Logger LOGGER = LoggerFactory.getLogger(QuartzSourceRunner.class);
	private static final String DETAIL_KEY = "GUID";
	private static ConcurrentHashMap<String, EventTrackSource> shades = new ConcurrentHashMap<String, EventTrackSource>();

	private LifecycleState lifecycleState;

	private String guid;

	@Override
	public void start() {
		EventTrackSource shade = (EventTrackSource) getShade();
		shade.start();
		guid = UUID.randomUUID().toString();
		shades.put(guid, shade);
		Sign sign = shade.getSign();
		Map<String, String> param = new HashMap<>();
		param.put(DETAIL_KEY, guid);
		try {
			SourceScheduler.addJob(QuartzRunner.class, shade.getName(), sign.getCourse().getType().toString(), sign
					.getCourse().getCron(), param);
			lifecycleState = LifecycleState.START;
			LOGGER.info("Shade:" + shade.getName() + "was running!");
		} catch (Exception e) {
			e.printStackTrace();
			LOGGER.error("Shade:" + shade.getName() + " start exception!");
			LOGGER.debug("", e);
		}
	}

	@Override
	public void stop() {
		EventTrackSource shade = (EventTrackSource) getShade();
		Sign sign = shade.getSign();
		try {
			SourceScheduler.delJob(shade.getName(), sign.getCourse().getType().toString());
			shades.remove(guid);
			shade.stop();
			lifecycleState = LifecycleState.STOP;
			LOGGER.info("Shade:" + shade.getName() + "was stoped!");
		} catch (SchedulerException e) {
			LOGGER.error("Shade:" + shade.getName() + " stop exception!");
			LOGGER.debug("", e);
		}
	}

	@Override
	public LifecycleState getLifecycleState() {
		return lifecycleState;
	}

	public static class QuartzRunner implements Job {
		@Override
		public void execute(JobExecutionContext context) throws JobExecutionException {
			JobDataMap jobdata = context.getJobDetail().getJobDataMap();
			String shadeKey = (String) jobdata.get(DETAIL_KEY);
			EventTrackSource shade = shades.get(shadeKey);
			try {
				shade.pick();
			} catch (Exception e) {
				LOGGER.error(shade.getName() + " pick exception!");
				LOGGER.debug("", e);
			}
		}
	}
}
