package com.uttpal.schedular.controller;

import com.uttpal.schedular.model.CreateScheduleRequest;
import com.uttpal.schedular.service.ScheduleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;


/**
 * @author Uttpal
 */
@RestController
@RequestMapping("/schedule")
public class ScheduleController {

    private ScheduleService scheduleService;

    @Autowired
    public ScheduleController(ScheduleService scheduleService) {
        this.scheduleService = scheduleService;
    }

    @PostMapping("")
    public String create(@Valid @RequestBody CreateScheduleRequest createScheduleRequest) {
        return scheduleService.schedule(createScheduleRequest);
    }
}
