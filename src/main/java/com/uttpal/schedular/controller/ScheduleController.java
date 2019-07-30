package com.uttpal.schedular.controller;

import com.uttpal.schedular.model.CancelScheduleRequest;
import com.uttpal.schedular.model.CreateScheduleRequest;
import com.uttpal.schedular.service.ScheduleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

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


    @DeleteMapping("")
    public String cancel(@Valid @RequestBody CancelScheduleRequest cancelScheduleRequest) {
        return scheduleService.cancel(cancelScheduleRequest.getClientId(), cancelScheduleRequest.getScheduleKey());
    }
}
