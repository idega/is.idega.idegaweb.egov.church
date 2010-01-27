package is.idega.idegaweb.egov.church.presentation;

import is.idega.idegaweb.egov.church.business.ChurchCourseParticipantWriter;
import is.idega.idegaweb.egov.citizen.presentation.CitizenFinder;
import is.idega.idegaweb.egov.course.CourseConstants;
import is.idega.idegaweb.egov.course.business.CourseBusiness;
import is.idega.idegaweb.egov.course.data.Course;
import is.idega.idegaweb.egov.course.data.CourseChoice;
import is.idega.idegaweb.egov.course.data.CourseType;
import is.idega.idegaweb.egov.course.presentation.CourseParticipantsList;

import java.rmi.RemoteException;
import java.sql.Date;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import com.idega.block.school.data.SchoolType;
import com.idega.business.IBOLookup;
import com.idega.business.IBOLookupException;
import com.idega.business.IBORuntimeException;
import com.idega.core.contact.data.Phone;
import com.idega.core.location.data.Address;
import com.idega.core.location.data.PostalCode;
import com.idega.presentation.IWContext;
import com.idega.presentation.Layer;
import com.idega.presentation.Table2;
import com.idega.presentation.TableCell2;
import com.idega.presentation.TableRow;
import com.idega.presentation.TableRowGroup;
import com.idega.presentation.text.DownloadLink;
import com.idega.presentation.text.Link;
import com.idega.presentation.text.Text;
import com.idega.presentation.ui.DropdownMenu;
import com.idega.presentation.ui.Form;
import com.idega.presentation.ui.GenericButton;
import com.idega.presentation.ui.HiddenInput;
import com.idega.presentation.ui.Label;
import com.idega.user.data.User;
import com.idega.util.CoreConstants;
import com.idega.util.IWTimestamp;
import com.idega.util.PersonalIDFormatter;
import com.idega.util.PresentationUtil;
import com.idega.util.text.Name;

public class ChurchCourseParticipantList extends CourseParticipantsList {
	private static final String PARAMETER_CHOICE_REMOVE = "prm_remove_choice";
	private CourseType defaultCourseType = null;

	@Override
	public void present(IWContext iwc) {
		try {
			Form form = new Form();
			form.setID("courseList");
			form.setStyleClass("adminForm");
			form.setEventListener(this.getClass());

			if (iwc.isParameterSet(PARAMETER_CHOICE_REMOVE)) {
				removeChoiceFromCourse(iwc);
			}
			
			form.add(getNavigation(iwc));
			if (iwc.isParameterSet(PARAMETER_COURSE_PK) || this.getSession().getIsAllProvidersSelected()) {
				form.add(getPrintouts(iwc));
			}
			form.add(getParticipants(iwc));

			if (getBackPage() != null) {
				Layer buttonLayer = new Layer();
				buttonLayer.setStyleClass("buttonLayer");
				form.add(buttonLayer);

				GenericButton back = new GenericButton(localize("back", "Back"));
				back.setPageToOpen(getBackPage());
				buttonLayer.add(back);
			}

			add(form);
		}
		catch (RemoteException re) {
			throw new IBORuntimeException(re);
		}
	}

	private void removeChoiceFromCourse(IWContext iwc) {
			try {
				CourseChoice choice = getCourseBusiness(iwc)
						.getCourseChoice(
								new Integer(iwc
										.getParameter(PARAMETER_CHOICE_REMOVE)));
				
				getCourseBusiness(iwc).invalidateApplication(choice.getApplication(), iwc.getCurrentUser(), iwc.getCurrentLocale());
			} catch (RemoteException re) {
				re.printStackTrace();
			}
	}
	
