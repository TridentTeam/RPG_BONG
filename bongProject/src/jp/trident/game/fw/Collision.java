package jp.trident.game.fw;

import java.util.ArrayList;

/**
 * コリジョンクラス
 *
 * @author wa-rudo
 *
 */
public class Collision {

	/**
	 * オブジェクト識別子定数 id
	 */
	public static final int ID_NOT			=-1;// なし
	public static final int ID_TOUCH		= 0;// タッチ
	public static final int ID_PLAYER 		= 1;// プレイヤー
	public static final int ID_VILLAGERS 	= 2;// 村人
	public static final int ID_KYOUKAI 		= 3;// 教会

	/**
	 * リスト識別子定数
	 */
	public static final int LIST_A = 0;
	public static final int LIST_B = 1;

	/**
	 * コリジョンデータ
	 */
	public class CollData {
		public int id = -1;							// オブジェクト識別
		public int type = -1;						// コリジョンタイプ(主体・その他)
		public boolean[] collType = new boolean[2];	// 自分に対してコリジョン判定を行うタイプ
		public DrawObject object = null;			// オブジェクト
	}

	/**
	 * ヒットデータ
	 */
	public class HitData {
		public int myId = -1;				// オブジェクト識別		（自分）
		public int myType = -1;				// 接触したタイプ		（自分）
		public int myIndex = -1;			// 接触したインデックス	（自分）
		public DrawObject myObject = null;	// 接触したオブジェクト	（自分）
		public int id = -1;					// オブジェクト識別		（相手）
		public int type = -1;				// 接触したタイプ		（相手）
		public int index = -1;				// 接触したインデックス	（相手）
		public DrawObject object = null;	// 接触したオブジェクト	（相手）
	}

	/**
	 * コリジョン変数
	 */

	/**
	 * 主体オブジェクトリスト 固定オブジェクト（プレイヤー・タッチ）
	 */
	private ArrayList<CollData> listA = null;

	/**
	 * その他オブジェクトリスト（エネミーなど）
	 */
	private ArrayList<CollData> listB = null;

	/**
	 * 接触したリスト
	 */
	private ArrayList<HitData>  hitList = null;



	/**
	* コンストラクタ
	*/
	public Collision() {
		this.listA   = new ArrayList<CollData>();
		this.listB   = new ArrayList<CollData>();
		this.hitList = new ArrayList<HitData>();
	}

	/**
	* 解放
	*/
	public void destroy() {
		if(this.listA != null) {
			this.listA.clear();// クリアしても、要素がなくなるだけでgcが行われる訳ではないらしい
			this.listA = null;// ここで、gcが行う理由みたいなものができるらしい
		}
		if(this.listB != null) {
			this.listB.clear();
			this.listB = null;
		}
		if(this.hitList != null) {
			this.hitList.clear();
			this.hitList = null;
		}
	}

	/**
	 * 初期化
	 */
	public void initialize() {
		this.listA.clear();
		this.listB.clear();
		this.hitList.clear();
	}

	/**
	 * コリジョンデータを作成する
	 *
	 * @param id		オブジェクト識別子
	 * @param type		リストタイプ（主体かその他）
	 * @param typeA		主体とも当たり判定を行うか
	 * @param typeB		その他に当たり判定を行うか
	 * @param obj		当たり判定オブジェクト
	 *
	 * @return cd		作成したコリジョンデータ
	 */
	public CollData createCollData(int id, int type, boolean collTypeA, boolean collTypeB, DrawObject obj) {
		CollData cd = new CollData();
		cd.id = id;
		cd.type = type;
		cd.collType[LIST_A] = collTypeA;
		cd.collType[LIST_B] = collTypeB;
		cd.object = obj;

		return cd;
	}

	/**
	 * ヒットデータを作成する
	 *
	 * @param myCd		主体コリジョンデータ
	 * @param myIndex	主体インデックス
	 * @param cd		その他コリジョンデータ
	 * @param index		その他インデックス
	 *
	 * @return hd		作成したヒットデータ
	 */
	private HitData createHitData(CollData myCd, final int myIndex, CollData cd, final int index) {
		HitData hd = new HitData();
		// 自分
		hd.myId = myCd.id;
		hd.myType = myCd.type;
		hd.myIndex = myIndex;
		hd.myObject = myCd.object;
		// 相手
		hd.id = cd.id;
		hd.type = cd.type;
		hd.index = index;
		hd.object = cd.object;

		return hd;
	}

	/**
	 * 主体リストに追加する
	 *
	 * @param cd	コリジョンデータ
	 */
	public void addListA(CollData cd) {
		this.listA.add(cd);
	}

