package net.ffxml.fftags;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import net.ffxml.fftags.ParamMapFactory.ParamType;

public class ParamMap {

	private Map<String, Object> valueMap;
	private Map<String, ParamType<?>> paramTypeMap;
	private HashMap<String, String> stringConvertedMap;
	private ParamMapFactory factory;

	ParamMap(HashMap<String, Object> valueMap,
			Map<String, ParamType<?>> paramTypeMap, ParamMapFactory factory) {
		this.valueMap = valueMap;
		this.paramTypeMap = paramTypeMap;
		this.factory = factory;
	}

	public Map<String, Object> getValueMap() {
		return Collections.unmodifiableMap(valueMap);
	}

	public String getString(String name) {
		return (String) valueMap.get(name);
	}

	public int getInt(String name) {
		return (Integer) valueMap.get(name);
	}
	
	public long getLong(String name) {
		return (Long) valueMap.get(name);
	}
	
	public boolean getBoolean(String name) {
		return (Boolean) valueMap.get(name);
	}
	
	public void setString(String name, String value) {
		valueMap.put(name, value);
	}
	
	public void setInt(String name, int value) {
		valueMap.put(name, value);
	}
	
	public void setLong(String name, long value) {
		valueMap.put(name, value);
	}
	
	public void setBoolean(String name, boolean value) {
		valueMap.put(name, value);
	}
	
	public Boolean getNullableBoolean(String name) {
		return (Boolean) valueMap.get(name);
	}

	Map<String, String> getNonDefaultStringConvertedParamMap() {
		if (stringConvertedMap == null) {
			stringConvertedMap = new HashMap<String, String>();
			for (ParamType<?> paramType : paramTypeMap.values()) {
				String name = paramType.getName();
				String stringValue = paramType.convertToString(valueMap
						.get(paramType.getName()));
				if (!factory.isDefaultValue(name, stringValue)) {
					stringConvertedMap.put(name, stringValue);
				}
			}
		}
		return stringConvertedMap;
	}

	/**
	 * Returns this ParamMap as QueryString.
	 * 
	 * @param encoding The encoding to use.
	 * @return The query-string.
	 */
	public String toQueryString(String encoding) {
		try {
			Map<String, String> stringMap = getNonDefaultStringConvertedParamMap();
			StringBuilder queryString = new StringBuilder();
			for (String name : stringMap.keySet()) {
				if(queryString.length() > 0) {
					queryString.append("&");
				}
				queryString.append(URLEncoder.encode(name, encoding));
				queryString.append("=");
				queryString.append(URLEncoder.encode(stringMap.get(name),
						encoding));
			}
			return queryString.toString();
		} catch (UnsupportedEncodingException e) {
			throw new RuntimeException(e);
		}
	}
}
