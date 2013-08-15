package jp.trident.game.rpg;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

import android.app.Activity;
import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.BitmapFactory;
import android.util.Log;
import jp.trident.game.fw.Collision;
import jp.trident.game.fw.SceneManager;
import jp.trident.game.rpg.StageSelectScene.QuestData;

/**
 * ゲームユーティリティー　シングルトン化
 *
 * @author wa-rudo
 *
 */
public class GameUtility {

	private final String TAG = "GameUtility";

	/**
	 * インスタンス
	 */
	static private GameUtility instance = null;

	/**
	 * 乱数オブジェクト
	 */
	public Random r = null;

	/**
	 * コンテキスト
	 */
	public Context context = null;

	/** Nexus7上での画像切り出し表示時のずれを直す */
	public BitmapFactory.Options bitmapfOption = null;

	/**
	 * シーンマネージャ
	 */
	public SceneManager sceneManager = null;

	/**
	 * コリジョンクラス
	 */
	public Collision collision = null;



	/**
	 * 一度だけ呼ばれる
	 */
	static {
		instance = new GameUtility();
	}


	/**
	 * コンストラクタ
	 */
	private GameUtility() {
	}

	/**
	 * インスタンスを取得
	 */
	static public GameUtility getInstance() {
		return instance;
	}

	/**
	 * 0～maxの中からランダムな整数を得る。
	 *
	 * @param max
	 * @return	乱数値
	 */
	public int getRandom(int max) {
		return r.nextInt(max);
	}

	/**
	 * assetsフォルダのファイルを読み込む
	 *
	 * @param sFilepath	ファイルパス
	 */
	public String readFileToAssets(String sFilepath) {
		String sData = "";

		// assets用アクセス
		Activity activity = (Activity)this.context;
		AssetManager assets = activity.getAssets();

		try {
			InputStream in = assets.open(sFilepath);
			BufferedReader br = new BufferedReader( new InputStreamReader(in) );
			String sLine;
			// １行ずつ文字列を取得していく
			while( (sLine = br.readLine()) != null ) {
				sData += (sLine + "\n");
			}

			br.close();
			in.close();
		}
		catch(Exception e) {
			Log.v(TAG, "ファイルを読み込みできませんでした。" + sFilepath);
		}

		return sData;
	}

	/**
	* ファイル書き込み処理（String文字列⇒ファイル）
	* ファイル権限が可能な場所ならどこでも大丈夫
	* ／data／data／パッケージ名／files／ファイル名
	* ／sdcard／ディレクトリ／ファイル名
	*
	* @param sFilepath　書き込みファイルパス
	* @param sOutdata　ファイル出力するデータ
	* @param sEnctype　文字エンコード
	*/
	public void writeFile(String sFilepath, String sOutdata, String sEnctype){

		BufferedWriter bufferedWriterObj = null;
		try {
			//ファイル出力ストリームの作成
			FileOutputStream fos = new FileOutputStream(sFilepath, true);
			OutputStreamWriter osw = new OutputStreamWriter(fos, sEnctype);
			bufferedWriterObj = new BufferedWriter(osw);

			bufferedWriterObj.write(sOutdata);
			bufferedWriterObj.flush();
		}
		catch (Exception e) {
			Log.v("File.writeFile", e.getMessage());
		}
		finally {
			try {
				if(bufferedWriterObj != null) { bufferedWriterObj.close(); }
			}
			catch (IOException e2) {
				Log.v("File.writeFile", e2.getMessage());
			}
		}
	}

	/**
	* ファイル読み込み処理（ファイル⇒String文字列）
	* @param sFilepath　書き込みファイルパス
	* @param sEnctype　文字エンコード
	* @return　読み込みだファイルデータ文字列
	*/
	public String readFile(String sFilepath, String sEnctype) {

		String sData = "";
		BufferedReader bufferedReaderObj = null;

		try {
			//入力ストリームの作成
			FileInputStream fis = new FileInputStream(sFilepath);
			InputStreamReader isr = new InputStreamReader(fis, sEnctype);
			bufferedReaderObj = new BufferedReader(isr);

			String sLine;
			while ( (sLine = bufferedReaderObj.readLine() ) != null) {
				sData += (sLine + "\n");
			}
		}
		catch (Exception e) {
			Log.v("File.readFile", e.getMessage());
		}
		finally{
			try {
				if(bufferedReaderObj != null) bufferedReaderObj.close();
			}
			catch (IOException e2) {
				Log.v("File.readFile", e2.getMessage());
			}
		}

		return sData;
	}
}
