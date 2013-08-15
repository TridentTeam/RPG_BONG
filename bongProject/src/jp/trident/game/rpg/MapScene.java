package jp.trident.game.rpg;


import java.util.ArrayList;

import android.graphics.Color;
import jp.trident.game.fw.BaseEventScene;
import jp.trident.game.fw.Collision;
import jp.trident.game.fw.Collision.HitData;
import jp.trident.game.fw.DrawObject;
import jp.trident.game.fw.EventSceneManager;
import jp.trident.game.fw.GameSurfaceView;
import jp.trident.game.fw.IEventScene;
import jp.trident.game.fw.IScene;
import jp.trident.game.fw.R;
import jp.trident.game.fw.Vector2;
import jp.trident.game.fw.VirtualController;

/**
 * マップシーン
 *
 * @author wa-rudo
 *
 * http://takabosoft.com/edge
 *
 */
public class MapScene implements IScene {
	
	/**
	 * マップファイルの名前定数
	 */
	private static final String FIELD_DIR = "fieldMapData/";
	private static final String FIELD_MAP_FILENAME = "fieldMap.map";
	private static final String FIELD_COLLMAP_FILENAME = "fieldCollMap.map";

	/**
	 * イベントシーンマネージャ
	 */
	private EventSceneManager eventSceneManager = null;

	/**
	 * イベント発生フラグ
	 */
	private boolean eventFlag = false;

	/**
	 * 背景関連変数
	 */
	private DrawObject bg = null;


	/**
	 * プレイヤー関連定数
	 */

	/**
	 * プレイヤー関連変数
	 */
	private MapPlayer mapPlayer = null;
	private DrawObject player = null;
	private DrawObject movePlayer = null;
	private int playerMoveXLimit = 0;
	private int playerMoveYLimit = 0;
	private int playerMoveXMaxLimit = 0;
	private int playerMoveYMaxLimit = 0;
	/**
	 * アニメーション定義 8
	 */
	private final int[][] playerAni = {
			{0,1,2},// 下
			{8,9,10},// 左
			{16,17,18},// 右
			{24,25,26},// 上
	};
	private int playerAniIndex = 0;
	
	/**
	 * ステージ番号
	 */
	private int stageNumber = 0;
	
	/**
	 * クエスト番号
	 */
	private int questNumber = 0;

	/**
	 * マップ
	 */
	private Map map = null;

	/**  残り時間 */
	private int remainingTime = 200;

	/**
	 * コリジョン
	 */
	private Collision collision = null;

	/**
	 * 画面切り替え効果
	 */
	private RectNarrowEffect screenChangeEffect = null;

	// デバッグ
	private DrawObject[] collObj = null;
	private DrawObject touch = null;
	private int collTypesIndex = 0;
	private boolean[][] collTypes = {
			{ true, true },
			{ true, false },
			{ false, true },
			{ false, false },
	};
	private int[] ids = {
			2,3,2,3,2,3
	};



	/**
	 * コンストラクタ
	 * 
	 * @param stageNumber		ステージ番号
	 */
	public MapScene(final int stageNumber, final int questNumber) {

		// ゲームユーティリティーを取得する。
		GameUtility gu = GameUtility.getInstance();
		
		// ステージ・クエストを保存する
		this.stageNumber = stageNumber;
		this.questNumber = questNumber;
		
		// イベントシーンマネージャを作成する。
		this.eventSceneManager = new EventSceneManager();

		// 背景を作成する。
		this.bg = new DrawObject(800, 480);

		// マッププレイヤーを作成する。
		this.mapPlayer = new MapPlayer();
		this.player = new DrawObject(64, 64);
		this.movePlayer = new DrawObject(44, 44);

		// マップを作成する。
		this.map = new Map(100, 100, 16, 16, R.drawable.map0);

		// コリジョンを取得する
		this.collision = gu.collision;

		// 画面切り替えエフェクト
		this.screenChangeEffect = new RectNarrowEffect(800, 500, 50, 50);

		// 画像を作成する。
		this.bg.imgLoad(gu.context, R.drawable.bg, gu.bitmapfOption, 1024, 512);
		this.player.imgLoad(gu.context, R.drawable.p_player, gu.bitmapfOption, 32, 32);
		this.movePlayer.imgLoad(this.player.img, 32, 32);

		// デバッグ
		this.touch = new DrawObject(50, 50);
		this.touch.imgLoad(gu.context, R.drawable.map_test, gu.bitmapfOption, 10, 10);

		this.collObj = new DrawObject[4];
		for(int i = 0; i < this.collObj.length; i++) {
			this.collObj[i] = new DrawObject(50,50);
			this.collObj[i].imgLoad(gu.context, R.drawable.map_test, gu.bitmapfOption, 10, 10);
			this.collObj[i].posX = i * 50;
			this.collObj[i].posY = 500;
			this.collObj[i].ani.frameAdd(i);
			this.collObj[i].ani.loopEnable();
		}
	}

