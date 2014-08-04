package by.creepid.integration.activemq.weblogic.producer;

import javax.jms.JMSException;
import javax.jms.MapMessage;
import javax.jms.Message;
import javax.jms.Session;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;

import by.creepid.integration.activemq.weblogic.domain.Order;
import java.util.logging.Level;
import java.util.logging.Logger;

public class OrderSender {
    
        private static final Logger logger = Logger.getLogger(OrderSender.class.getName());

	@Autowired
	private JmsTemplate jmsTemplate;

	public void sendOrder(final Order order) {
		jmsTemplate.send(

		new MessageCreator() {

			public Message createMessage(Session session) throws JMSException {
				MapMessage mapMessage = session.createMapMessage();
				
				mapMessage.setInt("orderId", order.getOrderId());
				mapMessage.setInt("customerId", order.getCustomerId());
				mapMessage.setDouble("price", order.getPrice());
				mapMessage.setString("orderCode", order.getOrderCode());
				
				return mapMessage;
			}

		});
		
		logger.log(Level.FINE, "Order sent - id: {0}", order.getOrderId());
	}
}
