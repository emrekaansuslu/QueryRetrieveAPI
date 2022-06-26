package com.icterra.midas.ae.queryretrieve.repository;

import com.icterra.midas.ae.queryretrieve.model.CrawlerStatus;
import org.springframework.data.mongodb.repository.MongoRepository;



public interface CrawlerStatusRepository extends MongoRepository<CrawlerStatus,String> {
    CrawlerStatus findCrawlerStatusByIndexAndPatientNumberAndDate(String excel_index,String patientId,String date);
}