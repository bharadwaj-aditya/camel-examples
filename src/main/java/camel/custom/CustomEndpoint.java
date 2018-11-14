/**
 * This code is intellectual property of Capillary Technologies.
 *
 * Copyright (c) (2017)
 * Created By aditya
 * Created On 27-Apr-2017
 */
package camel.custom;

import org.apache.camel.CamelContext;
import org.apache.camel.Consumer;
import org.apache.camel.Endpoint;
import org.apache.camel.Exchange;
import org.apache.camel.ExchangePattern;
import org.apache.camel.Message;
import org.apache.camel.Processor;
import org.apache.camel.Producer;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.impl.DefaultCamelContext;
import org.apache.camel.impl.DefaultEndpoint;
import org.apache.camel.impl.DefaultMessage;

/**
 * @author aditya
 *
 */
public class CustomEndpoint extends DefaultEndpoint {

    public static void main(String[] args) throws Exception {

        CamelContext context = new DefaultCamelContext();

        context.addRoutes(new RouteBuilder() {
            public void configure() {
                from(new CustomEndpoint()).to("file:src/main/resources/?fileName=testcustom&fileExist=Append");
            }
        });
        context.start();
        Thread.sleep(10000);
        context.stop();
    }

    @Override
    public Producer createProducer() throws Exception {
        return new CustomProducer(this);
    }

    @Override
    public Consumer createConsumer(Processor processor) throws Exception {
        return new CustomConsumer(this, processor);
    }

    @Override
    public boolean isSingleton() {
        return false;
    }

    public ExchangePattern getExchangePattern() {
        return ExchangePattern.InOut;
    }

    @Override
    protected String createEndpointUri() {
        return "custom:a";
    }

    private class CustomProducer implements Producer {

        private Endpoint endpoint;

        /**
         * @param endpoint
         */
        public CustomProducer(Endpoint endpoint) {
            super();
            this.endpoint = endpoint;
        }

        @Override
        public void process(Exchange exchange) throws Exception {
        }

        @Override
        public void start() throws Exception {
            System.out.println("started producer");
        }

        @Override
        public void stop() throws Exception {
            System.out.println("stopped producer");
        }

        @Override
        public boolean isSingleton() {
            return false;
        }

        @Override
        public Endpoint getEndpoint() {
            return endpoint;
        }

        @Override
        public Exchange createExchange() {
            System.out.println("create exchange");
            return null;
        }

        @Override
        public Exchange createExchange(ExchangePattern pattern) {
            System.out.println("create exchange by pattern");
            return null;
        }

        @Override
        public Exchange createExchange(Exchange exchange) {
            System.out.println("create exchange by from exchange");
            return null;
        }

    }

    private class CustomConsumer implements Consumer {

        private int counter;
        private Endpoint endpoint;
        private Processor processor;

        /**
         * @param endpoint
         * @param processor
         */
        public CustomConsumer(Endpoint endpoint, Processor processor) {
            super();
            this.endpoint = endpoint;
            this.processor = processor;
        }

        @Override
        public void start() throws Exception {
            System.out.println("started consumer");
            pushData();
        }

        private void pushData() {
            Runnable runnable = new Runnable() {

                @Override
                public void run() {
                    try {
                        Thread.sleep(2000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    for (int i = 0; i < 10; i++) {
                        Message m = new DefaultMessage();
                        m.setBody("Boohoo" + counter++);
                        Exchange exchange = endpoint.createExchange();
                        exchange.setIn(m);
                        try {
                            processor.process(exchange);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    System.out.println("completed pushing");
                }
            };
            new Thread(runnable).start();
        }

        @Override
        public void stop() throws Exception {
            System.out.println("stopped consumer");
        }

        @Override
        public Endpoint getEndpoint() {
            return endpoint;
        }

    }

}
