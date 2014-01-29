package net.ffxml.fftags;

import java.io.IOException;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.SimpleTagSupport;

public class ParamTag extends SimpleTagSupport {

	private String value;

	private String name;

	private boolean clear;

	public String getValue() {
		return value;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public boolean isClear() {
		return clear;
	}

	public void setClear(boolean clear) {
		this.clear = clear;
	}

	@Override
	public void doTag() throws JspException, IOException {
		UrlTag urlTag = (UrlTag) getParent();
		urlTag.addParam(name, value, clear);
	}
}
