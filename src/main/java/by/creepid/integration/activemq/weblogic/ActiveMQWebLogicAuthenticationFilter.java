package by.creepid.integration.activemq.weblogic;

import java.security.Principal;
import java.util.Collections;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;

import javax.security.auth.Subject;
import javax.security.auth.login.AppConfigurationEntry;
import javax.security.auth.login.Configuration;
import javax.security.auth.login.LoginContext;

import org.apache.activemq.broker.Broker;
import org.apache.activemq.broker.BrokerFilter;
import org.apache.activemq.broker.ConnectionContext;
import org.apache.activemq.command.ConnectionInfo;
import org.apache.activemq.security.SecurityContext;

import weblogic.security.URLCallbackHandler;

/**
 * * A broker filter that authenticates callers against WebLogic security. *
 * This is similar to the ActiveMQ JaasAuthenticationBroker except for two *
 * things: *
 * <ul>
 * *
 * <li>Instead of reading a JAAS configuration file, it hardcodes the JAAS *
 * configuration to require authentication against WebLogic</li> * *
 * <li>The SecurityContext implementation overrides the method used to * compare
 * actual and eligible principals in order to handle the fact * that WebLogic
 * principals (WLSGroupImpl in particular) do not seem * to match according to
 * equals and hashCode even if the principal class * and principal name are the
 * same (perhaps having to do with the * signature data on the
 * WLSAbstractPrincipal).</li> *
 * </ul>
 */
public class ActiveMQWebLogicAuthenticationFilter extends BrokerFilter {
	
	private final static Configuration WEBLOGIC_JAAS_CONFIGURATION = new Configuration() {

		public AppConfigurationEntry[] getAppConfigurationEntry(String name) {
			return new AppConfigurationEntry[] {

			new AppConfigurationEntry(
					"weblogic.security.auth.login.UsernamePasswordLoginModule",
					AppConfigurationEntry.LoginModuleControlFlag.REQUIRED,
					Collections.EMPTY_MAP) };

		}

		public void refresh() {
		}
	};
	private final CopyOnWriteArrayList securityContexts = new CopyOnWriteArrayList();

	public ActiveMQWebLogicAuthenticationFilter(Broker next) {
		super(next);
	}

	static class JaasSecurityContext extends SecurityContext {
		private final Subject subject;

		public JaasSecurityContext(String userName, Subject subject) {
			super(userName);
			this.subject = subject;
		}

		public Set getPrincipals() {
			return subject.getPrincipals();
		}

		/**
		 * * This is necessary because WebLogic uses extra logic when comparing
		 * principals, * probably to check whether they are cryptographically
		 * signed (which WebLogic * supports). We skip that test because
		 * ActiveMQ does not sign the principals * it deals with.
		 */
		public boolean isInOneOf(Set eligiblePrincipals) {
			for (Iterator it = getPrincipals().iterator(); it.hasNext();) {

				Principal test = (Principal) it.next();

				for (Iterator el = eligiblePrincipals.iterator(); el.hasNext();) {
					Principal eligible = (Principal) el.next();

					if (test.getName().equals(eligible.getName())
							&& test.getClass().getName()
									.equals(eligible.getClass().getName())) {
						return true;
					}
				}
			}
			return false;
		}
	}

	public void addConnection(ConnectionContext context, ConnectionInfo info)
			throws Exception {
		if (context.getSecurityContext() == null) {
			// Do the login.
			try {
				LoginContext lc = new LoginContext("ActiveMQ", 
						new Subject(),
						new URLCallbackHandler(
								info.getUserName(), 
								info.getPassword()),
								WEBLOGIC_JAAS_CONFIGURATION);
				lc.login();
				
				Subject subject = lc.getSubject();
				
				SecurityContext s = new JaasSecurityContext(info.getUserName(),
						subject);
				
				context.setSecurityContext(s);
				
				securityContexts.add(s);
			} catch (Exception e) {
				throw (SecurityException) new SecurityException(
						"User name or password is invalid.").initCause(e);
			}
			
		}
		
		super.addConnection(context, info);
	}

	public void removeConnection(ConnectionContext context,
			ConnectionInfo info, Throwable error) throws Exception {
		super.removeConnection(context, info, error);
		
		if (securityContexts.remove(context.getSecurityContext())) {
			context.setSecurityContext(null);
		}
	}

	/**
	 * * Previously logged in users may no longer have the same access anymore.
	 * Refresh * all the logged into users.
	 */
	public void refresh() {
		
		for (Iterator iter = securityContexts.iterator(); iter.hasNext();) {
			SecurityContext sc = (SecurityContext) iter.next();
			sc.getAuthorizedReadDests().clear();
			sc.getAuthorizedWriteDests().clear();
		}
	}
}