package com.metafour.email.models;

import java.util.List;
import java.util.Map;

public class EmailTemplateData extends Email {

	private String cdnPath;
	private String referenceId;
	private String formSubmitURL;
	private String formDataURL;

	private List<String> customJavascripts;
	private Map<String, String> hiddenFields;
	
	private String errorMsg;
	
	public EmailTemplateData() {
		super();
	}
	
	public EmailTemplateData(String cdnVsno) {
		super();
		this.cdnPath = "//cdn.metafour.com/" + cdnVsno;
	}
	
	public String getCdnPath() {
		return cdnPath;
	}

	public void setCdnPath(String cdnPath) {
		this.cdnPath = cdnPath;
	}

	public String getReferenceId() {
		return referenceId;
	}

	public void setReferenceId(String referenceId) {
		this.referenceId = referenceId;
	}

	public List<String> getCustomJavascripts() {
		return customJavascripts;
	}

	public void setCustomJavascripts(List<String> customJavascripts) {
		this.customJavascripts = customJavascripts;
	}

	public Map<String, String> getHiddenFields() {
		return hiddenFields;
	}

	public void setHiddenFields(Map<String, String> hiddenFields) {
		this.hiddenFields = hiddenFields;
	}

	public String getFormSubmitURL() {
		return formSubmitURL;
	}

	public void setFormSubmitURL(String formSubmitURL) {
		this.formSubmitURL = formSubmitURL;
	}

	public String getFormDataURL() {
		return formDataURL;
	}

	public void setFormDataURL(String formDataURL) {
		this.formDataURL = formDataURL;
	}

	public String getErrorMsg() {
		return errorMsg;
	}

	public void setErrorMsg(String errorMsg) {
		this.errorMsg = errorMsg;
	}

}
