/**
 * This code is intellectual property of Capillary Technologies.
 *
 * Copyright (c) (2017)
 * Created By aditya
 * Created On 25-Apr-2017
 */
package camel.response;

import java.util.Date;

import org.apache.camel.CamelContext;
import org.apache.camel.Exchange;
import org.apache.camel.ExchangePattern;
import org.apache.camel.Processor;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.impl.DefaultCamelContext;

/**
 * @author aditya
 *
 */
public class SyncResponse {

    private CamelContext context;
    private ProducerTemplate producerTemplate;

    public SyncResponse() throws Exception {
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

    public Object sendAndGetResponse(String request) {
        return producerTemplate.sendBody("direct:req", ExchangePattern.InOut, request);
    }

    public static class WaitingProcessor implements Processor {

        private static int i = 0;

        @Override
        public void process(Exchange exchange) throws Exception {
            System.out.println("started " + new Date());
            Thread.sleep(10000);
            exchange.getIn().setBody(exchange.getIn().getBody() + " " + i++);
            System.out.println("ended " + new Date());
        }

    }

    public static void main(String[] args) throws Exception {
        SyncResponse requestReply = new SyncResponse();
        System.out.println(requestReply.sendAndGetResponse("abc"));
        System.out.println(requestReply.sendAndGetResponse("abcd"));
        requestReply.getContext().stop();
    }
}
