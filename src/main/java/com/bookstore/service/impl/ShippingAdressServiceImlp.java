package com.bookstore.service.impl;

import org.springframework.stereotype.Service;

import com.bookstore.domain.ShippingAddress;
import com.bookstore.domain.UserShipping;
import com.bookstore.service.ShippingAdressService;
@Service
public class ShippingAdressServiceImlp implements ShippingAdressService {

	@Override
	public ShippingAddress setByUserShipping(UserShipping userShipping, ShippingAddress shippingAdress) {
		shippingAdress.setShippingAddressName(userShipping.getUserShippingName());
		shippingAdress.setShippingAddressCity(userShipping.getUserShippingCity());
		shippingAdress.setShippingAddressCountry(userShipping.getUserShippingCountry());
		shippingAdress.setShippingAddressStreet1(userShipping.getUserShippingStreet1());
		shippingAdress.setShippingAddressStreet2(userShipping.getUserShippingStreet2());
		shippingAdress.setShippingAddressZipcode(userShipping.getUserShippingZipcode());
		return shippingAdress;
	}

}
