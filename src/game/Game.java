package game;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import alignments.Alignment;
import alignments.Self;
import alignments.Village;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.entities.User;
import roles.*;

public class Game {
	
	public static enum State {JOINING, DAY, NIGHT};
	private State state;
	private HashSet<Member> members = new HashSet<Member>(7);
	private HashMap<User, Player> players;
	private HashMap<String, User> names; //gets a user from the user's identifier
	private HashSet<Player> living;
	private int playerCount = 0;
	private TextChannel channel;
	private int cycle = 0;
	private ArrayList<Role> roles = new ArrayList<Role>(5); //add roles in order of decreasing priority. These are where actions are executed.
	private ArrayList<RoleAlignmentPair> pairsToAssign = new ArrayList<RoleAlignmentPair>(); //these are to be assigned to players
	private int GAME_SIZE = 2;
	private ScheduledThreadPoolExecutor timerExecutor = new ScheduledThreadPoolExecutor(1);
	private ScheduledFuture currentTimer;
	private HashMap<User, Vote> votes = new HashMap<User, Vote>(); //key is the voter, value is that user's vote
	private final List<String> NO_LYNCH_STRINGS = Arrays.asList("nolynch", "novote", "idle");
	private final List<String> IDLE_ACTION_STRINGS = Arrays.asList("idle");
	private HashMap<String, HashSet<User>> nicks = new HashMap<String, HashSet<User>>(); //maps a player's nickname to the set of users with that name
	private int NIGHT_TIME = 120;
	private int DAY_TIME = 120;
	
	/**Initialize a game of c5*/
	public Game(TextChannel c){
		channel = c;
		state = State.JOINING;
		players = new HashMap<User, Player>(7);
		names = new HashMap<String, User>(7);
		currentTimer = timerExecutor.schedule(new Runnable(){
			public @Override void run() {
				postMessage("Not enough people joined.");
				cancelGame();
			}
		}, 60, TimeUnit.SECONDS);
		postMessage("A new game of c5 has started. Post '!c5 join' or '&c5 join' to join.");
	}
	
