package com.softuni.productshop.web.controller;

import java.security.Principal;
import java.util.List;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import com.softuni.productshop.domain.model.view.OrderViewModel;
import com.softuni.productshop.service.OrderService;
import com.softuni.productshop.web.annotations.PageTitle;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;


@Controller
@RequestMapping("/orders")
public class OrdersController extends BaseController {

    private final ModelMapper modelMapper;
    private final OrderService orderService;

    @Autowired
    public OrdersController(ModelMapper modelMapper,
                            OrderService orderService) {
        this.modelMapper = modelMapper;
        this.orderService = orderService;
    }

    @GetMapping("/all")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PageTitle("All Orders")
    public ModelAndView getAllOrders(ModelAndView modelAndView) {
        List<OrderViewModel> orderViewModels = orderService.findAllOrders()
                .stream()
                .map(order -> modelMapper.map(order, OrderViewModel.class))
                .collect(Collectors.toList());

        modelAndView.addObject("orders", orderViewModels);

        return view("order/all-orders", modelAndView);
    }

    @GetMapping("/all/details/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PageTitle("Orders Details")
    public ModelAndView allOrderDetails(@PathVariable String id, ModelAndView modelAndView) {
        OrderViewModel orderViewModel = modelMapper.map(orderService.findOrderById(id), OrderViewModel.class);
        modelAndView.addObject("order", orderViewModel);

        return view("order/order-details", modelAndView);
    }

    @GetMapping("/my")
    @PreAuthorize("isAuthenticated()")
    @PageTitle("My Orders")
    public ModelAndView getMyOrders(ModelAndView modelAndView, Principal principal) {
        List<OrderViewModel> orderViewModels = orderService.findOrdersByCustomer(principal.getName())
                .stream()
                .map(order -> modelMapper.map(order, OrderViewModel.class))
                .collect(Collectors.toList());

        modelAndView.addObject("orders", orderViewModels);

        return view("order/all-orders", modelAndView);
    }

    @GetMapping("/my/details/{id}")
    @PreAuthorize("isAuthenticated()")
    @PageTitle("Orders Details")
    public ModelAndView myOrderDetails(@PathVariable String id, ModelAndView modelAndView) {
        OrderViewModel orderViewModel = modelMapper.map(orderService.findOrderById(id), OrderViewModel.class);
        modelAndView.addObject("order", orderViewModel);

        return view("order/order-details", modelAndView);
    }
}
