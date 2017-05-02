/**
 * This code is intellectual property of Capillary Technologies.
 *
 * Copyright (c) (2017)
 * Created By aditya
 * Created On 28-Apr-2017
 */
package spring;

import org.springframework.stereotype.Component;

/**
 * @author aditya
 *
 */
@Component("myBean")
public class HandlerBean {

    public String saySomething() {
        return "hello";
    }

    public String reply(String s) {
        return "hey there: " + s;
    }

}