	/**
	 * 主体リストの指定オブジェクトを削除する
	 *
	 * @param id	削除したいオブジェクト識別子
	 *
	 * @return cd	null なし / cd 削除されたデータ
	 */
	public CollData removeListA(final int id) {
		CollData cd = null;
		for(int i = 0; i < this.listA.size(); i++) {
			cd = this.listA.get(i);

			// 指定オブジェクト識別子があったとき
			if(cd.id == id) {
				this.listA.remove(i);
				return cd;
			}
		}

		return null;
	}

	/**
	 * 主体の指定オブジェクトを削除する
	 *
	 * @param obj	削除したいオブジェクト
	 *
	 * @return	null なし / cd 削除されたオブジェクト
	 */
	public CollData removeListA(DrawObject obj) {
		CollData cd = null;
		for(int i = 0; i < this.listA.size(); i++) {
			cd = this.listA.get(i);
			if(cd.object == obj) {
				this.listA.remove(i);
				return cd;
			}
		}

		return null;
	}

	/**
	 * 主体リストのクリア
	 */
	public void clearListA() {
		this.listA.clear();
	}

	/**
	 * その他リストに追加する
	 *
	 * @param cd	コリジョンデータ
	 */
	public void addListB(CollData cd) {
		this.listB.add(cd);
	}

	/**
	 * その他リストの指定オブジェクトを削除する
	 *
	 * @param id	削除したいオブジェクト識別子
	 *
	 * @return cd	null なし / cd 削除されたデータ
	 */
	public CollData removeListB(final int id) {
		CollData cd = null;
		for(int i = 0; i < this.listB.size(); i++) {
			cd = this.listB.get(i);

			// 指定オブジェクト識別子があったとき
			if(cd.id == id) {
				this.listB.remove(i);
				return cd;
			}
		}

		return null;
	}

	/**
	 * その他の指定オブジェクトを削除する
	 *
	 * @param obj	削除したいオブジェクト
	 *
	 * @return		null なし / cd 削除されたオブジェクト
	 */
	public CollData removeListB(DrawObject obj) {
		CollData cd = null;
		for(int i = 0; i < this.listB.size(); i++) {
			cd = this.listB.get(i);
			if(cd.object == obj) {
				this.listB.remove(i);
				return cd;
			}
		}

		return null;
	}

	/**
	 * その他リストのクリア
	 */
	public void clearListB() {
		this.listB.clear();
	}

	/**
	 * 指定オブジェクトを削除する
	 *
	 * @param id	削除したいオブジェクト識別子
	 *
	 * @return cd 	削除したオブジェクト
	 */
	public CollData removeList(final int id) {
		CollData cd = null;
		cd = this.removeListA(id);
		if(cd == null) {
			cd = this.removeListB(id);
		}

		return cd;
	}

	/**
	 * 指定オブジェクトを削除する
	 *
	 * @param obj	削除したいオブジェクト
	 *
	 * @return cd 	削除したオブジェクト
	 */
	public CollData removeList(DrawObject obj) {
		CollData cd = null;
		cd = this.removeListA(obj);
		if(cd == null) {
			cd = this.removeListB(obj);
		}

		return cd;
	}

	/**
	 * 指定オブジェクトを削除する
	 *
	 * @param type	リストのタイプ
	 * @param index	要素番号
	 *
	 * @return 削除されたコリジョンデータ
	 */
	public CollData removeList(final int type, final int index) {
		CollData cd = null;
		if(type == LIST_A) {
			cd = this.listA.get(index);
			this.listA.remove(index);
		}
		else if(type == LIST_B) {
			cd = this.listA.get(index);
			this.listB.remove(index);
		}

		return cd;
	}

	/**
	 * 入れ替える 指定オブジェクトを myのデータに入れ替える
	 *
	 * @param hd	コリジョンデータ
	 */
	private void replace(HitData hd) {
		int myId = hd.myId;
		int myType = hd.myType;
		int myIndex = hd.myIndex;
		DrawObject myObj = hd.myObject;
		hd.myId = hd.id;
		hd.myType = hd.type;
		hd.myIndex = hd.index;
		hd.myObject = hd.object;
		hd.id = myId;
		hd.type = myType;
		hd.index = myIndex;
		hd.object = myObj;
	}

	/**
	 * 指定されたオブジェクトの接触した結果を取得
	 *
	 * @param obj	ヒットデータリストから取得したいオブジェクト
	 *
	 * @return list	指定されたヒットデータを集めたリスト
	 */
	public ArrayList<HitData> getHitData(DrawObject obj) {
		ArrayList<HitData> list = new ArrayList<HitData>();
		for(int i = 0; i < this.hitList.size(); i++) {
			HitData hd = this.hitList.get(i);
			if(hd == null) continue;

			// 指定オブジェクトがあったとき
			if(hd.myObject == obj) {
				list.add(hd);
			}
			else if(hd.object == obj) {
				// 入れ替える
				this.replace(hd);
				list.add(hd);
			}
		}

		// 指定オブジェクトが検出されなかったとき
		if(list.size() == 0) {
			list = null;
			return null;
		}

		return list;
	}

