package actions;

import java.util.Collection;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
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
	public void setTarget(String key, Optional<Player> p){
		if(key.equals(keyword)){
			if(canTarget(key, actor, p)){
				target = p;
				targetSet = true;
			}else{
				throw new IllegalArgumentException("Provided argument is not a valid target.");
			}
		}
	}
	
	@Override
	public Set<String> getKeywords(){
		Set<String> keywords = new HashSet<String>();
		keywords.add(keyword);
		return keywords;
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
			if(canTarget(keyword, actor, Optional.of(p))){
				targets.add(p);
			}
		}
		return targets;
	}
	
	public abstract boolean canTarget(String key, Player actor, Optional<Player> target);
}