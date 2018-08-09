package com.sequence.generator.controller;

import com.sequence.generator.service.SequenceGenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author wangmeng
 */

@RestController
public class SequenceGenController {

    @Autowired
    SequenceGenService sequenceGenService;

    @GetMapping(value = "/genId")
    public String genId(){
        return sequenceGenService.doWork("192.168.1.5");
    }

}
