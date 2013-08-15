/*
 * エントリーポイント
 */
package jp.trident.game.fw;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.MotionEvent;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

/**
 * ゲーム初期化。
 *
 * @author wada
 */
public class GameActivity extends Activity implements Runnable {
	/**
	 * 仮想のスクリーンサイズ
	 * インターフェイスの配置などはこの数字を想定して配置する
	 */
	public static final int SYSTEM_BAR_SIZE = 0;

	/**
	 * 画面の幅。
	 */
	public static final int SCREEN_WIDTH = 800;

	/**
	 * 画面の高さ。
	 */
	public static final int SCREEN_HEIGHT = 480;

	/**
	 * フレームレート。
	 */
	public static final int FRAME_RATE = 10;

	/**
	 * スクリーンサイズ。
	 */
	public Rect screenRect;

	/**
	 * キャンバス。
	 */
	protected Canvas mainCanvas;

	/**
	 * メインスレッド。
	 */
	private Thread thread;

	/**
	 * メインループ用フラグ｡
	 */
	private static boolean loopFlag;

	/**
	 *  trueの時空ループ｡
	 */
	private boolean stopFlag;

	/**
	 * フレームレイアウト｡
	 */
	private FrameLayout frameLayout;

	/**
	 * サーフェイスのホルダーを保存｡
	 */
	//private SurfaceHolder surfaceHolder;

	/**
	 * サーファイスビュー｡
	 */
	private GameSurfaceView surfaceView;

	/**
	 * ゲーム本体。
	 */
	private GameMain gameMain;

	/**
	 * コンテキスト。
	 */
	//private Context context;

	/**
	 * 作成時の処理。
	 *
	 * @param savedInstanceState	保存されていたインスタンス
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// 変数初期化
		loopFlag = true;

		// 画面の矩形範囲を取得(本体全体)
		WindowManager windowManager = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
		Display display = windowManager.getDefaultDisplay();
		screenRect = new Rect(0, 0, display.getWidth(), display.getHeight() - SYSTEM_BAR_SIZE);
		Rect gameScreen = new Rect(0, 0, SCREEN_WIDTH, SCREEN_HEIGHT);

		// 描画レイヤーを作成して、初期化する。 作成順に注意すること。
		surfaceView = new GameSurfaceView(this, screenRect, gameScreen);

		// レイアウトを作成する。
		frameLayout = new FrameLayout(this);

		// レイヤーを登録する。
		frameLayout.addView(surfaceView, new LinearLayout.LayoutParams(
				LinearLayout.LayoutParams.WRAP_CONTENT,
				LinearLayout.LayoutParams.WRAP_CONTENT));


		// ビューを設定する。
		setContentView(frameLayout);

		gameMain = new GameMain(this);

		// スレッドを作成して無限ループする。
		thread = new Thread(this);
		thread.start();
	}

	/**
	 * 終了時の処理。
	 */
	@Override
	protected void onDestroy() {
		super.onDestroy();

		// サウンドを停止する。
		gameMain.stopSound();
		gameMain.destroy();

		// スレッドを削除する。
		thread = null;
	}

	/**
	 * 一時停止時の処理。
	 */
	@Override
	protected void onPause() {
		super.onPause();
	}

	/**
	 * 再開時の処理。
	 */
	@Override
	protected void onRestart() {
		super.onRestart();

		setContentView(frameLayout);

		// 停止中にする。
		stopFlag = false;
	}

	/**
	 * 開始時の処理。
	 */
	@Override
	protected void onStart() {
		super.onStart();

		// 停止中を解除する。
		stopFlag = false;
	}

	/**
	 * アプリ復帰時の処理。
	 */
	@Override
	protected void onResume() {
		// ビューを設定する。
		setContentView(frameLayout);
		super.onResume();
	}

	/**
	 * アプリ中断時の処理。
	 */
	@Override
	protected void onStop() {
		super.onStop();

		// 停止中を解除する。
		stopFlag = true;
	}

	/**
	 * スレッドで実行する更新処理。
	 */
	@Override
	public void run() {
		// フレーム管理FPS
		long currentTime = 0;
		long prevTime = 0;
		long prevTimeFPS = 0;
		long frameRate = (long) (1000.0f / FRAME_RATE);
		int fpsCount = 0;

		// ゲームを初期化する。
		gameMain.initialize();

		try {
			while (loopFlag) {
				// 現在時刻を取得する。
				currentTime = System.currentTimeMillis();

				// FPSを更新する。
				if (currentTime - prevTimeFPS > 1000) {
					prevTimeFPS = currentTime;
					gameMain.setFps(fpsCount);
					fpsCount = 0;
				}

				if (!stopFlag) {
					// 前回から一定時間たっていたら更新する。
					if (currentTime - prevTime > frameRate) {
						prevTime = currentTime;
						fpsCount++;

						// 入力を更新する。
						VirtualController.update();

						// アプリケーションの更新および描画する。
						gameMain.update();
						if (surfaceView.Begin()) {
							surfaceView.Clear(surfaceView.clearColor);
							gameMain.draw(surfaceView);
							surfaceView.End();
						}
					}
				}
			}
			
			// 終了する。
			finish();

		} catch (Exception e) {
			// 何もしない。
			Log.v( "GameActivity", e.getMessage() );
		}
	}

	/**
	 * 画面タッチ時のイベントを処理する。
	 *
	 * @param event	イベント
	 */
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		// イベントをコントローラに丸投げする。
		VirtualController.onEvent(event, screenRect.right, screenRect.bottom);
		return super.onTouchEvent(event);
	}

	/**
	 * 終了時の処理。
	 */
	public static void exit() {
		// ゲームループを抜けるフラグを立てる。
		loopFlag = false;
	}
}

