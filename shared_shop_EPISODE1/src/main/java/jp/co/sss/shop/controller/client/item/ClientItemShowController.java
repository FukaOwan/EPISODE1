package jp.co.sss.shop.controller.client.item;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import jp.co.sss.shop.repository.ItemRepository;
import jp.co.sss.shop.service.BeanTools;
import jp.co.sss.shop.util.Constant;

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
	
//	商品一覧ボタン押下後処理(新着順)（追記：春山）
	@RequestMapping(path = "/client/item/list/1" , method = RequestMethod.GET)
	public String itemstop(Model model,Pageable pageable) {
		model.addAttribute("items",itemRepository.findByDeleteFlagOrderByInsertDateDescPage(Constant.NOT_DELETED, pageable));
		return "client/item/list";
	}
//	商品一覧表示（売れ筋順）（追記：春山）
//	@RequestMapping(path = "/client/item/list/{sortType}?categoryId={カテゴリID}", method = RequestMethod.GET)
//	public String sortItem(@PathVariable Integer id ,Model model) {
//		model.addAttribute("items",itemRepository.);
//		return "client/item/list";
//	}
}
