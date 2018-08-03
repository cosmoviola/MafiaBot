package game;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;

import alignments.Alignment;
import net.dv8tion.jda.core.entities.User;
import roles.Role;

/**A Player contains the information representing a person in a mafia game,
 * and is used to process the results of actions on each player.*/
public class Player {
	
	private User user;
	private Role role;
	private String identifier;
	private List<String> results = new LinkedList<String>();
	private Alignment alignment;
	private boolean isAlive = true;
	private boolean hooked = false;
	private boolean bodyguarded = false;
	
	/**Construct a Player for User u.*/
	public Player(User u){
		user = u;
		identifier = u.getName()+"#"+u.getDiscriminator();
	}
	
	/**Sets this Player's role to r.*/
	public void setRole(Role r){
		role = r;
	}
	
	/**Returns this Player's role.*/
	public Role getRole(){
		return role;
	}
	
	/**Returns the User object for the user this Player represents.*/
	public User getUser(){
		return user;
	}
	
	/**Returns the message this Player needs from their role.*/
	public String getNightMessage(Game g){
		return role.roleMessageForThisNight(g);
	}
	
	/**Sets the alignment of this Player to a.*/
	public void setAlignment(Alignment a){
		alignment = a;
	}
	
	/**Returns the Player's username#discriminator combination.*/
	public String getIdentifier(){
		return identifier;
	}
	
	/**Returns true iff this Player is currently alive.*/
	public boolean isAlive(){
		return isAlive;
	}
	
	/**Returns true iff this Player is currently hooked.*/
	public boolean isHooked(){
		return hooked;
	}
	
	/**Returns the alignment of this Player.*/
	public Alignment getAlignment(){
		return alignment;
	}
	
	/**Returns true iff this Player is aligned with the supplied Player.*/
	public boolean isAligned(Player p){
		return alignment.equals(p.getAlignment());
	}
	
	/**Sends the input string as a private message to this Player,
	 * and returns a future which is completed when the message is successfully sent.
	 */
	public CompletableFuture<Boolean> privateMessage(String s){
		if(s.equals("")){
			return CompletableFuture.completedFuture(true);
		}
		CompletableFuture<Boolean> future = new CompletableFuture<Boolean>();
		user.openPrivateChannel().queue(
			channel -> channel.sendMessage(s).queue(
				x -> future.complete(true),
				t -> future.completeExceptionally(new CompletionException(new Exception("Could not send message.")))),
			x -> future.completeExceptionally(new CompletionException(new Exception("Could not get private channel."))));
		return future;
	}
	
	/**Append the input results to this Player's list of results.*/
	public void appendResult(String s){
		results.add(s);
	}
	
	/**Clear the Player's list of results.*/
	public void resetResults(){
		results.clear();
	}
	
	/**Get a string of all the current results for this Player.*/
	public String getResultsMessage(){
		return String.join("\n", results);
	}
	
	/**Kill this Player.*/
	public void kill(){
		isAlive = false;
	}
	
	/**Hook this Player.*/
	public void hook(){
		hooked = true;
	}
	
	/**Prepare this Player for the next day, resetting all necessary fields.*/
	public void nightReset(){
		hooked = false;
		role.reset();
		resetResults();
	}

	/**Returns true iff this Player was bodyguarded this cycle.*/
	public boolean isBodyguarded() {
		return bodyguarded;
	}

	/**Bodyguard this player this cycle.*/
	public void bodyguard() {
		bodyguarded = true;
	}
}
