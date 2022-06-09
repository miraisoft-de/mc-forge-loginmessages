package de.miraisoft.loginmessages;

import com.mojang.brigadier.Message;

import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;

/**
 * Useful methods for Minecraft message handling<br>
 * License: CC BY Miraisoft
 * 
 * @author pcs
 * @since 1.0
 *
 */
public class MessageUtil {
	private static final String FORMATTING = "§6";

	/**
	 * Sends message to player
	 * 
	 * @param player
	 * @param message
	 */
	public static void send(final Player player, final String message) {
		player.displayClientMessage(Component.literal(FORMATTING + message), false);
	}

	/**
	 * Creates Minecraft message
	 * 
	 * @param text
	 * @return message
	 */
	public static Message create(final String text) {
		final Message message = () -> {
			return text;
		};
		return message;
	}

	/**
	 * Creates empty Minecraft message
	 * 
	 * @return empty message
	 */
	public static Message emptyMessage() {
		final Message message = () -> {
			return "";
		};
		return message;
	}
}
