package is.idega.idegaweb.egov.church.presentation;

import is.idega.block.family.business.FamilyConstants;
import is.idega.idegaweb.egov.application.presentation.ApplicationForm;
import is.idega.idegaweb.egov.church.data.ChurchCourseApplicationInfo;
import is.idega.idegaweb.egov.church.data.ChurchCourseApplicationInfoHome;
import is.idega.idegaweb.egov.course.CourseConstants;
import is.idega.idegaweb.egov.course.business.CourseApplicationSession;
import is.idega.idegaweb.egov.course.business.CourseBusiness;
import is.idega.idegaweb.egov.course.business.CourseDWR;
import is.idega.idegaweb.egov.course.data.Course;
import is.idega.idegaweb.egov.course.data.CourseApplication;
import is.idega.idegaweb.egov.course.data.CourseChoice;
import is.idega.idegaweb.egov.course.presentation.CourseBlock;

import java.rmi.RemoteException;
import java.util.Collection;
import java.util.Iterator;

import javax.ejb.CreateException;
import javax.ejb.FinderException;

import com.idega.block.school.business.SchoolBusiness;
import com.idega.block.school.data.School;
import com.idega.business.IBOLookup;
import com.idega.business.IBOLookupException;
import com.idega.business.IBORuntimeException;
import com.idega.core.contact.data.Email;
import com.idega.core.contact.data.Phone;
import com.idega.core.location.data.Address;
import com.idega.data.IDOLookup;
import com.idega.idegaweb.IWResourceBundle;
import com.idega.presentation.IWContext;
import com.idega.presentation.Layer;
import com.idega.presentation.Span;
import com.idega.presentation.Table2;
import com.idega.presentation.TableCell2;
import com.idega.presentation.TableRow;
import com.idega.presentation.TableRowGroup;
import com.idega.presentation.text.Heading1;
import com.idega.presentation.text.Link;
import com.idega.presentation.text.Paragraph;
import com.idega.presentation.text.Text;
import com.idega.presentation.ui.DropdownMenu;
import com.idega.presentation.ui.Form;
import com.idega.presentation.ui.HiddenInput;
import com.idega.presentation.ui.IWDatePicker;
import com.idega.presentation.ui.Label;
import com.idega.presentation.ui.RadioButton;
import com.idega.presentation.ui.TextArea;
import com.idega.presentation.ui.TextInput;
import com.idega.user.business.NoEmailFoundException;
import com.idega.user.business.NoPhoneFoundException;
import com.idega.user.data.Gender;
import com.idega.user.data.GenderHome;
import com.idega.user.data.User;
import com.idega.util.EmailValidator;
import com.idega.util.IWTimestamp;
import com.idega.util.PresentationUtil;
import com.idega.util.text.SocialSecurityNumber;

public class ChurchCourseParticipantEdit extends ApplicationForm {

	private static final String BUNDLE_IDENTIFIER = "is.idega.idegaweb.egov.church";

	private static final int ACTION_PHASE_1 = 1;
	private static final int ACTION_SAVE = 0;

	public static final String PARAMETER_APPLICATION_PK = CourseBlock.PARAMETER_CHOICE_PK;

	protected static final String PARAMETER_ACTION = "prm_action";

	private static final String PARAMETER_COURSE_TABLE_ID = "prm_cou_course_table";
	private static final String PARAMETER_COURSE = "prm_cou_course";
	private static final String PARAMETER_CHILD_NAME = "prm_child_name";
	private static final String PARAMETER_CHILD_PERSONAL_ID = "prm_child_personal_id";
	private static final String PARAMETER_CHILD_HOME = "prm_child_home";
	private static final String PARAMETER_CHILD_PO = "prm_child_po";
	private static final String PARAMETER_CHILD_PLACE = "prm_child_place";
	private static final String PARAMETER_CHILD_MOBILE = "prm_child_mobile";
	private static final String PARAMETER_CHILD_EMAIL = "prm_child_email";
	private static final String PARAMETER_CHILD_CHRISTENING_DATE = "prm_child_christening_date";
	private static final String PARAMETER_MOTHERS_NAME = "prm_mothers_name";
	private static final String PARAMETER_FATHERS_NAME = "prm_fathers_name";
	private static final String PARAMETER_CHILD_GENDER = "prm_child_gender";
	private static final String PARAMETER_CHILD_RELIGION = "prm_child_religion";
	private static final String PARAMETER_CHILD_SCHOOL = "prm_child_school";
	private static final String PARAMETER_CONTACT1_NAME = "prm_contact1_name";
	private static final String PARAMETER_CONTACT1_PERSONAL_ID = "prm_contact1_personal_id";
	private static final String PARAMETER_CONTACT1_HOME = "prm_contact1_home";
	private static final String PARAMETER_CONTACT1_PO = "prm_contact1_po";
	private static final String PARAMETER_CONTACT1_PLACE = "prm_contact1_place";
	private static final String PARAMETER_CONTACT1_MOBILE = "prm_contact1_mobile";
	private static final String PARAMETER_CONTACT1_EMAIL = "prm_contact1_email";
	private static final String PARAMETER_CONTACT1_PHONE = "prm_contact1_phone";
	private static final String PARAMETER_CONTACT1_WORK_PHONE = "prm_contact1_work_phone";
	private static final String PARAMETER_CONTACT1_RELATION = "prm_contact1_relation";
	private static final String PARAMETER_CONTACT2_NAME = "prm_contact2_name";
	private static final String PARAMETER_CONTACT2_PERSONAL_ID = "prm_contact2_personal_id";
	private static final String PARAMETER_CONTACT2_HOME = "prm_contact2_home";
	private static final String PARAMETER_CONTACT2_PO = "prm_contact2_po";
	private static final String PARAMETER_CONTACT2_PLACE = "prm_contact2_place";
	private static final String PARAMETER_CONTACT2_MOBILE = "prm_contact2_mobile";
	private static final String PARAMETER_CONTACT2_EMAIL = "prm_contact2_email";
	private static final String PARAMETER_CONTACT2_PHONE = "prm_contact2_phone";
	private static final String PARAMETER_CONTACT2_WORK_PHONE = "prm_contact2_work_phone";
	private static final String PARAMETER_CONTACT2_RELATION = "prm_contact2_relation";
	private static final String PARAMETER_OTHER_INFO = "prm_other_info";

	private static final String PARAMETER_EDUCATION_GROUP = "prm_education_group";

	private IWResourceBundle iwrb = null;
	private CourseChoice choice = null;

	private int numberOfPhases = 2;
	private boolean iUseSessionUser = false;
	private String iSchoolTypePK = null;
	private String iDropdownSchoolTypePK = null;
	private String iCourseTypePK = null;
	private String showSubmitDateString = null;

	@Override
	protected String getCaseCode() {
		return CourseConstants.CASE_CODE_KEY;
	}

	@Override
	public String getBundleIdentifier() {
		return ChurchCourseParticipantEdit.BUNDLE_IDENTIFIER;
	}

	@Override
	protected void present(IWContext iwc) {
		this.iwrb = getResourceBundle(iwc);
		PresentationUtil.addStyleSheetToHeader(iwc, getBundle(iwc)
				.getVirtualPathWithFileNameString("style/church.css"));

		try {
			switch (parseAction(iwc)) {
			case ACTION_PHASE_1:
				showPhaseOne(iwc);
				break;

			case ACTION_SAVE:
				save(iwc);
				break;
			}
		} catch (RemoteException e) {
			e.printStackTrace();
		}
	}

