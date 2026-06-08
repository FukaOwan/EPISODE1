package jp.co.sss.shop.controller.client.order;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
public class ClientOrderRegistController {
	//届け先入力画面表示
	@RequestMapping(path="/client/order/address/input",method=RequestMethod.GET)
	public String showAddressInput() {
		
	 return "order/address_input";
	}
 	//支払方法選択画面表示
	@RequestMapping(path="/client/order/payment/input", method=RequestMethod.POST)
	public String showPaymentInput() {
	 return "order/payment_input";
 }
	//支払方法登録画面表示
	@RequestMapping(path="/client/order/complete",method=RequestMethod.POST)
	public String showOrderComplete() {
		return "order/complete";
	}
	//注文入力フォーム情報初期化処理（届け先入力前に）
	@RequestMapping(path="/client/order/address/input", method=RequestMethod.POST)
	public String resetForm() {
		session
		return "redirect:/client/order/address/input";
	}
}
