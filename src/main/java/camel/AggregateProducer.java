/**
 * This code is intellectual property of Capillary Technologies.
 *
 * Copyright (c) (2017)
 * Created By aditya
 * Created On 26-Apr-2017
 */
package camel;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.camel.CamelContext;
import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.apache.camel.Predicate;
import org.apache.camel.Processor;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.impl.DefaultCamelContext;
import org.apache.camel.processor.aggregate.AggregationStrategy;
import org.apache.camel.processor.aggregate.GroupedMessageAggregationStrategy;

/**
 * seda and aggregator
 * @author aditya
 *
 */
public class AggregateProducer {

    public static void main(String[] args) throws Exception {
        CamelContext context = new DefaultCamelContext();

        context.addRoutes(new RouteBuilder() {
            public void configure() {
                from("quartz://myGroup/myTimerName?cron=*+*+*+?+*+MON-FRI")
                        .process((ex) -> ex.getIn().setBody("asd"))
                        .aggregate(constant(true), new GroupedMessageAggregationStrategy())
                        .completionSize(4) //Max size of aggregation
                        .completionInterval(3000) //Max idle time before aggregation is triggered
                        .process(new ListToStringProcessor())
                        .to("file:src/main/resources/?fileName=testagg&fileExist=Append")
                        .end();
            }
        });
        context.start();
        Thread.sleep(10000);
        context.stop();
        System.out.println("completed");
    }

    public static class ListToStringProcessor implements Processor {

        @Override
        public void process(Exchange exchange) throws Exception {
            System.out.println("boo "
                               + exchange.getProperty("CamelAggregatedCompletedBy")
                               + " "
                               + exchange.getProperty("CamelAggregatedSize"));
            List<Message> body = exchange.getIn().getBody(List.class);
            String collect =
                    (String) (body.stream().map((e) -> (String) (e.getBody())).collect(Collectors.joining("\n")));
            System.out.println(collect);
            exchange.getIn().setBody(collect + new Date());
        }
    }
}
