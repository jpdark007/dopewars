package fr.game.dopewars.events;

import fr.game.dopewars.Game;
import fr.game.dopewars.Message;
import fr.game.dopewars.Player;
import fr.game.dopewars.TraderConstants;

/**
 * Base event class
 * @author rob
 */
public abstract class Event implements TraderConstants {
  protected Game game;
  protected Player player;
  protected Message message;
          
  public Event(Game game){
    this.game = game;
    player = game.getPlayer();
  }
  
  public Message getMessage(){
    return message;
  }

  public boolean hit(int sides){
    int r = (int)(Math.random() * sides) + 1;
    return (r == 1);
  }
  
  public abstract boolean inEvent();
  abstract public Message handleEvent(boolean yes);
  abstract public boolean requiresInput();
}
