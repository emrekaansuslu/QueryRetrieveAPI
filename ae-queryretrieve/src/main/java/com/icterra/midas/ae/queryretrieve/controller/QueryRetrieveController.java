package com.icterra.midas.ae.queryretrieve.controller;

import com.icterra.midas.ae.queryretrieve.QueryRetrieveAE;
import com.icterra.midas.ae.queryretrieve.model.NetworkSettings;
import com.icterra.midas.ae.queryretrieve.model.QueryRetrieveFindResult;
import com.icterra.midas.ae.queryretrieve.model.ServiceStatusResult;
import com.icterra.midas.ae.queryretrieve.repository.NetworkSettingsRepository;
import com.icterra.midas.ae.queryretrieve.service.NetworkSettingsService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Slf4j
public class QueryRetrieveController {

    @Autowired
    private QueryRetrieveAE queryRetrieveAE;

    @GetMapping("/")
    String home() {
        return "Hello MIDAS QR !";
    }

    @GetMapping("/query")
    @ResponseBody
    public List<QueryRetrieveFindResult> query(@RequestParam(name="patientId") String patientId,
                                               @RequestParam(name="studyDate")String studyDate)
    {
        log.info("query: patientId - " + patientId + " studydate - " + studyDate);
        return queryRetrieveAE.query(studyDate, patientId);
    }

    @GetMapping("/retrieve")
    @ResponseBody
    public List<ServiceStatusResult> retrieve(@RequestParam(name="queryResultList") List<QueryRetrieveFindResult> queryRetrieveFindResult)
    {
        log.info("retrieve");
        return queryRetrieveAE.retrieve(queryRetrieveFindResult);
    }

}
