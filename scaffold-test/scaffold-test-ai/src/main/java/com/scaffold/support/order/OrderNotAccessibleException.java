package com.scaffold.support.order;

public class OrderNotAccessibleException extends IllegalArgumentException {

    public OrderNotAccessibleException() {
        super("No order accessible to the current user was found");
    }
}
