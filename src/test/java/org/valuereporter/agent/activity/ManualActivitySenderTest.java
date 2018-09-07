package org.valuereporter.agent.activity;

import org.slf4j.Logger;
import org.valuereporter.activity.CommandActivitySender;
import org.valuereporter.activity.ObservedActivity;

import java.util.ArrayList;
import java.util.List;

import static org.slf4j.LoggerFactory.getLogger;

/**
 * Created by baardl on 31.07.17.
 */
public class ManualActivitySenderTest {
    private static final Logger log = getLogger(ManualActivitySenderTest.class);

    private static String host = "localhost";
    private static String port = "4901";
    private static String prefix = "manual-test";

    //Manual testing
    public static void main(String[] args) {

        List<ObservedActivity> observedActivities = new ArrayList<>();
        CommandActivitySender commandSender = new CommandActivitySender(host, port,prefix, observedActivities);
        log.info("Forward {} activities to http://{}:{} with prefix {}. \n" +
                "\tTo view visit http://{}:{}/reporter/...TODO", observedActivities.size(), host, port, prefix,host,port);//TODO correct url
        commandSender.run();
        log.info("Done");

    }

}