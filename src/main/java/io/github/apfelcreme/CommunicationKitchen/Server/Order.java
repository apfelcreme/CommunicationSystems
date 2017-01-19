package io.github.apfelcreme.CommunicationKitchen.Server;

import io.github.apfelcreme.CommunicationKitchen.Server.Entities.Ingredient;

import java.util.Timer;
import java.util.TimerTask;

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
public class Order extends TimerTask{

    private Ingredient ingredient1;
    private Ingredient ingredient2;
    private Ingredient ingredient3;

    public Order(Ingredient ingredient1, Ingredient ingredient2, Ingredient ingredient3) {
        this.ingredient1 = ingredient1;
        this.ingredient2 = ingredient2;
        this.ingredient3 = ingredient3;
        new Timer().schedule(this, 15000);
    }

    /**
     * The action to be performed by this timer task.
     */
    @Override
    public void run() {
        ConnectionHandler.broadcastIngredientDespawn(ingredient1);
        ConnectionHandler.broadcastIngredientDespawn(ingredient2);
        ConnectionHandler.broadcastIngredientDespawn(ingredient3);
    }
}
