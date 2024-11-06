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
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import be.nabu.eai.repository.api.Repository;
import be.nabu.eai.repository.artifacts.jaxb.JAXBArtifact;
import be.nabu.libs.converter.ConverterFactory;
import be.nabu.libs.property.PropertyFactory;
import be.nabu.libs.property.api.Property;
import be.nabu.libs.property.api.Value;
import be.nabu.libs.resources.api.ResourceContainer;
import be.nabu.libs.types.api.DefinedSimpleType;
import be.nabu.libs.types.api.Marshallable;
import be.nabu.libs.types.api.SimpleType;
import be.nabu.libs.types.api.Type;
import be.nabu.libs.types.api.Unmarshallable;
import be.nabu.libs.types.base.ValueImpl;
import be.nabu.libs.types.converters.StringToSimpleType;
import be.nabu.libs.types.properties.EnumerationProperty;
import be.nabu.libs.validator.api.Validator;

public class SimpleTypeArtifact<T> extends JAXBArtifact<SimpleTypeConfiguration> implements DefinedSimpleType<T>, Marshallable<T>, Unmarshallable<T> {

	private SimpleType<T> parent;
	
	public SimpleTypeArtifact(String id, ResourceContainer<?> directory, Repository repository) {
		super(id, directory, repository, "simpleType.xml", SimpleTypeConfiguration.class);
	}

	@SuppressWarnings("unchecked")
	public SimpleType<T> getParent() {
		if (parent == null) {
			synchronized(this) {
				if (parent == null) {
					try {
						parent = new StringToSimpleType().convert(getConfiguration().getParent() != null && !getId().equals(getConfiguration().getParent()) ? getConfiguration().getParent() : "java.lang.String");
					}
					catch (IOException e) {
						throw new RuntimeException(e);
					}
				}
			}
		}
		return parent;
	}
	
	@Override
	public void save(ResourceContainer<?> directory) throws IOException {
		// reset parent so we can recalculate it
		parent = null;
		super.save(directory);
	}

	@Override
	public String marshal(T object, Value<?>... values) {
		return object == null ? null : ((Marshallable<T>) getParent()).marshal(object);
	}
	
	@Override
	public T unmarshal(String content, Value<?>... values) {
		return content == null ? null : ((Unmarshallable<T>) getParent()).unmarshal(content, values);
	}

	@Override
	public String getName(Value<?>...values) {
		String name = getConfig().getProperties().get("name");
		if (name == null || name.isEmpty()) {
			name = getId().replaceAll("^.*\\.([^.]+)$", "$1");
		}
		return name;
	}

	@Override
	public String getNamespace(Value<?>...values) {
		return getConfig().getProperties().get("namespace");
	}

	@Override
	public Class<T> getInstanceClass() {
		return getParent().getInstanceClass();
	}

	@Override
	public boolean isList(Value<?>... properties) {
		return getParent().isList(properties);
	}

	@Override
	public Validator<?> createValidator(Value<?>... properties) {
		return getParent().createValidator(properties);
	}

	@Override
	public Validator<Collection<?>> createCollectionValidator(Value<?>... properties) {
		return getParent().createCollectionValidator(properties);
	}

	@Override
	public Set<Property<?>> getSupportedProperties(Value<?>... properties) {
		return getParent().getSupportedProperties(properties);
	}

	@Override
	public Type getSuperType() {
		return getParent();
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public Value<?>[] getProperties() {
		Map<Property<?>, Value<?>> properties = new LinkedHashMap<Property<?>, Value<?>>();
		try {
			if (getConfiguration().getProperties() != null && !getConfiguration().getProperties().isEmpty()) {
				for (String propertyName : getConfiguration().getProperties().keySet()) {
					Property<?> property = PropertyFactory.getInstance().getProperty(propertyName);
					if (property != null) {
						String string = getConfiguration().getProperties().get(propertyName);
						if (string != null && !string.trim().isEmpty()) {
							properties.put(property, new ValueImpl(property, ConverterFactory.getInstance().getConverter().convert(string, property.getValueClass())));
						}
					}
				}
			}
			if (getConfiguration().getEnumerations() != null && !getConfiguration().getEnumerations().isEmpty()) {
				EnumerationProperty<T> enumerationProperty = new EnumerationProperty<T>();
				List<T> values = new ArrayList<T>();
				for (String enumeration : getConfiguration().getEnumerations()) {
					values.add(ConverterFactory.getInstance().getConverter().convert(enumeration, getParent().getInstanceClass()));
				}
				properties.put(enumerationProperty, new ValueImpl<List<T>>(enumerationProperty, values));
			}
		}
		catch (IOException e) {
			throw new RuntimeException(e);
		}
		return properties.values().toArray(new Value[0]);
	}

}
