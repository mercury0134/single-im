package org.mercury.im.link.core.infra.ws.standard;

import org.mercury.im.link.core.infra.ws.annotation.ServerEndpoint;
import org.springframework.beans.factory.config.BeanDefinitionHolder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.annotation.ClassPathBeanDefinitionScanner;
import org.springframework.core.type.filter.AnnotationTypeFilter;

import java.util.Set;

public class EndpointClassPathScanner extends ClassPathBeanDefinitionScanner {

    public EndpointClassPathScanner(BeanDefinitionRegistry registry, boolean useDefaultFilters) {
        super(registry, useDefaultFilters);
    }

    @Override
    protected Set<BeanDefinitionHolder> doScan(String... basePackages) {
        addIncludeFilter(new AnnotationTypeFilter(ServerEndpoint.class));
        return super.doScan(basePackages);
    }
}