	/**
	 * 接触リストから指定オブジェクトを検索する
	 *
	 * @param list	指定されたヒットデータを集めたリスト
	 * @param id		検索したいオブジェクト識別子
	 *
	 * @return 検索した結果		-1 なし / 0~　あり
	 */
	public final int hitListSearch(ArrayList<HitData> list, final int id) {
		for(int i = 0; i < list.size(); i++) {
			HitData hd = list.get(i);
			if(hd == null) continue;

			// 検索したいオブジェクトが見つかったとき
			if( (hd.myId == id) || (hd.id == id) ) return i;
		}

		return -1;
	}

	/**
	 * ヒットリストをクリアする
	 */
	public void clearHitList() {
		this.hitList.clear();
	}

	/**
	 * 接触したデータを削除
	 *
	 * @param hd		ヒットデータ
	 *
	 * @return _hd	削除するヒットデータ
	 */
	public HitData removeHitList(HitData hd) {
		HitData _hd = null;
		int index = this.hitList.indexOf(hd);// 指定オブジェクトを検索する

		if(index != -1) {
			_hd = this.hitList.get(index);
			this.hitList.remove(index);
		}

		return _hd;
	}

	/**
	 * 矩形の当たり判定
	 *
	 * @param obj1	オブジェクト
	 * @param obj2	オブジェクト
	 *
	 * @return true 接触あり / false 接触なし
	 */
	public boolean collision(DrawObject obj1, DrawObject obj2) {

		if( (obj1.posX + obj1.width)	< (obj2.posX) )					{ return false; }// 右・左
		if( (obj1.posX) 				> (obj2.posX + obj2.width) )	{ return false; }// 左・右
		if( (obj1.posY + obj1.height) 	< (obj2.posY) )					{ return false; }// 下・上
		if( (obj1.posY)					> (obj2.posY + obj2.height) )	{ return false; }// 上・下

		return true;
	}

	/**
	 * 更新 すべてのオブジェクトから当たり判定を行う
	 * A 主体 B その他
	 * 当たり判定を行う表
	 * (A A)
	 * (A B)
	 * (B B)
	 */
	public void update() {

		// 取得用データ
		CollData cd = null;
		boolean collTypeA = false;
		boolean collTypeB = false;

		// 主体と主体・その他と当たり判定を行う
		for(int i = 0; i < this.listA.size(); i++) {
			// 主体データを取得
			cd = this.listA.get(i);

			// 主体データが存在しない
			if(cd == null) continue;

			// 主体・その他・両方
			collTypeA = cd.collType[LIST_A];// Aに対して当たり判定を行うか
			collTypeB = cd.collType[LIST_B];// Bに対して当たり判定を行うか

			// 主体同士の当たり判定を行う
			if(collTypeA == true) {
				this.collisionCheck(this.listA, LIST_A, cd, i);
			}

			// 主体とその他の当たり判定を行う
			if(collTypeB == true) {
				this.collisionCheck(this.listB, LIST_A, cd, i);
			}
		}// for 主体

		// その他とその他との当たり判定を行う
		for(int i = 0; i < this.listB.size(); i++) {
			// その他データを取得
			cd = this.listB.get(i);

			// その他が存在しないとき
			if(cd == null) continue;

			collTypeB = cd.collType[LIST_B];
			if(collTypeB == false) continue;

			// その他のリストと当たり判定を行う
			this.collisionCheck(this.listB, LIST_B, cd, i);
		}// for その他
	}// func

	/**
	 * コリジョンチェックを行う
	 *
	 * @param list			リスト
	 * @param tyoeIndex		タイプのインデックス
	 * @param cd			コリジョンデータ
	 * @param index			インデックス
	 */
	private void collisionCheck(ArrayList<CollData> list, final int tyoeIndex, final CollData cd, final int index) {
		CollData cd2 = null;
		boolean collType = false;
		boolean collisionFlag = false;
		for(int i = index; i < list.size(); i++) {
			// データを取得
			cd2 = list.get(i);

			// データが自分自身のとき　またはその他データが存在しない
			if( (cd == cd2) || (cd2 == null) ) continue;

			// 当たり判定が無効のとき
			collType = cd2.collType[tyoeIndex];// タイプに対して当たり判定を行うか
			if(collType == false) continue;

			// 当たり判定を行う
			collisionFlag = this.collision(cd.object, cd2.object);

			if(collisionFlag == true) {
				// ヒットデータを作成する
				HitData hd = this.createHitData(cd, index, cd2, i);
				// ヒットデータを追加する
				this.hitList.add(hd);
			}
		}// for
	}// func
}

