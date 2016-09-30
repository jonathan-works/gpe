package br.com.infox.servlet.poll;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.jboss.seam.core.ConversationEntries;
import org.jboss.seam.core.ConversationEntry;

@WebServlet(urlPatterns = "/sessionPoll")
public class ServletSessionPoll extends HttpServlet {

	private static final long serialVersionUID = 1L;
	
	@Override
	protected void doHead(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
	    HttpSession session = req.getSession(false);
	    String conversationId = req.getParameter("conversationId");
	    if (session != null && conversationId != null) {
	        revalidateSeamConversation(session, conversationId);
	    }
	}

    private void revalidateSeamConversation(HttpSession session, String conversationId) {
        ConversationEntries conversationEntries = (ConversationEntries) session.getAttribute("org.jboss.seam.core.conversationEntries");
        if (conversationEntries != null) {
            ConversationEntry conversationEntry = conversationEntries.getConversationEntry(conversationId);
            if (conversationEntry != null) {
                conversationEntry.setLastRequestTime(System.currentTimeMillis());
            }
        }
    }

}
	