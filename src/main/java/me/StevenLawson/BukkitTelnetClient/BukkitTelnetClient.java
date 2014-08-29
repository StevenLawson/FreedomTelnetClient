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

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.UnsupportedLookAndFeelException;

public class BukkitTelnetClient
{
    public static final String VERSION_STRING = "v2.0.4-SNAPSHOT";
    public static final Logger LOGGER = Logger.getLogger(BukkitTelnetClient.class.getName());
    public static BTC_MainPanel mainPanel = null;
    public static BTC_ConfigLoader config = new BTC_ConfigLoader();

    public static void main(String args[])
    {
        config.load(true);

        findAndSetLookAndFeel("Windows");

        java.awt.EventQueue.invokeLater(new Runnable()
        {
            @Override
            public void run()
            {
                mainPanel = new BTC_MainPanel();
                mainPanel.setup();
            }
        });
    }

    private static void findAndSetLookAndFeel(final String searchStyleName)
    {
        try
        {
            javax.swing.UIManager.LookAndFeelInfo foundStyle = null;
            javax.swing.UIManager.LookAndFeelInfo fallbackStyle = null;

            for (javax.swing.UIManager.LookAndFeelInfo style : javax.swing.UIManager.getInstalledLookAndFeels())
            {
                if (searchStyleName.equalsIgnoreCase(style.getName()))
                {
                    foundStyle = style;
                    break;
                }
                else if ("Nimbus".equalsIgnoreCase(style.getName()))
                {
                    fallbackStyle = style;
                }
            }

            if (foundStyle != null)
            {
                javax.swing.UIManager.setLookAndFeel(foundStyle.getClassName());
            }
            else if (fallbackStyle != null)
            {
                javax.swing.UIManager.setLookAndFeel(fallbackStyle.getClassName());
            }
        }
        catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException ex)
        {
            LOGGER.log(Level.SEVERE, null, ex);
        }
    }
}
