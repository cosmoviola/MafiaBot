package actions;

import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;

import game.Game;
import game.Player;

public class Kill extends Action {
	
	private Optional<Player> target = Optional.empty();
	private final String keyword;
	private boolean targetSet = false;
	
	public Kill(int p, Function<Game, Boolean> f){
		this(p, f, "kill");
	}
	
	public Kill(int p, Function<Game, Boolean> f, String s){
		super(p, f);
		keyword = s;
	}

	@Override
	public void doAction(Game g) {
		if(g.getState().equals(Game.State.NIGHT) && actor.isAlive() && isActive(g)){
			if(actor.isHooked()){
				actor.appendResult("Your action failed as you were hooked.");
			}else target.ifPresent(t-> {
				g.killPlayer(t);
				g.appendChannelResult(g.getCurrentStoredNick(t) + " (ID: " + t.getIdentifier() + ")" + " has been killed. "
							+ "They were a "+t.getRole().cardFlip()+".");
			});
		}
	}
	
	@Override
	public String actionMessageForThisNight(Game g) {
		if(!isActive(g)){
			return "";
		}
		String beginning = "Message me '"+keyword+"' <user>' to kill target user.";
		Collection<Player> validTargets = getValidTargets(g);
		String targets = g.formValidTargetsString(validTargets);
		if(targets.equals("")){
			return beginning;
		}
		return beginning + " You may target: " + targets + ".";
	}
	
	/**Returns a Collection of all the Players which are valid targets of this action.*/
	public Collection<Player> getValidTargets(Game g){
		Collection<Player> targets = new HashSet<>();
		for(Player p : g.getPlayers()){
			if(canTarget(p)){
				targets.add(p);
			}
		}
		return targets;
	}
	
	/**Returns true iff this role can target the supplied Player.*/
	public boolean canTarget(Player p){
		return p.isAlive();
	}

	@Override
	public void addKeywordMappings(Map<String, Consumer<Optional<Player>>> map) {
		if(map.containsKey(keyword)){
			throw new IllegalArgumentException(keyword + " is already a keyword of this map.");
		}
		map.put(keyword, p -> {
			target = p;
			targetSet = true;
		});
	}
	
	@Override
	public void addKeywordActiveMappings(Map<String, Function<Game, Boolean>> map) {
		if(map.containsKey(keyword)){
			throw new IllegalArgumentException(keyword + " is already a keyword of this map.");
		}
		map.put(keyword, isActive);
	}


	@Override
	public boolean allTargetsSet() {
		return targetSet;
	}

	@Override
	public void reset() {
		target = Optional.empty();
		targetSet = false;
	}

	@Override
	public void setActor(Player p) {
		actor = p;
	}
}
