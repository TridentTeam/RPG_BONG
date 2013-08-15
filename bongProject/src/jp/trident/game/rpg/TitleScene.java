package jp.trident.game.rpg;

import jp.trident.game.fw.DrawObject;
import jp.trident.game.fw.GameSurfaceView;
import jp.trident.game.fw.IScene;
import jp.trident.game.fw.R;
import jp.trident.game.fw.VirtualController;



/**
 * タイトルシーン
 *
 * @author wa-rudo
 *
 */
public class TitleScene implements IScene {

	/**
	 * 背景関連変数
	 */
	private DrawObject bg1 = null;
	private DrawObject bg2 = null;

	/**
	 * プレイヤー関連変数
	 */
	private DrawObject player = null;

	/**
	 * アニメーション定義 8
	 */
	private final int[][] playerAni = {
			{0,1,2},
			{8,9,10},
			{16,17,18},
			{24,25,26},
	};
	private int aniIndex = 0;

	/**
	 * タッチ開始
	 */
	private DrawObject touchStart = null;

	/**
	 * タッチ
	 */
	private DrawObject touch = null;

	/**
	 * 画面切り替え効果
	 */
	private RectNarrowEffect screenChangeEffect = null;


	/**
	 * コンストラクタ
	 */
	public TitleScene() {

		// ゲームユーティリティーを取得
		GameUtility gu = GameUtility.getInstance();

		// 背景を作成する
		this.bg1 = new DrawObject(800, 480);
		this.bg2 = new DrawObject(800, 480);
		this.bg1.imgLoad(gu.context, R.drawable.background, gu.bitmapfOption, 458, 259 / 2);
		this.bg2.imgLoad(gu.context, R.drawable.background, gu.bitmapfOption, 458, 259 / 2);
		this.bg2.ani.frameAdd(1);
		this.bg2.ani.update();

		// プレイヤーを作成する
		this.player = new DrawObject(100,100);
		this.player.imgLoad(gu.context, R.drawable.p_player, gu.bitmapfOption, 32, 32);

		// タッチ開始を作成する
		this.touchStart = new DrawObject(200, 50);
		this.touchStart.imgLoad(gu.context, R.drawable.gage, gu.bitmapfOption, 480, 96);

		// タッチを作成する
		this.touch = new DrawObject(50, 50);
		this.touch.imgLoad(gu.context, R.drawable.map_test, gu.bitmapfOption, 10, 10);

		this.screenChangeEffect = new RectNarrowEffect(800, 500, 50, 50);

	}

	/**
	 * 解放
	 */
	@Override
	public void destroy() {
		this.bg1.destroy();
		this.bg2.destroy();
		this.player.destroy();
		this.touchStart.destroy();
		this.touch.destroy();
		this.screenChangeEffect.destroy();
	}

	/**
	 * 初期化
	 */
	@Override
	public void initialize() {

		GameUtility gu = GameUtility.getInstance();

		// 当たり判定の初期化
		gu.collision.initialize();

		// 背景の初期化
		this.bg1.posX = 0;
		this.bg1.posY = 0;
		this.bg2.posX = 0;
		this.bg2.posY = this.bg1.height * -1;

		// プレイヤーの初期化
		this.player.posX = (800 / 2) - (this.player.width / 2);
		this.player.posY = (480 / 2);
		// アニメーション設定
		this.aniIndex = 3;
		int[] frames = this.playerAni[this.aniIndex];
		this.player.ani.loopEnable();
		this.player.ani.frameAdd(frames);

		// タッチ開始の初期化
		this.touchStart.posX = (800 / 2) - (this.touchStart.width / 2);
		this.touchStart.posY = 480 - (this.touchStart.height + 50);

		// タッチの初期化
		this.touch.posX = this.touch.width * -1;
		this.touch.posY = this.touch.height * -1;
	}

	/**
	 * 更新
	 */
	@Override
	public void update() {

		boolean change = this.screenChangeEffect.update();

		if(change == true) {
			// マップシーンに移行
			//IScene scene = new MapScene();
			IScene scene = new StageSelectScene();
			GameUtility gu = GameUtility.getInstance();
			gu.sceneManager.sceneReplace(scene);
		}

		if(VirtualController.isTouch(0) == true) {
			float x = VirtualController.getTouchX(0);
			float y = VirtualController.getTouchY(0);

			this.touch.posX = (int)x;
			this.touch.posY = (int)y;
		}

		if(VirtualController.isTouchTrigger(0) == true) {
			float x = VirtualController.getTouchX(0);
			float y = VirtualController.getTouchY(0);

			GameUtility gu = GameUtility.getInstance();

			this.touch.posX = (int)x;
			this.touch.posY = (int)y;
			boolean coll = gu.collision.collision(this.touch, this.touchStart);

			if(coll == true) {
				this.screenChangeEffect.enable();
			}

			{// アニメーションの確認
				int indexMax = this.playerAni.length;
				if(this.aniIndex > indexMax - 1) {
					this.aniIndex = 0;
				}
				int[] frames = this.playerAni[this.aniIndex];
				this.player.ani.frameClear();
				this.player.ani.frameAdd(frames);
				this.aniIndex++;
			}
		}


		// 背景の更新
		// 移動
		this.bg1.posY += 10;
		this.bg2.posY += 10;
		// 移動制限
		if(this.bg1.posY > 480) {
			this.bg1.posY = (this.bg2.posY - this.bg1.height);
		}
		if(this.bg2.posY > 480) {
			this.bg2.posY = (this.bg1.posY - this.bg2.height);
		}

		// プレイヤーの更新
		this.player.ani.update();
		// 移動
		this.player.posY -= 10;
		// 移動制限
		if(this.player.posY + this.player.height < 0) {
			this.player.posY = 480;
		}
	}

	/**
	 * 描画
	 */
	@Override
	public void draw(GameSurfaceView sv) {

		// 背景１を描画
		sv.ScaleDrawImage(this.bg1.img,
				this.bg1.posX, this.bg1.posY,
				this.bg1.ani.sx, this.bg1.ani.sy, this.bg1.ani.sw, this.bg1.ani.sh,
				this.bg1.scaleX, this.bg1.scaleY, false);
		// 背景２を描画
		sv.ScaleDrawImage(this.bg2.img,
				this.bg2.posX, this.bg2.posY,
				this.bg2.ani.sx, this.bg2.ani.sy, this.bg2.ani.sw, this.bg2.ani.sh,
				this.bg2.scaleX, this.bg2.scaleY, false);

		// プレイヤーを描画
		sv.ScaleDrawImage(this.player.img,
				this.player.posX, this.player.posY,
				this.player.ani.sx, this.player.ani.sy, this.player.ani.sw, this.player.ani.sh,
				this.player.scaleX, this.player.scaleY, false);

		// タッチ開始を描画
		sv.ScaleDrawImage(this.touchStart.img,
				this.touchStart.posX, this.touchStart.posY,
				this.touchStart.ani.sx, this.touchStart.ani.sy, this.touchStart.ani.sw, this.touchStart.ani.sh,
				this.touchStart.scaleX, this.touchStart.scaleY, false);

		// タッチを描画
		sv.ScaleDrawImage(this.touch.img,
				this.touch.posX, this.touch.posY,
				this.touch.ani.sx, this.touch.ani.sy, this.touch.ani.sw, this.touch.ani.sh,
				this.touch.scaleX, this.touch.scaleY, false);

		// 画面切り替えエフェクトを描画
		this.screenChangeEffect.draw(sv);

	}
}
