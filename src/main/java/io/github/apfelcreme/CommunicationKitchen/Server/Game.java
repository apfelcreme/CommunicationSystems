package io.github.apfelcreme.CommunicationKitchen.Server;

import io.github.apfelcreme.CommunicationKitchen.Server.Entities.Ingredient;
import io.github.apfelcreme.CommunicationKitchen.Server.Entities.Player;
import io.github.apfelcreme.CommunicationKitchen.Server.Entities.Pot;
import io.github.apfelcreme.CommunicationKitchen.Server.Order.Order;
import io.github.apfelcreme.CommunicationKitchen.Server.Order.SequenceOrder;
import io.github.apfelcreme.CommunicationKitchen.Server.Order.SyncOrder;
import io.github.apfelcreme.CommunicationKitchen.Util.DrawableType;

import java.util.*;

/**
 * Copyright (C) 2017 Lord36 aka Apfelcreme
 * <p>
 * This program is free software;
 * you can redistribute it and/or modify it under the terms of the GNU General
 * Public License as published by the Free Software Foundation; either version 3
 * of the License, or (at your option) any later version.
 * <p>
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 * <p>
 * You should have received a copy of the GNU General Public License along with
 * this program; if not, see <http://www.gnu.org/licenses/>.
 *
 * @author Lord36 aka Apfelcreme
 */
public class Game {

    private int currentRound;
    private int currentLives;

    private int timePerOrder;
    private int numberOfRounds;
    
    private boolean paused;

    private TimerTask task = null;

    /**
     * a list of all orders
     */
    private List<Order> runningOrders = new ArrayList<Order>();
    private ArrayList<Order> successfulOrders;
    private ArrayList<Order> failedOrders;

    public Game(int timePerOrder, int numberOfRounds, int startLives) {
        this.timePerOrder = timePerOrder;
        this.numberOfRounds = numberOfRounds;
        this.currentRound = 0;
        this.currentLives = startLives;
        this.runningOrders = new ArrayList<Order>();
        this.successfulOrders = new ArrayList<Order>();
        this.failedOrders = new ArrayList<Order>();
        KitchenServer.getInstance().log("Spiel-Start");
        newOrder(timePerOrder);
    }

