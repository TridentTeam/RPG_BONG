/**
 * 描画用サーフェイス。
 */
package jp.trident.game.fw;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.PorterDuff;
import android.graphics.Rect;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

/**
 * 描画用サーフェイス。
 *
 * @author wada
 */
public class GameSurfaceView extends SurfaceView implements SurfaceHolder.Callback {
	/**
	 * 塗りつぶし色。
	 */
	public int clearColor = Color.argb(255, 255, 255, 255);

	/**
	 * 最大描画オブジェクト数。
	 */
	protected Canvas canvas;

	/**
	 * オフスクリーン用キャンバス。
	 */
	protected Canvas offCanvas;

	/**
	 * オフスクリーンビットマップ。
	 */
	private Bitmap offscreen;

	/**
	 * 端末の画面サイズ。
	 */
	private Rect screenSize;

	/**
	 * ゲーム設定画面サイズ。
	 */
	private Rect gameScreenSize;

	/**
	 * 描画用設定。
	 */
	private Paint paint = new Paint();

	/**
	 * 保存用のサーフェイスのホルダー。
	 */
	private SurfaceHolder surfaceHolder;

	/**
	 * 数字用ビットマップ。
	 */
	private Bitmap bmpNumber;

	/**
	 * Nexus7上での画像切り出し表示時のずれを直す
	 */
	private BitmapFactory.Options options = new BitmapFactory.Options();

	/**
	 * コンストラクタで自分をホルダーに登録する。
	 *
	 * @param context
	 *            コンテキスト
	 * @param screen
	 *            スクリーンサイズ
	 * @param gameScreen
	 */
	public GameSurfaceView(Context context, Rect screen, Rect gameScreen) {
		super(context);

		screenSize = screen;
		gameScreenSize = gameScreen;
		setFocusable(true);
		requestFocus();

		// ホルダーに自分を登録する
		surfaceHolder = getHolder();
		surfaceHolder.setFormat(PixelFormat.TRANSPARENT);
		surfaceHolder.addCallback(this);

		// 数字用テクスチャの読み込み
		BitmapFactory.Options options = new BitmapFactory.Options();
	    options.inScaled = false;
	    bmpNumber = BitmapFactory.decodeResource(context.getResources(),R.drawable.number, options);
	}

	/**
	 * サーフェイスの作成、変更、破棄（未使用）。
	 */
	@Override
	public void surfaceCreated(SurfaceHolder holder) {
	}

