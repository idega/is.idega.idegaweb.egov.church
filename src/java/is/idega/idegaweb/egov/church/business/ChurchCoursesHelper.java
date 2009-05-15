package is.idega.idegaweb.egov.church.business;

import java.util.Locale;
import java.util.Map;

/**
 * Helper interface for Church process(es)
 * 
 * @author <a href="mailto:valdas@idega.com">Valdas Žemaitis</a>
 * @version $Revision: 1.1 $
 *
 * Last modified: $Date: 2009/05/15 11:39:24 $ by: $Author: valdas $
 */
public interface ChurchCoursesHelper {

	public static final String BEAN_IDENTIFIER = "churchCoursesHelper";

	public  Map<Locale, Map<String, String>> getAvailableCourses();
	
}
