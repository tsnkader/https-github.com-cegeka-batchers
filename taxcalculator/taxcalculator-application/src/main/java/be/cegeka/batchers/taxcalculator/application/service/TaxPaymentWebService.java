package be.cegeka.batchers.taxcalculator.application.service;

import be.cegeka.batchers.taxcalculator.application.domain.Employee;
import be.cegeka.batchers.taxcalculator.application.domain.taxpayment.TaxServiceResponseTo;
import be.cegeka.batchers.taxcalculator.application.domain.taxpayment.TaxTo;
import be.cegeka.batchers.taxcalculator.application.service.exceptions.TaxWebServiceException;
import be.cegeka.batchers.taxcalculator.application.service.exceptions.TaxWebServiceFatalException;
import be.cegeka.batchers.taxcalculator.application.service.exceptions.TaxWebServiceNonFatalException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.joda.money.Money;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

import java.net.URI;

@Service
public class TaxPaymentWebService {

    private static final Logger LOG = LoggerFactory.getLogger(TaxPaymentWebService.class);

    @Autowired
    private String taxServiceUrl;

    @Autowired
    private RestTemplate restTemplate;

    public void doWebserviceCallToTaxService(Employee employee, Money taxesToPay) throws TaxWebServiceException {
        TaxTo taxTo = createWebserviceInput(employee, taxesToPay);

        try {
            URI uri = URI.create(taxServiceUrl);
            ResponseEntity<TaxServiceResponseTo> webserviceResult = restTemplate.postForEntity(uri, taxTo, TaxServiceResponseTo.class);
            TaxServiceResponseTo taxServiceResponseTo = webserviceResult.getBody();

            if (!"OK".equals(taxServiceResponseTo.getStatus())) {
                throw new TaxWebServiceNonFatalException(employee, taxesToPay, webserviceResult.getStatusCode(), getJson(taxServiceResponseTo), "invalid response from server");
            }
        } catch (HttpClientErrorException e) {
            throw new TaxWebServiceFatalException(employee, taxesToPay, e.getStatusCode(), e.getResponseBodyAsString(), e);
        } catch (HttpServerErrorException e) {
            throw new TaxWebServiceNonFatalException(employee, taxesToPay, e.getStatusCode(), e.getResponseBodyAsString(), e);
        } catch (ResourceAccessException e) {
            throw new TaxWebServiceNonFatalException(employee, taxesToPay, HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage(), e);
        }
    }

    private String getJson(Object object) {
        try {
            return new ObjectMapper().writeValueAsString(object);
        } catch (JsonProcessingException e) {
            LOG.error(e.getMessage(), e);
            throw new RuntimeException(e);
        }
    }

    private TaxTo createWebserviceInput(Employee employee, Money taxesToPay) {
        TaxTo taxTo = new TaxTo();
        taxTo.setEmployeeId(employee.getId());
        taxTo.setAmount(taxesToPay.getAmount().doubleValue());
        return taxTo;
    }
}
