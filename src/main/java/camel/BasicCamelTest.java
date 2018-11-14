/**
 * This code is intellectual property of Capillary Technologies.
 *
 * Copyright (c) (2017)
 * Created By aditya
 * Created On 25-Apr-2017
 */
package camel;

import org.apache.camel.CamelContext;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.impl.DefaultCamelContext;

/**
 * @author aditya
 *
 */
public class BasicCamelTest {

    public static void main(String[] args) throws Exception {
        CamelContext context = new DefaultCamelContext();
        context.addRoutes(new RouteBuilder() {
            public void configure() {
                from("file:src/main/resources/?noop=true&fileName=testsrc")
                        .streamCaching()
                        .split()
                        .tokenize("\n", 2)
                        .process((e) -> e.getIn().setBody("asd"))
                        .to("file:src/main/resources/?fileName=testdst&fileExist=Append",
                                "file:src/main/resources/?fileName=testdst2&fileExist=Override");

            }
        });
        context.start();
        Thread.sleep(3000);
        context.stop();
    }

}
