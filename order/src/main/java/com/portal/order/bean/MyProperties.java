package com.portal.order.bean;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.stereotype.Component;

@RefreshScope
@Component
public class MyProperties {

	@Value("${testkey}")
	private String testkey;

	public String getTestkey() {
		return testkey;
	}

	public void setTestkey(String testkey) {
		this.testkey = testkey;
	}
	
}
