package com.rogers.remip;

import org.mule.runtime.api.connection.ConnectionException;
import org.mule.runtime.api.connection.ConnectionProvider;
import org.mule.runtime.api.connection.ConnectionValidationResult;
import org.mule.runtime.extension.api.annotation.param.ParameterGroup;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.rogers.backend.services.SessionPoolFactory;
import com.rogers.remip.constants.ConnectorConstants;
import com.rogers.remip.datatypes.api.JoltConnectionDetails;

/**
 * The JoltConnectionProvider program implements the provider class for the mule
 * runtime.
 *
 * @author Rogers ESI/REMIP team
 * @version 1.0
 * @since 2020-11-20
 */
public class JoltConnectionProvider implements ConnectionProvider<JoltConnection> {	
	private static Logger LOGGER = LoggerFactory.getLogger(JoltConnectionProvider.class);

	
	@ParameterGroup(name = "Connection")
	JoltConnectionDetails config;

	/**
	 * This method invoke the JoltConnection class method to create the jolt
	 * connection pool.
	 * 
	 */
	@Override
	public JoltConnection connect() throws ConnectionException {
		return new JoltConnection(config);
	}

	/**
	 * This method invoke the JoltConnection class method to disconnect the jolt
	 * connection pool.
	 * 
	 */
	@Override
	public void disconnect(JoltConnection connection) {
		// TODO need to check how to destroy the connection with Boris

	}

	/**
	 * This method test the Jolt connection with config parameters such as poolName,
	 * primaryAddress, timeout, operatorId, maxConn etc.
	 * 
	 * @param connection connection obj with all config parameter
	 *
	 */
	@Override
	public ConnectionValidationResult validate(JoltConnection connection) {
		ConnectionValidationResult result = null;
		String poolName = System.getProperty(ConnectorConstants.poolName);
		try {
			SessionPoolFactory.createConnectionPool(poolName);
			LOGGER.info("Jolt connection pool has been created.");
			result = ConnectionValidationResult.success();
		} catch (Exception e) {
			LOGGER.error("Jolt connection pool creation failed with exception - " + e);
			result = ConnectionValidationResult.failure("Jolt connection pool creation failed.", e);
		}

		return result;
	}

}
