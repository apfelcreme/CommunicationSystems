package io.github.apfelcreme.CommunicationKitchen.Client.Listener;

import io.github.apfelcreme.CommunicationKitchen.Client.CommunicationKitchen;
import io.github.apfelcreme.CommunicationKitchen.Client.Drawable.Drawable;
import io.github.apfelcreme.CommunicationKitchen.Client.Drawable.DrawablePlayer;
import io.github.apfelcreme.CommunicationKitchen.Client.ServerConnector;
import io.github.apfelcreme.CommunicationKitchen.Util.Direction;

import java.awt.event.KeyEvent;

/**
 * Copyright (C) 2016 Lord36 aka Apfelcreme
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
public class KeyListener implements java.awt.event.KeyListener {
    /**
     * Invoked when a key has been typed.
     * See the class description for {@link KeyEvent} for a definition of
     * a key typed event.
     *
     * @param e
     */
    public void keyTyped(KeyEvent e) {

    }

    /**
     * Invoked when a key has been pressed.
     * See the class description for {@link KeyEvent} for a definition of
     * a key pressed event.
     *
     * @param e
     */
    public void keyPressed(KeyEvent e) {
        DrawablePlayer me = CommunicationKitchen.getInstance().getMe();
        if (e.getKeyCode() == KeyEvent.VK_W) {
            ServerConnector.getInstance().sendPlayerMove(me.getId(), Direction.UP);
        } else if (e.getKeyCode() == KeyEvent.VK_A) {
            ServerConnector.getInstance().sendPlayerMove(me.getId(), Direction.LEFT);
        } else if (e.getKeyCode() == KeyEvent.VK_S) {
            ServerConnector.getInstance().sendPlayerMove(me.getId(), Direction.DOWN);
        } else if (e.getKeyCode() == KeyEvent.VK_D) {
            ServerConnector.getInstance().sendPlayerMove(me.getId(), Direction.RIGHT);
        }
    }

    /**
     * Invoked when a key has been released.
     * See the class description for {@link KeyEvent} for a definition of
     * a key released event.
     *
     * @param e
     */
    public void keyReleased(KeyEvent e) {

    }
}
