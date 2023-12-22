package net.exoad.ansicolor;
// Software created by Jack Meng (AKA exoad). Licensed by the included "LICENSE" file. If this file is not found, the project is fully copyrighted.

import net.exoad.math.Maths;

import java.io.PrintStream;
import java.util.Objects;

/**
 * <h1>AnsiColor - ANSI Coloring</h1>
 * <em>Copyright (C) Jack Meng 2023</em>
 * <p>
 * ANSI provides most consoles with a coloring format that you can use to build
 * colorful CLI Applications or just to be more pretty with DEBUG messages. This
 * simple library helps you to create cascading calls for creating concise and
 * readable ANSI display codes. You don't have to mess with making your own and
 * potentially making it overburdened and verbose to use!
 * </p>
 * <p>
 * Use this project in your code for displaying colorful console messages! All
 * you have to is call {@link #make(String)} and then you have a whole bunch of
 * formatting options to utilize :)
 * </p>
 * <p>
 * You can also use functions similar to StringBuilder's class but however with
 * a very limited subset of the methods available to you. It is limited because
 * in order to create a cascading call pattern, certain functions that must
 * return a type other than the cascading type will destroy cascading. However,
 * some of them can be accessed like {@link String#charAt(int)} by calling the
 * {@link InternedInstance#content()} method which will return the String
 * representation of the content for you to mess with. Other than that, all
 * other methods will strictly return the current InternedInstance instance to
 * continue the cascading pattern.
 * </p>
 * <h2>
 * Simple usage guide
 * </h2>
 * Everything is based off of the concept of cascading. You might have seen this
 * with the class {@link StringBuilder} which you can constantly call methods
 * like {@link StringBuilder#append(String)} and
 * {@link StringBuilder#insert(int,String)} in a long chain like <br>
 * {@code newStringBuilder("Hello").append("World").append("!")}. This is the
 * exact concept used with {@link AnsiColor}!
 * <br>
 * To start your cascade, you first must acquire an instance from
 * {@link AnsiColor} which you can do via two methods:
 * <ul>
 * <li>{@link AnsiColor#make(String)} - Supplies the payload (AKA the string you
 * want to format first).
 * This introduces the pattern of apply first, format second:
 * {@code make(payload).format().format()...toString()}
 * </li>
 * <li>{@link AnsiColor#make()} - Intended for the programmer to supply the
 * payload later via a method like {@link InternedInstance#toString(String)}.
 * This introduces the pattern of format first, apply second:
 * {@code make().format().format()...toString(payload)}
 * </li>
 * </ul>
 * Whichever one you use does not effect anything as both are interchangeable.
 * It all comes down to personal preferences on how you like to order your
 * code :).
 * <br>
 * To print the message to say {@code System.out}, you can just put it like so:
 * <br>
 * {@code System.out.print(make().format().format().format()} as the inbuilt
 * {@link InternedInstance#toString()} takes care
 * of how to handle printing. However, this provides an automatic RESET
 * character which makes sure the formatted content
 * is only applied to that portion of the text. If you want to leave out this
 * automatic RESET character, don't use
 * these methods:
 * <ul>
 * <li>{@link InternedInstance#toString()}</li>
 * <li>{@link InternedInstance#toString(String)}</li>
 * </ul>
 * Instead you should use {@link InternedInstance#render()}
 * <br>
 * <h3>
 * Formatting
 * </h3>
 * Formatting is the way to modify the content which is given by
 * {@link InternedInstance#content()}. To modify text you use cascading
 * calls within the {@link InternedInstance} class. Some of these methods include:
 * <ul>
 * <li>
 * <p>
 * {@link InternedInstance#red()} - Makes the <em>FOREGROUND</em> of the content RED
 * </p>
 * </li>
 * <li>
 * <p>
 * {@link InternedInstance#green()} - Makes the <em>FOREGROUND</em> of the content GREEN
 * </p>
 * </li>
 * <li>
 * <p>
 * {@link InternedInstance#blue()} - Makes the <em>FOREGROUND</em> of the content BLUE
 * </p>
 * </li>
 * <li>
 * <p>
 * {@link InternedInstance#bold()} - Makes the content appear BOLDED (high intensity)
 * </p>
 * </li>
 * </ul>
 * You also notice that certain colored functions like
 * {@link InternedInstance#magenta_fg()} or {@link InternedInstance#yellow_bg()} has a
 * suffix like {@code _fg} and {@code _bg}. These are naming schemes and are
 * followed by for all colors:
 * <ul>
 * <li>
 * {@code _fg} or color with no suffix - FOREGROUND coloring
 * </li>
 * <li>
 * {@code _bg} - BACKGROUND coloring
 * </li>
 * </ul>
 * <em>
 * <strong>
 * [!] You should always keep in mind that different platforms support different
 * formatting looks. Sometimes
 * they don't support formatting at all!
 * </strong>
 * </em>
 * <h2>Example Usages</h2>
 * <ul>
 * <li>
 * <p>
 * <strong>Printing RED Spectrum (RGB)</strong>
 *
 * <pre>
 * for (int i = 0; i &#60;= 255; i++)
 *   System.out.print(AnsiColor.make(" ").rgb_bg(i, 0, 0));
 * </pre>
 * </p>
 * </li>
 * <li>
 * <p>
 * <strong>Printing a Bolded Warning Text</strong>
 *
 * <pre>
 * System.out.println(
 *     AnsiColor.make("[!]").yellow_bg().black_bg().blink_fast() + " " + AnsiColor.make("FAILED").bold().yellow_fg());
 * </pre>
 * </p>
 * </li>
 * </ul>
 *
 * @author Jack Meng
 * @version 1.0
 * @see StringBuilder
 * @see InternedInstance
 */
