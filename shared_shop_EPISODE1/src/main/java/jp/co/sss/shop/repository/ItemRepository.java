package jp.co.sss.shop.repository;


import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import jp.co.sss.shop.entity.Item;

/**
 * itemsテーブル用リポジトリ
 *
 * @author System Shared
 */
@Repository
public interface ItemRepository extends JpaRepository<Item, Integer> {
	
	/**
	 * 商品情報を登録日付順に取得 管理者機能で利用
	 * @param deleteFlag 削除フラグ
	 * @param pageable ページング情報
	 * @return 商品エンティティのページオブジェクト
	 */
	@Query("SELECT i FROM Item i INNER JOIN i.category c WHERE i.deleteFlag =:deleteFlag ORDER BY i.insertDate DESC,i.id DESC")
	Page<Item> findByDeleteFlagOrderByInsertDateDescPage(
	        @Param(value = "deleteFlag") int deleteFlag, Pageable pageable);

	/**
	 * 商品IDと削除フラグを条件に検索（管理者機能で利用）
	 * @param id 商品ID
	 * @param deleteFlag 削除フラグ
	 * @return 商品エンティティ
	 */
	public Item findByIdAndDeleteFlag(Integer id, int deleteFlag);

	/**
	 * 商品名と削除フラグを条件に検索 (ItemValidatorで利用)
	 * @param name 商品名
	 * @param notDeleted 削除フラグ
	 * @return 商品エンティティ
	 */
	public Item findByNameAndDeleteFlag(String name, int notDeleted);

//	商品を売上順で検索（追記：春山）
	@Query("SELECT i FROM Item i LEFT JOIN i.orderItemList o WHERE i.deleteFlag =:deleteFlag ORDER BY o.quantity ASC,i.id DESC")
	Page<Item>findByDeleteFlagOrderByQuantityDescPage(
	        @Param(value = "deleteFlag") int deleteFlag, Pageable pageable);
	
//	商品を カテゴリ別 + 新着順 で表示（追記：春山）
	@Query("SELECT i FROM Item i INNER JOIN i.category c WHERE i.deleteFlag =:deleteFlag AND c.id = :categoryId ORDER BY i.insertDate DESC,i.id DESC")
	Page<Item>findByDeleteFlagAndCategoryOrderByInsertDateDescPage(
	        @Param(value = "deleteFlag") int deleteFlag, @Param (value = "categoryId") Integer categoryId,Pageable pageable);

//	商品を カテゴリ別 ＋ 売れ筋順 で検索（追記：春山）
	@Query("SELECT i FROM Item i INNER JOIN i.category c LEFT JOIN i.orderItemList o WHERE i.deleteFlag =:deleteFlag AND c.id = :categoryId ORDER BY o.quantity ASC,i.id DESC")
	Page<Item>findByDeleteFlagAndCategoryOrderByQuantityDescPage(
	        @Param(value = "deleteFlag") int deleteFlag,  @Param (value = "categoryId") Integer categoryId, Pageable pageable);

	@Query("SELECT i FROM Item i INNER JOIN i.orderItemList o WHERE i.deleteFlag =:deleteFlag  And o.quantity != 0 ")
	List<Item> findByDeleteFlagAndQuantity(
	        @Param(value = "deleteFlag") int deleteFlag);
	
}
