package org.valuereporter.agent;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.valuereporter.agent.client.TemporaryStaticClient;
import org.valuereporter.agent.crawler.PublicMethodCrawler;
import org.valuereporter.client.activity.ObservedActivityDistributer;
import org.valuereporter.client.http.HttpObservationDistributer;

import java.lang.instrument.Instrumentation;
import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;
import java.util.HashMap;
import java.util.Map;


/**
 *
 * MonitorAgent is the main class for this tool.
 * This class will be started before your application, or application server starts up.
 * Two pieces of functionallities will be started at the same time:
 * - Instrumentation of classes, to monitor and report when they are beeing used.
 * - Crawling your application to find implemented methods, that might be used.
 *
 * The latter functionallity enables you to see reports on the methods, and classes that are never beeing used.
 *
 * Reduce noice:
 * Specify your package structure by adding the base.package parameter. This will enable the agent to only monitor, and
 * report on the crucial piece of information: Your code!
 *
 * Prefix:
 * The prefix parameter enables you to identify which application and node that are running the functionallity.
 * Valuereporter will be able to receive observations from multiple applications, and node. The prefix parameter will
 * separate the data from each of these individual runtimes.
 *
 * Usage
 * java -javaagent:../valuereporter-agent/valuereporter-agent-jar-with-dependencies.jar= \
 base.package:com.example,statsd.host.url:http://localhost:4901,prefix:<serviceId> \
 -jar <your jar file>
 */
public class MonitorAgent {
    private static final Logger log = LoggerFactory.getLogger(MonitorAgent.class);
    public static final String BASE_PACKAGE_KEY = "base_package";
    public static final String VALUE_REPORTER_HOST_KEY = "statsd_host";
    public static final String VALUE_REPORTER_PORT_KEY = "statsd_port";
    public static final String PREFIX_KEY = "prefix";
    public static final String DEFAULT_REPORTER_HOST = "localhost";
    public static final int DEFAULT_REPORTER_PORT = 8125;
    public static final String DEFAULT_REPORTER_PREFIX = "temp.default";

    public static void premain(String agentArguments, Instrumentation instrumentation) {
        RuntimeMXBean runtimeMxBean = ManagementFactory.getRuntimeMXBean();
        log.info("Runtime: {}: {}", runtimeMxBean.getName(), runtimeMxBean.getInputArguments());
        log.info("Starting agent with arguments {}" , agentArguments);
        String basePackage = "";
        String reporterHost = DEFAULT_REPORTER_HOST;
        int reporterPort = DEFAULT_REPORTER_PORT;
        String prefix = DEFAULT_REPORTER_PREFIX;

        if (agentArguments != null) {
            Map<String, String> properties = new HashMap<>();
            for(String propertyAndValue: agentArguments.split(",")) {
                String[] tokens = propertyAndValue.split(":", 2);
                if (tokens.length != 2) {
                    continue;
                }
                properties.put(tokens[0], tokens[1]);

            }
            basePackage = properties.get(BASE_PACKAGE_KEY);
            log.info("ValueReporter base.package property {}", basePackage);

            String host = properties.get(VALUE_REPORTER_HOST_KEY);
            log.info("ValueReporter host property {}", host);
            if ( host!= null) {
                reporterHost = host;
            }
            String port = properties.get(VALUE_REPORTER_PORT_KEY);
            log.info("ValueReporter port property {}", port);
            if (port != null && !port.isEmpty()) {
                reporterPort = Integer.valueOf(port);
            }
            String tmpPrefix = properties.get(PREFIX_KEY);
            if (tmpPrefix != null) {
                prefix = tmpPrefix;
            }
            log.info("ValueReporter prefix property {}", prefix);
        }

        // define the class transformer to use
        instrumentation.addTransformer(new TimedClassTransformer(basePackage));

        log.info("starting StatsdObservatioDistributer");
        TemporaryStaticClient.getInstance(prefix, reporterHost, reporterPort);
    }



}