public final class AnsiColor
{
	/**
	 * Determines if the ANSI printer should return the ANSI formatted or not.
	 * If {@link #setAnsiEnabled()} returns false, raw content would be returned
	 * without formatting.
	 */
	private static boolean enableInternedInstance=true;
	private final StringBuilder $ansi_content=new StringBuilder(); // set to empty string for now
	
	private AnsiColor()
	{
	}
	
	/**
	 * The current version
	 *
	 * @return A long in the format of YYYYMMDDVV. Where YYYY -> Four digit
	 * year, MM -> 2 digit month number, DD -> 2 digit day number, VV -> for
	 * version numbering like 1.1 would be "11"
	 */
	public static long getVersion()
	{
		return 2023_06_13_1_1L;
	}
	
	/**
	 * Whether to use ANSI or not
	 *
	 * @param e true = on, false = off
	 */
	public static synchronized void setAnsiEnabled(boolean e)
	{
		AnsiColor.enableInternedInstance=e;
	}
	
	/**
	 * The current state of using ANSI or not
	 *
	 * @return To use ansi or not
	 */
	public static boolean setAnsiEnabled()
	{
		return AnsiColor.enableInternedInstance;
	}
	
	/**
	 * Hard coded RESET the renders
	 *
	 * @return "\033[0;m"
	 */
	public static String reset()
	{
		return "\033[0m";
	}
	
	/**
	 * Use this to grab an ANSI create instance. Primarily used if you want to
	 * submit your payload before formatting such that the semantics are like
	 * so: "make(content).color1().bold1()...toString()"
	 * <p>
	 * Which one you use is up to personal preferences, there are no differences
	 * except for looks (or is it?). Contrary to {@link #make()}
	 *
	 * @return An ANSI create instance
	 */
	public static InternedInstance make(String content)
	{
		return new InternedInstance(content);
	}
	
