import java.io.*;
import java.util.*;

/**
 * A class that implements the abstract methods of <code>Abstract
 * HangmanClient</code>.
 * <p>
 * It provides a text-based interface, as well as a
 * <code>main()</code> method, giving an executable program.
 *
 * @author Ken Yoneda
 * @version 1.0, 8 May 2014
 */

public class TextHangmanClient extends AbstractHangmanClient
{
  /**
   * The constructor for the <code>TextHangManClient</code> class.
   *
   * @param debugging True iff debugging output is enabled
   * @param serverName The host on which the hangman server resides
   * @param portNumber The port number on which the server is
   * listening
   */
  public TextHangmanClient(boolean debugging, String serverName, 
			   int portNumber)
  {
    super(debugging, serverName, portNumber);
  }

  /**
   * Obtain a guess from the user.
   * @return an uppercase letter for a GUESS
   */
  public char elicitGuess()
  {
    Scanner s = new Scanner(System.in);
    System.out.print("Guess letter? ");
    char c = s.next().charAt(0);
    if (Character.isLowerCase(c))
      c = Character.toUpperCase(c);
    /* String lettersGuessed = new String();
        lettersGuessed += Character.toString(c);
	System.out.print("Letters guessed: " + lettersGuessed);*/
    
    return c;
  }

  /**
   * Display the current game state.
   */
  public void displayGame()
  {
    System.out.println("Guesses remaining: " + guessesRemaining);
    System.out.println(wordSoFar);
  }

  /**
   * Congratulate the winner on his/her acumen.
   */
  public void congratulateWinner()
  {
    System.out.println("Good job.");
  }

  /**
   * Player didn't guess the word; hang him.
   */
  public void punishLoser()
  {
    System.out.println("You are bad at hangman.");
    System.out.println("The word was: " + theWord);
  }

  /**
   * Find out whether we want to play again.
   * @return true or false, according to whether we want to play
   * again.
   */
  public boolean elicitPlayAgain()
  {
    alreadyPlayed = true;

    Scanner s = new Scanner(System.in);
    System.out.print("Play again? (Y/N) ");
    char c = s.next().charAt(0);

    if (c == 'Y' || c == 'y')
      return true;
    else
      return false;
  }

  /**
   * The usual <code>main()</code> function, which gets things
   * rolling.
   *
   * After parsing the command line, it invokes the constructor for
   * this class. Optional command line paramaters:
   * 
   * <ul>
   * <li> Flag <code>-d</code>: enable debug output</li>
   * <li> Flag <code>-h</code>: print help message<li> 
   * <li> Name of alternate Hangman server</li>
   *
   * Any other flags (e.g. -x) will cause the help message to be
   * printed, along with an error exit.
   */
  public static void main(String args[])
  {
    String server = HANGMAN_DEFAULT_SERVER;
    boolean debugging = false;
    int currentArg = 0;                 // current argument number

    if (args.length > 0)                // look for flags
      if (args[0].charAt(0) == '-') {
	currentArg++;
	switch (args[0].charAt(1)) { // flag
	case 'd':
	  debugging = true;
	  break;
	case 'h':                   // ask for help
	  usage();
	  System.exit(0);
	default:                      // illegal flag
	  usage();
	  System.exit(1);
	}
      }

    if (args.length > currentArg)       
      server = args[currentArg];

    new TextHangmanClient(debugging, server, HANGMAN_DEFAULT_PORT);
  }

  /**
   * Print usage message
   */
  public static void usage()
  {
    System.err.println("Usage: HangmanServer [-d] [-h] [server]");
    System.err.println("  -d: print debugging info");
    System.err.println("  -h: print this help msg");
  }
} 
