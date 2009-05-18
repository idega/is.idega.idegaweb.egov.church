package is.idega.idegaweb.egov.church.data;


import com.idega.block.school.data.School;
import com.idega.user.data.User;
import is.idega.idegaweb.egov.course.data.CourseApplication;
import com.idega.data.IDOEntity;

public interface ChurchCourseApplicationInfo extends IDOEntity {
	/**
	 * @see is.idega.idegaweb.egov.church.data.ChurchCourseApplicationInfoBMPBean#getApplication
	 */
	public CourseApplication getApplication();

	/**
	 * @see is.idega.idegaweb.egov.church.data.ChurchCourseApplicationInfoBMPBean#getExtraContact
	 */
	public User getExtraContact();

	/**
	 * @see is.idega.idegaweb.egov.church.data.ChurchCourseApplicationInfoBMPBean#getChristeningDate
	 */
	public String getChristeningDate();

	/**
	 * @see is.idega.idegaweb.egov.church.data.ChurchCourseApplicationInfoBMPBean#getMothersName
	 */
	public String getMothersName();

	/**
	 * @see is.idega.idegaweb.egov.church.data.ChurchCourseApplicationInfoBMPBean#getFathersName
	 */
	public String getFathersName();

	/**
	 * @see is.idega.idegaweb.egov.church.data.ChurchCourseApplicationInfoBMPBean#getReligion
	 */
	public String getReligion();

	/**
	 * @see is.idega.idegaweb.egov.church.data.ChurchCourseApplicationInfoBMPBean#getSchool
	 */
	public School getSchool();

	/**
	 * @see is.idega.idegaweb.egov.church.data.ChurchCourseApplicationInfoBMPBean#getContactRelation
	 */
	public String getContactRelation();

	/**
	 * @see is.idega.idegaweb.egov.church.data.ChurchCourseApplicationInfoBMPBean#getExtraContactRelation
	 */
	public String getExtraContactRelation();

	/**
	 * @see is.idega.idegaweb.egov.church.data.ChurchCourseApplicationInfoBMPBean#getInfo
	 */
	public String getInfo();

	/**
	 * @see is.idega.idegaweb.egov.church.data.ChurchCourseApplicationInfoBMPBean#setApplication
	 */
	public void setApplication(CourseApplication application);

	/**
	 * @see is.idega.idegaweb.egov.church.data.ChurchCourseApplicationInfoBMPBean#setExtraContact
	 */
	public void setExtraContact(User contact);

	/**
	 * @see is.idega.idegaweb.egov.church.data.ChurchCourseApplicationInfoBMPBean#setChristeningDate
	 */
	public void setChristeningDate(String date);

	/**
	 * @see is.idega.idegaweb.egov.church.data.ChurchCourseApplicationInfoBMPBean#setMothersName
	 */
	public void setMothersName(String name);

	/**
	 * @see is.idega.idegaweb.egov.church.data.ChurchCourseApplicationInfoBMPBean#setFathersName
	 */
	public void setFathersName(String name);

	/**
	 * @see is.idega.idegaweb.egov.church.data.ChurchCourseApplicationInfoBMPBean#setReligion
	 */
	public void setReligion(String religion);

	/**
	 * @see is.idega.idegaweb.egov.church.data.ChurchCourseApplicationInfoBMPBean#setSchool
	 */
	public void setSchool(int schoolId);

	/**
	 * @see is.idega.idegaweb.egov.church.data.ChurchCourseApplicationInfoBMPBean#setContactRelation
	 */
	public void setContactRelation(String relation);

	/**
	 * @see is.idega.idegaweb.egov.church.data.ChurchCourseApplicationInfoBMPBean#setExtraContactRelation
	 */
	public void setExtraContactRelation(String relation);

	/**
	 * @see is.idega.idegaweb.egov.church.data.ChurchCourseApplicationInfoBMPBean#setInfo
	 */
	public void setInfo(String info);
}