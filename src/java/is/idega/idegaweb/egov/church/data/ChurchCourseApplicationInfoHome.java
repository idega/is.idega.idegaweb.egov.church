package is.idega.idegaweb.egov.church.data;


import javax.ejb.CreateException;
import com.idega.data.IDOHome;
import javax.ejb.FinderException;

public interface ChurchCourseApplicationInfoHome extends IDOHome {
	public ChurchCourseApplicationInfo create() throws CreateException;

	public ChurchCourseApplicationInfo findByPrimaryKey(Object pk)
			throws FinderException;
}