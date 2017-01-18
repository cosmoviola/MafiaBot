package game;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.entities.User;
import roles.*;

public class Game {
	
	public static enum State {JOINING, DAY, NIGHT};
	private State state;
	private HashMap<User, Player> players;
	private HashMap<String, User> names; //gets a user from the user's discriminator
	private int playerCount = 0;
	private TextChannel channel;
	private int cycle = 0;
	private ArrayList<Role> roles = new ArrayList<Role>(5); //add roles in order of decreasing priority
	private final int GAME_SIZE = 5;
	private ScheduledThreadPoolExecutor timerExecutor = new ScheduledThreadPoolExecutor(1);
	private ScheduledFuture currentTimer;
	private HashMap<User, User> votes = new HashMap<User, User>(); //first user is voter, second is voted for
	
	/**Initialize a game of c5*/
	public Game(TextChannel c){
		channel = c;
		state = State.JOINING;
		players = new HashMap<User, Player>(7);
		currentTimer = timerExecutor.schedule(new Runnable(){
			public @Override void run() {
				cancelSetup();
			}
		}, 60, TimeUnit.SECONDS);
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
				names.put(u.getDiscriminator(), u);
				players.put(u, new Player(u));
				playerCount++;
				postMessage(u.getName()+" has joined the game. "
							+(GAME_SIZE-playerCount)+" players still needed.");
			}
		}else{
			postMessage("This game is not accepting players at this time.");
		}
		if(playerCount==GAME_SIZE){
			postMessage("Enough players have joined the game. Game starting.");
			beginGame();
		}
	}
	
	/**Ends game if not enough people joined in time during the JOINING state.*/
	private void cancelSetup(){
		postMessage("Not enough people joined.");
		C5Bot.removeGame(channel);
	}
	
	/**Set up game and assign roles. Begin the first night.*/
	private void beginGame(){
		cancelTimer();
		//role assignment
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
		
		String message = "The game begins. The players are:";
		for(User u: players.keySet()){
			message+=" "+u.getDiscriminator();
		}
		postMessage(message+".");
		beginNight();
	}
	
	/**Begin a night.*/
	private void beginNight(){
		state=State.NIGHT;
		postMessage("It is now Night "+cycle+". The night ends in 30 seconds or when all actions are in.");
		currentTimer = timerExecutor.schedule(new Runnable(){
			public @Override void run() {
				endNight();
			}
		}, 30, TimeUnit.SECONDS);
	}
	
	/**End a night. Resolves all actions placed.*/
	private void endNight(){
		postMessage("The night has ended.");
		Iterator<Role> i = roles.iterator();
		while(i.hasNext()){
			i.next().doAction(this);
		}
		beginDay();
	}
	
	/**Begin the next day.*/
	private void beginDay(){
		cycle++;
		state=State.DAY;
		postMessage("It is now Day "+cycle+". "
				+ "Vote for a player to lynch by submitting !vote <user> in this channel. "
				+ "You have 30 seconds.");
		currentTimer = timerExecutor.schedule(new Runnable(){
			public @Override void run() {
				endDay();
			}
		}, 30, TimeUnit.SECONDS);
	}
	
	/**End the day. Resolves the lynch.*/
	private void endDay(){
		postMessage("The voting period has ended.");
		HashMap<User, Integer> tally = new HashMap<User, Integer>();
		for(User e:votes.values()){
			if(tally.containsKey(e)){
				tally.put(e, tally.get(e)+1);
			}else{
				tally.put(e, 1);
			}
		}
		int max = 0;
		User currentLynch = null;
		for(User e: tally.keySet()){
			int current = tally.get(e);
			if(max<current){
				max = current;
				currentLynch = e;
			}else if(max==current){
				currentLynch = null;
			}
		}
		if(currentLynch == null){
			postMessage("No one was lynched.");
		}else{
			postMessage(currentLynch.getDiscriminator()+" was lynched. "
					+ "He was a "+players.get(currentLynch).getRole().cardFlip()+".");
		}
		beginNight();
	}
	
	/**Places a vote by voter onto voted.*/
	private void placeVote(User voter, User voted){
		if(getState()==State.DAY){
			votes.put(voter, voted);
		}
	}
	
	/**Removes a vote placed by a user.*/
	private void removeVote(User voter){
		if(getState()==State.DAY){
			votes.remove(voter);
		}
	}
	
	/**Takes commands in the main text channel and executes them.*/
	public void executeCommand(String[] cmd, User author){
		switch(cmd[1]){
			case "join":
				addPlayer(author);
				break;
			case "vote":
				if(cmd.length<3){
					postMessage("Vote command must have a target.");
					break;
				}
				String target = cmd[2];
				if(names.containsKey(target)){
					placeVote(author, names.get(target));
				}else{
					placeVote(author, null);
				}
				break;
			case "unvote":
				removeVote(author);
				break;
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
	
	/**Attempts to cancel the current timer for a day or night. 
	 * If false is returned, the timer could not be stopped.*/
	public boolean cancelTimer(){
		return currentTimer.cancel(false);
	}
}
