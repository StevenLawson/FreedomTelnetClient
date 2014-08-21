package me.StevenLawson.BukkitTelnetClient;

import java.awt.Color;
import java.util.regex.Pattern;

public class BTC_FormatHandler
{
    private static final Color PURPLE = new Color(128, 0, 128);
    private static final Color DARK_GREEN = new Color(86, 130, 3);

    private static final Pattern CHAT_MESSAGE = Pattern.compile("^:\\[.+? INFO\\]: \\<");
    private static final Pattern SAY_MESSAGE = Pattern.compile("^:\\[.+? INFO\\]: \\[Server:");
    private static final Pattern CSAY_MESSAGE = Pattern.compile("^:\\[.+? INFO\\]: \\[CONSOLE\\]<");

    private static final Pattern ADMINSAY_MESSAGE = Pattern.compile("^:\\[.+? INFO\\]: \\[TotalFreedomMod\\] \\[ADMIN\\] ");

    private static final Pattern WORLD_EDIT = Pattern.compile("^:\\[.+? INFO\\]: WorldEdit: ");

    private static final Pattern PREPROCESS_COMMAND = Pattern.compile("^:\\[.+? INFO\\]: \\[PREPROCESS_COMMAND\\] ");

    private static final Pattern ISSUED_SERVER_COMMAND = Pattern.compile("^:\\[.+? INFO\\]: .+? issued server command: ");
    private static final Pattern PLAYER_COMMAND = Pattern.compile("^:\\[.+? INFO\\]: \\[PLAYER_COMMAND\\] ");

    private static final Pattern ERROR_MESSAGE = Pattern.compile("^:\\[.+? (!?(WARN)|(ERROR))\\]: ");
    private static final Pattern EXCEPTION_MESSAGE = Pattern.compile("^[^\\[][^\\s]+: ");
    private static final Pattern STACK_TRACE = Pattern.compile("^\\t");

    private BTC_FormatHandler()
    {
        throw new AssertionError();
    }

    public static final boolean skipLine(String line)
    {
        final BTC_MainPanel mainPanel = BukkitTelnetClient.mainPanel;

        if (mainPanel == null)
        {
            return false;
        }

        if (mainPanel.getChkShowChatOnly().isSelected())
        {
            if (!CHAT_MESSAGE.matcher(line).find() && !SAY_MESSAGE.matcher(line).find() && !ADMINSAY_MESSAGE.matcher(line).find() && !CSAY_MESSAGE.matcher(line).find())
            {
                return true;
            }
        }
        else if (mainPanel.getChkIgnoreServerCommands().isSelected() && ISSUED_SERVER_COMMAND.matcher(line).find())
        {
            return true;
        }
        else if (mainPanel.getChkIgnorePlayerCommands().isSelected() && PLAYER_COMMAND.matcher(line).find())
        {
            return true;
        }
        else if (mainPanel.getChkIgnoreErrors().isSelected() && (ERROR_MESSAGE.matcher(line).find() || STACK_TRACE.matcher(line).find() || EXCEPTION_MESSAGE.matcher(line).find()))
        {
            return true;
        }

        return false;
    }

    public static final Color getColor(String line)
    {
        Color color = Color.BLACK;

        if (CHAT_MESSAGE.matcher(line).find() || SAY_MESSAGE.matcher(line).find() || CSAY_MESSAGE.matcher(line).find())
        {
            color = Color.BLUE;
        }
        else if (ADMINSAY_MESSAGE.matcher(line).find())
        {
            color = PURPLE;
        }
        else if (WORLD_EDIT.matcher(line).find())
        {
            color = Color.RED;
        }
        else if (PREPROCESS_COMMAND.matcher(line).find())
        {
            color = DARK_GREEN;
        }
        else if (ERROR_MESSAGE.matcher(line).find() || STACK_TRACE.matcher(line).find() || EXCEPTION_MESSAGE.matcher(line).find())
        {
            color = Color.LIGHT_GRAY;
        }

        return color;
    }
}
