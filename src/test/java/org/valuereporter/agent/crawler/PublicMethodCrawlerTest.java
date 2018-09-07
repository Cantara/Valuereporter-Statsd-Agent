package org.valuereporter.agent.crawler;

import org.testng.annotations.Test;
import org.valuereporter.ImplementedMethod;

import java.util.ArrayList;
import java.util.List;

public class PublicMethodCrawlerTest {

    @Test
    public void testUpdateMethodsForClass() throws Exception {
        PublicMethodCrawler crawler = new PublicMethodCrawler("host", "0000", "prefix","com.base.package");

        List<ImplementedMethod> implmentedMethods = new ArrayList<>();
        ImplementedMethod stubMethod = new ImplementedMethod("com.base.package.class.stubMethod");
        implmentedMethods.add(stubMethod);
        //Ensure no NPE is thrown
        crawler.updateMethodsForClass(implmentedMethods, this.getClass().getName());



    }
}