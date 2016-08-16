package br.com.infox.servlet.poll;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.jboss.seam.contexts.Lifecycle;

@WebServlet(urlPatterns = "/sessionPoll")
public class ServletSessionPoll extends HttpServlet {

	private static final long serialVersionUID = 1L;
	
	@Override
	protected void doHead(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		// LifeCycle colocado para evitar timeout da conversação do seam
		Lifecycle.beginCall();
		// do nothing
		Lifecycle.endCall();
	}
	

}
	