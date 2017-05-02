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

/**
 * seda and aggregator
 * @author aditya
 *
 */
public class SedaProducer {

    public static void main(String[] args) throws Exception {
        CamelContext context = new DefaultCamelContext();

        context.addRoutes(new RouteBuilder() {
            public void configure() {
                from("quartz://myGroup/myTimerName?cron=*+*+*+?+*+MON-FRI")
                        .to("seda:fileWriter?pollTimeout=5000&size=5&blockWhenFull=true");

                from("seda:fileWriter")
                        .process((ex) -> ex.getIn().setBody("asd"))
                        .aggregate(constant(true), new ArrayListAggregationStrategy())
                        .completionPredicate(new BatchSizePredicate(6))
                        .completionInterval(5000)
                        .process(new ListToStringProcessor())
                        .to("file:src/main/resources/?fileName=testseda&fileExist=Append")
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
            List body = exchange.getIn().getBody(List.class);
            String collect = (String) (body.stream().collect(Collectors.joining("\n")));
            exchange.getIn().setBody(collect + new Date());
        }
    }

    public static class ArrayListAggregationStrategy implements AggregationStrategy {

        public ArrayListAggregationStrategy() {
            super();
        }

        public Exchange aggregate(Exchange oldExchange, Exchange newExchange) {
            Message newIn = newExchange.getIn();
            Object newBody = newIn.getBody();
            ArrayList list = null;
            if (oldExchange == null) {
                list = new ArrayList();
                list.add(newBody);
                newIn.setBody(list);
                return newExchange;
            } else {
                Message in = oldExchange.getIn();
                list = in.getBody(ArrayList.class);
                list.add(newBody);
                return oldExchange;
            }
        }

    }

    public static class BatchSizePredicate implements Predicate {

        public int size;

        public BatchSizePredicate(int size) {
            this.size = size;
        }

        @Override
        public boolean matches(Exchange exchange) {
            if (exchange != null) {
                ArrayList list = exchange.getIn().getBody(ArrayList.class);
                if (list != null && list.size() == size) {
                    System.out.println("yesss");
                    return true;
                }
                System.out.println("not yet " + list.size());
            } else {
                System.out.println("nooooooooooo");
            }
            return false;
        }

    }

}
