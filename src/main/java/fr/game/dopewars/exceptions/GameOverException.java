package fr.game.dopewars.exceptions;

/**
 * Raised on game over event
 */
public class GameOverException extends Exception {

  public GameOverException() {
    super();
  }

  public GameOverException(String m) {
    super(m);
  }
}
