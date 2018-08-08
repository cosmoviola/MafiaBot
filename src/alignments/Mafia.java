package alignments;

import java.util.Collection;

import actions.Kill;
import game.Game;
import game.Player;

public class Mafia extends Alignment {
	
	String keyword;

	public Mafia(String n, int priority) {
		super(n);
		Kill kill = new Kill(priority, p -> true);
		for(String key : kill.getKeywords()){
			keyword = key;
			actions.put(key, kill);
		}
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
		return "You win when all threats to your faction are eliminated.";
	}

	@Override
	public String alignmentString(Game g) {
		StringBuilder sb = new StringBuilder("You are aligned with " + this.getName() + ". Your team members are: ");
		for(Player p : this.getMembers()){
			sb.append(g.getCurrentStoredNick(p) + " (ID: " + p.getIdentifier() + "), ");
		}
		sb.delete(sb.length()-2, sb.length());
		sb.append(".\nEvery night, one person on your team may kill one player.");
		return sb.toString();
	}

	@Override
	public String alignmentMessageForThisNight(Game g) {
		String beginning = "One person on your team may message me 'team " + keyword + " <user>' to kill target user.";
		Collection<Player> validTargets = ((Kill) actions.get(keyword)).getValidTargets(g);
		String targets = g.formValidTargetsString(validTargets);
		if(targets.equals("")){
			return beginning;
		}
		return beginning + " You may target: " + targets + ".";
	}

}
