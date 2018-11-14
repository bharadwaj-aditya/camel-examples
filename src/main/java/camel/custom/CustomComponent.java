/**
 * This code is intellectual property of Capillary Technologies.
 *
 * Copyright (c) (2017)
 * Created By aditya
 * Created On 27-Apr-2017
 */
package camel.custom;

import java.util.Map;

import org.apache.camel.CamelContext;
import org.apache.camel.Endpoint;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.impl.DefaultCamelContext;
import org.apache.camel.impl.DefaultComponent;

/**
 * @author aditya
 *
 */
public class CustomComponent extends DefaultComponent {

    public static void main(String[] args) throws Exception {
        CamelContext context = new DefaultCamelContext();
        context.addComponent("custom", new CustomComponent());

        context.addRoutes(new RouteBuilder() {
            public void configure() {
                from("custom:a").to("file:src/main/resources/?fileName=testcustom&fileExist=Append");
                //from("file:src/main/resources/?fileName=testsrc&fileExist=Append").to("file:src/main/resources/?fileName=testcustom&fileExist=Append");
            }
        });
        context.start();
        Thread.sleep(3000);
        context.stop();

    }

    protected Endpoint createEndpoint(String uri, String remaining, Map<String, Object> parameters) throws Exception {
/*        return new ProcessorEndpoint("custom:a", this, new Processor() {

            @Override
            public void process(Exchange exchange) throws Exception {
                System.out.println("goo");
                exchange.getIn().setBody("boo");
            }
        });*/
        return new CustomEndpoint();
    }

    /*    private class CustomProcessorEndpoint extends ProcessorEndpoint { 
        
    }
    */
}