	/**
	 * Use this to grab an ANSI create instance. Primarily used if you want to
	 * submit your payload <strong>AFTER</strong> formatting such that the
	 * semantics are like so: "make().color1().bold1()...toString(content)"
	 * <p>
	 * Which one you use is up to personal preferences, there are no differences
	 * except for looks (or is it?). Contrary to {@link #make(String)}
	 *
	 * @return An ANSI create instance
	 */
	public static InternedInstance make()
	{
		return new InternedInstance();
	}
	
	/**
	 * <p>
	 * This class is where all of the formatting happens. Acquire an instance
	 * via {@link AnsiColor#make()} or {@link AnsiColor#make(String)} where you
	 * can use cascading to create formatting.
	 * </p>
	 * <p>
	 * For 4bit colors you should check out: <a href=
	 * "https://i.stack.imgur.com/9UVnC.png">https://i.stack.imgur.com/9UVnC.png</a>
	 * </p>
	 * <strong>[!] You must always be aware that colors look different
	 * depending on the system!</strong>
	 *
	 * @author Jack Meng
	 */
	public static final class InternedInstance
	{
		private final AnsiColor instance;
		private final StringBuilder content;
		
		/**
		 * If the user provides no String, then we assume that the payload (i.e.
		 * content) will be submitted later.
		 */
		private InternedInstance()
		{
			this("");
		}
		
		private InternedInstance(String content)
		{
			this.content=new StringBuilder(Objects.requireNonNull(content));
			this.instance=new AnsiColor();
		}
		
		/**
		 * <p>
		 * Get what is currently treated as raw content in this builder. Raw
		 * content signifies anything that is TO BE FORMATTED and not the things
		 * that do the formatting.
		 * </p>
		 * <strong>[!] THIS METHOD DESTROYS CASCADING [!]</strong>
		 *
		 * @return The raw text to format
		 */
		public String content()
		{
			return this.content.toString();
		}
		
		// ########### START CONTENT MODIFY ########### //
		
		/**
		 * Extracted method
		 */
		public InternedInstance append(boolean b)
		{
			content.append(b);
			return this;
		}
		
		/**
		 * Extracted method
		 */
		public InternedInstance append(char b)
		{
			content.append(b);
			return this;
		}
		
		/**
		 * Extracted method
		 */
		public InternedInstance append(char[] b)
		{
			content.append(b);
			return this;
		}
		
		/**
		 * Extracted method
		 */
		public InternedInstance append(char[] b,int offset,int len)
		{
			content.append(b,offset,len);
			return this;
		}
		
		/**
		 * Extracted method
		 */
		public InternedInstance append(CharSequence b)
		{
			content.append(b);
			return this;
		}
		
		/**
		 * Extracted method
		 */
		public InternedInstance append(CharSequence b,int start,int end)
		{
			content.append(b,start,end);
			return this;
		}
		
		/**
		 * Extracted method
		 */
		public InternedInstance append(double b)
		{
			content.append(b);
			return this;
		}
		
		/**
		 * Extracted method
		 */
		public InternedInstance append(float b)
		{
			content.append(b);
			return this;
		}
		
		/**
		 * Extracted method
		 */
		public InternedInstance append(int b)
		{
			content.append(b);
			return this;
		}
		
		/**
		 * Extracted method
		 */
		public InternedInstance append(long b)
		{
			content.append(b);
			return this;
		}
		
		/**
		 * Extracted method
		 */
		public InternedInstance append(Object b)
		{
			content.append(b);
			return this;
		}
		
		/**
		 * Extracted method
		 */
		public InternedInstance append(String b)
		{
			content.append(b);
			return this;
		}
		
		/**
		 * Extracted method
		 */
		public InternedInstance append(StringBuffer b)
		{
			content.append(b);
			return this;
		}
		
		/**
		 * Extracted method
		 */
		public InternedInstance delete(int start,int end)
		{
			content.delete(start,end);
			return this;
		}
		
		/**
		 * Extracted method
		 */
		public StringBuilder deleteCharAt(int index)
		{
			return content.deleteCharAt(index);
		}
		
