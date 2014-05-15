package be.cegeka.batchers.taxcalculator.presentation.config;

import be.cegeka.batchers.taxcalculator.application.config.EmployeeGeneratorApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

@Configuration
@EnableWebMvc
@ComponentScan("be.cegeka.batchers.taxcalculator")
@Import(EmployeeGeneratorApplicationContext.class)
public class WebAppContext {
}