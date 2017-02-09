package io.github.apfelcreme.CommunicationKitchen.Client.Drawable;

import io.github.apfelcreme.CommunicationKitchen.Client.CommunicationKitchen;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;

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
public class DrawableOrder {

    private UUID id;
    private final String type;
    private int x;
    private int y;
    private long timeLimit;
    private long timeCreated;
    private TimerTask pause;
    private List<Drawable> ingredients = new ArrayList<Drawable>();

    public DrawableOrder(UUID id, String type, int x, int y, long timeLimit, List<UUID> ingredientIds) {
        this.id = id;
        this.type = type;
        this.x = x;
        this.y = y;
        this.timeLimit = timeLimit;
        this.timeCreated = System.currentTimeMillis();
        for (UUID ingredientId : ingredientIds) {
            Drawable ingredient = CommunicationKitchen.getInstance().getDrawable(ingredientId);
            if (ingredient != null) {
                ingredients.add(new Drawable(ingredient.getId(), ingredient.getQueuePos(), ingredient.getType(), 0, 0));
            }
        }
    }

    /**
     * returns the id
     *
     * @return the id
     */
    public UUID getId() {
        return id;
    }

    /**
     * returns the order type name
     *
     * @return the order type name
     */
    public String getType() {
        return type;
    }

    /**
     * returns the x coordinate
     *
     * @return the x coordinate
     */
    public int getX() {
        return x;
    }

    /**
     * sets the x coordinate
     *
     * @param x the x coordinate
     */
    public void setX(int x) {
        this.x = x;
    }

    /**
     * returns the y coordinate
     *
     * @return the y coordinate
     */
    public int getY() {
        return y;
    }

    /**
     * sets the y coordinate
     *
     * @param y the y coordinate
     */
    public void setY(int y) {
        this.y = y;
    }

    /**
     * returns the time limit
     *
     * @return the time limit in ms
     */
    public long getTimeLimit() {
        return timeLimit;
    }

    /**
     * returns the time stamp when the order was created
     *
     * @return the time stamp when the order was created
     */
    public long getTimeCreated() {
        return timeCreated;
    }

    /**
     * returns all the ingredients the order contains
     *
     * @return the ingredients that are displayed in the order bar (NOT THE ONES ON THE DRAWING BOARD!)
     */
    public List<Drawable> getIngredients() {
        return ingredients;
    }

    /**
     * sets the time to shorten or lengthen the cooldown
     *
     * @param timeLimit the new time limit
     */
    public void setTimeNewLimit(long timeLimit) {
        this.timeLimit = timeLimit;
        this.timeCreated = new Date().getTime();
    }
    
    /**
     * Pause the order, i.e. prevent the timer from counting down
     */
    public void pause() {
    	final long remainingTime = timeLimit - (System.currentTimeMillis() - timeCreated);
    	pause = new TimerTask() {
			
			@Override
			public void run() {
				setTimeNewLimit(remainingTime + 100);				
				
			}
		};
		new Timer().schedule(pause, 100, 100);		
    }
    
    /**
     * Resume the order, i.e. let the timer start counting down again
     */
    public void resume() {
    	pause.cancel();
    }
}
