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

	@Autowired
	ItemRepository itemRepository;

	List<BasketBean> basketList = new ArrayList<>();

	@RequestMapping(path = "/client/basket/add", method = RequestMethod.POST)
	public String addItem(Integer id, Model model, HttpSession session) {

		List<BasketBean> RbasketList = (List<BasketBean>) session.getAttribute("basketBeans");

		if (RbasketList == null) {
			List<BasketBean> RbasketList1 = new ArrayList<>();
			RbasketList = RbasketList1;
		}

		int i = 0;

		for (BasketBean b : basketList) {

			if (b.getId() == id) { //すでに籠に商品が入ってる場合//

				//商品の注文数を増やす処理が必要な場合//

				BasketBean basketBean = new BasketBean(b.getId(), b.getName(), b.getStock(), b.getOrderNum() + 1);
				basketList.remove(i);
				basketList.add(basketBean);

				break;

			}
			i++;
		}

		if (i == basketList.size()) { //籠に商品がなく、新しく追加する場合の処理。sizeでfor文が全部見られたかチェック//
			//Item item = itemRepository.findByIdAndDeleteFlag(itemForm.getId(), 0);

			Item item = itemRepository.getReferenceById(id);

			//BeanUtils.copyProperties(item, )
			BasketBean basketBean = new BasketBean(id, item.getName(), item.getStock());

			basketList.add(basketBean);

		}

		RbasketList.clear();

		for (int a = basketList.size(); a > 0; a--) {

			RbasketList.add(basketList.get(a - 1));

		}

		session.setAttribute("basketBeans", RbasketList);

		return "redirect:/client/basket/list";

	}

	@RequestMapping(path = "/client/basket/list")
	public String listItem(Model model, ItemForm itemForm, HttpSession session) {

		List<BasketBean> RbasketList = (List<BasketBean>) session.getAttribute("basketBeans");

		//	if(basketList==null){

		// List<BasketBean> basketList1=new ArrayList<>();
		//basketList= basketList1;

		//}

		//	if(basketList==null){

		// List<BasketBean> basketList1=new ArrayList<>();
		//basketList= basketList1;

		//}

		if (RbasketList != null) {

			if (RbasketList.get(0).getOrderNum() > RbasketList.get(0).getStock()) { //追加したら注文のほうが多くなってしまうので追加できない場合//

				// List<String> itemNameListZero=new ArrayList<>();
				// itemNameListZero.add(b.getName());
				model.addAttribute("itemNameListLessThan", RbasketList.get(0).getName());
				RbasketList.get(0).setOrderNum(RbasketList.get(0).getStock());
				basketList.get(basketList.size() - 1).setOrderNum(basketList.get(basketList.size() - 1).getStock());

			}

			else if (RbasketList.get(0).getStock() == 0) {

				// List<String> itemNameListLessThan=new ArrayList<>();
				// itemNameListLessThan.add(b.getName());
				model.addAttribute("itemNameListZero", RbasketList.get(0).getName());
				RbasketList.remove(0);
				basketList.remove(basketList.size() - 1);

			}
		}
		session.setAttribute("basketBeans", RbasketList);

		return "/client/basket/list";
	}

	@RequestMapping(path = "/client/basket/delete", method = RequestMethod.POST)
	public String deleteItem(ItemForm itemForm, HttpSession session) {

		List<BasketBean> RbasketList = (List<BasketBean>) session.getAttribute("basketBeans");
		int i = 0;

		for (BasketBean b : RbasketList) {

			if (b.getId() == itemForm.getId()) { //すでに籠に商品が入ってる場合//

				if (b.getOrderNum() == 1) {

					if (RbasketList.size() == 1) {
						basketList.clear();

						session.removeAttribute("basketBeans");

						return "redirect:/client/basket/list";

					}
					RbasketList.remove(i);

				} else {

					BasketBean basketBean = new BasketBean(b.getId(), b.getName(), b.getStock(), (b.getOrderNum() - 1));

					RbasketList.set(i, basketBean);
					basketList.set(RbasketList.size() - 1 - i, basketBean);

				}

			}
			i++;
		}

		session.setAttribute("basketBeans", RbasketList);
		return "redirect:/client/basket/list";
	}

	@RequestMapping(path = "/client/basket/allDelete", method = RequestMethod.POST)
	public String allDeleteItem(ItemForm itemForm, HttpSession session) {

		List<BasketBean> RbasketList = (List<BasketBean>) session.getAttribute("basketBeans");

		basketList.clear();

		session.removeAttribute("basketBeans");

		return "redirect:/client/basket/list";

	}

}