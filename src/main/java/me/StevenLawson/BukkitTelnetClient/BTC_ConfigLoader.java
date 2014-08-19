package me.StevenLawson.BukkitTelnetClient;

import java.io.*;
import java.util.*;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.apache.commons.io.FileUtils;
import org.w3c.dom.*;

public class BTC_ConfigLoader
{
    private static final String SETTINGS_FILE = "settings.xml";

    private final List<PlayerCommandEntry> playerCommands = new ArrayList<>();
    private final List<ServerEntry> servers = new ArrayList<>();

    public BTC_ConfigLoader()
    {
    }

    public boolean load(boolean verbose)
    {
        File settings = new File("settings.xml");

        if (!settings.exists())
        {
            if (extractFileFromJar("/" + SETTINGS_FILE, SETTINGS_FILE))
            {
                if (verbose)
                {
                    System.out.println("Copied default " + SETTINGS_FILE + ".");
                }
            }
        }

        if (settings.exists())
        {
            boolean loadError = loadXML(settings);

            final List<ServerEntry> oldServers = importOldConfig();
            this.servers.addAll(oldServers);

            final HashSet<ServerEntry> uniqueServers = new HashSet<>(this.servers);
            this.servers.clear();
            this.servers.addAll(uniqueServers);

            generateXML(settings);

            if (verbose)
            {
                if (loadError)
                {
                    System.out.println("Settings loaded with errors.");
                }
                else
                {
                    System.out.println("Settings loaded.");
                }
            }

            return true;
        }
        else
        {
            if (verbose)
            {
                System.out.println("Can't load " + SETTINGS_FILE + ".");
            }
        }

        return false;
    }

    public boolean save()
    {
        return generateXML(new File(SETTINGS_FILE));
    }

    public List<PlayerCommandEntry> getCommands()
    {
        return this.playerCommands;
    }

    public List<ServerEntry> getServers()
    {
        return this.servers;
    }

    private boolean generateXML(final File file)
    {
        try
        {
            final Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();

            final Element rootElement = doc.createElement("configuration");
            doc.appendChild(rootElement);

            rootElement.appendChild(PlayerCommandEntry.listToXML(this.playerCommands, doc));
            rootElement.appendChild(ServerEntry.listToXML(this.servers, doc));

            final Transformer transformer = TransformerFactory.newInstance().newTransformer();
            transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");

            transformer.transform(new DOMSource(doc), new StreamResult(file));

            return true;
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }

        return false;
    }

    private boolean loadXML(final File file)
    {
        boolean hadErrors = false;

        try
        {
            final Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(file);
            doc.getDocumentElement().normalize();

            if (!PlayerCommandEntry.xmlToList(this.playerCommands, doc))
            {
                System.out.println("Error loading playerCommands.");
                hadErrors = true;
            }

            if (!ServerEntry.xmlToList(this.servers, doc))
            {
                System.out.println("Error loading servers.");
                hadErrors = true;
            }
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
            hadErrors = true;
        }

        return hadErrors;
    }

    private static boolean extractFileFromJar(final String resourceName, final String fileName)
    {
        final InputStream resource = BTC_ConfigLoader.class.getResourceAsStream(resourceName);
        if (resource != null)
        {
            final File destination = new File(fileName);
            try
            {
                FileUtils.copyInputStreamToFile(resource, destination);
                return true;
            }
            catch (IOException ex)
            {
                ex.printStackTrace();
            }
        }

        return false;
    }

    private static List<ServerEntry> importOldConfig()
    {
        final List<ServerEntry> oldServers = new ArrayList<>();

        try
        {
            final File file = new File("btc_servers.cfg");
            if (file.exists())
            {
                try (final BufferedReader in = new BufferedReader(new FileReader(file)))
                {
                    String line;
                    while ((line = in.readLine()) != null)
                    {
                        line = line.trim();
                        oldServers.add(new ServerEntry("imported", line));
                    }
                }

                FileUtils.moveFile(file, new File("btc_servers.cfg.bak"));
            }
        }
        catch (IOException ex)
        {
            ex.printStackTrace();
        }

        return oldServers;
    }

