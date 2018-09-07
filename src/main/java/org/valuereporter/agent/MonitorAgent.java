package org.valuereporter.agent;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
    public static final String SERVICE_NAME_KEY = "serviceName";
    private static final String DEFAULT_REPORTER_HOST = "localhost";
    private static final String DEFAULT_REPORTER_PORT = "4901";
    private static final String DEFAULT_SERVICE_NAME = "serviceName-not-set";
    private static final String ACTIVITY_BATCH_SIZE_KEY = "valuereporter.activity.batchsize";
    private static final String ACTIVITY_POST_INTERVAL_KEY = "valuereporter.activity.postintervalms";
    private static final int DEFAULT_POST_INTERVAL_MS = 500;
    private static final int DEFAULT_ACTIVITY_BATCH_SIZE = 500;


    public static void premain(String agentArguments, Instrumentation instrumentation) {
        RuntimeMXBean runtimeMxBean = ManagementFactory.getRuntimeMXBean();
        log.info("Runtime: {}: {}", runtimeMxBean.getName(), runtimeMxBean.getInputArguments());
        log.info("Starting agent with arguments {}" , agentArguments);
        String basePackage = "";
        String reporterHost = DEFAULT_REPORTER_HOST;
        String reporterPort = DEFAULT_REPORTER_PORT;
        String serviceName = DEFAULT_SERVICE_NAME;
        int activityPostIntrvalMS = DEFAULT_POST_INTERVAL_MS;
        int activityBatchSize = DEFAULT_ACTIVITY_BATCH_SIZE;

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
            if ( port!= null) {
                reporterPort = port;
            }
            String tmpPrefix = properties.get(SERVICE_NAME_KEY);
            if (tmpPrefix != null) {
                serviceName = tmpPrefix;
            }
            log.info("ValueReporter serviceName property {}", serviceName);

            String tmpActivityBatchSize = properties.get(ACTIVITY_BATCH_SIZE_KEY);
            if (tmpActivityBatchSize != null) {
                activityBatchSize = new Integer(tmpActivityBatchSize);
            }
            log.info("ValueReporter ACTIVITY_BATCH_SIZE property {}", activityBatchSize);

            String tmpActivityPostInterval = properties.get(ACTIVITY_POST_INTERVAL_KEY);
            if (tmpActivityPostInterval != null) {
                activityPostIntrvalMS = new Integer(tmpActivityPostInterval);
            }
            log.info("ValueReporter ACTIVITY_BATCH_INTERVAL property {}", activityPostIntrvalMS);




        }

        // define the class transformer to use
        instrumentation.addTransformer(new TimedClassTransformer(basePackage));

        log.info("Starting HttpObservationDistributer");
        new Thread(new HttpObservationDistributer(reporterHost, reporterPort, serviceName)).start();
        log.info("Starting ObservedActivityDistributer");
        new Thread(ObservedActivityDistributer.getInstance(reporterHost, reporterPort, serviceName, activityPostIntrvalMS)).start();
        log.info("Activate Crawling for public methods.");
        new Thread(new PublicMethodCrawler(reporterHost, reporterPort,serviceName, basePackage)).start();
    }



}
