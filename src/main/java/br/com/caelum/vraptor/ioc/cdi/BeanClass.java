package br.com.caelum.vraptor.ioc.cdi;

import com.thoughtworks.xstream.annotations.XStreamAlias;

@XStreamAlias("bean")
public class BeanClass {

	private String name;

	public BeanClass() {

	}

	public BeanClass(Class<?> klass) {
		this.name = klass.getCanonicalName();
	}

	public String getName() {
		return name;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		BeanClass other = (BeanClass) obj;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return name;
	}
}
