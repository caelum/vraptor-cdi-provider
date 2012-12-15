package br.com.caelum.vraptor.ioc.cdi;

import java.util.List;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Instance;
import javax.enterprise.inject.Produces;
import javax.inject.Inject;

import com.google.common.collect.Lists;
import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.SingleValueConverter;

@ApplicationScoped
public class XStreamConvertersFactory {
	
	private Instance<Converter> possibleConverters;
	private Instance<SingleValueConverter> possibleSingleValueConverters;

	@Inject
	public XStreamConvertersFactory(Instance<Converter> possibleConverters,Instance<SingleValueConverter> possibleSingleValueConverters) {
		this.possibleConverters = possibleConverters;
		this.possibleSingleValueConverters = possibleSingleValueConverters;
	}
	
	@Deprecated
	public XStreamConvertersFactory() {
	}

	@Produces
	public List<Converter> getConverters(){
		return Lists.newArrayList(possibleConverters);
	}
	
	@Produces
	public List<SingleValueConverter> getSingleValueConverters(){
		return Lists.newArrayList(possibleSingleValueConverters);
	}
}
