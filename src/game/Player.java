package game;
import net.dv8tion.jda.core.entities.User;
import roles.Role;

public class Player {
	
	public enum Alignment {COP, WOLF};
	
	private User user;
	private Role role;
	private Alignment alignment;
	private boolean isAlive = true;
	
	public Player(User u){
		user = u;
	}
	
	public void setRole(Role r){
		role = r;
	}
	
	public void setAlignment(Alignment a){
		alignment = a;
	}
	
	public String getName(){
		return user.getName();
	}
	
	public boolean isAlive(){
		return isAlive;
	}
	
	public boolean isCop(){
		return alignment.equals(Alignment.COP);
	}
	
	public void privateMessage(String s){
		if(!user.hasPrivateChannel()){
			user.openPrivateChannel().queue();
		}
		user.getPrivateChannel().sendMessage(s).queue();
	}
}
