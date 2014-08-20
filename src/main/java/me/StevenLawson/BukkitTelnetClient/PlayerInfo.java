package me.StevenLawson.BukkitTelnetClient;

public class PlayerInfo
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
    public String toString()
    {
        return String.format("%s[Name: %s, Display Name: %s, IP: %s]", this.getClass().toString(), name, displayName, ip);
    }
}
