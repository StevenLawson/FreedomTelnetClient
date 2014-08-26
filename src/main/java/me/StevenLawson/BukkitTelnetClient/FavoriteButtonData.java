package me.StevenLawson.BukkitTelnetClient;

public class FavoriteButtonData
{
    private final String label;
    private final String command;

    public FavoriteButtonData(String label, String command)
    {
        this.label = label;
        this.command = command;
    }

    public String getLabel()
    {
        return label;
    }

    public String getCommand()
    {
        return command;
    }
}
