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

import java.util.HashSet;
import java.util.Objects;

public class ServerEntry extends ConfigEntry
{
    private String name;
    private String address;
    private boolean lastUsed = false;

    public ServerEntry()
    {
    }

    public ServerEntry(final String name, final String address)
    {
        this.name = name;
        this.address = address;
    }

    public ServerEntry(final String name, final String address, final boolean lastUsed)
    {
        this.name = name;
        this.address = address;
        this.lastUsed = lastUsed;
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

    @ParameterGetter(name = "address")
    public String getAddress()
    {
        return address;
    }

    @ParameterSetter(name = "address")
    public void setAddress(String address)
    {
        this.address = address;
    }

    @ParameterGetter(name = "lastUsed")
    public boolean isLastUsed()
    {
        return lastUsed;
    }

    @ParameterSetter(name = "lastUsed")
    public void setLastUsed(Boolean lastUsed)
    {
        this.lastUsed = lastUsed;
    }

    @Override
    public int hashCode()
    {
        int hash = 7;
        hash = 59 * hash + Objects.hashCode(this.name);
        hash = 59 * hash + Objects.hashCode(this.address);
        return hash;
    }

    @Override
    public boolean equals(Object obj)
    {
        if (obj == null)
        {
            return false;
        }

        if (getClass() != obj.getClass())
        {
            return false;
        }

        final ServerEntry other = (ServerEntry) obj;

        if (!Objects.equals(this.getName(), other.getName()))
        {
            return false;
        }

        if (!Objects.equals(this.getAddress(), other.getAddress()))
        {
            return false;
        }

        return true;
    }

    public static class ServerEntryList extends ConfigEntryList<ServerEntry>
    {
        public ServerEntryList()
        {
            super(new HashSet<ServerEntry>(), ServerEntry.class);
        }

        @Override
        public String getParentElementName()
        {
            return "servers";
        }

        @Override
        public String getItemElementName()
        {
            return "server";
        }
    }

    @Override
    public String toString()
    {
        return String.format("%s (%s)", getName(), getAddress());
    }

    @Override
    public String getElementName()
    {
        return "server";
    }
}
