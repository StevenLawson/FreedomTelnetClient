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

import java.lang.reflect.*;
import java.util.*;
import java.util.logging.Level;
import org.w3c.dom.*;

public abstract class ConfigEntryList<E extends ConfigEntry>
{
    private final Collection<E> list;
    private final Class<E> entryClass;

    public ConfigEntryList(Collection<E> list, Class<E> entryClass)
    {
        this.list = list;
        this.entryClass = entryClass;
    }

    public Collection<E> getList()
    {
        return list;
    }

    public Class<E> getEntryClass()
    {
        return entryClass;
    }

    public boolean listFromXML(final Document doc)
    {
        NodeList itemNodes = doc.getDocumentElement().getElementsByTagName(getParentElementName());
        if (itemNodes.getLength() == 0)
        {
            return false;
        }
        itemNodes = itemNodes.item(0).getChildNodes();

        getList().clear();

        for (int i = 0; i < itemNodes.getLength(); i++)
        {
            final Node itemNode = itemNodes.item(i);
            if (itemNode.getNodeType() == Node.ELEMENT_NODE)
            {
                final E newEntry;
                try
                {
                    newEntry = getEntryClass().newInstance();
                }
                catch (InstantiationException | IllegalAccessException ex)
                {
                    BukkitTelnetClient.LOGGER.log(Level.SEVERE, null, ex);
                    return false;
                }

                final Element itemElement = (Element) itemNode;
                for (final Method method : getEntryClass().getDeclaredMethods())
                {
                    final ParameterSetter annotation = BukkitTelnetClient.getDeclaredAnnotation(method, ParameterSetter.class);
                    if (annotation == null)
                    {
                        continue;
                    }

                    final NodeList tags = itemElement.getElementsByTagName(annotation.name());
                    if (tags.getLength() > 0)
                    {
                        final String valueStr = itemElement.getElementsByTagName(annotation.name()).item(0).getTextContent();
                        final Class<?> _type = method.getParameterTypes()[0];

                        try
                        {
                            if (_type == Boolean.class)
                            {
                                method.invoke(newEntry, Boolean.valueOf(valueStr));
                            }
                            else if (_type == String.class)
                            {
                                method.invoke(newEntry, valueStr);
                            }
                        }
                        catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException ex)
                        {
                            BukkitTelnetClient.LOGGER.log(Level.SEVERE, null, ex);
                        }
                    }
                }

                getList().add(newEntry);
            }
        }

        return true;
    }

    public Element listToXML(final Document doc)
    {
        final Element parent = doc.createElement(getParentElementName());

        for (final E entry : getList())
        {
            final Element item = doc.createElement(getItemElementName());
            parent.appendChild(item);

            for (final Method method : getEntryClass().getDeclaredMethods())
            {
                final ParameterGetter annotation = BukkitTelnetClient.getDeclaredAnnotation(method, ParameterGetter.class);
                if (annotation == null)
                {
                    continue;
                }

                final Element parameter = doc.createElement(annotation.name());

                Object value = null;
                try
                {
                    value = method.invoke(entry);
                }
                catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException ex)
                {
                    BukkitTelnetClient.LOGGER.log(Level.SEVERE, null, ex);
                }
                if (value != null)
                {
                    parameter.appendChild(doc.createTextNode(value.toString()));
                }

                item.appendChild(parameter);
            }
        }

        return parent;
    }

    public abstract String getParentElementName();

    public abstract String getItemElementName();
}
