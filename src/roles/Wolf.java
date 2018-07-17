package roles;

import java.util.Collection;
import java.util.HashSet;

import game.Game;
import game.Player;

public class Wolf extends Role {

	public Wolf() {
		
	}

	@Override
	public void doAction(Game g) {
		if(g.getState().equals(Game.State.NIGHT)&&actor.isAlive()){
			if(actor.isHooked()){
				actor.privateMessage("Your action failed as you were hooked.");
			}else if(target!=null){
				if(g.getCycle()==0){
					target.hook();
				}else{
					g.killPlayer(target);
					g.postMessage(target.getIdentifier()+" has been killed. "
							+ "They were a "+target.getRole().cardFlip()+".");
				}
			}
		}
	}

	@Override
	public String roleMessage() {
		return "You are the wolf. At night on cycle 0, message me 'hook <user>' to hook user. "
				+ "On following nights, message me 'kill <user>' to kill user.";
	}
	
	@Override
	public String roleMessageForThisNight(Game g){
		if(g.getCycle() == 0){
			return "It is Night 0. Message me 'hook <user>' to hook target user.";
		}else{
			return "It is Night "+g.getCycle()+". Message me 'kill <user>' to kill target user.";
		}
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
		return "You win when you are the last player standing.";
	}
	
	@Override
	public String cardFlip() {
		return "Wolf";
	}

	@Override
	public HashSet<String> getCommands() {
		HashSet<String> s = new HashSet<String>();
		s.add("hook");
		s.add("kill");
		return s;
	}
}