	/**
	 * 解放
	 */
	public void destroy() {
		this.bg.destroy();
		this.bg = null;
		this.player.destroy();
		this.player = null;
		this.movePlayer.destroy();
		this.movePlayer = null;
		this.map.destroy();
		this.map = null;
		this.screenChangeEffect.destroy();
		this.screenChangeEffect = null;
	}

	/**
	 * 初期化
	 */
	public void initialize() {

		// ゲームユーティリティーを取得する。
		GameUtility gu = GameUtility.getInstance();

		// イベント発生フラグを無効にする。
		this.eventFlag = false;

		// 背景の初期化
		this.bg.posX = 0;
		this.bg.posY = 0;
		
		// 各ファイルを読み込む
		String mapFile 		= FIELD_DIR + this.stageNumber + "/" + this.questNumber + "/" + FIELD_MAP_FILENAME;
		String mapCollFile	= FIELD_DIR + this.stageNumber + "/" + this.questNumber + "/" + FIELD_COLLMAP_FILENAME;
		String mapsData = gu.readFileToAssets(mapFile);
		String mapCollsData = gu.readFileToAssets(mapCollFile);

		// マップを読み込む
		this.map.load(mapsData);
		this.map.collLoad(mapCollsData);

		// マッププレイヤーの初期化
		Vector2 mapSize = map.getMapSize();
		this.player.posX = 300;
		this.player.posY = 200;
		// プレイヤーの移動範囲
		this.playerMoveXLimit = 0;
		this.playerMoveYLimit = 0;
		this.playerMoveXMaxLimit = (int)mapSize.x;
		this.playerMoveYMaxLimit = (int)mapSize.y;
		// プレイヤーのアニメーション設定
		this.player.ani.initialize();
		this.playerAniIndex = 3;
		int[] frames = this.playerAni[this.playerAniIndex];
		this.player.ani.frameAdd(frames);
		this.player.ani.loopEnable();
		this.player.ani.frameSpeed(1);

		// 画面切り替えエフェクトの初期化
		this.screenChangeEffect.initialize();

		// コリジョンの初期化
		this.collision.initialize();
		
		// 当たり判定を追加
		this.collision.addListA( this.collision.createCollData(Collision.ID_PLAYER, Collision.LIST_A, true, true, this.player) );
		this.collision.addListA( this.collision.createCollData(Collision.ID_TOUCH, Collision.LIST_A, true, false, this.touch) );
		for(int i = 0; i < this.collObj.length; i++) {
			this.collision.addListB( this.collision.createCollData(this.ids[i], Collision.LIST_B, this.collTypes[i][0], this.collTypes[i][1], this.collObj[i]) );
		}

	}

