package uk.ac.ed.inf.Output;

import uk.ac.ed.inf.ilp.constant.OrderStatus;
import uk.ac.ed.inf.ilp.constant.OrderValidationCode;
import uk.ac.ed.inf.ilp.data.CreditCardInformation;
import uk.ac.ed.inf.ilp.data.Order;
import uk.ac.ed.inf.ilp.data.Pizza;
import uk.ac.ed.inf.ilp.data.Restaurant;

import java.time.LocalDate;

public class OutputOrder{
    private String orderNo;
    private OrderStatus orderStatus;
    private OrderValidationCode orderValidationCode;
    private int costInPence;

    // Parameterized constructor for initializing an OutputOrder object
    public OutputOrder(String orderNo, OrderStatus orderStatus, OrderValidationCode orderValidationCode, int costInPence) {
        this.setOrderNo(orderNo);
        this.setOrderStatus(orderStatus);
        this.setOrderValidationCode(orderValidationCode);
        this.setCostInPence(costInPence);
    }
    public String getOrderNo() {
        return orderNo;
    }

    public OrderStatus getOrderStatus() {
        return orderStatus;
    }

    public OrderValidationCode getOrderValidationCode() {
        return orderValidationCode;
    }

    public int getCostInPence() {
        return costInPence;
    }

    public void setOrderNo(String orderNo) {
        this.orderNo = orderNo;
    }

    public void setOrderStatus(OrderStatus orderStatus) {
        this.orderStatus = orderStatus;
    }

    public void setOrderValidationCode(OrderValidationCode orderValidationCode) {
        this.orderValidationCode = orderValidationCode;
    }

    public void setCostInPence(int costInPence) {
        this.costInPence = costInPence;
    }

//    public Restaurant getRestaurantOfOrder() {
//        return restaurantOfOrder;
//    }

//    public void setRestaurantOfOrder(Restaurant restaurantOfOrder) {
//        this.restaurantOfOrder = restaurantOfOrder;
//    }
}
