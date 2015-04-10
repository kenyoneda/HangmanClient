import ClientServer.*;
import java.net.*;
import java.io.*;
import java.util.*;

/**
 * This class contains the logic of a hangman game.
 * <p>
 * Since this class is a derived class of
 * <code>SimpleSocketClient</code>, the main action is in its
 * <code>handleSession()</code> method. Said method will contact a
 * Hangman server, and obtain a word from the server. It will then
 * interact with the player, until one of the two things has happened:
 * <ul>
 * <li> The player has guessed the word. In this case, the player is
 * congratulated.</li>
 * <li> The player has made too many bad guesses. In this case, the
 * player is "punished".</li>
 * </ul> <p>
 * Those methods that actually interact with the player are left as
 * abstract methods. A concrete subclass of
 * <code>AbstractHangmanClient</code> can then interact with the
 * player as it sees fit. Hence, one might have a text-based
 * interface, a graphical interface, perhaps even an audio interface.
 *
 * @author Ken Yoneda
 * @version 1.0, 8 May 2014
 */

public abstract class AbstractHangmanClient
  extends ClientServer.SimpleSocketClient
{
  /**
   * the default host on which a Hangman server is running
   */
  protected static String HANGMAN_DEFAULT_SERVER = "erdos.dsm.fordham.edu";

  /**
   * the default Hangman port number
   */
  protected static int HANGMAN_DEFAULT_PORT = 9999;

  /**
   * how many guesses remain
   */
  protected int guessesRemaining;

  /**
   * the word to be guessed by the player
   */
  protected String theWord;

  /**
   * the word as determined so far by the player's letter choices
   */
  protected StringBuffer wordSoFar;

  /**
   * first or subsequent time through
   */
  protected boolean alreadyPlayed = false;


  protected String lettersGuessed;

  /**
   * The constructor for the <code>AbstractHangmanClient</code> class.
   * 
   * @param debugging True iff debugging output is enabled
   * @param hostName The host on which the hangmanserver resides
   * @param portNumber The port number on which the server is listening
   */
  public AbstractHangmanClient
    (boolean debugging, String hostName, int portNumber) {
    super(hostName, portNumber);
    debugOn = debugging;
    this.start();
  }

  /**
   * This method overrides the SimpleSocketClient.handleSession()
   * method.
   *
   * As mentioned in the introductory section, this method handles the
   * main logic of a hangman game. It uses a fairly simple finite
   * state machine to do this.
   */
  public void handleSession() {
    while (true) {
      startNewGame();
      while (guessesRemaining > 0) {
	processGuess();
	displayGame();
	if (wordSoFar.indexOf("*") == -1)
	  break;
      }
      handleEndGame();
      if (!elicitPlayAgain())
	break;
    }
  }

  /**
   * Start a new game.
   *
   * This involves contacting the server, finding out the length of
   * the new word, intializing our guess for what the new word looks
   * like, and displaying the latter.
   */
  void startNewGame() {
    DataInputStream remoteDIS = new DataInputStream(remoteInputStream);
    InputStreamReader remoteISR = new InputStreamReader(remoteDIS);
    SafeBufferedReader remoteBR = new SafeBufferedReader(remoteISR);

    String fromServer = new String();
    int wordLength = 0;
  
    try {
      remoteOutputStream.writeBytes("NEW" + "\n");
      if (!alreadyPlayed) {
	fromServer = remoteBR.readLine();
	fromServer = remoteBR.readLine();
      }
      fromServer = remoteBR.readLine();
      wordLength = Integer.parseInt(fromServer);
      System.out.println("Word length is: " + wordLength);
    }	
      
    catch (IOException ex) {
      System.err.println(ex);
      return;
    }
  
    guessesRemaining = 10;
  
    wordSoFar = new StringBuffer();
    for (int i = 0; i < wordLength; i++)
      wordSoFar.append("*");

    displayGame();
  }

  /**
   * Process a valid (A..Z) guess from the player.
   *
   * We send the guess to the server. The server tells us at what
   * positions the guess matches the word. Our knowledge of the word
   * is updated appropriately. If we have guessed the word, the
   * player is congratulated. If there are no matches, the number of
   * remaining guesses is decremented, and the player is punished if
   * there are no remaining guesses.
   */
  void processGuess() {
    try {    
      char guess = elicitGuess();
      remoteOutputStream.writeBytes("GUESS " + guess + "\n");

      String guessResponse;
      boolean correctGuess = false;
	
      DataInputStream remoteDIS = new DataInputStream(remoteInputStream);
      InputStreamReader remoteISR = new InputStreamReader(remoteDIS);
      SafeBufferedReader remoteBR = new SafeBufferedReader(remoteISR);
      
      for (int i = 0; i < wordSoFar.length(); i++) {
	guessResponse = remoteBR.readLine();
	if (guessResponse.contains("true")) {
	  wordSoFar.setCharAt(i, guess);
	  correctGuess = true;
	}
      }
      if (!correctGuess)
	guessesRemaining--;
    }
    catch (IOException ex) {
      System.err.println(ex);
      return;
    }
  }

  /**
   * Handle the end of game (either a winner or a loser)
   */
  void handleEndGame() {
    if (wordSoFar.indexOf("*") == -1)
      congratulateWinner();
    if (guessesRemaining == 0) {
      try {
	DataInputStream remoteDIS = new DataInputStream(remoteInputStream);
	InputStreamReader remoteISR = new InputStreamReader(remoteDIS);
	SafeBufferedReader remoteBR = new SafeBufferedReader(remoteISR);
	remoteOutputStream.writeBytes("QUIT" + "\n");
	theWord = remoteBR.readLine();
	punishLoser();
      }
      
      catch (IOException ex) {
	System.err.println(ex);
      }
    }
  }
  
  /**
   * Obtain a guess from the user. This method is abstract, because it
   * depends on the user interface.
   */
  public abstract char elicitGuess();

  /**
   * Display the current game state. This method is abstract, because
   * it depends on the user interface.
   */
  public abstract void displayGame();

  /**
   * Congratulate the winner on his/her acumen. This method is
   * abstract, because it depends on the user interface.
   */
  abstract void congratulateWinner();

  /**
   * Player didn't guess the word; hang him. This method is abstract,
   * because it depends on the user interface.
   */
  abstract void punishLoser();

  /**
   * Find out whether we want to play again. This method is abstract,
   * because it depends on the user interface.
   * @return true or false, according to whether we want to play again
   * or not.
   */
  abstract boolean elicitPlayAgain();
}
