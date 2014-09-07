/* 
 * Copyright (C) 2012-2014 Steven Lawson
 *
 * This file is part of FreedomTelnetClient.
 *
 * FreedomTelnetClient is free software: you can redistribute it and/or modify
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

import java.util.ArrayList;

public class FavoriteButtonEntry extends ConfigEntry
{
    private String label;
    private String command;

    public FavoriteButtonEntry()
    {
    }

    @ParameterGetter(name = "label")
    public String getLabel()
    {
        return label;
    }

    @ParameterSetter(name = "label")
    public void setLabel(String label)
    {
        this.label = label;
    }

    @ParameterGetter(name = "command")
    public String getCommand()
    {
        return command;
    }

    @ParameterSetter(name = "command")
    public void setCommand(String command)
    {
        this.command = command;
    }

    public static class FavoriteButtonEntryList extends ConfigEntryList<FavoriteButtonEntry>
    {
        public FavoriteButtonEntryList()
        {
            super(new ArrayList<FavoriteButtonEntry>(), FavoriteButtonEntry.class);
        }

        @Override
        public String getParentElementName()
        {
            return "favoriteButtons";
        }

        @Override
        public String getItemElementName()
        {
            return "favoriteButton";
        }
    }

    @Override
    public String getElementName()
    {
        return "favoriteButton";
    }
}