		/**
		 * Extracted method
		 *
		 * <p>
		 * <strong>[!] This method destroys cascading! [!]</strong>
		 * </p>
		 */
		public void getChars(int srcBegin,int srcEnd,char[] dst,int dstBegin)
		{
			content.getChars(srcBegin,srcEnd,dst,dstBegin);
		}
		
		/**
		 * Extracted method
		 * <p>
		 * <strong>[!] This method destroys cascading! [!]</strong>
		 * </p>
		 */
		public int indexOf(String str)
		{
			return content.indexOf(str);
		}
		
		/**
		 * Extracted method
		 * <p>
		 * <strong>[!] This method destroys cascading! [!]</strong>
		 * </p>
		 */
		public int indexOf(String str,int fromIndex)
		{
			return content.indexOf(str,fromIndex);
		}
		
		/**
		 * Extracted method
		 */
		public InternedInstance insert(int offset,boolean b)
		{
			content.insert(offset,b);
			return this;
		}
		
		/**
		 * Extracted method
		 */
		public InternedInstance insert(int offset,char b)
		{
			content.insert(offset,b);
			return this;
		}
		
		/**
		 * Extracted method
		 */
		public InternedInstance insert(int offset,char[] b)
		{
			content.insert(offset,b);
			return this;
		}
		
		/**
		 * Extracted method
		 */
		public InternedInstance insert(int index,char[] b,int offset,int len)
		{
			content.insert(index,b,offset,len);
			return this;
		}
		
		/**
		 * Extracted method
		 */
		public InternedInstance insert(int dstOffset,CharSequence s)
		{
			content.insert(dstOffset,s);
			return this;
		}
		
		/**
		 * Extracted method
		 */
		public InternedInstance insert(
			int dstOffset,CharSequence s,int start,int end
		)
		{
			content.insert(dstOffset,s,start,end);
			return this;
		}
		
		/**
		 * Extracted method
		 */
		public InternedInstance insert(int offset,double b)
		{
			content.insert(offset,b);
			return this;
		}
		
		/**
		 * Extracted method
		 */
		public InternedInstance insert(int offset,float b)
		{
			content.insert(offset,b);
			return this;
		}
		
		/**
		 * Extracted method
		 */
		public InternedInstance insert(int offset,int b)
		{
			content.insert(offset,b);
			return this;
		}
		
		/**
		 * Extracted method
		 */
		public InternedInstance insert(int offset,long b)
		{
			content.insert(offset,b);
			return this;
		}
		
		/**
		 * Extracted method
		 */
		public InternedInstance insert(int offset,Object b)
		{
			content.insert(offset,b);
			return this;
		}
		
		/**
		 * Extracted method
		 */
		public InternedInstance insert(int offset,String b)
		{
			content.insert(offset,b);
			return this;
		}
		
		/**
		 * Extracted method
		 * <p>
		 * <strong>[!] This method destroys cascading! [!]</strong>
		 * </p>
		 */
		public int lastIndexOf(String str)
		{
			return content.lastIndexOf(str);
		}
		
		/**
		 * Extracted method
		 * <p>
		 * <strong>[!] This method destroys cascading! [!]</strong>
		 * </p>
		 */
		public int lastIndexOf(String str,int fromIndex)
		{
			return content.lastIndexOf(str,fromIndex);
		}
		
		/**
		 * Extracted method
		 * <p>
		 * <strong>[!] This method destroys cascading! [!]</strong>
		 * </p>
		 */
		public int length()
		{
			return content.length();
		}
		
		/**
		 * Extracted method
		 */
		public InternedInstance replace(int start,int end,String str)
		{
			content.replace(start,end,str);
			return this;
		}
		
		/**
		 * Extracted method
		 */
		public InternedInstance reverse()
		{
			content.reverse();
			return this;
		}
		
		/**
		 * Extracted method
		 */
		public InternedInstance setCharAt(int index,char ch)
		{
			content.setCharAt(index,ch);
			return this;
		}
		
