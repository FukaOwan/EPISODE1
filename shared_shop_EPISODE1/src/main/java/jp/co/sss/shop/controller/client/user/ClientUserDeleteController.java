package jp.co.sss.shop.controller.client.user;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import jakarta.servlet.http.HttpSession;
import jp.co.sss.shop.bean.UserBean;
import jp.co.sss.shop.entity.User;
import jp.co.sss.shop.form.UserForm;
import jp.co.sss.shop.repository.UserRepository;
import jp.co.sss.shop.util.Constant;

@Controller 
public class ClientUserDeleteController {
	
//	会員情報リポジトリ

	@Autowired
	UserRepository userRepository;
	
//	セッション
	
	@Autowired
	HttpSession session;
	
//	削除ボタン　押下時処理
	
	@RequestMapping(path = "/client/user/delete/check", method = RequestMethod.POST)
	 public String deleteCheck() {
		UserBean loginUser = (UserBean) session.getAttribute("user");
		   User user = userRepository.findByIdAndDeleteFlag(loginUser.getId(),Constant.NOT_DELETED);

		  
		   UserForm userForm = new UserForm();

		   BeanUtils.copyProperties(user, userForm);

			//情報フォームをセッションに保持
		   session.setAttribute("userForm", userForm);


			// 削除確認画面　表示
			return "redirect:/client/user/delete/check";
		}


	
	
//	削除確認画面表示処理
	@RequestMapping(path = "/client/user/delete/check", method = RequestMethod.GET)
	public String updateInput(Model model) {
    UserForm userForm =(UserForm) session.getAttribute("userForm");

	    if (userForm == null) {
	        return "redirect:/syserror";
	    }

	    model.addAttribute("userForm", userForm);

	    return "client/user/delete_check";
	}

	
//	削除ボタン　押下処理
	@RequestMapping(path = "/client/user/delete/complete", method = RequestMethod.POST)
	public String l() {
		return "redirect:/client/user/delete/check";
	}
	
//	削除完了画面表示

    @RequestMapping(path = "/client/user/delete/complete", method = RequestMethod.GET)
     public String deleteCompleteFinish() {

	return "client/user/delete_complete";
}

}
