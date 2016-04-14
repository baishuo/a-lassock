package com.aleiye.lassock.live.hill.executor;

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

import com.aleiye.lassock.lifecycle.LifecycleState;
import com.aleiye.lassock.live.hill.executor.tool.SourceScheduler;
import com.aleiye.lassock.live.hill.source.EventTrackSource;
import com.aleiye.lassock.live.hill.source.Sign;
import com.aleiye.lassock.live.hill.source.SourceRunner;
import com.aleiye.lassock.util.LogUtils;

public class QuartzGroupSourceRunner extends SourceRunner {
	private static final Logger LOGGER = LoggerFactory.getLogger(QuartzGroupSourceRunner.class);
	private static final String GROUP_NAME = "groupName";
	private static ConcurrentHashMap<String, CopyOnWriteArrayList<EventTrackSource>> groups = new ConcurrentHashMap<String, CopyOnWriteArrayList<EventTrackSource>>();

	public static void createTask(String groupName, String cron) {
		Map<String, String> param = new HashMap<>();
		param.put(GROUP_NAME, groupName);
		try {
			SourceScheduler.addJob(QuartzGroupRunner.class, "GROUP_" + groupName, "GROUP_RUNNER", cron, param);
		} catch (Exception e) {
			LOGGER.debug("", e);
		}
	}

	public static void delTask(String groupName) {
		try {
			SourceScheduler.delJob("GROUP_" + groupName, "GROUP_RUNNER");
		} catch (Exception e) {
			LOGGER.debug("", e);
		}
	}

	private LifecycleState lifecycleState;

	@Override
	public void start() {
		EventTrackSource shade = (EventTrackSource) getShade();
		shade.start();
		String gruopname = shade.getSign().getCourse().getGroupName();
		try {
			if (groups.containsKey(gruopname)) {
				groups.get(gruopname).add(shade);
			} else {
				synchronized (groups) {
					CopyOnWriteArrayList<EventTrackSource> ss = new CopyOnWriteArrayList<EventTrackSource>();
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
		EventTrackSource shade = (EventTrackSource) getShade();
		Sign sign = shade.getSign();
		if (groups.get(sign.getCourse().getGroupName()).remove(shade)) {
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
			CopyOnWriteArrayList<EventTrackSource> shades = groups.get(shadeKey);
			if (shades != null && shades.size() > 0) {
				Iterator<EventTrackSource> it = shades.iterator();
				EventTrackSource shade = null;
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
