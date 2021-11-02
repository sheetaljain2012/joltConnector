package com.rogers.remip;

import static org.mule.runtime.extension.api.annotation.param.MediaType.ANY;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.mule.runtime.extension.api.annotation.Alias;
import org.mule.runtime.extension.api.annotation.param.Connection;
import org.mule.runtime.extension.api.annotation.param.MediaType;
import org.mule.runtime.extension.api.annotation.param.ParameterGroup;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.rogers.remip.constants.ConnectorConstants;
import com.rogers.remip.datatypes.api.JoltParameters;
import com.rogers.remip.datatypes.api.JoltResponseHolder;
import com.rogers.remip.utils.LoggerUtils;

/**
 * The JoltOperation program implements an mule connector operation which will
 * handle any Tuxedo jolt API request, call Tuxedo service and then return back
 * the Tuxedo response to calling client .
 *
 * @author Rogers ESI/REMIP team
 * @version 1.0
 * @since 2020-11-20
 */
public class JoltOperation {

	private static Logger LOGGER = LoggerFactory.getLogger(JoltOperation.class);

	/**
	 * Returns an Jolt response object and Status code as Failed or Success for the
	 * transaction.
	 * <p>
	 * The JoltConnection object will be initialized by the mule internally by
	 * provided connection arguments.
	 * <p>
	 * The apiClass name in JoltParameters argument should contain the serviceName
	 * of Tuxedo appended with 00 in the end.
	 * <p>
	 * The map in JoltParameters argument must send the elements name matching to
	 * the OLR elements names.
	 * <p>
	 * This method always returns response immediately (Synchronous), Either success
	 * or fail. It uses the reflection class to generate the Tuxudo class, methods
	 * dynamically.
	 *
	 * @param conn   connection object initialized by mule container during
	 *               deployment
	 * @param params Include ApiClassName and Map with input element key/value pair.
	 * @return tuxedo response object with status code
	 */
	@MediaType(value = ANY, strict = false)
	@Alias("Retrieve")
	public JoltResponseHolder processRetrieveRequest(@Connection JoltConnection conn,
			@ParameterGroup(name = "Request Parameters") JoltParameters params) throws Exception {

		LOGGER.info("Retrieve Connector Operation Start");
		JoltResponseHolder resp = new JoltResponseHolder();
		int occurrence = 0;
		try {

			Class<?> clazz = Class.forName(ConnectorConstants.packageName + params.getServiceName());
			Object obj = clazz.newInstance();
          
			// To get all key: value
			for (Object key : params.getRequest().keySet()) {
				
				Object value = params.getRequest().get((String) key);
				Method method = null;
				if (value != null) {
					try {
						if (value instanceof java.lang.String) {
							setStringMethods(key, value, obj, clazz);
						} else if (value instanceof java.lang.Integer) {
							setNumberMethods(key, value, obj, clazz);
						} else if (value instanceof java.lang.Double) {
							method = clazz.getMethod(ConnectorConstants.set + key, (new Class[] { double.class }));
							method.invoke(obj, value);
						} else if (value instanceof ArrayList) {
							setArrayMethods(key, value, obj, clazz, occurrence);
							occurrence = 0;
						} else {
							throw new Exception("Key type is not recognized - " + value.getClass());
						}

					} catch (Exception ex) {
						LOGGER.error("Exception - " + ex);
						throw ex;
					}
				}

			}
			
			clazz.getMethod(ConnectorConstants.call).invoke(obj);
			LOGGER.info("Response from Tuxedo -  \n"+FieldUtils.readField(obj, "traceInput", true) + "\n" +LoggerUtils.printResponse(clazz, obj));
			
			
			resp.setResponseStatus("Success");
			resp.setObj(obj);

			LOGGER.info("Retrieve Connector Operation End"); 
			
			return resp;

		} catch (Exception ex) {
			LOGGER.error("Exception - " + ex);
			throw ex;
		}
	}
	
	/**
	 * To be implemented in future
	 */
	@MediaType(value = ANY, strict = false)
	@Alias("Update")
	public JoltResponseHolder processUpdateRequest(@Connection JoltConnection conn,
			@ParameterGroup(name = "requestParams") JoltParameters params) throws Exception {

		// To be Implemented in future for update transaction
		return null;
	}
	
