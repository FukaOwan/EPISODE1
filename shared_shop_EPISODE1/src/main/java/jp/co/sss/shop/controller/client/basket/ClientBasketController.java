package jp.co.sss.shop.controller.client.basket;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import jakarta.servlet.http.HttpSession;
import jp.co.sss.shop.bean.BasketBean;
import jp.co.sss.shop.entity.Item;
import jp.co.sss.shop.form.ItemForm;
import jp.co.sss.shop.repository.ItemRepository;

@Controller

public class ClientBasketController {

	//リポジトリー設定//
	@Autowired
	ItemRepository itemRepository;

	//商品追加時の処理//
	@RequestMapping(path = "/client/basket/add", method = RequestMethod.POST)
	public String addItem(Integer id, Model model, HttpSession session) {

		//basketListは注文商品が入っているリスト//
		List<BasketBean> RbasketList = (List<BasketBean>) session.getAttribute("basketBeans");
		//RbasketListはbasketListの並び順を逆にしたリスト//
		List<BasketBean> basketList = (List<BasketBean>) session.getAttribute("BL");

		//買い物かごが空の場合の処理//
		if (RbasketList == null) {
			List<BasketBean> RbasketList1 = new ArrayList<>();
			List<BasketBean> basketList1 = new ArrayList<>();
			RbasketList = RbasketList1;
			basketList = basketList1;
		}

		int i = 0;

		//買い物かごに物が入っているときの商品追加処理//
		if (basketList != null) {
			//basketListに追加したい商品が入っているか探す//
			for (BasketBean b : basketList) {
				//すでに籠に追加商品が入ってる場合//
				if (b.getId() == id) {
					//追加商品の注文数を1増やす//
					BasketBean basketBean = new BasketBean(b.getId(), b.getName(), b.getStock(), b.getOrderNum() + 1);
					basketList.remove(i);
					basketList.add(basketBean);
					break;
				}
				i++;
			}
		}

		//籠に追加商品がない場合の商品追加処理//
		//size()でfor文が最後まで見られたかチェック//
		if (i == basketList.size() || basketList == null) {
			Item item = itemRepository.getReferenceById(id);
			BasketBean basketBean = new BasketBean(id, item.getName(), item.getStock());
			basketList.add(basketBean);
		}

		//RbasketListにbasketListを順番逆に入れる//
		RbasketList.clear();
		for (int a = basketList.size(); a > 0; a--) {
			RbasketList.add(basketList.get(a - 1));
		}
		
		session.setAttribute("BL", basketList);
		session.setAttribute("basketBeans", RbasketList);
		return "redirect:/client/basket/list";
	}

	//買い物かご一覧を表示する処理//
	@RequestMapping(path = "/client/basket/list")
	public String listItem(Model model, ItemForm itemForm, HttpSession session) {

		List<BasketBean> RbasketList = (List<BasketBean>) session.getAttribute("basketBeans");
		List<BasketBean> basketList = (List<BasketBean>) session.getAttribute("BL");

		//買い物かごが空の場合の処理//
		if (RbasketList != null) {
			//注文数が在庫より多い場合//
			if (RbasketList.get(0).getOrderNum() > RbasketList.get(0).getStock()) {
				//該当商品をエラーメッセージ出力用のmodelに入れる//
				model.addAttribute("itemNameListLessThan", RbasketList.get(0).getName());
				//注文数を在庫数に合わせる処理//
				RbasketList.get(0).setOrderNum(RbasketList.get(0).getStock());
				basketList.get(basketList.size() - 1).setOrderNum(basketList.get(basketList.size() - 1).getStock());
			}

			//買い物かご中に在庫数がゼロの商品がある場合//
			else if (RbasketList.get(0).getStock() == 0) {
				//該当商品をエラーメッセージ出力用のmodelに入れる//
				model.addAttribute("itemNameListZero", RbasketList.get(0).getName());
				RbasketList.remove(0);
				basketList.remove(basketList.size() - 1);
			}
		}
		session.setAttribute("basketBeans", RbasketList);
		session.setAttribute("BL", basketList);

		return "/client/basket/list";
	}

	//商品を削除する処理//
	@RequestMapping(path = "/client/basket/delete", method = RequestMethod.POST)
	public String deleteItem(ItemForm itemForm, HttpSession session) {

		List<BasketBean> RbasketList = (List<BasketBean>) session.getAttribute("basketBeans");
		List<BasketBean> basketList = (List<BasketBean>) session.getAttribute("BL");

		int i = 0;

		//買い物かごの中の削除対象商品を探す//
		for (BasketBean b : RbasketList) {
			if (b.getId() == itemForm.getId()) {
				//該当商品の注文数が１の場合//
				if (b.getOrderNum() == 1) {
					//買い物かご中にその商品しかない場合//
					if (RbasketList.size() == 1) {
						basketList.clear();
						session.setAttribute("BL", basketList);
						session.removeAttribute("basketBeans");
						return "redirect:/client/basket/list";
					}
					//その他の商品もある場合//
					basketList.remove(RbasketList.size() - 1 - i);
					RbasketList.remove(i);
					session.setAttribute("BL", basketList);
					session.setAttribute("basketBeans", RbasketList);
					return "redirect:/client/basket/list";

					//注文数が１より多い場合//
				} else {
					BasketBean basketBean = new BasketBean(b.getId(), b.getName(), b.getStock(), (b.getOrderNum() - 1));
					RbasketList.set(i, basketBean);
					basketList.set(RbasketList.size() - 1 - i, basketBean);
				}
			}
			i++;
		}
		session.setAttribute("BL", basketList);
		session.setAttribute("basketBeans", RbasketList);
		return "redirect:/client/basket/list";
	}

	//買い物かごを空にするボタンの処理//
	@RequestMapping(path = "/client/basket/allDelete", method = RequestMethod.POST)
	public String allDeleteItem(ItemForm itemForm, HttpSession session) {

		List<BasketBean> RbasketList = (List<BasketBean>) session.getAttribute("basketBeans");
		List<BasketBean> basketList = (List<BasketBean>) session.getAttribute("BL");

		basketList.clear();
		session.setAttribute("BL", basketList);
		session.removeAttribute("basketBeans");

		return "redirect:/client/basket/list";

	}

}