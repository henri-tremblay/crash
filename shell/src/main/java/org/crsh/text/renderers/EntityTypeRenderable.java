/*
 * Copyright (C) 2012 eXo Platform SAS.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */

package org.crsh.text.renderers;

import org.crsh.text.Renderable;
import org.crsh.text.Renderer;
import org.crsh.text.ui.*;

import java.util.*;

/**
 * @author <a href="mailto:alain.defrance@exoplatform.com">Alain Defrance</a>
 */
public class EntityTypeRenderable extends Renderable<EntityTypeRenderable.EntityTypeData> {

  @Override
  public Class<EntityTypeRenderable.EntityTypeData> getType() {
    return EntityTypeRenderable.EntityTypeData.class;
  }

  @Override
  public Renderer renderer(Iterator<EntityTypeRenderable.EntityTypeData> stream) {

    TableElement table = new TableElement();

    table.setRightCellPadding(1);

    while (stream.hasNext()) {
      EntityTypeData entityTypeData = stream.next();

      if (!entityTypeData.verbose) {
        if (table.getRows().size() == 0) {
          RowElement header = new RowElement(true);
          header.add(new LabelElement("NAME"), new LabelElement("TYPE"));
          table.add(header);
        }
        RowElement row = new RowElement();
        row.add(new LabelElement(entityTypeData.name), new LabelElement(entityTypeData.type));
        table.add(row);
      } else {
        table.setColumnLayout(Layout.weighted(1));
        RowElement name = new RowElement();
        name.add(new LabelElement("Name : " + entityTypeData.name));
        table.add(name);
        RowElement type = new RowElement();
        type.add(new LabelElement("Type : " + entityTypeData.type));
        table.add(type);
        RowElement mapping = new RowElement();
        mapping.add(new LabelElement("Mapping : " + entityTypeData.mapping));
        table.add(mapping);

        if (entityTypeData.attributes.size() > 0) {
          RowElement attributesLabel = new RowElement();
          attributesLabel.add(new LabelElement("Attributes : "));
          table.add(attributesLabel);

          TableElement attributeTable = new TableElement();
          attributeTable.setRightCellPadding(1);
          RowElement attributeRowHeader = new RowElement(true);
          attributeRowHeader.add(new LabelElement("NAME"), new LabelElement("TYPE"), new LabelElement("ASSOCIATION"), new LabelElement("COLLECTION"), new LabelElement("MAPPING"));
          attributeTable.add(attributeRowHeader);

          for (AttributeData attributes : entityTypeData.attributes) {
            RowElement row = new RowElement();
            row.add(new LabelElement(attributes.name), new LabelElement(attributes.type), new LabelElement(attributes.association), new LabelElement(attributes.collection), new LabelElement(attributes.mapping));
            attributeTable.add(row);
          }

          RowElement attributesRow = new RowElement();
          attributesRow.add(attributeTable);
          table.add(attributesRow);

        }
      }

    }

    return table.renderer();
  
  }

  public static class EntityTypeData {

    public final String name;
    public final String type;
    public final String mapping;
    public final boolean verbose;
    public final List<AttributeData> attributes;

    public EntityTypeData(String name, String type, String mapping) {
      this(name, type, mapping, false);
    }

    public EntityTypeData(String name, String type, String mapping, boolean verbose) {
      this.name = name;
      this.type = type;
      this.mapping = mapping;
      this.verbose = verbose;
      this.attributes = new ArrayList<AttributeData>();
    }

    public void add(AttributeData d) {
      attributes.add(d);
    }
    
  }

  public static class AttributeData {
    public final String name;
    public final String type;
    public final Boolean association;
    public final Boolean collection;
    public final String mapping;

    public AttributeData(String name, String type, Boolean association, Boolean collection, String mapping) {
      this.name = name;
      this.type = type;
      this.association = (association != null ? association : false);
      this.collection = (collection != null ? collection : false);
      this.mapping = mapping;
    }
    
  }
}
