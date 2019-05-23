package com.portal.student.service;

import java.util.logging.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import com.portal.student.bean.Order;

@Service
public class OrderServiceDeligate {

	private static final Logger LOG = Logger.getLogger(OrderServiceDeligate.class.getName());
	
	@Autowired
    RestTemplate restTemplate;
    
	@HystrixCommand(fallbackMethod = "callStudentServiceAndGetData_Fallback")
    public Order callOrderService()
    {
		LOG.info("Calling orer service.");
        Order response = restTemplate.getForObject("http://order-service/getorder", Order.class);
        return response;
    }
	
	@SuppressWarnings("unused")
    private Order callStudentServiceAndGetData_Fallback() {
 
		LOG.info("Student Service is down!!! fallback route enabled...");
 
        Order order = new Order();
		order.setId(100);
		order.setOrderNo("enabled circuit breaker");
		return order;
    }
     
    @Bean
    @LoadBalanced
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}
