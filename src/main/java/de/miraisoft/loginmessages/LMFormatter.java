package de.miraisoft.loginmessages;

import net.minecraft.entity.player.PlayerEntity;

/**
 * Handles custom text formatting
 * License: CC BY Miraisoft
 * 
 * @author pcs
 * @since 1.0.3
 *
 */
public class LMFormatter {
	private static final String[] DEFAULT_FORMATTINGS = new String[] { "§d§l", "§9" };
	private static final String FORMATTING_BEGIN = "<<";
	private static final String FORMATTING_END = ">>";
	private static final String PLAYERNAME = "playername";

	public static String getDefaultLineFormatting(final int n) {
		return DEFAULT_FORMATTINGS[n % DEFAULT_FORMATTINGS.length];
	}

	public static String convertToMCText(String text, PlayerEntity player) {
		for (MCTextFormatting formattingCode : MCTextFormatting.values()) {
			text = text.replaceAll(FORMATTING_BEGIN + formattingCode.getCode() + FORMATTING_END,
					MCTextFormatting.ESCAPE_CHARACTER + formattingCode.getCode());
		}
		text = text.replaceAll(FORMATTING_BEGIN + PLAYERNAME + FORMATTING_END, player.getScoreboardName());
		return text;
	}
	
	public static String convertBeforeDisplaying(String text, PlayerEntity player) {
		// display player name #6
		text = text.replaceAll(FORMATTING_BEGIN + PLAYERNAME + FORMATTING_END, player.getScoreboardName());
		return text;
	}
}
