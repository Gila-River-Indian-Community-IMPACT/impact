
package us.oh.state.epa.stars2.util;

import java.util.Arrays;
import java.util.Properties;

import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.Message;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import us.oh.state.epa.stars2.def.SystemPropertyDef;
import us.oh.state.epa.stars2.framework.exception.DAOException;

import java.io.Serializable;

/**
 * A SendMail is a wrapper around the JavaMail API that enables an
 * application to send email.
 *
 * @see <A href="https://www.oracle.com/technetwork/java/javamail/index-138267.html">
 *              JavaMail API and Internet Mail Resources</A>
 * @see <A href="http://www.javaworld.com/javaworld/jw-10-2001/jw-1026-javamail.html">
 *              JavaMail Quick Start</A>
 *
 * @author Charles Meier
 * @version %I%, %G%
 *
 */
public class SendMail implements Serializable {

	private static final long serialVersionUID = -9074349205510631861L;

	private String transProtocol = "smtp";
	private String mailServer;
	private String mailUser;
	private String mailUserPwd;

	private String from;
	private String replyTo;
	private String[] recipients;
	private String[] ccRecipients;
	private String[] bccRecipients;
	private String subject;
	private String message;

	private transient Session mailSession;

	public SendMail() {
		
		super();

		mailServer = SystemPropertyDef.getSystemPropertyValue("MailServer", null);
		mailUser = SystemPropertyDef.getSystemPropertyValue("MailUser", null);
		mailUserPwd = SystemPropertyDef.getSystemPropertyValue("MailUserPwd", null);
		from = SystemPropertyDef.getSystemPropertyValue("MailFrom", null);

	}

	public SendMail(String transProtocol, String mailServer, String mailUser, String mailUserPwd, String from,
			String replyTo, String[] recipients, String[] ccRecipients, String[] bccRecipients, String subject,
			String message, Session mailSession) {

		super();
		this.transProtocol = transProtocol;
		this.mailServer = mailServer;
		this.mailUser = mailUser;
		this.mailUserPwd = mailUserPwd;
		this.from = from;
		this.replyTo = replyTo;
		this.recipients = recipients;
		this.ccRecipients = ccRecipients;
		this.bccRecipients = bccRecipients;
		this.subject = subject;
		this.message = message;
		this.mailSession = mailSession;

	}

	public final String getTransportProtocol() {
		return transProtocol;
	}

	public final void setTransportProtocol(String transProtocol) {
		this.transProtocol = transProtocol;
	}

	public final String getOutboundMailServer() {
		return mailServer;
	}

	public final void setOutboundMailServer(String mailServer) {
		this.mailServer = mailServer;
	}

	public final String getMailUser() {
		return mailUser;
	}

	public final void setMailUser(String mailUser) {
		this.mailUser = mailUser;
	}

	public final String getMailUserPwd() {
		return mailUserPwd;
	}

	public final void setMailUserPwd(String mailUserPwd) {
		this.mailUserPwd = mailUserPwd;
	}

	public final String getFrom() {
		return from;
	}

	public final void setFrom(String from) {
		this.from = from;
	}

	public final String getReplyTo() {
		return replyTo;
	}

	public final void setReplyTo(String replyTo) {
		this.replyTo = replyTo;
	}

	public final String[] getRecipients() {
		return recipients;
	}

	public final void setRecipients(String[] recipients) {
		this.recipients = recipients;
	}

	public final String[] getCcRecipients() {
		return ccRecipients;
	}

	public final void setCcRecipients(String[] ccRecipients) {
		this.ccRecipients = ccRecipients;
	}

	public final String[] getBccRecipients() {
		return bccRecipients;
	}

	public final void setBccRecipients(String[] bccRecipients) {
		this.bccRecipients = bccRecipients;
	}

	public final String getSubject() {
		return subject;
	}

	public final void setSubject(String subject) {
		this.subject = subject;
	}

	public final String getMessage() {
		return message;
	}

	public final void setMessage(String message) {
		this.message = message;
	}

	public final Session getMailSession() {
		return mailSession;
	}

	public final void setMailSession(Session mailSession) {
		this.mailSession = mailSession;
	}

