/**
 * Gageクラス
 *
 * @author hagihara
 */

package jp.trident.game.rpg;

import jp.trident.game.fw.GameSurfaceView;
import android.graphics.Bitmap;

public class Gage {

	//----------------------//
	// 定数定義
	//----------------------//
	/** 幅の最大値 */
	private static final int MAX_HEIGHT = 480;
	/** 伸縮スピード既定値 */
	private static final int DEFAULT_SPEED = 50;
	/** 描画元の高さ */
	private static final int DEFAULT_SH = 140;

	//----------------------//
	// 変数定義
	//----------------------//
	/** 画像イメージ */
	private Bitmap img;

	/** x座標 */
	private int x;
	/** y座標 */
	private int y;
	/** 描画元の幅 */
	private int sw;
	/** 描画元の高さ */
	private int sh;

	/** ゲージ速度 */
	private int speed;
	/**
	 * ゲージの状態（止まっているか、動いているか）
	 * true:動いている
	 * false:止まっている
	 */
	private boolean state;

	/**
	 * コンストラクタ
	 */
	public Gage(int x, int y){
		this.x = x;
		this.y = y;
		sw = 0;
		sh = DEFAULT_SH;
		speed = DEFAULT_SPEED;
		state = true;
	}

	/**
	 * アップデート
	 */
	public void update(){
		sw += speed;
		if(sw > MAX_HEIGHT || sw < 0){
			speed *= -1;
		}
	}

	/**
	 * 描画
	 */
	public void Draw(GameSurfaceView sv){
		sv.DrawImage(img, x, y, 0, 0, sw, sh, false);
	}

	/**
	 * ゲージの停止
	 */
	public void stop(){
		speed = 0;
		state = false;
	}

	/**
	 * ゲージの初期化リセット
	 */
	public void reset(){
		speed = DEFAULT_SPEED;
		sw = 0;
		state = true;
	}

	/**
	 * imgのセット
	 */
	public void setImg(Bitmap img) {
		this.img = img;
	}

	/**
	 * state(ゲージの状態)のセット
	 */
	public void setState(boolean state) {
		this.state = state;
	}



	/**
	 * state(ゲージの状態)のゲット
	 */
	public boolean isState() {
		return state;
	}

	/**
	 * MAX_HEIGHT(幅の最大値)のゲット
	 * @return MAX_HEIGHT
	 */
	public static int getMaxHeight() {
		return MAX_HEIGHT;
	}

	/**
	 * DEFAULT_SPEED(伸縮スピード規定値)のゲット
	 * @return DEFAULT_SPEED
	 */
	public static int getDefaultSpeed() {
		return DEFAULT_SPEED;
	}

	/**
	 * DEFAULT_SH(描画元の高さ)のゲット
	 * @return DEFAULT_SH
	 */
	public static int getDefaultSh() {
		return DEFAULT_SH;
	}

	/**
	 * sw(描画元の幅)のゲット
	 * @return sw
	 */
	public int getSw() {
		return sw;
	}

	/**
	 * imgのゲット
	 * @return img
	 */
	public Bitmap getImg() {
		return img;
	}
}