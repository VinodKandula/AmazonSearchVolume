package com.sellics.test.controller;


import com.sellics.test.services.SearchService;
import com.sellics.test.calculation.SearchVolumeIterator;
import com.sellics.test.models.Estimation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.http.HttpStatus.GATEWAY_TIMEOUT;
import static org.springframework.http.HttpStatus.OK;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.http.ResponseEntity.status;
import static org.springframework.web.bind.annotation.RequestMethod.GET;


/**
 * @author Chaklader on 2020-02-07
 */
@RestController
public class SearchController {

    private SearchService service;

    @Autowired
    public SearchController(SearchService service) {
        this.service = service;
    }

    /**
     * @param keyword    A required parameter that is going to be used as the search term
     * @param market     optional parameter to select the marketplace by code. If empty it is going to default to the value set in  com.sellics.test.controller.default_mkt
     * @param department optional parameter to filter by department. Defaults to the value set in com.sellics.test.controller.default_department
     *                   This endpoint will run a itterative calculation in the time available as set by om.sellics.test.controller.runningtime_in_nanoseconds. If the time runs out it will return a score but the status code will be 504.
     *                   If the calculation finishes in time, the status will be 200.
     * @return Estimation consisting of the score, and the search term
     */
    @RequestMapping(path = "/estimate", method = GET, produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<Estimation> estimate(@RequestParam(value = "keyword") String keyword,
                                               @RequestParam(value = "market", required = false, defaultValue = "1") String market,
                                               @RequestParam(value = "department", required = false, defaultValue = "aps") String department) {

        SearchVolumeIterator estimationAlgorithm = service.createAlgorithm(keyword, market, department);
        int score = service.tryRun(estimationAlgorithm);

        Estimation estimation = new Estimation(score, keyword);
        HttpStatus status = estimationAlgorithm.hasNext() ? GATEWAY_TIMEOUT : OK;
        return status(status).body(estimation);

    }

}