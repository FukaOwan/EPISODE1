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
	
	@RequestMapping(path="/client/basket/add", method=RequestMethod.POST)
	public String addItem(Model model,ItemForm itemForm,HttpSession session){
	
		 List<BasketBean> basketList=( List<BasketBean>) session.getAttribute("basketBeans");
		 
		if(basketList==null){
			
			 List<BasketBean> basketList1=new ArrayList<>();
			 basketList= basketList1;
				
		}
		
		
		 int i=0;
		
		for(BasketBean b:basketList) {
			
			if(b.getId()==itemForm.getId()){ //すでに籠に商品が入ってる場合//
				
				if(b.getOrderNum()>=b.getStock()) { //追加したら注文のほうが多くなってしまうので追加できない場合//
					
					if(b.getOrderNum()==b.getStock()) {
						
					 List<String> itemNameListZero=new ArrayList<>();
					 itemNameListZero.add(b.getName());
					 model.addAttribute("itemNameListZero",itemNameListZero);
					}
					
					else {
						
						 List<String> itemNameListLessThan=new ArrayList<>();
						 itemNameListLessThan.add(b.getName());
						 model.addAttribute("itemNameListLessThan",itemNameListLessThan);
						 
						}
					break;
					}
				

				
				}
				
				else {//商品の注文数を増やす処理が必要な場合//
					
					BasketBean basketBean=new BasketBean(b.getId(),b.getName(),b.getStock(),(b.getOrderNum()+1));
					basketList.remove(i);
					basketList.add(basketBean);
					
					break;
					
				}
			i++;
		}
			

			
			
		
		if(i==basketList.size()) { //籠に商品がなく、新しく追加する場合の処理。sizeでfor文が全部見られたかチェック//
			Item item = itemRepository.findByIdAndDeleteFlag(itemForm.getId(), 0);
			//BeanUtils.copyProperties(item, )
			BasketBean basketBean=new BasketBean(item.getId(),item.getName(),item.getStock());

			basketList.add(basketBean);
			
		}
		
		 
        session.setAttribute("basketBeans", basketList);
        
        return"redirect:/client/basket/list";

        

		
}
	
	
	
	
	
	

	@RequestMapping(path="/client/basket/list", method=RequestMethod.GET)
	public String listItem(ItemForm itemForm,HttpSession session){
		
		 List<BasketBean> basketList=( List<BasketBean>) session.getAttribute("basketBeans");
		 
			if(basketList==null){
				
				 List<BasketBean> basketList1=new ArrayList<>();
				 basketList= basketList1;
				
			}
		
			 
	        session.setAttribute("basketBeans", basketList);
	        

			
	
		   return"/client/basket/list";
}
	
	@RequestMapping(path="/client/basket/delete", method=RequestMethod.POST)
    public String deleteItem(ItemForm itemForm,HttpSession session){

		 List<BasketBean> basketList=(List<BasketBean>) session.getAttribute("basketBeans");
			int i=0;

for(BasketBean b:basketList) {
	
			
			if(b.getId()==itemForm.getId()){ //すでに籠に商品が入ってる場合//
				
				
					if(b.getOrderNum()==1) {
					basketList.remove(i);
					
					if(basketList==null) { session.removeAttribute("basketBeans");
					
					return"redirect:/client/basket/list";

					}
					
					break;
					}
					else {
						
						BasketBean basketBean=new BasketBean(b.getId(),b.getName(),b.getStock(),(b.getOrderNum()-1));

						basketList.set(i, basketBean);
						
						break;
					}
					
				}
			i++;
		}
	
session.setAttribute("basketBeans", basketList);
return"redirect:/client/basket/list";

	}
	
	
	

	
	@RequestMapping(path="/client/basket/allDelete", method=RequestMethod.POST)
    public String allDeleteItem(ItemForm itemForm,HttpSession session){

		 List<BasketBean> basketList=( List<BasketBean>) session.getAttribute("basketBeans");

    session.removeAttribute("basketBeans");


	
return"redirect:/client/basket/list";


	}
	
	
	
	
	
	
	
	
	}