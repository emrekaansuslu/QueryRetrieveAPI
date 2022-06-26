package com.icterra.midas.ae.queryretrieve;

import com.icterra.midas.ae.queryretrieve.controller.ExcelController;
import com.icterra.midas.ae.queryretrieve.model.NetworkSettings;
import com.icterra.midas.ae.queryretrieve.service.NetworkSettingsService;
import com.icterra.midas.ae.queryretrieve.task.DownloadTask;
import com.icterra.midas.ae.queryretrieve.task.DownloadTask;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@EnableScheduling
@SpringBootApplication
public class QueryRetrieveApplication implements CommandLineRunner {

    @Autowired
    private QueryRetrieveAE queryRetrieveAE;

    @Autowired
    private DownloadTask downloadTask;





    public static void main(String[] args) {
        SpringApplication.run(QueryRetrieveApplication.class, args);
    }


    @Override
    public void run(String... args) throws Exception {

        queryRetrieveAE.initialize();
        ScheduledExecutorService scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();
        scheduledExecutorService.scheduleAtFixedRate(downloadTask,0,3, TimeUnit.MINUTES);
    }
}
