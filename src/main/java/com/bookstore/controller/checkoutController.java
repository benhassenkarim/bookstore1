package com.bookstore.controller;

import java.security.Principal;
import java.util.Collections;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.bookstore.domain.BillingAddress;
import com.bookstore.domain.CartItem;
import com.bookstore.domain.Payment;
import com.bookstore.domain.ShippingAddress;
import com.bookstore.domain.ShoppingCart;
import com.bookstore.domain.User;
import com.bookstore.domain.UserBilling;
import com.bookstore.domain.UserPayment;
import com.bookstore.domain.UserShipping;
import com.bookstore.service.BillingAdressService;
import com.bookstore.service.CartItemService;
import com.bookstore.service.PaymentService;
import com.bookstore.service.ShippingAdressService;
import com.bookstore.service.UserPaymentService;
import com.bookstore.service.UserService;
import com.bookstore.service.UserShippingService;
import com.bookstore.utility.USConstants;

@Controller
public class checkoutController {

	private ShippingAddress shippingAdress=new ShippingAddress();
	private BillingAddress billingAddress =new BillingAddress();
	private Payment payment= new Payment();
	
	@Autowired
	private UserService userService;
	
	@Autowired
	private CartItemService cartItemService;
	
	@Autowired
	private ShippingAdressService shippingAdressService;
	
	@Autowired
	private PaymentService paymentService;
	
	@Autowired
	private BillingAdressService billingAdressService;
	
	@Autowired
	private UserShippingService userShippingService;
	
