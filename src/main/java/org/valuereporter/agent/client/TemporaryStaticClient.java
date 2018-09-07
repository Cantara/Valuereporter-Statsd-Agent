package org.valuereporter.agent.client;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.timgroup.statsd.NonBlockingStatsDClient;
import com.timgroup.statsd.StatsDClient;

public class TemporaryStaticClient {
	private static final Logger log = LoggerFactory.getLogger(TemporaryStaticClient.class);
	private static final TemporaryStaticClient instance = new TemporaryStaticClient();
	private StatsDClient statsd;
	private String prefix;
	private String reporterHost;
	private int reporterPort;

	private TemporaryStaticClient() {

	}

	void setStatsd(StatsDClient statsd) {
		this.statsd = statsd;
	}

	public static TemporaryStaticClient getInstance() {
		return instance;
	}

	public static TemporaryStaticClient getInstance(String prefix, String reporterHost, int reporterPort) {
		getInstance().setStatsd(new NonBlockingStatsDClient(prefix, reporterHost, reporterPort));
		return getInstance();
	}

	public static void reportTime(String name, long startTimeMillis, long endTimeMillis) {
		getStatsd().recordExecutionTime(name, endTimeMillis - startTimeMillis);
	}

	public static void reportActivity(String name) {
		getStatsd().incrementCounter(name);
	}

	public static StatsDClient getStatsd() {
		return getInstance().statsd;
	}

	public static String getPrefix() {
		return getInstance().prefix;
	}

	public static String getReporterHost() {
		return getInstance().reporterHost;
	}

	public static int getReporterPort() {
		return getInstance().reporterPort;
	}
}
