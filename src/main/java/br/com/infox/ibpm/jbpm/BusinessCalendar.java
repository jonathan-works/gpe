package br.com.infox.ibpm.jbpm;

import java.util.Properties;

import org.jbpm.util.ClassLoaderUtil;

public class BusinessCalendar {
	
	private static Properties jbpmCalendarProperties = ClassLoaderUtil
	   										.getProperties("jbpm.business.calendar.properties");
	private static org.jbpm.calendar.BusinessCalendar businessCalendar = 
											new	org.jbpm.calendar.BusinessCalendar(jbpmCalendarProperties);

	private BusinessCalendar() {
		
	}
	
	public static org.jbpm.calendar.BusinessCalendar instance() {
		return businessCalendar;
	}
	
}