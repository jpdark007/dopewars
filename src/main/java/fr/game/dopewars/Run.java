package fr.game.dopewars;

import java.util.Arrays;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Scanner;

import fr.game.dopewars.events.Event;
import fr.game.dopewars.exceptions.CannotAffordException;
import fr.game.dopewars.exceptions.GameOverException;
import fr.game.dopewars.exceptions.InvalidTradeException;
import fr.game.dopewars.exceptions.OutOfSpaceException;
import fr.game.dopewars.exceptions.QuantityOutOfBoundsException;

public class Run {

	static Game game = Game.getInstance();

	public static void main(String[] args) {

		while (true) {
			Scanner stdin = new Scanner(System.in);
			game.printBoard();
			System.out.println("1 : Buy");
			System.out.println("2 : Sell");
			System.out.println("3 : Move");
			System.out.println("0 : Refresh");
			String userGuess = stdin.next();

			if (userGuess.equals("1")) {
				buyProduct();
			} else if (userGuess.equals("2")) {
				sellProduct();
			} else if (userGuess.equals("3")) {
				try {
					moveToLocation();
				} catch (GameOverException e) {
					logMessage(e.getMessage());
					break;
				}
			} else if (userGuess.equals("0")) {
				game.printBoard();
			} else if (userGuess.equals("q")) {
				break;
			}
		}
	}

	private static void moveToLocation() throws GameOverException {
		Scanner stdin = new Scanner(System.in);
		System.out.println(Arrays.toString(game.getLocations()));
		int location = stdin.nextInt();
		while (location > game.getLocations().length) {
			location = stdin.nextInt();
		}
		Location l = game.getLocations()[location];

		if (game.moveTo(l)) {
			logMessage(String.format("Day %d: Moved to %s", game.getDay(), l.getName()));
			handleGameEvents();
		}

	}

	private static void buyProduct() {
		// BUY
		Scanner stdin = new Scanner(System.in);
		System.out.println(Arrays.toString(game.getProducts()));
		System.out.println("Product ? : ");
		int productAsk = stdin.nextInt();
		System.out.println("Quantity ? : ");
		int quantityAsk = stdin.nextInt();

		Product product = game.getProducts()[productAsk];
		Trade trade = new Trade(product, product.getPrice());

		trade.setMode(Trade.Mode.BUY);
		trade.setMax(game.getPlayer().getMaxPurchase(product));

		game.setCurrentTrade(trade);

//		int quantity = quantityAsk > trade.getMax() ? trade.getMax() : quantityAsk;
		trade.setQuantity(quantityAsk);
		try {
			trade.commit();

			String logMsg = String.format("You bought %d \u00D7 %s at %s!", quantityAsk, trade.getProduct().getName(),
					Game.CURRENCY_FORMATTER.format(trade.getPrice()));
			logMessage(logMsg);
		} catch (OutOfSpaceException ex) {
			logMessage("You cannot fit this many in your coat!");
		} catch (CannotAffordException ex) {
			logMessage("You cannot afford this many!");
		} catch (QuantityOutOfBoundsException ex) {
			logMessage("You do not have that much to sell!");
		} catch (InvalidTradeException ex) {
			logMessage(ex.getMessage());
		}
	}

	private static void sellProduct() {
		// SELL
		Scanner stdin = new Scanner(System.in);
		game.printStash();

		System.out.println("Product ? : ");
		int productAsk = stdin.nextInt();
		System.out.println("Quantity ? : ");
		int quantityAsk = stdin.nextInt();
		
		//Find choosen product in stash
		
		Map<Product, Map<Long, Integer>> map = game.getPlayer().getProducts();
		int i = 0;
		Product product = null;
		long buyingPrice = 1L;
//		int quantity = 0;
		search: for (Map.Entry<Product, Map<Long, Integer>> entry : map.entrySet()) {
			Map<Long, Integer> stock = entry.getValue();
			for (Entry<Long, Integer> oneProduct : stock.entrySet()) {
				if (i == productAsk) {
					product = entry.getKey();
					buyingPrice = oneProduct.getKey();
//					quantity = oneProduct.getValue();
					break search;
				}
				++i;
			}
		}

		Trade trade = new Trade(product, buyingPrice);

		trade.setMode(Trade.Mode.SELL);
		trade.setMax(game.getPlayer().quantityForProduct(product, buyingPrice));
		game.setCurrentTrade(trade);

		trade.setQuantity(quantityAsk);
		try {
			trade.commit();

			long price = trade.getPrice();
			//Manage with product found on the floor
			if (0 == price) {
				price = product.getPrice();
			}
			long salePrice = price * quantityAsk;
			long profit = (trade.getProduct().getPrice() - price) * quantityAsk;
			
			String logMsg = String.format("You sold %d \u00D7 %s for %s, making %s!", quantityAsk,
					trade.getProduct().getName(), Game.CURRENCY_FORMATTER.format(salePrice),
					Game.CURRENCY_FORMATTER.format(profit));
			logMessage(logMsg);
		} catch (OutOfSpaceException ex) {
			logMessage("You cannot fit this many in your coat!");
		} catch (CannotAffordException ex) {
			logMessage("You cannot afford this many!");
		} catch (QuantityOutOfBoundsException ex) {
			logMessage("You do not have that much to sell!");
		} catch (InvalidTradeException ex) {
			logMessage(ex.getMessage());
		}
	}

	public static void handleGameEvents() {
		boolean b = true;

		Message message;
		for (Event e : game.getEvents()) {
			if (e.requiresInput()) {
				Scanner stdin = new Scanner(System.in);
				System.out.println(e.getMessage());
				System.out.println("1 : Yes");
				System.out.println("2 : No");
				String userGuess = stdin.nextLine();
				if (userGuess.equals("1")) {
					b = true;
				} else if (userGuess.equals("2")) {
					b = false;
				}
			}

			message = e.handleEvent(b);
			logMessage(message.getMessage());
		}

		if (game.inFight()) {
			displayFightDialog();
		}
	}

	private static void logMessage(String message) {
		if (message != null) {
			System.out.println(message);
		}
	}

	private static void displayFightDialog() {
		Fight f = game.getFight();
		Npc npc = f.getNpc();
		Player p = game.getPlayer();
		Message m = null;

		while (!f.fightOver()) {
			updateFightMeters(p, npc);
			Scanner stdin = new Scanner(System.in);
			System.out.println("1 : Fight");
			System.out.println("2 : Run");
			String userGuess = stdin.nextLine();

			if (userGuess.equals("1")) {
				m = f.attack();
			} else if (userGuess.equals("2")) {
				m = f.run();
			}
			logMessage(m.getMessage());
		}
	}

	private static void updateFightMeters(final Player p, final Npc npc) {
		System.out.println(p.getName() + ":" + p.getHealth() + "/" + p.getMaxHealth());
		System.out.println(npc.getName() + ":" + npc.getHealth() + "/" + npc.getMaxHealth());
	}

}
