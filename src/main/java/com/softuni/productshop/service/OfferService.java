package com.softuni.productshop.service;

import com.softuni.productshop.domain.model.service.OfferServiceModel;

import java.util.List;

public interface OfferService {

    List<OfferServiceModel> findAllOffers();
}
