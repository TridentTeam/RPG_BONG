/**
 *  BattlePlayerクラス
 *
 *  @author toki
 */
package jp.trident.game.rpg;

import jp.trident.game.fw.GameSurfaceView;
import android.graphics.Bitmap;


public class BattlePlayer extends Player {

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

	//----------------------//
	// 変数定義
	//----------------------//
	/** 描画元の画像のY座標 */
	private int sy;

	/** プレイヤークラス */
	private Player player;
	/** 画像 */
	private Bitmap img;


	/**
	 * 戦闘シーン用プレイヤーのコンストラクタ
	 */
	public BattlePlayer(){

		// プレイヤークラスの作成
		player = new Player();

		// 初期値設定 待機モーション
		sy = 128;
	}

	/**
	 * imgのセッター
	 * @param img
	 */
	public void setImg(Bitmap img) {
		this.img = img;
	}

	/**
	 * 戦闘シーン用プレイヤーの更新
	 */
	public void update(){

		// アニメの更新
		player.anime++;
		if (player.anime >= 3) {
			player.anime = 0;
		}
	}

	/**
	 * 戦闘シーン用のプレイヤーの描画
	 *
	 * @param sv	ゲームサーフェイスビュー
	 */
	public void Draw(GameSurfaceView sv){

		sv.ScaleDrawImage(img, player.b_x, player.b_y, Player.PLAYER_WIDTH * player.anime, sy, Player.PLAYER_WIDTH, Player.PLAYER_HEIGHT, SCALE_X, SCALE_Y, false);
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
	 * プレイヤー情報の更新
	 */
	public void setPlayerInfo(Player player) {
		
	}
}
