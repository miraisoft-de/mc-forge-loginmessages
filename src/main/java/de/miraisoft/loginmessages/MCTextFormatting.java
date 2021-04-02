package de.miraisoft.loginmessages;

/**
 * Data structure for Minecraft text formatting codes
 * License: CC BY Miraisoft
 * 
 * @author pcs
 * @since 1.0.3
 *
 */
public enum MCTextFormatting {
	BLACK("black", '0'),
	DARK_BLUE("dark blue", '1'),
	DARK_GREEN("dark green", '2'),
	DARK_AQUA("dark green", '3'),
	DARK_RED("dark green", '4'),
	DARK_PURPLE("dark green", '5'),
	GOLD("gold", '6'),
	GRAY("gray", '7'),
	DARK_GRAY("dark gray", '8'),
	BLUE("blue", '9'),
	GREEN("green", 'a'),
	AQUA("aqua", 'b'),
	RED("red", 'c'),
	LIGHT_PURPLE("light purple", 'd'),
	YELLOW("yellow", 'e'),
	WHITE("white", 'f'),
	OBFUSCATED("obfuscated", 'k'),
	BOLD("bold", 'l'),
	STRIKETHROUGH("strikethrough", 'm'),
	UNDERLINE("underline", 'n'),
	ITALIC("italic", 'o'),
	RESET("reset", 'r');
	// new line not added
	
	public static final String ESCAPE_CHARACTER = "§";
	
	private String description;
	private char code;
	
	private MCTextFormatting(String description, char code) {
		this.description = description;
		this.code = code;
	}

	public String getDescription() {
		return description;
	}
	
	public char getCode() {
		return code;
	}
	
}
