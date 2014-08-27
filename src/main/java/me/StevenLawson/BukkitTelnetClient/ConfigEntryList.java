/* 
 * Copyright (C) 2012-2014 Steven Lawson
 *
 * This file is part of BukkitTelnetClient.
 *
 * BukkitTelnetClient is free software: you can redistribute it and/or modify
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

import java.lang.annotation.*;
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

    public Element toXML(final Document doc)
    {
        final Element parent = doc.createElement(getParentElementName());

        for (final E entry : getList())
        {
            final Element item = doc.createElement(getItemElementName());
            parent.appendChild(item);

            for (final Method method : getEntryClass().getDeclaredMethods())
            {
                final ParameterGetter annotation = method.getDeclaredAnnotation(ParameterGetter.class);
                if (annotation != null)
                {
                    try
                    {
                        final Element parameter = doc.createElement(annotation.name());
                        parameter.appendChild(doc.createTextNode(method.invoke(entry).toString()));
                        item.appendChild(parameter);
                    }
                    catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException | DOMException ex)
                    {
                        BukkitTelnetClient.LOGGER.log(Level.SEVERE, null, ex);
                    }
                }
            }
        }

        return parent;
    }

    public boolean fromXML(final Document doc)
    {
        NodeList itemNodes = doc.getDocumentElement().getElementsByTagName(getParentElementName());
        if (itemNodes.getLength() < 1)
        {
            return false;
        }
        itemNodes.item(0).getChildNodes();

        getList().clear();

        for (int i = 0; i < itemNodes.getLength(); i++)
        {
            final Node node = itemNodes.item(0);
            if (node.getNodeType() == Node.ELEMENT_NODE)
            {
                final Element element = (Element) node;

                try
                {
                    final E newEntry = getEntryClass().newInstance();

                    for (final Method method : getEntryClass().getDeclaredMethods())
                    {
                        final ParameterSetter annotation = method.getDeclaredAnnotation(ParameterSetter.class);
                        if (annotation != null)
                        {
                            final String valueStr = element.getElementsByTagName(annotation.name()).item(0).getTextContent();
                            final Class<?> _type = method.getParameterTypes()[0];
                            if (_type == Boolean.class)
                            {
                                method.invoke(newEntry, Boolean.valueOf(valueStr));
                            }
                            else if (_type == String.class)
                            {
                                method.invoke(newEntry, valueStr);
                            }
                        }
                    }

                    getList().add(newEntry);
                }
                catch (Exception ex)
                {
                    BukkitTelnetClient.LOGGER.log(Level.SEVERE, null, ex);
                }
            }
        }

        return true;
    }

    public abstract String getParentElementName();

    public abstract String getItemElementName();

    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.METHOD)
    public @interface ParameterGetter
    {
        public String name();
    }

    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.METHOD)
    public @interface ParameterSetter
    {
        public String name();
    }
}
