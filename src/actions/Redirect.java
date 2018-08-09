package actions;

import java.util.Collection;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;

import game.Game;
import game.Player;

public class Redirect extends Action {
	
	Optional<Player> from = Optional.empty();
	boolean fromSet = false;
	final String fromKeyword;
	Optional<Player> to = Optional.empty();
	boolean toSet = false;
	final String toKeyword;
	
	public Redirect(int p, Function<Game, Boolean> f){
		this(p, f, "from", "to");
	}
	
	public Redirect(int p, Function<Game, Boolean> f, String from, String to) {
		super(p, f);
		if(from.equals(to)){
			throw new IllegalArgumentException("Keyword for 'from' cannot be the same as keyword for 'to'.");
		}
		fromKeyword = from;
		toKeyword = to;
	}

	@Override
	public void setTarget(String key, Optional<Player> p) {
		if(key.equals(fromKeyword)){
			if(canTarget(key, actor, p)){
				from = p;
				fromSet = true;
			}else{
				throw new IllegalArgumentException("Provided argument is not a valid target.");
			}
		}else if(key.equals(toKeyword)){
			if(canTarget(key, actor, p)){
				to = p;
				toSet = true;
			}else{
				throw new IllegalArgumentException("Provided argument is not a valid target.");
			}
		}
	}

	@Override
	public boolean canTarget(String key, Player actor, Optional<Player> target) {
		if(key.equals(fromKeyword)||key.equals(toKeyword)){
			return !target.isPresent() || target.get().isAlive();
		}
		return false;
	}

	@Override
	public Set<String> getKeywords() {
		Set<String> set = new HashSet<String>();
		set.add(toKeyword);
		set.add(fromKeyword);
		return set;
	}

	@Override
	public void doAction(Game g) {
		if(g.getState().equals(Game.State.NIGHT) && actor.isAlive() && isActive(g) && from.isPresent() && to.isPresent()){
			if(actor.isHooked()){
				actor.appendResult("Your redirect action failed.");
			}else{
				Player target = from.get();
				if(target.isSafeguarded()){
					actor.appendResult("You tried to use your redirect action, but your target was protected.");
				}else{
					from.get().redirectTo(to.get());
				}
			}
		}
	}

	@Override
	public String actionMessageForThisNight(Game g) {
		if(!isActive(g)){
			return "";
		}
		String beginning = "Message me '"+fromKeyword+" <user>' to redirect from target user. Message me '"
		                   +toKeyword+" <user>' to redirect to target user. You must do both of these for this action to have an effect.";
		Collection<Player> validTargets = getValidTargets(fromKeyword, g);
		String targets = g.formValidTargetsString(validTargets);
		if(targets.equals("")){
			return beginning;
		}
		return beginning + " You may target: " + targets + ".";
	}

	private Collection<Player> getValidTargets(String key, Game g) {
		Collection<Player> targets = new HashSet<>();
		for(Player p : g.getPlayers()){
			if(canTarget(key, actor, Optional.of(p))){
				targets.add(p);
			}
		}
		return targets;
	}

	@Override
	public boolean allTargetsSet() {
		return toSet && fromSet;
	}

	@Override
	public void reset() {
		to = Optional.empty();
		toSet = false;
		from = Optional.empty();
		fromSet = false;
	}
}