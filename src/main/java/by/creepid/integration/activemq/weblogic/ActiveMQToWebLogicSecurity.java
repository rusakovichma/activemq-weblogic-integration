package by.creepid.integration.activemq.weblogic;

import java.util.HashSet;
import java.util.Set;

import org.apache.activemq.broker.Broker;
import org.apache.activemq.broker.BrokerPlugin;
import org.apache.activemq.security.AuthorizationBroker;
import org.apache.activemq.security.AuthorizationEntry;
import org.apache.activemq.security.DefaultAuthorizationMap;

import weblogic.security.principal.WLSGroupImpl;


public class ActiveMQToWebLogicSecurity implements BrokerPlugin {
	private String authorizedGroup;

	public Broker installPlugin(Broker broker) {
		Broker first = new ActiveMQWebLogicAuthenticationFilter(broker);
		AuthorizationEntry entry = new AuthorizationEntry();

		Set acls = new HashSet();
		acls.add(new WLSGroupImpl(authorizedGroup));
		
		entry.setAdminACLs(acls);
		entry.setReadACLs(acls);
		entry.setWriteACLs(acls);
		
		DefaultAuthorizationMap map = new DefaultAuthorizationMap();
		map.setDefaultEntry(entry);
		// todo: if finer-grained access is required,
		// add more entries to the authorization map
		Broker second = new AuthorizationBroker(first, map);
		
		return second;
	}

	public String getAuthorizedGroup() {
		return authorizedGroup;
	}

	/**
	 * * Called by XBean at configuration time to set the authorized group from
	 * a * property in the main ActiveMQ configuration file.
	 */
	public void setAuthorizedGroup(String authorizedGroup) {
		this.authorizedGroup = authorizedGroup;
	}
}
