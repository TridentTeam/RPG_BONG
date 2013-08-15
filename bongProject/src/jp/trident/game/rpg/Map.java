package jp.trident.game.rpg;

import java.util.ArrayList;

import jp.trident.game.fw.Collision;
import jp.trident.game.fw.DrawObject;
import jp.trident.game.fw.GameSurfaceView;
import jp.trident.game.fw.Vector2;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.util.Log;

/**
 * マップ
 *
 * @author s112136
 *
 */
public class Map {

	/**
	 * マップの位置座標 ワールド座標
	 */
	private Vector2 mapPos = null;
	
	/**
	 * ベースの描画マップオブジェクト
	 */
	private DrawObject baseMapObject = null;

	/**
	 * マップ配列 マップのローカル座標
	 */
	private DrawObject[][] mapArray = null;

	/**
	 * チップ配列
	 */
	private int[][] chipArray = null;

	/**
	 * マップのコリジョン配列
	 */
	private int[][] collArray = null;

	/**
	 * ビュー関連の変数
	 */

	/**
	 * ビューの位置座標
	 */
	private Vector2 viewPos = null;

	/**
	 * ビューの幅・高さ
	 */
	private Vector2 viewSize = null;

	/**
	 * 移動検索変数
	 */
	private ArrayList<Vector2> dmoveList = null;

	/**
	 * プレイヤーと接触したオブジェクト識別リスト
	 */
	private ArrayList<Integer> collMapIdList = null;
	
	/**
	 * コリジョン
	 */
	private Collision collision = null;

	/**
	 * デバッグモード
	 */
	private boolean debugMode = true;
	

	/**
	 * コンストラクタ
	 * 
	 * @param w 
	 */
	public Map(int w, int h, int sw, int sh, final int imgId) {
		GameUtility gu = GameUtility.getInstance();
		
		this.baseMapObject = new DrawObject(w, h);
		this.baseMapObject.imgLoad(gu.context, imgId, gu.bitmapfOption, sw, sh);
		this.baseMapObject.ani.loopEnable();
		this.mapPos 	= new Vector2();
		this.viewPos    = new Vector2();
		this.viewSize 	= new Vector2(800.0f, 480.0f);
		this.collMapIdList = new ArrayList<Integer>();
		this.collision = gu.collision;
	}
	
	/**
	 * 解放
	 */
	public void destroy() {
		this.baseMapObject.destroy();
		for(int y = 0; y < this.mapArray.length; y++) {
			for(int x = 0; x < this.mapArray[y].length; x++) {
				this.mapArray[y][x].destroy();
			}
		}
		if(this.collMapIdList != null) {
			this.collMapIdList.clear();
			this.collMapIdList = null;
		}
		this.collision = null;
	}

	/**
	 * マップのロード
	 * 
	 * @param sData		マップファイルを読み込んだ文字列データ
	 */
	public void load(final String sData) {
		if(sData == null) { return; }
		
		// ベースオブジェクトのデータを取得
		final DrawObject bmo = this.baseMapObject;
		String[] sLines = sData.split("\n");//改行ごとに文字列を取得する
		String[] size = sLines[0].split(",");
		final int sizeYMax = Integer.parseInt(size[0]);
		final int sizeXMax = Integer.parseInt(size[1]);
		// 配列確保
		this.mapArray  = new DrawObject[sizeYMax][sizeXMax];
		this.chipArray = new int[sizeYMax][sizeXMax];

		int m = 0;
		int posX = 0;
		int posY = 0;
		for(int y = 0; y < sizeYMax; y++) {
			String[] _sData = sLines[y + 1].split(",");
			if(_sData == null) { continue; }
			
			for(int x = 0; x < sizeXMax; x++) {
				m = Integer.parseInt(_sData[x]);
				posX = bmo.width * x;
				posY = bmo.height * y;
				DrawObject drawObj = new DrawObject(bmo.width, bmo.height);
				drawObj.imgLoad(bmo.img, bmo.ani.sw, bmo.ani.sh);
				drawObj.ani.frameAdd(m);
				drawObj.ani.frameCurrent();
				drawObj.posX = posX;
				drawObj.posY = posY;
				
				this.mapArray[y][x] = drawObj;
				this.chipArray[y][x] = m;
			}
		}
	}

