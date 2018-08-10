package game;

import net.dv8tion.jda.core.entities.User;

/**Represents a player's vote in the lynch. 
 * Either no vote is set, a vote is for a certain User, or the vote is for no lynch.*/
public class Vote {
	private User vote = null;
	private boolean voteSet = false;
	private final Player voter;
	
	public Vote(Player p){
		vote = null;
		voteSet = false;
		voter = p;
	}
	
	public Vote(Player p, User u){
		vote = u;
		voteSet = true;
		voter = p;
	}
	
	public void setVote(User u){
		vote = u;
		voteSet = true;
	}
	
	public void setNoLynch(){
		vote = null;
		voteSet = true;
	}
	
	public void unsetVote(){
		vote = null;
		voteSet = false;
	}
	
	public User getVote(){
		return vote;
	}
	
	public boolean isVoteSet(){
		return voteSet;
	}
	
	public int getVoteStrength(){
		return voter.getVoteStrength();
	}
}
