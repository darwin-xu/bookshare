package com.bookshare;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

@SpringBootApplication
public class BookshareApplication extends WebMvcConfigurerAdapter {

    public static Properties prop;

    public static void main(String[] args) throws FileNotFoundException, IOException {
        (prop = new Properties()).load(BookshareApplication.class.getResourceAsStream("/application.properties"));
        SpringApplication.run(BookshareApplication.class, args);
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/files/**")
                .addResourceLocations("file:" + prop.getProperty("bookshare.book.cover.path") + "/");
    }

}
