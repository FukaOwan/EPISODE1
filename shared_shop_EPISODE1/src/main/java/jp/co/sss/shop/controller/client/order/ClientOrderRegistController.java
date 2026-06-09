package jp.co.sss.shop.controller.client.order;

import java.util.ArrayList;
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
import jp.co.sss.shop.bean.OrderItemBean;
import jp.co.sss.shop.bean.UserBean;
import jp.co.sss.shop.entity.Item;
import jp.co.sss.shop.entity.Order;
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
	HttpSession session;
	@Autowired
	OrderRepository orderRepository;
	@Autowired
	UserRepository userRepository;
	@Autowired
	ItemRepository itemRepository;
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
	public String showAddressInput(Model model) {
		OrderForm form = (OrderForm) session.getAttribute("orderForm");
		
		model.addAttribute("orderForm", form);
		BindingResult errors = (BindingResult) session.getAttribute("orderFormErrors");
		if (errors != null) {
			model.addAttribute("orderForm", form);
			model.addAttribute("org.springframework.validation.BindingResult.orderForm", errors);

			session.removeAttribute("orderFormErrors");
		}
		return "client/order/address_input";
		
	}
	//届け先入力画面次へボタン押下時
	@RequestMapping(path="/client/order/payment/input", method = RequestMethod.POST)
	public String inputPayment(@Valid @ModelAttribute OrderForm orderForm, BindingResult result) {
		//・セッションスコープから注文入力フォーム情報を取得 
		orderForm = (@Valid OrderForm) session.getAttribute("orderForm");
		
		 if(result.hasErrors()) {
			 //- 入力エラー情報をセッションスコープに設定->画面遷移はうまくいっているが、エラーが出ない
			session.setAttribute("orderForm", orderForm);
			session.setAttribute("orderFormErrors", result);
			return "redirect:/client/order/address/input";
		}else {
			return "redirect:/client/order/payment/input";
		}
	}
	
	
 	//支払方法選択画面表示
	@RequestMapping(path="/client/order/payment/input", method=RequestMethod.GET)
	public String showPaymentInput(Model model) {
		
		model.addAttribute("orderForm", session.getAttribute("orderForm"));
		
		return "client/order/payment_input";
	}
	
	//支払方法選択画面次へボタン押下時
	@RequestMapping(path="/client/order/check", method = RequestMethod.POST)
	public String moveToCheck(Integer payMethod) {
		OrderForm orderForm = (OrderForm) session.getAttribute("orderForm");
		orderForm.setPayMethod(payMethod);
		
		session.setAttribute("orderForm", orderForm);
		return "redirect:/client/order/check";
	}
	
	//注文確認画面表示処理
	@RequestMapping(path="/client/order/check", method=RequestMethod.GET)
	public String showOrderCheck(Model model) {
		
		List<BasketBean> basketLists = (List<BasketBean>) session.getAttribute("basketBeans");
		
		Integer total = 0;

		List<OrderItemBean> orderItems = new ArrayList<>();
		
		for (int i = 0; i < basketLists.size() ;i++) {
			BasketBean basket = basketLists.get(i);
			Item item = itemRepository.getReferenceById(basket.getId());
			
		//在庫チェック	
		if (item.getStock() == 0){
			model.addAttribute("errormessage");
			//買い物かごから削除
			basketLists.remove(i);
			i--;
			
		}else if (basketLists.get(i).getOrderNum() > item.getStock()) {
			model.addAttribute("errormessage");
			//買い物かごを更新
			basket.setOrderNum(item.getStock());
		}
			
		
		total = basket.getOrderNum() * item.getPrice();
		model.addAttribute("total", total);
		OrderItemBean orderitem = new OrderItemBean();
		BeanUtils.copyProperties(item, orderitem);
		orderitem.setOrderNum(basket.getOrderNum());
		orderitem.setSubtotal(basket.getOrderNum() * item.getPrice());
		orderItems.add(orderitem);
		}
		
		session.setAttribute("basketlists", basketLists);
		model.addAttribute("orderItemBeans", orderItems);
		session.setAttribute("orderItemBeans", orderItems);
		model.addAttribute("orderForm", session.getAttribute("orderForm"));
		return "client/order/check";
	}
	
	//注文確認画面「戻る」押下時
	@RequestMapping(path="/client/order/payment/back", method = RequestMethod.POST)
	public String returnAddressInput() {
		return "redirect:/client/order/address/input";
	}
	
	//ご注文の確定ボタン押下時処理
	@RequestMapping(path="client/order/complete", method=RequestMethod.POST)
	public String submitOrder() {
		
		List<BasketBean> basketBeans = (List<BasketBean>) session.getAttribute("basketlists");
		
		for (int i = 0; i < basketBeans.size() ;i++) {
			Item item = itemRepository.getReferenceById(basketBeans.get(i).getId());
		if(basketBeans.get(i).getStock() > item.getStock()) {
			return "redirect:/client/order/check";
			}
		}
		//DB登録用エンティティを生成
		List<OrderItemBean> orderItemBeans = (List<OrderItemBean>) session.getAttribute("orderItemBeans");
		List<OrderItem> orderItemsList = new ArrayList<>();
		
		Order order = new Order();
		OrderForm orderform = (OrderForm) session.getAttribute("orderForm");
		User user = userRepository.getReferenceById(orderform.getId());
		order.setAddress(orderform.getAddress());
		order.setPostalCode(orderform.getPostalCode());
		order.setName(orderform.getName());
		order.setPhoneNumber(orderform.getPhoneNumber());
		order.setPayMethod(orderform.getPayMethod());
		order.setUser(user);
		order = orderRepository.save(order);
		
		for(int i = 0; i <orderItemBeans.size() ; i++) {
	 		Item item = itemRepository.getReferenceById(orderItemBeans.get(i).getId());
			OrderItem orderitem = new OrderItem();
			orderitem.setItem(item);
			orderitem.setPrice(orderItemBeans.get(i).getSubtotal());
			orderitem.setQuantity(orderItemBeans.get(i).getOrderNum());
			orderitem.setOrder(order);
			
			orderitem = orderItemRepository.save(orderitem);
			orderItemsList.add(orderitem);
		}
		session.removeAttribute("orderForm");
		session.removeAttribute("orderItemBeans");
		
		return "redirect:/client/order/complete";
	}
	
	//注文完了画面表示
	@RequestMapping(path="/client/order/complete",method=RequestMethod.GET)
	public String showOrderComplete() {
		return "client/order/complete";
	}
}