	/**
	 * This method handle the String and Character dataType mapping to Tuxedo
	 * Method.
	 *
	 * @param key   The Tuxedo element Name from OLR
	 * @param value The value for the tuxedo method parameter.
	 * @param obj   The ApiClass Name object to set the method with Parameter.
	 * @param clazz The ApiClass Name to find the method in the class.
	 */
	private void setStringMethods(Object key, Object value, Object obj, Class<?> clazz) throws Exception {
		Method method = null;
		try {
			method = clazz.getMethod(ConnectorConstants.set + key, (new Class[] { String.class }));
			method.invoke(obj, value);
		} catch (Exception ex) {
			try {
				method = clazz.getMethod(ConnectorConstants.set + key, (new Class[] { byte.class }));
				method.invoke(obj, ((String) value).getBytes()[0]);
			} catch (Exception e) {
				throw e;
			}
		}
	}
	
	/**
	 * This method handle the Integer and Short dataType mapping to Tuxedo Method.
	 *
	 * @param key   The Tuxedo element Name from OLR
	 * @param value The value for the tuxedo method parameter.
	 * @param obj   The ApiClass Name object to set the method with Parameter.
	 * @param clazz The ApiClass Name to find the method in the class.
	 */
	private void setNumberMethods(Object key, Object value, Object obj, Class<?> clazz) throws Exception {
		Method method = null;
		try {
			method = clazz.getMethod(ConnectorConstants.set + key, (new Class[] { int.class }));
			method.invoke(obj, value);
		} catch (Exception ex) {
			try {
				method = clazz.getMethod(ConnectorConstants.set + key, (new Class[] { short.class }));
				method.invoke(obj, ((Integer) value).shortValue());
			} catch (Exception exc) {
				throw exc;
			}

		}
	}

	/**
	 * This method handle the Array dataType mapping to Tuxedo Method.
	 *
	 * @param key        The Tuxedo element Name from OLR
	 * @param value      The value for the tuxedo method parameter.
	 * @param obj        The ApiClass Name object to set the method with Parameter.
	 * @param clazz      The ApiClass Name to find the method in the class.
	 * @param occurrence The current position in the array.
	 */
	private void setArrayMethods(Object key, Object value, Object obj, Class<?> clazz, int occurrence)
			throws Exception {

		@SuppressWarnings("unchecked")
		List<Object> objects = (ArrayList<Object>) value;

		for (int i = 0; i < objects.size(); i++) {
			if (objects.get(i) instanceof java.lang.String) {
				setArrayStringMethods(key, objects.get(i), obj, clazz, occurrence);
				occurrence++;
			} else if (objects.get(i) instanceof java.lang.Integer) {
				setArrayNumberMethods(key, objects.get(i), obj, clazz, occurrence);
				occurrence++;
			} else if (objects.get(i) instanceof java.lang.Double) {
				Method method = clazz.getMethod(ConnectorConstants.set + key,
						(new Class[] { double.class, int.class }));
				method.invoke(obj, (double) value, occurrence++);
			}
		}

	}

	/**
	 * This method handle the String and Character dataType mapping to Tuxedo
	 * Method for Array object.
	 *
	 * @param key   The Tuxedo element Name from OLR
	 * @param value The value for the tuxedo method parameter.
	 * @param obj   The ApiClass Name object to set the method with Parameter.
	 * @param clazz The ApiClass Name to find the method in the class.
	 * @param occurrence The current position in the array.
	 */
	private void setArrayStringMethods(Object key, Object value, Object obj, Class<?> clazz, int occurrence)
			throws Exception {
		Method method = null;
		try {
			method = clazz.getMethod(ConnectorConstants.set + key, (new Class[] { String.class, int.class }));
			method.invoke(obj, value, occurrence);
		} catch (Exception ex) {
			try {
				method = clazz.getMethod(ConnectorConstants.set + key, (new Class[] { byte.class, int.class }));
				method.invoke(obj, ((String) value).getBytes()[0], occurrence);
			} catch (Exception e) {
				throw e;
			}
		}
	}


	/**
	 * This method handle the Integer and Short dataType mapping to Tuxedo
	 * Method for Array object.
	 *
	 * @param key   The Tuxedo element Name from OLR
	 * @param value The value for the tuxedo method parameter.
	 * @param obj   The ApiClass Name object to set the method with Parameter.
	 * @param clazz The ApiClass Name to find the method in the class.
	 * @param occurrence The current position in the array.
	 */
	private void setArrayNumberMethods(Object key, Object value, Object obj, Class<?> clazz, int occurrence)
			throws Exception {
		Method method = null;
		try {
			method = clazz.getMethod(ConnectorConstants.set + key, (new Class[] { int.class, int.class }));
			method.invoke(obj, value, occurrence);
		} catch (Exception ex) {
			try {

			} catch (Exception e) {
				try {
					method = clazz.getMethod(ConnectorConstants.set + key, (new Class[] { short.class, int.class }));
					method.invoke(obj, (short) value, occurrence);
				} catch (Exception exc) {
					throw exc;
				}
			}

		}
	}

}
