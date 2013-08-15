package jp.trident.game.rpg;

import android.graphics.Bitmap;
import jp.trident.game.fw.GameSurfaceView;

public class Enemy {

	//----------------------//
	// 定数定義
	//----------------------//
	/** スケール用の倍率 横幅 */
	private static final int SCALE_X = 2;
	/** スケール用の倍率 縦幅 */
	private static final int SCALE_Y = 2;

	//----------------------//
	// 変数定義
	//----------------------//
	/** 描画元の画像のY座標 */
	private int sy;
	/** 画像 */
	private Bitmap img;

	/** 敵の名前 */
	protected String name;
	/** 敵判別用ID */
	protected int enemyId;
	/** 敵の現在HP */
	protected int currentHp;
	/** 敵の最大HP */
	protected int maxHp;
	/** 敵の攻撃力 */
	protected int attack;
	/** 敵の防御力 */
	protected int defense;
	/** 敵からの取得ゴールド */
	protected int getGold;
	/** 敵からの取得経験値 */
	protected int getExp;

	/** 戦闘中の敵のx座標 */
	protected int x;
	/** 戦闘中の敵のy座標 */
	protected int y;
	/** 敵の横幅サイズ */
	protected int width;
	/** 敵の縦幅サイズ */
	protected int height;

	/** 敵のアニメーション用 */
	protected int anime;
	/**
	 * 敵が生きているか、死んでいるか
	 * true		生きている
	 * false	死んでいる
	 */
	protected boolean alive;

	/**
	 * コンストラクタ。
	 */
	Enemy() {

		// 敵の内部情報(座標,縦横幅など)
		alive = true;
	}

	/**
	 * 敵の更新処理
	 */
	public void update(){


	}

	/**
	 * 敵の描画処理
	 *
	 * @param sv
	 */
	public void Draw(GameSurfaceView sv){

		//sv.ScaleDrawImage(img, x, y, Player.PLAYER_WIDTH * anime, sy, Player.PLAYER_WIDTH, Player.PLAYER_HEIGHT, SCALE_X, SCALE_Y, false);
	}

	/**
	 * 敵の判別
	 * 渡されたID(数値)によってそれぞれの
	 * 画像、ステータスが渡される
	 */
	public void EnemyId(int enemyId){

		switch(enemyId){

		case 0:

			break;

		case 1:

			break;

		case 2:

			break;

		case 3:

			break;

		case 4:

			break;
		case 5:

			break;
		}
	}
}