	/**
	 * 更新
	 */
	public void update() {

		if(this.eventFlag == true) {

			// イベントの更新を行う
			boolean eventEndFlag = this.eventSceneManager.update();

			// イベントが終了した
			if(eventEndFlag == true) {

				// TODO イベント後のプレイヤー情報をマッププレイヤーに反映させる

				// イベントの解放
				IEventScene dummy = new BaseEventScene(null);
				IEventScene eventScene = this.eventSceneManager.sceneChange(dummy);
				eventScene.destroy();
				this.eventFlag = false;
			}
		}
		else {

			// マップ関連を更新する
			this.updateMapScene();
		}

		// コリジョンヒットリストをクリアする
		this.collision.clearHitList();
		// 当たり判定の更新を行う
		this.collision.update();

	}

	/**
	 * 描画
	 */
	public void draw(GameSurfaceView sv) {

		if(this.eventFlag == true) {
			this.eventSceneManager.draw(sv);
		}
		else {

			// マップ関連を描画する
			this.drawMapScene(sv);
		}

		// 残り時間を表示する。
		sv.DrawNumber(remainingTime, 400, 20);
	}

	/**
	 * マップシーンの更新
	 */
	private void updateMapScene() {
		
		// ゲームオーバー処理を行う
		if(remainingTime < 0) {
			GameUtility gu = GameUtility.getInstance();
			IScene scene = new ResultScene(this.stageNumber, this.questNumber, this.mapPlayer, this.remainingTime, true);
			gu.sceneManager.sceneReplace(scene);
			return;
		}
		
		// ゲームクリア処理を行う

		
		// ゲーム中処理を行う
		// 画面にタッチしてる？
		if (VirtualController.isTouch(0) == true) {
			// FPSの表示座標をタッチ位置に更新
			float touchX = VirtualController.getTouchX(0);
			float touchY = VirtualController.getTouchY(0);
			int dirX = (int)touchX - 400;//方向
			int dirY = (int)touchY - 240;
			int speed = 10;
			int moveX = 0;
			int moveY = 0;
			boolean aniChangeFlag = false;// アニメーション変更フラグ

			// 移動
			if( (dirX * dirX) > (dirY * dirY) ) {
				if(dirX > 0) {
					moveX += speed;
					if(this.playerAniIndex != 2) {
						aniChangeFlag = true;
						this.playerAniIndex = 2;
					}
				}
				else {
					moveX -= speed;
					if(this.playerAniIndex != 1) {
						aniChangeFlag = true;
						this.playerAniIndex = 1;
					}
				}
			}
			else {
				if(dirY > 0) {
					moveY += speed;
					if(this.playerAniIndex != 0) {
						aniChangeFlag = true;
						this.playerAniIndex = 0;
					}
				}
				else {
					moveY -= speed;
					if(this.playerAniIndex != 3) {
						aniChangeFlag = true;
						this.playerAniIndex = 3;
					}
				}
			}

			// アニメーション変更
			if(aniChangeFlag == true) {
				this.player.ani.frameClear();
				int[] frames = this.playerAni[this.playerAniIndex];
				this.player.ani.frameAdd(frames);
			}

			// プレイヤーの移動座標
			this.movePlayer.posX = this.player.posX + moveX;
			this.movePlayer.posY = this.player.posY + moveY;
			// プレイヤーの当たり判定座標
			this.movePlayer.posX = this.movePlayer.posX + speed;
			this.movePlayer.posY = this.movePlayer.posY + speed;
			boolean moveFlag = true;

			// プレイヤーの移動範囲を制限する。
			if( (this.movePlayer.posX < playerMoveXLimit) || (this.movePlayer.posX + this.movePlayer.width > playerMoveXMaxLimit) ) {
				moveFlag = false;
			}
			if( (this.movePlayer.posY < playerMoveYLimit) || (this.movePlayer.posY + this.movePlayer.height > playerMoveYMaxLimit) ) {
				moveFlag = false;
			}

			// プレイヤーとマップの当たり判定
			boolean coll = this.map.targetMapColision(this.movePlayer, moveX / speed, moveY / speed);

			if( (coll == false) && (moveFlag == true) ) {
				// プレイヤーの移動
				this.player.posX += moveX;
				this.player.posY += moveY;
			}
			else if(coll == true) {

				if(this.map.isPlayerToMapObjectCollision(1) == true) {

					// 海

				}

				if(this.map.isPlayerToMapObjectCollision(4) == true) {

					this.screenChangeEffect.enable();

					// デバッグで、バトルイベントに移行
					//this.eventSceneManager.sceneChange( new BattleEventScene(mapPlayer) );
					//this.eventFlag = true;
				}

			}// if coll
		}// if isTouch
		
		// シーン切り替えエフェクトを更新
		boolean sceEndFlag = this.screenChangeEffect.update();
		
		// シーン切り替えエフェクトが終了したとき、バトルイベントシーンへ移行する
		if(sceEndFlag == true) {
			// デバッグで、バトルイベントに移行
			this.eventSceneManager.sceneChange( new BattleEventScene(mapPlayer) );
			this.eventFlag = true;
			this.screenChangeEffect.initialize();
		}
		
		// プレイヤーのアニメーションの更新を行う
		this.player.ani.update();

		// デバッグ
		this.touch.posX -= 10;
		if(this.touch.posX < 0) {
			this.touch.posX = 800;
		}

		int speed = 10;
		for(int i = 0; i < this.collObj.length; i++) {
			DrawObject obj = this.collObj[i];

			obj.ani.update();
			obj.posX -= speed;

			if(obj.posX < 0) {
				obj.posX = 800;
			}

			speed += 10;
		}


		// 時間を経過させる
		this.remainingTime --;

	}// func end