	/**
	 * Sends an email.
	 */
	public void send() throws DAOException {

		try {

			Properties props = new Properties();

			String transProtocol = getTransportProtocol();
			if (transProtocol == null || transProtocol.length() < 1) {
				throw new Exception("SendMail.send() transportProtocol is not set.");
			}
			props.setProperty("mail.transport.protocol", transProtocol);

			String mailServer = getOutboundMailServer();
			if (mailServer == null || mailServer.length() < 1) {
				throw new Exception("SendMail.send() outboundMailServer is not set.");
			}
			props.setProperty("mail.host", mailServer);

			String mailUser = getMailUser();
			if (mailUser != null && mailUser.length() > 0) {
				props.setProperty("mail.user", mailUser);
			}

			String mailPwd = getMailUserPwd();
			if (mailPwd != null && mailPwd.length() > 0) {
				props.setProperty("mail.password", mailPwd);
			}

			if (mailSession == null) {
				mailSession = Session.getInstance(props, null);
			}
			MimeMessage message = new MimeMessage(mailSession);

			String subject = getSubject();
			if (subject != null && subject.indexOf('\n') >= 0) {
				StringBuffer tmpBuf = new StringBuffer(subject.length());
				int from = 0;
				int to = subject.indexOf('\n', 0);
				while (to > from && to >= 0) {
					tmpBuf.append(subject.substring(from, to));
					from = to + 1;
					to = subject.indexOf('\n', from);
				}
				subject = tmpBuf.toString();
			}
			if (subject != null && subject.length() > 0) {
				message.setSubject(subject);
			}

			String theMessage = getMessage();
			if (theMessage != null && theMessage.length() > 0) {
				message.setText(theMessage);
			}

			String[] recipients = getRecipients();
			if (recipients == null || recipients.length == 0) {
				throw new Exception("SendMail.send() recipients is not set.");
			}

			for (int i = 0; i < recipients.length; i++) {
				if (recipients[i] != null && recipients[i].length() > 0) {
					message.addRecipient(Message.RecipientType.TO, new InternetAddress(recipients[i]));
				}
			}

			Transport transport = mailSession.getTransport();
			transport.connect();
			transport.sendMessage(message, message.getRecipients(Message.RecipientType.TO));
			transport.close();

		} catch (Exception e) {
			throw new DAOException("Exception caught while sending email. " + e.getMessage(), e);
		}
		return;

	} // END: public void send()

	@Override
	public int hashCode() {

		final int prime = 31;
		int result = 1;
		result = prime * result + Arrays.hashCode(bccRecipients);
		result = prime * result + Arrays.hashCode(ccRecipients);
		result = prime * result + ((from == null) ? 0 : from.hashCode());
		result = prime * result + ((mailServer == null) ? 0 : mailServer.hashCode());
		result = prime * result + ((mailUser == null) ? 0 : mailUser.hashCode());
		result = prime * result + ((mailUserPwd == null) ? 0 : mailUserPwd.hashCode());
		result = prime * result + ((message == null) ? 0 : message.hashCode());
		result = prime * result + Arrays.hashCode(recipients);
		result = prime * result + ((replyTo == null) ? 0 : replyTo.hashCode());
		result = prime * result + ((subject == null) ? 0 : subject.hashCode());
		result = prime * result + ((transProtocol == null) ? 0 : transProtocol.hashCode());
		return result;

	}

	@Override
	public boolean equals(Object obj) {

		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		SendMail other = (SendMail) obj;
		if (!Arrays.equals(bccRecipients, other.bccRecipients))
			return false;
		if (!Arrays.equals(ccRecipients, other.ccRecipients))
			return false;
		if (from == null) {
			if (other.from != null)
				return false;
		} else if (!from.equals(other.from))
			return false;
		if (mailServer == null) {
			if (other.mailServer != null)
				return false;
		} else if (!mailServer.equals(other.mailServer))
			return false;
		if (mailUser == null) {
			if (other.mailUser != null)
				return false;
		} else if (!mailUser.equals(other.mailUser))
			return false;
		if (mailUserPwd == null) {
			if (other.mailUserPwd != null)
				return false;
		} else if (!mailUserPwd.equals(other.mailUserPwd))
			return false;
		if (message == null) {
			if (other.message != null)
				return false;
		} else if (!message.equals(other.message))
			return false;
		if (!Arrays.equals(recipients, other.recipients))
			return false;
		if (replyTo == null) {
			if (other.replyTo != null)
				return false;
		} else if (!replyTo.equals(other.replyTo))
			return false;
		if (subject == null) {
			if (other.subject != null)
				return false;
		} else if (!subject.equals(other.subject))
			return false;
		if (transProtocol == null) {
			if (other.transProtocol != null)
				return false;
		} else if (!transProtocol.equals(other.transProtocol))
			return false;

		return true;

	}

	@Override
	public String toString() {

		return "SendMail [transProtocol=" + transProtocol + ", mailServer=" + mailServer + ", mailUser=" + mailUser
				+ ", mailUserPwd=" + mailUserPwd + ", from=" + from + ", replyTo=" + replyTo + ", recipients="
				+ Arrays.toString(recipients) + ", ccRecipients=" + Arrays.toString(ccRecipients) + ", bccRecipients="
				+ Arrays.toString(bccRecipients) + ", subject=" + subject + ", message=" + message + "]";

	}

}
