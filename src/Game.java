import java.util.HashMap;

import net.dv8tion.jda.core.entities.User;

public class Game {
	
	private static enum States {JOINING, DAY, NIGHT};
	private States state;
	private HashMap<User, Player> players;
	
	/**Initialize a game of c5*/
	public Game(){
		state = States.JOINING;
		players = new HashMap<User, Player>(7);
	}
}
