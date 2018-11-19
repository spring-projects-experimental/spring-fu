package org.springframework.fu.jafu.beans;

public class BeanWithDependency {

	private final SimpleBean simpleBean;

	public BeanWithDependency(SimpleBean simpleBean) {
		this.simpleBean = simpleBean;
	}

}
