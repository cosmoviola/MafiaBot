package actions;

import java.util.Collection;
import java.util.function.Function;

import game.Game;
import game.Player;

public class AlignmentCop extends SingleTargetableKeywordAction {
	
	public static enum Sanity {SANE, INSANE, NAIVE, PARANOID};
	private Sanity sanity;

	public AlignmentCop(int p, Function<Game, Boolean> f){
		this(p, f, "check", Sanity.SANE);
	}
	
	public AlignmentCop(int p, Function<Game, Boolean> f, Sanity san){
		this(p, f, "check", san);
	}
	
	public AlignmentCop(int p, Function<Game, Boolean> f, String str){
		this(p, f, str, Sanity.SANE);
	}
	
	public AlignmentCop(int p, Function<Game, Boolean> f, String str, Sanity san){
		super(p, f, str);
		sanity = san;
	}

	@Override
	public void doAction(Game g){
		String friendlyResult = " is aligned with you.";
		String hostileResult = " is hostile to you.";
		if(g.getState().equals(Game.State.NIGHT) && actor.isAlive() && isActive(g)){
			if(actor.isHooked()){
				actor.appendResult("Your cop action failed.");
			}else target.ifPresent(t -> {
				switch(sanity){
				case SANE:
					if(actor.isAligned(t)){
						actor.appendResult(t.getIdentifier()+friendlyResult);
					}else{
						actor.appendResult(t.getIdentifier()+hostileResult);
					}
					break;
				case INSANE:
					if(actor.isAligned(t)){
						actor.appendResult(t.getIdentifier()+hostileResult);
					}else{
						actor.appendResult(t.getIdentifier()+friendlyResult);
					}
					break;
				case NAIVE:
					actor.appendResult(t.getIdentifier()+friendlyResult);
					break;
				case PARANOID:
					actor.appendResult(t.getIdentifier()+hostileResult);
					break;
				}
			});
		}
	}

	@Override
	public String actionMessageForThisNight(Game g){
		Collection<Player> validTargets = getValidTargets(g);
		String targets = g.formValidTargetsString(validTargets);
		if(targets.equals("")){
			return "You do not have any valid targets for your action.";
		}
		return "Message me 'check <user>' to determine user's alignment. You may target: " + targets + ".";
	}
	
	/**Returns true iff this role can target the supplied Player.*/
	public boolean canTarget(Player p){
		return p.isAlive();
	}
}
