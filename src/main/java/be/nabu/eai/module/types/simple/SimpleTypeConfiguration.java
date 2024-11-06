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

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import be.nabu.eai.api.ValueEnumerator;
import be.nabu.eai.repository.util.KeyValueMapAdapter;

@XmlRootElement(name = "simpleType")
@XmlType(propOrder = { "parent", "enumerations", "properties" })
public class SimpleTypeConfiguration {
	private String parent;
	private List<String> enumerations;
	private Map<String, String> properties;
	
	@ValueEnumerator(enumerator = SimpleTypeEnumerator.class)
	public String getParent() {
		return parent;
	}
	public void setParent(String parent) {
		this.parent = parent;
	}
	
	public List<String> getEnumerations() {
		return enumerations;
	}
	public void setEnumerations(List<String> enumerations) {
		this.enumerations = enumerations;
	}
	
	@XmlJavaTypeAdapter(KeyValueMapAdapter.class)
	public Map<String, String> getProperties() {
		// always has to have a value because it is then passed by reference to the maincontroller and the updates to it can be seen
		if (properties == null) {
			properties = new LinkedHashMap<String, String>();
		}
		return properties;
	}
	
	public void setProperties(Map<String, String> properties) {
		this.properties = properties;
	}
}
