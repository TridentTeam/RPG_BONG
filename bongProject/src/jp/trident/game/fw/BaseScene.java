package jp.trident.game.fw;

import android.util.Log;

/**
 * ベースシーン
 *
 * @author wa-rudo
 *
 */
public class BaseScene implements IScene {

	/**
	 * シーン名
	 */
	protected String sceneName;


	/**
	 * コンストラクタ
	 */
	public BaseScene() {
		sceneName = "BaseScene";
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
	public void update() {

	}

	/**
	 * 描画
	 */
	public void draw(GameSurfaceView sv) {

	}

	/**
	 * ログ出力
	 */
	public void log(String str) {
		Log.v(sceneName, str);
	}

}