	@Autowired
	private UserPaymentService userPaymentService;
	
	
	@RequestMapping("/checkout")
	private String checkout(
			@RequestParam("id") Long cartId,
			@RequestParam(value = "missingRequiredField",required = false)boolean missingRequiredField,
			Model model,Principal principal) {
		
		User user=userService.findByUsername(principal.getName());
		if(cartId !=user.getShoppingCart().getId()) {
			return "badRequestPage";
		}
		List<CartItem> cartItemList =cartItemService.findByShoppingCart(user.getShoppingCart());
		
		if(cartItemList.size()==0) {
			model.addAttribute("emptyCart",true);
			return "forward:/shoppingCart/cart";
		}
		
		for(CartItem cartItem:cartItemList) {
			if(cartItem.getBook().getInStockNumber()<cartItem.getQty()) {
				model.addAttribute("notEnoughStock",true);
				return "forward:/shoppingCart/cart";
			}
		}
		
		List<UserShipping> userShippingList=user.getUserShippingList();
		List<UserPayment> userPaymentList=user.getUserPaymentList();
		
		model.addAttribute("userPaymentList",userPaymentList);
		model.addAttribute("userShippingList",userShippingList);
		
		if(userPaymentList.size()==0) {
			model.addAttribute("emptyPaymentList",true);
		}else {
			model.addAttribute("emptyPaymentList",false);}
		
		if(userShippingList.size()==0) {
			model.addAttribute("emptyShippingList",true);
		}else {
			model.addAttribute("emptyShippingList",false);}
		ShoppingCart shoppingCart=user.getShoppingCart();
		
		for(UserShipping userShipping:userShippingList) {
			if(userShipping.isUserShippingDefault()) {
				shippingAdressService.setByUserShipping(userShipping,shippingAdress);
			}
		}
		for(UserPayment userPayment:userPaymentList ) {
			if(userPayment.isDefaultPayment()) {
				paymentService.setByUserPayment(userPayment,payment);
				billingAdressService.setByUserBilling(userPayment.getUserBilling(),billingAddress);
			}
		}
		model.addAttribute("shippingAddress",shippingAdress);
		model.addAttribute("Payment",payment);
		model.addAttribute("billingAddress",billingAddress);
		model.addAttribute("cartItemList",cartItemList);
		model.addAttribute("shoppingCart",user.getShoppingCart());
		
		List<String> stateList = USConstants.listOfUSStatesCode;
		Collections.sort(stateList);
		model.addAttribute("stateList", stateList);
		model.addAttribute("/classActiveShipping", true);
		if(missingRequiredField) {
			model.addAttribute("missingRequiredField",true);
		}
		return "checkout";
	}
	@RequestMapping(value="/checkout",method=RequestMethod.POST)
	private String checkout(
			@ModelAttribute("shippingAddress") ShippingAddress shippingAddress,
			@ModelAttribute("billingAdress") BillingAddress billingAddress ,
			@ModelAttribute("payment") Payment payment,
			@ModelAttribute("billingSameAsShipping") String billingSameAsShipping,
			@ModelAttribute("shippingMethod") String shippingMethod,
			Model model,Principal principal
			) {
		ShoppingCart shoppingCart=userService.findByUsername(principal.getName()).getShoppingCart();
		List<CartItem> cartItemList=cartItemService.findByShoppingCart(shoppingCart);
		
	}
	@RequestMapping("setShippingAddress")
	public String setShippingAdress(
			@RequestParam("userShippingId") Long userShippingId,
			Principal principal,Model model
			) {
		User user=userService.findByUsername(principal.getName());
		UserShipping userShipping=userShippingService.findById(userShippingId).get();
		if(userShipping.getUser().getId()!=user.getId()) {
			return "badRequestPage";
		}else {
			shippingAdressService.setByUserShipping(userShipping, shippingAdress);
			List<CartItem> cartItemList =cartItemService.findByShoppingCart(user.getShoppingCart());
			
			
			model.addAttribute("shippingAddress",shippingAdress);
			model.addAttribute("Payment",payment);
			model.addAttribute("billingAddress",billingAddress);
			model.addAttribute("cartItemList",cartItemList);
			model.addAttribute("shoppingCart",user.getShoppingCart());
			
			List<String> stateList = USConstants.listOfUSStatesCode;
			Collections.sort(stateList);
			model.addAttribute("stateList", stateList);
			
			List<UserShipping> userShippingList=user.getUserShippingList();
			List<UserPayment> userPaymentList=user.getUserPaymentList();
			
			model.addAttribute("userPaymentList",userPaymentList);
			model.addAttribute("userShippingList",userShippingList);
			
			model.addAttribute("shippingAdress",shippingAdress);
			model.addAttribute("classActiveShipping",true);
			
		
			if(userPaymentList.size()==0) {
				model.addAttribute("emptyPaymentList",true);
			}else {
				model.addAttribute("emptyPaymentList",false);}
			
				model.addAttribute("emptyShippingList",false);
				
			
			
			return "checkout";
		}
	}
		@RequestMapping("setPaymentMethod")
		public String setPaymentMethod(
				@RequestParam("userPaymentId") Long userPaymentId,
				Principal principal,Model model
				) {
			User user=userService.findByUsername(principal.getName());
			UserPayment userPayment= userPaymentService.findById(userPaymentId).get();
			UserBilling userBilling=userPayment.getUserBilling();
			if(userPayment.getUser().getId()!=user.getId()) {
				return "badRequestPage";
			}else {
				paymentService.setByUserPayment(userPayment, payment);
				List<CartItem> cartItemList =cartItemService.findByShoppingCart(user.getShoppingCart());
				billingAdressService.setByUserBilling(userBilling, billingAddress);
				
				model.addAttribute("shippingAddress",shippingAdress);
				model.addAttribute("Payment",payment);
				model.addAttribute("billingAddress",billingAddress);
				model.addAttribute("cartItemList",cartItemList);
				model.addAttribute("shoppingCart",user.getShoppingCart());
				
				List<String> stateList = USConstants.listOfUSStatesCode;
				Collections.sort(stateList);
				model.addAttribute("stateList", stateList);
				
				List<UserShipping> userShippingList=user.getUserShippingList();
				List<UserPayment> userPaymentList=user.getUserPaymentList();
				
				model.addAttribute("userPaymentList",userPaymentList);
				model.addAttribute("userShippingList",userShippingList);
				
				model.addAttribute("shippingAdress",shippingAdress);
				model.addAttribute("classActivePayment",true);
				
				
					model.addAttribute("emptyPaymentList",false);
				
				if(userShippingList.size()==0) {
					model.addAttribute("emptyShippingList",true);
				}else {
					model.addAttribute("emptyShippingList",false);}
				
				return "checkout";
			}
	}
}