	private void showPhaseOne(IWContext iwc) throws RemoteException {
		Form form = getForm(iwc, ACTION_PHASE_1);
		form.setId("course_step_1");
		form.add(new HiddenInput(PARAMETER_ACTION, String
				.valueOf(ACTION_PHASE_1)));
		form.maintainParameter(PARAMETER_APPLICATION_PK);

		addErrors(iwc, form);

		Heading1 heading = new Heading1(this.iwrb.getLocalizedString(
				"application.application_name", "Course application"));
		heading.setStyleClass("applicationHeading");
		form.add(heading);

		form.add(getPhasesHeader(iwrb.getLocalizedString("application.course",
				"Course"), 1, numberOfPhases, true));

		Layer info = new Layer(Layer.DIV);
		info.setStyleClass("info");
		form.add(info);

		Paragraph infoHelp = new Paragraph();
		infoHelp.setStyleClass("infoHelp");
		infoHelp
				.add(new Text(
						this.iwrb
								.getLocalizedString(
										"application.application_help",
										"Below you can select the type of courses you want to register to if there are any available.<br />The courses are held at the main office unless otherwise stated.<br /><br />Please select the courses you want to register to and click 'Next'.")));
		info.add(infoHelp);

		Integer schoolTypePK = getSchoolTypePK() != null ? new Integer(
				getSchoolTypePK().toString()) : null;
		Integer courseTypePK = getCourseTypePK() != null ? new Integer(
				getCourseTypePK().toString()) : null;
		Integer dropdownSchoolTypePK = getDropdownSchoolTypePK() != null ? new Integer(
				getDropdownSchoolTypePK().toString())
				: null;

		// Child info
		heading = new Heading1(this.iwrb.getLocalizedString(
				"application.child_info", "Child info"));
		heading.setStyleClass("subHeader");
		heading.setStyleClass("topSubHeader");
		form.add(heading);

		Layer section = new Layer(Layer.DIV);
		section.setStyleClass("formSection");
		form.add(section);

		if (schoolTypePK == null || courseTypePK == null
				|| dropdownSchoolTypePK == null) {
			Layer formItem = new Layer(Layer.DIV);
			formItem.setStyleClass("formItem");
			formItem.add(new Text("Set the parameters for the form"));

			add(form);

			return;
		}

		Layer helpLayer = new Layer(Layer.DIV);
		helpLayer.setStyleClass("helperText");
		helpLayer.add(new Text(this.iwrb.getLocalizedString(
				"application.child_info_help", "Child info help.")));
		section.add(helpLayer);

		User child = choice.getUser();
		Address childAddressDef = getUserBusiness(iwc).getUsersMainAddress(
				child);
		Phone childMobileDef = null;
		try {
			childMobileDef = getUserBusiness(iwc).getUsersMobilePhone(child);
		} catch (NoPhoneFoundException e2) {
		}
		Email childEmailDef = null;
		try {
			childEmailDef = getUserBusiness(iwc).getUsersMainEmail(child);
		} catch (NoEmailFoundException e1) {
		}
		User relative1 = choice.getApplication().getOwner();
		Address relAddress1 = getUserBusiness(iwc).getUsersMainAddress(
				relative1);
		Phone relPhone1 = null;
		try {
			relPhone1 = getUserBusiness(iwc).getUsersHomePhone(relative1);
		} catch (NoPhoneFoundException e2) {
		}
		Phone relMobile1 = null;
		try {
			relMobile1 = getUserBusiness(iwc).getUsersMobilePhone(relative1);
		} catch (NoPhoneFoundException e2) {
		}
		Phone relWorkPhone1 = null;
		try {
			relWorkPhone1 = getUserBusiness(iwc).getUsersWorkPhone(relative1);
		} catch (NoPhoneFoundException e2) {
		}
		Email relEmail1 = null;
		try {
			relEmail1 = getUserBusiness(iwc).getUsersMainEmail(relative1);
		} catch (NoEmailFoundException e1) {
		}

		ChurchCourseApplicationInfo extraInfo = null;
		User relative2 = null;
		try {
			extraInfo = getChurchCourseApplicationInfoHome().findByApplication(
					choice.getApplication());
			relative2 = extraInfo.getExtraContact();
		} catch (FinderException e) {
		}

		TextInput childName = new TextInput(PARAMETER_CHILD_NAME);
		childName.keepStatusOnAction(true);
		childName.setValue(child.getName());

		TextInput childPersonalID = new TextInput(PARAMETER_CHILD_PERSONAL_ID);
		childPersonalID.setMaxlength(10);
		childPersonalID.keepStatusOnAction(true);
		childPersonalID.setValue(child.getPersonalID());

		TextInput childHome = new TextInput(PARAMETER_CHILD_HOME);
		childHome.keepStatusOnAction(true);
		childHome.setValue(childAddressDef.getStreetAddress());

		TextInput childPO = new TextInput(PARAMETER_CHILD_PO);
		childPO.keepStatusOnAction(true);
		childPO.setValue(childAddressDef.getPostalCode().getPostalCode());

		TextInput childPlace = new TextInput(PARAMETER_CHILD_PLACE);
		childPlace.keepStatusOnAction(true);
		childPlace.setValue(childAddressDef.getPostalCode().getName());

		TextInput childMobile = new TextInput(PARAMETER_CHILD_MOBILE);
		childMobile.keepStatusOnAction(true);
		if (childMobileDef != null) {
			childMobile.setValue(childMobileDef.getNumber());
		}

		TextInput childEmail = new TextInput(PARAMETER_CHILD_EMAIL);
		childEmail.keepStatusOnAction(true);
		if (childEmailDef != null) {
			childEmail.setValue(childEmailDef.getEmailAddress());
		}

		DropdownMenu childGender = new DropdownMenu(PARAMETER_CHILD_GENDER);
		childGender.addMenuElement(-1, this.iwrb.getLocalizedString(
				"child.select_gender", "Please select gender"));
		childGender.addMenuElement(0, this.iwrb.getLocalizedString(
				"child.male", "Male"));
		childGender.addMenuElement(1, this.iwrb.getLocalizedString(
				"child.female", "Female"));
		childGender.keepStatusOnAction(true);
		if (child.getGender().isMaleGender()) {
			childGender.setSelectedElement(0);
		} else {
			childGender.setSelectedElement(1);
		}

		DropdownMenu childReligion = new DropdownMenu(PARAMETER_CHILD_RELIGION);
		childReligion.addMenuElement(-1, this.iwrb.getLocalizedString(
				"religion.select_religion", "Please select religion"));
		childReligion.addMenuElement(0, this.iwrb.getLocalizedString(
				"religion.national_church", "National church"));
		childReligion.addMenuElement(1, this.iwrb.getLocalizedString(
				"religion.other", "Other"));
		childReligion.keepStatusOnAction(true);
		childReligion.setSelectedElement(extraInfo.getReligion());

		IWTimestamp stamp = new IWTimestamp();
		stamp.addYears(-13);

		IWDatePicker childChristeningDate = new IWDatePicker(
				PARAMETER_CHILD_CHRISTENING_DATE);
		childChristeningDate.setShowYearChange(true);
		childChristeningDate.keepStatusOnAction(true);
		// IWTimestamp cDate = new IWTimestamp(extraInfo.getChristeningDate());
		childChristeningDate.setDate(stamp.getDate());

		TextInput mothersName = new TextInput(PARAMETER_MOTHERS_NAME);
		mothersName.keepStatusOnAction(true);
		mothersName.setValue(extraInfo.getMothersName());

		TextInput fathersName = new TextInput(PARAMETER_FATHERS_NAME);
		fathersName.keepStatusOnAction(true);
		fathersName.setValue(extraInfo.getFathersName());

		DropdownMenu childSchool = new DropdownMenu(PARAMETER_CHILD_SCHOOL);
		childSchool.addMenuElementFirst("-1", this.iwrb.getLocalizedString(
				"child.select_school", "Please select school"));
		try {
			Collection<School> providers = getSchoolBusiness(iwc).getSchoolHome().findAllBySchoolType(dropdownSchoolTypePK.intValue());
			for (School school : providers) {
				childSchool.addMenuElement(school.getPrimaryKey().toString(), school.getName());
			}
		} catch (FinderException e) {
			e.printStackTrace();
		}
		childSchool.keepStatusOnAction(true);
		if (extraInfo.getSchool() != null) {
			childSchool.setSelectedElement(extraInfo.getSchool()
					.getPrimaryKey().toString());
		}

		Layer formItem = new Layer(Layer.DIV);
		formItem.setStyleClass("formItem");
		formItem.setStyleClass("required");
		Label label = new Label(new Span(new Text(this.iwrb.getLocalizedString(
				"child.name", "Child's name"))), childName);
		formItem.add(label);
		formItem.add(childName);
		section.add(formItem);

		formItem = new Layer(Layer.DIV);
		formItem.setStyleClass("formItem");
		formItem.setStyleClass("required");
		label = new Label(new Span(new Text(this.iwrb.getLocalizedString(
				"child.ssn", "Child's ssn"))), childPersonalID);
		formItem.add(label);
		formItem.add(childPersonalID);
		section.add(formItem);

		formItem = new Layer(Layer.DIV);
		formItem.setStyleClass("formItem");
		formItem.setStyleClass("required");
		label = new Label(new Span(new Text(this.iwrb.getLocalizedString(
				"child.home", "Child's address"))), childHome);
		formItem.add(label);
		formItem.add(childHome);
		section.add(formItem);

		formItem = new Layer(Layer.DIV);
		formItem.setStyleClass("formItem");
		formItem.setStyleClass("required");
		label = new Label(new Span(new Text(this.iwrb.getLocalizedString(
				"child.po", "Child's po"))), childPO);
		formItem.add(label);
		formItem.add(childPO);
		section.add(formItem);

		formItem = new Layer(Layer.DIV);
		formItem.setStyleClass("formItem");
		formItem.setStyleClass("required");
		label = new Label(new Span(new Text(this.iwrb.getLocalizedString(
				"child.po_place", "Child's PO place"))), childPlace);
		formItem.add(label);
		formItem.add(childPlace);
		section.add(formItem);

		formItem = new Layer(Layer.DIV);
		formItem.setStyleClass("formItem");
		label = new Label(new Span(new Text(this.iwrb.getLocalizedString(
				"child.mobile", "Child's mobile"))), childMobile);
		formItem.add(label);
		formItem.add(childMobile);
		section.add(formItem);

		formItem = new Layer(Layer.DIV);
		formItem.setStyleClass("formItem");
		label = new Label(new Span(new Text(this.iwrb.getLocalizedString(
				"child.email", "Child's email"))), childEmail);
		formItem.add(label);
		formItem.add(childEmail);
		section.add(formItem);

		formItem = new Layer(Layer.DIV);
		formItem.setStyleClass("formItem");
		formItem.setStyleClass("required");
		label = new Label(new Span(new Text(this.iwrb.getLocalizedString(
				"child.gender", "Child's gender"))), childGender);
		formItem.add(label);
		formItem.add(childGender);
		section.add(formItem);

		formItem = new Layer(Layer.DIV);
		formItem.setStyleClass("formItem");
		formItem.setStyleClass("required");
		label = new Label(new Span(new Text(this.iwrb.getLocalizedString(
				"child.religion", "Child's religion"))), childReligion);
		formItem.add(label);
		formItem.add(childReligion);
		section.add(formItem);

		formItem = new Layer(Layer.DIV);
		formItem.setStyleClass("formItem");
		formItem.setStyleClass("required");
		label = new Label(new Span(new Text(this.iwrb.getLocalizedString(
				"child.christening_date", "Child's Christening date"))),
				childChristeningDate);
		formItem.add(label);
		formItem.add(childChristeningDate);
		section.add(formItem);

		formItem = new Layer(Layer.DIV);
		formItem.setStyleClass("formItem");
		formItem.setStyleClass("required");
		label = new Label(new Span(new Text(this.iwrb.getLocalizedString(
				"mothers_name", "Mother's name"))), mothersName);
		formItem.add(label);
		formItem.add(mothersName);
		section.add(formItem);

		formItem = new Layer(Layer.DIV);
		formItem.setStyleClass("formItem");
		formItem.setStyleClass("required");
		label = new Label(new Span(new Text(this.iwrb.getLocalizedString(
				"fathers_name", "Father's name"))), fathersName);
		formItem.add(label);
		formItem.add(fathersName);
		section.add(formItem);

		formItem = new Layer(Layer.DIV);
		formItem.setStyleClass("formItem");
		label = new Label(new Span(new Text(this.iwrb.getLocalizedString(
				"child.school", "Child's school"))), childSchool);
		formItem.add(label);
		formItem.add(childSchool);
		section.add(formItem);

		// Courses
		Collection courses = null;
		if (courseTypePK != null) {
			courses = getCourseBusiness(iwc).getCoursesDWR(-1,
					schoolTypePK.intValue(), courseTypePK.intValue(), -1,
					iwc.getCurrentLocale().getCountry(), this.iUseSessionUser);
		}

		Table2 table = new Table2();
		table.setStyleClass("courses");
		table.setCellpadding(0);
		table.setCellspacing(0);
		table.setBorder(0);
		TableRowGroup group = table.createHeaderRowGroup();
		TableRow row = group.createRow();
		row.setStyleClass("header");
		TableCell2 cell = row.createHeaderCell();
		cell.setStyleClass("column0");

		cell = row.createHeaderCell();
		cell.setStyleClass("column1");
		cell.add(new Text(iwrb.getLocalizedString("provider", "Provider")));
		cell = row.createHeaderCell();
		cell.setStyleClass("column2");
		cell.add(new Text(iwrb.getLocalizedString("date", "Date")));
		cell = row.createHeaderCell();
		cell.setStyleClass("column3");
		cell.add(new Text(iwrb.getLocalizedString("time", "Time")));

		group = table.createBodyRowGroup();
		group.setId(PARAMETER_COURSE_TABLE_ID);
		if (courses != null) {
			Iterator iter = courses.iterator();
			int counter = 0;
			while (iter.hasNext()) {
				CourseDWR course = (CourseDWR) iter.next();
				row = group.createRow();
				if (counter++ % 2 == 0) {
					row.setStyleClass("even");
				} else {
					row.setStyleClass("odd");
				}
				cell = row.createCell();
				cell.setStyleClass("column0");

				RadioButton radio = new RadioButton(PARAMETER_COURSE, course
						.getPk());
				radio.setStyleClass("checkbox");
				radio.keepStatusOnAction(true);
				cell.add(radio);
				if (course.getPk().equals(
						this.choice.getCourse().getPrimaryKey().toString())) {
					radio.setSelected(true);
				}

				cell = row.createCell();
				cell.setStyleClass("column1");
				cell.add(new Text(course.getProvider()));

				cell = row.createCell();
				cell.setStyleClass("column2");
				cell.add(new Text(course.getFirstDateOfCourse().getDateString(
						"dd.MM.yyyy")));

				cell = row.createCell();
				cell.setStyleClass("column3");
				cell.add(new Text(course.getFirstDateOfCourse().getDateString(
						"HH:mm")));
			}
		}

		heading = new Heading1(this.iwrb.getLocalizedString(
				"available_courses", "Available courses"));
		heading.setStyleClass("subHeader");
		form.add(heading);

		section = new Layer(Layer.DIV);
		section.setStyleClass("formSection");
		section.setStyleClass("formTableSection");
		form.add(section);

		helpLayer = new Layer(Layer.DIV);
		helpLayer.setStyleClass("helperText");
		helpLayer.add(new Text(this.iwrb.getLocalizedString(
				"application.select_course_help",
				"Please select the course you want to register to.")));
		section.add(helpLayer);

		formItem = new Layer(Layer.DIV);
		formItem.setStyleClass("formItem");
		formItem.add(table);
		section.add(formItem);

		// Contact 1
		TextInput contact1Name = new TextInput(PARAMETER_CONTACT1_NAME);
		contact1Name.keepStatusOnAction(true);
		contact1Name.setValue(relative1.getName());

		TextInput contact1PersonalID = new TextInput(
				PARAMETER_CONTACT1_PERSONAL_ID);
		contact1PersonalID.setMaxlength(10);
		contact1PersonalID.keepStatusOnAction(true);
		contact1PersonalID.setValue(relative1.getPersonalID());

		TextInput contact1Home = new TextInput(PARAMETER_CONTACT1_HOME);
		contact1Home.keepStatusOnAction(true);
		contact1Home.setValue(relAddress1.getStreetAddress());

		TextInput contact1PO = new TextInput(PARAMETER_CONTACT1_PO);
		contact1PO.keepStatusOnAction(true);
		contact1PO.setValue(relAddress1.getPostalCode().getPostalCode());

		TextInput contact1Place = new TextInput(PARAMETER_CONTACT1_PLACE);
		contact1Place.keepStatusOnAction(true);
		contact1Place.setValue(relAddress1.getPostalCode().getName());

		TextInput contact1Email = new TextInput(PARAMETER_CONTACT1_EMAIL);
		contact1Email.keepStatusOnAction(true);
		contact1Email.setValue(relEmail1.getEmailAddress());

		TextInput contact1Phone = new TextInput(PARAMETER_CONTACT1_PHONE);
		contact1Phone.keepStatusOnAction(true);
		contact1Phone.setValue(relPhone1.getNumber());

		TextInput contact1Mobile = new TextInput(PARAMETER_CONTACT1_MOBILE);
		contact1Mobile.keepStatusOnAction(true);
		if (relMobile1 != null) {
			contact1Mobile.setValue(relMobile1.getNumber());
		}

		TextInput contact1WorkPhone = new TextInput(
				PARAMETER_CONTACT1_WORK_PHONE);
		contact1WorkPhone.keepStatusOnAction(true);
		if (relWorkPhone1 != null) {
			contact1WorkPhone.setValue(relWorkPhone1.getNumber());
		}

		DropdownMenu contact1Relation = new DropdownMenu(
				PARAMETER_CONTACT1_RELATION);
		contact1Relation.addMenuElement("-1", this.iwrb.getLocalizedString(
				"select_relation", "Select relation"));
		contact1Relation.addMenuElement(FamilyConstants.RELATION_MOTHER,
				this.iwrb.getLocalizedString("relation.mother", "Mother"));
		contact1Relation.addMenuElement(FamilyConstants.RELATION_FATHER,
				this.iwrb.getLocalizedString("relation.father", "Father"));
		contact1Relation.addMenuElement(FamilyConstants.RELATION_STEPMOTHER,
				this.iwrb.getLocalizedString("relation.stepmother",
						"Stepmother"));
		contact1Relation.addMenuElement(FamilyConstants.RELATION_STEPFATHER,
				this.iwrb.getLocalizedString("relation.stepfather",
						"Stepfather"));
		contact1Relation.addMenuElement(FamilyConstants.RELATION_GRANDMOTHER,
				this.iwrb.getLocalizedString("relation.grandmother",
						"Grandmother"));
		contact1Relation.addMenuElement(FamilyConstants.RELATION_GRANDFATHER,
				this.iwrb.getLocalizedString("relation.grandfather",
						"Grandfather"));
		contact1Relation.addMenuElement(FamilyConstants.RELATION_SIBLING,
				this.iwrb.getLocalizedString("relation.sibling", "Sibling"));
		contact1Relation.addMenuElement(FamilyConstants.RELATION_OTHER,
				this.iwrb.getLocalizedString("relation.other", "Other"));
		contact1Relation.keepStatusOnAction(true);

		contact1Relation.setSelectedElement(extraInfo.getContactRelation());

		heading = new Heading1(this.iwrb.getLocalizedString("contact1",
				"Contact 1"));
		heading.setStyleClass("subHeader");
		form.add(heading);

		section = new Layer(Layer.DIV);
		section.setStyleClass("formSection");
		form.add(section);

		helpLayer = new Layer(Layer.DIV);
		helpLayer.setStyleClass("helperText");
		helpLayer.add(new Text(this.iwrb.getLocalizedString(
				"application.contact1_help", "Help for contact 1")));
		section.add(helpLayer);

		formItem = new Layer(Layer.DIV);
		formItem.setStyleClass("formItem");
		formItem.setStyleClass("required");
		label = new Label(new Span(new Text(this.iwrb.getLocalizedString(
				"contact1.name", "Contact1 name"))), contact1Name);
		formItem.add(label);
		formItem.add(contact1Name);
		section.add(formItem);

		formItem = new Layer(Layer.DIV);
		formItem.setStyleClass("formItem");
		formItem.setStyleClass("required");
		label = new Label(new Span(new Text(this.iwrb.getLocalizedString(
				"contact1.ssn", "Contact1 ssn"))), contact1PersonalID);
		formItem.add(label);
		formItem.add(contact1PersonalID);
		section.add(formItem);

		formItem = new Layer(Layer.DIV);
		formItem.setStyleClass("formItem");
		formItem.setStyleClass("required");
		label = new Label(new Span(new Text(this.iwrb.getLocalizedString(
				"contact1.address", "Contact1 address"))), contact1Home);
		formItem.add(label);
		formItem.add(contact1Home);
		section.add(formItem);

		formItem = new Layer(Layer.DIV);
		formItem.setStyleClass("formItem");
		formItem.setStyleClass("required");
		label = new Label(new Span(new Text(this.iwrb.getLocalizedString(
				"contact1.po", "Contact1 po"))), contact1PO);
		formItem.add(label);
		formItem.add(contact1PO);
		section.add(formItem);

		formItem = new Layer(Layer.DIV);
		formItem.setStyleClass("formItem");
		formItem.setStyleClass("required");
		label = new Label(new Span(new Text(this.iwrb.getLocalizedString(
				"contact1.po_place", "Contact1 PO place"))), contact1Place);
		formItem.add(label);
		formItem.add(contact1Place);
		section.add(formItem);

		formItem = new Layer(Layer.DIV);
		formItem.setStyleClass("formItem");
		formItem.setStyleClass("required");
		label = new Label(new Span(new Text(this.iwrb.getLocalizedString(
				"contact1.email", "Contact1 email"))), contact1Email);
		formItem.add(label);
		formItem.add(contact1Email);
		section.add(formItem);

		formItem = new Layer(Layer.DIV);
		formItem.setStyleClass("formItem");
		formItem.setStyleClass("required");
		label = new Label(new Span(new Text(this.iwrb.getLocalizedString(
				"contact1.phone", "Contact1 phone"))), contact1Phone);
		formItem.add(label);
		formItem.add(contact1Phone);
		section.add(formItem);

		formItem = new Layer(Layer.DIV);
		formItem.setStyleClass("formItem");
		label = new Label(new Span(new Text(this.iwrb.getLocalizedString(
				"contact1.mobile", "Contact1 mobile"))), contact1Mobile);
		formItem.add(label);
		formItem.add(contact1Mobile);
		section.add(formItem);

		formItem = new Layer(Layer.DIV);
		formItem.setStyleClass("formItem");
		label = new Label(new Span(new Text(this.iwrb.getLocalizedString(
				"contact1.work_phone", "Contact1 work phone"))),
				contact1WorkPhone);
		formItem.add(label);
		formItem.add(contact1WorkPhone);
		section.add(formItem);

		formItem = new Layer(Layer.DIV);
		formItem.setStyleClass("formItem");
		formItem.setStyleClass("required");
		label = new Label(new Span(new Text(this.iwrb.getLocalizedString(
				"contact1.relation", "Contact1 relation"))), contact1Relation);
		formItem.add(label);
		formItem.add(contact1Relation);
		section.add(formItem);

		// Contact 2
		TextInput contact2Name = new TextInput(PARAMETER_CONTACT2_NAME);
		contact2Name.keepStatusOnAction(true);

		TextInput contact2PersonalID = new TextInput(
				PARAMETER_CONTACT2_PERSONAL_ID);
		contact2PersonalID.setMaxlength(10);
		contact2PersonalID.keepStatusOnAction(true);

		TextInput contact2Home = new TextInput(PARAMETER_CONTACT2_HOME);
		contact2Home.keepStatusOnAction(true);

		TextInput contact2PO = new TextInput(PARAMETER_CONTACT2_PO);
		contact2PO.keepStatusOnAction(true);

		TextInput contact2Place = new TextInput(PARAMETER_CONTACT2_PLACE);
		contact2Place.keepStatusOnAction(true);

		TextInput contact2Email = new TextInput(PARAMETER_CONTACT2_EMAIL);
		contact2Email.keepStatusOnAction(true);

		TextInput contact2Phone = new TextInput(PARAMETER_CONTACT2_PHONE);
		contact2Phone.keepStatusOnAction(true);

		TextInput contact2Mobile = new TextInput(PARAMETER_CONTACT2_MOBILE);
		contact2Mobile.keepStatusOnAction(true);

		TextInput contact2WorkPhone = new TextInput(
				PARAMETER_CONTACT2_WORK_PHONE);
		contact2WorkPhone.keepStatusOnAction(true);

		DropdownMenu contact2Relation = new DropdownMenu(
				PARAMETER_CONTACT2_RELATION);
		contact2Relation.addMenuElement("-1", this.iwrb.getLocalizedString(
				"select_relation", "Select relation"));
		contact2Relation.addMenuElement(FamilyConstants.RELATION_MOTHER,
				this.iwrb.getLocalizedString("relation.mother", "Mother"));
		contact2Relation.addMenuElement(FamilyConstants.RELATION_FATHER,
				this.iwrb.getLocalizedString("relation.father", "Father"));
		contact2Relation.addMenuElement(FamilyConstants.RELATION_STEPMOTHER,
				this.iwrb.getLocalizedString("relation.stepmother",
						"Stepmother"));
		contact2Relation.addMenuElement(FamilyConstants.RELATION_STEPFATHER,
				this.iwrb.getLocalizedString("relation.stepfather",
						"Stepfather"));
		contact2Relation.addMenuElement(FamilyConstants.RELATION_GRANDMOTHER,
				this.iwrb.getLocalizedString("relation.grandmother",
						"Grandmother"));
		contact2Relation.addMenuElement(FamilyConstants.RELATION_GRANDFATHER,
				this.iwrb.getLocalizedString("relation.grandfather",
						"Grandfather"));
		contact2Relation.addMenuElement(FamilyConstants.RELATION_SIBLING,
				this.iwrb.getLocalizedString("relation.sibling", "Sibling"));
		contact2Relation.addMenuElement(FamilyConstants.RELATION_OTHER,
				this.iwrb.getLocalizedString("relation.other", "Other"));
		contact2Relation.keepStatusOnAction(true);

		DropdownMenu educationGroup = new DropdownMenu(PARAMETER_EDUCATION_GROUP);
		educationGroup.addMenuElement("-1", this.iwrb.getLocalizedString(
				"select_education_group", "Select education group"));
		for (int i = 1; i < 4; i++) {
			educationGroup.addMenuElement(i, this.iwrb.getLocalizedString(
					"education_group_" + i, "Education group " + i));			
		}
		
		if (extraInfo.getEducationGroup() > 0) {
			educationGroup.setSelectedElement(extraInfo.getEducationGroup());
		}
		
		if (relative2 != null) {
			Address relAddress2 = getUserBusiness(iwc).getUsersMainAddress(
					relative2);
			Phone relPhone2 = null;
			try {
				relPhone2 = getUserBusiness(iwc).getUsersHomePhone(relative2);
			} catch (NoPhoneFoundException e2) {
			}
			Phone relMobile2 = null;
			try {
				relMobile2 = getUserBusiness(iwc)
						.getUsersMobilePhone(relative2);
			} catch (NoPhoneFoundException e2) {
			}
			Phone relWorkPhone2 = null;
			try {
				relWorkPhone2 = getUserBusiness(iwc).getUsersWorkPhone(
						relative2);
			} catch (NoPhoneFoundException e2) {
			}
			Email relEmail2 = null;
			try {
				relEmail2 = getUserBusiness(iwc).getUsersMainEmail(relative2);
			} catch (NoEmailFoundException e1) {
			}

			contact2Name.setValue(relative2.getName());
			contact2PersonalID.setValue(relative2.getPersonalID());
			contact2Home.setValue(relAddress2.getStreetAddress());
			contact2PO.setValue(relAddress2.getPostalCode().getPostalCode());
			contact2Place.setValue(relAddress2.getPostalCode().getName());
			contact2Email.setValue(relEmail2.getEmailAddress());
			contact2Phone.setValue(relPhone2.getNumber());
			if (relMobile2 != null) {
				contact2Mobile.setValue(relMobile2.getNumber());
			}
			if (relWorkPhone2 != null) {
				contact2WorkPhone.setValue(relWorkPhone2.getNumber());
			}

			contact2Relation.setSelectedElement(extraInfo
					.getExtraContactRelation());
		}

		heading = new Heading1(this.iwrb.getLocalizedString("contact2",
				"Contact 2"));
		heading.setStyleClass("subHeader");
		form.add(heading);

		section = new Layer(Layer.DIV);
		section.setStyleClass("formSection");
		form.add(section);

		helpLayer = new Layer(Layer.DIV);
		helpLayer.setStyleClass("helperText");
		helpLayer.add(new Text(this.iwrb.getLocalizedString(
				"application.contact2_help", "Help for contact 2")));
		section.add(helpLayer);

		formItem = new Layer(Layer.DIV);
		formItem.setStyleClass("formItem");
		formItem.setStyleClass("required");
		label = new Label(new Span(new Text(this.iwrb.getLocalizedString(
				"contact2.name", "Contact2 name"))), contact2Name);
		formItem.add(label);
		formItem.add(contact2Name);
		section.add(formItem);

		formItem = new Layer(Layer.DIV);
		formItem.setStyleClass("formItem");
		formItem.setStyleClass("required");
		label = new Label(new Span(new Text(this.iwrb.getLocalizedString(
				"contact2.ssn", "Contact2 ssn"))), contact2PersonalID);
		formItem.add(label);
		formItem.add(contact2PersonalID);
		section.add(formItem);

		formItem = new Layer(Layer.DIV);
		formItem.setStyleClass("formItem");
		formItem.setStyleClass("required");
		label = new Label(new Span(new Text(this.iwrb.getLocalizedString(
				"contact2.address", "Contact2 address"))), contact2Home);
		formItem.add(label);
		formItem.add(contact2Home);
		section.add(formItem);

		formItem = new Layer(Layer.DIV);
		formItem.setStyleClass("formItem");
		formItem.setStyleClass("required");
		label = new Label(new Span(new Text(this.iwrb.getLocalizedString(
				"contact2.po", "Contact2 po"))), contact2PO);
		formItem.add(label);
		formItem.add(contact2PO);
		section.add(formItem);

		formItem = new Layer(Layer.DIV);
		formItem.setStyleClass("formItem");
		formItem.setStyleClass("required");
		label = new Label(new Span(new Text(this.iwrb.getLocalizedString(
				"contact2.po_place", "Contact2 PO place"))), contact2Place);
		formItem.add(label);
		formItem.add(contact2Place);
		section.add(formItem);

		formItem = new Layer(Layer.DIV);
		formItem.setStyleClass("formItem");
		formItem.setStyleClass("required");
		label = new Label(new Span(new Text(this.iwrb.getLocalizedString(
				"contact2.email", "Contact2 email"))), contact2Email);
		formItem.add(label);
		formItem.add(contact2Email);
		section.add(formItem);

		formItem = new Layer(Layer.DIV);
		formItem.setStyleClass("formItem");
		formItem.setStyleClass("required");
		label = new Label(new Span(new Text(this.iwrb.getLocalizedString(
				"contact2.phone", "Contact2 phone"))), contact2Phone);
		formItem.add(label);
		formItem.add(contact2Phone);
		section.add(formItem);

		formItem = new Layer(Layer.DIV);
		formItem.setStyleClass("formItem");
		label = new Label(new Span(new Text(this.iwrb.getLocalizedString(
				"contact2.mobile", "Contact2 mobile"))), contact2Mobile);
		formItem.add(label);
		formItem.add(contact2Mobile);
		section.add(formItem);

		formItem = new Layer(Layer.DIV);
		formItem.setStyleClass("formItem");
		label = new Label(new Span(new Text(this.iwrb.getLocalizedString(
				"contact2.work_phone", "Contact2 work phone"))),
				contact2WorkPhone);
		formItem.add(label);
		formItem.add(contact2WorkPhone);
		section.add(formItem);

		formItem = new Layer(Layer.DIV);
		formItem.setStyleClass("formItem");
		formItem.setStyleClass("required");
		label = new Label(new Span(new Text(this.iwrb.getLocalizedString(
				"contact2.relation", "Contact2 relation"))), contact2Relation);
		formItem.add(label);
		formItem.add(contact2Relation);
		section.add(formItem);

		// Other info
		heading = new Heading1(this.iwrb.getLocalizedString("otherInfo",
				"Other info"));
		heading.setStyleClass("subHeader");
		form.add(heading);

		section = new Layer(Layer.DIV);
		section.setStyleClass("formSection");
		form.add(section);

		helpLayer = new Layer(Layer.DIV);
		helpLayer.setStyleClass("helperText");
		helpLayer.add(new Text(this.iwrb.getLocalizedString(
				"application.other_info_help", "Help for other info")));
		section.add(helpLayer);

		TextArea otherInfo = new TextArea(PARAMETER_OTHER_INFO);
		otherInfo.keepStatusOnAction(true);
		if (extraInfo.getInfo() != null) {
			otherInfo.setValue(extraInfo.getInfo());
		}

		formItem = new Layer(Layer.DIV);
		formItem.setStyleClass("formItem");
		label = new Label(new Span(new Text(this.iwrb.getLocalizedString(
				"other_info", "Other info"))), otherInfo);
		formItem.add(label);
		formItem.add(otherInfo);
		section.add(formItem);

		// staff fields
		heading = new Heading1(this.iwrb.getLocalizedString("staffFields",
				"Staff fields"));
		heading.setStyleClass("subHeader");
		form.add(heading);

		section = new Layer(Layer.DIV);
		section.setStyleClass("formSection");
		form.add(section);

		helpLayer = new Layer(Layer.DIV);
		helpLayer.setStyleClass("helperText");
		helpLayer.add(new Text(this.iwrb.getLocalizedString(
				"application.staff_fields_help", "Help for staff fields")));
		section.add(helpLayer);

		formItem = new Layer(Layer.DIV);
		formItem.setStyleClass("formItem");
		label = new Label(new Span(new Text(this.iwrb.getLocalizedString(
				"education_group", "Education group"))), educationGroup);
		formItem.add(label);
		formItem.add(educationGroup);
		section.add(formItem);

		// Botton
		Layer bottom = new Layer(Layer.DIV);
		bottom.setStyleClass("bottom");
		form.add(bottom);

		Link next = getButtonLink(this.iwrb.getLocalizedString("next", "Next"));
		next.setValueOnClick(PARAMETER_ACTION, String.valueOf(ACTION_SAVE));
		next.setOnClick("this.style.display='none';");
		next.setToFormSubmit(form);

		if (this.showSubmitDateString != null) {
			IWTimestamp open = new IWTimestamp(this.showSubmitDateString);
			IWTimestamp now = new IWTimestamp();
			if (now.isLaterThan(open)) {
				bottom.add(next);
			}

		} else {
			bottom.add(next);
		}

		add(form);
	}

