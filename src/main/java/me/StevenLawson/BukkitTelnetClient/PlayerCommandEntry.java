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

import java.util.ArrayList;

public class PlayerCommandEntry extends ConfigEntry
{
    private String name;
    private String format;

    public PlayerCommandEntry()
    {
    }

    @ParameterGetter(name = "format")
    public String getFormat()
    {
        return format;
    }

    @ParameterSetter(name = "format")
    public void setFormat(String format)
    {
        this.format = format;
    }

    @ParameterGetter(name = "name")
    public String getName()
    {
        return name;
    }

    @ParameterSetter(name = "name")
    public void setName(String name)
    {
        this.name = name;
    }

    public static class PlayerCommandEntryList extends ConfigEntryList<PlayerCommandEntry>
    {
        public PlayerCommandEntryList()
        {
            super(new ArrayList<PlayerCommandEntry>(), PlayerCommandEntry.class);
        }

        @Override
        public String getParentElementName()
        {
            return "playerCommands";
        }

        @Override
        public String getItemElementName()
        {
            return "playerCommand";
        }
    }

    @Override
    public String getElementName()
    {
        return "playerCommand";
    }
}
