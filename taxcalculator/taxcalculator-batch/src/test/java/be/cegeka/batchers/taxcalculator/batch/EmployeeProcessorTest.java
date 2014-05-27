package be.cegeka.batchers.taxcalculator.batch;

import static org.fest.assertions.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.joda.money.CurrencyUnit;
import org.joda.money.Money;
import org.joda.time.DateTime;
import org.joda.time.Interval;
import org.joda.time.LocalDate;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.batch.core.StepExecution;

import be.cegeka.batchers.taxcalculator.application.domain.Employee;
import be.cegeka.batchers.taxcalculator.application.domain.EmployeeBuilder;
import be.cegeka.batchers.taxcalculator.application.domain.TaxCalculation;
import be.cegeka.batchers.taxcalculator.application.service.RunningTimeService;
import be.cegeka.batchers.taxcalculator.application.service.TaxCalculatorService;

@RunWith(MockitoJUnitRunner.class)
public class EmployeeProcessorTest {

	public static final double DELTA = 1e-15;
	private Interval interval;

	@InjectMocks
	private CalculateTaxProcessor calculateTaxProcessor;

	@Mock
	private StepExecution stepExecution;

	@Before
	public void setUp() {
		calculateTaxProcessor.taxCalculatorService = createTaxCalculatorService();
		DateTime now = new DateTime();
		LocalDate today = now.toLocalDate();
		LocalDate tomorrow = today.plusDays(1);
		interval = new Interval(today.toDateTimeAtStartOfDay(), tomorrow.toDateTimeAtStartOfDay());

		when(stepExecution.getJobExecutionId()).thenReturn(1L);
	}

	@Test
	public void whenAnEmployeeWithoutCalculatedTax_isProcessed_thenTaxIsOnlyPercentOfCurrentIncome() throws Exception {
		Employee employee1 = new EmployeeBuilder().withIncome(1000).withId(1L).build();
		Employee employee2 = new EmployeeBuilder().withIncome(1500).withId(1L).build();

		TaxCalculation taxCalculation1 = calculateTaxProcessor.process(employee1);
		TaxCalculation taxCalculation2 = calculateTaxProcessor.process(employee2);

		assertThat(taxCalculation1.getTax()).isEqualTo(Money.of(CurrencyUnit.EUR, 100));
		assertThat(taxCalculation1.getCalculationDate()).isNotNull();

		assertThat(taxCalculation2.getTax()).isEqualTo(Money.of(CurrencyUnit.EUR, 150));
		assertThat(taxCalculation2.getCalculationDate()).isNotNull();
	}

	public TaxCalculatorService createTaxCalculatorService() {
		TaxCalculatorService taxCalculatorService = new TaxCalculatorService();
		taxCalculatorService.setRunningTimeService(mock(RunningTimeService.class));
		return taxCalculatorService;
	}
}
