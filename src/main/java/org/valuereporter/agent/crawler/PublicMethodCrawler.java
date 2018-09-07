package org.valuereporter.agent.crawler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.valuereporter.ImplementedMethod;
import org.valuereporter.http.HttpImplementedMethodSender;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author <a href="bard.lind@gmail.com">Bard Lind</a>
 */
public class PublicMethodCrawler implements Runnable {
    private static final Logger log = LoggerFactory.getLogger(PublicMethodCrawler.class);
    private final String reporterHost;
    private final String reporterPort;
    private final String serviceName;
    private final String basePackage;
    private List<ImplementedMethod> publicMethods = new ArrayList<>();
    private int MAX_CACHE_SIZE = 500;

    public PublicMethodCrawler(String reporterHost, String reporterPort, String serviceName, String basePackage) {
        this.reporterHost = reporterHost;
        this.reporterPort = reporterPort;
        this.serviceName = serviceName;
        this.basePackage = basePackage;
    }

    @Override
    public void run() {
        crawlForPublicMethods();
    }

    protected void crawlForPublicMethods() {
        log.info("Starting to crawl for Public Methods.");
        try {
            ArrayList<String> names = PublicMethodFinder.getClassNamesFromPackage(basePackage);
            log.info("Found these names {}", names.size());
            List<Class> classes = PublicMethodFinder.findClasses(names);

            for (Class clazz : classes) {
                List<ImplementedMethod> publicMethodsInClass = null;
                log.trace("Found a potential class. {}", clazz.getName());
                if (clazz.isPrimitive() || clazz.isArray() || clazz.isAnnotation()
                        || clazz.isEnum() || clazz.isInterface()) {
                    log.trace("Skip class {}: not a class", clazz.getName());
                } else {
                    publicMethodsInClass = PublicMethodFinder.findPublicMethods(clazz);
                    updateMethodsForClass(publicMethodsInClass, clazz.getName());
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        log.info("Crawled all classes. Local cash has {} elements still. Forwarding these to ValueReporter.", publicMethods.size());
        forwardOutput();
        log.info("Done crawling.");
    }

    protected void updateMethodsForClass(List<ImplementedMethod> publicMethodsInClass, String className) {
        if (publicMethodsInClass != null) {
            log.trace("Found {} public methods in {}", publicMethodsInClass.size(), className);
            publicMethods.addAll(publicMethodsInClass);
            if (publicMethods.size() >= MAX_CACHE_SIZE) {
                forwardOutput();
            }
        } else {
            log.warn("Observed Method is null");
        }

    }

    private void forwardOutput() {
        log.trace("Forwarding PublicMethods. Local cache size {}", publicMethods.size());
        new Thread(new HttpImplementedMethodSender(reporterHost, reporterPort, serviceName, publicMethods)).start();
        publicMethods.clear();
    }
}
