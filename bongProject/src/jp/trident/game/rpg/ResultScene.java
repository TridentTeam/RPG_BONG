package jp.trident.game.rpg;

import jp.trident.game.fw.DrawObject;
import jp.trident.game.fw.GameSurfaceView;
import jp.trident.game.fw.IScene;
import jp.trident.game.fw.R;
import jp.trident.game.fw.VirtualController;

/**
 * 結果表示シーン
 * 
 * @author wa-rudo
 *
 */
public class ResultScene implements IScene {
	
	/**
	 * 結果クラス
	 */
	public class Result {
		public int stageNumber = 0;			// ステージ番号
		public int questNumber = 0;			// クエスト番号
		public Player player = null;		// プレイヤーデータ
		public int remainingTime = -1;		// 残り時間
		public boolean gameClear = false;	// クリアしたか
	}
	
	/**
	 * 背景
	 */
	private DrawObject bg = null;
	
	/**
	 * ゲーム結果
	 */
	private Result result = null;
	
	
	
	/**
	 * コンストラクタ
	 * 
	 * @param stageNumber		ステージ数
	 * @param player			プレイヤー
	 * @param remainingTime		制限時間
	 * @param gameClear			ゲームクリア判定
	 */
	public ResultScene(final int stageNumber, final int questNumber, Player player, final int remainingTime, final boolean gameClear) {
		
		// ゲームユーティリティーを取得
		GameUtility gu = GameUtility.getInstance();
		
		// 結果を作成
		this.result = new Result();
		this.result.stageNumber = stageNumber;
		this.result.questNumber = questNumber;
		this.result.player = player;
		this.result.remainingTime = remainingTime;
		this.result.gameClear = gameClear;
		
		// 背景を作成
		this.bg = new DrawObject(800, 480);
		this.bg.imgLoad(gu.context, R.drawable.bg, gu.bitmapfOption, 800, 480);
		
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
		
		// 背景の初期化
		this.bg.posX = 0;
		this.bg.posY = 0;

	}

	/**
	 * 更新
	 */
	public void update() {
		
		if(VirtualController.isTouchTrigger(0) == true) {
			GameUtility gu = GameUtility.getInstance();
			gu.sceneManager.scenePop();
			if(this.result.gameClear == true) {
				// ステージセレクトシーンに、結果を登録する
				StageSelectScene stageSelectScene = (StageSelectScene)gu.sceneManager.getPeekScene();
				this.result.remainingTime = -2;
				stageSelectScene.clearQuestDataUpdate(this.result.remainingTime);
			}
			return;
		}

	}

	/**
	 * 描画
	 */
	public void draw(GameSurfaceView sv) {
		
		// 背景を描画
		sv.ScaleDrawImage(this.bg.img,
				this.bg.posX, this.bg.posY,
				this.bg.ani.sx, this.bg.ani.sy, this.bg.ani.sw, this.bg.ani.sh,
				this.bg.scaleX, this.bg.scaleY, false);
				
	}

}