	/**
	 * マップシーンの描画
	 */
	private void drawMapScene(GameSurfaceView sv) {

		// マップのビュー視点の位置座標
		Vector2 mapPos = this.map.getMapPos();
		Vector2 mapViewPos = this.map.getViewPos();
		int mapViewX = (int)( (mapPos.x - mapViewPos.x) );
		int mapViewY = (int)( (mapPos.y - mapViewPos.y) );

		// 背景を描画
		sv.ScaleDrawImage(this.bg.img,
				this.bg.posX, this.bg.posY,
				this.bg.ani.sx, this.bg.ani.sy, this.bg.ani.sw, this.bg.ani.sh,
				this.bg.scaleX, this.bg.scaleY, false);

		// マップを描画する。
		this.map.draw(sv, this.player.posX, this.player.posY);

		// プレイヤーを描画する。
		int px = this.player.posX + mapViewX;
		int py = this.player.posY + mapViewY;
		// プレイヤーを描画
		sv.ScaleDrawImage(this.player.img,
				px, py,
				this.player.ani.sx, this.player.ani.sy, this.player.ani.sw, this.player.ani.sh,
				this.player.scaleX, this.player.scaleY, false);

		// プレイヤーを描画する。
//		px = this.movePlayer.posX + mapViewX;
//		py = this.movePlayer.posY + mapViewY;
//		// プレイヤーを描画
//		sv.ScaleDrawImage(this.movePlayer.img,
//				px, py,
//				this.movePlayer.ani.sx, this.movePlayer.ani.sy, this.movePlayer.ani.sw, this.movePlayer.ani.sh,
//				this.movePlayer.scaleX, this.movePlayer.scaleY, false);


		sv.ScaleDrawImage(this.touch.img,
				this.touch.posX + mapViewX, this.touch.posY + mapViewY,
				this.touch.ani.sx, this.touch.ani.sy, this.touch.ani.sw, this.touch.ani.sh,
				this.touch.scaleX, this.touch.scaleY, false);


		for(int i = 0; i < this.collObj.length; i++) {
			DrawObject obj = this.collObj[i];
			sv.ScaleDrawImage(obj.img,
					obj.posX + mapViewX, obj.posY + mapViewY,
					obj.ani.sx, obj.ani.sy, obj.ani.sw, obj.ani.sh,
					obj.scaleX, obj.scaleY, false);
		}


		// 画面切り替えエフェクトを描画する
		this.screenChangeEffect.draw(sv);


		// プレイヤーのLevel・HP・MP
		this.mapPlayer.statusDraw(sv);

		// デバッグ
		// プレイヤーの位置座標
		sv.DrawText("プレイヤー位置 " + " ( " + this.player.posX + " , " + this.player.posY + " ) ", 0, 440, Color.WHITE);
		// プレイヤー最大移動制限
		sv.DrawText("プレイヤー最大移動制限 " + " ( " + this.playerMoveXMaxLimit + " , " + this.playerMoveYMaxLimit + " ) ", 0, 460, Color.WHITE);
		// プレイヤーの表示される領域をラインで表示する
		sv.DrawRectLine(this.player.posX + mapViewX, this.player.posY + mapViewY, this.player.width, this.player.height, Color.BLUE);
		// プレイヤーの当たり判定される領域をラインで表示する
		sv.DrawRectLine(this.movePlayer.posX + mapViewX, this.movePlayer.posY + mapViewY, this.movePlayer.width, this.movePlayer.height, Color.YELLOW);


		// コリジョンデバッグ文字位置
		int dw = 20;
		int dx = 600;
		int dy = 20;

		// 当たり判定
		ArrayList<HitData> playerCollList = this.collision.getHitData(this.player);
		if(playerCollList != null) {
			HitData hd = null;

//			int index = this.collision.hitListSearch(playerCollList, Collision.ID_VILLAGERS);
//			if(index != -1) {
//				hd = playerCollList.get(index);
//				sv.DrawText("hd " + " myId " + hd.myId + " id " + hd.id, dx, dy, Color.WHITE);
//				sv.DrawRectLine(hd.object.posX + mapViewX, hd.object.posY + mapViewY, hd.object.width, hd.object.height, Color.BLACK);
//
//				// 村人を当たり判定から消す
//				this.collision.removeList(this.pon);
//			}

			for(int i = 0; i < playerCollList.size(); i++) {
				hd = playerCollList.get(i);
				sv.DrawText("hd " + " myId " + hd.myId + " id " + hd.id, dx, dy, Color.WHITE);
				sv.DrawRectLine(hd.myObject.posX + mapViewX, hd.myObject.posY + mapViewY, hd.myObject.width, hd.myObject.height, Color.WHITE);
				sv.DrawRectLine(hd.object.posX + mapViewX, hd.object.posY + mapViewY, hd.object.width, hd.object.height, Color.BLACK);
				dy += dw;
			}
		}

		// テスト　コリジョン
		for(int i = 0; i < this.collObj.length; i++) {
			ArrayList<HitData> collList = this.collision.getHitData(this.collObj[i]);
			if(collList != null) {
				HitData hd = null;
				for(int j = 0; j < collList.size(); j++) {
					hd = collList.get(j);
					sv.DrawText("hd " + " myId " + hd.myId + " id " + hd.id, dx, dy, Color.WHITE);
					sv.DrawRectLine(hd.myObject.posX + mapViewX, hd.myObject.posY + mapViewY, hd.myObject.width, hd.myObject.height, Color.WHITE);
					sv.DrawRectLine(hd.object.posX + mapViewX, hd.object.posY + mapViewY, hd.object.width, hd.object.height, Color.BLACK);
					dy += dw;
				}
			}
		}

		// タッチの接触オブジェクトを取得する
		ArrayList<HitData> touchCollList = this.collision.getHitData(this.touch);
		if(touchCollList != null) {
			HitData hd = null;
			for(int i = 0; i < touchCollList.size(); i++) {
				hd = touchCollList.get(i);
				sv.DrawText("hd " + " myId " + hd.myId + " id " + hd.id, dx, dy, Color.WHITE);
				sv.DrawRectLine(hd.myObject.posX + mapViewX, hd.myObject.posY + mapViewY, hd.myObject.width, hd.myObject.height, Color.WHITE);
				sv.DrawRectLine(hd.object.posX + mapViewX, hd.object.posY + mapViewY, hd.object.width, hd.object.height, Color.BLACK);
				dy += dw;
			}
		}
	}
}
