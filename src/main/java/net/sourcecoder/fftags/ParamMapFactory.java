package net.sourcecoder.fftags;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

public class ParamMapFactory {

	private Map<String, ParamType<?>> paramTypeMap = new HashMap<String, ParamType<?>>();

	abstract class ParamType<T> {

		protected String name;
		protected T defaultValue;

		public ParamType(String name, T defaultValue) {
			this.name = name;
			this.defaultValue = defaultValue;
		}

		public String getName() {
			return name;
		}

		public T getDefaultValue() {
			return defaultValue;
		}

		@SuppressWarnings("rawtypes")
		public T extractValueFromRequest(Map requestParamMap) {
			if (requestParamMap.containsKey(name)) {
				String[] valueArr = (String[]) requestParamMap.get(name);
				return convertFromString((String) valueArr[0]);
			} else {
				return getDefaultValue();
			}
		}

		abstract public T convertFromString(String value);

		abstract public String convertToString(Object value);
	}

	class StringParamType extends ParamType<String> {
		public StringParamType(String name, String defaultValue) {
			super(name, defaultValue);
		}

		@Override
		public String convertFromString(String value) {
			return value;
		}

		@Override
		public String convertToString(Object value) {
			return (String) value;
		}
	}

	class IntParamType extends ParamType<Integer> {
		public IntParamType(String name, int defaultValue) {
			super(name, defaultValue);
		}

		@Override
		public Integer convertFromString(String value) {
			return Integer.valueOf(value);
		}

		@Override
		public String convertToString(Object value) {
			return String.valueOf((Integer) value);
		}
	}
	
	class LongParamType extends ParamType<Long> {
		public LongParamType(String name, long defaultValue) {
			super(name, defaultValue);
		}

		@Override
		public Long convertFromString(String value) {
			return Long.valueOf(value);
		}

		@Override
		public String convertToString(Object value) {
			return String.valueOf((Long) value);
		}
	}

	class BooleanParamType extends ParamType<Boolean> {
		public BooleanParamType(String name, boolean defaultValue) {
			super(name, defaultValue);
		}

		@Override
		public Boolean convertFromString(String value) {
			return Boolean.parseBoolean(value);
		}

		@Override
		public String convertToString(Object value) {
			return String.valueOf((Boolean) value);
		}

		@SuppressWarnings("rawtypes")
		@Override
		public Boolean extractValueFromRequest(Map requestParamMap) {
			if (requestParamMap.containsKey(name)) {
				String[] valueArr = (String[]) requestParamMap.get(name);
				return convertFromString((String) valueArr[0]);
			} else if (requestParamMap
					.containsKey(getCheckboxDetectionFieldName())) {
				return false;
			} else {
				return getDefaultValue();
			}
		}

		private String getCheckboxDetectionFieldName() {
			return "_" + name;
		}
	}

	class NullableBooleanParamType extends BooleanParamType {
		public NullableBooleanParamType(String name, Boolean defaultValue) {
			super(name, defaultValue);
		}

		@Override
		public Boolean convertFromString(String value) {
			if (value == null) {
				return null;
			}
			if (value.toLowerCase().equals("false")) {
				return false;
			}
			if (value.toLowerCase().equals("true")) {
				return true;
			}
			return null;
		}

		@Override
		public String convertToString(Object value) {
			if (value == null) {
				return "";
			}
			return String.valueOf((Boolean) value);
		}
	}

	public void addStringParam(String name, String defaultValue) {
		addParam(name, new StringParamType(name, defaultValue));
	}

	public void addIntParam(String name, int defaultValue) {
		addParam(name, new IntParamType(name, defaultValue));
	}
	
	public void addLongParam(String name, long defaultValue) {
		addParam(name, new LongParamType(name, defaultValue));
	}

	/**
	 * Fügt einen boolean-Parameter hinzu. Wenn der Parameter in einem Form mit
	 * einer Checkbox verwendet wird, dann ist folgendes zu tun:
	 * <ul>
	 * <li>
	 * Parameter clearen, damit er nicht zusätzlich als Hidden-Parameter
	 * zurückgegeben wird.
	 * 
	 * <pre>
	 * 	&lt;ff:url generateHiddenFields="true"&gt;
	 * 		...
	 * 		&lt;ff:param name="boolParam" clear="true" /&gt;
	 * 		...
	 * 	&lt;/ff:url&gt;
	 * </pre>
	 * 
	 * </li>
	 * <li>
	 * Ein zusätzliches hidden-Field mit dem Namen des Parameters mit
	 * vorangestelltem "_". Dies dient zur Checkbox erkennung, da eine Checkbox
	 * nichts zurückliefert, wenn diese nicht angehakt ist. Das Vorhandensein des
	 * hidden-Fields signalisiert dem Server, dass die Checkbox sichtbar war,
	 * aber nicht angehakt wurde.
	 * 
	 * <pre>
	 * 	&lt;input type="hidden" name="_boolParam" /&gt;
	 * </pre>
	 * 
	 * </li>
	 * <li>
	 * Die eigentliche Checkbox kann wie folgt definiert werden:
	 * 
	 * <pre>
	 * 			&lt;input type="checkbox" name="boolParam" ${boolParam ? 'checked="checked"' : ''}" value="true" /&gt;
	 * </pre>
	 * 
	 * </li>
	 * </ul>
	 * 
	 * @param name
	 * @param defaultValue
	 */
	public void addBooleanParameter(String name, boolean defaultValue) {
		addParam(name, new BooleanParamType(name, defaultValue));
	}

	public void addNullableBooleanParameter(String name, Boolean defaultValue) {
		addParam(name, new NullableBooleanParamType(name, defaultValue));
	}

	private void addParam(String name, ParamType<?> paramType) {
		if (paramTypeMap.containsKey(name)) {
			throw new RuntimeException("A parameter with the name " + name
					+ " already exists.");
		}
		paramTypeMap.put(name, paramType);
	}

	public ParamMap buildParamMap(HttpServletRequest request) {
		HashMap<String, Object> map = new HashMap<String, Object>();

		@SuppressWarnings("rawtypes")
		Map requestParamMap = request.getParameterMap();
		for (String name : paramTypeMap.keySet()) {
			ParamType<?> paramType = paramTypeMap.get(name);
			map.put(name, paramType.extractValueFromRequest(requestParamMap));
		}
		return new ParamMap(map, paramTypeMap, this);
	}

	public boolean isDefaultValue(String name, String stringValue) {
		ParamType<?> paramType = paramTypeMap.get(name);
		return stringValue.equals(paramType.convertToString(paramType
				.getDefaultValue()));
	}
}
