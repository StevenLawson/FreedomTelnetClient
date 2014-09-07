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

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.logging.Level;
import org.w3c.dom.*;

public abstract class ConfigEntry
{
    public abstract String getElementName();

    public ConfigEntry fromXML(final Document doc)
    {
        final ConfigEntry newEntry;
        try
        {
            newEntry = getClass().newInstance();
        }
        catch (InstantiationException | IllegalAccessException ex)
        {
            BukkitTelnetClient.LOGGER.log(Level.SEVERE, null, ex);
            return null;
        }

        final NodeList itemNodes = doc.getDocumentElement().getElementsByTagName(getElementName());
        if (itemNodes.getLength() > 0)
        {
            final Node itemNode = itemNodes.item(0);
            if (itemNode.getNodeType() == Node.ELEMENT_NODE)
            {
                final Element itemElement = (Element) itemNode;
                for (final Method method : getClass().getDeclaredMethods())
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
            }
        }

        return newEntry;
    }

    public Element toXML(final Document doc)
    {
        final Element item = doc.createElement(getElementName());

        for (final Method method : getClass().getDeclaredMethods())
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
                value = method.invoke(this);
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

        return item;
    }
}
