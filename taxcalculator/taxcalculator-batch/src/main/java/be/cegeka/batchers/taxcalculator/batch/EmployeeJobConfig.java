package be.cegeka.batchers.taxcalculator.batch;

import be.cegeka.batchers.taxcalculator.domain.Employee;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.item.database.JpaPagingItemReader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import javax.persistence.EntityManagerFactory;

@Configuration
@EnableBatchProcessing
@ComponentScan(basePackages = "be.cegeka.batchers.taxcalculator.batch")
public class EmployeeJobConfig {

    @Autowired
    private JobBuilderFactory jobBuilders;

    @Autowired
    private StepBuilderFactory stepBuilders;

    @Autowired
    private EmployeeReader reader;

    @Autowired
    private EmployeeProcessor processor;

    @Autowired
    private EntityManagerFactory entityManagerFactory;

    @Autowired
    private EmployeeWriter writer;

    @Bean
    public JpaPagingItemReader<Employee> employeeItemReader() {
        JpaPagingItemReader<Employee> employeeItemReader = new JpaPagingItemReader<>();
        employeeItemReader.setEntityManagerFactory(entityManagerFactory);
        employeeItemReader.setQueryString(Employee.GET_ALL_QUERY);
        return employeeItemReader;
    }

    @Bean
    public Job employeeJob(){
        return jobBuilders.get("employeeJob")
                .start(step())
                .build();
    }

    @Bean
    public Step step(){
        return stepBuilders.get("step")
                .<Employee,Employee>chunk(1)
                .reader(employeeItemReader())
                .processor(processor)
                .writer(writer)
                .build();
    }
}
