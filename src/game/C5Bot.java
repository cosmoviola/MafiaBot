package game;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
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
	private static Map<TextChannel, Game> games = new HashMap<TextChannel, Game>();
	private static Map<User, TextChannel> channels = new HashMap<User, TextChannel>();
	private static User botUser;
	
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
                    .addEventListener(new C5Bot())
                    .buildBlocking();
            botUser = jda.getSelfUser();
        }
        catch(Exception e){
            e.printStackTrace();
        }
	}
	
	@Override
	public void onGuildMessageReceived(GuildMessageReceivedEvent event){
		Message m = event.getMessage();
		String s = m.getContentRaw();
		TextChannel channel = m.getTextChannel();
		String[] words = s.split(" ");
		if(words.length>=2&&(words[0].toLowerCase().equals("!c5")||words[0].toLowerCase().equals("&c5"))){
			if(words[1].toLowerCase().equals("start")){
				if(games.containsKey(channel)){
					postMessage("There is already a c5 game running in this channel.", channel);
				}else{
					Game g = new Game(channel);
					games.put(channel, g);
					g.addPlayer(m.getMember());
				}
			}else if(games.containsKey(channel)){
				games.get(channel).executeChannelCommand(words, m.getMember());
			}
		}
	}
	
	@Override
	public void onPrivateMessageReceived(PrivateMessageReceivedEvent event){
		User author = event.getAuthor();
		if(author.equals(botUser)){	
		}else{
			Message m = event.getMessage();
			String s = m.getContentRaw();
			String[] words = s.split(" ");
			if(words.length>=2){
				Game g = games.get(channels.get(author));
				if(g != null){
					g.executePrivateCommand(words, author);
				}
			}
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
	public static void addUserToUserList(User u, TextChannel t){
		channels.put(u,t);
	}
	
	/**Removes User from list of Users in Games.*/
	public static void removeUserFromUserList(User u, TextChannel t){
		channels.remove(u,t);
	}
	
	/**Checks if User is in list of Users in Games.*/
	public static boolean checkUserInUserList(User u){
		return channels.keySet().contains(u);
	}
}
