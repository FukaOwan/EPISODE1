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

	/**
	 * 処理①退会ボタンを押したとき
	 * 
	 * ログイン中の会員情報を取得し
	 * 削除確認画面へ表示するためのフォームを作成
	 * 
	 * @return 削除確認画面処理表示
	 */

	@RequestMapping(path = "/client/user/delete/check", method = RequestMethod.POST)
	public String deleteCheck() {

		//   ログイン中の会員情報をセッションから取得
		UserBean loginUser = (UserBean) session.getAttribute("user");
		//    削除対象の会員情報を取得
		User user = userRepository.findByIdAndDeleteFlag(loginUser.getId(), Constant.NOT_DELETED);

		//   画面表示用のフォームオブジェクトを生成
		UserForm userForm = new UserForm();

		//   会員情報をフォームへコピー  
		BeanUtils.copyProperties(user, userForm);

		//   削除確認画面用にフォーム情報をセッションへ保存
		session.setAttribute("userForm", userForm);

		// 削除確認画面表示処理にリダイレクト
		return "redirect:/client/user/delete/check";
	}

	/**
	 * 処理②削除画面表示処理
	 * 
	 * セッションに保存された会員情報を取得し
	 * 削除画面へ表示する
	 * 
	 * @param model
	 * @return　削除確認画面
	 */

	@RequestMapping(path = "/client/user/delete/check", method = RequestMethod.GET)
	public String updateInput(Model model) {

		//セッションから削除対象の会員情報を取得
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

	/**
	 * 処理③退会処理
	 * 
	 * 会員情報に削除フラグを設定
	 * 退会処理を実行
	 * 
	 * 退会後はセッションを破棄し
	 * ログイン状態を解除する
	 * 
	 * @return 削除完了画面表示処理
	 */
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

		// セッションを破棄しログアウト状態にする
		session.invalidate();

		// 削除完了画面　表示処理
		return "redirect:/client/user/delete/complete";
	}

	/**
	 * 処理④削除完了画面表示処理
	 * 
	 * 退会完了画面を表示する
	 * 
	 * @return 削除完了画面
	 */

	@RequestMapping(path = "/client/user/delete/complete", method = RequestMethod.GET)
	public String deleteCompleteFinish() {

		//    	削除完了画面のHTML
		return "client/user/delete_complete";
	}

}
