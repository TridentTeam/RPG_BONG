package jp.trident.game.fw;

import java.util.Stack;

/**
 * シーンマネージャ
 *
 * @author wa-rudo
 *
 */
public class SceneManager {

	/**
	 * シーンスタック
	 */
	private Stack<IScene> scenes;

	/**
	 * シーンスタックのプッシュフラグ
	 */
	private boolean scenePushFlag = false;

	/**
	 * 現在のシーン
	 */
	private IScene currentScene = null;

	/**
	 * 次のシーン
	 */
	private IScene nextScene = null;

	/**
	 * 初期化フラグ
	 */
	private boolean sceneInitFlag = false;


	/**
	 * コンストラクタ
	 */
	public SceneManager() {
		IScene dummy = new BaseScene();
		this.currentScene = dummy;
		this.scenes = new Stack<IScene>();
		this.scenes.push(this.currentScene);
	}

	/**
	 * 解放
	 */
	public void destroy() {
		// シーンの解放
		for(int i = 0; i < this.scenes.size(); i++) {
			IScene scene = this.scenes.pop();
			scene.destroy();
		}
		// スタックの中身を空にする
		this.scenes.clear();
		this.scenes = null;
		this.currentScene = null;
		this.nextScene = null;
	}

	/**
	 * 更新
	 */
	public void update() {

		// 初期化されているか
		if(this.sceneInitFlag == false) {
			// シーンの変更要請があったとき
			if(this.nextScene != null) {
				if(this.scenePushFlag == false) {
					this.currentScene.destroy();// 現在のシーンを解放する
				}
				this.currentScene = this.nextScene;// 次のシーンを現在のシーンに切り替える
				this.nextScene = null;
			}

			this.currentScene.initialize();
			this.scenePushFlag = false;
			this.sceneInitFlag = true;
		}

		// 現在のシーンの更新
		this.currentScene.update();
	}

	/**
	 * 描画
	 *
	 * @param sv
	 */
	public void draw(GameSurfaceView sv) {
		this.currentScene.draw(sv);
	}

	/**
	 * シーンの変更を行う 前のシーンを削除する
	 *
	 * @param nextScene 次のシーン
	 *
	 * @return currentScene 現在のシーン
	 */
	public IScene sceneReplace(IScene nextScene) {
		this.nextScene = nextScene;
		this.currentScene = this.scenes.pop();
		this.scenes.push(nextScene);
		this.sceneInitFlag = false;

		return this.currentScene;
	}

	/**
	 * シーンの変更を行う 前のシーンを残す
	 *
	 * @param nextScene 次のシーン
	 *
	 * @return currentScene 現在のシーン
	 */
	public IScene scenePush(IScene nextScene) {
		this.nextScene = nextScene;
		this.currentScene = this.scenes.peek();
		this.scenes.push(nextScene);
		this.scenePushFlag = true;
		this.sceneInitFlag = false;

		return this.currentScene;
	}

	/**
	 * シーンを取得する
	 */
	public IScene scenePop() {
		this.currentScene = this.scenes.pop();
		this.nextScene = this.scenes.peek();
		this.sceneInitFlag = false;

		return this.currentScene;
	}

	/**
	 * 現在のシーンを渡す
	 */
	public IScene getCurrentScene() {
		return this.currentScene;
	}

	/**
	 * シーンの最後尾を取得
	 */
	public IScene getPeekScene() {
		return this.scenes.peek();
	}
}
