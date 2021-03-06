package be.cegeka.batchers.taxcalculator.batch.processor;

import be.cegeka.batchers.taxcalculator.application.domain.Employee;
import be.cegeka.batchers.taxcalculator.application.service.TaxCalculatorService;
import be.cegeka.batchers.taxcalculator.batch.domain.TaxCalculation;
import org.joda.money.Money;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.listener.StepExecutionListenerSupport;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import static be.cegeka.batchers.taxcalculator.infrastructure.config.Environment.getCurrentEnvironment;

@Component
@StepScope
public class CalculateTaxProcessor extends StepExecutionListenerSupport implements ItemProcessor<Employee, TaxCalculation> {
    private static final Logger LOG = LoggerFactory.getLogger(CalculateTaxProcessor.class);

    @Autowired
    private TaxCalculatorService taxCalculatorService;
    @Value("#{jobParameters[year]}")
    private Long year;
    @Value("#{jobParameters[month]}")
    private Long month;

    private StepExecution stepExecution;

    @Override
    public TaxCalculation process(Employee employee) {
        LOG.info("Tax process {} on {} ", employee, getCurrentEnvironment() + getPartitionIfExists());
        Money calculatedTax = taxCalculatorService.calculateTax(employee);
        return TaxCalculation.from(stepExecution.getJobExecutionId(), employee, year.intValue(), month.intValue(), calculatedTax);
    }

    @Override
    public void beforeStep(StepExecution stepExecution) {
        this.stepExecution = stepExecution;
    }

    private String getPartitionIfExists() {
        ExecutionContext executionContext = stepExecution.getExecutionContext();
        if (executionContext != null) {
            Object partitionObject = executionContext.get("partition");
            if (partitionObject != null) {
                Long partition = (Long) partitionObject;
                if (partition != null) {
                    return " Partition " + partition;
                }
            }
        }
        return "";
    }
}
