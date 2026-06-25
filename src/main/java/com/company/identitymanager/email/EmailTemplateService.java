package com.company.identitymanager.email;

import org.springframework.stereotype.Service;

@Service
public class EmailTemplateService {
	public String buildWelcomeEmail(
	        String name,
	        String loginUrl) {

	    return """
	        <html>
	        <body>

	        <h2>Welcome to Enterprise Identity Manager</h2>

	        <p>Hello %s,</p>

	        <p>
	        Your tenant administrator account has been created.
	        </p>

	        <p>
	        Login here:
	        <a href="%s">%s</a>
	        </p>

	        <p>
	        You will be required to:
	        </p>

	        <ul>
	            <li>Set a permanent password</li>
	            <li>Configure MFA</li>
	        </ul>

	        </body>
	        </html>
	        """.formatted(
	                name,
	                loginUrl,
	                loginUrl
	        );
	}
	
	public String buildInviteEmail(
	        String name,
	        String role,
	        String loginUrl) {

	    return """
	        <html>
	        <body>

	        <h2>Team Invitation</h2>

	        <p>Hello %s,</p>

	        <p>
	        You have been invited to join the platform.
	        </p>

	        <p>
	        Assigned Role: <b>%s</b>
	        </p>

	        <p>
	        Login:
	        <a href="%s">%s</a>
	        </p>

	        </body>
	        </html>
	        """.formatted(
	                name,
	                role,
	                loginUrl,
	                loginUrl
	        );
	}
}