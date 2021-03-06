package fr.game.dopewars;

import fr.game.dopewars.events.*;
import fr.game.dopewars.exceptions.*;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;
import java.util.Map.Entry;

import com.thoughtworks.xstream.XStream;

/**
 * Main dopewars game class. Holds state for the game
 * 
 * @see http://gotgoodlist.com/dopewars-prices-stats-and-facts for some changes
 *      data from the original dopewars
 */
public final class Game implements TraderConstants {

	static private Game instance = null;

	static private Product[] products;
	// final static private Product[] products = { new Product("Acid", 1000,
	// 4500), new Product("Cocaine", 15000, 30000),
	// new Product("Hashish", 450, 1500), new Product("Heroin", 5000, 14000),
	// new Product("Ecstacy", 10, 60),
	// new Product("Smack", 1500, 4500), new Product("Opium", 500, 1300), new
	// Product("Crack", 1000, 3500),
	// new Product("Peyote", 100, 700), new Product("Shrooms", 600, 1400), new
	// Product("Speed", 70, 250),
	// new Product("Weed", 300, 900) };
	 static private Location[] locations;
//	final static private Location[] locations = { new Location("Coney Island", products),
//			new Location("Manhattan", products), new Location("The Bronz", products), new Location("SoHo", products),
//			new Location("The Village", products) };
	 static private Npc[] npcs;
//	final static private Npc[] npcs = { new Npc("Street Thug", 20, 20, 5, 2000, 5000),
//			new Npc("Officer Hardass", 50, 25, 0, 10000, 20000), new Npc("Rival Dealer", 80, 25, 2, 50000, 75000, 2),
//			new Npc("Robocop", 300, 40, 0, 160000, 250000, 3) };
	private Player player;
	private Location location;
	private Trade currentTrade;
	private int day;
	private Event[] gameEvents;
	private ArrayList<Event> events;
	private ArrayList<Message> messages;
	private Fight fight;

	static public Game getInstance() {
		if (instance == null) {
			instance = new Game();
		}

		return instance;
	}

	private Game() {
		events = new ArrayList<Event>();
		messages = new ArrayList<Message>();
		day = 0;

		loadProducts();
		loadLocations();
		loadNpcs();

		player = new Player("You");
		location = locations[0];

		setupEvents();
		updatePrices();
	}
	
	private static void loadNpcs() {
		FileReader fileReader;
		XStream xstream = new XStream();
		xstream.alias("Npc", Npc.class);
		try {
			fileReader = new FileReader("npcs.xml");
			npcs = (Npc[]) xstream.fromXML(fileReader);
			fileReader.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}


	private static void loadLocations() {
		FileReader fileReader;
		XStream xstream = new XStream();
		xstream.alias("Location", Location.class);
		try {
			fileReader = new FileReader("locations.xml");
			List<Location> locationsLoad =  (List<Location>) xstream.fromXML(fileReader);
			fileReader.close();
			List<Location> locationsTmp = new ArrayList<Location>();
			for (Location location : locationsLoad) {
				locationsTmp.add(new Location(location.getName(), products));
			}
			locations = locationsTmp.toArray(new Location[locationsTmp.size()]);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private static void loadProducts() {
		FileReader fileReader;
		XStream xstream = new XStream();
		xstream.alias("Product", Product.class);
		try {
			fileReader = new FileReader("products.xml");
			products = (Product[]) xstream.fromXML(fileReader);
			fileReader.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void checkGameConditions() throws GameOverException {

		if (day > DAYS_IN_GAME) {
			throw new GameOverException("Time's up!");
		}

		if (getPlayer().getCash() < 0) {
			throw new GameOverException("You're broke!");
		}

		if (!getPlayer().isAlive()) {
			throw new GameOverException("You're dead!");
		}
	}

	public Trade getCurrentTrade() {
		return currentTrade;
	}

	public void setCurrentTrade(Trade trade) {
		this.currentTrade = trade;
	}

	public int getDay() {
		return day;
	}

	public boolean moveTo(Location location) throws GameOverException {
		boolean moved = false;

		if (this.location != location) {
			events.clear();
			messages.clear();
			this.location = location;
			runGameEvents();
			day++;
			moved = true;
			checkGameConditions();
		}

		return moved;
	}

	public Location[] getLocations() {
		return locations;
	}

	public Npc[] getNpcs() {
		return npcs;
	}

	public Location getLocation() {
		return location;
	}

	public ArrayList<Event> getEvents() {
		return events;
	}

	public Player getPlayer() {
		return player;
	}

	public Product[] getProducts() {
		return products;
	}

	public ArrayList<Message> getMessages() {
		return messages;
	}

	public void addMessage(Message message) {
		messages.add(message);
	}

	public void runGameEvents() {
		updatePrices();
		for (Event e : gameEvents) {
			if (e.inEvent()) {
				events.add(e);
			}
		}
	}

	public void updatePrices() {
		Message m;
		for (Product p : products) {
			p.updatePrice();
			if ((m = p.getMessage()) != null) {
				addMessage(m);
			}
		}
	}

	public Fight getFight() {
		return fight;
	}

	public boolean inFight() {
		return (fight != null);
	}

	public void fightOver() {
		fight = null;
	}

	public int daysLeft() {
		return (DAYS_IN_GAME - day);
	}

	public Fight startFight() {
		ArrayList<Npc> available = new ArrayList<Npc>();

		for (Npc n : npcs) {
			if (n.getLevel() <= player.getLevel()) {
				available.add(n);
			}
		}

		int i = (int) (Math.random() * available.size());
		return startFight(available.get(i));
	}

	public Fight startFight(Npc npc) {
		npc.reset();
		this.fight = new Fight(npc);

		return getFight();
	}

	/**
	 * setup the events that make the game "fun" these need to be here since we
	 * cannot get proper references without initializing first
	 */
	private void setupEvents() {
		this.gameEvents = new Event[] { new ArmorEvent(this), new AutoHealEvent(this), new CoatEvent(this),
				new FightEvent(this), new FullHealEvent(this), new GunEvent(this), new LuckyEvent(this),
				new TripEvent(this) };
	}

	public void printBoard() {
		System.out.println(getLocation());
		System.out.print(player.getCashValue());
		System.out.print(" Space : " + player.spaceHeld() + "/" + player.getSpace());
		System.out.print(" Health : " + player.getHealth() + "/" + player.getMaxHealth());
		System.out.print(" Days : " + daysLeft() + "/" + Game.DAYS_IN_GAME);
		System.out.println();
		System.out.println("Products : ");
		for (Product product : getProducts()) {
			System.out.print(product + " ");
		}
		System.out.println();
		printStash();
		System.out.println(getMessages());
	}

	public void printStash() {
		System.out.println("Stash : ");
		Map<Product, Map<Long, Integer>> map = getPlayer().getProducts();
		int i = 0;
		for (Map.Entry<Product, Map<Long, Integer>> entry : map.entrySet()) {
			Map<Long, Integer> stock = entry.getValue();
			for (Entry<Long, Integer> oneProduct : stock.entrySet()) {
				System.out.println(i++ + " / " + entry.getKey() + " Price :" + oneProduct.getKey() + "/ Quantity :"
						+ oneProduct.getValue());
			}
		}
	}
}
