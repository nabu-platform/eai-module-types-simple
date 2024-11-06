/*
* Copyright (C) 2016 Alexander Verbruggen
*
* This program is free software: you can redistribute it and/or modify
* it under the terms of the GNU Lesser General Public License as published by
* the Free Software Foundation, either version 3 of the License, or
* (at your option) any later version.
*
* This program is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
* GNU Lesser General Public License for more details.
*
* You should have received a copy of the GNU Lesser General Public License
* along with this program. If not, see <https://www.gnu.org/licenses/>.
*/

package be.nabu.eai.module.types.simple;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import be.nabu.eai.developer.MainController;
import be.nabu.eai.developer.managers.base.BaseJAXBGUIManager;
import be.nabu.eai.developer.util.ElementSelectionListener;
import be.nabu.eai.repository.resources.RepositoryEntry;
import be.nabu.libs.property.api.Property;
import be.nabu.libs.property.api.Value;
import be.nabu.libs.types.api.DefinedSimpleType;
import be.nabu.libs.types.api.SimpleType;

public class SimpleTypeGUIManager extends BaseJAXBGUIManager<SimpleTypeConfiguration, SimpleTypeArtifact<Object>> {

	public SimpleTypeGUIManager() {
		super("Simple Type", new SimpleTypeManager().getArtifactClass(), new SimpleTypeManager(), SimpleTypeConfiguration.class);
	}

	@Override
	protected List<Property<?>> getCreateProperties() {
		List<Property<?>> properties = new ArrayList<Property<?>>();
		ElementSelectionListener.TypeProperty property = new ElementSelectionListener.TypeProperty();
		properties.add(property);
		return properties;
	}

	@Override
	protected SimpleTypeArtifact<Object> newInstance(MainController controller, RepositoryEntry entry, Value<?>... values) throws IOException {
		SimpleTypeArtifact<Object> instance = new SimpleTypeArtifact<Object>(entry.getId(), entry, entry.getRepository());
		for (Value<?> value : values) {
			if (value.getProperty() instanceof ElementSelectionListener.TypeProperty) {
				SimpleType<?> type = (SimpleType<?>) value.getValue();
				instance.getConfiguration().setParent(type instanceof DefinedSimpleType ? ((DefinedSimpleType<?>) type).getId() : type.getInstanceClass().getName());
			}
		}
		return instance;
	}

	@Override
	public <V> V getValue(SimpleTypeArtifact<Object> instance, Property<V> property) {
		if (property.getName().equals("properties")) {
			for (Property<?> supported : instance.getParent().getSupportedProperties()) {
				try {
					if (!instance.getConfiguration().getProperties().containsKey(supported.getName())) {
						instance.getConfiguration().getProperties().put(supported.getName(), null);
					}
				}
				catch (Exception e) {
					throw new RuntimeException(e);
				}
			}
		}
		return super.getValue(instance, property);
	}

	@Override
	public String getCategory() {
		return "Types";
	}
}
