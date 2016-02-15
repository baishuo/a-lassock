package com.aleiye.lassock.live.hill.shade;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.aleiye.lassock.live.hill.PollableShade;
import com.aleiye.lassock.live.hill.ShadeRunner;
import com.aleiye.lassock.live.hill.shade.tool.ShadeScheduler;
import com.aleiye.lassock.live.lifecycle.LifecycleState;
import com.aleiye.lassock.live.scroll.Sign;
import com.aleiye.lassock.util.LogUtils;

public class QuartzGroupShadeRunner extends ShadeRunner {
	private static final Logger LOGGER = LoggerFactory.getLogger(QuartzGroupShadeRunner.class);
	private static final String GROUP_NAME = "groupName";
	private static ConcurrentHashMap<String, CopyOnWriteArrayList<PollableShade>> groups = new ConcurrentHashMap<String, CopyOnWriteArrayList<PollableShade>>();

	public static void createTask(String groupName, String cron) {
		Sign sign = new Sign();
		sign.setId("GROUP_" + groupName);
		sign.setId("GROUP_RUNNER");
		sign.setCron(cron);
		Map<String, String> param = new HashMap<>();
		param.put(GROUP_NAME, groupName);
		try {
			ShadeScheduler.addJob(QuartzGroupRunner.class, sign, param);
		} catch (Exception e) {
			LOGGER.debug("", e);
		}
	}

	public static void delTask(String groupName) {
		Sign sign = new Sign();
		sign.setId("GROUP_" + groupName);
		sign.setId("GROUP_RUNNER");
		try {
			ShadeScheduler.delJob(sign);
		} catch (Exception e) {
			LOGGER.debug("", e);
		}
	}

	private LifecycleState lifecycleState;

	@Override
	public void start() {
		PollableShade shade = (PollableShade) getShade();
		shade.start();
		String gruopname = shade.getSign().getGroupName();
		try {
			if (groups.containsKey(gruopname)) {
				groups.get(gruopname).add(shade);
			} else {
				synchronized (groups) {
					CopyOnWriteArrayList<PollableShade> ss = new CopyOnWriteArrayList<PollableShade>();
					ss.add(shade);
					groups.put(gruopname, ss);
				}
			}
			lifecycleState = LifecycleState.START;
			LOGGER.info("Shade:" + shade.getName() + "was running!");
		} catch (Exception e) {
			LOGGER.error("Shade:" + shade.getName() + " start exception!");
			LOGGER.debug("", e);
		}
	}

	@Override
	public void stop() {
		PollableShade shade = (PollableShade) getShade();
		Sign sign = shade.getSign();
		if (groups.get(sign.getGroupName()).remove(shade)) {
			;
		}
		shade.stop();
		lifecycleState = LifecycleState.STOP;
		LOGGER.info("Shade:" + shade.getName() + "was stoped!");
	}

	@Override
	public LifecycleState getLifecycleState() {
		return lifecycleState;
	}

	public static class QuartzGroupRunner implements Job {
		@Override
		public void execute(JobExecutionContext context) throws JobExecutionException {
			JobDataMap jobdata = context.getJobDetail().getJobDataMap();
			String shadeKey = (String) jobdata.get(GROUP_NAME);
			CopyOnWriteArrayList<PollableShade> shades = groups.get(shadeKey);
			if (shades != null && shades.size() > 0) {
				Iterator<PollableShade> it = shades.iterator();
				PollableShade shade = null;
				while (it.hasNext()) {
					try {
						shade = it.next();
						shade.pick();
					} catch (Exception e) {
						LOGGER.error(shade.getName() + " pick exception!");
						LogUtils.error(shade.getName() + " pick exception!" + e.getMessage());
						LOGGER.debug("", e);
					}
				}
			}

		}
	}
}
