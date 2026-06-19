package jp.co.sss.shop.controller.client.order;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import jakarta.servlet.http.HttpSession;
import jp.co.sss.shop.bean.OrderBean;
import jp.co.sss.shop.bean.OrderItemBean;
import jp.co.sss.shop.bean.UserBean;
import jp.co.sss.shop.entity.Order;
import jp.co.sss.shop.entity.OrderItem;
import jp.co.sss.shop.repository.OrderRepository;
import jp.co.sss.shop.service.BeanTools;
import jp.co.sss.shop.service.PriceCalc;

@Controller
public class ClientOrderShowController {

	//	注文情報
	@Autowired
	OrderRepository orderRepository;

	//	セッション
	@Autowired
	HttpSession session;

	//	合計金額計算サービス
	@Autowired
	PriceCalc priceCalc;

	//	Entity、Form、Bean間のデータ生成、コピーサービス
	@Autowired
	BeanTools beanTools;

	/**
	 * 処理①注文一覧表示処理
	 * 
	 * ログイン中の会員が過去に注文した情報を取得し
	 * 注文一覧画面を表示する
	 * 
	 * @param model
	 * @return 注文一覧画面
	 */
	@RequestMapping(path = "/client/order/list", method = { RequestMethod.GET, RequestMethod.POST })
	public String showOrderList(Model model) {

		//	ログイン中の会員情報をセッションから取得
		UserBean loginUser = (UserBean) session.getAttribute("user");

		//	ログイン中の会員の注文情報を新しい順で取得
		List<Order> orderList = orderRepository.findByUser_IdOrderByInsertDateDescIdDesc(loginUser.getId());

		// 画面表示用の注文情報リストを生成
		List<OrderBean> orderBeanList = new ArrayList<OrderBean>();
		// 注文ごとの表示情報と合計金額を作成
		for (Order order : orderList) {

			// BeanToolsクラスのcopyEntityToOrderBeanメソッドを使用して表示する注文情報を生成
			OrderBean orderBean = beanTools.copyEntityToOrderBean(order);

			//orderレコードから紐づくOrderItemのListを取り出す
			List<OrderItem> orderItemList = order.getOrderItemsList();

			//PriceCalcクラスのorderItemPriceTotalメソッドを使用して合計金額を算出
			int total = priceCalc.orderItemPriceTotal(orderItemList);

			//クーポンやタイムセールによる割引処理がある場合
			if(order.getCouponInfo()!= 0) {
				total = (int) ((int)total * 0.9);
			}
			

			//合計金額のセット
			orderBean.setTotal(total);
			orderBeanList.add(orderBean);
		}

		// 注文情報リストをViewへ渡す
		model.addAttribute("orders", orderBeanList);
		
//		画面の名前を入れる（伊藤）
		model.addAttribute("currentPage", "order-list");

		return "client/order/list";
	}

	/**
	 * 処理②注文詳細画面表示処理
	 * 
	 * 選択された注文の詳細情報を取得し
	 * 注文詳細画面へ表示する
	 * 
	 * @param id
	 * @param model
	 * @return 注文詳細画面
	 */
	@RequestMapping(path = "/client/order/detail/{id}")
	public String showOrder(@PathVariable int id, Model model) {

		// 表示用の注文情報を生成
		Order order = orderRepository.getReferenceById(id);

		// 表示する注文情報を生成
		OrderBean orderBean = beanTools.copyEntityToOrderBean(order);

		// 注文商品情報を取得
		List<OrderItemBean> orderItemBeanList = beanTools.generateOrderItemBeanList(order.getOrderItemsList());

		// 合計金額を算出
		int total = priceCalc.orderItemBeanPriceTotalUseSubtotal(orderItemBeanList);
		if(order.getCouponInfo()!= 0) {
			total = (int) ((int)total * 0.9);
		}
		model.addAttribute("couponInfo",order.getCouponInfo() );
		// 注文情報をViewへ渡す(注文情報 商品情報 合計金額)
		model.addAttribute("order", orderBean);
		model.addAttribute("orderItemBeans", orderItemBeanList);
		model.addAttribute("total", total);

		//注文詳細HTML
		return "client/order/detail";
	}

}
