package by.creepid.integration.activemq.weblogic.consumer;

import java.util.Map;

import org.springframework.stereotype.Component;

import by.creepid.integration.activemq.weblogic.domain.Order;
import java.util.logging.Level;
import java.util.logging.Logger;

@Component
public class OrderListener {

    private static final Logger logger = Logger.getLogger(OrderListener.class.getName());

    public void orderReceived(Map<String, Object> message) throws Exception {
        int orderId = (Integer) message.get("orderId");
        int customerId = (Integer) message.get("customerId");
        double price = (Double) message.get("price");
        String orderCode = (String) message.get("orderCode");

        Order customer = new Order(orderId, customerId, price, orderCode);

        logger.log(Level.FINE, "Order received: {0}, customerId: {1}, price: {2}",
                new Object[]{orderId, customerId, price});
    }
}
