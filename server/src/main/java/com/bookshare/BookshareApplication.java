package com.bookshare;

import java.io.IOException;
import java.lang.invoke.MethodHandles;
import java.nio.charset.Charset;
import java.util.Map;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

import com.bookshare.utility.StringUtil;

@SpringBootApplication
public class BookshareApplication extends WebMvcConfigurerAdapter {

    private final static Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    public static Properties prop;

    static {
        try {
            (prop = new Properties()).load(BookshareApplication.class.getResourceAsStream("/application.properties"));
        } catch (IOException e) {
            logger.error(StringUtil.toString(e));
        }
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
        registry.addResourceHandler("/files/**")
                .addResourceLocations("file:" + prop.getProperty("bookshare.book.cover.path") + "/");
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new ModelCookieInterceptor()).addPathPatterns("/sessions/**");
    }

}
