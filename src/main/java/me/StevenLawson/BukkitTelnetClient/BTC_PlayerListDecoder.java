package me.StevenLawson.BukkitTelnetClient;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class BTC_PlayerListDecoder
{
    private static final Pattern PLAYER_LIST_MESSAGE = Pattern.compile(":\\[.+@BukkitTelnet\\]\\$ playerList~(.+)");
    private static final Pattern LOGIN_MESSAGE = Pattern.compile("\\[.+?@BukkitTelnet\\]\\$ Logged in as (.+)\\.");

    private BTC_PlayerListDecoder()
    {
        throw new AssertionError();
    }

    public static final String checkForLoginMessage(String message)
    {
        final Matcher matcher = LOGIN_MESSAGE.matcher(message);
        if (matcher.find())
        {
            return matcher.group(1);
        }

        return null;
    }

    public static final Map<String, PlayerInfo> checkForPlayerListMessage(String message)
    {
        final Matcher matcher = PLAYER_LIST_MESSAGE.matcher(message);
        if (matcher.find())
        {
            final String data = matcher.group(1);
            try
            {
                final Map<String, PlayerInfo> playerInfo = new HashMap<>();

                final JSONObject json = new JSONObject(data);
                final JSONArrayIterable players = new JSONArrayIterable(json.getJSONArray("players"));
                for (JSONObject player : players)
                {
                    final String name = player.getString("name");
                    final String ip = player.getString("ip");
                    final String displayName = player.getString("displayName");
                    playerInfo.put(name, new PlayerInfo(name, ip, displayName));
                }

                return playerInfo;
            }
            catch (JSONException ex)
            {
            }
        }

        return null;
    }

    public static final class PlayerInfo
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

    public static final class JSONArrayIterable implements Iterable<JSONObject>
    {
        private final JSONArray array;

        public JSONArrayIterable(JSONArray array)
        {
            this.array = array;
        }

        @Override
        public Iterator<JSONObject> iterator()
        {
            return new Iterator<JSONObject>()
            {
                private int index = 0;
                private final int length = array.length();

                @Override
                public boolean hasNext()
                {
                    return index < length;
                }

                @Override
                public JSONObject next()
                {
                    return array.getJSONObject(index++);
                }
            };
        }
    }
}
