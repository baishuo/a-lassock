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
import com.aleiye.lassock.live.hill.Sign;
import com.aleiye.lassock.live.hill.executor.tool.ShadeScheduler;
import com.aleiye.lassock.live.hill.shade.PollableShade;
import com.aleiye.lassock.live.hill.shade.ShadeRunner;

public class QuartzShadeRunner extends ShadeRunner {
	private static final Logger LOGGER = LoggerFactory.getLogger(QuartzShadeRunner.class);
	private static final String DETAIL_KEY = "GUID";
	private static ConcurrentHashMap<String, PollableShade> shades = new ConcurrentHashMap<String, PollableShade>();

	private LifecycleState lifecycleState;

	private String guid;

	@Override
	public void start() {
		PollableShade shade = (PollableShade) getShade();
		shade.start();
		guid = UUID.randomUUID().toString();
		shades.put(guid, shade);
		Sign sign = shade.getSign();
		Map<String, String> param = new HashMap<>();
		param.put(DETAIL_KEY, guid);
		try {
			ShadeScheduler.addJob(QuartzRunner.class, sign, param);
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
		PollableShade shade = (PollableShade) getShade();
		Sign sign = shade.getSign();
		try {
			ShadeScheduler.delJob(sign);
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
			// SchedulerContext schCtx =
			// context.getScheduler().getContext();
			JobDataMap jobdata = context.getJobDetail().getJobDataMap();
			String shadeKey = (String) jobdata.get(DETAIL_KEY);
			PollableShade shade = shades.get(shadeKey);
			try {
				shade.pick();
			} catch (Exception e) {
				LOGGER.error(shade.getName() + " pick exception!");
				LOGGER.debug("", e);
			}
		}
	}
}
