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
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.WebContext;
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

	@RequestMapping(method = RequestMethod.GET, value = "/compose")
	@ResponseBody
	public String viewEmailComposeSecreen(HttpServletRequest request, HttpServletResponse response) {
		logger.debug("Email template view resolve");

		final WebContext wctx = new WebContext(request, response, servletContext);

		EmailTemplateData data = emailProcessor.getEmailTemplateData(request.getParameterMap());

		// load js files
		wctx.setVariable("custom_js", data.getCustomJavascripts());
		wctx.setVariable("meta_ui_js", data.getCdnPath() + "/js");
		wctx.setVariable("meta_ui_css", data.getCdnPath() + "/css/metafour-ui.css");

		// load email data
		wctx.setVariable("from", getFilteredValue(data.getFrom()));
		wctx.setVariable("to", getFilteredValue(data.getTo()));
		wctx.setVariable("cc", getFilteredValue(data.getCc()));
		wctx.setVariable("bcc", getFilteredValue(data.getBcc()));
		wctx.setVariable("replyTo", getFilteredValue(data.getReplyTo()));
		wctx.setVariable("subject", getFilteredValue(data.getSubject()));
		wctx.setVariable("body", getFilteredValue(data.getBody()));

		// load submit url
		wctx.setVariable("formSubmitURL", StringsM4.isBlank(data.getFormSubmitURL()) ? "/emailprocess/sendmail" : data.getFormSubmitURL());
		// available docs
		wctx.setVariable("availableFiles", emailProcessor.getAvailableFilesToAttach(data.getReferenceId()));
		// pre-attached files
		wctx.setVariable("attachedFiles", emailProcessor.getAttachments());

		return getTemplateEngine().process("email.html", wctx);
	}

	@RequestMapping(value = "/sendmail", method = RequestMethod.POST)
	public @ResponseBody Map<String, ?> sendEmail(@ModelAttribute Email email, WebRequest webRequest, final Locale locale) {
		Map<String, String> oMap = new LinkedHashMap<String, String>();
		if (StringsM4.isBlank(email.getFrom())) {
			addError("Sender email is missing", oMap);
		} else {
			if (email.getFrom().indexOf(',') != -1) {
				addError("Multiple sender email is not allowed", oMap);
				return oMap;
			}
		}
		if (StringsM4.isBlank(email.getTo())) addError("Recipient email is missing", oMap);
		if (StringsM4.isBlank(email.getSubject())) addError("Email subject is missing", oMap);
		if (StringsM4.isBlank(email.getBody())) addError("Email details is missing", oMap);

		oMap = emailProcessor.sendEmail(email, webRequest);
		return oMap;
	}

	private void addError(String message, Map<String, String> oMap) {
		oMap.put("status", "error");
		oMap.put("message", message);
	}

	private TemplateEngine getTemplateEngine() {
		TemplateResolver tmplResolver = new ClassLoaderTemplateResolver();
		tmplResolver.setTemplateMode("HTML5");
		tmplResolver.setCharacterEncoding("UTF-8");
		tmplResolver.setCacheable(false);

		TemplateEngine templateEngine = new TemplateEngine();
		templateEngine.addTemplateResolver(tmplResolver);
		return templateEngine;
	}

	private String getFilteredValue(String val) {
		return StringsM4.isNotBlank(val) ? val : "";
	}

}
