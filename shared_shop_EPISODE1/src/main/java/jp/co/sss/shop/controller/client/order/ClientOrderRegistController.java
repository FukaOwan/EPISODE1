package jp.co.sss.shop.controller.client.order;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
public class ClientOrderRegistController {
	//届け先入力画面表示
	@RequestMapping(path="/client/order/address/input",method=RequestMethod.POST)
	public String showAddressInput() {
	 return "order/address_input";
 }
 	//支払方法選択画面表示
	@RequestMapping(path="/client/order/payment/input", method=RequestMethod.POST)
	public String showPaymentInput() {
	 return "/client/order/check";
 }
	@RequestMapping(path="",method=Requeset)
 
}
