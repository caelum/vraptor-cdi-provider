package br.com.caelum.vraptor.ioc.cdi;

import java.util.List;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Instance;
import javax.enterprise.inject.Produces;
import javax.inject.Inject;

import br.com.caelum.vraptor.serialization.Serialization;

import com.google.common.collect.Lists;

@ApplicationScoped
public class SerializationsFactory {

	private Instance<Serialization> serializations;

	@Inject
	public SerializationsFactory(Instance<Serialization> serializations) {
		this.serializations = serializations;
	}

	@Deprecated
	public SerializationsFactory() {
	}
	
	@Produces
	public List<Serialization> getSerializations() {
		return Lists.newArrayList(serializations);
	}

}
