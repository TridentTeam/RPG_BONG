package jp.trident.game.rpg;


import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;

import jp.trident.game.fw.DrawObject;
import jp.trident.game.fw.GameSurfaceView;
import jp.trident.game.fw.IScene;
import jp.trident.game.fw.R;
import jp.trident.game.fw.VirtualController;

/**
 * ステージセレクトシーン
 * 
 * @author wa-rudo
 *
 */
public class StageSelectScene implements IScene {
	
	/**
	 * ステージ定数
	 */
	private static final String STAGE_DIR = "stageData/";
	private static final String STAGE_FILE_NAME = "quest.txt";
	
	/**
	 * クエストデータ
	 * 
	 * @author wa-rudo
	 *
	 */
	public class QuestData {
		public int number = -1;				// クエスト番号
		public String name = "";			// クエスト名
		public int targetClearTime = 100;	// 目標クリア時間
		public int clearTime = -1;			// クリア時間
	}
	
	/**
	 * クエストリスト
	 */
	private ArrayList<QuestData> questList = null;
	
	/**
	 * クエストが全部クリアされたか
	 */
	private boolean questAllClearFlag = false;
	
	/**
	 * 現在のステージ
	 */
	private int stageNumber = 0;
	
	/**
	 * 現在のクエスト
	 */
	private int questNumber = 0;
	
	/**
	 * 背景
	 */
	private DrawObject bg = null;
	
	
	
	/**
	 * 一度だけ呼ばれる
	 */
	static {
		
		// 全ステージのクエストファイルを読み込み、アプリケーション内のファイルにコピーする（書き込みする）
		Log.v("stageSelectScene", "ファイルを作成する。");
		GameUtility gu = GameUtility.getInstance();
		if(gu != null) {
			
			// dataディレクトリを作成するか
			File dataDir = new File("data/data");
			
			if(dataDir.exists() == false) {
				dataDir.mkdir();
			}
			
			File fileDir = new File("data/data/" + gu.context.getPackageName() + "/files");
			
			if(fileDir.exists() == false) {
				fileDir.mkdir();
			}
			
			final String appFileDir = "data/data/" + gu.context.getPackageName() + "/files/";
			final String appFileName = "quest.txt";
			for(int i = 0; i < 1; i++) {// ステージ数
				for(int j = 0; j < 1; j++) {// クエスト数
					// ファイルのパスを設定
					String readFilepath = STAGE_DIR + i + "/" + j + "/" + STAGE_FILE_NAME;
					String writeFilepath = appFileDir + i + j + appFileName;
					
					// dataディレクトリにファイルを作成する。
//					try {
//						FileOutputStream fileOutputStream = gu.context.openFileOutput(i + appFileName, Context.MODE_PRIVATE);
//						fileOutputStream.close();
//					} catch(Exception e) {}
					
					// ステージクエストのファイルをコピーする
					String sData = gu.readFileToAssets(readFilepath);
					gu.writeFile(writeFilepath, sData, "UTF-8");
				}
			}
		}
	}
	
	/**
	 * コンストラクタ
	 */
	public StageSelectScene() {
		
		// ゲームユーティリティーを取得
		GameUtility gu = GameUtility.getInstance();
		
		// クエストリストを作成
		this.questList = new ArrayList<QuestData>();
		// 最初は、クエストをクリアにする
		this.questAllClearFlag = true;
		
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
		// 再読み込み
		if(this.questAllClearFlag == true) {
			this.questFileLoad();
			this.questAllClearFlag = false;
		}
		
		// 背景の初期化
		this.bg.posX = 0;
		this.bg.posY = 0;
	}
	
	/**
	 * クエストファイルの読み込み
	 */
	private void questFileLoad() {
		GameUtility gu = GameUtility.getInstance();
		
		// クエストリストのクリア
		this.questList.clear();
		
		final String packageName = gu.context.getPackageName();
		final String filepath = "data/data/" + packageName + "/files/" + this.stageNumber + this.questNumber + STAGE_FILE_NAME;
		String sData = gu.readFile(filepath, "UTF-8");
		String[] sLines = sData.split("\n");// 改行ずつ文字列を取得する
		String[] size = sLines[0].split(",");// クエスト数と、クエストサイズを取得
		final int questNum = Integer.parseInt(size[0]);
		final int questSize = Integer.parseInt(size[1]) + 1;
		for(int i = 0; i < questNum; i++) {
			QuestData qd = new QuestData();
			String[] sNumber = sLines[questSize * i + 1].split(",");
			String[] sName   = sLines[questSize * i + 2].split(",");
			String[] stcTime = sLines[questSize * i + 3].split(",");
			String[] scTime  = sLines[questSize * i + 4].split(",");
			qd.number 			= Integer.parseInt(sNumber[1]);
			qd.name 			= sName[1];
			qd.targetClearTime	= Integer.parseInt(stcTime[1]);
			qd.clearTime 		= Integer.parseInt(scTime[1]);
			
			this.questList.add(qd);
		}	
	}

	/**
	 * 更新
	 */
	public void update() {
		
		if(VirtualController.isTouchTrigger(0) == true) {
			GameUtility gu = GameUtility.getInstance();
			IScene scene = new MapScene(this.stageNumber, this.questNumber);
			gu.sceneManager.scenePush(scene);
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
		
		// クエストを表示
		int w = 20;
		int x = 0;
		int y = 50;
		int color = Color.GRAY;
		for(int i = 0; i < this.questList.size(); i++) {
			QuestData qd = this.questList.get(i);
			sv.DrawText(" number " + qd.number, x, y, color);
			x += 100;
			sv.DrawText(" name " + qd.name, x, y, color);
			x += 150;
			sv.DrawText(" tClearTime " + qd.targetClearTime, x, y, color);
			x += 150;
			sv.DrawText(" clearTime " + qd.clearTime, x, y, color);
			x = 0;
			y += w;
		}
	}
	
	/**
	 * クリアしたときのクエストのデータ更新を行う
	 */
	public void clearQuestDataUpdate(final int clearTime) {
		QuestData qd = this.questList.get(this.questNumber);
		qd.clearTime = clearTime;
		
		this.saveQuestData();
	}
	
	/**
	 * セーブを行う
	 */
	public void saveQuestData() {
		GameUtility gu = GameUtility.getInstance();
		final int questSize = this.questList.size();
		String sData = "";
		sData += (questSize + "," + 4 + "\n");
		
		for(int i = 0; i < questSize; i++) {
			QuestData qd = this.questList.get(i);
			sData += "questNumber," + qd.number + "\n";
			sData += "questName," + qd.name + "\n";
			sData += "questTargetClearTime," + qd.targetClearTime + "\n";
			sData += "questClearTime," + qd.clearTime + "\n";
			sData += "\n";
		}
		
		final String filename = "" + this.stageNumber + this.questNumber + STAGE_FILE_NAME;
		final String packageName = gu.context.getPackageName();
		final String filepath = "data/data/" + packageName + "/files/" + filename;
		
		// ファイルを削除する
		gu.context.deleteFile(filename);
		// ファイルを書き込む
		gu.writeFile(filepath, sData, "UTF-8");
	}
}
