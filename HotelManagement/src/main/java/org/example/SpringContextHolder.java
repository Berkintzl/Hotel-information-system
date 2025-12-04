package org.example;

import org.springframework.context.ConfigurableApplicationContext;

public class SpringContextHolder {
    private static ConfigurableApplicationContext context;

    public static void set(ConfigurableApplicationContext ctx) { context = ctx; }

    public static <T> T getBean(Class<T> type) { return context.getBean(type); }
}
