package jp.co.sss.shop.controller.client.user;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;

import jakarta.servlet.http.HttpSession;
import jp.co.sss.shop.bean.UserBean;

@Controller
public class ClientUserShowController {
	/**
	 * セッション情報
	 */
	@Autowired
	HttpSession session;
	
	@GetMapping("/client/user/detail")
	public String show(@ModelAttribute UserBean userBean) {
		BeanUtils.copyProperties(session.getAttribute("user"), userBean);
		return "client/user/detail";
	}
}
