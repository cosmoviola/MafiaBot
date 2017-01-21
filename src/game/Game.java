package game;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import alignments.Alignment;
import alignments.Self;
import alignments.Village;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.entities.User;
import roles.*;

public class Game {
	
	public static enum State {JOINING, DAY, NIGHT};
	private State state;
	private HashMap<User, Player> players;
	private HashMap<String, User> names; //gets a user from the user's identifier
	private HashSet<Player> living;
	private int playerCount = 0;
	private TextChannel channel;
	private String id; //identifier of TextChannel game takes place in
	private int cycle = 0;
	private ArrayList<Role> roles = new ArrayList<Role>(5); //add roles in order of decreasing priority. These are where actions are executed.
	private ArrayList<RoleAlignmentPair> pairsToAssign = new ArrayList<RoleAlignmentPair>(); //these are to be assigned to players
	private final int GAME_SIZE = 5; //final because this is c5.
	private ScheduledThreadPoolExecutor timerExecutor = new ScheduledThreadPoolExecutor(1);
	private ScheduledFuture currentTimer;
	private HashMap<User, User> votes = new HashMap<User, User>(); //first user is voter, second is voted for
	
	/**Initialize a game of c5*/
	public Game(TextChannel c){
		channel = c;
		id = c.getId();
		state = State.JOINING;
		players = new HashMap<User, Player>(7);
		names = new HashMap<String, User>(7);
		currentTimer = timerExecutor.schedule(new Runnable(){
			public @Override void run() {
				cancelSetup();
			}
		}, 60, TimeUnit.SECONDS);
	}
	
	/**Adds a User to this game if game is in the JOINING state and the User has not joined yet.
	 * When game is full, begins the game.
	 */
	public void addPlayer(User u){
		if(state.equals(State.JOINING)){
			if(players.containsKey(u)){
				postMessage(u.getName()+" has already joined this game.");
			}else{
				Player p = new Player(u);
				names.put(p.getIdentifier(), u);
				players.put(u, p);
				p.openPrivateChannel();
				playerCount++;
				postMessage(u.getName()+" has joined the game. "
						+(GAME_SIZE-playerCount)+" players still needed.");
			}
			if(playerCount==GAME_SIZE){
				postMessage("Enough players have joined the game. Game starting.");
				beginGame();
			}
		}else{
			postMessage("This game is not accepting players at this time.");
		}
	}
	
	/**Removes the given user from the game if game is in the JOINING state and the User has joined.*/
	public void removePlayer(User u){
		if(state.equals(State.JOINING)){
			if(players.containsKey(u)){
				names.remove(players.get(u).getIdentifier());
				players.remove(u);
				playerCount--;
				postMessage(u.getName()+" has left the game. "
							+(GAME_SIZE-playerCount)+" players needed.");
			}
		}
	}
	
	/**Gets the Collection of players.*/
	public Collection<Player> getPlayers(){
		return players.values();
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
		living = new HashSet<Player>(players.values());
		c5roles();
		ArrayList<Player> shufflePlayers = new ArrayList<Player>(players.values());
		Collections.shuffle(shufflePlayers);
		for(int i=0; i<GAME_SIZE; i++){
			RoleAlignmentPair pair = pairsToAssign.get(i);
			Role r = pair.getRole();
			Alignment a = pair.getAlignment();
			Player p = shufflePlayers.get(i);
			p.setRole(r);
			p.setAlignment(a);
			r.setActor(p);
			a.addPlayer(p);
			p.privateMessage(r.roleMessage());
			p.privateMessage(r.winCondition());
		}
		
		String message = "The game begins. The players are:";
		for(Player p: players.values()){
			message+=" "+p.getIdentifier();
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
		i = roles.iterator();
		while(i.hasNext()){ //this is a separate loop in case a target is needed for another role.
			i.next().resetTarget();
		}
		Alignment a = checkVictory();
		if(a!=null){
			endGame(a);
		}else{
			beginDay();
		}
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
			Player p = players.get(currentLynch);
			postMessage(p.getIdentifier()+" was lynched. "
					+ "He was a "+p.getRole().cardFlip()+".");
		}
		votes.clear();
		Alignment a = checkVictory();
		if(a!=null){
			endGame(a);
		}else{
			beginNight();
		}
	}
	
