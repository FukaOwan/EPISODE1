package jp.co.sss.shop.controller.client.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import jakarta.servlet.http.HttpSession;
import jp.co.sss.shop.form.UserForm;

@Controller
public class ClientUserRegistController {
	/**
	 * セッション情報
	 */
	@Autowired
	HttpSession session;
	
	/**
	 * 処理1　新規登録リンク　クリック時処理
	 * @param userForm
	 * @return
	 */
	@GetMapping("/client/user/regist/input/init")
	public String init() {
		// 入力フォーム情報を新規生成しセッションスコープに保存
		UserForm userForm = new UserForm();
		session.setAttribute("userForm", userForm);
		// 登録入力画面表示処理にリダイレクト
		return "redirect:/client/user/regist/input";
	}
	
	/**
	 * 処理3　登録画面表示処理
	 * @param model
	 * @return
	 */
	@GetMapping("/client/user/regist/input")
	public String regist_input(Model model) {
		// セッションスコープから入力フォーム情報を取得
		UserForm userForm = (UserForm) session.getAttribute("userForm");
		// 入力フォーム情報をリクエストスコープに設定
		model.addAttribute("userForm", userForm);
		// セッションスコープから入力エラー情報を取得
		BindingResult result = (BindingResult) session.getAttribute("result");
		// 入力エラー情報がある場合
		if (result != null) {
			model.addAttribute("result", result);
			session.removeAttribute("result");
		}
		// 登録画面表示
		return "client/user/regist_input";
	}
	
	@PostMapping("/client/user.regist/check")
	public String check() {
		return "redirect:/";
	}
}
