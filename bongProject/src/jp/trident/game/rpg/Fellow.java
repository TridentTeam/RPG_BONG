/**
 * Fellowクラス
 */
package jp.trident.game.rpg;

import jp.trident.game.fw.GameSurfaceView;
import android.graphics.Bitmap;

/**
 * @author s112169
 *
 */
public class Fellow {

	//----------------------//
	// 定数定義
	//----------------------//
	/** 待機モーション */
	private static final int WAIT_MOTION = 4 * Player.PLAYER_HEIGHT;
	/** 近接攻撃モーション */
	private static final int ATTACK_MOTION = 11 * Player.PLAYER_HEIGHT;
	/** 魔法攻撃モーション */
	private static final int MAGIC_MOTION = 14 * Player.PLAYER_HEIGHT;
	/** 被撃モーション */
	private static final int DAMAGE_MOTION = 5 * Player.PLAYER_HEIGHT;
	/** 勝利モーション */
	private static final int WIN_MOTION = 8 * Player.PLAYER_HEIGHT;
	/** 死亡モーション */
	private static final int DEATH_MOTION = 10 * Player.PLAYER_HEIGHT;

	/** スケール用の倍率 横幅 */
	private static final int SCALE_X = 2;
	/** スケール用の倍率 縦幅 */
	private static final int SCALE_Y = 2;
	/** 元画像の幅 */
	private static final int FELLOW_WIDTH = 32;
	/** 元画像の高さ */
	private static final int FELLOW_HEIGHT = 32;

	//----------------------//
	// 変数定義
	//----------------------//
	/** x座標 */
	private int x;
	/** y座標 */
	private int y;
	/** 描画元の画像のy座標 */
	private int sy;
	/** アニメーション番号 */
	private int anime;
	/** 画像イメージ */
	private Bitmap img;

	/** 攻撃力 */
	private int atk;


	/**
	 * コンストラクタ
	 */
	public Fellow(int x, int y, int player_atk){
		this.x = x;
		this.y = y;

		//初期設定（待機モーション）
		sy = 128;
		atk = player_atk/2;
	}

	/**
	 * x座標のget
	 */
	public int getX() {
		return x;
	}

	/**
	 * y座標のget
	 */
	public int getY() {
		return y;
	}
	/**
	 * imgのset
	 */
	public void setImg(Bitmap img) {
		this.img = img;
	}

	/**
	 * アップデート
	 */
	public void update(){

		//アニメの更新
		anime++;
		if(anime >= 3){
			anime = 0;
		}
	}

	/**
	 * 描画
	 */
	public void Draw(GameSurfaceView sv){
		sv.ScaleDrawImage(img, x, y, FELLOW_WIDTH * anime, sy, FELLOW_WIDTH, FELLOW_HEIGHT, SCALE_X, SCALE_Y, false);
	}

	/**
	 * バトルシーンでのモーションの切り替え
	 */
	public void motionChange(int motionFlag){

		switch(motionFlag){

		case 0:
			// 待機モーション
			sy = WAIT_MOTION;
			break;

		case 1:
			// 近接攻撃モーション
			sy = ATTACK_MOTION;
			break;

		case 2:
			// 魔法攻撃モーション
			sy = MAGIC_MOTION;
			break;

		case 3:
			// 被撃モーション
			sy = DAMAGE_MOTION;
			break;

		case 4:
			// 勝利モーション
			sy = WIN_MOTION;
			break;
		case 5:
			// 死亡モーション
			sy = DEATH_MOTION;
			break;
		}
	}

	/**
	 * ダメージ計算をしてダメージ量を返す
	 */
	public int damageCalc(int gageTouchDamage){
		gageTouchDamage /= 50;
		int damage = atk * gageTouchDamage;
		return damage;
	}
}