	/**
	 * マップのコリジョンロード
	 * 
	 * @param sData		コリジョンマップファイルを読み込んだ文字列データ
	 */
	public void collLoad(final String sData) {
		if(sData == null) { return; }
		
		String[] sLines = sData.split("\n");//改行ごとに文字列を取得する
		String[] size = sLines[0].split(",");
		final int sizeYMax = Integer.parseInt(size[0]);
		final int sizeXMax = Integer.parseInt(size[1]);
		this.collArray = new int[sizeYMax][sizeXMax];// 配列確保
		
		for(int y = 0; y < sizeYMax; y++) {
			String[] _sData = sLines[y + 1].split(",");
			if(_sData == null) { continue; }
			
			for(int x = 0; x < sizeXMax; x++) {
				int collFlag = Integer.parseInt(_sData[x]);
				
				this.collArray[y][x] = collFlag;
			}
		}
	}

	/**
	 * マップの移動
	 */
	public void mapMove(float x, float y) {
		this.mapPos.x += x;
		this.mapPos.y += y;
	}

	/**
	 * 指定オブジェクトとマップとの当たり判定
	 *
	 * @param obj	: オブジェクト
	 * @param dirX	: 移動方向
	 * @param dirY	: 移動方向
	 *
	 * @return 接触したか : true 接触あり / false 接触なし
	 */
	public boolean targetMapColision(DrawObject obj, float dirX, float dirY) {

		// 当たり判定の座標は、マップ座標で行う。ビュー座標に変換しない
		// map [0][0] を原点とする

		boolean collision = false;
		// プレイヤーと接触したマップオブジェクト識別子をクリア
		this.collMapIdList.clear();

		// 移動時に、プレイヤー周りのマップを検索
		ArrayList<Vector2> moveList = this.moveSearch(obj.posX, obj.posY, obj.width, obj.height, dirX, dirY);
		int moveSize = moveList.size();
		this.dmoveList = moveList;// デバッグ用
		// マップ要素
		int mapMxMaxIndex = this.mapArray[0].length;
		int mapMyMaxIndex = this.mapArray.length;
		for(int i = 0; i < moveSize; i++) {
			// 取得
			Vector2 move = moveList.get(i);
			int indexX = (int)move.x;
			int indexY = (int)move.y;
			// マップの要素制限を行う
			indexX = (indexX < 0) ? 0 : indexX;
			indexY = (indexY < 0) ? 0 : indexY;
			indexX = (indexX >= mapMxMaxIndex) ? mapMxMaxIndex - 1 : indexX;
			indexY = (indexY >= mapMyMaxIndex) ? mapMyMaxIndex - 1 : indexY;

			int mapColl = this.collArray[indexY][indexX];// マップコリジョン判定
			int mapId   = this.chipArray[indexY][indexX];

			// 当たり判定対象外
			if(mapColl == 0) continue;

			boolean coll = this.collisionMapObject(obj, indexX, indexY);

			// 当たっていたとき
			if(coll == true) {
				collision = true;
				// プレイヤーと接触したマップオブジェクト識別子を追加
				this.collMapIdList.add(mapId);
			}
		}

		return collision;
	}

	/**
	 * マップオブジェクトと当たり判定
	 *
	 * @param obj	: オブジェクト
	 * @param indexX : マップオブジェクトの要素番号
	 * @param indexY : マップオブジェクトの要素番号
	 */
	private boolean collisionMapObject(DrawObject obj, int indexX, int indexY) {
		// 取得
		DrawObject drawObj = this.mapArray[indexY][indexX];// 描画オブジェクト

		if(drawObj == null) return false;

		// 当たり判定
		boolean coll = this.collision.collision(obj, drawObj);
		
		return coll;
	}