	/**
	 * サーフェイスの変化で実行される。
	 */
	public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
	}

	/**
	 * サーフェイスの破棄で実行される。
	 */
	public void surfaceDestroyed(SurfaceHolder holder) {
	}

	/**
	 * 描画を開始する。 成功したら必ずEndで閉めること。
	 *
	 * @return 成功ならtrue
	 */
	protected boolean Begin() {
		// キャンバスロック
		canvas = surfaceHolder.lockCanvas();
		if (canvas != null && offscreen == null) {
			// オフスクリーンビットマップの作成
			offscreen = Bitmap.createBitmap(canvas.getWidth(), canvas.getHeight(), Bitmap.Config.ARGB_8888);
			offCanvas = new Canvas(offscreen);
			offCanvas.setBitmap(offscreen);
			offCanvas.drawColor(Color.WHITE);
		}
		return (canvas != null);
	}

	/**
	 * 描画を終了する。 Beginで開始したら必ずEndで閉めること。
	 */
	protected void End() {
		canvas.drawBitmap(offscreen, gameScreenSize, screenSize, null);
		// ロック解除
		if (canvas != null) {
			surfaceHolder.unlockCanvasAndPost(canvas);
		}
	}

	/**
	 * キャンバスを指定色でクリアする。 必ずBegin～End間で呼ぶこと。
	 *
	 * @param color
	 *            色
	 * @return 成功したらtrue
	 */
	public boolean Clear(int color) {
		if (offCanvas == null) {
			return false;
		}

		// 指定色で完全に上書きする(アルファブレンドオフ)
		offCanvas.drawColor(color, PorterDuff.Mode.SRC);
		return true;
	}

	/**
	 * キャンバスを指定色でクリアする 必ずBegin～End間で呼ぶこと
	 *
	 * @param color
	 *            色
	 * @return 成功したらtrue
	 */
	protected boolean ClearBlur(int color) {
		if (canvas == null) {
			return false;
		}

		canvas.drawColor(color, PorterDuff.Mode.SRC_OVER);
		return true;
	}

	/**
	 * テキストを描画する。
	 *
	 * @param s
	 *            文字列
	 * @param x
	 *            X座標
	 * @param y
	 *            Y座標
	 * @param color
	 *            色
	 */
	public void DrawText(String s, int x, int y, int color) {
		paint.setColor(color);
		paint.setAntiAlias(true);
		paint.setTextSize(20);

		offCanvas.drawText(s, x, y, paint);
	}

	/**
	 * 矩形の線を描画する。
	 *
	 * @param x  X座標
	 * @param y  Y座標
	 * @param w  幅
	 * @param h  高さ
	 * @param color  色
	 */
	public void DrawRectLine(int x, int y, int w, int h, int color) {
		paint.setColor(color);
		paint.setStrokeWidth(5.0f);

		offCanvas.drawLine(x, y, x + w, y, paint);
		offCanvas.drawLine(x, y, x, y + h, paint);
		offCanvas.drawLine(x + w, y, x + w, y + h, paint);
		offCanvas.drawLine(x, y + h, x + w, y + h, paint);
	}

	/**
	 * 画像を描画する。
	 *
	 * @param bmp	ビットマップ
	 * @param x		描画先のX座標
	 * @param y		描画先のY座標
	 */
	public void DrawImage(Bitmap bmp, int x, int y) {
		offCanvas.drawBitmap(bmp, x, y, paint);
	}

	/**
	 * 画像を描画する。
	 *
	 * @param bmp	ビットマップ
	 * @param x		描画先のX座標
	 * @param y		描画先のY座標
	 * @param sx	描画元のX座標
	 * @param sy	描画元のY座標
	 * @param sw	描画元の幅
	 * @param sh	描画元の高さ
	 * @param reverse	反転するか？
	 */
	public void DrawImage(Bitmap bmp, int x, int y, int sx, int sy, int sw, int sh, boolean reverse) {

		Rect src = new Rect(sx, sy, sx + sw, sy + sh);
		Rect desc = new Rect(x, y, x + sw, y + sh);

		if (reverse) {
			desc.left = desc.right * -1;
			desc.right = (int) (desc.left + sw);

			offCanvas.save();
			offCanvas.scale(-1.0f, 1.0f, 1.0f, 1.0f);
			offCanvas.drawBitmap(bmp, src, desc, null);
			offCanvas.restore();
		} else {
			offCanvas.drawBitmap(bmp, src, desc, null);
		}
	}

	/**
	 * 画像を描画する。
	 *
	 * @param bmp	ビットマップ
	 * @param x		描画先のX座標
	 * @param y		描画先のY座標
	 * @param sx	描画元のX座標
	 * @param sy	描画元のY座標
	 * @param sw	描画元の幅
	 * @param sh	描画元の高さ
	 * @param scaleW	幅の倍率
	 * @param scaleH	高さの倍率
	 * @param reverse	反転するか？
	 */
	public void ScaleDrawImage(Bitmap bmp, int x, int y, int sx, int sy, int sw,
			int sh, int scaleW, int scaleH, boolean reverse){

		Rect src = new Rect(sx, sy, sx + sw, sy + sh);
		Rect desc = new Rect(x, y, x + (sw*scaleW), y + (sh*scaleH));

		if (reverse) {
			desc.left = desc.right * -1;
			desc.right = (int) (desc.left + sw);

			offCanvas.save();
			offCanvas.scale(-1.0f, 1.0f, 1.0f, 1.0f);
			offCanvas.drawBitmap(bmp, src, desc, null);
			offCanvas.restore();
		} else {
			offCanvas.drawBitmap(bmp, src, desc, null);
		}
	}
	
	/**
	 * 画像を描画する。
	 *
	 * @param bmp	ビットマップ
	 * @param x		描画先のX座標
	 * @param y		描画先のY座標
	 * @param sx	描画元のX座標
	 * @param sy	描画元のY座標
	 * @param sw	描画元の幅
	 * @param sh	描画元の高さ
	 * @param scaleW	幅の倍率
	 * @param scaleH	高さの倍率
	 * @param reverse	反転するか？
	 */
	public void ScaleDrawImage(Bitmap bmp, int x, int y, int sx, int sy, int sw,
			int sh, float scaleW, float scaleH, boolean reverse){

		Rect src = new Rect(sx, sy, sx + sw, sy + sh);
		Rect desc = new Rect(x, y, x + (int)(sw*scaleW), y + (int)(sh*scaleH));

		if (reverse) {
			desc.left = desc.right * -1;
			desc.right = (int) (desc.left + sw);

			offCanvas.save();
			offCanvas.scale(-1.0f, 1.0f, 1.0f, 1.0f);
			offCanvas.drawBitmap(bmp, src, desc, null);
			offCanvas.restore();
		} else {
			offCanvas.drawBitmap(bmp, src, desc, null);
		}
	}

	/**
	 * 数字をテクスチャで描画する。
	 *
	 * @param value		数値
	 * @param x		描画先のX座標
	 * @param y		描画先のY座標
	 */
	public void DrawNumber(int value, int x, int y) {
		// 桁数を求める
		int count = 1;
		int value2 = value;
		while (true) {
			if (value2 / 10 >= 10) {
				count++;
				value2 /= 10;
			} else {
				count++;
				break;
			}
		}

		value2 = value;
		for (int i = 0; i < count; i++) {
			value2 = value % 10;
			value /= 10;
			DrawImage(bmpNumber, x + ((count - i) * 32), y, value2 * 32, 0, 32,
					32, false);
		}
	}
}
