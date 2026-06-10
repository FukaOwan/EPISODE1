package jp.co.sss.shop.controller.client.order;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import jakarta.servlet.http.HttpSession;

@Controller
public class ClientOrderRegistController {

	/**
	 * セッション情報
	 */
	@Autowired
	HttpSession session;

	//届け先入力画面表示
	@RequestMapping(path = "/client/order/address/input", method = RequestMethod.POST)
	public String showAddressInput(Model model) {
		return "order/address_input";
	}

	//支払方法選択画面表示
	@RequestMapping(path="/client/order/payment/input", method=RequestMethod.POST)
	public String showPaymentInput() {
		
//	セッションからクーポン情報を取得
//	Integer クーポン = ((UserBean) session.getAttribute("user")).getクーポン();	
	
	 return "/client/order/check";
 }

}
