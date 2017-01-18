package game;
import java.util.HashMap;

import net.dv8tion.jda.core.AccountType;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.JDABuilder;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;

public class C5Bot extends ListenerAdapter{

	private static String token;
	private static HashMap<TextChannel, Game> games = new HashMap<TextChannel, Game>();
	
	/**Creates an instance of JDA*/
	public static void main(String[] args) {
		if(args.length!=1){
			System.out.println("Usage: java -jar MemeBot.jar token");
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
					g.addPlayer(m.getAuthor());
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
}
