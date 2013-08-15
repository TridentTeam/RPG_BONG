package jp.trident.game.rpg;

import android.graphics.Color;
import jp.trident.game.fw.GameSurfaceView;

public class MapPlayer extends Player {

	/**
	 * コンストラクタ
	 */
	public MapPlayer() {
		super();
	}

	/**
	 * ステータスの描画をする。
	 */
	public void statusDraw(GameSurfaceView sv) {

		int w = 20;
		int x = 0;
		int y = 20;

		y += w;
		sv.DrawText("MapPlayer デバッグ表示", x, y, Color.BLACK);
		y += w;
		sv.DrawText(" NAME " + super.name, x, y, Color.BLACK);
		y += w;
		sv.DrawText(" LEVEL " + super.level, x, y, Color.BLACK);
		y += w;
		sv.DrawText(" 現在HP " + super.currentHp, x, y, Color.BLACK);
		y += w;
		sv.DrawText(" 最大HP " + super.maxHp, x, y, Color.BLACK);
		y += w;
		sv.DrawText(" スタミナ " + super.stamina, x, y, Color.BLACK);
		y += w;
		sv.DrawText(" 所持ゴールド " + super.gold, x, y, Color.BLACK);
		y += w;
		sv.DrawText(" 攻撃力 " + super.attack, x, y, Color.BLACK);
		y += w;
		sv.DrawText(" 防御力 " + super.defense, x, y, Color.BLACK);
		y += w;
		sv.DrawText(" 装備中の武器id " + super.weapon, x, y, Color.BLACK);
		y += w;
		sv.DrawText(" 装備中の防具id " + super.armor, x, y, Color.BLACK);
		y += w;
		sv.DrawText(" 次レベルまでの経験値 " + super.nextExp, x, y, Color.BLACK);
		y += w;
		sv.DrawText(" 合計の経験値 " + super.totalExp, x, y, Color.BLACK);
		y += w;
		sv.DrawText(" " + ( (super.modeFlag == true) ? "走っている" : "歩いている" ) , x, y, Color.BLACK);
		y += w;
		sv.DrawText(" " + ( (super.alive == true)    ? "生きている" : "死んでいる" ) , x, y, Color.BLACK);
		y += w;

		// 位置座標
		sv.DrawText(" マップ位置座標 " + " x " + super.m_x + " y " + super.m_y, x, y, Color.BLACK);
		y += w;

	}

}
