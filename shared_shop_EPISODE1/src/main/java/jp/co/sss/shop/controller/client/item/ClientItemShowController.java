package jp.co.sss.shop.controller.client.item;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import jp.co.sss.shop.bean.ItemBean;
import jp.co.sss.shop.entity.Item;
import jp.co.sss.shop.repository.ItemRepository;
import jp.co.sss.shop.service.BeanTools;

/**
 * 商品管理 一覧表示機能(一般会員用)のコントローラクラス
 *
 * @author SystemShared
 */
@Controller
public class ClientItemShowController {
	/**
	 * 商品情報
	 */
	@Autowired
	ItemRepository itemRepository;

	/**
	 * Entity、Form、Bean間のデータコピーサービス
	 */
	@Autowired
	BeanTools beanTools;
	
	/**
	 * トップ画面 表示処理
	 *
	 * @param model    Viewとの値受渡し
	 * @return "index" トップ画面
	 */
	@RequestMapping(path = "/" , method = { RequestMethod.GET, RequestMethod.POST })
	public String index(Model model) {
	
		return "index";
	}
	
	/**
	 * 商品詳細画面 表示処理
	 *
	 * @param model    Viewとの値受渡し
	 * @return "client/item/detail" 商品詳細画面
	 */
	@GetMapping("/client/item/detail/{id}")
	public String itemDetail(@PathVariable Integer id,Model model) {
		Item item = itemRepository.getReferenceById(id);
		
		ItemBean itemBean = new ItemBean();
		BeanUtils.copyProperties(item, itemBean);
		
		itemBean.setCategoryName(item.getCategory().getName());
		
		model.addAttribute("item",itemBean);
		return "client/item/detail";
	}
	
	/**
	 * 商品一覧画面 表示処理（「戻るボタン」を機能させるための仮）
	 *
	 * @return "/client/item/list" 商品一覧画面
	 */
	@PostMapping(path = "/client/item/list/1")
	public String itemList(){
		return "/client/item/list";
	}
}