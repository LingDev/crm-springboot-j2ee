package com.linkdoan.backend.controller;

import com.linkdoan.backend.service.ResultService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ResultController {

    @Autowired
    ResultService resultService;

    @GetMapping(value = "/results")
    public ResponseEntity<?> getResult(@RequestParam(name = "termId") String termId,
                                       @RequestParam(name = "rank") Integer rank) {
        return new ResponseEntity(resultService.getResult(termId, rank), HttpStatus.OK);
    }

}
