package de.miraisoft.loginmessages;

import java.io.Serializable;
import java.util.Collection;
import java.util.Collections;
import java.util.concurrent.CompletableFuture;

import com.google.gson.JsonObject;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;

import net.minecraft.commands.synchronization.ArgumentSerializer;
import net.minecraft.network.FriendlyByteBuf;

/**
 * {@code ArgumentType} implementation for Minecraft forge mod
 * miraisoftloginmessages<br>
 * This class covers possible second arguments<br>
 * License: CC BY Miraisoft
 * 
 * @author pcs
 * @since 1.0.2
 *
 */
public class LMArgumentSecond implements ArgumentType<String>, Serializable {

	private static final long serialVersionUID = -4237192523728916L;

	@Override
	public String parse(final StringReader reader) throws CommandSyntaxException {
		final String text = reader.getRemaining();
		reader.setCursor(reader.getTotalLength());
		return text;
	}

	public <S> CompletableFuture<Suggestions> listSuggestions(final CommandContext<S> context,
			final SuggestionsBuilder builder) {
		final int firstSpaceIndex = context.getInput().indexOf(' ');
		final int lastSpaceIndex = context.getInput().lastIndexOf(' ');
		final boolean containsMultipleSpaces = (firstSpaceIndex != lastSpaceIndex);
		String args = context.getInput().replace("/" + LMConstants.LOGINMESSAGE, "").trim();

		if (args.length() > 0) {
			if (args.startsWith(LMConstants.ADD) && containsMultipleSpaces) {
				builder.suggest("new login message");
			} else if (args.startsWith(LMConstants.REMOVE) && !args.startsWith(LMConstants.REMOVE + "a")
					&& containsMultipleSpaces) {
				builder.suggest("message number");
			}
		}

		return builder.buildFuture();
	}

	public Collection<String> getExamples() {
		return Collections.emptyList();
	}

	public static class Serializer implements ArgumentSerializer<LMArgumentSecond> {

		@Override
		public void serializeToNetwork(LMArgumentSecond second, FriendlyByteBuf buffer) {
			// do nothing
			
		}

		@Override
		public LMArgumentSecond deserializeFromNetwork(FriendlyByteBuf buffer) {
			return new LMArgumentSecond();
		}

		@Override
		public void serializeToJson(LMArgumentSecond second, JsonObject json) {
			// TODO do nothing
			
		}

	}
}