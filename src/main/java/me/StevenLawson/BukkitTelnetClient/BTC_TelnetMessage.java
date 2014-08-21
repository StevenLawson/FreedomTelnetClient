package me.StevenLawson.BukkitTelnetClient;

import java.awt.Color;
import java.util.regex.Pattern;

public class BTC_TelnetMessage extends BTC_ConsoleMessage
{
    private static final String PATTERN_PREFIX = "^:\\[.+? INFO\\]: ";
    private static final Color PURPLE = new Color(128, 0, 128);
    private static final Color DARK_GREEN = new Color(86, 130, 3);

    private final BTC_LogMessageType messageType;

    public BTC_TelnetMessage(String message)
    {
        super(message);
        this.messageType = BTC_LogMessageType.getMessageType(message);
    }

    public BTC_LogMessageType getMessageType()
    {
        return this.messageType;
    }

    public boolean skip()
    {
        final BTC_MainPanel mainPanel = BukkitTelnetClient.mainPanel;

        if (mainPanel == null || this.messageType == null)
        {
            return false;
        }
        if (mainPanel.getChkShowChatOnly().isSelected())
        {
            switch (messageType)
            {
                case CHAT_MESSAGE:
                case CSAY_MESSAGE:
                case SAY_MESSAGE:
                case ADMINSAY_MESSAGE:
                    return false;
                default:
                    return true;
            }
        }
        else if (mainPanel.getChkIgnoreServerCommands().isSelected() && this.messageType == BTC_LogMessageType.ISSUED_SERVER_COMMAND)
        {
            return true;
        }
        else if (mainPanel.getChkIgnorePlayerCommands().isSelected() && this.messageType == BTC_LogMessageType.PLAYER_COMMAND)
        {
            return true;
        }
        else if (mainPanel.getChkIgnoreErrors().isSelected())
        {
            switch (this.messageType)
            {
                case ERROR_MESSAGE:
                case STACK_TRACE:
                case EXCEPTION_MESSAGE:
                    return false;
                default:
                    return true;
            }
        }

        return false;
    }

    @Override
    public Color getColor()
    {
        if (this.messageType == null)
        {
            return super.getColor();
        }
        else
        {
            return this.messageType.getColor();
        }
    }

    public static enum BTC_LogMessageType
    {
        CHAT_MESSAGE(PATTERN_PREFIX + "\\<", Color.BLUE),
        SAY_MESSAGE(PATTERN_PREFIX + "\\[Server:", Color.BLUE),
        CSAY_MESSAGE(PATTERN_PREFIX + "\\[CONSOLE\\]<", Color.BLUE),
        //
        ADMINSAY_MESSAGE(PATTERN_PREFIX + "\\[TotalFreedomMod\\] \\[ADMIN\\] ", PURPLE),
        //
        WORLD_EDIT(PATTERN_PREFIX + "WorldEdit: ", Color.RED),
        //
        PREPROCESS_COMMAND(PATTERN_PREFIX + "\\[PREPROCESS_COMMAND\\] ", DARK_GREEN),
        //
        ISSUED_SERVER_COMMAND(PATTERN_PREFIX + ".+? issued server command: "),
        PLAYER_COMMAND(PATTERN_PREFIX + "\\[PLAYER_COMMAND\\] "),
        //
        ERROR_MESSAGE("^:\\[.+? (!?(WARN)|(ERROR))\\]: ", Color.LIGHT_GRAY),
        EXCEPTION_MESSAGE("^[^\\[][^\\s]+: ", Color.LIGHT_GRAY),
        STACK_TRACE("^\\t", Color.LIGHT_GRAY);

        private final Pattern messagePattern;
        private final Color color;

        private BTC_LogMessageType(final String messagePatternStr)
        {
            this.messagePattern = Pattern.compile(messagePatternStr);
            this.color = Color.BLACK;
        }

        private BTC_LogMessageType(final String messagePatternStr, final Color color)
        {
            this.messagePattern = Pattern.compile(messagePatternStr);
            this.color = color;
        }

        public Pattern getMessagePattern()
        {
            return this.messagePattern;
        }

        public Color getColor()
        {
            return this.color;
        }

        public static BTC_LogMessageType getMessageType(final String message)
        {
            for (final BTC_LogMessageType type : values())
            {
                if (type.getMessagePattern().matcher(message).find())
                {
                    return type;
                }
            }
            return null;
        }
    }
}