    /**
     * The action to be performed by this timer task.
     */
    public void newOrder(final long time) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                boolean notReady = true;
                while (notReady) {
                    //wait until all players are ready
                    for (Player player : KitchenServer.getInstance().getPlayers()) {
                        if (player.isReady()) {
                            notReady = false;
                        }
                    }
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                Order order;
                if (Math.random() >= 0.5) {
                    order = new SyncOrder(UUID.randomUUID(), KitchenServer.getInstance().getPlayers().size() * 2, time, 3000);
                } else {
                    order = new SequenceOrder(UUID.randomUUID(), (int) ((KitchenServer.getInstance().getPlayers().size() + 1) * 1.8), time);
                }
                runningOrders.add(order);
                KitchenServer.getInstance().log("Order-Spawn: " + order.getClass().getName());
                for (Ingredient ingredient : order.getIngredients()) {
                    KitchenServer.getInstance().log("  " + ingredient.getId() + " - "
                            + ingredient.getType() + "(" + ingredient.getX() + "," + ingredient.getY() + ")");
                    ConnectionHandler.broadcastAddDrawable(ingredient.getId(),
                            ingredient.getQueuePos(),
                            ingredient.getType().getDrawableType(),
                            ingredient.getX(),
                            ingredient.getY());
                }


                ConnectionHandler.broadcastNewOrder(order);
                ConnectionHandler.broadcastLives(currentLives);
            }
        }).start();
    }

    /**
     * returns the list of orders that are currently active
     *
     * @return the list of orders
     */
    public List<Order> getRunningOrders() {
        return runningOrders;
    }

    /**
     * returns the list of successful orders
     *
     * @return the list of successful orders
     */
    public ArrayList<Order> getSuccessfulOrders() {
        return successfulOrders;
    }

    /**
     * returns the list of failed orders
     *
     * @return the list of failed orders
     */
    public ArrayList<Order> getFailedOrders() {
        return failedOrders;
    }

    /**
     * return the current lives
     *
     * @return the amount of lives the players still have
     */
    public int getCurrentLives() {
        return currentLives;
    }

    /**
     * stops the game
     *
     * @param gameResult the result of the game
     * @param reason     the reason why the game is stopped
     */
    public void stop(GameResult gameResult, Message reason) {
        ConnectionHandler.broadcastMessage(reason);
        if (task != null) {
            task.cancel();
        }
        KitchenServer.getInstance().log("Stop game due to " + gameResult.name()
                + ". Reason: " + reason);
    }

    /**
     * @param paused	
     */
    public void setPaused(boolean paused) {
    	this.paused = paused;
    }
    
    /**
	 * @return the paused
	 */
	public boolean isPaused() {
		return paused;
	}

	/**
     * Handles a failure. Stops the game if necessary (no more lives)
     *
     * @param reason - the reason for failing
     */
    public void handleFailure(Message reason) {
        if (failedOrders.size() == 1) {
            ConnectionHandler.broadcastMessage(reason);
            for (Player player : KitchenServer.getInstance().getPlayers()) {
                player.setReady(false);
            }
        }
        currentLives--;
        ConnectionHandler.broadcastLives(currentLives);
        ConnectionHandler.broadcastDamage();
        if (currentLives <= 0) {
            stop(GameResult.FAILURE, reason);
        } else {
            newOrder(timePerOrder);
        }
    }

    /**
     * Handles a success. Stops the game if necessary (all rounds at the current level completed)
     *
     * @param winMessage - the learned skill
     */
    public void handleSuccess(Message winMessage) {
        if (successfulOrders.size() == 1) {
            ConnectionHandler.broadcastMessage(winMessage);
            for (Player player : KitchenServer.getInstance().getPlayers()) {
                player.setReady(false);
            }
        }
        currentRound++;
        ConnectionHandler.broadcastOrderSuccess();
        if (currentRound >= numberOfRounds) {
            stop(GameResult.SUCCESS, Message.WIN_GAME);
        } else {
            newOrder(timePerOrder);
        }
    }

    /**
     * game results
     */
    private enum GameResult {
        SUCCESS, FAILURE
    }

    /**
     * the messages that can be sent
     */
    public enum Message {
        FAIL_SEQUENCE("Die Reihenfolge der Zutaten stimmt nicht! Nutze den Chat, um mit deinen Mitspielern zu besprechen, " +
                "wer welche Zutaten zu welcher Zeit in den Kochtopf gibt."),
        FAIL_TIME("Leider ist die Zeit abgelaufen! Um die vielen Zutaten in der kurzen Zeit einzusammeln und im Kochtopf " +
                "zu platzieren, ist es wichtig, dass du dich mit deinen Mitspielern abstimmst! Insbesondere solltet " +
                "ihr euch darauf einigen, wer welche Zutaten einsammelt. Du kannst dafür den Chat nutzen!"),
        FAIL_SYNC("Leider habt ihr die Zutaten nicht schnell genug hintereinander in den Kochtopf gegeben."),

        
        WIN_SEQUENCE("Weiter so! Offenbar habt ihr verstanden, wie wichtig es ist, dass ihr eure Zusammenarbeit gut koordiniert. " +
        		"Ein wichtiger Aspekt der Koordination ist die Betrachtung von Abhängigkeiten zwischen Aktivitäten. " + 
        		"Indem ihr die Zutaten in der richtigen Reihenfolge in den Kochtopf gegeben habt, konntet ihr die Bestellung erfolgreich meistern."),
        WIN_SYNC("Weiter so! Offenbar habt ihr verstanden, wie wichtig es ist, dass ihr eure Zusammenarbeit gut koordiniert. " +
        		"Ein wichtiger Aspekt der Koordination ist die Betrachtung von zeitlichen Abhängigkeiten zwischen Aktivitäten. " + 
        		"Indem ihr die Zutaten sehr schnell hintereinander in den Kochtopf gegeben habt, konntet ihr die Bestellung erfolgreich meistern."),
        WIN_GAME("Glückwunsch, ihr habt diese Spielrunde gewonnen!<br />" +
        		"Sicherlich habt ihr gemerkt, dass ihr mit jeder Runde besser geworden seid und dass Kommunikation ein erfolgsentscheidender Faktor war. " +
        		"Nicht nur für die Koordination ist Kommunikation sehr wichtig, sondern auch für den Prozess der Gruppenbildung. " + 
        		"Nach einem Modell von Bruce Tuckman durchlaufen neue Gruppen die Phasen Forming > Storming > Norming > Performing. " +
        		"Im Kern besagt dieses Modell, dass sich Gruppenmitglieder zunächst finden und Regeln der Zusammenarbeit entwickeln müssen " + 
        		"bevor sie als Gruppe ihr volles Leistungspotenzial entfalten können. " +
        		"Wenn du deine Mitspieler vorher nicht kanntest, habt ihr jede dieser Phasen während des Spiels bewusst oder unbewusst durchlaufen. " +
        		"Ihr musstet für jede neue Runde festlegen, wer welche Zutaten einsammelt. Doch wer von euch hat das immer bestimmt? " +
        		"Wenn ihr bei jeder Runde erneut lange diskutiert habt, wer welche Zutaten sammelt, seid ihr nicht über die Norming-Phase hinausgekommen " +
        		"und konntet das Spiel sicherlich nur mit intensivem Gebrauch der Pause-Funktion gewinnen. " +
        		"Haben die Zuständigkeiten hingegen jedes Mal schnell festgestanden, habt ihr die Performing-Phase erreicht.<br />" +
        		"Und die Quintessenz?<br />" +
        		"Ihr könnt keine Phase überspringen, aber ihr könnt Phasen verkürzen, indem ihr Regeln der Zusammenarbeit explizit diskutiert!");

        private String message;

        Message(String message) {
            this.message = message;
        }

        public String getMessage() {
            return message;
        }
    }

}
