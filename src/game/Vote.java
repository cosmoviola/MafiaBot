package game;

import net.dv8tion.jda.core.entities.User;

/**Represents a player's vote in the lynch. 
 * Either no vote is set, a vote is for a certain User, or the vote is for no lynch.*/
public class Vote {
	private User vote = null;
	private boolean voteSet = false;
	
	public Vote(){
		vote = null;
		voteSet = false;
	}
	
	public Vote(User u){
		vote = u;
		voteSet = true;
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
}
