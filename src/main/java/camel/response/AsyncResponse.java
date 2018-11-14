/**
 * :q
 * This code is intellectual property of Capillary Technologies.
 *
 * Copyright (c) (2017)
 * Created By aditya
 * Created On 25-Apr-2017
 */
package camel.response;

import java.util.Date;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.camel.CamelContext;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.impl.DefaultCamelContext;

/**
 * @author aditya
 *
 */
public class AsyncResponse {

    private CamelContext context;
    private ProducerTemplate producerTemplate;

    public AsyncResponse() throws Exception {
        //Uses the default 5 threads in the pool
        super();
        context = new DefaultCamelContext();
        context.addRoutes(new RouteBuilder() {
            public void configure() {
                from("direct:req").process(new WaitingProcessor()).end();
            }
        });
        producerTemplate = context.createProducerTemplate();
        context.start();
    }

    public CamelContext getContext() {
        return context;
    }

    public ProducerTemplate getProducerTemplate() {
        return producerTemplate;
    }

    public CompletableFuture<Object> sendAndGetResponse(String request) {
        return producerTemplate.asyncRequestBody("direct:req", request, Object.class);
    }

    public static class WaitingProcessor implements Processor {

        private static AtomicInteger i = new AtomicInteger(0);

        @Override
        public void process(Exchange exchange) throws Exception {
            System.out.println("started " + new Date());
            Thread.sleep(10000);
            exchange.getIn().setBody(exchange.getIn().getBody() + " " + i.incrementAndGet());
            System.out.println("ended " + new Date());
        }

    }

    public static void main(String[] args) throws Exception {
        AsyncResponse requestReply = new AsyncResponse();
        CompletableFuture<Object> resp1 = requestReply.sendAndGetResponse("abc");
        CompletableFuture<Object> resp2 = requestReply.sendAndGetResponse("abcd");
        System.out.println(resp1.get());
        System.out.println(resp2.get());
        requestReply.getContext().stop();
    }
}