	/**Adds a User to this game if game is in the JOINING state and the User has not joined yet.
	 * When game is full, begins the game.
	 */
	public void addPlayer(Member mem){
		User u = mem.getUser();
		if(state.equals(State.JOINING)){
			if(players.containsKey(u)){
				postMessage(u.getName()+" has already joined this game.");
			}else if(C5Bot.checkUserInUserList(u)){
				postMessage(u.getName()+" is aleady in a game of c5 and cannot join.");
			}else{
				C5Bot.addUserToUserList(u, channel);
				Player p = new Player(u);
				names.put(p.getIdentifier(), u);
				members.add(mem);
				players.put(u, p);
				playerCount++;
				addNicknameMapping(mem);
				postMessage(u.getName()+" has joined the game. "
						+(GAME_SIZE-playerCount)+" player" + (GAME_SIZE-playerCount == 1 ? "s" : "") + " still needed.");
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
	public void removePlayer(Member mem){
		User u = mem.getUser();
		if(state.equals(State.JOINING)){
			if(players.containsKey(u)){
				removeNicknameMapping(u);
				names.remove(players.get(u).getIdentifier());
				players.remove(u);
				playerCount--;
				C5Bot.removeUserFromUserList(u, channel);
				members.remove(mem);
				postMessage(u.getName()+" has left the game. "
							+(GAME_SIZE-playerCount)+" player" + (GAME_SIZE-playerCount == 1 ? "s" : "") + " needed.");
			}
		}
	}
	
	/**Gets the Collection of players.*/
	public Collection<Player> getPlayers(){
		return players.values();
	}
	
	/**Return the nickname currently usable to target the supplied player. This function will be O(n) for large games, but the
	 * weight of adding another map probably isn't worthwhile given the size of a typical game.
	 * 
	 * Throws IllegalArgumentException if the underlying user of the supplied player does not have a nickname stored. 
	 * This should not happen.*/
	public String getCurrentStoredNick(Player p){
		User u = p.getUser();
		return getCurrentStoredNick(u);
	}
	
	/**Return the nickname currently usable to target the supplied user. This function will be O(n) for large games, but the
	 * weight of adding another map probably isn't worthwhile given the size of a typical game.
	 * 
	 * Throws IllegalArgumentException if the supplied user does not have a nickname stored. This should not happen.*/
	public String getCurrentStoredNick(User u){
		for(String e : nicks.keySet()){
			HashSet<User> s = nicks.get(e);
			if(s!=null && s.contains(u)){
				return e;
			}
		}
		throw new IllegalArgumentException("User " + u.getName() + "#" + u.getDiscriminator() + " (ID: " + u.getId()+") does not have a nickname stored.");
	}
	
	/**Add a nickname mapping to the nickname map for the given Member object.*/
	public void addNicknameMapping(Member mem){
		User u = mem.getUser();
		String nick = mem.getEffectiveName().toLowerCase();
		if(nicks.containsKey(nick)){
			nicks.get(nick).add(u);
		}else{
			HashSet<User> set = new HashSet<User>();
			set.add(u);
			nicks.put(nick, set);
		}
	}
	
	/**Remove a nickname mapping from the nickname map corresponding to the given user, if it exists.*/
	public void removeNicknameMapping(User u){
		nicks.forEach((k,v) -> {
			if(v.contains(u)){
				if(v.size()==1){
					nicks.remove(k);
				}else{
					v.remove(u);
				}
			}
		});
	}
	
	/**Update the stored targetable nicknames for each player in the game.*/
	public void updateNicknames(){
		nicks.clear();
		members.forEach(mem -> addNicknameMapping(mem));
	}
	
	/**Ends game prematurely.*/
	private void cancelGame(){
		for(User e:players.keySet()){
			C5Bot.removeUserFromUserList(e, channel);
		}
		C5Bot.removeGame(channel);
	}
	
	/**Set up game and assign roles. Begin the first night.*/
	private void beginGame(){
		cancelTimer();
		//role assignment
		living = new HashSet<Player>(players.values());
		twoPlayerTestRoles();
		ArrayList<Player> shufflePlayers = new ArrayList<Player>(players.values());
		Collections.shuffle(shufflePlayers);
		String playersMessage = "The players are:";
		for(Player p: players.values()){
			playersMessage+=" "+p.getIdentifier();
		}
		CompletableFuture<Boolean>[] messageFutures = new CompletableFuture[GAME_SIZE];
		for(int i=0; i<GAME_SIZE; i++){
			RoleAlignmentPair pair = pairsToAssign.get(i);
			Role r = pair.getRole();
			Alignment a = pair.getAlignment();
			Player p = shufflePlayers.get(i);
			p.setRole(r);
			p.setAlignment(a);
			r.setActor(p);
			a.addPlayer(p);
			messageFutures[i] = p.privateMessage(r.roleMessage() + " "+playersMessage+"\n"+r.winCondition());
			System.out.println(p.getIdentifier()+" "+r.getClass().getName());
		}
		try{
			CompletableFuture.allOf(messageFutures).join();
		}catch (CompletionException e){
			postMessage("There was a problem sending out role PMs. Game is being cancelled.");
			cancelGame();
			throw e;
		}
		postMessage("The game begins."+playersMessage+".");
		beginNight();
	}
	
	/**Begin a night.*/
	private void beginNight(){
		state=State.NIGHT;
		updateNicknames();
		CompletableFuture.allOf(living.stream().map(p -> {
			return p.privateMessage(p.getNightMessage(this));
		}).toArray(CompletableFuture[]::new)).join();
		postMessage("It is now Night "+cycle+". The night ends in "+NIGHT_TIME+" seconds or when all actions are in.");
		currentTimer = timerExecutor.schedule(new Runnable(){
			public @Override void run() {
				endNight();
			}
		}, NIGHT_TIME, TimeUnit.SECONDS);
	}
	
	/**End a night. Resolves all actions placed.*/
	private void endNight(){
		currentTimer.cancel(false);
		postMessage("The night has ended.");
		Iterator<Role> i = roles.iterator();
		while(i.hasNext()){
			i.next().doAction(this);
		}
		i = roles.iterator();
		while(i.hasNext()){ //this is a separate loop in case a target is needed for another role.
			i.next().resetTarget();
		}
		for(Player e:players.values()){
			e.nightReset();
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
		updateNicknames();
		String dayMessage = "It is now Day "+cycle+". "
				+ "Vote for a player to lynch by submitting '!c5 vote <user>' or '&c5 vote <user>' in this channel. "
				+ "You have "+DAY_TIME+" seconds.\n";
		String targets = "";
		for(Player p : living){
			targets += " " + getCurrentStoredNick(p) + " (ID: " + p.getIdentifier() + ")";
		}
		if(targets.equals("")){
			postMessage(dayMessage + "There are no valid targets for the lynch.");
		}else{
			postMessage(dayMessage + "The living players are: " + targets + ".");
		}
		currentTimer = timerExecutor.schedule(new Runnable(){
			public @Override void run() {
				endDay();
			}
		}, DAY_TIME, TimeUnit.SECONDS);
	}
	
	/**Tallies the user with the most votes and lynches them.*/
	private void resolveLynch(){
		postMessage("The voting period has ended.");
		HashMap<User, Integer> tally = new HashMap<User, Integer>();
		for(Vote e:votes.values()){
			if(e.isVoteSet()){
				User u = e.getVote();
				if(tally.containsKey(u)){
					tally.put(u, tally.get(u)+1);
				}else{
					tally.put(u, 1);
				}
			}
		}
		int max = 0;
		User currentLynch = null;
		System.out.println("\nDay " + cycle + " vote:");
		for(User e: tally.keySet()){
			System.out.println(e + " got " + tally.get(e) + " votes.");
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
			if(living.contains(p)){
				killPlayer(p);
				postMessage(p.getIdentifier()+" was lynched. "
						+ "They were a "+p.getRole().cardFlip()+".");
			}else{
				postMessage("You tried to lynch "+p.getIdentifier()+", but they were already dead.");
			}
		}
		votes.clear();
	}
	
	/**End the day. Resolves the lynch.*/
	private void endDay(){
		currentTimer.cancel(false);
		resolveLynch();
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
		Collection<Player> c = a.getMembers();
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
			message +=" (the "+a.getName()+") have won!";
		}else{
			message = "No one wins.";
		}
		postMessage(message);
		for(User e:players.keySet()){
			C5Bot.removeUserFromUserList(e, channel);
		}
		C5Bot.removeGame(channel);
	}
	
	/**Places a vote by voter onto voted.*/
	private void placeVote(User voter, Vote vote){
		if(getState()==State.DAY){
			votes.put(voter, vote);
			if(votes.keySet().size()==living.size()&&cancelTimer()){
				endDay();
			}
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
		if(living.contains(p)){
			living.remove(p);
			p.kill();
		}
	}
	
	/**Attempts to turn a user-provided target string identifying a player into a Optional User object representing the target. 
	 * Returns the empty Optional if no such user uniquely exists. This can happen if two users have the same nickname. */
	public Optional<User> getNamedTarget(String target){
		if(names.containsKey(target)){
			return Optional.of(names.get(target));
		}else if(nicks.containsKey(target.toLowerCase())){
			HashSet<User> set = nicks.get(target.toLowerCase());
			if(set.size()==1){
				return Optional.of(set.iterator().next());
			}
		}
		return Optional.empty();
	}
	
	/**Returns a vote for the either the user represented by the input, no lynch, or an unset vote if the input string is meaningless.*/
	public Vote getVoteFromTarget(String target){
		Vote v = new Vote();
		if(NO_LYNCH_STRINGS.contains(target.replaceAll("\\s", "").toLowerCase())){
			v.setNoLynch();
		}else{
			Optional<User> u = getNamedTarget(target);
			if(u.isPresent()){
				v.setVote(u.get());
			}else{
				v.unsetVote();
			}
		}
		return v;
	}
	
	/**Takes commands in the main text channel and executes them.*/
	public void executeChannelCommand(String[] cmd, Member member){
		User author = member.getUser();
		switch(cmd[1]){
			case "join":
				addPlayer(member);
				break;
			case "leave":
				removePlayer(member);
				break;
			case "vote":
			case "lynch":
				if(cmd.length<3){
					postMessage("Vote command must have a target.");
					break;
				}
				String target = String.join(" ", Arrays.copyOfRange(cmd, 2, cmd.length));
				if(living.contains(players.get(author))){
					Vote v = getVoteFromTarget(target);
					if(v.isVoteSet()){
						placeVote(author, v);
					}else{
						postMessage(target + " does not uniquely identify a valid vote target. Your previous vote is unchanged, if you set one.");
					}
				}
				break;
			case "unvote":
				removeVote(author);
				break;
			case "endNight":
				endNight();
				break;
			case "endDay":
				endDay();
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
		if(executorRole.getCommands().contains(cmd[0])){
			String targetStr = String.join(" ", Arrays.copyOfRange(cmd, 1, cmd.length));
			if(IDLE_ACTION_STRINGS.contains(targetStr)){
				executorRole.setTarget(Optional.empty());
				executor.privateMessage("You are idling your action.");
			}else{
				Optional<User> target = getNamedTarget(targetStr);
				if(target.isPresent()){
					executorRole.setTarget(target.map(t -> players.get(t)));
					executor.privateMessage("You are targeting "+targetStr+" (Discord ID: "+target.get().getName()+"#"+target.get().getDiscriminator()+").");
				}else{
					executor.privateMessage(targetStr + " does not uniquely identify a valid target. Your previous target is unchanged, if you set one.");
				}
			}
			if(allTargetsSet()){
				endNight();
			}
		}else{
			executor.privateMessage("That is not a valid command.");
		}
	}
	
	/**Initialize game to use the roles in a c5 game.*/
	public void c5roles(){
		roles.add(new Wolf());
		roles.add(new SaneCop());
		roles.add(new InsaneCop());
		roles.add(new NaiveCop());
		roles.add(new ParanoidCop());
		Alignment cops = new Village("cops");
		pairsToAssign.add(new RoleAlignmentPair(roles.get(0), new Self("wolf")));
		pairsToAssign.add(new RoleAlignmentPair(roles.get(1), cops));
		pairsToAssign.add(new RoleAlignmentPair(roles.get(2), cops));
		pairsToAssign.add(new RoleAlignmentPair(roles.get(3), cops));
		pairsToAssign.add(new RoleAlignmentPair(roles.get(4), cops));
	}
	
	/**One player game for testing purposes.*/
	public void onePlayerTestRoles(){
		roles.add(new Wolf());
		pairsToAssign.add(new RoleAlignmentPair(roles.get(0), new Self("wolf")));
	}
	
	/**Two player game for testing purposes.*/
	public void twoPlayerTestRoles(){
		roles.add(new Wolf());
		roles.add(new SaneCop());
		pairsToAssign.add(new RoleAlignmentPair(roles.get(0), new Self("wolf")));
		Alignment cops = new Village("cops");
		pairsToAssign.add(new RoleAlignmentPair(roles.get(1),cops));
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
	
	/**Checks if all targets for the night have been set.
	 * 
	 * @return True if it is night and everyone has set a target, false otherwise.
	 */
	public boolean allTargetsSet(){
		if(state != State.NIGHT){
			return false;
		}
		for(Player p: getPlayers()){
			if(p.isAlive()){
				if(!p.getRole().isTargetSet()){
					return false;
				}
			}
		}
		return true;
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
