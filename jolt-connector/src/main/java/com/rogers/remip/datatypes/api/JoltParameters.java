package com.rogers.remip.datatypes.api;

import org.mule.runtime.extension.api.annotation.Expression;
import org.mule.runtime.extension.api.annotation.param.Parameter;
import org.mule.runtime.extension.api.annotation.param.display.DisplayName;

import java.util.Map;

import org.mule.runtime.api.meta.ExpressionSupport;

public class JoltParameters {

	@Parameter
	@Expression(ExpressionSupport.SUPPORTED)
	@DisplayName("Service Name")
	private String serviceName;

	@Parameter
	@Expression(ExpressionSupport.SUPPORTED)
	@DisplayName("Request")
	private Map<String, Object> request;

	public Map<String, Object> getRequest() {
		return request;
	}

	public void setRequest(Map<String, Object> request) {
		this.request = request;
	}

	public String getServiceName() {
		return serviceName;
	}

	public void setServiceName(String serviceName) {
		this.serviceName = serviceName;
	}

	

}
