package net.ffxml.fftags;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.tagext.JspFragment;
import javax.servlet.jsp.tagext.SimpleTagSupport;

import org.apache.commons.lang.StringEscapeUtils;

public class UrlTag extends SimpleTagSupport {

	private String paramMapName = "paramMap";

	private String value;

	private String var;

	private HashMap<String, String> paramMap;

	private boolean generateHiddenFields;

	public boolean isGenerateHiddenFields() {
		return generateHiddenFields;
	}

	public void setGenerateHiddenFields(boolean generateHiddenFields) {
		this.generateHiddenFields = generateHiddenFields;
	}

	public String getParamMapName() {
		return paramMapName;
	}

	public void setParamMapName(String paramMapName) {
		this.paramMapName = paramMapName;
	}

	public String getVar() {
		return var;
	}

	public void setVar(String var) {
		this.var = var;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	@SuppressWarnings("unchecked")
	@Override
	public void doTag() throws JspException, IOException {
		PageContext pageContext = (PageContext) getJspContext();
		Object paramContainer = pageContext.getAttribute(paramMapName,
				PageContext.REQUEST_SCOPE);

		Map<String, String> origParamMap = null;
		if (paramContainer instanceof Map) {
			origParamMap = (Map<String, String>) paramContainer;
		} else if (paramContainer instanceof ParamMap) {
			origParamMap = ((ParamMap) paramContainer)
					.getNonDefaultStringConvertedParamMap();
		}

		paramMap = new HashMap<String, String>();
		if (origParamMap != null) {
			paramMap.putAll(origParamMap);
		}

		JspFragment jspBody = getJspBody();
		if (jspBody != null) {
			jspBody.invoke(null);
		}

		StringBuilder content = new StringBuilder();
		if (generateHiddenFields) {
			content = generateHiddenFields(pageContext);
		} else {
			content = generateUrl(pageContext);
		}

		if (var != null && !var.trim().isEmpty()) {
			getJspContext().setAttribute(var, content.toString());
		} else {
			getJspContext().getOut().print(content.toString());
		}
	}

	private StringBuilder generateHiddenFields(PageContext pageContext) {
		StringBuilder content = new StringBuilder();
		for (String name : paramMap.keySet()) {
			content.append("<input type=\"hidden\" name=\"");
			content.append(StringEscapeUtils.escapeHtml(name));
			content.append("\" value=\"");
			content.append(StringEscapeUtils.escapeHtml(paramMap.get(name)));
			content.append("\" />\n");
		}
		return content;
	}

	private StringBuilder generateUrl(PageContext pageContext)
			throws JspException, IOException, UnsupportedEncodingException {
		StringBuilder url = new StringBuilder();
		String contextPath = pageContext.getServletContext().getContextPath();
		if (!"/".equals(contextPath)) {
			url.append(contextPath);
		}
		url.append(value);
		if (paramMap.size() > 0) {
			boolean firstParm = true;

			String enc = pageContext.getResponse().getCharacterEncoding();
			for (String name : paramMap.keySet()) {
				if (firstParm) {
					url.append("?");
				} else {
					url.append("&");
				}
				url.append(URLEncoder.encode(name, enc));
				url.append("=");
				url.append(URLEncoder.encode(paramMap.get(name), enc));
				firstParm = false;
			}
		}
		return url;
	}

	public void addParam(String name, String value, boolean clear) {
		if (clear) {
			if (name.endsWith("*")) {
				String searchString = name.substring(0, name.length() - 1);
				HashSet<String> toRemove = new HashSet<String>();
				for (String paramName : paramMap.keySet()) {
					if (paramName.startsWith(searchString)) {
						toRemove.add(paramName);
					}
				}
				for (String paramName : toRemove) {
					paramMap.remove(paramName);
				}
			} else {
				paramMap.remove(name);
			}
		} else {
			paramMap.put(name, value);
		}
	}
}
