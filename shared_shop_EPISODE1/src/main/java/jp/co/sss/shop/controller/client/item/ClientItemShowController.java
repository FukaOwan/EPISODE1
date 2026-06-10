package jp.co.sss.shop.controller.client.item;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import jp.co.sss.shop.entity.Item;
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
	public String index(Model model,Pageable pageable) {
		
//		トップ画面 OderItemから商品情報取得
		List<Item> item =itemRepository.findByDeleteFlagAndQuantity(Constant.NOT_DELETED);
		
//		トップ画面商品表示分岐
		if(item !=null) {
			model.addAttribute("items",itemRepository.findByDeleteFlagOrderByQuantityDescPage(Constant.NOT_DELETED,pageable));
		}else if(item ==null){
			model.addAttribute("items",itemRepository.findByDeleteFlagOrderByInsertDateDescPage(Constant.NOT_DELETED, pageable));
		}
		return "index";
	}
	
//	商品一覧（追記：春山）
	@RequestMapping(path = "/client/item/list/{sortType}", method = RequestMethod.GET)
	public String categoryList(@PathVariable Integer sortType,@RequestParam (required = false)Integer categoryId, Model model,Pageable pageable) {
		
//		新着順表示
		if(sortType==1 && (categoryId==null || categoryId==0)) {
			model.addAttribute("items",itemRepository.findByDeleteFlagOrderByInsertDateDescPage(Constant.NOT_DELETED, pageable));
		
//		売れ筋順表示	
		}else if(sortType==2 && (categoryId==null || categoryId==0)){
			model.addAttribute("items",itemRepository.findByDeleteFlagOrderByQuantityDescPage(Constant.NOT_DELETED,pageable));

//		カテゴリ別検索 + 新着順表示
		}else if(sortType==1 && categoryId!=null) {
			model.addAttribute("items",itemRepository.findByDeleteFlagAndCategoryOrderByInsertDateDescPage(Constant.NOT_DELETED,categoryId,pageable));
			model.addAttribute("categoryId",categoryId);
		
//		カテゴリ別検索 + 売れ筋順表示
		}else if(sortType==2 && categoryId!=null) {
			model.addAttribute("items",itemRepository.findByDeleteFlagAndCategoryOrderByQuantityDescPage(Constant.NOT_DELETED,categoryId,pageable));
			model.addAttribute("categoryId",categoryId);
			
		}
		
		return "client/item/list";	
	}



}
