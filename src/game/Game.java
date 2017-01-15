package game;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.entities.User;
import roles.*;

public class Game {
	
	public static enum State {JOINING, DAY, NIGHT};
	private State state;
	private HashMap<User, Player> players;
	private TextChannel channel;
	private int cycle = 0;
	private ArrayList<Role> roles = new ArrayList<Role>(5); //add roles in order of decreasing priority
	private final int GAME_SIZE = 5;
	
	/**Initialize a game of c5*/
	public Game(TextChannel c){
		channel = c;
		state = State.JOINING;
		players = new HashMap<User, Player>(7);
	}
	
	/**Adds a User to this game if game is in the JOINING state and the User has not joined yet.
	 * When game is full, begins the game.
	 * @param u
	 */
	public void addPlayer(User u){
		if(state.equals(State.JOINING)){
			if(players.containsKey(u)){
				postMessage(u.getName()+" has already joined this game.");
			}else{
				players.put(u, new Player(u));
			}
		}else{
			postMessage("This game is not accepting players at this time.");
		}	
	}
	
	public void beginGame(){
		c5roles();
		ArrayList<Player> shufflePlayers = new ArrayList<Player>(players.values());
		Collections.shuffle(shufflePlayers);
		for(int i=0; i<GAME_SIZE; i++){
			Role r = roles.get(i);
			Player p = shufflePlayers.get(i);
			p.setRole(r);
			r.setActor(p);
			p.privateMessage(r.roleMessage());
			p.privateMessage(r.winCondition());
		}
	}
	
	/**Initialize game to use the roles in a c5 game.*/
	public void c5roles(){
		roles.add(new Wolf());
		roles.add(new SaneCop());
		roles.add(new InsaneCop());
		roles.add(new NaiveCop());
		roles.add(new ParanoidCop());
	}
	
	/**Send a message to the text channel this game is taking place in.*/
	public void postMessage(String s){
		channel.sendMessage(s).queue();
	}
	
	/**Returns the current state of the game.*/
	public State getState(){
		return state;
	}
	
	/**Returns the current cycle of the game.*/
	public int getCycle(){
		return cycle;
	}
}
