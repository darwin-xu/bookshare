package com.bookshare.background;

import java.lang.invoke.MethodHandles;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class DemandDispatcher {

    private final static Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");

    @Scheduled(fixedRateString = "${bookshare.book.dispatch-interval:60}000")
    public void reportCurrentTime() {
        logger.info("The time is now {}", dateFormat.format(new Date()));

    }

}
