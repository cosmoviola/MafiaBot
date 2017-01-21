package alignments;

import game.Game;
import game.Player;

public class Self extends Alignment {

	public Self(String n) {
		super(n);
	}

	@Override
	public boolean checkVictory(Game g) {
		for(Player e: g.getPlayers()){
			if(e.isAlive()||!e.getAlignment().equals(this)){
				return false;
			}
		}
		return true;
	}

}
