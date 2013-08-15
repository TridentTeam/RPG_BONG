package jp.trident.game.fw;

import jp.trident.game.rpg.Player;

/**
 * イベントシーンのインターフェイス
 *
 * @author s112136
 *
 */
public interface IEventScene {

	/**
	 * 解放
	 */
	public void destroy();

	/**
	 * 初期化
	 */
	public void initialize();

	/**
	 * 更新
	 */
	public boolean update();

	/**
	 * 描画
	 */
	public void draw(GameSurfaceView sv);

	/**
	 * プレイヤーを渡す
	 * @return player
	 */
	public Player getPlayer();

}
