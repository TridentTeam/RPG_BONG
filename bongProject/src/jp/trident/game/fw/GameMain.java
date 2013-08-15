/*
 * ゲーム本体。
 */
package jp.trident.game.fw;

import java.util.Random;

import jp.trident.game.rpg.BattlePlayer;
import jp.trident.game.rpg.Fellow;
import jp.trident.game.rpg.Gage;
import jp.trident.game.rpg.GameUtility;
import jp.trident.game.rpg.MapScene;
import jp.trident.game.rpg.TitleScene;
import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.util.DisplayMetrics;

/**
 * ゲーム本体のアクティビティ。
 *
 * @author toki
 */
public class GameMain extends Activity {

	/** 乱数オブジェクト */
	private Random r = new Random();
	/** FPS */
	private int fps;
	/** 仮想コントローラ */
	private VirtualController virtualController;
	/** コンテキスト */
	private Context context;
	/** BGM */
	private MediaPlayer bgm;
	/** 効果音 */
	private MediaPlayer se;

	/** Nexus7上での画像切り出し表示時のずれを直す */
	public BitmapFactory.Options options = new BitmapFactory.Options();

	// ----------------------//
	// ゲーム用定数
	// ----------------------//

	//----------------------//
	// ゲーム用変数
	//----------------------//

	/** シーンマネージャ */
	private SceneManager sceneManager = null;
	
	/** コリジョン */
	private Collision collision = null;


	/** マウスを押したX座標 */
	private float x = 0;
	/** マウスを押したY座標 */
	private float y = 0;
	/**  フレームレート計算用カウンタ */
	private int timeCount = 0;




	/**
	 * コンストラクタ
	 */
	public GameMain(Context context) {
		this.context = context;

		// ピクセル密度に応じて画像をスケールさせるかどうか
		options.inScaled = false;

		// タッチ処理用コントローラを作成する。
		this.virtualController = new VirtualController();

		// ゲームユーティリティーを作成する。
		GameUtility gameUtility = GameUtility.getInstance();
		// シーンマネージャを作成する。
		this.sceneManager = new SceneManager();
		// コリジョンを作成する。
		this.collision = new Collision();
		// ゲームユーティリティーに保存する
		gameUtility.r = r;
		gameUtility.context = context;
		gameUtility.bitmapfOption = options;
		// シーンマネージャをゲームユーティリティに保存する。
		gameUtility.sceneManager = this.sceneManager;
		gameUtility.collision = this.collision;

	}

	/**
	 * 解放
	 */
	public void destroy() {

		// シーンマネージャの解放
		this.sceneManager.destroy();
		this.sceneManager = null;
		// コリジョンの解放
		this.collision.destroy();
		this.collision = null;
		
	}

	/**
	 * 0～maxの中からランダムな整数を得る。
	 *
	 * @param max
	 * @return	乱数値
	 */
	public int getRandom(int max) {
		return r.nextInt(max);
	}

	/**
	 * 全サウンドを終了する（アプリ終了時にも呼ばれる）
	 */
	public void stopSound() {
		//bgm.stop();
		//se.stop();
	}

	/**
	 * ゲームを初期化する。
	 */
	void initialize() {
		// BGMを読み込む。
		//bgm = MediaPlayer.create(context, R.raw.bgm);
		// BGMを再生する。
		//bgm.start();
		// 効果音を読み込む。
		//se = MediaPlayer.create(context, R.raw.se);
		
		// シーンマネージャの初期化
		//IScene scene = new MapScene();
		IScene scene = new TitleScene();
		this.sceneManager.scenePush(scene);

	}

	/**
	 * フレーム毎に座標などを更新する。
	 */
	public void update() {
		this.updateGame();
	}

	/**
	 * フレーム毎に描画する。
	 *
	 * @param sv	サーフェイスビュー
	 */
	public void draw(GameSurfaceView sv) {
		this.drawGame(sv);
	}

	/**
	 * ゲームの状態を更新する。
	 */
	void updateGame() {

		this.sceneManager.update();
	}

	/**
	 * ゲームシーンを描画する。
	 *
	 * @param sv	サーフェイスビュー
	 */
	void drawGame(GameSurfaceView sv) {

		this.sceneManager.draw(sv);

		// テキスト表示
		sv.DrawText("FPS:" + fps, 10, 20, Color.BLACK);
	}

	/**
	 * FPSを設定する。
	 *
	 * @param fps	FPS
	 */
	public void setFps(int fps) {
		this.fps = fps;
	}
}

