/**
 * This code is intellectual property of Capillary Technologies.
 *
 * Copyright (c) (2017)
 * Created By aditya
 * Created On 26-Apr-2017
 */
package camel;

import org.apache.camel.CamelContext;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.impl.DefaultCamelContext;

import camel.CamelScheduledRoute.StringProcessor;

/**
 * To define a static endpoint without a class
 * @author aditya
 *
 */
public class DirectProducer {

    public static void main(String[] args) throws Exception {
        CamelContext context = new DefaultCamelContext();

        context.addRoutes(new RouteBuilder() {
            public void configure() {
                from("quartz://myGroup/myTimerName?cron=*+*+*+?+*+MON-FRI").to("direct:fileWriter");
                from("direct:fileWriter").process(new StringProcessor()).to(
                        "file:src/main/resources/?fileName=testdirect&fileExist=Append");
            }
        });
        context.start();
        Thread.sleep(10000);
        context.stop();

    }

}