		/**
		 * Extracted method
		 */
		public InternedInstance setLength(int newLength)
		{
			content.setLength(newLength);
			return this;
		}
		
		/**
		 * Extracted method
		 * <p>
		 * <strong>[!] This method destroys cascading! [!]</strong>
		 * </p>
		 */
		public CharSequence subSequence(int start,int end)
		{
			return content.subSequence(start,end);
		}
		
		/**
		 * Extracted method
		 * <p>
		 * <strong>[!] This method destroys cascading! [!]</strong>
		 * </p>
		 */
		public String substring(int start)
		{
			return content.substring(start);
		}
		
		/**
		 * Extracted method
		 * <p>
		 * <strong>[!] This method destroys cascading! [!]</strong>
		 * </p>
		 */
		public String substring(int start,int end)
		{
			return content.substring(start,end);
		}
		
		/**
		 * Extracted method
		 */
		public InternedInstance trimToSize()
		{
			content.trimToSize();
			return this;
		}
		
		/**
		 * Calls {@link #black_fg()}
		 */
		public InternedInstance black()
		{
			return black_fg();
		}
		
		// ########### END CONTENT MODIFY ########### //
		
		// ########### START 4 BIT COLORS ########### //
		
		public InternedInstance black_fg()
		{
			return make("30");
		}
		
		/**
		 * ANSI code maker. Constructs the ANSI code given and provides further
		 * cascading simplifications.
		 *
		 * @param content ANSI Code
		 *
		 * @return Cascaded instance
		 */
		private InternedInstance make(String content)
		{
			instance.$ansi_content
				.append(content)
				.append(";");
			return this;
		}
		
		public InternedInstance black_bg()
		{
			return make("40");
		}
		
		/**
		 * Calls {@link #red_fg()}
		 */
		public InternedInstance red()
		{
			return red_fg();
		}
		
		public InternedInstance red_fg()
		{
			return make("31");
		}
		
		public InternedInstance red_bg()
		{
			return make("41");
		}
		
		/**
		 * Calls {@link #green_fg()}
		 */
		public InternedInstance green()
		{
			return green_fg();
		}
		
		public InternedInstance green_fg()
		{
			return make("32");
		}
		
		public InternedInstance green_bg()
		{
			return make("42");
		}
		
		/**
		 * Calls {@link #yellow_fg()}
		 */
		public InternedInstance yellow()
		{
			return yellow_fg();
		}
		
		public InternedInstance yellow_fg()
		{
			return make("33");
		}
		
		public InternedInstance yellow_bg()
		{
			return make("43");
		}
		
		/**
		 * Calls {@link #blue_fg()}
		 */
		public InternedInstance blue()
		{
			return blue_fg();
		}
		
		public InternedInstance blue_fg()
		{
			return make("34");
		}
		
		public InternedInstance blue_bg()
		{
			return make("43");
		}
		
		/**
		 * Calls {@link #magenta_fg()}
		 */
		public InternedInstance magenta()
		{
			return magenta_fg();
		}
		
		public InternedInstance magenta_fg()
		{
			return make("35");
		}
		
		public InternedInstance magenta_bg()
		{
			return make("45");
		}
		
		/**
		 * Calls {@link #cyan_fg()}
		 */
		public InternedInstance cyan()
		{
			return cyan_fg();
		}
		
		public InternedInstance cyan_fg()
		{
			return make("36");
		}
		
		public InternedInstance cyan_bg()
		{
			return make("46");
		}
		
		/**
		 * NOTE: On certain renderers, white_fg and white_bg will return a GRAY
		 * color, while on some it returns WHITE! Calls {@link #white_fg()}
		 */
		public InternedInstance gray()
		{
			return white_fg();
		}
		
		public InternedInstance white_fg()
		{
			return make("37");
		}
		
		public InternedInstance white_bg()
		{
			return make("47");
		}
		
