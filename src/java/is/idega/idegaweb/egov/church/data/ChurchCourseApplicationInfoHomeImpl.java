package is.idega.idegaweb.egov.church.data;


import javax.ejb.CreateException;
import javax.ejb.FinderException;
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
}