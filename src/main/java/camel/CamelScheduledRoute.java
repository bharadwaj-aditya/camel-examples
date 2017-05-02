/**
 * This code is intellectual property of Capillary Technologies.
 *
 * Copyright (c) (2017)
 * Created By aditya
 * Created On 26-Apr-2017
 */
package camel;

import java.util.Date;

import org.apache.camel.CamelContext;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.TypeConversionException;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.impl.DefaultCamelContext;
import org.apache.camel.support.TypeConverterSupport;

/**
 * @author aditya
 *
 */
public class CamelScheduledRoute {

    public static void main(String[] args) throws Exception {
        CamelContext context = new DefaultCamelContext();
        
        context.getTypeConverterRegistry().addTypeConverter(Character[].class, String.class, new TypeConverterSupport() {
            
            @Override
            public <T> T convertTo(Class<T> type, Exchange exchange, Object value) throws TypeConversionException {
                return (T)((String)value).toCharArray();
            }
        });
        
        context.getTypeConverterRegistry().addTypeConverter(String.class, Character[].class,
                new TypeConverterSupport() {

                    @Override
                    public <T> T convertTo(Class<T> type, Exchange exchange, Object value)
                            throws TypeConversionException {
                        Character[] charArray = (Character[]) value;
                        StringBuilder br = new StringBuilder();
                        for (char c : charArray) {
                            br.append(c);
                        }
                        return (T) br.toString();
                    }
                });

        context.addRoutes(new RouteBuilder() {
            public void configure() {
                from("quartz://myGroup/myTimerName?cron=*+*+*+?+*+MON-FRI")
                        .process(new StringProcessor())
                        .convertBodyTo(Character[].class)
                        .convertBodyTo(String.class)
                        .to("file:src/main/resources/?fileName=testcron&fileExist=Append",
                                "log:org.apache.camel.example?level=WARN");
            }
        });
        context.start();
        Thread.sleep(3000);
        context.stop();

    }

    public static class StringProcessor implements Processor {

        @Override
        public void process(Exchange exchange) throws Exception {
            System.out.println(exchange.getIn().getBody());
            System.out.println(exchange.getIn().getHeaders());
            exchange.getIn().setBody(new Date() + "asd\n");
        }

    }

}
