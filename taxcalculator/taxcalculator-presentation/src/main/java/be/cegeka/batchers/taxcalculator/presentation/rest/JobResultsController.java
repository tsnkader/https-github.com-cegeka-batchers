package be.cegeka.batchers.taxcalculator.presentation.rest;

import be.cegeka.batchers.taxcalculator.batch.domain.JobResult;
import be.cegeka.batchers.taxcalculator.batch.service.JobResultsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

@Controller
public class JobResultsController {
    @Autowired
    private JobResultsService jobResultsService;

    @RequestMapping(value = "jobResults", method = RequestMethod.GET)
    @ResponseBody
    public List<JobResult> getJobResults() {
        return jobResultsService.getFinishedJobResults();
    }
}