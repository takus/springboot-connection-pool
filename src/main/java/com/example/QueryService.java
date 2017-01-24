package com.example;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;

@Service
@Slf4j
public class QueryService {

    @Autowired
    PingRepository repository;

    @Value("${app.readerEnabled:true}")
    Boolean readerEnabled;

    @Value("${app.writerEnabled:true}")
    Boolean writerEnabled;

    @PostConstruct
    public void init() {

        if (readerEnabled) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    boolean shutdown = false;
                    while (!shutdown) {
                        try {
                            Thread.sleep(1000L);
                        } catch (InterruptedException e) {
                            log.warn(e.getMessage());
                        }

                        try {
                            Ping ping = repository.findOne(1);
                            log.info(ping.getV().toString());
                        } catch (Exception e) {
                            log.warn(e.getMessage());
                        }
                    }
                }
            }).start();
        }

        if (writerEnabled) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    boolean shutdown = false;
                    while (!shutdown) {
                        try {
                            long timestamp = System.currentTimeMillis() / 1000;
                            Ping ping = new Ping(1, (int) timestamp);
                            repository.save(ping);
                        } catch (Exception e) {
                            log.warn(e.getMessage());
                        }

                        try {
                            Thread.sleep(1000L);
                        } catch (InterruptedException e) {
                            log.warn(e.getMessage());
                        }
                    }
                }
            }).start();
        }

    }
}
