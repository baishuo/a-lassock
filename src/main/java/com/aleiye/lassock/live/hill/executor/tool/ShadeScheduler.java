package com.aleiye.lassock.live.hill.executor.tool;

import java.text.ParseException;
import java.util.Map;

import org.quartz.CronScheduleBuilder;
import org.quartz.CronTrigger;
import org.quartz.Job;
import org.quartz.JobBuilder;
import org.quartz.JobDetail;
import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.SchedulerFactory;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.quartz.TriggerKey;
import org.quartz.impl.StdSchedulerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.aleiye.lassock.lifecycle.LifecycleState;
import com.aleiye.lassock.live.hill.Sign;

/**
 * 任务管理
 * 
 * @author ruibing.zhao
 * @since 2016年2月18日
 * @version 1.0
 */
public class ShadeScheduler {
	private static final Logger LOGGER = LoggerFactory.getLogger(ShadeScheduler.class);

	// quartz trigger的命名前缀
	// private static final String trigger_name_prefix = "trigger_";

	private static Scheduler scheduler;

	private static LifecycleState lifecycleState;

	private ShadeScheduler() {}

	public static Scheduler getScheduler() {
		return scheduler;
	}

	public static void start() {
		SchedulerFactory factory = new StdSchedulerFactory();
		try {
			scheduler = factory.getScheduler();
			scheduler.start();
			lifecycleState = LifecycleState.START;
		} catch (SchedulerException e) {
			LOGGER.error("Quartz scheduler start excetpion！");
			LOGGER.debug("", e);
		}
	}

	public static boolean isStarted() {
		return lifecycleState == LifecycleState.START;
	}

	public static void shutdown(boolean flag) {
		try {
			scheduler.shutdown(flag);
			lifecycleState = LifecycleState.STOP;
			LOGGER.info("Quartz scheduler shutdown！");
		} catch (SchedulerException e) {
			LOGGER.error("Quartz scheduler shutdown excetpion！");
			LOGGER.debug("", e);
		}
	}

	public static void addJob(Class<? extends Job> clazz, Sign sign, Map<String, String> param) throws ParseException,
			SchedulerException {
		String name = sign.getId();
		String group = sign.getType();
		// job
		JobDetail job = JobBuilder.newJob(clazz).withIdentity(name, group).build();
		// 参数
		job.getJobDataMap().putAll(param);
		// 触发器
		CronTrigger cronTrigger = TriggerBuilder.newTrigger().withIdentity(name, group)
				.withSchedule(CronScheduleBuilder.cronSchedule(sign.getCron())).build();
		scheduler.scheduleJob(job, cronTrigger);
	}

	/**
	 * 删除作业
	 * 
	 * @param jobname
	 * @param jobGroup
	 * @throws SchedulerException
	 */
	public static void delJob(Sign sign) throws SchedulerException {
		String name = sign.getId();
		String group = sign.getType();
		JobKey jk = new JobKey(name, group);
		TriggerKey tk = new TriggerKey(name, group);
		// 停止触发器
		scheduler.pauseTrigger(tk);
		scheduler.pauseJob(jk);
		// 移除触发器
		scheduler.unscheduleJob(tk);
		// 删除任务
		scheduler.deleteJob(jk);

	}

	public static Trigger getTrigger(String triggerName, String triggerGroup) throws SchedulerException {
		return scheduler.getTrigger(new TriggerKey(triggerName, triggerGroup));
	}

}