	/**Ends the game with Alignment a the victors.*/
	private void endGame(Alignment a){
		cancelTimer();
		String message;
		Collection c = a.getMembers();
		Iterator<Player> i = c.iterator();
		if(c.size()==1){
			message = i.next().getIdentifier()+" (the "+a.getName()+") has won!";
		}else if(i.hasNext()==true){
			boolean next = true;
			message = "";
			while(next){
				Player e = i.next();
				if(i.hasNext()){
					message+=e.getIdentifier()+", ";
				}else{
					message+="and "+e.getIdentifier();
					next=false;
				}
			}
			message=" (the "+a.getName()+") have won!";
		}else{
			message = "No one wins.";
		}
		postMessage(message);
		C5Bot.removeGame(channel);
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
	
	/**Checks if one of the alignments has won. 
	 * If so, returns that alignment. Otherwise, returns null.
	 * This currently does not support multiple alignments winning.*/
	private Alignment checkVictory(){
		Collection<Alignment> alignments = Alignment.getAllAlignments();
		for(Alignment e: alignments){
			if(e.checkVictory(this)){
				return e;
			}
		}
		return null;
	}
	
	/**Kills player p.*/
	public void killPlayer(Player p){
		living.remove(p);
		p.kill();
	}
	
	/**Takes commands in the main text channel and executes them.*/
	public void executeChannelCommand(String[] cmd, User author){
		switch(cmd[1]){
			case "join":
				addPlayer(author);
				break;
			case "leave":
				removePlayer(author);
				break;
			case "vote":
				if(cmd.length<3){
					postMessage("Vote command must have a target.");
					break;
				}
				String target = cmd[2];
				if(names.containsKey(target)&&living.contains(author)){
					placeVote(author, names.get(target));
				}else{
					placeVote(author, null);
				}
				break;
			case "unvote":
				removeVote(author);
				break;
			default:
				postMessage("Unrecognized command.");
				break;
		}	
	}
	
	/**Executes the command contained in cmd. 
	 * In a later implementation, commands will fetch an Action object, which will determine
	 * what action is done. For now, all commands have the same effect.
	 */
	public void executePrivateCommand(String[] cmd, User author){
		Player executor = players.get(author);
		Role executorRole = executor.getRole();
		if(executorRole.getCommands().contains(cmd[1])){
			if(names.containsKey(cmd[2])){
				executorRole.setTarget(players.get(names.get(cmd[2])));
			}else{
				executorRole.setTarget(null);
			}
		}else{
			executor.privateMessage("That is not a valid command.");
		}
	}
	
	/**Initialize game to use the roles in a c5 game.*/
	public void c5roles(){
		roles.add(new Wolf(id));
		roles.add(new SaneCop(id));
		roles.add(new InsaneCop(id));
		roles.add(new NaiveCop(id));
		roles.add(new ParanoidCop(id));
		pairsToAssign.add(new RoleAlignmentPair(roles.get(0), new Self("wolf")));
		pairsToAssign.add(new RoleAlignmentPair(roles.get(1), new Village("cops")));
		pairsToAssign.add(new RoleAlignmentPair(roles.get(2), new Village("cops")));
		pairsToAssign.add(new RoleAlignmentPair(roles.get(3), new Village("cops")));
		pairsToAssign.add(new RoleAlignmentPair(roles.get(4), new Village("cops")));
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
	
	private class RoleAlignmentPair{
		
		Role role;
		Alignment alignment;
		
		public RoleAlignmentPair(Role r, Alignment a){
			role = r;
			alignment = a;
		}
		
		public Role getRole(){
			return role;
		}
		
		public Alignment getAlignment(){
			return alignment;
		}
	}
}