    public static class PlayerCommandEntry
    {
        private final String name;
        private final String format;

        public PlayerCommandEntry(String name, String format)
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

        public static Element listToXML(final List<PlayerCommandEntry> playerCommands, final Document doc)
        {
            final Element plcElement = doc.createElement("playerCommands");

            for (final PlayerCommandEntry command : playerCommands)
            {
                final Element commandElement = doc.createElement("playerCommand");
                plcElement.appendChild(commandElement);

                final Element commandName = doc.createElement("name");
                commandName.appendChild(doc.createTextNode(command.getName()));
                commandElement.appendChild(commandName);

                final Element commandFormat = doc.createElement("format");
                commandFormat.appendChild(doc.createTextNode(command.getFormat()));
                commandElement.appendChild(commandFormat);
            }

            return plcElement;
        }

        public static boolean xmlToList(final List<PlayerCommandEntry> playerCommands, final Document doc)
        {
            NodeList playerCommandNodes = doc.getDocumentElement().getElementsByTagName("playerCommands");
            if (playerCommandNodes.getLength() < 1)
            {
                return false;
            }
            playerCommandNodes = playerCommandNodes.item(0).getChildNodes();

            playerCommands.clear();

            for (int i = 0; i < playerCommandNodes.getLength(); i++)
            {
                final Node node = playerCommandNodes.item(i);
                if (node.getNodeType() == Node.ELEMENT_NODE)
                {
                    final Element element = (Element) node;

                    final PlayerCommandEntry command = new PlayerCommandEntry(
                            element.getElementsByTagName("name").item(0).getTextContent(),
                            element.getElementsByTagName("format").item(0).getTextContent()
                    );

                    playerCommands.add(command);
                }
            }

            return true;
        }
    }

    public static class ServerEntry
    {
        private final String name;
        private final String address;

        public ServerEntry(final String name, final String address)
        {
            this.name = name;
            this.address = address;
        }

        public String getName()
        {
            return name;
        }

        public String getAddress()
        {
            return address;
        }

        @Override
        public int hashCode()
        {
            int hash = 7;
            hash = 67 * hash + Objects.hashCode(this.name);
            hash = 67 * hash + Objects.hashCode(this.address);
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

            if (!Objects.equals(this.name, other.name))
            {
                return false;
            }

            if (!Objects.equals(this.address, other.address))
            {
                return false;
            }

            return true;
        }

        public static Element listToXML(final List<ServerEntry> servers, final Document doc)
        {
            final Element serversElement = doc.createElement("servers");

            for (final ServerEntry command : servers)
            {
                final Element commandElement = doc.createElement("server");
                serversElement.appendChild(commandElement);

                final Element serverName = doc.createElement("name");
                serverName.appendChild(doc.createTextNode(command.getName()));
                commandElement.appendChild(serverName);

                final Element serverAddress = doc.createElement("address");
                serverAddress.appendChild(doc.createTextNode(command.getAddress()));
                commandElement.appendChild(serverAddress);
            }

            return serversElement;
        }

        public static boolean xmlToList(final List<ServerEntry> servers, final Document doc)
        {
            NodeList serverNodes = doc.getDocumentElement().getElementsByTagName("servers");
            if (serverNodes.getLength() < 1)
            {
                return false;
            }
            serverNodes = serverNodes.item(0).getChildNodes();

            servers.clear();

            for (int i = 0; i < serverNodes.getLength(); i++)
            {
                final Node node = serverNodes.item(i);
                if (node.getNodeType() == Node.ELEMENT_NODE)
                {
                    final Element element = (Element) node;

                    final ServerEntry server = new ServerEntry(
                            element.getElementsByTagName("name").item(0).getTextContent(),
                            element.getElementsByTagName("address").item(0).getTextContent()
                    );

                    servers.add(server);
                }
            }

            return true;
        }
    }
}
