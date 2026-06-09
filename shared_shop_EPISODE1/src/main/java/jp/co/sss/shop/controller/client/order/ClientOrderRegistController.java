package jp.co.sss.shop.controller.client.order;

import java.util.List;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import jp.co.sss.shop.bean.BasketBean;
import jp.co.sss.shop.bean.UserBean;
import jp.co.sss.shop.entity.Item;
import jp.co.sss.shop.entity.OrderItem;
import jp.co.sss.shop.entity.User;
import jp.co.sss.shop.form.OrderForm;
import jp.co.sss.shop.repository.ItemRepository;
import jp.co.sss.shop.repository.OrderItemRepository;
import jp.co.sss.shop.repository.OrderRepository;
import jp.co.sss.shop.repository.UserRepository;

@Controller
public class ClientOrderRegistController {
	@Autowired
	OrderRepository orderRepository;
	@Autowired
	UserRepository userRepository;
	@Autowired
	ItemRepository itemRepository;
	@Autowired
	HttpSession session;
	@Autowired
	OrderItemRepository orderItemRepository;
	//注文入力フォーム情報初期化処理（届け先入力前に）
		@RequestMapping(path="/client/order/address/input", method=RequestMethod.POST)
		public String resetForm(OrderForm orderForm) {
			Integer id  = (Integer) ((UserBean) session.getAttribute("user")).getId();
			
			User user = userRepository.findByIdAndDeleteFlag(id, 0);
			
			BeanUtils.copyProperties(user, orderForm);
			orderForm.setPayMethod(1);
			session.setAttribute("orderForm", orderForm);
			return "redirect:/client/order/address/input";
		}

	//届け先入力画面表示
	@RequestMapping(path="/client/order/address/input",method=RequestMethod.GET)
	public String showAddressInput(@Valid @ModelAttribute OrderForm orderForm, BindingResult result, HttpSession sessionForOrder, Model model) {
		
		model.addAttribute("orderForm", sessionForOrder.getAttribute("orderForm"));
		/*if (result.hasErrors()) {
			sessionForOrder.invalidate();
		}*/
		return "client/order/address_input";
		
	}
	//届け先入力画面次へボタン押下時
	@RequestMapping(path="/client/order/payment/input", method = RequestMethod.POST)
	public String inputPayment(@Valid @ModelAttribute OrderForm orderForm,BindingResult result, HttpSession session) {
		
		
		if(result.hasErrors()) {
			 session.setAttribute("error", orderForm);
			return "redirect:/client/order/address/input";
		}else {
			return "redirect:/client/order/payment/input";
		}
		
	}
	
	
 	//支払方法選択画面表示
	@RequestMapping(path="/client/order/payment/input", method=RequestMethod.GET)
	public String showPaymentInput(HttpSession sessionForOrder, Model model) {
		
		model.addAttribute("orderInfo", sessionForOrder);
		
		return "client/order/payment_input";
	}
	//支払方法選択画面次へボタン押下時
	@RequestMapping(path="/client/order/check", method = RequestMethod.POST)
	public String moveToCheck(OrderForm orderForm, HttpSession sessionForOrder, Integer payMethod) {
		BeanUtils.copyProperties(sessionForOrder.getAttribute("orderForm"), orderForm);
		orderForm.setPayMethod(payMethod);
		
		sessionForOrder.setAttribute("orderInfoVerPayMethod", orderForm);
		return "redirect:/client/order/check";
	}
	
	
	//注文確認画面表示処理
	@RequestMapping(path="/client/order/check", method=RequestMethod.GET)
	public String showOrderCheck(HttpSession sessionForOrder, Model model, OrderForm orderForm) {
		
		List<BasketBean> basketLists = (List<BasketBean>) session.getAttribute("basketBeans");
		
		for (int i = 0; i <= basketLists.size() ;i++) {
			Item item = itemRepository.findByIdAndDeleteFlag(basketLists.get(i).getId(), 0);
		if (item.getStock() == 0){
			model.addAttribute("errormessage");
			//買い物かごから削除
			basketLists.remove(i);
			i--;
			
		}else if (basketLists.get(i).getStock() >= item.getStock()) {
			model.addAttribute("errormessage");
			//買い物かごを更新
			BasketBean basketbean = new BasketBean();
			BeanUtils.copyProperties(item, basketbean);
			basketLists.set(i, basketbean);
		}
		session.setAttribute("basketInfo", basketLists.get(i));
			
		Integer itemSum = 0;
		itemSum = basketLists.get(i).getStock() * item.getPrice();
		model.addAttribute("itemSum", itemSum);
		Integer allSum = 0;
		allSum += basketLists.get(i).getStock() * item.getPrice();
		model.addAttribute("allSum", allSum);

		}
		model.addAttribute("basketlists",basketLists);
		model.addAttribute("orderform", orderForm);
		return "client/order/check";
	}
	
	//注文確認画面「戻る」押下時
	@RequestMapping(path="/client/order/payment/back", method = RequestMethod.POST)
	public String returnAddressInput() {
		return "redirect:/client/order/address/input";
	}
	
	//ご注文の確定ボタン押下時処理
	@RequestMapping(path="client/order/complete", method=RequestMethod.POST)
	public String submitOrder(HttpSession sessionForOrder) {
		List<BasketBean> basketBean = (List<BasketBean>) session.getAttribute("basketInfo");
		for (int i = 0; i <= basketBean.size() ;i++) {
			Item item = itemRepository.findByIdAndDeleteFlag(basketBean.get(i).getId(), 0);
		if(basketBean.get(i).getStock() >= item.getStock()) {
			return "redirect:/client/order/check";
		}
		}
		OrderItem orderItem = new OrderItem();
		orderItem = orderItemRepository.save(orderItem);
		sessionForOrder.invalidate();
		session.removeAttribute("basketInfo");
		
		return "redirect:/client/order/complete";
	}
	
	//注文完了画面表示
	@RequestMapping(path="/client/order/complete",method=RequestMethod.GET)
	public String showOrderComplete() {
		return "client/order/complete";
	}
	

	
}
