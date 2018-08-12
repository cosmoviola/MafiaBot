package help;

import java.util.Arrays;

public class Help {
	public static String parseHelpCommand(String[] cmd){
		if(cmd.length == 0){
			return defaultHelpCommand();
		}
		switch(cmd[0].toLowerCase()){
			case "role":
				return parseRoleInfoCommand(Arrays.copyOfRange(cmd, 1, cmd.length));
			default:
				return "Invalid command '"+ cmd[0]+"'.";
		}
	}
	
	public static String parseRoleInfoCommand(String[] cmd){
		String rolename = String.join("", cmd).toLowerCase();
		switch(rolename){
			case "cop":
			case "sanecop":
				return "Targets a user at night and is told whether the target is aligned with them. Does not know they are sane.";
			case "insanecop":
				return "Targets a user at night and is told the target is aligned with them if the target is not aligned with them, "
						+ "and vice-versa. Does not know they are insane.";
			case "paranoidcop":
				return "Targets a user at night and is told that the target is not aligned with them. Does not know they are paranoid.";
			case "naivecop":
				return "Targets a user at night and is told that the target is aligned with them. Does not know they are naive.";
			case "wolf":
				return "On Night 0, targets a user at night to prevent their action from working. After Night 0, targets a user at "
						+ "night to kill them.";
			case "hooker":
				return "Targets a user at night to prevent them from using their night actions.";
			case "martyr":
				return "Targets a user at night to force them to target the martyr instead of their target.";
			case "bodyguard":
			case "bg":
				return "Targets a user at night to protect them from all kills.";
			case "safeguard":
			case "sg":
				return "Targets a user at night to protect them from all non-killing actions.";
			case "mayor":
				return "Has no night action, but their vote is worth double a normal vote in the lynch.";
			case "vanilla":
			case "vanillager":
				return "Has no special abilities.";
			case "redirector":
				return "Targets two users at night to redirect all of one user's actions to the second user.";
			default:
				return "The provided role is not recoginzed as a role.";
		}
	}
	
	public static String defaultHelpCommand(){
		return "C5 is a format of mafia with 5 players. Four cops are aligned, a Sane Cop, an Insane Cop, "
				+ "a Paranoid Cop, and a Naive Cop, in killing a wolf."
				+ "\n\nUse the command 'help role <rolename>' to see what that role does.";
	}
}
