package com.metafour.email.service;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.springframework.web.context.request.WebRequest;

import com.metafour.email.models.Email;
import com.metafour.email.models.EmailTemplateData;

public interface EmailProcessor {

	public EmailTemplateData getEmailTemplateData(Map<String, String[]> params);

	public List<String> getAttachments();
	
	public String processEmailContentTemplate(Map<String, String[]> params);
	
	public Map<String, String[]> getAvailableFilesToAttach(String referenceId);
	
	public Map<String, String> sendEmail(Email email, WebRequest webRequest);
	
	public Collection<Map<String, String>> findContact(String hint);

}
