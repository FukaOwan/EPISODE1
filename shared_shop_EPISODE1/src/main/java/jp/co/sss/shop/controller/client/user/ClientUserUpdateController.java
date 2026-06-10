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
import jp.co.sss.shop.util.Constant;

@Controller
public class ClientUserUpdateController {
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
	 * 処理1　変更ボタン、確認画面-戻るボタン 押下時処理
	 * @return
	 */
	@PostMapping("/client/user/update/input")
	public String input() {
		// セッションスコープから入力フォーム情報を取得
		UserForm userForm = (UserForm) session.getAttribute("userForm");
		// 入力フォーム情報がない場合
		if(userForm == null) {
			// ログイン会員IDを取得
			Integer userId = ((UserBean)session.getAttribute("user")).getId();
			// 変更対象データをDBから取得
			User user = userRepository.findByIdAndDeleteFlag(userId, Constant.NOT_DELETED);
			// 取得データを元に入力画面初期表示用の入力フォーム情報を新規生成
			userForm = new UserForm();
			BeanUtils.copyProperties(user, userForm);
			// 入力フォーム情報をセッションスコープに保存
			session.setAttribute("userForm", userForm);
		}
		// 変更入力画面表示処理へリダイレクト
		return "redirect:/client/user/update/input";
	}
	
	/**
	 * 処理2 変更入力画面表示処理
	 * @param model
	 * @return
	 */
	@GetMapping("/client/user/update/input")
	public String show_input(Model model) {
		// セッションスコープから入力フォーム情報を取得
		UserForm userForm = (UserForm) session.getAttribute("userForm");
		// 入力フォーム情報をリクエストスコープに設定
		model.addAttribute("userForm", userForm);
		// セッションスコープから入力エラー情報を取得
		BindingResult result = (BindingResult) session.getAttribute("result");
		// 入力エラー情報がある場合
		if(result != null) {
			// 取得した入力エラー情報をリクエストスコープに設定
			model.addAttribute("result", result);
			// セッションスコープから、入力エラー情報を削除
			session.removeAttribute("result");
		}
		// 変更入力画面表示
		return "/client/user/update_input";
	}
	
	/**
	 * 処理3 確認ボタン 押下時処理
	 * @param form
	 * @param result
	 * @return
	 */
	@PostMapping("/client/user/update/check")
	public String check(@Valid @ModelAttribute UserForm form, BindingResult result) {
		// セッションスコープからフォーム情報を取得
		UserForm userForm = (UserForm) session.getAttribute("userForm");
		if(form.getAuthority()==null) {
			//権限情報がない場合、セッション情報から値をセット
			form.setAuthority(userForm.getAuthority());
		}
		// 画面から入力された入力フォームを、セッションスコープに入力フォーム情報として保存
		session.setAttribute("userForm", form);
		// 入力エラー情報がある場合
		if(result.hasErrors()) {
			// 入力エラー情報をセッションスコープに設定
			session.setAttribute("result", result);
			// 変更入力画面表示処理にリダイレクト
			return "redirect:/client/user/update/input";
		// 入力エラーがない場合
		}else {
			// 変更確認画面表示処理にリダイレクト
			return "redirect:/client/user/update/check";
		}
	}
	
	/**
	 * 処理4
	 * @param model
	 * @return
	 */
	@GetMapping("/client/user/update/check")
	public String show_check(Model model) {
		// セッションスコープに入力フォーム情報を取得
		UserForm userForm = (UserForm) session.getAttribute("userForm");
		// 入力フォーム情報をリクエストスコープに設定
		model.addAttribute("userForm", userForm);
		// 登録確認画面表示
		return "/client/user/update_check";
	}
	
	@PostMapping("/client/user/update/complete")
	public String complete() {
		// セッションスコープに入力フォーム情報を取得
		UserForm userForm = (UserForm) session.getAttribute("userForm");
		User user = userRepository.findByIdAndDeleteFlag(userForm.getId(), Constant.NOT_DELETED);
		BeanUtils.copyProperties(userForm, user, "id");
		userRepository.save(user);
		session.removeAttribute("userForm");
		session.setAttribute("user", user);
		return "redirect:/client/user/update/complete";
	}
	
	@GetMapping("/client/user/update/complete")
	public String show_complete() {
		return "/client/user/update_complete";
	}
}
