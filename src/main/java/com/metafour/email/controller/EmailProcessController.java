package com.metafour.email.controller;

import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.context.request.WebRequest;
import org.thymeleaf.context.WebContext;
import org.thymeleaf.spring3.SpringTemplateEngine;
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver;
import org.thymeleaf.templateresolver.TemplateResolver;

import com.metafour.email.models.Email;
import com.metafour.email.models.EmailTemplateData;
import com.metafour.email.service.EmailProcessor;
import com.metafour.util.StringsM4;

@Controller
@RequestMapping("/emailprocess")
public class EmailProcessController {
	private static final Logger logger = LoggerFactory.getLogger(EmailProcessController.class);

	@Autowired 
	private EmailProcessor emailProcessor;

	@Autowired
	private ServletContext servletContext;

	public EmailProcessController() {
		super();
	}


	@RequestMapping(method = RequestMethod.GET, value = "/compose")
	@ResponseBody
	public String viewEmailComposeSecreen(HttpServletRequest request, HttpServletResponse response) {
		logger.debug("Email template view resolve");

		final WebContext wctx = new WebContext(request, response, servletContext);

		EmailTemplateData templData = emailProcessor.getEmailTemplateData(request.getParameterMap());

		// load js files
		wctx.setVariable("custom_js", templData.getCustomJavascripts());
		wctx.setVariable("meta_ui_js", templData.getCdnPath() + "/js");
		wctx.setVariable("meta_ui_css", templData.getCdnPath() + "/css/metafour-ui.css");

		// load email data
		wctx.setVariable("from", getFilteredValue(templData.getFrom()));
		wctx.setVariable("to", getFilteredValue(templData.getTo()));
		wctx.setVariable("cc", getFilteredValue(templData.getCc()));
		wctx.setVariable("bcc", getFilteredValue(templData.getBcc()));
		wctx.setVariable("replyTo", getFilteredValue(templData.getReplyTo()));
		wctx.setVariable("subject", getFilteredValue(templData.getSubject()));
	
		// load submit url
		wctx.setVariable("formSubmitURL", StringsM4.isBlank(templData.getFormSubmitURL()) ? "/emailprocess/sendmail" : templData.getFormSubmitURL());

		// available docs
		wctx.setVariable("availableFiles", emailProcessor.getAvailableFilesToAttach(templData.getReferenceId()));

		// pre-attached files
		wctx.setVariable("attachedFiles", emailProcessor.getAttachments());
		
		// process email template 
		String message = emailProcessor.processEmailContentTemplate(request.getParameterMap());
		wctx.setVariable("body", StringsM4.isNotBlank(message) ? message : getFilteredValue(templData.getBody()));

		return getTemplateEngine().process("email.html", wctx);
	}


	@RequestMapping(value = "/sendmail", method = RequestMethod.POST)
	public @ResponseBody Map<String, ?> sendEmail(@ModelAttribute Email email, WebRequest webRequest, final Locale locale) {
		Map<String, String> oMap = new LinkedHashMap<String, String>();
		
		if (StringsM4.isBlank(email.getFrom())) {
			addError("Sender email is missing", oMap);
			return oMap;
		} else {
			if (email.getFrom().indexOf(',') != -1) {
				addError("Multiple sender email is not allowed", oMap);
				return oMap;
			}
		}
		
		if (StringsM4.isBlank(email.getTo())) addError("Recipient email is missing", oMap);
		if (StringsM4.isBlank(email.getBody())) addError("Email details is missing", oMap);
		if (StringsM4.isBlank(email.getSubject())) addError("Email subject is missing", oMap);

		return emailProcessor.sendEmail(email, webRequest);
	}

	/*
	 * Add response error
	 */
	private void addError(String message, Map<String, String> oMap) {
		oMap.put("status", "error");
		oMap.put("message", message);
	}

	/*
	 * Process template
	 */
	private SpringTemplateEngine getTemplateEngine() {
		TemplateResolver tmplResolver = new ClassLoaderTemplateResolver();
		tmplResolver.setTemplateMode("HTML5");
		tmplResolver.setCharacterEncoding("UTF-8");
		tmplResolver.setCacheable(false);

		SpringTemplateEngine templateEngine = new SpringTemplateEngine();
		templateEngine.addTemplateResolver(tmplResolver);

		return templateEngine;
	}
	

	private String getFilteredValue(String val){
		return StringsM4.isNotBlank(val) ? val : "";
	}

}
