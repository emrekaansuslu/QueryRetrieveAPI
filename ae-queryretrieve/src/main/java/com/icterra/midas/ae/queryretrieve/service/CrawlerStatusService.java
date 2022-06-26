package com.icterra.midas.ae.queryretrieve.service;

import com.icterra.midas.ae.queryretrieve.model.CrawlerStatus;
import com.icterra.midas.ae.queryretrieve.repository.CrawlerStatusRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CrawlerStatusService {

    @Autowired
    CrawlerStatusRepository crawlerStatusRepository;

    public CrawlerStatus updateCrawlerStatus(CrawlerStatus crawlerStatus) {
        CrawlerStatus cs = crawlerStatusRepository.findCrawlerStatusByIndexAndPatientNumberAndDate(crawlerStatus.getIndex(),crawlerStatus.getPatientNumber(),crawlerStatus.getDate());

        if(cs != null) {
            cs.setIndex(crawlerStatus.getIndex());
            cs.setStatus(crawlerStatus.getStatus());
            cs.setPatientNumber(crawlerStatus.getPatientNumber());
            return crawlerStatusRepository.save(cs);
        } else {
            return crawlerStatusRepository.save(crawlerStatus);
        }
    }


}
