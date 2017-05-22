package com.bookshare;

import java.lang.invoke.MethodHandles;
import java.nio.charset.Charset;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

@ConfigurationProperties("bookshare.book")
@SpringBootApplication
public class BookshareApplication extends WebMvcConfigurerAdapter {

    private final static Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    private String rootCoverPath;

    public void setRootCoverPath(String rootCoverPath) {
        this.rootCoverPath = rootCoverPath;
    }

    public static void main(String[] args) {
        Map<String, String> env = System.getenv();
        logger.debug("System environment list:");
        for (String envName : env.keySet()) {
            logger.debug("[" + envName + "]: " + env.get(envName));
        }
        logger.debug("Default Charset=" + Charset.defaultCharset());
        SpringApplication.run(BookshareApplication.class, args);
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/files/**").addResourceLocations("file:" + rootCoverPath + "/");
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new ModelCookieInterceptor()).addPathPatterns("/sessions/**");
    }

}
