package me.StevenLawson.BukkitTelnetClient;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class BTC_ConfigLoader
{
    public static class PlayerListCommand
    {
        private final String name;
        private final String format;

        public PlayerListCommand(String name, String format)
        {
            this.name = name;
            this.format = format;
        }

        public String getFormat()
        {
            return format;
        }

        public String getName()
        {
            return name;
        }
    }

    private static final List<PlayerListCommand> COMMANDS = new ArrayList<>();

    static
    {
        COMMANDS.add(new PlayerListCommand("Ban", "glist ban %s"));
        COMMANDS.add(new PlayerListCommand("Toggle Mute", "mute %s"));
        COMMANDS.add(new PlayerListCommand("Kick", "tempban %s 10s Kicked"));
        COMMANDS.add(new PlayerListCommand("Tempban 5m", "tempban %s 5m"));
        COMMANDS.add(new PlayerListCommand("Smite", "smite %s"));
        COMMANDS.add(new PlayerListCommand("Op", "op %s"));
        COMMANDS.add(new PlayerListCommand("Deop", "deop %s"));
        COMMANDS.add(new PlayerListCommand("GTFO", "gtfo %s"));
        COMMANDS.add(new PlayerListCommand("Toggle Freeze", "fr %s"));
        COMMANDS.add(new PlayerListCommand("Cage", "cage %s"));
        COMMANDS.add(new PlayerListCommand("Uncage", "cage %s off"));
        COMMANDS.add(new PlayerListCommand("Doom", "doom %s"));
        COMMANDS.add(new PlayerListCommand("Creative", "creative %s"));
        COMMANDS.add(new PlayerListCommand("Survival", "survival %s"));
    }

    public BTC_ConfigLoader()
    {
        try
        {
            final Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();

            final Element rootElement = doc.createElement("configuration");
            doc.appendChild(rootElement);

            final Element plcElement = doc.createElement("playerListCommands");
            rootElement.appendChild(plcElement);

            for (final PlayerListCommand command : COMMANDS)
            {
                final Element commandElement = doc.createElement("command");
                plcElement.appendChild(commandElement);

                final Element commandName = doc.createElement("name");
                commandName.appendChild(doc.createTextNode(command.getName()));
                commandElement.appendChild(commandName);

                final Element commandFormat = doc.createElement("format");
                commandFormat.appendChild(doc.createTextNode(command.getFormat()));
                commandElement.appendChild(commandFormat);
            }

            final Transformer transformer = TransformerFactory.newInstance().newTransformer();
            transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");

            transformer.transform(new DOMSource(doc), new StreamResult(new File("test.xml")));
        }
        catch (ParserConfigurationException | DOMException | IllegalArgumentException | TransformerException ex)
        {
            ex.printStackTrace();
        }
    }
}