		/**
		 * NOTE: On certain renderers, bright_black_fg and bright_black_bg will
		 * return the same colors as black_fg and black_bg respectively. Calls
		 * {@link #bright_black_fg()}
		 */
		public InternedInstance dark_gray()
		{
			return bright_black_fg();
		}
		
		public InternedInstance bright_black_fg()
		{
			return make("90");
		}
		
		public InternedInstance bright_black_bg()
		{
			return make("100");
		}
		
		/**
		 * Calls {@link #bright_red_fg()}
		 */
		public InternedInstance bright_red()
		{
			return bright_red_fg();
		}
		
		public InternedInstance bright_red_fg()
		{
			return make("91");
		}
		
		public InternedInstance bright_red_bg()
		{
			return make("101");
		}
		
		/**
		 * Calls {@link #bright_green_fg()}
		 */
		public InternedInstance bright_green()
		{
			return bright_green_fg();
		}
		
		public InternedInstance bright_green_fg()
		{
			return make("92");
		}
		
		public InternedInstance bright_green_bg()
		{
			return make("102");
		}
		
		/**
		 * Calls {@link #bright_yellow_fg()}
		 */
		public InternedInstance bright_yellow()
		{
			return bright_yellow_fg();
		}
		
		public InternedInstance bright_yellow_fg()
		{
			return make("93");
		}
		
		public InternedInstance bright_yellow_bg()
		{
			return make("103");
		}
		
		/**
		 * Calls {@link #bright_blue_fg()}
		 */
		public InternedInstance bright_blue()
		{
			return bright_blue_fg();
		}
		
		public InternedInstance bright_blue_fg()
		{
			return make("94");
		}
		
		public InternedInstance bright_blue_bg()
		{
			return make("104");
		}
		
		/**
		 * Calls {@link #bright_magenta_fg()}
		 */
		public InternedInstance bright_magenta()
		{
			return bright_magenta_fg();
		}
		
		public InternedInstance bright_magenta_fg()
		{
			return make("95");
		}
		
		public InternedInstance bright_magenta_bg()
		{
			return make("105");
		}
		
		/**
		 * Calls {@link #bright_cyan_fg()}
		 */
		public InternedInstance bright_cyan()
		{
			return bright_cyan_fg();
		}
		
		public InternedInstance bright_cyan_fg()
		{
			return make("96");
		}
		
		public InternedInstance bright_cyan_bg()
		{
			return make("106");
		}
		
		/**
		 * Calls {@link #bright_white_fg()}
		 */
		public InternedInstance white()
		{
			return bright_white_fg();
		}
		
		public InternedInstance bright_white_fg()
		{
			return make("97");
		}
		
		public InternedInstance bright_white_bg()
		{
			return make("107");
		}
		
		// ########### END 4 BIT COLORS ########### //
		
		// ########### START FONT EFFECTS ########### //
		
		/**
		 * Just use AnsiColor::reset
		 *
		 * @return InternedInstance object
		 */
		public InternedInstance reset()
		{
			return make("0");
		}
		
		public InternedInstance bold()
		{
			return make("1");
		}
		
		/**
		 * Not widely supported
		 */
		public InternedInstance faint()
		{
			return make("2");
		}
		
		/**
		 * Not widely supported. Some renderers treat this as invert the text
		 */
		public InternedInstance italic()
		{
			return make("3");
		}
		
		public InternedInstance underline()
		{
			return make("4");
		}
		
		public InternedInstance blink_slow()
		{
			return make("5");
		}
		
		public InternedInstance blink_fast()
		{
			return make("6");
		}
		
		/**
		 * Swaps the foreground and background
		 */
		public InternedInstance swap_fg_bg()
		{
			return make("7");
		}
		
		/**
		 * Hides the text. Not widely supported.
		 */
		public InternedInstance hide()
		{
			return make("8");
		}
		
		/**
		 * Does not conceal. Not widely supported.
		 */
		public InternedInstance strikethrough()
		{
			return make("9");
		}
		
