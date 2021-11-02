package com.rogers.remip;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.rogers.backend.services.SessionPoolFactory;
import com.rogers.remip.constants.ConnectorConstants;
import com.rogers.remip.datatypes.api.JoltConnectionDetails;
import com.rogers.utils.SystemUtils;

/**
 * The JoltConnection program implements an connection initialization logic to
 * Tuxedo via Jolt library. This class will be invoke via mule runtime during
 * deployment to establish the jolt connection pool.
 *
 * @author Rogers ESI/REMIP team
 * @version 1.0
 * @since 2020-11-20
 */
public class JoltConnection {
	private static Logger LOGGER = LoggerFactory.getLogger(JoltConnection.class);

	private static boolean flag = true;

	/**
	 * Constructor for this class.
	 *
	 * @param config Required config parameters such as poolName, primaryAddress,
	 *               timeout, operatorId, maxConn to setup Jolt connection.
	 */
	public JoltConnection(JoltConnectionDetails config) {
		if (flag) {
			setConntionPool(config);
			flag = false;
		}
	}

	/**
	 * This method create the jolt connection pool via Jolt library.
	 *
	 * @param config Required config parameters such as poolName, primaryAddress,
	 *               timeout, operatorId, maxConn to setup Jolt connection.
	 */
	private void setConntionPool(JoltConnectionDetails config) {
		LOGGER.info("==================== Init Application Start =====================");

		initSystemProperty(ConnectorConstants.poolName, config.getPoolName(), true);
		initSystemProperty(ConnectorConstants.primaryAddress, "//" + config.getHost() + ":" + config.getPort(), true);
		initSystemProperty(ConnectorConstants.failoverAddress, "//" + config.getHost() + ":" + config.getPort(), false);
		initSystemProperty(ConnectorConstants.timeout, config.getTimeout(), true);
		initSystemProperty(ConnectorConstants.maxConn, config.getMaxConn(), true);
		

		String poolName = System.getProperty("api.jolt.pool.name");
		try {
			SessionPoolFactory.createConnectionPool(poolName);
			LOGGER.info("Jolt connection pool has been created.");
		} catch (Exception e) {
			LOGGER.error("Jolt connection pool creation failed with exception - " + e);
			throw new RuntimeException("Jolt connection pool creation failed.");
		}

		initSystemProperty(ConnectorConstants.operatorId, config.getOperatorId(), true);
		initSystemProperty(ConnectorConstants.transactionMode, config.getTransactionMode(), true);
		initSystemProperty(ConnectorConstants.envCode, config.getEnvCode(), true);
		initSystemProperty(ConnectorConstants.marketCode, config.getMarketCode(), true);

		LOGGER.info("===================== Init Application End =====================");
	}

	/**
	 * This method set the property into system environment variable.
	 * In the property is optional then ignore else throw exception.
	 *
	 * @param propName Key to search/set the value from Env Vairable.
	 * @param argPropValue Value for the key to set to Env Vairable
	 * @param mandatory to notify if it is mandatory or not.
	 * 
	 */
	private void initSystemProperty(String propName, String argPropValue, boolean mandatory) {
		String propValue = System.getProperty(propName);
		if (SystemUtils.isEmpty(propValue)) {
			if (SystemUtils.isEmpty(argPropValue)) {
				if (mandatory)
					throw new RuntimeException("Propety " + propName + " isn't initialized.");
				else
					System.setProperty(propName, "");
			} else {
				System.setProperty(propName, argPropValue);
			}
		}

	}
}
