package game;
import net.dv8tion.jda.core.entities.User;
import roles.Role;

public class Player {
	
	public enum Alignment {COP, WOLF};
	
	private User user;
	private Role role;
	private Alignment alignment;
	
	public Player(User u){
		user = u;
	}
	
	public void setRole(Role r){
		role = r;
	}
	
	public void setAlignment(Alignment a){
		alignment = a;
	}
}
