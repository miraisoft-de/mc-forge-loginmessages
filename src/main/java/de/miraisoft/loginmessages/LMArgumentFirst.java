package de.miraisoft.loginmessages;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import com.google.gson.JsonObject;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;

import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.synchronization.ArgumentTypeInfo;
import net.minecraft.network.FriendlyByteBuf;

/**
 * {@code ArgumentType} implementation for Minecraft forge mod
 * miraisoftloginmessages<br>
 * Valid first arguments are: {@code add}, {@code list}, {@code remove},
 * {@code removeall}, {@code help}<br>
 * {@code add} and {@code remove} need second argument as well<br>
 * License: CC BY Miraisoft
 * 
 * @author pcs
 * @since 1.0.2
 *
 */
public class LMArgumentFirst implements ArgumentType<String>, Serializable {

	private static final long serialVersionUID = -426364364374784L;

	@Override
	public String parse(final StringReader reader) throws CommandSyntaxException {
		return reader.readUnquotedString();
	}

	public <S> CompletableFuture<Suggestions> listSuggestions(final CommandContext<S> context,
			final SuggestionsBuilder builder) {
		String args = context.getInput().replace("/" + LMConstants.LOGINMESSAGE, "").trim();

		List<String> validArguments = new ArrayList<>();
		validArguments.add(LMConstants.ADD);
		validArguments.add(LMConstants.LIST);
		validArguments.add(LMConstants.REMOVE);
		validArguments.add(LMConstants.REMOVEALL);
		validArguments.add(LMConstants.HELP);

		if (args.length() > 0) {
			for (final String va : validArguments) {
				if (va.startsWith(args)) {
					builder.suggest(va);
				}
			}
		} else {
			for (final String va : validArguments) {
				builder.suggest(va);
			}
		}

		return builder.buildFuture();

	}

	public Collection<String> getExamples() {
		return Collections.emptyList();
	}

	public static class Info implements ArgumentTypeInfo<LMArgumentFirst, Info.Template> {

		@Override
		public void serializeToNetwork(Template template, FriendlyByteBuf buffer) {
			// do nothing?
		}

		@Override
		public Template deserializeFromNetwork(FriendlyByteBuf buffer) {
			return new Template(new LMArgumentFirst());
		}

		@Override
		public void serializeToJson(Template template, JsonObject jsonObject) {
			// do nothing?
		}

		@Override
		public Template unpack(LMArgumentFirst arg) {
			return new Template(arg);
		}
		
		public class Template implements ArgumentTypeInfo.Template<LMArgumentFirst>
        {
            final LMArgumentFirst arg;

            Template(LMArgumentFirst arg)
            {
                this.arg = arg;
            }

            @Override
            public LMArgumentFirst instantiate(CommandBuildContext cbContext)
            {
                return new LMArgumentFirst();
            }

            @Override
            public ArgumentTypeInfo<LMArgumentFirst, ?> type()
            {
                return Info.this;
            }
        }
	}
}