	private Form getForm(IWContext iwc, int state) {
		Form form = new Form();
		form.setID("churchCourseApplication");

		return form;
	}

	private int parseAction(IWContext iwc) {
		int action = ACTION_PHASE_1;
		if (iwc.isParameterSet(PARAMETER_ACTION)) {
			action = Integer.parseInt(iwc.getParameter(PARAMETER_ACTION));
		}

		if (iwc.isParameterSet(PARAMETER_APPLICATION_PK)) {
			try {
				choice = getCourseBusiness(iwc)
						.getCourseChoice(
								new Integer(iwc
										.getParameter(PARAMETER_APPLICATION_PK)));
			} catch (RemoteException re) {
				re.printStackTrace();
			}
		}

		return action;
	}

	private CourseBusiness getCourseBusiness(IWContext iwc) {
		try {
			return (CourseBusiness) IBOLookup.getServiceInstance(iwc,
					CourseBusiness.class);
		} catch (IBOLookupException e) {
			throw new IBORuntimeException(e);
		}
	}

	private SchoolBusiness getSchoolBusiness(IWContext iwc) {
		try {
			return (SchoolBusiness) IBOLookup.getServiceInstance(iwc,
					SchoolBusiness.class);
		} catch (IBOLookupException e) {
			throw new IBORuntimeException(e);
		}
	}

