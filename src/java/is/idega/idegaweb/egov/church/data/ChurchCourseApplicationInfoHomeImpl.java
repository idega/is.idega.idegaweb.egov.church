package is.idega.idegaweb.egov.church.data;


import javax.ejb.CreateException;
import javax.ejb.FinderException;
import is.idega.idegaweb.egov.course.data.CourseApplication;
import com.idega.data.IDOEntity;
import com.idega.data.IDOFactory;

public class ChurchCourseApplicationInfoHomeImpl extends IDOFactory implements
		ChurchCourseApplicationInfoHome {
	public Class getEntityInterfaceClass() {
		return ChurchCourseApplicationInfo.class;
	}

	public ChurchCourseApplicationInfo create() throws CreateException {
		return (ChurchCourseApplicationInfo) super.createIDO();
	}

	public ChurchCourseApplicationInfo findByPrimaryKey(Object pk)
			throws FinderException {
		return (ChurchCourseApplicationInfo) super.findByPrimaryKeyIDO(pk);
	}

	public ChurchCourseApplicationInfo findByApplication(
			CourseApplication application) throws FinderException {
		IDOEntity entity = this.idoCheckOutPooledEntity();
		Object pk = ((ChurchCourseApplicationInfoBMPBean) entity)
				.ejbFindByApplication(application);
		this.idoCheckInPooledEntity(entity);
		return this.findByPrimaryKey(pk);
	}
}