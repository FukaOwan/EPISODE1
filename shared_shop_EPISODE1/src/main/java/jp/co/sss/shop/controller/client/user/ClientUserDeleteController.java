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

	//	処理①削除ボタン　押下時処理
	//	@param UserBeam
	//	@return 削除確認画面表示処理へ

	@RequestMapping(path = "/client/user/delete/check", method = RequestMethod.POST)
	public String deleteCheck() {

		//　　　セッションスコープに保存されている「user」を取得しloginUserに保存
		UserBean loginUser = (UserBean) session.getAttribute("user");

		User user = userRepository.findByIdAndDeleteFlag(loginUser.getId(), Constant.NOT_DELETED);

		//		フォームの新オブジェクト
		UserForm userForm = new UserForm();

		//      userFormにuserをコピー
		BeanUtils.copyProperties(user, userForm);

		//情報フォームをセッションに保持
		session.setAttribute("userForm", userForm);

		// 削除確認画面表示処理にリダイレクト
		return "redirect:/client/user/delete/check";
	}

	//　処理②削除確認画面表示処理
	//	@param UserForm
	//	@return 削除確認画面 表示

	@RequestMapping(path = "/client/user/delete/check", method = RequestMethod.GET)
	public String updateInput(Model model) {

		//セッションから入力フォーム取得
		UserForm userForm = (UserForm) session.getAttribute("userForm");
		if (userForm == null) {
			// セッション情報がない場合、エラー
			return "redirect:/syserror";
		}
		// 入力フォーム情報を画面表示設定
		model.addAttribute("userForm", userForm);

		// 削除確認画面　表示
		return "client/user/delete_check";
	}

	//処理③削除ボタン　押下処理
	//	@return 対象のない場合→/syserrorの処理へ
	//	@return 削除完了画面　表示処理→/client/user/delete/complete

	@RequestMapping(path = "/client/user/delete/complete", method = RequestMethod.POST)
	public String deleteComplete() {

		// セッションから削除対象フォーム情報を取得
		UserForm userForm = (UserForm) session.getAttribute("userForm");
		if (userForm == null) {
			// セッション情報がない場合、エラー
			return "redirect:/syserror";
		}
		// 削除対象の会員情報を取得
		User user = userRepository.findByIdAndDeleteFlag(userForm.getId(), Constant.NOT_DELETED);

		if (user == null) {
			// 対象が無い場合、エラー
			return "redirect:/syserror";
		}

		// 削除フラグを立てる
		user.setDeleteFlag(Constant.DELETED);

		// 会員情報を保存
		userRepository.save(user);

		// ログインユーザの会員退会の場合、セッションスコープの情報を破棄(＝ログアウト)
		session.invalidate();

		// 削除完了画面　表示処理
		return "redirect:/client/user/delete/complete";
	}

	//	処理④　削除完了画面
	//      @return 削除完了画面　表示

	@RequestMapping(path = "/client/user/delete/complete", method = RequestMethod.GET)
	public String deleteCompleteFinish() {

		//    	削除完了画面のHTML
		return "client/user/delete_complete";
	}

}
