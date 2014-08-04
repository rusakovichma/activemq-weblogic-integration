ActiveMQ to WebLogic server integration
===============

1. Install [Weblogic](http://www.oracle.com/technetwork/middleware/weblogic/downloads/wls-main-097127.html)
2. Add user and group `ActiveMQ`:<br/>
   Enter http://localhost:7001/console -> Security Realms -> myrealm -> Users and Groups -> Groups -> add group "ActiveMQGroup"
Users -> add user "ActiveMQUser" and set for him group `ActiveMQGroup`
3. User settings in `activemq-weblogic\src\main\resources\spring\order-config.xml`:
	```xml
	<bean id="connectionFactory" class="org.apache.activemq.ActiveMQConnectionFactory">
		<property name="brokerURL">
			<value>tcp://localhost:61616</value>
		</property>
			<property name="userName">
		<value>ActiveMQUser</value>
		</property>
		<property name="password">
			<value>12345678</value>
		</property>
	</bean>
	```

4. Set domain settings in `activemq-weblogic/pom.xml`:<br/>
	```xml
	<properties>
		<MW_HOME>D:/workspace/wls1211_dev</MW_HOME>
		<WL_HOME>${MW_HOME}/wlserver</WL_HOME>
		<WM_HOME>${MW_HOME}/modules</WM_HOME>
		<WL_VERSION>12.1.1.0</WL_VERSION>
		
		<wl.domain.home>D:/workspace/WLS_domain/base_domain</wl.domain.home>
		<log4j.config>${wl.domain.home}/config/log4j.properties</log4j.config>
	</properties>
	```

5. Change if need group in `activemq-weblogic\src\main\resources\activemq-config.xml`:<br/>
	```xml
	<plugins>
		<bean id="WebLogicSecurity"
		class="by.topby.integration.activemq.weblogic.ActiveMQToWebLogicSecurity"
		xmlns="http://www.springframework.org/schema/beans">
			<property name="authorizedGroup" value="ActiveMQGroup" />
		</bean>
	</plugins>
	```

6. Persistence settings `activemq-weblogic\src\main\resources\activemq-config.xml`:
	```xml
	<persistenceAdapter>
		<kahaDB directory="${wl.domain.home}/activemq/kahadb" />
		<!-- By default, use an embedded Derby database -->
		<!-- <journaledJDBC journalLogFiles="5" dataDirectory="D:/workspace/WLS_domain/base_domain/activemq" 
		/> -->
		<!-- Use this with the WebLogicDataSource below to use a WebLogic database 
		connection pool instead of the embedded Derby database <journaledJDBC journalLogFiles="5" 
		dataDirectory="/server/bea/weblogic920/domains/jms/activemq-data" dataSource="#WebLogicDataSource" 
		/> -->
	</persistenceAdapter>
	```
7. `mvn clean install`
8. Deploy .war: `activemq-weblogic.war` Deployments -> Install
