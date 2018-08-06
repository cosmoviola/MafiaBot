package alignments;

import java.util.Optional;
import game.Game;
import game.Player;

public class Self extends Alignment {

	public Self(String n) {
		super(n);
	}

	@Override
	public boolean checkVictory(Game g) {
		for(Player e: g.getPlayers()){
			if(e.isAlive()&&!e.getAlignment().equals(this)){
				return false;
			}
		}
		for(Player e: this.members){
			if(e.isAlive()){
				return true;
			}
		}
		return false;
	}
	
	@Override
	public String winCondition() {
		return "You win when you are the last player standing.";
	}

	@Override
	public void setTarget(String key, Player actor, Optional<Player> target) {
		throw new RuntimeException("This alignment does not have any actions.");
	}

}