	@Override
	protected Table2 getParticipants(IWContext iwc) throws RemoteException {
		Table2 table = new Table2();
		table.setStyleClass("adminTable");
		table.setStyleClass("ruler");
		table.setWidth("100%");
		table.setCellpadding(0);
		table.setCellspacing(0);

		TableRowGroup group = table.createHeaderRowGroup();
		TableRow row = group.createRow();
		TableCell2 cell = row.createHeaderCell();
		cell.setStyleClass("firstColumn");
		cell.setStyleClass("number");
		cell.add(Text.getNonBrakingSpace());

		int columns = 1;

		cell = row.createHeaderCell();
		cell.setStyleClass("name");
		cell.add(new Text(getResourceBundle().getLocalizedString("name", "Name")));
		columns++;

		cell = row.createHeaderCell();
		cell.setStyleClass("personalID");
		cell.add(new Text(getResourceBundle().getLocalizedString("personal_id", "Personal ID")));
		columns++;

		cell = row.createHeaderCell();
		cell.setStyleClass("address");
		cell.add(new Text(getResourceBundle().getLocalizedString("address", "Address")));
		columns++;

		cell = row.createHeaderCell();
		cell.setStyleClass("postalCode");
		cell.add(new Text(getResourceBundle().getLocalizedString("postal_code", "Postal code")));
		columns++;

		cell = row.createHeaderCell();
		cell.setStyleClass("homePhone");
		cell.add(new Text(getResourceBundle().getLocalizedString("home_phone", "Phone")));
		columns++;

		cell = row.createHeaderCell();
		cell.setStyleClass("lastColumn");
		cell.setStyleClass("deleteChoice");
		cell.add(Text.getNonBrakingSpace());
		columns++;

		group = table.createBodyRowGroup();
		int iRow = 1;

		Course course = null;
		CourseType type = null;
		Collection choices = new ArrayList();
		if (iwc.isParameterSet(PARAMETER_COURSE_PK)) {
			choices = getBusiness().getCourseChoices(iwc.getParameter(PARAMETER_COURSE_PK), false);
			course = getBusiness().getCourse(iwc.getParameter(PARAMETER_COURSE_PK));
			type = course.getCourseType();
			if (type.getAbbreviation() != null) {
				table.setStyleClass("abbr_" + type.getAbbreviation());
			}
		}

		Iterator iter = choices.iterator();
		while (iter.hasNext()) {
			row = group.createRow();

			CourseChoice choice = (CourseChoice) iter.next();
			User user = choice.getUser();
			Address address = getUserBusiness().getUsersMainAddress(user);
			PostalCode postalCode = null;
			if (address != null) {
				postalCode = address.getPostalCode();
			}
			Phone phone = getUserBusiness().getChildHomePhone(user);

			if (iRow == course.getMax()) {
				row.setStyleClass("lastAvailable");
			}
			else if (iRow == (course.getMax() + 1)) {
				row.setStyleClass("firstExceedingParticipant");
			}

			if (iRow > course.getMax()) {
				row.setStyleClass("exceedingParticipant");
			}

			cell = row.createCell();
			cell.setStyleClass("firstColumn");
			cell.setStyleClass("number");
			cell.add(new Text(String.valueOf(iRow)));

			Name name = new Name(user.getFirstName(), user.getMiddleName(), user.getLastName());

			cell = row.createCell();
			cell.setStyleClass("name");
			if (getResponsePage() != null) {
				Link link = new Link(name.getName(iwc.getCurrentLocale()));
				link.addParameter(PARAMETER_CHOICE_PK, choice.getPrimaryKey().toString());
				link.setPage(getResponsePage());

				cell.add(link);
			}
			else {
				cell.add(new Text(name.getName(iwc.getCurrentLocale())));
			}

			cell = row.createCell();
			cell.setStyleClass("personalID");
			if (getChangeEmailResponsePage() != null) {
				Link link = new Link(PersonalIDFormatter.format(user.getPersonalID(), iwc.getCurrentLocale()));
				link.setEventListener(CitizenFinder.class);
				if (user.getUniqueId() != null) {
					link.addParameter(PARAMETER_USER_UNIQUE_ID, user.getUniqueId());
				}
				else {
					link.addParameter(PARAMETER_USER_PK, user.getPrimaryKey().toString());
				}
				link.setPage(getChangeEmailResponsePage());
				
				cell.add(link);
			} else {
				cell.add(new Text(PersonalIDFormatter.format(user.getPersonalID(), iwc.getCurrentLocale())));
			}

			cell = row.createCell();
			cell.setStyleClass("address");
			if (address != null) {
				cell.add(new Text(address.getStreetAddress()));
			}
			else {
				cell.add(new Text(CoreConstants.MINUS));
			}

			cell = row.createCell();
			cell.setStyleClass("postalCode");
			if (postalCode != null) {
				cell.add(new Text(postalCode.getPostalAddress()));
			}
			else {
				cell.add(new Text(CoreConstants.MINUS));
			}

			cell = row.createCell();
			cell.setStyleClass("homePhone");
			if (phone != null) {
				cell.add(new Text(phone.getNumber()));
			}
			else {
				cell.add(new Text(CoreConstants.MINUS));
			}

			cell = row.createCell();
			cell.setStyleClass("deleteChoice");
			
			Link delete = new Link(getBundle().getImage("delete.png", localize("remove", "Remove")));
			delete.addParameter(PARAMETER_CHOICE_REMOVE, choice.getPrimaryKey().toString());
			delete.setClickConfirmation(getResourceBundle().getLocalizedString("choice.confirm_delete", "Are you sure you want to delete the choice selected?"));
			delete.maintainParameter(PARAMETER_COURSE_PK, iwc);
			cell.add(delete);

			if (iRow % 2 == 0) {
				row.setStyleClass("evenRow");
			}
			else {
				row.setStyleClass("oddRow");
			}
			iRow++;
		}

		group = table.createFooterRowGroup();
		row = group.createRow();

		cell = row.createCell();
		cell.setStyleClass("numberOfParticipants");
		cell.setColumnSpan(columns);
		cell.add(new Text(getResourceBundle().getLocalizedString("number_of_participants", "Number of participants") + ": " + (iRow - 1)));

		return table;
	}

	
	@Override
	protected Layer getNavigation(IWContext iwc) throws RemoteException {
		boolean showAllCourses = iwc.getApplicationSettings().getBoolean(
				CourseConstants.PROPERTY_SHOW_ALL_COURSES, false);
		boolean showCourseType = false;

		Layer layer = new Layer(Layer.DIV);
		layer.setStyleClass("formSection");

		List<String> scripts = new ArrayList<String>();
		scripts.add("/dwr/interface/CourseDWRUtil.js");
		scripts.add(CoreConstants.DWR_ENGINE_SCRIPT);
		scripts.add(CoreConstants.DWR_UTIL_SCRIPT);
		PresentationUtil.addJavaScriptSourcesLinesToHeader(iwc, scripts);

		if (!isSchoolUser()) {
			DropdownMenu providers = null;
			if (iwc.getAccessController().hasRole(
					CourseConstants.SUPER_ADMINISTRATOR_ROLE_KEY, iwc)) {
				providers = getAllProvidersDropdown(iwc);
			} else if (iwc.getAccessController().hasRole(
					CourseConstants.ADMINISTRATOR_ROLE_KEY, iwc)) {
				providers = getProvidersDropdown(iwc);
			}

			if (providers != null) {
				providers.addMenuElement("-99", getResourceBundle()
						.getLocalizedString("all_providers", "All providers"));
				providers.setToSubmit();

				if (getSession().getIsAllProvidersSelected()) {
					providers.setSelectedElement(-99);
				}

				Layer formItem = new Layer(Layer.DIV);
				formItem.setStyleClass("formItem");
				Label label = new Label(getResourceBundle().getLocalizedString(
						"provider", "Provider"), providers);
				formItem.add(label);
				formItem.add(providers);
				layer.add(formItem);
			}
		}


		DropdownMenu schoolType = new DropdownMenu(PARAMETER_SCHOOL_TYPE_PK);
		schoolType.setId(PARAMETER_SCHOOL_TYPE_PK);
		schoolType.setOnChange("changeValues();");
		schoolType
				.addMenuElementFirst("", getResourceBundle()
						.getLocalizedString("select_school_type",
								"Select school type"));
		schoolType.keepStatusOnAction(true);

		boolean showTypes = false;
		if (getSession().getProvider() != null) {
			Collection schoolTypes = getBusiness().getSchoolTypes(
					getSession().getProvider());
			if (schoolTypes.size() == 1) {
				type = (SchoolType) schoolTypes.iterator().next();
				schoolType.setSelectedElement(type.getPrimaryKey().toString());
			} else {
				showTypes = true;
			}
			schoolType.addMenuElements(schoolTypes);
		}

		DropdownMenu courseType = new DropdownMenu(PARAMETER_COURSE_TYPE_PK);
		courseType.setId(PARAMETER_COURSE_TYPE_PK);
		courseType.setOnChange("changeCourseValues();");
		courseType
				.addMenuElementFirst("", getResourceBundle()
						.getLocalizedString("select_course_type",
								"Select course type"));
		courseType.keepStatusOnAction(true);

		Integer typePK = null;
		if (iwc.isParameterSet(PARAMETER_SCHOOL_TYPE_PK)) {
			typePK = new Integer(iwc.getParameter(PARAMETER_SCHOOL_TYPE_PK));
			Collection courseTypes = getBusiness().getCourseTypes(typePK);
			if (courseTypes.size() == 1) {
				defaultCourseType = (CourseType) courseTypes.iterator().next();
				courseType.setSelectedElement(defaultCourseType.getPrimaryKey()
						.toString());
			} else {
				showCourseType = true;
			}
			courseType.addMenuElements(courseTypes);
		} else if (type != null) {
			typePK = new Integer(type.getPrimaryKey().toString());
			Collection courseTypes = getBusiness().getCourseTypes(typePK);
			if (courseTypes.size() == 1) {
				showCourseType = false;
				defaultCourseType = (CourseType) courseTypes.iterator().next();
				courseType.setSelectedElement(defaultCourseType.getPrimaryKey()
						.toString());
			}
			courseType.addMenuElements(courseTypes);
		}

		int inceptionYear = Integer.parseInt(iwc.getApplicationSettings()
				.getProperty(CourseConstants.PROPERTY_INCEPTION_YEAR, "-1"));
		int nextYear = new IWTimestamp().getYear();
		nextYear++;
		int year = showAllCourses ? -1 : nextYear;
		Date fromDate = null;
		Date toDate = null;
		if (iwc.isParameterSet(PARAMETER_YEAR)) {
			year = Integer.parseInt(iwc.getParameter(PARAMETER_YEAR));
		}
		if (year > 0) {
			fromDate = new IWTimestamp(1, 1, year).getDate();
			toDate = new IWTimestamp(31, 12, year).getDate();
		}

		DropdownMenu yearMenu = new DropdownMenu(PARAMETER_YEAR);
		if (inceptionYear > 0) {
			yearMenu.keepStatusOnAction(true);
			yearMenu.setID(PARAMETER_YEAR);
			yearMenu.setOnChange("changeCourseValues();");
			yearMenu.setSelectedElement(year);

			for (int i = inceptionYear; i <= nextYear; i++) {
				yearMenu.addMenuElement(i, String.valueOf(i));
			}
		}

		DropdownMenu course = new DropdownMenu(PARAMETER_COURSE_PK);
		course.setId(PARAMETER_COURSE_PK);
		course.keepStatusOnAction(true);
		course.addMenuElementFirst("", getResourceBundle().getLocalizedString(
				"select_course", "Select course"));
		course.setToSubmit();

		if ((getSession().getProvider() != null && typePK != null)
				|| showAllCourses) {
			Collection courses = getBusiness().getCourses(
					-1,
					getSession().getProvider() != null ? getSession()
							.getProvider().getPrimaryKey() : null,
					typePK,
					defaultCourseType != null ? defaultCourseType
							.getPrimaryKey().toString() : iwc
							.isParameterSet(PARAMETER_COURSE_TYPE_PK) ? iwc
							.getParameter(PARAMETER_COURSE_TYPE_PK) : null,
					fromDate, toDate);

			Iterator iter = courses.iterator();
			while (iter.hasNext()) {
				Course element = (Course) iter.next();
				IWTimestamp date = new IWTimestamp(element.getStartDate());
				course.addMenuElement(element.getPrimaryKey().toString(), date
						.getDateString("dd.MM.yyyy HH:mm"));
			}
		}

		if (showTypes) {
			Layer formItem = new Layer(Layer.DIV);
			formItem.setStyleClass("formItem");
			Label label = new Label(getResourceBundle().getLocalizedString(
					"category", "Category"), schoolType);
			formItem.add(label);
			formItem.add(schoolType);
			layer.add(formItem);
		} else if (type != null) {
			layer.add(new HiddenInput(PARAMETER_SCHOOL_TYPE_PK, type
					.getPrimaryKey().toString()));
		}

		if (showCourseType) {
			Layer formItem = new Layer(Layer.DIV);
			formItem.setStyleClass("formItem");
			Label label = new Label(getResourceBundle().getLocalizedString(
					"type", "Type"), courseType);
			formItem.add(label);
			formItem.add(courseType);
			layer.add(formItem);

		} else if (defaultCourseType != null) {
			layer.add(new HiddenInput(PARAMETER_COURSE_TYPE_PK,
					defaultCourseType.getPrimaryKey().toString()));
		}

		if (!showAllCourses && inceptionYear > 0) {
			Layer formItem = new Layer(Layer.DIV);
			formItem.setStyleClass("formItem");
			Label label = new Label(getResourceBundle().getLocalizedString(
					"year", "Year"), yearMenu);
			formItem.add(label);
			formItem.add(yearMenu);
			layer.add(formItem);
		}

		if (!getSession().getIsAllProvidersSelected()) {
			Layer formItem = new Layer(Layer.DIV);
			formItem.setStyleClass("formItem");
			Label label = new Label(getResourceBundle().getLocalizedString(
					"course", "Course"), course);
			formItem.add(label);
			formItem.add(course);
			layer.add(formItem);
		}

		Layer clearLayer = new Layer(Layer.DIV);
		clearLayer.setStyleClass("Clear");
		layer.add(clearLayer);

		return layer;
	}

