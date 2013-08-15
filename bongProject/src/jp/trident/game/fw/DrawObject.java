package jp.trident.game.fw;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

/**
 * 描画に必要なデータをまとめたクラス
 *
 * 描画
 * sv.ScaleDrawImage(img, posX, posY, ani.sx, ani.sy, ani.sw, ani.sh, scaleX, scaleY, false);
 *
 * @author wa-rudo
 *
 */
public class DrawObject {

	/**
	 * 表示する幅
	 */
	public int width = 0;

	/**
	 * 表示する高さ
	 */
	public int height = 0;

	/**
	 * 位置座標X
	 */
	public int posX = 0;

	/**
	 * 位置座標Y
	 */
	public int posY = 0;

	/**
	 * スケール
	 */
	public float scaleX = 0.0f;

	/**
	 * スケール
	 */
	public float scaleY = 0.0f;

	/**
	 * アニメーション
	 */
	public Animation ani = null;

	/**
	 * 画像
	 */
	public Bitmap img = null;


	/**
	 * コンストラクタ
	 *
	 * @param width		表示される幅
	 * @param height	表示される高さ
	 */
	public DrawObject(int width, int height) {
		this.width = width;
		this.height = height;
		this.scaleX = 1.0f;
		this.scaleY = 1.0f;
		this.posX = 0;
		this.posY = 0;
	}

	/**
	 * 解放
	 */
	public void destroy() {
		if(this.img != null) {
			this.img.recycle();// メモリ不足になったときにメモリの解放を行う
			this.img = null;
		}
	}

	/**
	 * 画像を作成する
	 *
	 * @param context	コンテキスト
	 * @param id 		画像id
	 * @param opts 		ネクサス７用オプション
	 * @param sw 		１コマのテクスチャ幅
	 * @param sh 		１コマのテクスチャ高さ
	 */
	public void imgLoad(Context context, final int id, BitmapFactory.Options opts, final int sw, final int sh) {

		// 画像を作成する
		this.img = BitmapFactory.decodeResource(context.getResources(), id, opts);
		// アニメーションを作成する
		this.ani = new Animation(this.img.getWidth(), this.img.getHeight(), sw, sh);
		// スケールを計算する
		this.scaleX = ( (float)this.width  / (float)sw );
		this.scaleY = ( (float)this.height / (float)sh );
	}

	/**
	 * 画像をロードする
	 *
	 * @param img	画像
	 * @param sw	１コマのテクスチャ幅
	 * @param sh 	１コマのテクスチャ高さ
	 */
	public void imgLoad(Bitmap img, final int sw, final int sh) {

		// 画像を作成する
		this.img = img;
		// アニメーションを作成する
		this.ani = new Animation(this.img.getWidth(), this.img.getHeight(), sw, sh);
		// スケールを計算する
		this.scaleX = ( (float)this.width  / (float)sw );
		this.scaleY = ( (float)this.height / (float)sh );
	}

	/**
	 * 表示する幅・高さの大きさ変更
	 *
	 * @param width		表示する幅
	 * @param height	表示する高さ
	 */
	public void resize(int width, int height) {
		this.width = width;
		this.height = height;
		// スケールを計算する
		this.scaleX = ( (float)this.width  / (float)this.ani.sw );
		this.scaleY = ( (float)this.height / (float)this.ani.sh );
	}
}
