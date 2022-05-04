package com.bookstore.service;

import com.bookstore.domain.BillingAddress;
import com.bookstore.domain.UserBilling;

public interface BillingAdressService {

	BillingAddress setByUserBilling(UserBilling userBilling, BillingAddress billingAddress);

}
