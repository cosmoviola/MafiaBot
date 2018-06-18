package game;
import java.util.concurrent.CompletionStage;

import alignments.Alignment;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.requests.RequestFuture;
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
	
	public RequestFuture<Message> privateMessage(String s){
		return (RequestFuture<Message>) user.openPrivateChannel().submit().thenCompose(((channel) -> channel.sendMessage(s).submit()));
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
