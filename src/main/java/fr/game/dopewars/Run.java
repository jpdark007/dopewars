package fr.game.dopewars;

import java.util.Arrays;
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

		Scanner stdin = new Scanner(System.in);
		while (true) {
			game.printBoard();
			System.out.println("1 : Buy");
			System.out.println("2 : Sell");
			System.out.println("3 : Move");
			System.out.println("0 : Refresh");
			String userGuess = stdin.next();

			if (userGuess.equals("1")) {
				// BUY
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

				int quantity = quantityAsk > trade.getMax() ? trade.getMax() : quantityAsk;
				trade.setQuantity(quantity);
				try {
					trade.commit();

					String logMsg = String.format("You bought %d \u00D7 %s at %s!", quantity,
							trade.getProduct().getName(), Game.CURRENCY_FORMATTER.format(trade.getPrice()));
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
			} else if (userGuess.equals("2")) {
				// SELL
				System.out.println("Stash : " + game.getPlayer().getProducts());
				System.out.println("Product ? : ");
				int productAsk = stdin.nextInt();
				System.out.println("Quantity ? : ");
				int quantityAsk = stdin.nextInt();
				Product product = (Product) game.getPlayer().getProducts().values().toArray()[productAsk];

				Trade trade = new Trade(product, product.getPrice());

				trade.setMode(Trade.Mode.SELL);
				trade.setMax(game.getPlayer().getMaxPurchase(product));
				game.setCurrentTrade(trade);

				int quantity = quantityAsk > trade.getMax() ? trade.getMax() : quantityAsk;

				long price = trade.getPrice();
				long salePrice = price * quantity;

				long profit = (trade.getProduct().getPrice() - price) * quantity;
				String logMsg = String.format("You sold %d \u00D7 %s for %s, making %s!", quantity,
						trade.getProduct().getName(), Game.CURRENCY_FORMATTER.format(salePrice),
						Game.CURRENCY_FORMATTER.format(profit));
				logMessage(logMsg);
			} else if (userGuess.equals("3")) {
				System.out.println(Arrays.toString(game.getLocations()));
				int location = stdin.nextInt();
				while (location > game.getLocations().length) {
					location = stdin.nextInt();
				}
				Location l = game.getLocations()[location];
				try {
					if (game.moveTo(l)) {
						logMessage(String.format("Day %d: Moved to %s", game.getDay(), l.getName()));
						handleGameEvents();
					}
				} catch (GameOverException e) {
					logMessage(e.getMessage());
					break;
				}
			} else if (userGuess.equals("0")) {
				game.printBoard();
			} else if (userGuess.equals("q")) {
				stdin.close();
				break;
			}
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
