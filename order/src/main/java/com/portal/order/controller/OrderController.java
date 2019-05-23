package com.portal.order.controller;

import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import com.portal.order.bean.MyProperties;
import com.portal.order.bean.Order;
import ch.qos.logback.classic.Logger;

/**
 * Order Controller
 * @author Kuldeep
 *
 */
@RestController
public class OrderController {

	private static final Logger LOG = (Logger)LoggerFactory.getLogger(OrderController.class);
	
	@Autowired
	MyProperties myProperties;
	
	@Autowired
	Environment environment;

	@RequestMapping(value = "/getorder", method = RequestMethod.GET)
	public Order getorder() {
		LOG.info("Inside Order Service");
		
		Order order = new Order();
		order.setId(100);
		order.setOrderNo("INV1000" + environment.getProperty("server.port") + "::value::"+myProperties.getTestkey());
		return order;

	}
}