	/**
	 * マップ上での移動範囲検索
	 */
	private ArrayList<Vector2> moveSearch(float posX, float posY, float w, float h, float dirX, float dirY) {
		ArrayList<Vector2> moveList = new ArrayList<Vector2>();// マップの要素数を格納するリスト
		final float sw = this.baseMapObject.width;
		final float sh = this.baseMapObject.height;
		float tempX = 0.0f;// 計算用変数
		float tempY = 0.0f;
		int indexX = 0;
		int indexY = 0;

		// 現在のプレイヤー位置
		tempX = (posX / sw);
		tempY = (posY / sh);
		indexX = (int)tempX;
		indexY = (int)tempY;
		Vector2 move = new Vector2(indexX, indexY);
		moveList.add(move);

		// 移動方向
		tempX = (posX / sw) + dirX;
		tempY = (posY / sh) + dirY;
		indexX = (int)tempX;
		indexY = (int)tempY;
		Vector2 move1 = new Vector2(indexX, indexY);
		moveList.add(move1);

		// X
		if( (tempX - indexX) < 0.5f ) {
			tempX = (posX / sw) + dirX - 1;
			tempY = (posY / sh) + dirY;
			indexX = (int)tempX;
			indexY = (int)tempY;
			Vector2 move2 = new Vector2(indexX, indexY);
			moveList.add(move2);
		}
		else if( (tempX - indexX) > 0.5f ) {
			tempX = (posX / sw) + dirX + 1;
			tempY = (posY / sh) + dirY;
			indexX = (int)tempX;
			indexY = (int)tempY;
			Vector2 move2 = new Vector2(indexX, indexY);
			moveList.add(move2);
		}
		// Y
		if( (tempY - indexY) < 0.5f ) {
			tempX = (posX / sw) + dirX;
			tempY = (posY / sh) + dirY - 1;
			indexX = (int)tempX;
			indexY = (int)tempY;
			Vector2 move3 = new Vector2(indexX, indexY);
			moveList.add(move3);
		}
		else if( (tempY - indexY) > 0.5f ) {
			tempX = (posX / sw) + dirX;
			tempY = (posY / sh) + dirY + 1;
			indexX = (int)tempX;
			indexY = (int)tempY;
			Vector2 move3 = new Vector2(indexX, indexY);
			moveList.add(move3);
		}

		return moveList;
	}

	/**
	 * マップチップの属性取得
	 */

	/**
	 * マップの描画
	 *
	 * @param sv	: 描画クラス
	 * @param posX	: プレイヤーX座標
	 * @param posY	: プレイヤーY座標
	 *
	 */
	public void draw(GameSurfaceView sv, float posX, float posY) {
		// マップの位置座標
		final int mow = this.baseMapObject.width;
		final int moh = this.baseMapObject.height;
		final int mapWidth  = (this.mapArray[0].length * mow);
		final int mapHeight = (this.mapArray.length    * moh);

		// プレイヤーの位置座標からビュー位置座標を計算する
		float viewW = this.viewSize.x;
		float viewH = this.viewSize.y;
		float viewPosX = posX - (viewW / 2.0f);
		float viewPosY = posY - (viewH / 2.0f);
		float viewPosXMax = posX + (viewW / 2.0f);
		float viewPosYMax = posY + (viewH / 2.0f);
		// ビューの制限
		if(viewPosX < 0) {
			viewPosX = 0;
			viewPosXMax = viewPosX + viewW;
		}
		else if(viewPosXMax > mapWidth) {
			viewPosXMax = mapWidth;
			viewPosX = viewPosXMax - viewW;
		}
		if(viewPosY < 0) {
			viewPosY = 0;
			viewPosYMax = viewPosY + viewH;
		}
		else if(viewPosYMax > mapHeight) {
			viewPosYMax = mapHeight;
			viewPosY = viewPosYMax - viewH;
		}

		// デバッグ
//		viewPosX = posX;
//		viewPosY = posY;

		// ビューを保存
		this.viewPos.x = viewPosX;
		this.viewPos.y = viewPosY;

		// マップ描画位置座標
		int mapViewPosX = (int)(this.mapPos.x - this.viewPos.x);
		int mapViewPosY = (int)(this.mapPos.y - this.viewPos.y);

		// マップの最大要素数
		final int mapMxMaxIndex = this.mapArray[0].length;
		final int mapMyMaxIndex = this.mapArray.length;

		// 描画領域をマップの要素数へ変換する
		int mxIndex = (int)(viewPosX / mow);
		int myIndex = (int)(viewPosY / moh);
		int mxMaxIndex = (int)(viewPosXMax / mow) + 1;
		int myMaxIndex = (int)(viewPosYMax / moh) + 1;
		// 制限
		mxMaxIndex = (mxMaxIndex > mapMxMaxIndex) ? mapMxMaxIndex : mxMaxIndex;
		myMaxIndex = (myMaxIndex > mapMyMaxIndex) ? mapMyMaxIndex : myMaxIndex;

		// ビュー視点を切り取り描画
		this.targetDraw(sv, this.mapArray, mxIndex, myIndex, mxMaxIndex, myMaxIndex, mapViewPosX, mapViewPosY);

		if(this.debugMode == true) {
			
			// 移動項目の当たり判定を行うリストをデバッグ表示する
			if(this.dmoveList != null) {
				for(int i = 0; i < this.dmoveList.size(); i++) {
					Vector2 m = this.dmoveList.get(i);
					float _x = m.x * mow;
					float _y = m.y * moh;
					_x += mapViewPosX;
					_y += mapViewPosY;
					sv.DrawRectLine( (int)_x, (int)_y, mow, moh, Color.RED);
				}
			}
			
			// ビュー領域を表示する
			sv.DrawRectLine( (int)this.mapPos.x, (int)this.mapPos.y, (int)this.viewSize.x, (int)this.viewSize.y, Color.WHITE);
			
			// ビューのインデックスを表示する
			int dtw = 20;
			int dtx = 200;
			int dty = 200;
			sv.DrawText("ビュー index " + " ( " + mxIndex + " , " + myIndex + " ) ", dtx, dty, Color.WHITE);
			dty += dtw;
			sv.DrawText("ビュー indexMax " + " ( " + mxMaxIndex + " , " + myMaxIndex + " ) ", dtx, dty, Color.WHITE);
		}

	}

