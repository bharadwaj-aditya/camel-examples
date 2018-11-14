/**
 * This code is intellectual property of Capillary Technologies.
 *
 * Copyright (c) (2017)
 * Created By aditya
 * Created On 28-Apr-2017
 */
package camel;

import org.apache.camel.CamelContext;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.impl.DefaultCamelContext;

/**
 * @author aditya
 *
 */
public class ExceptionHandling {

    public static void main(String[] args) throws Exception {
        CamelContext context = new DefaultCamelContext();
        context.addRoutes(new RouteBuilder() {
            public void configure() {
                
                errorHandler(deadLetterChannel("seda:errors").maximumRedeliveries(3));
                
                from("file:src/main/resources/?noop=true&fileName=testsrc")
                        .streamCaching()
                        .split()
                        .tokenize("\n")
                        .to("direct:lines");
                from("direct:lines").doTry().process(new ExceptionProcessor()).doCatch(Exception.class).endDoTry().to("seda:goo").to(
                        "file:src/main/resources/?fileName=testexception&fileExist=Append");
                
                from("seda:errors").log("failed").end();

            }
        });
        context.start();
        Thread.sleep(10000);
        context.stop();
    }

    private static class ExceptionProcessor implements Processor {

        int x = 0;

        @Override
        public void process(Exchange exchange) throws Exception {
            x++;
            if (x % 3 == 0) {
                System.out.println("woohoo " + exchange.getIn().getBody());
                exchange.getIn().setBody(exchange.getIn().getBody() + " woohoo " + x + "\n");
                return;
            }
            throw new Exception("go to hell");
        }

    }

}
