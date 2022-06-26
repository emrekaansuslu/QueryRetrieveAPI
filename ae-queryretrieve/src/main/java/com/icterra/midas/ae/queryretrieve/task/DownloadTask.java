package com.icterra.midas.ae.queryretrieve.task;


import com.icterra.midas.ae.queryretrieve.QueryRetrieveAE;
import com.icterra.midas.ae.queryretrieve.controller.ExcelController;
import com.icterra.midas.ae.queryretrieve.model.*;
import com.icterra.midas.ae.queryretrieve.service.CrawlerStatusService;
import com.icterra.midas.ae.queryretrieve.util.FileOperations;
import lombok.Synchronized;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

@Slf4j
@Component
public class DownloadTask extends TimerTask {

    @Autowired
    private CrawlerStatusService crawlerStatusService;
    @Autowired
    private FileOperations fileOperations;
    @Autowired
    private QueryRetrieveAE queryRetrieveAE;
    @Autowired
    private ExcelController excelController;

    private String destinationPath = "E:\\DATAS";
    private DateTimeFormatter excelFormat = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss");
    private DateTimeFormatter dbFormat = DateTimeFormatter.ofPattern("yyyyMMdd");

    private Object lockObject = new Object();

    public void  sendRequestForExcel() {
        synchronized (lockObject) {
            String lastExcelIndex = "";
            try {
                lastExcelIndex = fileOperations.getExcelIndex();
            } catch (Exception e) {
                log.error(e.toString());
                log.error(" Get ExcelIndex Error !!");
                return;
            }

            // Update Excel Index
            int index = Integer.parseInt(lastExcelIndex);
            int updatedIndex = index + 1;
            try {
                fileOperations.setExcelIndex("" + updatedIndex);
            } catch (Exception e) {
                log.error(e.toString());
                log.error(" Update ExcelIndex Error !");
            }
            ArrayList<String> informationList = (ArrayList<String>) excelController.getPatientInformation(index);


            if (informationList != null) {
                String applyDateUpdated =  informationList.get(2);;

                String patientID = informationList.get(1);
                System.out.println("Crawling excel index :: " + index);
                System.out.println("Crawling patient Id :: " + patientID);
                System.out.println("Crawling patient date :: " + applyDateUpdated);

                try {
                    List<QueryRetrieveFindResult> queryResult = queryRetrieveAE.query(applyDateUpdated, patientID);
                    if (queryResult != null && queryResult.size() > 0) {
                        List<ServiceStatusResult> retrieveResult = queryRetrieveAE.retrieve(queryResult);
                        if (retrieveResult != null && retrieveResult.size() > 0) {
                            int downloadImageCount = fileOperations.getDownloadImageCount(patientID, applyDateUpdated);
                            if (downloadImageCount != 0) {
                                // SUCCESS CASE
                                log.info("Patient Id :: " + patientID + " Patient Date :: " + applyDateUpdated + " CRAWLED SUCCESSFULLY !!");
                                CrawlerStatus crawlerStatus = new CrawlerStatus();
                                crawlerStatus.setStatus("Success");
                                crawlerStatus.setPatientNumber(patientID);
                                crawlerStatus.setIndex(informationList.get(0));
                                crawlerStatus.setDate(applyDateUpdated);
                                crawlerStatusService.updateCrawlerStatus(crawlerStatus);
                            } else {
                                // FAIL CASE
                                log.info("Patient Id :: " + patientID + " Patient Date :: " + applyDateUpdated + " CRAWLED FAILED !!");
                                CrawlerStatus crawlerStatus = new CrawlerStatus();
                                crawlerStatus.setStatus("Failed");
                                crawlerStatus.setPatientNumber(patientID);
                                crawlerStatus.setIndex(informationList.get(0));
                                crawlerStatus.setDate(applyDateUpdated);
                                crawlerStatusService.updateCrawlerStatus(crawlerStatus);
                                crawlerStatusService.updateCrawlerStatus(crawlerStatus);
                            }
                        } else {
                            // FAIL CASE
                            log.info("Patient Id :: " + patientID + " Patient Date :: " + applyDateUpdated + " CRAWLED FAILED !!");
                            CrawlerStatus crawlerStatus = new CrawlerStatus();
                            crawlerStatus.setStatus("Success");
                            crawlerStatus.setPatientNumber(patientID);
                            crawlerStatus.setIndex(informationList.get(0));
                            crawlerStatus.setDate(applyDateUpdated);
                            crawlerStatusService.updateCrawlerStatus(crawlerStatus);
                            crawlerStatusService.updateCrawlerStatus(crawlerStatus);
                        }
                    } else {
                        // FAIL CASE
                        log.info("Patient Id :: " + patientID + " Patient Date :: " + applyDateUpdated + " CRAWLED FAILED !!");
                        CrawlerStatus crawlerStatus = new CrawlerStatus();
                        crawlerStatus.setStatus("Failed");
                        crawlerStatus.setStatus("Success");
                        crawlerStatus.setPatientNumber(patientID);
                        crawlerStatus.setIndex(informationList.get(0));
                        crawlerStatus.setDate(applyDateUpdated);
                        crawlerStatusService.updateCrawlerStatus(crawlerStatus);
                        crawlerStatusService.updateCrawlerStatus(crawlerStatus);
                    }
                } catch (Exception e) {
                    // FAIL CASE
                    log.info("Patient Id :: " + patientID + " Patient Date :: " + applyDateUpdated + " CRAWLED FAILED !!");
                    CrawlerStatus crawlerStatus = new CrawlerStatus();
                    crawlerStatus.setStatus("Failed");
                    crawlerStatus.setStatus("Success");
                    crawlerStatus.setPatientNumber(patientID);
                    crawlerStatus.setIndex(informationList.get(0));
                    crawlerStatus.setDate(applyDateUpdated);
                    crawlerStatusService.updateCrawlerStatus(crawlerStatus);
                    crawlerStatusService.updateCrawlerStatus(crawlerStatus);
                    log.info(e.toString());
                    e.printStackTrace();
                }
            } else {
                log.info("Patient Id :: " + "NULL" + " Patient Date :: " + "NULL" + " CRAWLED FAILED !!");
                CrawlerStatus crawlerStatus = new CrawlerStatus();
                crawlerStatus.setStatus("Failed");
                crawlerStatus.setStatus("Success");
                crawlerStatus.setPatientNumber(informationList.get(1));
                crawlerStatus.setIndex(informationList.get(0));
                crawlerStatus.setDate(informationList.get(2));
                crawlerStatusService.updateCrawlerStatus(crawlerStatus);
                crawlerStatusService.updateCrawlerStatus(crawlerStatus);
            }
        }
    }

    @Override
    public void run() {
        sendRequestForExcel();
    }
}
