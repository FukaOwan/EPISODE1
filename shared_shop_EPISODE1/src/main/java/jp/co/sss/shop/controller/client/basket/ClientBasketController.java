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
	@Autowired
	HttpSession session;

	//商品追加時の処理//
	@RequestMapping(path = "/client/basket/add", method = RequestMethod.POST)
	public String addItem(Integer id, Model model) {
		//RbasketListはbasketListの並び順を逆にしたリスト//
		List<BasketBean> RbasketList = (List<BasketBean>) session.getAttribute("basketBeans");
		//basketListは注文商品が入っているリスト//
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
	public String listItem(Model model) {

		List<BasketBean> RbasketList = (List<BasketBean>) session.getAttribute("basketBeans");
		List<BasketBean> basketList = (List<BasketBean>) session.getAttribute("BL");

		//買い物かごが空でない場合の処理//
		if (RbasketList != null) {
			int size1;
			int size2;
			List<String>  itemNameListLessThan= new ArrayList<>();
			List<String>  itemNameListZero= new ArrayList<>();


			
			do {
				int c = 0;
				size1 = RbasketList.size();
				//買い物かごの中身ごとの在庫数を調べる
				for (BasketBean b : RbasketList) {
					Item item = itemRepository.getReferenceById(b.getId());
					//買い物かごの中身の在庫数をデータベースに合わせる
					RbasketList.get(c).setStock(item.getStock());
					basketList.get(basketList.size() - 1 - c).setStock(item.getStock());

					//注文数が在庫より多い場合//
					if ((b.getOrderNum() > item.getStock()) && (item.getStock() != 0)) {
						//該当商品をエラーメッセージ出力用のmodelに入れる//
						itemNameListLessThan.add(b.getName());
						model.addAttribute("itemNameListLessThan", itemNameListLessThan);
						//注文数を在庫数に合わせる処理//
						RbasketList.get(c).setOrderNum(item.getStock());
						basketList.get(basketList.size() - 1 - c).setOrderNum(b.getStock());
					}

					//買い物かご中に在庫数がゼロの商品がある場合//
					else if (item.getStock() == 0) {
						if (RbasketList.size() != 1) {
							//該当商品をエラーメッセージ出力用のmodelに入れる//
							itemNameListZero.add(b.getName());
							model.addAttribute("itemNameListZero",itemNameListZero);
							RbasketList.remove(c);
							basketList.remove(basketList.size() - 1 - c);
							c = c - 1;
							break;
						} else {
							itemNameListZero.add(b.getName());
							model.addAttribute("itemNameListZero",itemNameListZero);
							basketList.clear();
							session.setAttribute("BL", basketList);
							session.removeAttribute("basketBeans");
							List<BasketBean> RbasketList1 = (List<BasketBean>) session.getAttribute("basketBeans");

							session.setAttribute("basketBeans", RbasketList1);

							
							return "/client/basket/list";
						}
					}
					c++;
				}
				size2 = RbasketList.size();
				//remove処理がされていたらもう一回for文を回す
			} while (size1 != size2);
		}
		session.setAttribute("basketBeans", RbasketList);
		session.setAttribute("BL", basketList);

		return "/client/basket/list";
	}

	//商品を削除する処理//
	@RequestMapping(path = "/client/basket/delete", method = RequestMethod.POST)
	public String deleteItem(ItemForm itemForm) {

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
	public String allDeleteItem() {

		List<BasketBean> RbasketList = (List<BasketBean>) session.getAttribute("basketBeans");
		List<BasketBean> basketList = (List<BasketBean>) session.getAttribute("BL");

		basketList.clear();
		session.setAttribute("BL", basketList);
		session.removeAttribute("basketBeans");

		return "redirect:/client/basket/list";

	}

}