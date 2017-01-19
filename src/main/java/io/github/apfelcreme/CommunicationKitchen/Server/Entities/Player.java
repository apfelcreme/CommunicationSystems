package io.github.apfelcreme.CommunicationKitchen.Server.Entities;

import java.util.UUID;

import io.github.apfelcreme.CommunicationKitchen.Server.ConnectionHandler;
import io.github.apfelcreme.CommunicationKitchen.Server.KitchenServer;
import io.github.apfelcreme.CommunicationKitchen.Server.Order;
import io.github.apfelcreme.CommunicationKitchen.Util.Direction;
import io.github.apfelcreme.CommunicationKitchen.Util.Util;

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
public class Player {

    private UUID id;
    private int x;
    private int y;
    private Direction direction;
    private Ingredient carrying;

    public Player(UUID id, int x, int y, Direction direction) {
        this.id = id;
        this.x = x;
        this.y = y;
        this.direction = direction;
    }

    public void move(Direction direction) {
        switch (direction) {

            case LEFT:
                x = x - 10;
                if (x < 0) {
                    x = 0;
                }
                setDirection(Direction.LEFT);
                break;
            case RIGHT:
                x = x + 10;
                if (x > KitchenServer.getInstance().getFieldDimension().width - 40) {
                    x = KitchenServer.getInstance().getFieldDimension().width - 40;
                }
                setDirection(Direction.RIGHT);
                break;
            case UP:
                y = y - 10;
                if (y < 0) {
                    y = 0;
                }
                setDirection(Direction.UP);
                break;
            case DOWN:
                y = y + 10;
                if (y > (KitchenServer.getInstance().getFieldDimension().height - 40)) {
                    y = KitchenServer.getInstance().getFieldDimension().height - 40;
                }
                setDirection(Direction.DOWN);
                break;
        }

        for (Order order : KitchenServer.getInstance().getOrders()) {

        }
        
        for (Ingredient ingredient : KitchenServer.getInstance().getIngredients()) {
        	if (Util.arePositionsEqual(this.x, this.y, ingredient.getX(), ingredient.getY(), 10)) {
        		System.out.println("Player catched ingredient " + ingredient.getType());
        		if (this.carrying == null) {
        			this.carrying = ingredient;
        			ConnectionHandler.broadcastIngredientDespawn(ingredient);        			
        		}
        	}
        }
        Pot pot = KitchenServer.getInstance().getPot();
        if (Util.arePositionsEqual(this.x, this.y, pot.getX(), pot.getY(), 10)) {
        	this.carrying = null;
        }

        // send a message to all clients, so they can redraw the position
        // of the player on their own GUIs
        ConnectionHandler.broadcastPlayerMove(id, x, y, direction);
    }

    /**
     * returns the players id
     *
     * @return the players id
     */
    public UUID getId() {
        return id;
    }

    /**
     * returns the players x coordinate
     *
     * @return the players x coordinate
     */
    public int getX() {
        return x;
    }

    /**
     * returns the players y coordinate
     *
     * @return the players y coordinate
     */
    public int getY() {
        return y;
    }

    /**
     * returns the direction the player is facing
     *
     * @return the direction
     */
    public Direction getDirection() {
        return direction;
    }

    /**
     * sets the direction the player is facing
     *
     * @param direction the direction
     */
    public void setDirection(Direction direction) {
        this.direction = direction;
    }

    /**
     * returns the ingredient the player is carrying
     *
     * @return the ingredient
     */
    public Ingredient getCarrying() {
        return carrying;
    }

    /**
     * sets the ingredient the player is carrying
     *
     * @param carrying an ingredient
     */
    public void setCarrying(Ingredient carrying) {
        this.carrying = carrying;
    }
}
