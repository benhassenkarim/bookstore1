package com.bookstore.service.impl;

import org.springframework.stereotype.Service;

import com.bookstore.domain.Payment;
import com.bookstore.domain.UserPayment;
import com.bookstore.service.PaymentService;
@Service
public class PaymentServiceImpl  implements PaymentService{

	@Override
	public Payment setByUserPayment(UserPayment userPayment, Payment payment) {
		
		payment.setCardNumber(userPayment.getCardName());
		payment.setCvc(userPayment.getCvc());
		payment.setExpiryMonth(userPayment.getExpiryMonth());
		payment.setExpiryYear(userPayment.getExpiryYear());
		payment.setHolderName(userPayment.getHolderName());
		payment.setType(userPayment.getType());
		return payment;
	}

}