	/**
	 * 指定を描画
	 *
	 * @param sv	: サーフェイスビュー
	 * @param map	: マップ配列
	 * @param x		: 一次配列の最初
	 * @param y		: 二次配列の最初
	 * @param xMax	: 一次配列の最後
	 * @param yMax	: 二次配列の最後
	 * @param vx	: 視点位置
	 * @param vy	: 視点位置
	 */
	public void targetDraw(GameSurfaceView sv, DrawObject[][] map, int x, int y, int xMax, int yMax, int vx, int vy) {
		
		int posX = 0;
		int posY = 0;
		for(int _y = y; _y < yMax; _y++) {
			for(int _x = x; _x < xMax; _x++) {

				// 描画オブジェクトを取得
				DrawObject drawObj = map[_y][_x];
				posX = (int)drawObj.posX + vx;
				posY = (int)drawObj.posY + vy;

				sv.ScaleDrawImage(drawObj.img, posX, posY, drawObj.ani.sx, drawObj.ani.sy, drawObj.ani.sw, drawObj.ani.sh, drawObj.scaleX, drawObj.scaleY, false);
			}
		}
	}

	/**
	 * マップの幅・高さを渡す
	 */
	public Vector2 getMapSize() {
		Vector2 mapSize = new Vector2();
		mapSize.x = (this.mapArray[0].length * this.baseMapObject.width);
		mapSize.y = (this.mapArray.length    * this.baseMapObject.height);
		return mapSize;
	}


	/**
	 * ビュー関連
	 */

	/**
	 * ビューの幅・高さを変更
	 */
	public void viewResize(float w, float h) {
		this.viewSize.x = w;
		this.viewSize.y = h;
	}

	/**
	 * ビュー視点の座標を渡す
	 */
	public Vector2 getMapPos() {
		float x = this.mapPos.x;
		float y = this.mapPos.y;
		return new Vector2(x, y);
	}

	/**
	 * ビューの左上座標を渡す
	 */
	public Vector2 getViewPos() {
		return this.viewPos;
	}

	/**
	 * プレイヤーと接触したマップオブジェクト識別子があるか
	 *
	 * @param mapId : マップオブジェクト識別子
	 * @return true : 指定オブジェクトと接触した
	 * 			fasle : 指定オブジェクトの該当なし
	 */
	public boolean isPlayerToMapObjectCollision(final int mapId) {
		int size = this.collMapIdList.size();
		for(int i = 0; i < size; i++) {
			final int id = this.collMapIdList.get(i);
			if(id == mapId) {
				return true;
			}
		}

		return false;
	}

}
