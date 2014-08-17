package me.StevenLawson.BukkitTelnetClient;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

public final class PlayerInfo
{
    private final String name;
    private final String ip;
    private final String displayName;

    public PlayerInfo(String name, String ip, String displayName)
    {
        this.name = name;
        this.ip = ip;
        this.displayName = displayName;
    }

    public String getName()
    {
        return name;
    }

    public String getIp()
    {
        return ip;
    }

    public String getDisplayName()
    {
        return displayName;
    }

    @Override
    public int hashCode()
    {
        return new HashCodeBuilder(17, 31).
                append(name).
                append(ip).
                append(displayName).
                toHashCode();
    }

    @Override
    public boolean equals(Object obj)
    {
        if (!(obj instanceof PlayerInfo))
        {
            return false;
        }

        if (obj == this)
        {
            return true;
        }

        PlayerInfo rhs = (PlayerInfo) obj;
        return new EqualsBuilder().
                append(name, rhs.name).
                append(ip, rhs.ip).
                append(displayName, rhs.displayName).
                isEquals();
    }

    @Override
    public String toString()
    {
        return String.format("%s[Name: %s, Display Name: %s, IP: %s]", this.getClass().toString(), name, displayName, ip);
    }
}
