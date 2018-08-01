package actions;

import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;

import game.Game;
import game.Player;

public abstract class SingleTargetableKeywordAction extends Action {
	
	protected Optional<Player> target = Optional.empty();
	protected final String keyword;
	private boolean targetSet = false;
	
	public SingleTargetableKeywordAction(int p, Function<Game, Boolean> f, String s) {
		super(p, f);
		keyword = s;
	}

	@Override
	public void addKeywordMappings(Map<String, Consumer<Optional<Player>>> map){
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
	
	public abstract boolean canTarget(Player p);
}