		/**
		 * Regular font effects
		 */
		public InternedInstance primary()
		{
			return make("10");
		}
		
		public InternedInstance font_1()
		{
			return make("11");
		}
		
		public InternedInstance font_2()
		{
			return make("12");
		}
		
		public InternedInstance font_3()
		{
			return make("13");
		}
		
		public InternedInstance font_4()
		{
			return make("14");
		}
		
		public InternedInstance font_5()
		{
			return make("15");
		}
		
		public InternedInstance font_6()
		{
			return make("16");
		}
		
		public InternedInstance font_7()
		{
			return make("17");
		}
		
		public InternedInstance font_8()
		{
			return make("18");
		}
		
		public InternedInstance font_9()
		{
			return make("19");
		}
		
		/**
		 * Unsupported.
		 */
		public InternedInstance fraktur()
		{
			return make("20");
		}
		
		/**
		 * Sometimes treated as double underline. Both are not widely
		 * supported.
		 */
		public InternedInstance no_bold()
		{
			return make("21");
		}
		
		/**
		 * No bold or low thickness
		 */
		public InternedInstance normal()
		{
			return make("22");
		}
		
		/**
		 * no_fraktur as well
		 */
		public InternedInstance no_italic()
		{
			return make("23");
		}
		
		/**
		 * Both single and double underlines eliminated
		 */
		public InternedInstance no_underline()
		{
			return make("24");
		}
		
		public InternedInstance no_blink()
		{
			return make("25");
		}
		
		public InternedInstance no_inverse()
		{
			return make("27");
		}
		
		public InternedInstance no_hide()
		{
			return make("28");
		}
		
		public InternedInstance no_strikethrough()
		{
			return make("29");
		}
		
		public InternedInstance framed()
		{
			return make("51");
		}
		
		public InternedInstance encircled()
		{
			return make("52");
		}
		
		public InternedInstance overlined()
		{
			return make("53");
		}
		
		public InternedInstance no_framed_no_encircled()
		{
			return make("54");
		}
		
		public InternedInstance no_overlined()
		{
			return make("55");
		}
		
		// ! 60-65 are almost never supported, so are left out
		
		// ########### END FONT EFFECTS ########### //
		
		// ########### START CONTROL ########### //
		
		public InternedInstance rgb_fg(int[] rgb)
		{
			return rgb_fg(rgb[0],rgb[1],rgb[2]);
		}
		
		public InternedInstance rgb_fg(int r,int g,int b)
		{
			r=Maths.clampInt(0,255,r); // RED | R
			g=Maths.clampInt(0,255,g); // GREEN | G
			b=Maths.clampInt(0,255,b); // BLUE | B
			fg();
			make("2");
			make(r);
			make(g);
			make(b);
			return this;
		}
		
		// ########### END CONTROL ########### //
		
		// ########### START MISC ########### //
		
		/**
		 * Mostly used for internal creation of colors. You should rarely use
		 * this if at all.
		 * <p>
		 * Tells to treat the following elements (in the ANSI code) as a color
		 * for the text's foreground
		 */
		public InternedInstance fg()
		{
			return make("38");
		}
		
		public InternedInstance make(int... codes)
		{
			for(int r: codes)
				make(r+"");
			return this;
		}
		
		public InternedInstance rgb_bg(int[] rgb)
		{
			return rgb_bg(rgb[0],rgb[1],rgb[2]);
		}
		
		public InternedInstance rgb_bg(int r,int g,int b)
		{
			r=Maths.clampInt(0,255,r); // RED | R
			g=Maths.clampInt(0,255,g); // GREEN | G
			b=Maths.clampInt(0,255,b); // BLUE | B
			bg();
			make("2");
			make(r);
			make(g);
			make(b);
			return this;
		}
		
