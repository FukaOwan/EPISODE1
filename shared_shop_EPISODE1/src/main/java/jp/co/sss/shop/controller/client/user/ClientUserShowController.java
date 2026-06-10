package jp.co.sss.shop.controller.client.user;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import jakarta.servlet.http.HttpSession;
import jp.co.sss.shop.bean.UserBean;

@Controller
public class ClientUserShowController {
	/**
	 * セッション情報
	 */
	@Autowired
	HttpSession session;
	
	/**
	 * 会員詳細表示
	 * @param userBean
	 * @return
	 */
	@RequestMapping(path="/client/user/detail", method = { RequestMethod.GET, RequestMethod.POST })
	public String show(@ModelAttribute UserBean userBean) {
		BeanUtils.copyProperties(session.getAttribute("user"), userBean);
		return "client/user/detail";
	}
}
