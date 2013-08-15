package jp.trident.game.fw;

import java.util.ArrayList;

/**
 * アニメーションクラス
 *
 * メモ
 * 画像の幅・高さと１フレームの幅・高さを指定するだけで、
 * フレームでのテクスチャuvを計算してくれる。
 *
 * フレームリストに、フレーム数を追加して、更新すると自動でテクスチャuvを計算してくれる。
 *
 * フレーム番号は、「enchant.jsの画像フレームと同じ仕様」
 *
 * @author wa-rudo
 *
 */
public class Animation {

	/**
	 * テクスチャ位置座標
	 */
	public int sx = 0;

	/**
	 * テクスチャ位置座標
	 */
	public int sy = 0;

	/**
	 * １コマのテクスチャ幅
	 */
	public int sw = 0;

	/**
	 * １コマのテクスチャ高さ
	 */
	public int sh = 0;

	/**
	 * テクスチャの幅
	 */
	private int width = 0;

	/**
	 * テクスチャの高さ
	 */
	private int height = 0;

	/**
	 * フレーム数
	 */
	private int frame = 0;

	/**
	 * フレームリスト
	 */
	private ArrayList<Integer> frameList = null;

	/**
	 * フレームリストの要素番号
	 */
	private int frameListIndex = 0;

	/**
	 * 時間
	 */
	private int time = 0;

	/**
	 * アニメーション更新速度
	 */
	private int speed = 1;

	/**
	 * アニメーションが終了されたか
	 */
	private boolean animationEndFlag = false;

	/**
	 * アニメーションループするか
	 */
	private boolean loopFlag = false;



	/**
	 * コンストラクタ
	 *
	 * @param width 	画像の幅
	 * @param height 	画像の高さ
	 * @param sw		１コマのテクスチャ幅
	 * @param sh 		１コマのテクスチャ高さ
	 */
	public Animation(int width, int height, int sw, int sh) {
		this.width = width;
		this.height = height;
		this.sw = sw;
		this.sh = sh;
		this.frame = 0;
		this.frameList = new ArrayList<Integer>();
	}

	/**
	 * 初期化
	 */
	public void initialize() {
		this.frame = 0;
		this.frameList.clear();
		this.frameListIndex = 0;
		this.time = 0;
		this.speed = 1;
		this.animationEndFlag = false;
		this.loopFlag = false;
	}

	/**
	 * アニメーションの更新を行う
	 */
	public void update() {

		// 時間を経過させる
		this.time ++;

		int updateTime = this.time % this.speed;// 更新フレームの間隔
		final int size = this.frameList.size();
		int f = 0;

		// 更新時間がまだのとき　もしくは、フレームリストがないとき
		if( (updateTime != 0) || (size == 0) ) return;

		// アニメーションループが有効のとき、フレームリスト内を繰り返す
		if(this.loopFlag == true) {
			if(this.frameListIndex > size - 1) {
				this.frameListIndex = 0;
				this.animationEndFlag = true;// ループ中でも、最終のフレームに到達したことを示す
			}
			else {
				this.animationEndFlag = false;
			}
			// フレームリストから、フレームを取得し、更新する
			f = this.frameList.get(this.frameListIndex);
			this.frame(f);
			this.frameListIndex ++;
		}
		// アニメーションループが無効のとき、表示するフレームをリストから削除していく
		else {
			if(size == 1) {
				this.animationEndFlag = true;
			}
			f = this.frameList.get(this.frameListIndex);
			this.frameList.remove(this.frameListIndex);
			this.frame(f);
		}
	}

	/**
	 * アニメーションを変える
	 *
	 * @param frame フレーム数
	 */
	private void frame(final int frame) {
		int indexXMax = (int)(this.width / this.sw);// 画像幅の最大要素数を求める
		int indexX = frame % indexXMax;// フレームから、要素数を求める
		int indexY = frame / indexXMax;
		this.sx = indexX * this.sw;// 求めた要素数から座標へと変換する
		this.sy = indexY * this.sh;
		this.frame = frame;
	}

	/**
	 * 表示されているフレーム数を渡す
	 */
	public int getFrame() {
		return this.frame;
	}

	/**
	 * アニメーションフレームの追加
	 *
	 * @param frame フレーム数
	 */
	public void frameAdd(final int frame) {
		this.frameList.add(frame);
		this.animationEndFlag = false;
	}

	/**
	 * アニメーションフレームの追加
	 *
	 * @param frames フレーム配列
	 */
	public void frameAdd(final int[] frames) {
		for(int i = 0; i < frames.length; i++) {
			this.frameList.add(frames[i]);
		}
		this.animationEndFlag = false;
	}

	/**
	 * アニメーションフレームをカレントにする
	 * 更新を行わず、フレームリストから適応させる。
	 */
	public void frameCurrent() {
		int f = 0;
		if(this.loopFlag == true) {
			f = this.frameList.get(this.frameListIndex);
			this.frame(f);
			this.frameListIndex ++;
		}
		else {
			f = this.frameList.get(this.frameListIndex);
			this.frameList.remove(this.frameListIndex);
			this.frame(f);
		}
	}

	/**
	 * アニメーション更新速度を変更する
	 *
	 * @param speed 更新速度
	 */
	public void frameSpeed(final int speed) {
		this.speed = speed;
	}

	/**
	 * アニメーションフレームのクリア
	 */
	public void frameClear() {
		this.frameList.clear();
		this.frameListIndex = 0;
		this.time = this.speed - 1;
	}

	/**
	 * アニメーションが終了されたか？
	 */
	public boolean isAnimationEndFlag() {
		return this.animationEndFlag;
	}

	/**
	 * アニメーションループを有効にする
	 */
	public void loopEnable() {
		this.loopFlag = true;
	}

	/**
	 * アニメーションループを無効にする
	 */
	public void loopDisable() {
		this.loopFlag = false;
		this.frameListIndex = 0;
	}

	/**
	 * アニメーションループが有効か？
	 */
	public boolean isLoop() {
		return this.loopFlag;
	}

}