		/**
		 * Mostly used for internal creation of colors. You should rarely use
		 * this if at all.
		 * <p>
		 * Tells to treat the following elements (in the ANSI code) as a color
		 * for the text's background
		 */
		public InternedInstance bg()
		{
			return make("48");
		}
		
		/**
		 * Apply your own custom list of ANSI codes. It is not suggested using
		 * this unless there are codes outside this builder's range.
		 *
		 * @param i ANSI codes (vararg)
		 *
		 * @return Instance for cascading
		 */
		public InternedInstance apply(int... i)
		{
			return make(i);
		}
		
		// ########### END MISC ########### //
		
		/**
		 * Does not provide a RESET at the end as compared to
		 * {@link #toString()} or {@link #toString(String)}
		 *
		 * @return The finalized string without a rest character
		 */
		public String render()
		{
			return "\033["+end()
				.append("m")
				.append(this.content.append(content))
				.toString();
		}
		
		/**
		 * Should not be called by the programmer unless absolutely necessary.
		 * The {@link #toString()} and {@link #escaped()} all call this method
		 * in order to validate the String.
		 * <p>
		 * This method does not create the final values for rendering
		 *
		 * @return String -> A validated String for printing.
		 */
		public StringBuilder end()
		{
			return instance.$ansi_content.deleteCharAt(instance.$ansi_content.lastIndexOf(
				";"));
		}
		
		/**
		 * For debugging purposes. Displays the built value by escaping all the
		 * necessary components.
		 *
		 * @return String
		 */
		public String escaped()
		{
			return "\\033["+end()
				.append("m")
				.append(content.toString())
				.append("\\033[0m")
				.toString();
		}
		
		/**
		 * Uses the default {@link System#out#print()}
		 */
		public InternedInstance print()
		{
			this.print(System.out);
			return this;
		}
		
		public InternedInstance print(PrintStream e)
		{
			e.print(toString());
			return this;
		}
		
		/**
		 * This method also appends a RESET character at the end to reset all
		 * ANSI coloring after.
		 *
		 * Technically this is just a wrapper call to {@link #toString(String)}
		 */
		@Override public String toString()
		{
			return toString(content.toString());
		}
		
		/**
		 * Allows for content to be added at the end (either end). This method
		 * also appends a RESET character at the end to reset all ANSI coloring
		 * after.
		 *
		 * @param content the content to append to the original content before
		 * returning the string
		 *
		 * @return The formatted content
		 */
		public String toString(String content)
		{
			return AnsiColor.setAnsiEnabled()?"\033["+end()
				.append("m")
				.append(this.content.append(content))
				.append(AnsiColor.reset())
				.toString():content;
		}
		
		/**
		 * Uses the default {@link System#out#println()}
		 */
		public InternedInstance println()
		{
			this.println(System.out);
			return this;
		}
		
		public InternedInstance println(PrintStream e)
		{
			e.println(toString());
			return this;
		}
		
		/**
		 * Use for end cascading payload submission
		 *
		 * @param ps Suggested {@link PrintStream} to use
		 * @param content The payload
		 *
		 * @return The current instance
		 *
		 * @since 1.1
		 */
		public InternedInstance println(PrintStream ps,String content)
		{
			ps.println(toString(content));
			return this;
		}
		
		/**
		 * Use for end cascading payload submission
		 *
		 * @param ps Suggested {@link PrintStream} to use
		 * @param content The payload
		 *
		 * @return The current instance
		 *
		 * @since 1.1
		 */
		public InternedInstance print(PrintStream ps,String content)
		{
			ps.print(toString(content));
			return this;
		}
		
		/**
		 * @param content
		 *
		 * @return
		 *
		 * @since 1.1
		 */
		public InternedInstance print(String content)
		{
			System.out.println(toString(content));
			return this;
		}
		
		/**
		 * @param content
		 *
		 * @return
		 *
		 * @since 1.1
		 */
		public InternedInstance println(String content)
		{
			System.out.print(toString(content));
			return this;
		}
		
	}
	
}