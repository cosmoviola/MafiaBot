package alignments;

import game.Game;
import game.Player;

public class Village extends Alignment {

	public Village(String n){
		super(n);
	}

	@Override
	public boolean checkVictory(Game g) {
		for(Player e: g.getPlayers()){
			if(e.isAlive()&&!e.getAlignment().equals(this)){
				return false;
			}
		}
		return true;
	}
	
	@Override
	public String winCondition() {
		return "You win when all threats to your faction are eliminated.";
	}
}
