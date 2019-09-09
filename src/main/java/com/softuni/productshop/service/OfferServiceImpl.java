package com.softuni.productshop.service;

import org.modelmapper.ModelMapper;
import com.softuni.productshop.domain.entity.Offer;
import com.softuni.productshop.domain.entity.Product;
import com.softuni.productshop.domain.model.service.OfferServiceModel;
import com.softuni.productshop.domain.model.service.ProductServiceModel;
import com.softuni.productshop.repository.OfferRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

@Service
public class OfferServiceImpl implements OfferService {

    private final OfferRepository offerRepository;
    private final ProductService productService;
    private final ModelMapper modelMapper;

    @Autowired
    public OfferServiceImpl(OfferRepository offerRepository, ProductService productService, ModelMapper modelMapper) {
        this.offerRepository = offerRepository;
        this.productService = productService;
        this.modelMapper = modelMapper;
    }

    @Override
    public List<OfferServiceModel> findAllOffers() {
        return offerRepository.findAll().stream()
                .map(o -> modelMapper.map(o, OfferServiceModel.class))
                .collect(Collectors.toList());
    }

    @Scheduled(fixedRate = 300000)
    private void generateOffers() {
        offerRepository.deleteAll();
        List<ProductServiceModel> products = productService.findAllProducts();

        if (products.isEmpty()) {
            return;
        }

        Random rnd = new Random();
        List<Offer> offers = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            Offer offer = new Offer();
            offer.setProduct(modelMapper.map(products.get(rnd.nextInt(products.size())), Product.class));
            offer.setPrice(offer.getProduct().getPrice().multiply(new BigDecimal(0.8)));

            if (offers.stream().filter(o -> o.getProduct().getId().equals(offer.getProduct().getId())).count() == 0) {
                offers.add(offer);
            }
        }

        offerRepository.saveAll(offers);
    }
}
