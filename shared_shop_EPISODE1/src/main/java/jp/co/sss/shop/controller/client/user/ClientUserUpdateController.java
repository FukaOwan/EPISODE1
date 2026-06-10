package jp.co.sss.shop.controller.client.user;

import java.sql.Date;

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
	 * 会員情報　リポジトリ(東山)
	 */
	@Autowired
	UserRepository userRepository;
	
	/**
	 * セッション情報(東山)
	 */
	@Autowired
	HttpSession session;
	
	/**
	 * 処理1　変更ボタン、確認画面-戻るボタン 押下時処理(東山)
	 * @return
	 */
	@PostMapping("/client/user/update/input")
	public String input() {
		// セッションスコープから入力フォーム情報を取得
		UserForm userForm = (UserForm) session.getAttribute("userForm");
		// 入力フォーム情報がない場合
		if(userForm == null) {
			// ログイン会員IDを取得
			UserBean loginUser = (UserBean) session.getAttribute("user");
			Integer userId = loginUser.getId();
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
	 * 処理2 変更入力画面表示処理(東山)
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
	 * 処理3 確認ボタン 押下時処理(東山)
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
	 * 処理4(東山)
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
	
	/**
	 * 処理5 登録ボタン 押下時処理(東山)
	 * @return
	 */
	@PostMapping("/client/user/update/complete")
	public String complete() {
		// セッションスコープに入力フォーム情報を取得
		UserForm userForm = (UserForm) session.getAttribute("userForm");
		// 入力フォーム情報を元にDB登録用エンティティオブジェクトを生成
		User user = userRepository.findByIdAndDeleteFlag(userForm.getId(), Constant.NOT_DELETED);
		Integer deleteFlag = user.getDeleteFlag();
		Date insertDate = user.getInsertDate();
		BeanUtils.copyProperties(userForm, user, "id", "deleteFlag", "insertDate");
		user.setDeleteFlag(deleteFlag);
		user.setInsertDate(insertDate);
		// DB更新実施
		userRepository.save(user);
		// セッションスコープの入力フォーム情報削除
		session.removeAttribute("userForm");
		// セッションスコープの会員情報を更新
		UserBean userBean = new UserBean();
		BeanUtils.copyProperties(user, userBean);
		session.setAttribute("user", userBean);
		// 変更完了画面表示処理にリダイレクト
		return "redirect:/client/user/update/complete";
	}
	
	/**
	 * 処理6 変更完了画面表示処理(東山)
	 * @return
	 */
	@GetMapping("/client/user/update/complete")
	public String show_complete() {
		// 登録完了画面表示
		return "/client/user/update_complete";
	}
}