	private void save(IWContext iwc) throws RemoteException {
		checkErrors(iwc);

		if (hasErrors()) {
			showPhaseOne(iwc);
			return;
		}

		String childName = iwc.getParameter(PARAMETER_CHILD_NAME);
		String childSSN = iwc.getParameter(PARAMETER_CHILD_PERSONAL_ID);
		String childAddress = iwc.getParameter(PARAMETER_CHILD_HOME);
		String childPO = iwc.getParameter(PARAMETER_CHILD_PO);
		String childPOPlace = iwc.getParameter(PARAMETER_CHILD_PLACE);
		String childGender = iwc.getParameter(PARAMETER_CHILD_GENDER);
		String childMobile = iwc.isParameterSet(PARAMETER_CHILD_MOBILE) ? iwc
				.getParameter(PARAMETER_CHILD_MOBILE) : null;
		String childEmail = iwc.isParameterSet(PARAMETER_CHILD_EMAIL) ? iwc
				.getParameter(PARAMETER_CHILD_EMAIL) : null;
		if (childEmail != null) {
			if (!EmailValidator.getInstance().validateEmail(childEmail)) {
				setError(PARAMETER_CHILD_EMAIL, iwrb
						.getLocalizedString("error.child_invalid_email",
								"Child email is not valid"));
				showPhaseOne(iwc);

				return;
			}
		}
		String coursePK = iwc.getParameter(PARAMETER_COURSE);

		if (coursePK != null) {
			if (!this.choice.getCourse().getPrimaryKey().toString().equals(
					coursePK)) {
				Course course = getCourseBusiness(iwc).getCourse(
						new Integer(coursePK.toString()));
				this.choice.setCourse(course);
				this.choice.store();
			}
		}

		Gender gender = null;
		if ("0".equals(childGender)) {
			try {
				gender = getGenderHome().getMaleGender();
			} catch (FinderException e) {
			}
		} else {
			try {
				gender = getGenderHome().getFemaleGender();
			} catch (FinderException e) {
			}
		}

		User child = null;
		try {
			child = getUserBusiness(iwc).createUserByPersonalIDIfDoesNotExist(
					childName, childSSN, gender, null);
		} catch (CreateException e) {
			setError(PARAMETER_CHILD_NAME, this.iwrb.getLocalizedString(
					"error.create_child", "Couldn't create child"));

			showPhaseOne(iwc);
			return;
		}

		if (childMobile != null) {
			getUserBusiness(iwc).updateUserMobilePhone(child, childMobile);
		}
		if (childEmail != null) {
			try {
				getUserBusiness(iwc).updateUserMail(child, childEmail);
			} catch (CreateException e) {
				e.printStackTrace();
			}
		}

		// Stafnasel 6;107 Reykjavik;Iceland:is_IS;Reykjavik:12345
		StringBuffer childAddressBuffer = new StringBuffer(childAddress);
		childAddressBuffer.append(";");
		childAddressBuffer.append(childPO);
		childAddressBuffer.append(" ");
		childAddressBuffer.append(childPOPlace);
		childAddressBuffer.append(";Iceland:is_IS");

		try {
			getUserBusiness(iwc).updateUsersMainAddressByFullAddressString(
					child, childAddressBuffer.toString());
		} catch (CreateException e) {
			e.printStackTrace();
		}

		String contact1Name = iwc.getParameter(PARAMETER_CONTACT1_NAME);
		String contact1SSN = iwc.getParameter(PARAMETER_CONTACT1_PERSONAL_ID);
		String contact1Address = iwc.getParameter(PARAMETER_CONTACT1_HOME);
		String contact1PO = iwc.getParameter(PARAMETER_CONTACT1_PO);
		String contact1POPlace = iwc.getParameter(PARAMETER_CONTACT1_PLACE);
		String contact1Email = iwc.getParameter(PARAMETER_CONTACT1_EMAIL);
		String contact1Phone = iwc.getParameter(PARAMETER_CONTACT1_PHONE);
		String contact1Mobile = iwc.isParameterSet(PARAMETER_CONTACT1_MOBILE) ? iwc
				.getParameter(PARAMETER_CONTACT1_MOBILE)
				: null;
		String contact1WorkPhone = iwc
				.isParameterSet(PARAMETER_CONTACT1_WORK_PHONE) ? iwc
				.getParameter(PARAMETER_CONTACT1_WORK_PHONE) : null;

		User contact1 = null;
		try {
			contact1 = getUserBusiness(iwc)
					.createUserByPersonalIDIfDoesNotExist(contact1Name,
							contact1SSN, null, null);
		} catch (CreateException e) {
			setError(PARAMETER_CONTACT1_NAME, this.iwrb.getLocalizedString(
					"error.create_contact1", "Couldn't create contact1"));

			showPhaseOne(iwc);
			return;
		}

		if (contact1Phone != null) {
			getUserBusiness(iwc).updateUserHomePhone(contact1, contact1Phone);
		}
		if (contact1Mobile != null) {
			getUserBusiness(iwc)
					.updateUserMobilePhone(contact1, contact1Mobile);
		}
		if (contact1WorkPhone != null) {
			getUserBusiness(iwc).updateUserWorkPhone(contact1,
					contact1WorkPhone);
		}
		if (contact1Email != null) {
			try {
				getUserBusiness(iwc).updateUserMail(contact1, contact1Email);
			} catch (CreateException e) {
				e.printStackTrace();
			}
		}

		// Stafnasel 6;107 Reykjavik;Iceland:is_IS;Reykjavik:12345
		StringBuffer contact1AddressBuffer = new StringBuffer(contact1Address);
		contact1AddressBuffer.append(";");
		contact1AddressBuffer.append(contact1PO);
		contact1AddressBuffer.append(" ");
		contact1AddressBuffer.append(contact1POPlace);
		contact1AddressBuffer.append(";Iceland:is_IS");

		try {
			getUserBusiness(iwc).updateUsersMainAddressByFullAddressString(
					contact1, contact1AddressBuffer.toString());
		} catch (CreateException e) {
			e.printStackTrace();
		}

		CourseApplication application = this.choice.getApplication();
		application.setOwner(contact1);

		User contact2 = null;

		if (iwc.isParameterSet(PARAMETER_CONTACT2_NAME)
				|| iwc.isParameterSet(PARAMETER_CONTACT2_PERSONAL_ID)) {
			String contact2Name = iwc.getParameter(PARAMETER_CONTACT2_NAME);
			String contact2SSN = iwc
					.getParameter(PARAMETER_CONTACT2_PERSONAL_ID);
			String contact2Address = iwc.getParameter(PARAMETER_CONTACT2_HOME);
			String contact2PO = iwc.getParameter(PARAMETER_CONTACT2_PO);
			String contact2POPlace = iwc.getParameter(PARAMETER_CONTACT2_PLACE);
			String contact2Email = iwc.getParameter(PARAMETER_CONTACT2_EMAIL);
			String contact2Phone = iwc.getParameter(PARAMETER_CONTACT2_PHONE);
			String contact2Mobile = iwc
					.isParameterSet(PARAMETER_CONTACT2_MOBILE) ? iwc
					.getParameter(PARAMETER_CONTACT2_MOBILE) : null;
			String contact2WorkPhone = iwc
					.isParameterSet(PARAMETER_CONTACT2_WORK_PHONE) ? iwc
					.getParameter(PARAMETER_CONTACT2_WORK_PHONE) : null;

			try {
				contact2 = getUserBusiness(iwc)
						.createUserByPersonalIDIfDoesNotExist(contact2Name,
								contact2SSN, null, null);
			} catch (CreateException e) {
				setError(PARAMETER_CONTACT2_NAME, this.iwrb.getLocalizedString(
						"error.create_contact2", "Couldn't create contact2"));

				showPhaseOne(iwc);
				return;
			}

			if (contact2Phone != null) {
				getUserBusiness(iwc).updateUserHomePhone(contact2,
						contact2Phone);
			}
			if (contact2Mobile != null) {
				getUserBusiness(iwc).updateUserMobilePhone(contact2,
						contact2Mobile);
			}
			if (contact2WorkPhone != null) {
				getUserBusiness(iwc).updateUserWorkPhone(contact2,
						contact2WorkPhone);
			}
			if (contact2Email != null) {
				try {
					getUserBusiness(iwc)
							.updateUserMail(contact2, contact2Email);
				} catch (CreateException e) {
					e.printStackTrace();
				}
			}

			// Stafnasel 6;107 Reykjavik;Iceland:is_IS;Reykjavik:12345
			StringBuffer contact2AddressBuffer = new StringBuffer(
					contact2Address);
			contact2AddressBuffer.append(";");
			contact2AddressBuffer.append(contact2PO);
			contact2AddressBuffer.append(" ");
			contact2AddressBuffer.append(contact2POPlace);
			contact2AddressBuffer.append(";Iceland:is_IS");

			try {
				getUserBusiness(iwc).updateUsersMainAddressByFullAddressString(
						contact2, contact2AddressBuffer.toString());
			} catch (CreateException e) {
				e.printStackTrace();
			}

		}

		// String childChristeningDate = iwc
		// .getParameter(PARAMETER_CHILD_CHRISTENING_DATE);
		String childMotherName = iwc.getParameter(PARAMETER_MOTHERS_NAME);
		String childFatherName = iwc.getParameter(PARAMETER_FATHERS_NAME);
		String childReligion = iwc.getParameter(PARAMETER_CHILD_RELIGION);
		String childSchool = iwc.isParameterSet(PARAMETER_CHILD_SCHOOL) ? iwc
				.getParameter(PARAMETER_CHILD_SCHOOL) : null;

		String contact1Relation = iwc.getParameter(PARAMETER_CONTACT1_RELATION);

		String otherInfo = iwc.isParameterSet(PARAMETER_OTHER_INFO) ? iwc
				.getParameter(PARAMETER_OTHER_INFO) : null;
				
		int educationGroup = iwc.getIntegerParameter(PARAMETER_EDUCATION_GROUP).intValue();

		try {
			ChurchCourseApplicationInfo info = getChurchCourseApplicationInfoHome()
					.findByApplication(application);
			info.setApplication(application);
			// info.setChristeningDate(childChristeningDate);
			info.setMothersName(childMotherName);
			info.setFathersName(childFatherName);
			info.setReligion(childReligion);
			if (childSchool != null) {
				info.setSchool(Integer.parseInt(childSchool));
			}
			info.setContactRelation(contact1Relation);
			if (contact2 != null) {
				String contact2Relation = iwc
						.getParameter(PARAMETER_CONTACT2_RELATION);
				info.setExtraContact(contact2);
				info.setExtraContactRelation(contact2Relation);
			}
			if (otherInfo != null) {
				info.setInfo(otherInfo);
			}

			if (educationGroup > 0) {
				info.setEducationGroup(educationGroup);
			}
			
			info.store();
		} catch (FinderException e) {
			e.printStackTrace();
		}

		if (application != null) {
			getCourseApplicationSession(iwc).clear(iwc);
			Heading1 heading = new Heading1(this.iwrb.getLocalizedString(
					"application.application_name", "Course application"));
			heading.setStyleClass("applicationHeading");
			add(heading);

			addPhasesReceipt(
					iwc,
					this.iwrb.getLocalizedString("application.receipt",
							"Application receipt"),
					this.iwrb.getLocalizedString(
							"application.application_save_completed",
							"Application sent"),
					this.iwrb
							.getLocalizedString(
									"application.application_send_confirmation",
									"Your course application has been received and will be processed."),
					2, numberOfPhases);

			Layer clearLayer = new Layer(Layer.DIV);
			clearLayer.setStyleClass("Clear");
			add(clearLayer);

			Layer bottom = new Layer(Layer.DIV);
			bottom.setStyleClass("bottom");
			add(bottom);

		} else {
			Heading1 heading = new Heading1(this.iwrb.getLocalizedString(
					"application.application_name", "Course application"));
			heading.setStyleClass("applicationHeading");
			add(heading);

			add(getPhasesHeader(this.iwrb.getLocalizedString(
					"application.submit_failed", "Application submit failed"),
					2, numberOfPhases));
			add(getStopLayer(this.iwrb.getLocalizedString(
					"application.submit_failed", "Application submit failed"),
					this.iwrb.getLocalizedString(
							"application.submit_failed_info",
							"Application submit failed")));
		}

	}

