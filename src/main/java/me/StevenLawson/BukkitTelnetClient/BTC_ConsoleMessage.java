package me.StevenLawson.BukkitTelnetClient;

import java.awt.Color;

public class BTC_ConsoleMessage
{
    private final String message;
    private Color color;

    public BTC_ConsoleMessage(final String message)
    {
        this.message = message;
        this.color = Color.BLACK;
    }

    public BTC_ConsoleMessage(final String message, final Color color)
    {
        this.message = message;
        this.color = color;
    }

    public String getMessage()
    {
        return message;
    }

    public Color getColor()
    {
        return color;
    }

    public void setColor(Color color)
    {
        this.color = color;
    }
}
