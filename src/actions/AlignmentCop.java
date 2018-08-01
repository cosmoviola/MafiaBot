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
		System.out.println("Enter doAction cop");
		if(g.getState().equals(Game.State.NIGHT) && actor.isAlive() && isActive(g)){
			System.out.println("Can perform cop");
			if(actor.isHooked()){
				System.out.println("Hooked cop");
				actor.appendResult("Your action failed as you were hooked.");
			}else target.ifPresent(t -> {
				switch(sanity){
				case SANE:
					System.out.println("Checking cop");
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
		System.out.println(targets);
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
