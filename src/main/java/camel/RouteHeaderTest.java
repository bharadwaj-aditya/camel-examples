/**
 * This code is intellectual property of Capillary Technologies.
 *
 * Copyright (c) (2017)
 * Created By aditya
 * Created On 19-Jul-2017
 */
package camel;

import java.util.Map;

import org.apache.camel.CamelContext;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.impl.DefaultCamelContext;

/**
 * To pass headers across routes
 * @author aditya
 *
 */
public class RouteHeaderTest {

    public static void main(String[] args) throws Exception {
        CamelContext context = new DefaultCamelContext();
        context.addRoutes(new RouteBuilder() {
            public void configure() {
                from("file:src/main/resources/?noop=true&fileName=testsrc")
                        .streamCaching()
                        .split()
                        .tokenize("\n", 2)
                        .process((e) -> e.getIn().setHeader("asd", "pqr"))
                        .to("direct:fileWriter");

                from("direct:fileWriter").process(new Processor() {

                    @Override
                    public void process(Exchange exchange) throws Exception {
                        Map<String, Object> headers = exchange.getIn().getHeaders();
                        System.out.println("get header " + headers.get("asd"));

                    }
                }).to("file:src/main/resources/?fileName=testdst2&fileExist=Override");

            }
        });
        System.out.println(
                "pool size " + context.getExecutorServiceManager().getDefaultThreadPoolProfile().getPoolSize());
        context.start();
        Thread.sleep(3000);
        context.stop();
    }
}
