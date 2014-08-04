package by.creepid.integration.activemq.weblogic;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

import by.creepid.integration.activemq.weblogic.producer.OrderService;
import java.util.logging.Level;
import java.util.logging.Logger;

@Component
public class AppListener implements ApplicationListener<ContextRefreshedEvent> {

    private static final Logger logger = Logger.getLogger(AppListener.class.getName());

    private static final int ORDER_COUNT = 5;
    private static final double ORDER_PRICE = 10.0D;

    @Autowired
    private OrderService orderService;

    private void sendOrders() {
        for (int i = 1; i <= ORDER_COUNT; i++) {
            logger.log(Level.FINE, "Sending order: {0}", i);
            orderService.sendOrder(1 + i, ORDER_PRICE + i);
        }
    }

    @Override
    public void onApplicationEvent(final ContextRefreshedEvent event) {
        sendOrders();
    }

    public void setOrderService(OrderService orderService) {
        this.orderService = orderService;
    }

}
