package de.miraisoft.loginmessages;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.mojang.brigadier.CommandDispatcher;

import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.command.arguments.ArgumentTypes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.PlayerEvent.PlayerLoggedInEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.server.FMLServerStartingEvent;

/**
 * Main class for Minecraft forge mod miraisoftloginmessages<br>
 * Provides command /loginmessage<br>
 * License: CC BY Miraisoft
 * 
 * @author pcs
 * @since 1.0
 * @see LMCommand
 *
 */
@Mod(LoginMessagesMod.MOD_ID)
@Mod.EventBusSubscriber(modid = LoginMessagesMod.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class LoginMessagesMod {
	public static final String MOD_ID = "miraisoftloginmessages";

	private static final Logger logger = LogManager.getLogger();

	private static final Path directory = Paths.get("config/" + MOD_ID);
	private static final File file = new File(directory.toUri().getPath() + "/loginmessages.conf");

	private static final String[] TEXT_FORMATTINGS = new String[] { "§d§l", "§9" };
	private static final int MOD_PERMISSION_LEVEL = 3;

	public LoginMessagesMod() {
		if (!directory.toFile().exists()) {
			try {
				Files.createDirectory(directory);
			} catch (final IOException e) {
				logger.error("[init] Could not create config directory", e);
			}
		}
		MinecraftForge.EVENT_BUS.register(this);
		logger.debug("[init] Mod miraisoftloginmessages is initialized");

		try {
			ArgumentTypes.register("lmargument0", LMArgumentFirst.class, new LMArgumentFirst.Serializer());
			ArgumentTypes.register("lmargument1", LMArgumentSecond.class, new LMArgumentSecond.Serializer());
		} catch (Exception e) {
			logger.error("[init] Cannot register serializer for argument types", e);
		}
	}

	public static File getFile() {
		return file;
	}

	@SubscribeEvent
	public static void onServerStarting(FMLServerStartingEvent event) {
		try {
			final CommandDispatcher<CommandSource> dispatcher = event.getCommandDispatcher();

			dispatcher.register(Commands.literal(LMConstants.LOGINMESSAGE)
					.requires(source -> source.hasPermissionLevel(MOD_PERMISSION_LEVEL))
					.then(Commands.argument(LMConstants.ARG0, new LMArgumentFirst())
							.requires(source -> source.hasPermissionLevel(MOD_PERMISSION_LEVEL))
							.executes(new LMCommand())
							.then(Commands.argument(LMConstants.ARG1, new LMArgumentSecond())
									.requires(source -> source.hasPermissionLevel(MOD_PERMISSION_LEVEL))
									.executes(new LMCommand()))));

			logger.debug(
					"[registerCommand] Command /" + LMConstants.LOGINMESSAGE + " has successfully been registered");
		} catch (final Exception e) {
			logger.error("[registerCommand] Failed to register command /" + LMConstants.LOGINMESSAGE, e);
		}

	}

	@SubscribeEvent
	public void onPlayerLogin(final PlayerLoggedInEvent event) {
		final PlayerEntity player = event.getPlayer();
		if (file.exists()) {
			try {
				final BufferedReader reader = new BufferedReader(new FileReader(file));
				int n = 0;
				while (reader.ready()) {
					final StringBuffer lineBuffer = new StringBuffer();
					// change default formatting between the messages to make them easier to read
					lineBuffer.append(getFormatting(n));
					lineBuffer.append(reader.readLine());
					MessageUtil.send(player, lineBuffer.toString());
					n++;
				}
				reader.close();
			} catch (final Exception e) {
				logger.error("[onPlayerLogin] Could not display login messages", e);
			}
		}
	}

	private String getFormatting(final int n) {
		return TEXT_FORMATTINGS[n % TEXT_FORMATTINGS.length];
	}
}
