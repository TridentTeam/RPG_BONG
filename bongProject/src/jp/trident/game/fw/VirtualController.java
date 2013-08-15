/*
 * コントローラ。
 */
package jp.trident.game.fw;

import android.view.MotionEvent;

/**
 * 仮想コントローラ。
 *
 * @author wada
 */
public class VirtualController {
	/**
	 * タッチデータ。
	 *
	 * @author wada
	 */
	class TouchData {
		/**
		 * タッチX座標。
		 */
		float x;

		/**
		 * タッチY座標。
		 */
		float y;

		/**
		 * 前回タッチされているか。
		 */
		boolean prevTouch;

		/**
		 * 今タッチされているか
		 */
		boolean touch;

		/**
		 * イベントデータ受け取り口、1フレームに1回touchにコピーされる
		 */
		boolean workTouch;

		/**
		 * OSが割り振るタッチのID
		 */
		int id;
	};

	/**
	 * 最大タッチ数。
	 */
	public static final int MAX_TOUCH = 2;

	/**
	 * タッチ状態記憶領域。
	 */
	private static TouchData touchData[];

	/**
	 * 現在タッチされている数。
	 */
	private static int numberOfTouches;

	/**
	 * コンストラクタでキー情報領域を確保する。
	 */
	public VirtualController() {
		touchData = new TouchData[2];
		for (int i = 0; i < touchData.length; i++) {
			touchData[i] = new TouchData();
		}
	}

	/**
	 * 入力を更新する。
	 */
	public static void update() {
		for (int i = 0; i < touchData.length; i++) {
			touchData[i].prevTouch = touchData[i].touch;
			touchData[i].touch = touchData[i].workTouch;
		}
	}

	/**
	 * タッチされているか？
	 *
	 * @param index
	 *            タッチのインデックス
	 * @return タッチされていればtrue
	 */
	public static boolean isTouch(int index) {
		return touchData[index].touch;
	}

	/**
	 * 最大タッチ数を取得する。
	 *
	 * @return 最大タッチ数
	 */
	public static int getMaxTouch() {
		return MAX_TOUCH;
	}

	/**
	 * タッチされた瞬間か？
	 *
	 * @param index
	 *            タッチのインデックス
	 * @return タッチされた瞬間ならtrue
	 */
	public static boolean isTouchTrigger(int index) {
		return (touchData[index].touch ^ touchData[index].prevTouch)
				& touchData[index].touch;
	}

	/**
	 * タッチX座標を取得する。 タッチ状態でなければ最後にタッチされていた位置が返る。
	 *
	 * @param index
	 *            タッチインデックス
	 * @return タッチX座標
	 */
	public static float getTouchX(int index) {
		return touchData[index].x;
	}

	/**
	 * タッチY座標を取得する。 タッチ状態でなければ最後にタッチされていた位置が返る。
	 *
	 * @param index
	 *            タッチインデックス
	 * @return タッチY座標
	 */
	public static float getTouchY(int index) {
		return touchData[index].y;
	}

	/**
	 * 丸投げされたイベントを処理する。
	 *
	 * @param event
	 *            OSから投げられるイベントデータそのまま
	 * @param screenX
	 *            画面X座標
	 * @param screenY
	 *            画面Y座標
	 */
	public static void onEvent(MotionEvent event, int screenX, int screenY) {
		// 現在タッチされている数を取得
		int count = event.getPointerCount();

		// リリースイベントが起きたときはgetPointerCountではタッチされている扱い
		switch (event.getAction()) {

		case MotionEvent.ACTION_UP:
			for (int i = 0; i < touchData.length; i++) {
				touchData[i].workTouch = false;
			}
			numberOfTouches = 0;
			return;
		}

		// 前回タッチの更新
		for (int i = 0; i < touchData.length; i++) {
			if (!touchData[i].workTouch) {
				continue;
			}

			touchData[i].workTouch = false;
			numberOfTouches--;
			for (int j = 0; j < count; j++) {
				if (touchData[i].id == event.getPointerId(j)) {
					touchData[i].x = event.getX(j)
							* GameActivity.SCREEN_WIDTH / screenX;
					touchData[i].y = (event.getY(j) - GameActivity.SYSTEM_BAR_SIZE)
							* GameActivity.SCREEN_HEIGHT / screenY;
					touchData[i].workTouch = true;
					numberOfTouches++;
					break;
				}
			}
		}

		// タッチの追加処理
		if (numberOfTouches >= touchData.length || count <= numberOfTouches) {
			return;
		}

		for (int i = 0; i < count; i++) {
			// 登録されていないIDを探す。
			boolean addFlag = true;
			for (int j = 0; j < touchData.length; j++) {
				if (touchData[j].workTouch
						&& touchData[j].id == event.getPointerId(i)) {
					addFlag = false;
					break;
				}
			}

			if (addFlag) {
				for (int j = 0; j < touchData.length; j++) {
					if (!touchData[j].workTouch) {
						touchData[j].workTouch = true;
						touchData[j].x = event.getX(i)
								* GameActivity.SCREEN_WIDTH / screenX;
						touchData[j].y = (event.getY(i) - GameActivity.SYSTEM_BAR_SIZE)
								* GameActivity.SCREEN_HEIGHT / screenY;
						touchData[j].id = event.getPointerId(i);
						numberOfTouches++;
						break;
					}
				}
			}

			if (numberOfTouches >= touchData.length) {
				break;
			}
		}
	}
}
