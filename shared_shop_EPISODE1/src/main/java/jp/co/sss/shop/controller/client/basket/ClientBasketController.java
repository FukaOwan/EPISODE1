package jp.co.sss.shop.controller.client.basket;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import jakarta.servlet.http.HttpSession;
import jp.co.sss.shop.bean.BasketBean;
import jp.co.sss.shop.form.ItemForm;

@Controller
public class ClientBasketController {
	@RequestMapping(path="/client/basket/list", method=RequestMethod.GET)
	public String listItem(ItemForm itemForm,HttpSession session){
		
		 List<BasketBean> basketList=( List<BasketBean>) session.getAttribute("basketList");
		
			if(basketList==null){
				
				 List<BasketBean> basketList1=new ArrayList<>();
				 basketList= basketList1;
					
			}
			BasketBean basketBean1=new BasketBean(1,"りんご",30);
			basketList.add(basketBean1);
			
			
	        session.setAttribute("basketBeans", basketList);
	       
			
	
		   return"/client/basket/list";
		   
}
	@RequestMapping(path="/client/basket/list", method=RequestMethod.POST)
	public String returnBasket() {
		return "/client/basket/list";
	}

}
