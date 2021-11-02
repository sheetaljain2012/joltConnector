package com.rogers.remip.datatypes.api;

import org.mule.runtime.extension.api.annotation.param.Optional;
import org.mule.runtime.extension.api.annotation.param.Parameter;

public class JoltConnectionDetails {

	@Parameter
	private String host;

	@Parameter
	private String port;

	@Parameter
	@Optional(defaultValue = "joltPool")
	private String poolName;

	@Parameter
	@Optional(defaultValue = "5")
	private String maxConn;

	@Parameter
	@Optional(defaultValue = "300")
	private String timeout;

	@Parameter
	private String operatorId;

	@Parameter
	@Optional(defaultValue = "ENV26")
	private String envCode;

	@Parameter
	@Optional(defaultValue = "O")
	private String transactionMode;

	@Parameter
	@Optional(defaultValue = "CAN")
	private String marketCode;

	public String getHost() {
		return host;
	}

	public void setHost(String host) {
		this.host = host;
	}

	public String getPort() {
		return port;
	}

	public void setPort(String port) {
		this.port = port;
	}

	public String getTimeout() {
		return timeout;
	}

	public void setTimeout(String timeout) {
		this.timeout = timeout;
	}

	public String getPoolName() {
		return poolName;
	}

	public void setPoolName(String poolName) {
		this.poolName = poolName;
	}

	public String getOperatorId() {
		return operatorId;
	}

	public void setOperatorId(String operatorId) {
		this.operatorId = operatorId;
	}

	public String getEnvCode() {
		return envCode;
	}

	public void setEnvCode(String envCode) {
		this.envCode = envCode;
	}

	public String getTransactionMode() {
		return transactionMode;
	}

	public void setTransactionMode(String transactionMode) {
		this.transactionMode = transactionMode;
	}

	public String getMarketCode() {
		return marketCode;
	}

	public void setMarketCode(String marketCode) {
		this.marketCode = marketCode;
	}

	public String getMaxConn() {
		return maxConn;
	}

	public void setMaxConn(String maxConn) {
		this.maxConn = maxConn;
	}

}
