package jp.trident.game.rpg;

import jp.trident.game.fw.DrawObject;
import jp.trident.game.fw.GameSurfaceView;
import jp.trident.game.fw.R;

/**
 * 四角形に狭まるクラス
 * 画面切り替えのエフェクトに使う
 *
 * @author Owner
 *
 */
public class RectNarrowEffect {

	/**
	 * 背景
	 */
	private DrawObject bg = null;

	/**
	 * 背景配列の一次元数
	 */
	private int bgIndexWidth = 0;

	/**
	 * 背景配列の一次元
	 */
	private int bgIndexHeight = 0;

	/**
	 * 背景配列の長さ
	 */
	private int bgLength = 0;

	/**
	 * 時間
	 */
	private int time = 0;

	/**
	 * 更新速度
	 */
	private int speed = 10;

	/**
	 * アクティブ
	 */
	private boolean activeFlag = false;


	/**
	 * コンストラクタ
	 */
	public RectNarrowEffect() {
		GameUtility gu = GameUtility.getInstance();

		int _w = 20;
		int w = 800 / _w;
		int h = 480 / _w;
		this.bgIndexWidth  = _w;
		this.bgIndexHeight = _w;
		this.bgLength = h * w;
		this.bg = new DrawObject(w, h);
		this.bg.imgLoad(gu.context, R.drawable.map_test, gu.bitmapfOption, 10, 10);
	}

	/**
	 * コンストラクタ
	 *
	 * @param width		画面幅
	 * @param height	画面高さ
	 * @param sw		１コマのテクスチャ幅
	 * @param sh		１コマのテクスチャ高さ
	 */
	public RectNarrowEffect(int width, int height, int sw, int sh) {
		GameUtility gu = GameUtility.getInstance();

		int indexW = width  / sw;
		int indexH = height / sh;
		this.bgIndexWidth  = indexW;
		this.bgIndexHeight = indexH;
		this.bgLength = indexW * indexH;
		this.bg = new DrawObject(sw, sh);
		this.bg.imgLoad(gu.context, R.drawable.map_test, gu.bitmapfOption, 10, 10);
	}

	/**
	 * 解放
	 */
	public void destroy() {
		this.bg.destroy();
	}

	/**
	 * 初期化
	 */
	public void initialize() {
		this.time = 0;
		this.activeFlag = false;
	}

	/**
	 * 更新
	 */
	public boolean update() {

		// アクティブのとき更新を行う
		if(this.activeFlag == true) {

			if(this.bgLength < this.time) {
				return true;
			}

			// 時間経過
			this.time += this.speed;
		}

		return false;
	}

	/**
	 * 描画
	 */
	public void draw(GameSurfaceView sv) {

		// アクティブでないときは、描画しない
		if(this.activeFlag == false) return;

		int type = 0;
		int dx = 0;// 移動方向
		int dy = 1;
		int ix = 0;// 位置インデックス
		int iy = -1;
		int tx = 0;// 位置
		int ty = 0;
		int tw = this.bgIndexWidth;// 幅・高さ
		int th = this.bgIndexHeight;
		int px = 0;// 描画する位置座標
		int py = 0;
		for(int i = 0; i < this.bgLength; i++) {

			if(this.time < i) { break; }

			// 方向制限
			if(type == 0) if(iy + dy >= th) { dx = 1; dy = 0; tx++; type = 1; }// 高さの制限
			if(type == 1) if(ix + dx >= tw) { dx = 0; dy =-1; th--; type = 2; }// 幅の制限
			if(type == 2) if(iy + dy <  ty) { dx =-1; dy = 0; tw--; type = 3; }// Y座標の制限
			if(type == 3) if(ix + dx <  tx) { dx = 0; dy = 1; ty++; type = 0; }// X位置の制限

			// 要素を移動する
			ix += dx;
			iy += dy;

			// 位置座標へ
			px = ix * this.bg.width;
			py = iy * this.bg.height;

			sv.ScaleDrawImage(this.bg.img,
					px, py,
					this.bg.ani.sx, this.bg.ani.sy, this.bg.ani.sw, this.bg.ani.sh,
					this.bg.scaleX, this.bg.scaleY, false);
		}
	}

	/**
	 * 画面切り替えを有効にする
	 */
	public void enable() {
		this.activeFlag = true;
	}

}
