package com.spring.auth.service;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.MailSendException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import com.sendgrid.Method;
import com.sendgrid.Request;
import com.sendgrid.Response;
import com.sendgrid.SendGrid;
import com.sendgrid.helpers.mail.Mail;
import com.sendgrid.helpers.mail.objects.Content;
import com.sendgrid.helpers.mail.objects.Email;
import com.spring.auth.configurations.SecurityConfiguration;
import com.spring.auth.entities.Signupresponse;
import com.spring.auth.entities.User;
import com.spring.auth.repositories.SpringAuthRepository;
import com.spring.auth.repositories.UserRepository;
import com.spring.auth.utils.CustomException;

import jakarta.mail.internet.MimeMessage;

@Service
public class AuthServiceImpl implements AuthService {
	
	@Value("${sendgrid.api.key}")
    private String sendGridApiKey;

	@Autowired
	SpringAuthRepository repository;

	@Autowired
	JpaUserDetailService detailService;

	@Autowired
	SecurityConfiguration securityconfig;

	@Autowired
	UserRepository userRepository;
	
//	@Autowired
//	JavaMailSender mailSender;

	@Override
	public ResponseEntity<Signupresponse> signup(Signupresponse signupresponse) throws CustomException {
		try {
			Boolean savesignuprespexists = repository.getByUsername(signupresponse.getUsername());
			if (savesignuprespexists) {
				CustomException ce = new CustomException();
				ce.setMessage("Error-User Already Exists");
				ce.setCode(10);
				ce.printStackTrace();
				throw ce;
			}
			Signupresponse newsignupresp = new Signupresponse();
			if (detailService.isEmail(signupresponse.getUsername())) {
				newsignupresp.setUsername(signupresponse.getUsername());
			} else {
				CustomException ce = new CustomException();
				ce.setMessage("Error-Wrong Email Format");
				ce.setCode(10);
				ce.printStackTrace();
				throw ce;
			}
			if (signupresponse.getPassword().equals(signupresponse.getConfirmpassword())
					&& (signupresponse.getPassword().length() >= 6)) {
				newsignupresp.setPassword(securityconfig.passwordEncoder().encode(signupresponse.getPassword()));
			} else {
				CustomException ce = new CustomException();
				ce.setMessage("Error-Invalid Password");
				ce.setCode(10);
				ce.printStackTrace();
				throw ce;
			}
			User user = new User();
			user.setUsername(newsignupresp.getUsername());
			user.setPassword(newsignupresp.getPassword());
			
			if(sendVerificationEmail(signupresponse.getUsername())) {
			repository.save(newsignupresp);
			userRepository.save(user);
			return ResponseEntity.ok(newsignupresp);
			}else {
				return (ResponseEntity<Signupresponse>) ResponseEntity.internalServerError();
			}

		} catch (Exception e) {
			CustomException ce = new CustomException();
			if (e.getMessage().contains("Error")) {
				ce.setMessage(e.getMessage());
			} else {
				ce.setMessage(e.getMessage());
			}
			throw ce;
		}
	}
	
	public Boolean sendVerificationEmail(String to) throws IOException {
		try {
        Email from = new Email("jenowac691@alibto.com");
        String subject = "Verify Your Account";
        Email recipient = new Email(to);

        String verificationLink = "http://localhost:8080/auth/verify" ;

        Content content = new Content("text/html",
        	    "<!DOCTYPE html>" +
        	    "<html><head><meta charset='UTF-8'><meta name='viewport' content='width=device-width,initial-scale=1'>" +
        	    "<style>" +
        	    "  @import url('https://fonts.googleapis.com/css2?family=DM+Serif+Display&family=DM+Sans:wght@400;500&display=swap');" +
        	    "  body{margin:0;padding:0;background:#f0ede8;font-family:'DM Sans',sans-serif;}" +
        	    "  .wrapper{max-width:560px;margin:40px auto;background:#fffdf9;border-radius:16px;overflow:hidden;box-shadow:0 4px 32px rgba(0,0,0,0.08);}" +
        	    "  .header{background:#1a1a2e;padding:40px 48px 32px;text-align:center;}" +
        	    "  .logo{font-family:'DM Serif Display',serif;font-size:22px;color:#f5c842;letter-spacing:2px;text-transform:uppercase;}" +
        	    "  .body{padding:48px;}" +
        	    "  h1{font-family:'DM Serif Display',serif;font-size:28px;color:#1a1a2e;margin:0 0 16px;line-height:1.3;}" +
        	    "  p{color:#555;font-size:15px;line-height:1.7;margin:0 0 24px;}" +
        	    "  .btn{display:inline-block;background:#1a1a2e;color:#f5c842 !important;text-decoration:none;font-family:'DM Sans',sans-serif;font-weight:500;font-size:15px;padding:16px 40px;border-radius:8px;letter-spacing:0.5px;}" +
        	    "  .btn-wrap{text-align:center;margin:32px 0;}" +
        	    "  .divider{height:1px;background:#ede9e0;margin:32px 0;}" +
        	    "  .footer{padding:0 48px 40px;text-align:center;}" +
        	    "  .footer p{font-size:12px;color:#aaa;margin:0;line-height:1.6;}" +
        	    "  .footer a{color:#aaa;}" +
        	    "</style></head><body>" +
        	    "<div class='wrapper'>" +
        	    "  <div class='header'><div class='logo'>&#9733; YourApp</div></div>" +
        	    "  <div class='body'>" +
        	    "    <h1>You're one step away&nbsp;&#10024;</h1>" +
        	    "    <p>Thanks for signing up! We just need to confirm your email address before you can access your account. It only takes a second.</p>" +
        	    "    <div class='btn-wrap'><a href='" + verificationLink + "' class='btn'>Verify My Account &rarr;</a></div>" +
        	    "    <div class='divider'></div>" +
        	    "    <p style='margin:0;font-size:13px;color:#999;'>This link expires in <strong>24 hours</strong>. If you didn't create an account, you can safely ignore this email.</p>" +
        	    "  </div>" +
        	    "  <div class='footer'><p>If the button doesn't work, paste this link into your browser:<br><a href='" + verificationLink + "'>" + verificationLink + "</a></p></div>" +
        	    "</div>" +
        	    "</body></html>"
        	);

        Mail mail = new Mail(from, subject, recipient, content);

        SendGrid sg = new SendGrid(sendGridApiKey);
        Request request = new Request();

        request.setMethod(Method.POST);
        request.setEndpoint("mail/send");
        request.setBody(mail.build());

        Response response = sg.api(request);

        System.out.println(response.getStatusCode());
        return true;
		}catch(Exception e) {
			e.printStackTrace();
			throw e;
			
		}
    }
}


