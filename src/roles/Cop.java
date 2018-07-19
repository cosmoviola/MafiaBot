package roles;

import java.util.Collection;
import java.util.HashSet;

import game.Game;
import game.Player;

public abstract class Cop extends Role {

	public Cop() {
	
	}

	@Override
	public String roleMessage() {
		return "You are a cop. At night, message me check <user> to determine user's alignment. "
				+ "Be warned: you do not know your sanity."; 
	}
	
	@Override
	public String roleMessageForThisNight(Game g){
		Collection<Player> validTargets = getValidTargets(g);
		String targets = "";
		for(Player p : validTargets){
			targets += " " + g.getCurrentStoredNick(p) + " (ID: " + p.getIdentifier() + ")";
		}
		if(targets.equals("")){
			return "It is Night "+g.getCycle()+". You do not have any valid targets for your action.";
		}
		return "It is Night "+g.getCycle()+". Message me 'check <user>' to determine user's alignment. You may target:" + targets + ".";
	}
	
	@Override
	public boolean canTarget(Player p){
		return p.isAlive();
	}
	
	@Override
	public Collection<Player> getValidTargets(Game g){
		Collection<Player> players = g.getPlayers();
		g.getPlayers().removeIf((p-> !canTarget(p)));
		return players;
	}
	
	@Override
	public String winCondition() {
		return "You win when the wolf is dead.";
	}

	@Override
	public String cardFlip() {
		return "Cop";
	}

	@Override
	public HashSet<String> getCommands() {
		HashSet<String> s = new HashSet<String>();
		s.add("check");
		return s;
	}
}
