package jp.trident.game.fw;

/**
 * シーンのインターフェイス
 *
 * @author s112136
 *
 */
public interface IScene {

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
	public void update();

	/**
	 * 描画
	 */
	public void draw(GameSurfaceView sv);

}
