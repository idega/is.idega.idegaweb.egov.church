package is.idega.idegaweb.egov.church.business;

import is.idega.idegaweb.egov.church.ChurchConstants;
import is.idega.idegaweb.egov.course.business.CourseBusinessBean;
import is.idega.idegaweb.egov.course.data.Course;
import is.idega.idegaweb.egov.course.data.CourseType;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import com.idega.business.IBOLookup;
import com.idega.business.IBOLookupException;
import com.idega.idegaweb.IWMainApplication;
import com.idega.idegaweb.IWResourceBundle;
import com.idega.presentation.IWContext;
import com.idega.util.CoreUtil;
import com.idega.util.ListUtil;

/**
 * Implementation of {@link ChurchCoursesHelper} to provide data for Church process(es)
 * 
 * @author <a href="mailto:valdas@idega.com">Valdas Žemaitis</a>
 * @version $Revision: 1.1 $
 *
 * Last modified: $Date: 2009/05/15 11:39:24 $ by: $Author: valdas $
 */
@Scope(BeanDefinition.SCOPE_SINGLETON)
@Service(ChurchCoursesHelper.BEAN_IDENTIFIER)
public class ChurchCoursesHelperImp implements ChurchCoursesHelper {

	private static final Logger LOGGER = Logger.getLogger(ChurchCoursesHelperImp.class.getName());
	
	public  Map<Locale, Map<String, String>> getAvailableCourses() {
		IWContext iwc = CoreUtil.getIWContext();
		if (iwc == null) {
			LOGGER.warning(IWContext.class + " is not available!");
			return null;
		}
		IWResourceBundle iwrb = iwc.getIWMainApplication().getBundle(ChurchConstants.IW_BUNDLE_IDENTIFIER).getResourceBundle(iwc);
		
		Locale locale =	iwc.getCurrentLocale();
		if (locale == null) {
			locale = Locale.ENGLISH;
		}
		
		String fakeKey = String.valueOf(-1);
		String noResultsLocKey = "sorry_no_course_available";
		String noResultsLocValue = "Sorry, there are no courses available";
		
		CourseBusinessBean courseBusiness = getCourseBusiness();
		if (courseBusiness == null) {
			return getEmptyResultsList(locale, iwrb, fakeKey, noResultsLocKey, noResultsLocValue);
		}
		
		Collection<CourseType> allCourses = null;
		try {
			 allCourses = courseBusiness.getCourseTypeHome().findAll();
		} catch(Exception e) {
			LOGGER.log(Level.WARNING, "Error getting courses types", e);
		}
		if (ListUtil.isEmpty(allCourses)) {
			return getEmptyResultsList(locale, iwrb, fakeKey, noResultsLocKey, noResultsLocValue);
		}
		
		String churchCourseKey = IWMainApplication.getDefaultIWMainApplication().getSettings().getProperty("church_course_key", ChurchConstants.CHURCH_COURSE_KEY);
		Collection<String> typesIds = new ArrayList<String>(allCourses.size());
		for (CourseType type: allCourses) {
			if (churchCourseKey.equals(type.getAbbreviation())) {
				typesIds.add(type.getPrimaryKey().toString());
			}
		}
		
		Collection<Course> courses = null;
		try {
			courses = courseBusiness.getCoursesByTypes(typesIds);
		} catch (RemoteException e) {
			LOGGER.log(Level.WARNING, "No courses found by types: " + typesIds, e);
		}
		if (ListUtil.isEmpty(courses)) {
			return getEmptyResultsList(locale, iwrb, fakeKey, noResultsLocKey, noResultsLocValue);
		}

		String placesLabel = iwrb.getLocalizedString("free_places_for_course", "free places");
		
		Map<String, String> realValues = new HashMap<String, String>();
		for (Course course: courses) {
			int freePlaces = course.getFreePlaces();
			if (isCourseAvailable(course, freePlaces)) {
				realValues.put(course.getPrimaryKey().toString(), getCourseLabel(locale, course, placesLabel, freePlaces));
			}
		}
		if (realValues.isEmpty()) {
			return getEmptyResultsList(locale, iwrb, fakeKey, noResultsLocKey, noResultsLocValue);
		}
		
		Map<Locale, Map<String, String>> availableCourses = new HashMap<Locale, Map<String,String>>(1);
		availableCourses.put(locale, realValues);
		return availableCourses;
	}
	
	private Map<Locale, Map<String, String>> getEmptyResultsList(Locale locale, IWResourceBundle iwrb, String key, String locKey, String locValue) {
		Map<Locale, Map<String, String>> emptyResults = new HashMap<Locale, Map<String,String>>(1);
		
		Map<String, String> emptyResult = new HashMap<String, String>(1);
		emptyResult.put(key, iwrb.getLocalizedString(locKey, locValue));
		emptyResults.put(locale, emptyResult);
		
		return emptyResults;
	}
	
	private boolean isCourseAvailable(Course course, int freePlaces) {
		return freePlaces > 0 && freePlaces <= course.getMax();
	}
	
	private String getCourseLabel(Locale locale, Course course, String labelAboutFreePlaces, int freePlaces) {
		return new StringBuilder(course.getName()).append(" (").append(labelAboutFreePlaces).append(": ").append(freePlaces).append(")").toString();
	}
	
	private CourseBusinessBean getCourseBusiness() {
		try {
			return IBOLookup.getServiceInstance(IWMainApplication.getDefaultIWApplicationContext(), CourseBusinessBean.class);
		} catch (IBOLookupException e) {
			LOGGER.log(Level.SEVERE, "Error getting " + CourseBusinessBean.class, e);
		}
		return null;
	}
	
}
