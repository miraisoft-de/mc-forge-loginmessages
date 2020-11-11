package de.miraisoft.loginmessages;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.Collection;
import java.util.Collections;
import java.util.concurrent.CompletableFuture;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.mojang.brigadier.Message;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;

/**
 * ArgumentType implementation for {@link LMCommand}<br>
 * Valid first arguments are: {@code add}, {@code list}, {@code remove},
 * {@code removeall}, {@code help}<br>
 * {@code add} and {@code remove} need second argument as well<br>
 * License: CC BY Miraisoft
 * 
 * @author pcs
 * @since 1.0
 *
 */
public class LMArgumentType implements ArgumentType<String> {
	private static final Logger logger = LogManager.getLogger();

	public static final int TYPE_LEVEL0 = 0;
	public static final int TYPE_LEVEL1 = 1;

	private static final int PREVIEW_LENGTH = 10;

	/**
	 * Enum of valid first arguments
	 *
	 */
	public enum ValidArgument {
		ADD("Add login message"), LIST("List login messages"), REMOVE("Remove login message"),
		REMOVEALL("Remove all login messages"), HELP("Command usage help");

		public Message tooltip;

		private ValidArgument(final String tooltipText) {
			this.tooltip = () -> {
				return tooltipText;
			};
		}

		@Override
		public String toString() {
			return name().toLowerCase();
		}

		public boolean isMatch(final String arg) {
			return toString().equals(arg);
		}
	};

	private int type;

	public LMArgumentType(final int type) {
		this.type = type;
	}

	/**
	 * @see StringArgumentType
	 */
	@Override
	public String parse(final StringReader reader) throws CommandSyntaxException {
		if (type == TYPE_LEVEL0) {
			return reader.readUnquotedString();
		} else {
			final String text = reader.getRemaining();
			reader.setCursor(reader.getTotalLength());
			return text;
		}
	}

	public <S> CompletableFuture<Suggestions> listSuggestions(final CommandContext<S> context,
			final SuggestionsBuilder builder) {
		final int firstSpaceIndex = context.getInput().indexOf(' ');
		final int lastSpaceIndex = context.getInput().lastIndexOf(' ');
		final boolean containsMultipleSpaces = (firstSpaceIndex != lastSpaceIndex);
		String args = context.getInput().replace("/" + LMCommand.LOGINMESSAGE, "").trim();
		if (args.length() > 0) {
			if (args.startsWith(ValidArgument.ADD.toString()) && containsMultipleSpaces) {
				builder.suggest("new login message");
			} else if (ValidArgument.REMOVE.isMatch(args) && !containsMultipleSpaces) {
				builder.suggest(ValidArgument.REMOVE.toString(), ValidArgument.REMOVE.tooltip);
				builder.suggest(ValidArgument.REMOVEALL.toString(), ValidArgument.REMOVEALL.tooltip);
			} else if (args.startsWith(ValidArgument.REMOVE.toString())
					// remove <=> removeall
					&& !args.startsWith(ValidArgument.REMOVE.toString() + "a") && containsMultipleSpaces) {
				suggestRemoveOptions(builder);
			} else {
				for (final ValidArgument va : ValidArgument.values()) {
					if (va.toString().startsWith(args) && !containsMultipleSpaces) {
						builder.suggest(va.toString(), va.tooltip);
					}
				}

			}
		} else {
			builder.suggest(ValidArgument.ADD.toString(), ValidArgument.ADD.tooltip);
			builder.suggest(ValidArgument.LIST.toString(), ValidArgument.LIST.tooltip);
			builder.suggest(ValidArgument.REMOVE.toString(), ValidArgument.REMOVE.tooltip);
			builder.suggest(ValidArgument.REMOVEALL.toString(), ValidArgument.REMOVEALL.tooltip);
			builder.suggest(ValidArgument.HELP.toString(), ValidArgument.HELP.tooltip);
		}

		return builder.buildFuture();
	}

	private void suggestRemoveOptions(final SuggestionsBuilder builder) {
		try {
			final File file = LoginMessagesMod.getFile();
			if (file.exists()) {
				final BufferedReader reader = new BufferedReader(new FileReader(file));
				int n = 0;
				while (reader.ready()) {
					n++;
					String line = reader.readLine();
					// provide only a preview of the message here
					if (line.length() > PREVIEW_LENGTH) {
						line = line.substring(0, PREVIEW_LENGTH) + "...";
					}
					builder.suggest(n + " " + line);
				}
				reader.close();
			}
		} catch (Exception e) {
			logger.error("Cannot suggest remove options", e);
		}
	}

	public Collection<String> getExamples() {
		return Collections.emptyList();
	}
}
