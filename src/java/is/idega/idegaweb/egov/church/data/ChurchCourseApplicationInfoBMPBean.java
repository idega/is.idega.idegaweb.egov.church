package is.idega.idegaweb.egov.church.data;

import is.idega.idegaweb.egov.course.data.CourseApplication;
import is.idega.idegaweb.egov.course.data.CourseProvider;

import javax.ejb.FinderException;

import com.idega.block.school.data.School;
import com.idega.data.GenericEntity;
import com.idega.data.query.MatchCriteria;
import com.idega.data.query.SelectQuery;
import com.idega.data.query.Table;
import com.idega.user.data.User;

public class ChurchCourseApplicationInfoBMPBean extends GenericEntity implements
		ChurchCourseApplicationInfo {

	private static final String ENTITY_NAME = "cou_church_course_info";
	
	private static final String COLUMN_COURSE_APPLICATION = "application_id";
	private static final String COLUMN_EXTRA_CONTACT = "extra_contact_id";
	private static final String COLUMN_CHRISTENING_DATE = "christening_date";
	private static final String COLUMN_MOTHERS_NAME = "mothers_name";
	private static final String COLUMN_FATHERS_NAME = "father_name";
	private static final String COLUMN_RELIGION = "religion";
	private static final String COLUMN_SCHOOL = "school_id";
	private static final String COLUMN_CONTACT_RELATION = "contact_relation";
	private static final String COLUMN_EXTRA_CONTACT_RELATION = "extra_contact_relation";
	private static final String COLUMN_INFO = "info";
	
	private static final String COLUMN_EDUCATION_GROUP = "education_group"; //not a real group, just an integer used for reports
	
	@Override
	public String getEntityName() {
		return ENTITY_NAME;
	}

	@Override
	public void initializeAttributes() {
		addAttribute(getIDColumnName());
		addOneToOneRelationship(COLUMN_COURSE_APPLICATION, CourseApplication.class);
		addManyToOneRelationship(COLUMN_EXTRA_CONTACT, User.class);
		addAttribute(COLUMN_CHRISTENING_DATE, "", String.class);
		addAttribute(COLUMN_MOTHERS_NAME, "", String.class);
		addAttribute(COLUMN_FATHERS_NAME, "", String.class);
		addAttribute(COLUMN_RELIGION, "", String.class);
		addManyToOneRelationship(COLUMN_SCHOOL, School.class);
		addAttribute(COLUMN_CONTACT_RELATION, "", String.class);
		addAttribute(COLUMN_EXTRA_CONTACT_RELATION, "", String.class);
		addAttribute(COLUMN_INFO, "", String.class, 1000);
		addAttribute(COLUMN_EDUCATION_GROUP, "", Integer.class);
	}

	//getters
	public CourseApplication getApplication() {
		return (CourseApplication) getColumnValue(COLUMN_COURSE_APPLICATION);
	}
	
	public User getExtraContact() {
		return (User) getColumnValue(COLUMN_EXTRA_CONTACT);
	}
	
	public String getChristeningDate() {
		return getStringColumnValue(COLUMN_CHRISTENING_DATE);
	}
	
	public String getMothersName() {
		return getStringColumnValue(COLUMN_MOTHERS_NAME);
	}
	
	public String getFathersName() {
		return getStringColumnValue(COLUMN_FATHERS_NAME);
	}
	
	public String getReligion() {
		return getStringColumnValue(COLUMN_RELIGION);
	}
	
	public CourseProvider getSchool() {
		return (CourseProvider) getColumnValue(COLUMN_SCHOOL);
	}
	
	public String getContactRelation() {
		return getStringColumnValue(COLUMN_CONTACT_RELATION);
	}
	
	public String getExtraContactRelation() {
		return getStringColumnValue(COLUMN_EXTRA_CONTACT_RELATION);
	}
	
	public String getInfo() {
		return getStringColumnValue(COLUMN_INFO);
	}
	
	public int getEducationGroup() {
		return getIntColumnValue(COLUMN_EDUCATION_GROUP, 0);
	}
	
	//setters
	public void setApplication(CourseApplication application) {
		setColumn(COLUMN_COURSE_APPLICATION, application);
	}
	
	public void setExtraContact(User contact) {
		setColumn(COLUMN_EXTRA_CONTACT, contact);
	}
	
	public void setChristeningDate(String date) {
		setColumn(COLUMN_CHRISTENING_DATE, date);
	}
	
	public void setMothersName(String name) {
		setColumn(COLUMN_MOTHERS_NAME, name);
	}
	
	public void setFathersName(String name) {
		setColumn(COLUMN_FATHERS_NAME, name);
	}
	
	public void setReligion(String religion) {
		setColumn(COLUMN_RELIGION, religion);
	}
	
	public void setSchool(int schoolId) {
		setColumn(COLUMN_SCHOOL, schoolId);
	}
	
	public void setContactRelation(String relation) {
		setColumn(COLUMN_CONTACT_RELATION, relation);
	}
	
	public void setExtraContactRelation(String relation) {
		setColumn(COLUMN_EXTRA_CONTACT_RELATION, relation);
	}
	
	public void setInfo(String info) {
		setColumn(COLUMN_INFO, info);
	}

	public void setEducationGroup(int group) {
		setColumn(COLUMN_EDUCATION_GROUP, group);
	}
	
	//ejb
	public Object ejbFindByApplication(CourseApplication application) throws FinderException {
		Table table = new Table(this);

		SelectQuery query = new SelectQuery(table);
		query.addColumn(table.getColumn(getIDColumnName()));
		query.addCriteria(new MatchCriteria(table.getColumn(COLUMN_COURSE_APPLICATION), MatchCriteria.EQUALS, application));

		return idoFindOnePKByQuery(query);
	}
}