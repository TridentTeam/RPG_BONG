package jp.trident.game.fw;

import java.util.Stack;

/**
 * イベントシーンマネージャ
 *
 * @author wa-rudo
 *
 */
public class EventSceneManager {

	/**
	 * シーンスタック
	 */
	private Stack<IEventScene> scenes;

	/**
	 * 初期化フラグ
	 */
	private boolean sceneInitFlag = false;


	/**
	 * コンストラクタ
	 */
	public EventSceneManager() {
		BaseEventScene dummy = new BaseEventScene(null);
		this.scenes = new Stack<IEventScene>();
		this.scenes.push(dummy);
	}

	/**
	 * 解放
	 */
	public void destroy() {
		// シーンの解放
		for(int i = 0; i < this.scenes.size(); i++) {
			IEventScene scene = this.scenes.pop();
			scene.destroy();
		}
		// スタックの中身を空にする
		this.scenes.clear();
	}

	/**
	 * 更新
	 */
	public boolean update() {

		// 現在のシーンを取得
		IEventScene currentScene = this.getCurrentScene();

		if(this.sceneInitFlag == false) {
			currentScene.initialize();
			this.sceneInitFlag = true;
		}

		return currentScene.update();
	}

	/**
	 * 描画
	 *
	 * @param sv
	 */
	public void draw(GameSurfaceView sv) {
		// 現在のシーンを取得
		IEventScene currentScene = this.getCurrentScene();
		currentScene.draw(sv);
	}

	/**
	 * シーン変更
	 *
	 * @param sceneNext 次のシーン
	 *
	 * @return scenePre 前回のシーン
	 */
	public IEventScene sceneChange(IEventScene sceneNext) {
		IEventScene scenePre = this.scenes.pop();
		this.scenes.push(sceneNext);
		this.sceneInitFlag = false;

		return scenePre;
	}

	/**
	 * 現在のシーンを渡す
	 */
	public IEventScene getCurrentScene() {
		return this.scenes.peek();
	}
}
