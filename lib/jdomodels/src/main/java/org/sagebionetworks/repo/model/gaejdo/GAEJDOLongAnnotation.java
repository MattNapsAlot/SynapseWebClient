package org.sagebionetworks.repo.model.gaejdo;

import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

import com.google.appengine.api.datastore.Key;

/**
 * This is the persistable class for a Annotations whose values are long integer numbers.
 * 
 * Note: equals and hashcode are based on the attribute and value, allowing
 * distinct annotations with the same attribute.
 * 
 * @author bhoff
 * 
 */
@PersistenceCapable(detachable = "false")
public class GAEJDOLongAnnotation implements GAEJDOAnnotation<Long> {
	@PrimaryKey
	@Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
	private Key id;

	@Persistent
	private GAEJDOAnnotations owner; // this is the backwards pointer for the
										// 1-1 owned relationship

	@Persistent
	private String attribute;

	@Persistent
	private Long value;

	public GAEJDOLongAnnotation() {
	}

	public GAEJDOLongAnnotation(String attr, Long value) {
		setAttribute(attr);
		setValue(value);
	}

	public String toString() {
		return getAttribute() + ": " + getValue();
	}

	public Key getId() {
		return id;
	}

	public void setId(Key id) {
		this.id = id;
	}

	public GAEJDOAnnotations getOwner() {
		return owner;
	}

	public void setOwner(GAEJDOAnnotations owner) {
		this.owner = owner;
	}

	public String getAttribute() {
		return attribute;
	}

	public void setAttribute(String attribute) {
		this.attribute = attribute;
	}

	public Long getValue() {
		return value;
	}

	public void setValue(Long value) {
		this.value = value;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (!(obj instanceof GAEJDOLongAnnotation))
			return false;
		GAEJDOLongAnnotation other = (GAEJDOLongAnnotation) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}

}