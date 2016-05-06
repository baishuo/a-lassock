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

/**
 * 任务管理
 * 
 * @author ruibing.zhao
 * @since 2016年2月18日
 * @version 1.0
 */
public class SourceScheduler {
	private static final Logger LOGGER = LoggerFactory.getLogger(SourceScheduler.class);

	// quartz trigger的命名前缀
	// private static final String trigger_name_prefix = "trigger_";

	private static Scheduler scheduler;

	private static LifecycleState lifecycleState;

	private SourceScheduler() {}

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

	public static void addJob(Class<? extends Job> clazz, String name, String group, String cron,
			Map<String, String> param) throws ParseException, SchedulerException {
		// job
		JobDetail job = JobBuilder.newJob(clazz).withIdentity(name, group).build();
		// 参数
		job.getJobDataMap().putAll(param);
		// 触发器
		CronTrigger cronTrigger = TriggerBuilder.newTrigger().withIdentity(name, group)
				.withSchedule(CronScheduleBuilder.cronSchedule(cron)).build();
		scheduler.scheduleJob(job, cronTrigger);
	}

	/**
	 * 删除作业
	 * 
	 * @param jobname
	 * @param jobGroup
	 * @throws SchedulerException
	 */
	public static void delJob(String name, String group) throws SchedulerException {
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
