package game;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;

import alignments.Alignment;
import net.dv8tion.jda.core.entities.User;
import roles.Role;

public class Player {
	
	private User user;
	private Role role;
	private String identifier;
	private Alignment alignment;
	private boolean isAlive = true;
	private boolean hooked = false;
	
	public Player(User u){
		user = u;
		identifier = u.getName()+"#"+u.getDiscriminator();
	}
	
	public void setRole(Role r){
		role = r;
	}
	
	public Role getRole(){
		return role;
	}
	
	public User getUser(){
		return user;
	}
	
	public String getNightMessage(Game g){
		return role.roleMessageForThisNight(g);
	}
	
	public void setAlignment(Alignment a){
		alignment = a;
	}
	
	public String getIdentifier(){
		return identifier;
	}
	
	public boolean isAlive(){
		return isAlive;
	}
	
	public boolean isHooked(){
		return hooked;
	}
	
	public Alignment getAlignment(){
		return alignment;
	}
	
	public boolean isCop(){
		return alignment.equals(Alignment.getAlignment("cops"));
	}
	
	public CompletableFuture<Boolean> privateMessage(String s){
		CompletableFuture<Boolean> future = new CompletableFuture<Boolean>();
		user.openPrivateChannel().queue(
			channel -> channel.sendMessage(s).queue(
				x -> future.complete(true),
				t -> future.completeExceptionally(new CompletionException(new Exception("Could not send message.")))),
			x -> future.completeExceptionally(new CompletionException(new Exception("Could not get private channel."))));
		return future;
	}
	
	public void kill(){
		isAlive = false;
	}
	
	public void hook(){
		hooked = true;
	}
	
	public void nightReset(){
		hooked = false;
	}
}
