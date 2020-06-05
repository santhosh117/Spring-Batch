package com.project.batch.Controller;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;

@RestController
public class PersonController implements ErrorController {

    private static final String PATH = "/error";

    @RequestMapping(value = PATH)
    public String error() {
        return "Error handling";
    }

    @Override
    public String getErrorPath() {
        return PATH;
    }

    @Autowired
    JobLauncher jobLauncher;

    @Autowired
    @Qualifier("personJob")
    Job personDetailJob;

    @GetMapping("/")
    public String home() {
        return "Home page";
    }

    @GetMapping("/run-batch-job")
    public String handle() throws Exception {
        JobParametersBuilder builder = new JobParametersBuilder();
        builder.addDate("date", new Date());
        jobLauncher.run(personDetailJob, builder.toJobParameters());
        return "Batch job has been invoked";
    }
}