package jp.trident.game.rpg;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import jp.trident.game.fw.BaseEventScene;
import jp.trident.game.fw.BaseScene;
import jp.trident.game.fw.GameSurfaceView;
import jp.trident.game.fw.IEventScene;
import jp.trident.game.fw.R;
import jp.trident.game.fw.SceneManager;
import jp.trident.game.fw.VirtualController;

/**
 * バトルイベントシーン
 *
 * @author wa-rudo
 *
 */
public class BattleEventScene implements IEventScene {

	// ----------------------//
	// ゲーム用定数
	// ----------------------//

	/** 仲間の数 */
	private final static int FELLOW_NUM = 2;

	//----------------------//
	// ゲーム用変数
	//----------------------//

	/** バトルシーン用プレイヤー */
	private BattlePlayer b_player = null;
	/** 仲間 */
	private Fellow[] fellow = new Fellow[FELLOW_NUM];
	/** ゲージ */
	private Gage gage = null;

	/** タッチされた時のゲージのsw保存用 */
	private int gage_sw = 0;
	/** モーション用のフラグ */
	private int motionFlag = 0;
	/** 仲間から敵へのダメージ量 */
	private int damageFtoE = 0;

	//----------------------//
	// Bitmap関係の変数
	//----------------------//

	/** 背景 */
	private Bitmap bg;
	/**  バトルシーンのプレイヤー画像用 */
	private Bitmap b_playerImg;
	/** 仲間イメージ */
	private Bitmap[] fellowImg = new Bitmap[FELLOW_NUM];
	/** メッセージウィンドウ画像用  */
	private Bitmap msgImg;
	/**  ゲージ画像。*/
	private Bitmap gageImg;


	/**
	 * コンストラクタ
	 */
	public BattleEventScene(Player player) {
		
		// ゲームユーティリティーを取得する。
		GameUtility gu = GameUtility.getInstance();

		// バトルシーン用のプレイヤークラス
		b_player = new BattlePlayer();
		b_player.setPlayerInfo(player);
		// ゲージ用のゲージクラス
		gage = new Gage(300,360);
		// 仲間用の仲間クラス
		fellow[0] = new Fellow(670,160,10);
		// 仲間2用の仲間クラス
		fellow[1] = new Fellow(640,260,10);

		// リソースから背景画像を読み込む。
		bg = BitmapFactory.decodeResource(gu.context.getResources(), R.drawable.bg, gu.bitmapfOption);

		// リソースからメッセージウィンドウの画像を読み込む。
		msgImg =  BitmapFactory.decodeResource(gu.context.getResources(), R.drawable.message, gu.bitmapfOption);

		// リソースからバトルシーン用のプレイヤー画像を読み込む。
		b_playerImg = BitmapFactory.decodeResource(gu.context.getResources(), R.drawable.p_player, gu.bitmapfOption);
		// バトルシーン用プレイヤーの画像をセット
		b_player.setImg(b_playerImg);

		// リソースから仲間画像を読み込む
		fellowImg[0] = BitmapFactory.decodeResource(gu.context.getResources(), R.drawable.p_magician, gu.bitmapfOption);
		// 仲間1人目の画像をセット
		fellow[0].setImg(fellowImg[0]);

		//リソースから仲間画像を読み込む
		fellowImg[1] = BitmapFactory.decodeResource(gu.context.getResources(), R.drawable.p_grappler, gu.bitmapfOption);
		// 仲間2人目の画像をセット
		fellow[1].setImg(fellowImg[1]);

		// リソースからゲージ画像を読み込む
		gageImg = BitmapFactory.decodeResource(gu.context.getResources(), R.drawable.gage, gu.bitmapfOption);
		// ゲージ画像をセット
		gage.setImg(gageImg);
	}

	/**
	 * 解放
	 */
	public void destroy() {

	}

	/**
	 * 初期化
	 */
	public void initialize() {

	}

	/**
	 * 更新
	 */
	public boolean update() {

		// 画面にタッチしてる？
		if (VirtualController.isTouchTrigger(0)) {
			// ゲームユーティリティーを取得する。
			GameUtility gu = GameUtility.getInstance();
			// FPSの表示座標をタッチ位置に更新
			float x = VirtualController.getTouchX(0);
			float y = VirtualController.getTouchY(0);

			motionFlag += 1;
			// タッチをするとプレイヤーのモーションが切り替わる
			b_player.motionChange(motionFlag);
			// タッチすると仲間のモーションが切り替わる
			fellow[0].motionChange(motionFlag);
			fellow[1].motionChange(motionFlag);
			if( motionFlag >= 5 ){
				// 0になった後タッチをすると1になり待機にならないので
				// -1にしておく
				motionFlag = -1;

				// ---------------------------------------------
				return true;
			}

			//ゲージを止める処理
			if(gage.isState() == true){
				gage.stop();
			}
			//ゲージが止まっている時にタッチするとゲージが初期化される処理
			if(gage.isState() == false){
				gage.reset();
			}

			// ダメージ計算
			for(int i = 0; i < FELLOW_NUM; i++){
				damageFtoE = fellow[i].damageCalc(gage_sw) + gu.getRandom(5);
			}

			// 効果音を再生する。
			//se.start();
		}

		// バトルシーン用プレイヤーの更新
		b_player.update();

		// 仲間の更新
		for (int i = 0; i < FELLOW_NUM; i++) {
			fellow[i].update();
		}

		//ゲージ更新
		gage.update();

		return false;
	}

	/**
	 * 描画
	 */
	public void draw(GameSurfaceView sv) {

		// 背景を表示する。
		sv.DrawImage(bg, 0, 0);
		// メッセージウィンドウの描画
		sv.ScaleDrawImage(msgImg, 0, 338, 0, 0, 800, 143, 1, 1,false);
		// ゲージの描画
		gage.Draw(sv);
		// バトル用のプレイヤーの描画
		b_player.Draw(sv);
		// 仲間の描画
		for (int i = 0; i < FELLOW_NUM; i++) {
			fellow[i].Draw(sv);
		}

		// テキストを表示する。
		//sv.DrawText("x:" + x + " y:" + y, 100, 20, Color.BLACK);
		sv.DrawText("Gage:" + gage.getSw(), 300, 20, Color.BLACK);

		// ダメージ量の計算表示
		for (int i = 0; i < FELLOW_NUM; i++) {
			if (damageFtoE != 0) {
				sv.DrawText("ダメージ量:" + damageFtoE, fellow[i].getX(),fellow[i].getY(), Color.BLACK);
			}
		}
	}

	/**
	 * プレイヤーを渡す
	 * 
	 * @return player
	 */
	public Player getPlayer() {
		return b_player;
	}

}