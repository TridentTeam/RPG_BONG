package jp.trident.game.fw;

import jp.trident.game.rpg.Player;
import android.util.Log;

/**
 * ベースイベントシーン
 *
 * @author wa-rudo
 *
 */
public class BaseEventScene implements IEventScene {

	/**
	 * シーン名
	 */
	protected String sceneName;

	/**
	 * コンストラクタ
	 */
	public BaseEventScene() {
		sceneName = "BaseEventScene";
	}

	/**
	 * コンストラクタ
	 *
	 * @param player プレイヤー
	 */
	public BaseEventScene(Player player) {
		sceneName = "BaseEventScene";
		setPlayer(player);
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
		return false;
	}

	/**
	 * 描画
	 */
	public void draw(GameSurfaceView sv) {

	}

	/**
	 * プレイヤーを渡す
	 * @return player
	 */
	public Player getPlayer() {
		return null;
	}

	/**
	 * プレイヤーをセット
	 *
	 * @param player プレイヤー
	 */
	public void setPlayer(Player player) {

	}

	/**
	 * ログ出力
	 */
	public void log(String str) {
		Log.v(sceneName, str);
	}

}
