package com.softuni.productshop.web.controller;

import com.softuni.productshop.domain.model.rest.ProductOrderRequestModel;
import com.softuni.productshop.service.OrderService;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RestController
@RequestMapping("/api/order")
public class OrdersApiController {
    private final OrderService orderService;

    public OrdersApiController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping("/submit")
    public void submitOrder(@RequestBody ProductOrderRequestModel model, Principal principal) throws Exception {
        String name = principal.getName();
//        orderService.createOrder(model.getId(), name);
    }
}