	@Override
	public boolean actionPerformed(IWContext iwc) {
		try {
			if (iwc.isParameterSet(PARAMETER_PROVIDER_PK)) {
				if ("-99".equals(iwc.getParameter(PARAMETER_PROVIDER_PK))) {
					getSession(iwc).setIsAllProvidersSelected(true);
					getSession(iwc).setProvider(null);
				} else {
					getSession(iwc)
							.setProviderPK(
									new Integer(
											iwc
													.getParameter(PARAMETER_PROVIDER_PK)));
					getSession(iwc).setIsAllProvidersSelected(false);
				}
			} else {
				getSession(iwc).setProviderPK(null);
				getSession(iwc).setIsAllProvidersSelected(false);
			}
		} catch (RemoteException re) {
			throw new IBORuntimeException(re);
		}
		return true;
	}

	@Override
	protected Link getXLSLink(IWContext iwc) {
		DownloadLink link = new DownloadLink(getBundle().getImage("xls.gif"));
		link.setStyleClass("xls");
		link.setTarget(Link.TARGET_NEW_WINDOW);
		link.maintainParameter(PARAMETER_COURSE_PK, iwc);
		link.setMediaWriterClass(ChurchCourseParticipantWriter.class);

		return link;
	}

	@Override
	protected Table2 getParticipants(IWContext iwc,
			boolean addViewParticipantLink, boolean addCheckboxes)
			throws RemoteException {
		Table2 table = new Table2();
		table.setStyleClass("adminTable");
		table.setStyleClass("ruler");
		table.setWidth("100%");
		table.setCellpadding(0);
		table.setCellspacing(0);

		TableRowGroup group = table.createHeaderRowGroup();
		TableRow row = group.createRow();
		TableCell2 cell = row.createHeaderCell();
		cell.setStyleClass("firstColumn");
		cell.setStyleClass("number");
		cell.add(Text.getNonBrakingSpace());

		int columns = 1;

		cell = row.createHeaderCell();
		cell.setStyleClass("name");
		cell.add(new Text(getResourceBundle()
				.getLocalizedString("name", "Name")));
		columns++;

		cell = row.createHeaderCell();
		cell.setStyleClass("personalID");
		cell.add(new Text(getResourceBundle().getLocalizedString("personal_id",
				"Personal ID")));
		columns++;

		cell = row.createHeaderCell();
		cell.setStyleClass("address");
		cell.add(new Text(getResourceBundle().getLocalizedString("address",
				"Address")));
		columns++;

		cell = row.createHeaderCell();
		cell.setStyleClass("postalCode");
		cell.add(new Text(getResourceBundle().getLocalizedString("postal_code",
				"Postal code")));
		columns++;

		cell = row.createHeaderCell();
		cell.setStyleClass("lastColumn");
		cell.setStyleClass("homePhone");
		cell.add(new Text(getResourceBundle().getLocalizedString("home_phone",
				"Phone")));

		group = table.createBodyRowGroup();
		int iRow = 1;

		Course course = null;
		CourseType type = null;
		Collection choices = new ArrayList();
		if (iwc.isParameterSet(PARAMETER_COURSE_PK)) {
			choices = getBusiness().getCourseChoices(
					iwc.getParameter(PARAMETER_COURSE_PK), false);
			course = getBusiness().getCourse(
					iwc.getParameter(PARAMETER_COURSE_PK));
			type = course.getCourseType();
			if (type.getAbbreviation() != null) {
				table.setStyleClass("abbr_" + type.getAbbreviation());
			}
		} else if (getSession().getIsAllProvidersSelected()) {
			choices = getBusiness().getAllChoicesByCourseAndDate(null, null, null);
		}

		Iterator iter = choices.iterator();
		while (iter.hasNext()) {
			row = group.createRow();

			CourseChoice choice = (CourseChoice) iter.next();
			User user = choice.getUser();
			Address address = getUserBusiness().getUsersMainAddress(user);
			PostalCode postalCode = null;
			if (address != null) {
				postalCode = address.getPostalCode();
			}
			Phone phone = getUserBusiness().getChildHomePhone(user);

			if (!getSession().getIsAllProvidersSelected()) {
				if (iRow == course.getMax()) {
					row.setStyleClass("lastAvailable");
				} else if (iRow == (course.getMax() + 1)) {
					row.setStyleClass("firstExceedingParticipant");
				}
	
				if (iRow > course.getMax()) {
					row.setStyleClass("exceedingParticipant");
				}
			}
			
			cell = row.createCell();
			cell.setStyleClass("firstColumn");
			cell.setStyleClass("number");
			cell.add(new Text(String.valueOf(iRow)));

			Name name = new Name(user.getFirstName(), user.getMiddleName(),
					user.getLastName());

			cell = row.createCell();
			cell.setStyleClass("name");
			if (getResponsePage() != null) {
				Link link = new Link(name.getName(iwc.getCurrentLocale()));
				link.addParameter(PARAMETER_CHOICE_PK, choice.getPrimaryKey()
						.toString());
				link.setPage(getResponsePage());

				cell.add(link);
			} else {
				cell.add(new Text(name.getName(iwc.getCurrentLocale())));
			}

			cell = row.createCell();
			cell.setStyleClass("personalID");
			cell.add(new Text(PersonalIDFormatter.format(user.getPersonalID(),
					iwc.getCurrentLocale())));

			cell = row.createCell();
			cell.setStyleClass("address");
			if (address != null) {
				cell.add(new Text(address.getStreetAddress()));
			} else {
				cell.add(new Text(CoreConstants.MINUS));
			}

			cell = row.createCell();
			cell.setStyleClass("postalCode");
			if (postalCode != null) {
				cell.add(new Text(postalCode.getPostalAddress()));
			} else {
				cell.add(new Text(CoreConstants.MINUS));
			}

			cell = row.createCell();
			cell.setStyleClass("lastColumn");
			cell.setStyleClass("homePhone");
			if (phone != null) {
				cell.add(new Text(phone.getNumber()));
			} else {
				cell.add(new Text(CoreConstants.MINUS));
			}

			if (iRow % 2 == 0) {
				row.setStyleClass("evenRow");
			} else {
				row.setStyleClass("oddRow");
			}
			iRow++;
		}

		group = table.createFooterRowGroup();
		row = group.createRow();

		cell = row.createCell();
		cell.setStyleClass("numberOfParticipants");
		cell.setColumnSpan(columns);
		cell.add(new Text(getResourceBundle().getLocalizedString(
				"number_of_participants", "Number of participants")
				+ ": " + (iRow - 1)));

		return table;
	}

	private CourseBusiness getCourseBusiness(IWContext iwc) {
		try {
			return (CourseBusiness) IBOLookup.getServiceInstance(iwc,
					CourseBusiness.class);
		} catch (IBOLookupException e) {
			throw new IBORuntimeException(e);
		}
	}
}