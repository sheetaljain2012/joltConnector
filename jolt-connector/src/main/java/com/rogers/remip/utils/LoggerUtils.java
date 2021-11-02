package com.rogers.remip.utils;

import java.lang.reflect.Field;

import java.util.Iterator;
import java.util.Set;

import org.apache.commons.lang3.reflect.FieldUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.rogers.remip.JoltOperation;
import com.rogers.remip.constants.ConnectorConstants;

import bea.jolt.pool.Result;

public class LoggerUtils {
	
	private static Logger LOGGER = LoggerFactory.getLogger(LoggerUtils.class);
		
	@SuppressWarnings("rawtypes")
	public static String printResponse(Class clazz, Object obj) {
		StringBuffer strBuffer = new StringBuffer();
		
		try {
			Field field = (Field) clazz.getDeclaredField(ConnectorConstants.out);
			field.setAccessible(true);
			Result result = (Result) field.get(obj);
			
			Set resultSet = result.keySet();
			Iterator iterator = resultSet.iterator();
			
			strBuffer.append(FieldUtils.readField(obj, "traceOutput", true));
	        while (iterator.hasNext()) {
	        	String key = (String) iterator.next();
	        	String paddedkey = padRightWithSpace(key);
	        	for (int i = 0; i < result.getCount(key); i++) {
	        		if (!"".equals(result.getValue(key, i, null).toString().trim()))
	        			strBuffer.append("\n  " + i + " " + paddedkey + result.getValue(key, i, null));
	        	}
	        }
	        strBuffer.append("\n");
		} catch (Exception ex) {
			LOGGER.error("Exception - " + ex);
		}
        
		return strBuffer.toString();
	}

	private static String padRightWithSpace(String key) {
	    if (key.length() >= 33) {
	        return key;
	    }
	    StringBuilder sb = new StringBuilder();
	    while (sb.length() < (33 - key.length())) {
	        sb.append(' ');
	    }
	    
	    sb.insert(0, key);
	    
	    return sb.toString();
	}
}
