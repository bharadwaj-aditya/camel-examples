/**
 * This code is intellectual property of Capillary Technologies.
 *
 * Copyright (c) (2017)
 * Created By aditya
 * Created On 28-Apr-2017
 */
package rest;

import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.rest.RestBindingMode;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.stereotype.Component;

/**
 * @author aditya
 *
 */
@SpringBootApplication
@Component
public class RestRouteBuilder extends RouteBuilder {

    public static void main(String[] args) throws InterruptedException {
        SpringApplication.run(RestRouteBuilder.class, args);
    }

    @Override
    public void configure() throws Exception {
        restConfiguration()
                .component("jetty")
                .bindingMode(RestBindingMode.json)
                .dataFormatProperty("prettyPrint", "true")
                .port(8080);

        rest("/user")
                .description("User rest service")
                .consumes("application/json")
                .produces("application/json")

                .get("/{id}")
                .description("get reply")
                .outType(String.class)
                .to("bean:myService?method=reply(${header.id})")

                //redirect call when id is not passed
                .get("/")
                .description("default")
                .outType(String.class)
                .to("restlet:http://localhost:8080/user/cool?restletMethod=get");
        /*
        .put()
        .description("Updates or create a user")
        .type(String.class)
        .to("bean:userService?method=updateUser")
        
        .get("/findAll")
        .description("Find all users")
        .outTypeList(String.class)
        .to("bean:userService?method=listUsers");*/
    }

}
