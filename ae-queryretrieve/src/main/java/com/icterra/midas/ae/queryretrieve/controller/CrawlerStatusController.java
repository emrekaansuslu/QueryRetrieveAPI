package com.icterra.midas.ae.queryretrieve.controller;

import com.icterra.midas.ae.queryretrieve.model.CrawlerStatus;
import com.icterra.midas.ae.queryretrieve.repository.CrawlerStatusRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@CrossOrigin(origins = "http://localhost:3000")
@RestController
@RequestMapping("api/")
public class CrawlerStatusController {

    @Autowired
    private CrawlerStatusRepository crawlerStatusRepository;

    @GetMapping("crawlerStatus")
    public List<CrawlerStatus> getCrawlerStatusList() {
        return this.crawlerStatusRepository.findAll();
    }
}
