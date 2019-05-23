package com.portal.student.controller;

import java.util.logging.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import com.portal.student.bean.Order;
import com.portal.student.bean.Student;
import com.portal.student.service.OrderServiceDeligate;

/**
 * Student Controller
 * @author Kuldeep
 *
 */
@RestController
public class StudentController {

	private static final Logger LOG = Logger.getLogger(StudentController.class.getName());
	 
	@Autowired
	Environment environment;
	
	@Autowired
	OrderServiceDeligate orderServiceDeligate;
	
	@RequestMapping(value = "/getstudent", method = RequestMethod.GET)
    public Student getStudents() {
		
		LOG.info("inside getStudents");
		Student student = new Student();
		student.setId(100);
		Order ord = orderServiceDeligate.callOrderService();
		student.setName("test student port no :"+ environment.getProperty("server.port"));
		student.setOrderNo(ord.getOrderNo());
		return student;
    }
	
	@RequestMapping(value = "/getadmin", method = RequestMethod.GET)
    public Student getadmin() {
		
		LOG.info("inside getStudents");
		Student student = new Student();
		student.setId(100);
		Order ord = orderServiceDeligate.callOrderService();
		student.setName("test student port no :"+ environment.getProperty("server.port"));
		student.setOrderNo(ord.getOrderNo());
		return student;
    }
}
