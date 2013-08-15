/**
 *  Playerクラス
 *
 *  @author toki
 */
package jp.trident.game.rpg;

public class Player{

	// ----------------------//
	// 定数定義
	// ----------------------//
	/** プレイヤーの横幅サイズ */
	public static final int PLAYER_WIDTH = 32;

	/** プレイヤーの縦幅サイズ */
	public static final int PLAYER_HEIGHT = 32;

	//----------------------//
	// 変数定義
	//----------------------//
	/** プレイヤーの名前 */
	protected String name;
	/** プレイヤーのレベル */
	protected int level;
	/** プレイヤーの現在HP */
	protected int currentHp;
	/** プレイヤーの最大HP */
	protected int maxHp;
	/** プレイヤーのスタミナ(ダッシュ用) 数値は100固定 */
	protected int stamina;
	/** プレイヤーの所持ゴールド */
	protected int gold;

	/** プレイヤーの攻撃力 */
	protected int attack;
	/** プレイヤーの防御力 */
	protected int defense;
	/** プレイヤーが装備中の武器id */
	protected int weapon;
	/** プレイヤーが装備中の防具id */
	protected int armor;
	/** 次レベルまでの経験値(初期値10) */
	protected int nextExp;
	/** 合計の経験値(初期値0) */
	protected int totalExp;

	/** マップ上でのプレイヤーのx座標 */
	protected int m_x;
	/** マップ上でのプレイヤーのy座標 */
	protected int m_y;
	/** 戦闘中のプレイヤーのx座標 */
	protected int b_x;
	/** 戦闘中のプレイヤーのy座標 */
	protected int b_y;

	/** プレイヤーの横幅 */
	protected int width;
	/** プレイヤーの縦幅 */
	protected int height;

	/** プレイヤーのアニメーション用 */
	protected int anime;
	/** プレイヤーの移動速度 */
	protected int speed;

	/**
	 * プレイヤーの状態(走っているか、歩いているか)
	 * true		走っている
	 * false	歩いている
	 */
	protected boolean modeFlag;
	/**
	 * プレイヤーが生きているか、死んでいるか
	 * true		生きている
	 * false	死んでいる
	 */
	protected boolean alive;


	/**
	 * コンストラクタ。
	 */
	Player() {

		// プレイヤーのステータス関係
		name = "ユウシャ";
		level = 1;
		currentHp = 100;
		maxHp =100;
		stamina = 100;
		gold = 100;
		attack = 10;
		defense = 10;
		weapon = 0;
		armor = 0;
		nextExp = 10;
		totalExp = 0;

		// プレイヤーの内部情報(座標,縦横幅など)
		m_x = 400;
		m_y = 220;
		b_x = 550;
		b_y = 200;

		speed = 10;
		anime = 0;

		modeFlag = false;
		alive = true;
	}
	
	/**
	 * プレイヤー情報の更新 派生クラスで内容を変更する。
	 */
	public void setPlayerInfo(Player player) {
		
	}
	
}
