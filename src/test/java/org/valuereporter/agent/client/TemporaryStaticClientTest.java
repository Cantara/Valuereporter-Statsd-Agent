package org.valuereporter.agent.client;

import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.testng.Assert.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import org.valuereporter.agent.MonitorAgent;

import com.timgroup.statsd.StatsDClient;

public class TemporaryStaticClientTest {
	private static final Logger log = LoggerFactory.getLogger(TemporaryStaticClientTest.class);

	private TemporaryStaticClient staticClient;
	private StatsDClient statsd;

	@BeforeMethod
	public void setUp() {
		statsd = mock(StatsDClient.class);

	}
	@AfterMethod
	public void afterMethod() {
		staticClient.setStatsd(null);
		TemporaryStaticClient.getInstance().setStatsd(null);
	}

	@Test
	public void testSetStatsd() {
		staticClient = TemporaryStaticClient.getInstance();
		assertNull(staticClient.getStatsd());
		staticClient.setStatsd(statsd);
		assertNotNull(staticClient.getStatsd());
	}

	@Test
	public void testGetInstanceWithParams() {
		staticClient = TemporaryStaticClient.getInstance(MonitorAgent.DEFAULT_REPORTER_PREFIX, MonitorAgent.DEFAULT_REPORTER_HOST, MonitorAgent.DEFAULT_REPORTER_PORT);
		assertNotNull(staticClient.getStatsd());
		assertFalse(staticClient.getStatsd().getClass().getName().contains("Mockito"));
	}
	@Test
	public void testGetInstance() {
		staticClient = TemporaryStaticClient.getInstance();
		assertNull(staticClient.getStatsd());
	}

	@Test
	public void testReportTime() {
		staticClient = TemporaryStaticClient.getInstance();
		staticClient.setStatsd(statsd);
		assertTrue(staticClient.getStatsd().getClass().getName().contains("Mockito"));
		staticClient.reportTime("test", 3, 4);
		verify(statsd).recordExecutionTime(eq("test"), eq(1L));
	}

	@Test
	public void testReportActivity() {
		staticClient = TemporaryStaticClient.getInstance();
		staticClient.setStatsd(statsd);
		assertTrue(staticClient.getStatsd().getClass().getName().contains("Mockito"));
		staticClient.reportActivity("test");
		verify(statsd).incrementCounter(eq("test"));
	}


}