package jp.co.sss.shop.controller.client.user;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import jp.co.sss.shop.bean.UserBean;
import jp.co.sss.shop.entity.User;
import jp.co.sss.shop.form.UserForm;
import jp.co.sss.shop.repository.UserRepository;

@Controller
public class ClientUserRegistController {
	/**
	 * 会員情報　リポジトリ
	 */
	@Autowired
	UserRepository userRepository;
	
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
	 * 処理2　確認画面-戻るボタン 押下時処理
	 * @return
	 */
	@PostMapping("/client/user/regist/input")
	public String input() {
		// セッションスコープから入力フォーム情報を取得
		UserForm userForm = (UserForm) session.getAttribute("userForm");
		// 入力フォーム情報がない場合
		if (userForm == null) {
			// 入力フォーム情報を新規生成
			userForm = new UserForm();
			// 入力フォーム情報をセッションスコープから保存
			session.setAttribute("userForm", userForm);
		}
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
			model.addAttribute("org.springframework.validation.BindingResult.userForm", result);
			session.removeAttribute("result");
		}
		// 登録画面表示
		return "client/user/regist_input";
	}
	
	/**
	 * 処理4　確認ボタン
	 * @param registForm
	 * @param result
	 * @return
	 */
	@PostMapping("/client/user/regist/check")
	public String check(@Valid @ModelAttribute UserForm registForm, BindingResult result, Model model) {
		// セッションスコープからフォーム情報を取得
		UserForm userForm = (UserForm) session.getAttribute("userForm");
		// 画面から入力された入力フォームを、セッションスコープに入力フォーム情報として保存
		BeanUtils.copyProperties(registForm, userForm);
		session.setAttribute("userForm", userForm);
		// BindngResultオブジェクトに入力エラー情報がある場合
		if(result.hasErrors()) {
			// 入力エラー情報と入力フォーム情報を設定
			session.setAttribute("result", result);
			// 登録入力画面表示処理にリダイレクト
			return "redirect:/client/user/regist/input";
		} else {
			return "redirect:/client/user/regist/check";
		}
	}
	
	/**
	 * 処理5 登録確認画面表示処理
	 * @param model
	 * @return
	 */
	@GetMapping("/client/user/regist/check")
	public String regist_check(Model model) {
		// セッションスコープから入力フォーム情報を取得
		UserForm userForm = (UserForm) session.getAttribute("userForm");
		// 入力フォーム情報をリクエストスコープに設定
		model.addAttribute("userForm", userForm);
		// 登録確認画面表示
		return "client/user/regist_check";
	}
	
	/**
	 * 処理6 登録ボタン押下時処理
	 * @return
	 */
	@PostMapping("/client/user/regist/complete")
	public String complete() {
		// セッションスコープから入力フォーム情報を取得
		UserForm userForm = (UserForm) session.getAttribute("userForm");
		// 入力フォーム情報を元にDB登録用エンティティオブジェクトを生成
		User user = new User();
		// DB登録実施
		BeanUtils.copyProperties(userForm, user);
		user.setPoint(0);
		user.setCoupon(0);
		userRepository.save(user);
		// セッションスコープ
		session.removeAttribute("userForm");
		UserBean userBean = new UserBean();
		BeanUtils.copyProperties(user, userBean);
		session.setAttribute("user", userBean);
		return "redirect:/client/user/regist/complete";
	}
	
	/**
	 * 処理7 登録完了画面表示処理
	 * @return
	 */
	@GetMapping("/client/user/regist/complete")
	public String show_complete() {
		// 登録完了画面表示
		return "/client/user/regist_complete";
	}
}
