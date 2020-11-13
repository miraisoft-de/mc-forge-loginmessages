package de.miraisoft.loginmessages;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.StringTokenizer;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.Message;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;

import net.minecraft.command.CommandSource;
import net.minecraft.entity.player.ServerPlayerEntity;

/**
 * Implementation for command /loginmessage<br>
 * Usage examples:<br>
 * /loginmessage add Welcome to the server!<br>
 * /loginmessage list<br>
 * /loginmessage remove 1<br>
 * /loginmessage removeall<br>
 * /loginmessage help<br>
 * License: CC BY Miraisoft
 * 
 * @author pcs
 * @since 1.0
 *
 */
public class LMCommand implements Command<CommandSource> {
	private static final Logger logger = LogManager.getLogger();

	public static final String LOGINMESSAGE = "loginmessage";

	public static final String ARG0 = "arg0";
	public static final String ARG1 = "arg1";

	public static final String ADD = "add";
	public static final String LIST = "list";
	public static final String REMOVE = "remove";
	public static final String REMOVEALL = "removeall";
	public static final String HELP = "help";

	private static final int MESSAGE_LENGTH_LIMIT = 100;

	@Override
	public int run(final CommandContext<CommandSource> context) throws CommandSyntaxException {
		final ServerPlayerEntity player = context.getSource().asPlayer();
		try {
			if (context.getArgument(ARG0, String.class) != null) {
				final File file = LoginMessagesMod.getFile();
				final String arg0Value = context.getArgument(ARG0, String.class);
				if (ADD.equals(arg0Value) && context.getArgument(ARG1, String.class) != null) {
					addLoginMessage(context, player, file);
				} else if (REMOVEALL.equals(arg0Value)) {
					removeAllLoginMessages(player, file);
				} else if (LIST.equals(arg0Value)) {
					listLoginMessages(player, file);
				} else if (REMOVE.equals(arg0Value) && context.getArgument(ARG1, String.class) != null) {
					removeSingleLoginMessage(context, player, file);
				} else if (HELP.equals(arg0Value)) {
					displayUsageHelp(player);
				}
			}
		} catch (final CommandSyntaxException cse) {
			throw cse;
		} catch (final Exception e) {
			logger.error("[run] Error during command execution", e);
			final Message message = MessageUtil.create("Error during command execution: " + e.getMessage());
			throw new CommandSyntaxException(new SimpleCommandExceptionType(MessageUtil.emptyMessage()), message);
		}
		return 0;
	}

	private void removeSingleLoginMessage(final CommandContext<CommandSource> context, final ServerPlayerEntity player,
			final File file) throws CommandSyntaxException {
		try {
			boolean removed = false;
			if (file.exists()) {
				final String arg1 = context.getArgument(ARG1, String.class);
				final StringTokenizer tokenizer = new StringTokenizer(arg1, " ");
				int messageIndex = -1;
				try {
					messageIndex = Integer.parseInt(tokenizer.nextToken());
				} catch (Exception e) {
					logger.error("[removeSingleLoginMessage] Cannot remove message", e);
					final Message message = MessageUtil
							.create("Can't remove message. Usage example: /loginmessage remove 1");
					throw new CommandSyntaxException(new SimpleCommandExceptionType(MessageUtil.emptyMessage()),
							message);
				}
				final BufferedReader reader = new BufferedReader(new FileReader(file));
				final StringBuffer messageBuffer = new StringBuffer();
				int n = 0;
				while (reader.ready()) {
					n++;
					String line = reader.readLine();
					if (n == messageIndex) {
						removed = true;
						continue;
					}
					if (messageBuffer.length() > 0) {
						messageBuffer.append("\n");
					}
					messageBuffer.append(line);
				}
				reader.close();
				if (messageBuffer.length() == 0) {
					file.delete();
				} else {
					final BufferedWriter writer = new BufferedWriter(new FileWriter(file, false));
					writer.write(messageBuffer.toString());
					writer.close();
				}
				String feedback = null;
				if (removed) {
					feedback = "Login message " + messageIndex + " has been removed from list";
				} else {
					feedback = "No login message has been removed";
				}
				MessageUtil.send(player, feedback);
			} else {
				MessageUtil.send(player, "There are no messages to remove");
			}
		} catch (CommandSyntaxException cse) {
			throw cse;
		} catch (final Exception e) {
			logger.error("[removeSingleLoginMessage] Cannot remove message", e);
			final Message message = MessageUtil.create("Cannot remove message: " + e.getMessage());
			throw new CommandSyntaxException(new SimpleCommandExceptionType(MessageUtil.emptyMessage()), message);
		}
	}

	private void listLoginMessages(final ServerPlayerEntity player, final File file) throws CommandSyntaxException {
		try {
			if (file.exists()) {
				final BufferedReader reader = new BufferedReader(new FileReader(file));
				int n = 0;
				while (reader.ready()) {
					n++;
					final String loginMessage = n + ") " + reader.readLine();
					MessageUtil.send(player, loginMessage);
				}
				reader.close();
			} else {
				MessageUtil.send(player, "There are no login messages listed");
			}
		} catch (final Exception e) {
			logger.error("[listLoginMessages] Cannot display list", e);
			final Message message = MessageUtil.create("Cannot display list: " + e.getMessage());
			throw new CommandSyntaxException(new SimpleCommandExceptionType(MessageUtil.emptyMessage()), message);
		}
	}

	private void removeAllLoginMessages(final ServerPlayerEntity player, final File file)
			throws CommandSyntaxException {
		try {
			if (file.exists()) {
				file.delete();
				MessageUtil.send(player, "All login messages have been removed");
			} else {
				MessageUtil.send(player, "There are no messages to remove");
			}
		} catch (final Exception e) {
			logger.error("[removeAllLoginMessages] Cannot remove messages", e);
			final Message message = MessageUtil.create("Cannot remove messages: " + e.getMessage());
			throw new CommandSyntaxException(new SimpleCommandExceptionType(MessageUtil.emptyMessage()), message);
		}
	}

	private void addLoginMessage(final CommandContext<CommandSource> context, final ServerPlayerEntity player,
			final File file) throws CommandSyntaxException {
		try {
			final String arg1 = context.getArgument(ARG1, String.class);
			if (arg1.length() > MESSAGE_LENGTH_LIMIT) {
				MessageUtil.send(player, "Message is too long. The limit is " + MESSAGE_LENGTH_LIMIT + " characters!");
				return;
			}
			final StringBuffer loginMessage = new StringBuffer();
			if (file.exists()) {
				loginMessage.append("\n");
			}
			loginMessage.append(arg1);
			final BufferedWriter writer = new BufferedWriter(new FileWriter(file, true));
			writer.write(loginMessage.toString());
			writer.close();
			MessageUtil.send(player, "Login message has been added to list");
		} catch (final Exception e) {
			logger.error("[addLoginMessage] Cannot add message", e);
			final Message message = MessageUtil.create("Cannot add message: " + e.getMessage());
			throw new CommandSyntaxException(new SimpleCommandExceptionType(MessageUtil.emptyMessage()), message);
		}
	}

	private void displayUsageHelp(final ServerPlayerEntity player) throws CommandSyntaxException {
		String[] examples = new String[] { "/loginmessage add Welcome to the server!", "/loginmessage list",
				"/loginmessage remove 1", "/loginmessage removeall" };
		MessageUtil.send(player, "Command usage examples:");
		for (final String example : examples) {
			MessageUtil.send(player, example);
		}
	}
}
