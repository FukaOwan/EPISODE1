package jp.co.sss.shop.controller.client.item;

import java.util.List;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import jp.co.sss.shop.bean.ItemBean;
import jp.co.sss.shop.entity.Item;
import jp.co.sss.shop.entity.OrderItem;
import jp.co.sss.shop.repository.ItemRepository;
import jp.co.sss.shop.repository.OrderItemRepository;
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
	@Autowired
	OrderItemRepository orderItemRepository;

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
	@RequestMapping(path = "/", method = { RequestMethod.GET, RequestMethod.POST })
	public String index(Model model, Pageable pageable) {
	    // 売れ筋順（売上がないものは新着順）でデータを取得(追記：春山)
	    Page<Item> item = itemRepository.findByDeleteFlagOrderByQuantityDescPage(Constant.NOT_DELETED, pageable);
	    
	    model.addAttribute("items", item);
	    
//	    トップ画面のサブタイトル表示用
	    List<OrderItem> orderItem = orderItemRepository.findAll();
	    model.addAttribute("orderItem", orderItem);
	    
//		画面の名前（伊藤）
		model.addAttribute("currentPage", "top");
		return "index";
	}
	
//	商品一覧（追記：春山）
	@RequestMapping(path = "/client/item/list/{sortType}",  method = { RequestMethod.GET, RequestMethod.POST })
	public String categoryList(@PathVariable Integer sortType,@RequestParam (required = false)Integer categoryId, Model model) {
		
//		新着順表示
		if(sortType==1 && (categoryId==null || categoryId==0)) {
			model.addAttribute("items",itemRepository.findByDeleteFlagOrderByInsertDateDesc(Constant.NOT_DELETED));
		
//		売れ筋順表示	
		}else if(sortType==2 && (categoryId==null || categoryId==0)){
			model.addAttribute("items",itemRepository.findByDeleteFlagOrderByQuantityDesc(Constant.NOT_DELETED));

//		カテゴリ別検索 + 新着順表示
		}else if(sortType==1 && categoryId!=null) {
			model.addAttribute("items",itemRepository.findByDeleteFlagAndCategoryOrderByInsertDateDesc(Constant.NOT_DELETED,categoryId));
			model.addAttribute("categoryId",categoryId);
		
//		カテゴリ別検索 + 売れ筋順表示
		}else if(sortType==2 && categoryId!=null) {
			model.addAttribute("items",itemRepository.findByDeleteFlagAndCategoryOrderByQuantityDesc(Constant.NOT_DELETED,categoryId));
			model.addAttribute("categoryId",categoryId);
			
		}
		
//		画面の名前（伊藤）
		model.addAttribute("currentPage", "item-list");
		return "client/item/list";	
	}
	
	@RequestMapping(path = "/client/item/search",  method = { RequestMethod.GET, RequestMethod.POST })
	public String itemSearch(String name, Model model) {
		model.addAttribute("items", itemRepository.findByDeleteFlagAndNameContaining(Constant.NOT_DELETED, name));
		return "client/item/list";
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
	
}











