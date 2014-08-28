/* 
 * Copyright (C) 2012-2014 Steven Lawson
 *
 * This file is part of BukkitTelnetClient.
 *
 * BukkitTelnetClient is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package me.StevenLawson.BukkitTelnetClient;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Collection;
import javax.swing.JButton;
import javax.swing.JPanel;

public class BTC_FavoriteButtonsPanel extends JPanel
{
    public BTC_FavoriteButtonsPanel(final Collection<FavoriteButtonEntry> buttonList)
    {
        super.setLayout(new GridLayout(0, 2, 1, 1));

        final ActionListener actionListener = new ActionListener()
        {
            @Override
            public void actionPerformed(final ActionEvent event)
            {
                if (BukkitTelnetClient.mainPanel != null)
                {
                    BukkitTelnetClient.mainPanel.getConnectionManager().sendDelayedCommand(event.getActionCommand(), true, 100);
                }
            }
        };

        for (final FavoriteButtonEntry buttonData : buttonList)
        {
            final JButton button = new JButton();
            button.setText(buttonData.getLabel());
            button.setActionCommand(buttonData.getCommand());
            button.addActionListener(actionListener);

            final Dimension max = button.getMaximumSize();
            max.setSize(max.getWidth(), 10);
            button.setMaximumSize(max);

            add(button);
        }
    }

    @Override
    public void setLayout(LayoutManager lm)
    {
        //Disable
    }
}
