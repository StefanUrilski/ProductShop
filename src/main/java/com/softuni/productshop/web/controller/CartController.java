package com.softuni.productshop.web.controller;


import org.modelmapper.ModelMapper;
import com.softuni.productshop.domain.model.service.OrderProductServiceModel;
import com.softuni.productshop.domain.model.service.OrderServiceModel;
import com.softuni.productshop.domain.model.view.OrderProductViewModel;
import com.softuni.productshop.domain.model.view.ProductDetailsViewModel;
import com.softuni.productshop.domain.model.view.ShoppingCartItem;
import com.softuni.productshop.service.OrderService;
import com.softuni.productshop.service.ProductService;
import com.softuni.productshop.service.UserService;
import com.softuni.productshop.web.annotations.PageTitle;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpSession;
import java.math.BigDecimal;
import java.security.Principal;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

@Controller
@RequestMapping("/cart")
public class CartController extends BaseController {

    private final ModelMapper modelMapper;
    private final UserService userService;
    private final OrderService orderService;
    private final ProductService productService;

    @Autowired
    public CartController(ModelMapper modelMapper,
                          UserService userService,
                          OrderService orderService,
                          ProductService productService) {
        this.modelMapper = modelMapper;
        this.userService = userService;
        this.orderService = orderService;
        this.productService = productService;
    }

    @SuppressWarnings("unchecked")
    private List<ShoppingCartItem> retrieveCart(HttpSession session) {
        initCart(session);

        return (List<ShoppingCartItem>) session.getAttribute("shopping-cart");
    }

    private void initCart(HttpSession session) {
        if (session.getAttribute("shopping-cart") == null) {
            session.setAttribute("shopping-cart", new LinkedList<>());
        }
    }

    private void addItemToCart(ShoppingCartItem item, List<ShoppingCartItem> cart) {
        for (ShoppingCartItem shoppingCartItem : cart) {
            if (shoppingCartItem.getProduct().getProduct().getId().equals(item.getProduct().getProduct().getId())) {
                shoppingCartItem.setQuantity(shoppingCartItem.getQuantity() + item.getQuantity());
                return;
            }
        }

        cart.add(item);
    }

    private void removeItemFromCart(String id, List<ShoppingCartItem> cart) {
        cart.removeIf(cartItem -> cartItem.getProduct().getProduct().getId().equals(id));
    }

    private BigDecimal calcTotal(List<ShoppingCartItem> cart) {
        BigDecimal result = new BigDecimal(0);
        for (ShoppingCartItem item : cart) {
            result = result.add(item.getProduct().getPrice().multiply(new BigDecimal(item.getQuantity())));
        }

        return result;
    }

    private OrderServiceModel prepareOrder(List<ShoppingCartItem> cart, String customer) {
        OrderServiceModel orderServiceModel = new OrderServiceModel();
        orderServiceModel.setCustomer(userService.findByUsername(customer));
        List<OrderProductServiceModel> products = new ArrayList<>();
        for (ShoppingCartItem item : cart) {
            OrderProductServiceModel productServiceModel = modelMapper.map(item.getProduct(), OrderProductServiceModel.class);

            for (int i = 0; i < item.getQuantity(); i++) {
                products.add(productServiceModel);
            }
        }

        orderServiceModel.setProducts(products);
        orderServiceModel.setTotalPrice(calcTotal(cart));

        return orderServiceModel;
    }

    @PostMapping("/add-product")
    @PreAuthorize("isAuthenticated()")
    public ModelAndView addToCartConfirm(String id, int quantity, HttpSession session) {
        ProductDetailsViewModel product = modelMapper.map(productService.findProductById(id), ProductDetailsViewModel.class);

        OrderProductViewModel orderProductViewModel = new OrderProductViewModel();
        orderProductViewModel.setProduct(product);
        orderProductViewModel.setPrice(product.getPrice());

        ShoppingCartItem cartItem = new ShoppingCartItem();
        cartItem.setProduct(orderProductViewModel);
        cartItem.setQuantity(quantity);

        List<ShoppingCartItem> cart = retrieveCart(session);
        addItemToCart(cartItem, cart);

        return redirect("/home");
    }

    @GetMapping("/details")
    @PreAuthorize("isAuthenticated()")
    @PageTitle("Cart Details")
    public ModelAndView cartDetails(ModelAndView modelAndView, HttpSession session) {
        List<ShoppingCartItem> cart = retrieveCart(session);
        modelAndView.addObject("totalPrice", calcTotal(cart));

        return view("cart/cart-details", modelAndView);
    }

    @DeleteMapping("/remove-product")
    @PreAuthorize("isAuthenticated()")
    public ModelAndView removeFromCartConfirm(String id, HttpSession session) {
        removeItemFromCart(id, retrieveCart(session));

        return redirect("/cart/details");
    }

    @PostMapping("/checkout")
    @PreAuthorize("isAuthenticated()")
    public ModelAndView checkoutConfirm(HttpSession session, Principal principal) {
        List<ShoppingCartItem> cart = retrieveCart(session);

        OrderServiceModel orderServiceModel = prepareOrder(cart, principal.getName());
        orderService.createOrder(orderServiceModel);
        return redirect("/home");
    }
}
