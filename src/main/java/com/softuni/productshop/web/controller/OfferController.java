package com.softuni.productshop.web.controller;

import org.modelmapper.ModelMapper;
import com.softuni.productshop.domain.model.view.OfferViewModel;
import com.softuni.productshop.service.OfferService;
import com.softuni.productshop.web.annotations.PageTitle;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;
import java.util.stream.Collectors;

@Controller
public class OfferController extends BaseController {

    private final ModelMapper modelMapper;
    private final OfferService offerService;

    @Autowired
    public OfferController(ModelMapper modelMapper,
                           OfferService offerService) {
        this.modelMapper = modelMapper;
        this.offerService = offerService;
    }

    @GetMapping("/top-offers")
    @PreAuthorize("isAuthenticated()")
    @PageTitle("Top Offers")
    public ModelAndView topOffers(ModelAndView modelAndView) {
        return view("offer/top-offers", modelAndView);
    }

    @GetMapping("/top-offers/{category}")
    @ResponseBody
    public List<OfferViewModel> fetchByCategory(@PathVariable String category) {
        return offerService.findAllOffers()
                .stream()
                .map(order -> modelMapper.map(order, OfferViewModel.class))
                .collect(Collectors.toList());
    }
}
