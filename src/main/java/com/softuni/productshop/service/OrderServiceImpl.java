package com.softuni.productshop.service;

import com.softuni.productshop.common.Constants;
import org.modelmapper.ModelMapper;
import com.softuni.productshop.domain.entity.Order;
import com.softuni.productshop.domain.model.service.OrderServiceModel;
import com.softuni.productshop.error.OrderNotFoundException;
import com.softuni.productshop.repository.OrderRepository;
import com.softuni.productshop.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class OrderServiceImpl implements OrderService {

    private final ModelMapper modelMapper;
    private final OrderRepository orderRepository;

    @Autowired
    public OrderServiceImpl(
            ModelMapper modelMapper,
            OrderRepository orderRepository) {
        this.modelMapper = modelMapper;
        this.orderRepository = orderRepository;
    }

    @Override
    @Transactional
    public void createOrder(OrderServiceModel orderServiceModel) {
        orderServiceModel.setFinishedOn(LocalDateTime.now());

        orderRepository.saveAndFlush(modelMapper.map(orderServiceModel, Order.class));
    }

    @Override
    public List<OrderServiceModel> findAllOrders() {
        List<Order> orders = orderRepository.findAll();
        return orders
                .stream()
                .map(o -> modelMapper.map(o, OrderServiceModel.class))
                .collect(Collectors.toList());
    }

    @Override
    public List<OrderServiceModel> findOrdersByCustomer(String username) {
        return orderRepository.findAllOrdersByCustomer_UsernameOrderByFinishedOn(username)
                .stream()
                .map(o -> modelMapper.map(o, OrderServiceModel.class))
                .collect(Collectors.toList());
    }

    @Override
    public OrderServiceModel findOrderById(String id) {
        return orderRepository.findById(id)
                .map(o -> modelMapper.map(o, OrderServiceModel.class))
                .orElseThrow(() -> new OrderNotFoundException(Constants.ID_NOT_FOUND));
    }
}