	private CourseApplicationSession getCourseApplicationSession(IWContext iwc) {
		try {
			return (CourseApplicationSession) IBOLookup.getSessionInstance(iwc,
					CourseApplicationSession.class);
		} catch (IBOLookupException e) {
			throw new IBORuntimeException(e);
		}
	}

	private void checkErrors(IWContext iwc) {
		if (!iwc.isParameterSet(PARAMETER_CHILD_NAME)) {
			setError(PARAMETER_CHILD_NAME, this.iwrb.getLocalizedString(
					"error.child_name", "Child name is empty"));
		}

		if (!iwc.isParameterSet(PARAMETER_CHILD_PERSONAL_ID)) {
			setError(PARAMETER_CHILD_PERSONAL_ID, this.iwrb.getLocalizedString(
					"error.child_ssn_empty", "Child personal ID is empty"));
		} else if (!SocialSecurityNumber.isValidSocialSecurityNumber(iwc
				.getParameter(PARAMETER_CHILD_PERSONAL_ID), iwc
				.getCurrentLocale())) {
			setError(PARAMETER_CHILD_PERSONAL_ID, this.iwrb.getLocalizedString(
					"error.child_invalid_personal_id",
					"Invalid child personal ID"));
		}

		if (!iwc.isParameterSet(PARAMETER_CHILD_HOME)) {
			setError(PARAMETER_CHILD_HOME, this.iwrb.getLocalizedString(
					"error.child_home", "Child home is empty"));
		}

		if (!iwc.isParameterSet(PARAMETER_CHILD_PO)) {
			setError(PARAMETER_CHILD_PO, this.iwrb.getLocalizedString(
					"error.child_po", "Child PO is empty"));
		}

		if (!iwc.isParameterSet(PARAMETER_CHILD_PLACE)) {
			setError(PARAMETER_CHILD_PLACE, this.iwrb.getLocalizedString(
					"error.child_place", "Child PO place is empty"));
		}

		if (!iwc.isParameterSet(PARAMETER_CHILD_CHRISTENING_DATE)) {
			setError(PARAMETER_CHILD_CHRISTENING_DATE, this.iwrb
					.getLocalizedString("error.child_christening_date",
							"Child christening date is empty"));
		}

		if (!iwc.isParameterSet(PARAMETER_MOTHERS_NAME)) {
			setError(PARAMETER_MOTHERS_NAME, this.iwrb.getLocalizedString(
					"error.mothers_name", "Mothers name is empty"));
		}

		if (!iwc.isParameterSet(PARAMETER_FATHERS_NAME)) {
			setError(PARAMETER_FATHERS_NAME, this.iwrb.getLocalizedString(
					"error.fathers_name", "Fathers name is empty"));
		}

		if (!iwc.isParameterSet(PARAMETER_CHILD_GENDER)
				|| "-1".equals(iwc.getParameter(PARAMETER_CHILD_GENDER))) {
			setError(PARAMETER_CHILD_GENDER, this.iwrb.getLocalizedString(
					"error.child_gender", "Child gender is empty"));
		}

		if (!iwc.isParameterSet(PARAMETER_CHILD_RELIGION)
				|| "-1".equals(iwc.getParameter(PARAMETER_CHILD_RELIGION))) {
			setError(PARAMETER_CHILD_RELIGION, this.iwrb.getLocalizedString(
					"error.child_religion", "Child religion is empty"));
		}

		if (!iwc.isParameterSet(PARAMETER_COURSE)) {
			setError(PARAMETER_COURSE, this.iwrb.getLocalizedString(
					"error.course", "No course selected"));
		}

		if (!iwc.isParameterSet(PARAMETER_CONTACT1_NAME)) {
			setError(PARAMETER_CONTACT1_NAME, this.iwrb.getLocalizedString(
					"error.contact1_name", "Contact1 name is empty"));
		}

		if (!iwc.isParameterSet(PARAMETER_CONTACT1_PERSONAL_ID)) {
			setError(PARAMETER_CONTACT1_PERSONAL_ID, this.iwrb
					.getLocalizedString("error.contact1_ssn_empty",
							"Contact1 ssn is empty"));
		} else if (!SocialSecurityNumber.isValidSocialSecurityNumber(iwc
				.getParameter(PARAMETER_CONTACT1_PERSONAL_ID), iwc
				.getCurrentLocale())) {
			setError(PARAMETER_CONTACT1_PERSONAL_ID, this.iwrb
					.getLocalizedString("error.contact1_invalid_personal_id",
							"Invalid contact1 personal ID"));
		}

		if (!iwc.isParameterSet(PARAMETER_CONTACT1_HOME)) {
			setError(PARAMETER_CONTACT1_HOME, this.iwrb.getLocalizedString(
					"error.contact1_home", "Contact1 home is empty"));
		}

		if (!iwc.isParameterSet(PARAMETER_CONTACT1_PO)) {
			setError(PARAMETER_CONTACT1_PO, this.iwrb.getLocalizedString(
					"error.contact1_po", "Contact1 PO is empty"));
		}

		if (!iwc.isParameterSet(PARAMETER_CONTACT1_PLACE)) {
			setError(PARAMETER_CONTACT1_PLACE, this.iwrb.getLocalizedString(
					"error.contact1_place", "Contact1 PO place is empty"));
		}

		if (!iwc.isParameterSet(PARAMETER_CONTACT1_EMAIL)) {
			setError(PARAMETER_CONTACT1_EMAIL, this.iwrb.getLocalizedString(
					"error.contact1_email", "Contact1 email is empty"));
		} else if (!EmailValidator.getInstance().validateEmail(
				iwc.getParameter(PARAMETER_CONTACT1_EMAIL))) {
			setError(PARAMETER_CONTACT1_EMAIL, iwrb.getLocalizedString(
					"error.contact1_invalid_email",
					"Contact1 email is not valid"));
		}

		if (!iwc.isParameterSet(PARAMETER_CONTACT1_PHONE)) {
			setError(PARAMETER_CONTACT1_PHONE, this.iwrb.getLocalizedString(
					"error.contact1_phone", "Contact1 phone is empty"));
		}

		if (!iwc.isParameterSet(PARAMETER_CONTACT1_RELATION)
				|| "-1".equals(iwc.getParameter(PARAMETER_CONTACT1_RELATION))) {
			setError(PARAMETER_CONTACT1_RELATION, this.iwrb.getLocalizedString(
					"error.contact1_relation", "Contact1 relation is empty"));
		}

		if (iwc.isParameterSet(PARAMETER_CONTACT2_NAME)
				|| iwc.isParameterSet(PARAMETER_CONTACT2_PERSONAL_ID)) {
			if (!iwc.isParameterSet(PARAMETER_CONTACT2_NAME)) {
				setError(PARAMETER_CONTACT2_NAME, this.iwrb.getLocalizedString(
						"error.contact2_name", "Contact2 name is empty"));
			}

			if (!iwc.isParameterSet(PARAMETER_CONTACT2_PERSONAL_ID)) {
				setError(PARAMETER_CONTACT2_PERSONAL_ID, this.iwrb
						.getLocalizedString("error.contact2_ssn_empty",
								"Contact2 ssn is empty"));
			} else if (!SocialSecurityNumber.isValidSocialSecurityNumber(iwc
					.getParameter(PARAMETER_CONTACT2_PERSONAL_ID), iwc
					.getCurrentLocale())) {
				setError(PARAMETER_CONTACT2_PERSONAL_ID, this.iwrb
						.getLocalizedString(
								"error.contact2_invalid_personal_id",
								"Invalid contact2 personal ID"));
			}

			if (!iwc.isParameterSet(PARAMETER_CONTACT2_HOME)) {
				setError(PARAMETER_CONTACT2_HOME, this.iwrb.getLocalizedString(
						"error.contact2_home", "Contact2 home is empty"));
			}

			if (!iwc.isParameterSet(PARAMETER_CONTACT2_PO)) {
				setError(PARAMETER_CONTACT2_PO, this.iwrb.getLocalizedString(
						"error.contact2_po", "Contact2 PO is empty"));
			}

			if (!iwc.isParameterSet(PARAMETER_CONTACT2_PLACE)) {
				setError(PARAMETER_CONTACT2_PLACE, this.iwrb
						.getLocalizedString("error.contact2_place",
								"Contact2 PO place is empty"));
			}

			if (!iwc.isParameterSet(PARAMETER_CONTACT2_EMAIL)) {
				setError(PARAMETER_CONTACT2_EMAIL, this.iwrb
						.getLocalizedString("error.contact2_email",
								"Contact2 email is empty"));
			} else if (!EmailValidator.getInstance().validateEmail(
					iwc.getParameter(PARAMETER_CONTACT2_EMAIL))) {
				setError(PARAMETER_CONTACT2_EMAIL, iwrb.getLocalizedString(
						"error.contact2_invalid_email",
						"Contact2 email is not valid"));
			}

			if (!iwc.isParameterSet(PARAMETER_CONTACT2_PHONE)) {
				setError(PARAMETER_CONTACT2_PHONE, this.iwrb
						.getLocalizedString("error.contact2_phone",
								"Contact2 phone is empty"));
			}

			if (!iwc.isParameterSet(PARAMETER_CONTACT2_RELATION)
					|| "-1".equals(iwc
							.getParameter(PARAMETER_CONTACT2_RELATION))) {
				setError(PARAMETER_CONTACT2_RELATION, this.iwrb
						.getLocalizedString("error.contact2_relation",
								"Contact2 relation is empty"));
			}
		}
	}

	private String getSchoolTypePK() {
		return this.iSchoolTypePK;
	}

	public void setSchoolTypePK(String schoolTypePK) {
		this.iSchoolTypePK = schoolTypePK;
	}

	private String getCourseTypePK() {
		return this.iCourseTypePK;
	}

	public void setCourseTypePK(String courseTypePK) {
		this.iCourseTypePK = courseTypePK;
	}

	private String getDropdownSchoolTypePK() {
		return this.iDropdownSchoolTypePK;
	}

	public void setDropdownSchoolTypePK(String schoolTypePK) {
		this.iDropdownSchoolTypePK = schoolTypePK;
	}

	public void setDateStringToShowSubmit(String dateString) {
		this.showSubmitDateString = dateString;
	}

	public String getDateStringToShowSubmit() {
		return this.showSubmitDateString;
	}

	protected GenderHome getGenderHome() throws RemoteException {
		return (GenderHome) IDOLookup.getHome(Gender.class);
	}

	protected ChurchCourseApplicationInfoHome getChurchCourseApplicationInfoHome()
			throws RemoteException {
		return (ChurchCourseApplicationInfoHome) IDOLookup
				.getHome(ChurchCourseApplicationInfo.class);
	}
}
