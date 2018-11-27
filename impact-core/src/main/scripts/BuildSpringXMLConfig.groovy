/**
 * Generate the Spring XML configuration.
 */
import groovy.xml.MarkupBuilder

import org.springframework.beans.factory.config.BeanDefinition
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider
import org.springframework.core.type.filter.AnnotationTypeFilter
import org.springframework.stereotype.Repository
import java.util.Date;

def writer = new StringWriter()

def xml = new MarkupBuilder(writer)

def ClassPathScanningCandidateComponentProvider scanner = 
	new ClassPathScanningCandidateComponentProvider(false);
scanner.addIncludeFilter(new AnnotationTypeFilter(Repository.class));

def packages = ["us.oh.state.epa.stars2","us.wy.state.deq.impact"]

def warning = "<!-- Generated %s by BuildSpringXMLConfig.groovy. DO NOT HAND EDIT. -->\n"
def tstamp = new Date()

printf(warning, tstamp)
xml.beans(xmlns: "http://www.springframework.org/schema/beans", 
	'xmlns:xsi': "http://www.w3.org/2001/XMLSchema-instance",
	'xmlns:p': "http://www.springframework.org/schema/p", 
	'xmlns:context': "http://www.springframework.org/schema/context",
	'xmlns:tx': "http://www.springframework.org/schema/tx", 
	'xmlns:jpa': "http://www.springframework.org/schema/data/jpa",
	'xmlns:jee': "http://www.springframework.org/schema/jee",
	'xsi:schemaLocation': "http://www.springframework.org/schema/beans http://www.springframework.org/schema/beans/spring-beans.xsd \
		http://www.springframework.org/schema/data/jpa http://www.springframework.org/schema/data/jpa/spring-jpa.xsd \
		http://www.springframework.org/schema/tx http://www.springframework.org/schema/tx/spring-tx.xsd \
		http://www.springframework.org/schema/context http://www.springframework.org/schema/context/spring-context.xsd \
		http://www.springframework.org/schema/jee http://www.springframework.org/schema/jee/spring-jee.xsd"
		) {

	'context:component-scan'('base-package': packages.join(", "))
	'context:annotation-config'()
	'context:load-time-weaver'()

	'tx:annotation-driven'('transaction-manager': "transactionManager", mode: 'aspectj')
	
	bean(id: "transactionManager", 
			class: "org.springframework.jdbc.datasource.DataSourceTransactionManager") {
		property(name: "dataSource", ref: "dataSource")
	}
	bean(id: "retryAspect",
			class: "us.wy.state.deq.impact.aspects.RetryAspect",
			'factory-method': "aspectOf") {
		property(name: "maxRetries", value: "2")
	}

	'jee:jndi-lookup'(id: "dataSource",
	   'jndi-name': "jdbc/impactDS",
	   'expected-type': "javax.sql.DataSource")
	
	for (pkg in packages) {
		for (BeanDefinition bd : scanner.findCandidateComponents(pkg)) {
			def cn = bd.beanClassName
			def cnsplit = cn.split("\\.")
			def bn = cnsplit[cnsplit.length-1].replaceAll("(SQLDAO)", "DAO")
			bean(id: "readOnly" + bn, class: cn) {
				property(name: "schema", value: "#{T(us.oh.state.epa.stars2.CommonConst).READONLY_SCHEMA}")
			}
			bean(id: "staging" + bn, class: cn) {
				property(name: "schema", value: "#{T(us.oh.state.epa.stars2.CommonConst).STAGING_SCHEMA}")
			}
		}
	}
}

def out = writer.toString()

println out
printf(warning, tstamp)


