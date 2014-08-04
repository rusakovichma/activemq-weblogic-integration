package by.creepid.integration.activemq.weblogic.producer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import by.creepid.integration.activemq.weblogic.domain.Order;

@Service("orderService")
public class OrderService {
    static int orderSequence = 1;
    
    @Autowired
    private OrderSender orderSender;
    public void setOrderSender(OrderSender orderSender){
        this.orderSender = orderSender;
    }
    
    public void sendOrder(int customerId, double price)
    {
        Order order = new Order(orderSequence, 2, price, "ordercd"+ orderSequence++);
        orderSender.sendOrder(order);
    }
}
