package jp.co.sss.shop.controller.client.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.SessionAttribute;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import jp.co.sss.shop.form.UserForm;

@Controller
public class ClientUserRegistController {
	/**
	 * セッション情報
	 */
	@Autowired
	HttpSession session;
	
	@GetMapping("/client/user/regist/input/init")
	public String init() {
		UserForm userForm = new UserForm();
		session.setAttribute("userForm", userForm);
		return "redirect:client/user/regist/input";
	}
	
	@GetMapping("client/user/regist/input")
	public String regist_input(@Valid @SessionAttribute UserForm userForm, BindingResult result, @ModelAttribute Model model) {
		model.addAttribute("userForm", userForm);
		if (result.hasErrors()) {
			
			session.removeAttribute("result");
		}
		
		
		return "client/user/regist_input";
	}
}
