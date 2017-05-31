package game;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import net.dv8tion.jda.core.AccountType;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.JDABuilder;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.core.events.message.priv.PrivateMessageReceivedEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;

public class C5Bot extends ListenerAdapter{

	private static String token;
	private static HashMap<TextChannel, Game> games = new HashMap<TextChannel, Game>();
	private static HashMap<String, TextChannel> channels = new HashMap<String, TextChannel>(); 
	private static Set<User> users = new HashSet<User>();
	
	/**Creates an instance of JDA*/
	public static void main(String[] args) {
		if(args.length!=1){
			System.out.println("Usage: java -jar C5Bot.jar token");
			System.exit(1);
		}
		token = args[0];
		try{
            JDA jda = new JDABuilder(AccountType.BOT)
                    .setToken(token)
                    .addListener(new C5Bot())
                    .buildBlocking();
        }
        catch(Exception e){
            e.printStackTrace();
        }
	}
	
	@Override
	public void onGuildMessageReceived(GuildMessageReceivedEvent event){
		Message m = event.getMessage();
		String s = m.getContent();
		TextChannel channel = m.getTextChannel();
		String[] words = s.split(" ");
		if(words.length>=2&&words[0].toLowerCase().equals("!c5")){
			if(words[1].toLowerCase().equals("start")){
				if(games.containsKey(channel)){
					postMessage("There is already a c5 game running in this channel.", channel);
				}else{
					Game g = new Game(channel);
					games.put(channel, g);
					channels.put(channel.getId(), channel);
					g.addPlayer(m.getAuthor());
				}
			}else if(games.containsKey(channel)){
				games.get(channel).executeChannelCommand(words, m.getAuthor());
			}
		}
	}
	
	@Override
	public void onPrivateMessageReceived(PrivateMessageReceivedEvent event){
		Message m = event.getMessage();
		String s = m.getContent();
		String[] words = s.split(" ");
		if(words.length>=3&&channels.containsKey(words[0])){
			games.get(channels.get(words[0])).executePrivateCommand(words, event.getAuthor());
		}
	}
	
	/**Remove this game from the internal list of games. Should result in garbage collection,
	 * unless something is wrong.
	 */
	public static void removeGame(TextChannel channel){
		games.remove(channel);
	}
	
	/**Send a message to the given text channel*/
	public static void postMessage(String s, TextChannel channel){
		channel.sendMessage(s).queue();
	}
	
	/**Adds User to list of Users in Games. A User cannot be in more than one Game at a time.*/
	public static void addUserToUserList(User u){
		users.add(u);
	}
	
	/**Removes User from list of Users in Games.*/
	public static void removeUserFromUserList(User u){
		users.remove(u);
	}
	
	/**Checks if User is in list of Users in Games.*/
	public static boolean checkUserInUserList(User u){
		return users.contains(u);
	}
}
