package jp.co.sss.shop.controller.client.order;

import java.time.LocalTime;
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

	LocalTime start = LocalTime.of(19, 0);
	LocalTime end = LocalTime.of(22, 0);

	//注文入力フォーム情報初期化処理（届け先入力前に）
	@RequestMapping(path = "/client/order/address/input", method = RequestMethod.POST)
	public String resetForm(OrderForm orderForm) {
		Integer id = (Integer) ((UserBean) session.getAttribute("user")).getId();

		User user = userRepository.findByIdAndDeleteFlag(id, 0);
		//ユーザーのクーポン情報を取得
		BeanUtils.copyProperties(user, orderForm);
		orderForm.setPayMethod(1);
		//時間でも判断する処理を書く
		LocalTime now = LocalTime.now();
		if (!now.isBefore(start) && now.isBefore(end)) {
			orderForm.setCouponFlag(0);
		} else {
			if (user.getCoupon() <= 0) {
				orderForm.setCouponFlag(0);
			} else {
				orderForm.setCouponFlag(1);
			}
		}

		session.setAttribute("orderForm", orderForm);
		return "redirect:/client/order/address/input";
	}

	//届け先入力画面表示
	@RequestMapping(path = "/client/order/address/input", method = RequestMethod.GET)
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
	@RequestMapping(path = "/client/order/payment/input", method = RequestMethod.POST)
	public String inputPayment(@Valid @ModelAttribute OrderForm orderForm, BindingResult result) {
		//セッションスコープから注文入力フォーム情報を取得 
		OrderForm orderform = (@Valid OrderForm) session.getAttribute("orderForm");
		orderForm.setPayMethod(orderform.getPayMethod());
		//ユーザーのクーポン情報を取得
		orderForm.setCouponFlag(orderform.getCouponFlag());
		session.setAttribute("orderForm", orderForm);

		if (result.hasErrors()) {
			//- 入力エラー情報をセッションスコープに設定
			session.setAttribute("orderFormErrors", result);
			return "redirect:/client/order/address/input";
		} else {
			return "redirect:/client/order/payment/input";
		}
	}

	//支払方法選択画面表示
	@RequestMapping(path = "/client/order/payment/input", method = RequestMethod.GET)
	public String showPaymentInput(Model model, HttpSession session) {

		model.addAttribute("orderForm", session.getAttribute("orderForm"));
		model.addAttribute("payMethod", 1);
		//リクエストスコープにクーポンの初期値0（利用しない）をセット
		OrderForm orderform = (OrderForm) session.getAttribute("orderForm");
		orderform.setUseCouponFlag(0);
		session.setAttribute("orderForm", orderform);
		return "client/order/payment_input";
	}

	//支払方法選択画面次へボタン押下時
	//ここから下couponFlag→useCouponFlagに変更
	@RequestMapping(path = "/client/order/check", method = RequestMethod.POST)
	public String moveToCheck(Integer payMethod, Integer useCouponFlag) {
		OrderForm orderForm = (OrderForm) session.getAttribute("orderForm");

		orderForm.setPayMethod(payMethod);
		//追記
		if (useCouponFlag != null) {
			orderForm.setUseCouponFlag(useCouponFlag);
		} else {
			orderForm.setUseCouponFlag(0);
		}
		//
		session.setAttribute("orderForm", orderForm);
		return "redirect:/client/order/check";
	}

	//注文確認画面表示処理
	@RequestMapping(path = "/client/order/check", method = RequestMethod.GET)
	public String showOrderCheck(Model model) {

		List<BasketBean> basketLists = (List<BasketBean>) session.getAttribute("basketBeans");
		OrderForm orderForm = (OrderForm) session.getAttribute("orderForm");
		User user = userRepository.getReferenceById(orderForm.getId());

		Integer total = 0;

		List<OrderItemBean> orderItems = new ArrayList<>();
		if (basketLists == null) {
			model.addAttribute("orderItemBeans", null);
		} else {
			for (int i = 0; i < basketLists.size(); i++) {
				BasketBean basket = basketLists.get(i);
				Item item = itemRepository.getReferenceById(basket.getId());

				//在庫チェック	
				if (item.getStock() == 0) {
					//エラーメッセージ用
					model.addAttribute("itemNameListZero", item.getName());
					//買い物かごから削除
					basketLists.remove(i);
					i--;

				} else if (basketLists.get(i).getOrderNum() > item.getStock()) {
					model.addAttribute("itemNameListLessThan", item.getName());
					//買い物かごを更新
					basket.setOrderNum(item.getStock());
				}
				//合計計算	
				total += basket.getOrderNum() * item.getPrice();
				OrderItemBean orderitem = new OrderItemBean();
				BeanUtils.copyProperties(item, orderitem);
				orderitem.setOrderNum(basket.getOrderNum());
				orderitem.setSubtotal(basket.getOrderNum() * item.getPrice());
				orderItems.add(orderitem);
			}

			//割引後合計計算//
			LocalTime now = LocalTime.now();
			if ((!now.isBefore(start) && now.isBefore(end)) || orderForm.getUseCouponFlag() == 1) {
				orderForm.setOffTotal(total * 9 / 10);
				model.addAttribute("couponFlag", 1);

			}

			//購入後に加算されるポイント計算//
			Integer totalPoint;
			if ((!now.isBefore(start) && now.isBefore(end)) || orderForm.getUseCouponFlag() == 1) {
				totalPoint = (int)((int) orderForm.getOffTotal() * 0.01);
			} else {
				totalPoint = total / 100;
			}
			model.addAttribute("total", total);
			session.setAttribute("totalPoint", totalPoint);
			session.setAttribute("offTotal", orderForm.getOffTotal());

			session.setAttribute("basketlists", basketLists);
			model.addAttribute("orderItemBeans", orderItems);
			session.setAttribute("orderItemBeans", orderItems);
			model.addAttribute("orderForm", session.getAttribute("orderForm"));
		}

		return "client/order/check";
	}

	//注文確認画面「戻る」押下時
	@RequestMapping(path = "/client/order/payment/back", method = RequestMethod.POST)
	public String returnAddressInput() {
		return "redirect:/client/order/address/input";
	}

	//ご注文の確定ボタン押下時処理
	@RequestMapping(path = "client/order/complete", method = RequestMethod.POST)
	public String submitOrder(Model model, HttpSession session) {

		List<BasketBean> basketBeans = (List<BasketBean>) session.getAttribute("basketlists");
		List<BasketBean> basketList = (List<BasketBean>) session.getAttribute("BL");

		Integer totalPoint = (Integer) session.getAttribute("totalPoint");

		//在庫チェック
		for (int i = 0; i < basketBeans.size(); i++) {
			Item item = itemRepository.getReferenceById(basketBeans.get(i).getId());
			if (basketBeans.get(i).getOrderNum() > item.getStock()) {
				model.addAttribute("itemNameListLessThan", item.getName());
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
		//オリジナル機能追記クーポン、タイムセールなし→0、2タイムセール時、1クーポンあり
		LocalTime now = LocalTime.now();
		if (orderform.getUseCouponFlag() == 1) {
			order.setCouponInfo(1);
		} else if (!now.isBefore(start) && now.isBefore(end)) {
			order.setCouponInfo(2);
		}else {
			order.setCouponInfo(0);
		}
		order = orderRepository.save(order);
		
		for (int i = 0; i < orderItemBeans.size(); i++) {
			Item item = itemRepository.getReferenceById(orderItemBeans.get(i).getId());
			OrderItem orderitem = new OrderItem();
			orderitem.setItem(item);
			orderitem.setPrice(orderItemBeans.get(i).getPrice());
			orderitem.setQuantity(orderItemBeans.get(i).getOrderNum());
			orderitem.setOrder(order);
			orderitem = orderItemRepository.save(orderitem);
			orderItemsList.add(orderitem);
			//商品情報の在庫数を注文個数分減らす
			item.setStock(item.getStock() - orderitem.getQuantity());
			item = itemRepository.save(item);
		}

		//user内のクーポン数減らす、ポイント加算//
		if (orderform.getUseCouponFlag() == 1) {
			user.setCoupon(user.getCoupon() - 1);
		}
		user.setPoint(user.getPoint() + totalPoint);
		
		//追記
		//ユーザービーンを生成→セッション"user"を呼び出して代入する
		UserBean userBean = (UserBean) session.getAttribute("user");
		
		//ユーザービーンにポイント情報を入れる
		userBean.setPoint(user.getPoint());
		//セッション"user"にユーザービーンをセットしなおす
		session.setAttribute("user", userBean);
		
		session.setAttribute("point", user.getPoint());
		
		if (user.getPoint() >= 100) {
			user.setPoint(user.getPoint() - 100);
			user.setCoupon(user.getCoupon() + 1);
		}
		user = userRepository.save(user);
		//追記
		//セッション破棄
		session.removeAttribute("orderForm");
		session.removeAttribute("orderItemBeans");
		session.removeAttribute("totalPoint");
		session.removeAttribute("basketelists");
		session.removeAttribute("basketBeans");
		session.removeAttribute("BL");
		
		return "redirect:/client/order/complete";
	}

	//注文完了画面表示
	@RequestMapping(path = "/client/order/complete", method = RequestMethod.GET)
	public String showOrderComplete(Model model, HttpSession session) {

		//注文完了画面に会員が持っているポイントの情報を持っていく//
		Integer point = (Integer) session.getAttribute("point");
		model.addAttribute("point", point);
		session.removeAttribute("point");

		return "client/order/complete";
	}
}