package is.idega.idegaweb.egov.church.presentation;

import is.idega.idegaweb.egov.application.presentation.ApplicationForm;
import is.idega.idegaweb.egov.course.CourseConstants;
import is.idega.idegaweb.egov.course.business.CourseBusiness;
import is.idega.idegaweb.egov.course.business.CourseDWR;

import java.rmi.RemoteException;
import java.util.Collection;
import java.util.Iterator;

import com.idega.block.school.presentation.SchoolDropdown;
import com.idega.business.IBOLookup;
import com.idega.business.IBOLookupException;
import com.idega.business.IBORuntimeException;
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
import com.idega.util.IWTimestamp;
import com.idega.util.PresentationUtil;

public class ChurchCourseApplication extends ApplicationForm {
	private static final String BUNDLE_IDENTIFIER = "is.idega.idegaweb.egov.church";
	
	private static final int ACTION_PHASE_1 = 1;
	private static final int ACTION_SAVE = 0;

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

	private IWResourceBundle iwrb = null;

	private int numberOfPhases = 2;
	private boolean iUseSessionUser = false;
	private String iSchoolTypePK = null;
	private String iDropdownSchoolTypePK = null;
	private String iCourseTypePK = null;

	@Override
	protected String getCaseCode() {
		return CourseConstants.CASE_CODE_KEY;
	}

	@Override
	public String getBundleIdentifier() {
		return ChurchCourseApplication.BUNDLE_IDENTIFIER;
	}

	@Override
	protected void present(IWContext iwc) {
		this.iwrb = getResourceBundle(iwc);
		PresentationUtil.addStyleSheetToHeader(iwc, getBundle(iwc).getVirtualPathWithFileNameString("style/church.css"));

		try {
			switch (parseAction(iwc)) {
				case ACTION_PHASE_1:
					showPhaseOne(iwc);
					break;

				case ACTION_SAVE:
					save(iwc);
					break;
			}
		}
		catch (RemoteException e) {
			e.printStackTrace();
		}
	}

	private void showPhaseOne(IWContext iwc) throws RemoteException {
		Form form = getForm(iwc, ACTION_PHASE_1);
		form.setId("course_step_1");
		form.add(new HiddenInput(PARAMETER_ACTION, String.valueOf(ACTION_PHASE_1)));

		addErrors(iwc, form);

		Heading1 heading = new Heading1(this.iwrb.getLocalizedString("application.application_name", "Course application"));
		heading.setStyleClass("applicationHeading");
		form.add(heading);

		form.add(getPhasesHeader(iwrb.getLocalizedString("application.course", "Course"), 1, numberOfPhases, true));

		Layer info = new Layer(Layer.DIV);
		info.setStyleClass("info");
		form.add(info);

		Paragraph infoHelp = new Paragraph();
		infoHelp.setStyleClass("infoHelp");
		infoHelp.add(new Text(this.iwrb.getLocalizedString("application.application_help", "Below you can select the type of courses you want to register to if there are any available.<br />The courses are held at the main office unless otherwise stated.<br /><br />Please select the courses you want to register to and click 'Next'.")));
		info.add(infoHelp);

		Integer schoolTypePK = getSchoolTypePK() != null ? new Integer(getSchoolTypePK().toString()) : null;
		Integer courseTypePK = getCourseTypePK() != null ? new Integer(getCourseTypePK().toString()) : null;
		Integer dropdownSchoolTypePK = getDropdownSchoolTypePK() != null ? new Integer(getDropdownSchoolTypePK().toString()) : null;

		//Child info
		heading = new Heading1(this.iwrb.getLocalizedString("application.child_info", "Child info"));
		heading.setStyleClass("subHeader");
		heading.setStyleClass("topSubHeader");
		form.add(heading);

		Layer section = new Layer(Layer.DIV);
		section.setStyleClass("formSection");
		form.add(section);

		if (schoolTypePK == null || courseTypePK == null || dropdownSchoolTypePK == null) {
			Layer formItem = new Layer(Layer.DIV);
			formItem.setStyleClass("formItem");
			formItem.add(new Text("Set the parameters for the form"));

			add(form);
			
			return;
		}

		Layer helpLayer = new Layer(Layer.DIV);
		helpLayer.setStyleClass("helperText");
		helpLayer.add(new Text(this.iwrb.getLocalizedString("application.child_info_help", "Child info help.")));
		section.add(helpLayer);
		
		
		TextInput childName = new TextInput(PARAMETER_CHILD_NAME);

		TextInput childPersonalID = new TextInput(PARAMETER_CHILD_PERSONAL_ID);
		childPersonalID.setMaxlength(10);

		TextInput childHome = new TextInput(PARAMETER_CHILD_HOME);

		TextInput childPO = new TextInput(PARAMETER_CHILD_PO);

		TextInput childPlace = new TextInput(PARAMETER_CHILD_PLACE);

		TextInput childMobile = new TextInput(PARAMETER_CHILD_MOBILE);

		TextInput childEmail = new TextInput(PARAMETER_CHILD_EMAIL);
		
		DropdownMenu childGender = new DropdownMenu(PARAMETER_CHILD_GENDER);
		childGender.addMenuElement(-1, this.iwrb.getLocalizedString("child.select_gender", "Please select gender"));
		childGender.addMenuElement(0, this.iwrb.getLocalizedString("child.male", "Male"));
		childGender.addMenuElement(1, this.iwrb.getLocalizedString("child.female", "Female"));
		
		DropdownMenu childReligion = new DropdownMenu(PARAMETER_CHILD_RELIGION);
		childReligion.addMenuElement(-1, this.iwrb.getLocalizedString("religion.select_religion", "Please select religion"));
		childReligion.addMenuElement(0, this.iwrb.getLocalizedString("religion.national_church", "National church"));
		childReligion.addMenuElement(1, this.iwrb.getLocalizedString("religion.other", "Other"));

		IWDatePicker childChristeningDate = new IWDatePicker(PARAMETER_CHILD_CHRISTENING_DATE);
		childChristeningDate.setShowYearChange(true);

		TextInput mothersName = new TextInput(PARAMETER_MOTHERS_NAME);

		TextInput fathersName = new TextInput(PARAMETER_FATHERS_NAME);

		DropdownMenu childSchool = new SchoolDropdown(PARAMETER_CHILD_SCHOOL, dropdownSchoolTypePK.intValue());
		childSchool.addMenuElementFirst("-1", this.iwrb.getLocalizedString("child.select_school", "Please select school"));
		
		Layer formItem = new Layer(Layer.DIV);
		formItem.setStyleClass("formItem");
		formItem.setStyleClass("required");
		Label label = new Label(new Span(new Text(this.iwrb.getLocalizedString("child.name", "Child's name"))), childName);
		formItem.add(label);
		formItem.add(childName);
		section.add(formItem);

		formItem = new Layer(Layer.DIV);
		formItem.setStyleClass("formItem");
		formItem.setStyleClass("required");
		label = new Label(new Span(new Text(this.iwrb.getLocalizedString("child.ssn", "Child's ssn"))), childPersonalID);
		formItem.add(label);
		formItem.add(childPersonalID);
		section.add(formItem);

		formItem = new Layer(Layer.DIV);
		formItem.setStyleClass("formItem");
		formItem.setStyleClass("required");
		label = new Label(new Span(new Text(this.iwrb.getLocalizedString("child.home", "Child's address"))), childHome);
		formItem.add(label);
		formItem.add(childHome);
		section.add(formItem);
		
		formItem = new Layer(Layer.DIV);
		formItem.setStyleClass("formItem");
		formItem.setStyleClass("required");
		label = new Label(new Span(new Text(this.iwrb.getLocalizedString("child.po", "Child's po"))), childPO);
		formItem.add(label);
		formItem.add(childPO);
		section.add(formItem);

		formItem = new Layer(Layer.DIV);
		formItem.setStyleClass("formItem");
		formItem.setStyleClass("required");
		label = new Label(new Span(new Text(this.iwrb.getLocalizedString("child.po_place", "Child's PO place"))), childPlace);
		formItem.add(label);
		formItem.add(childPlace);
		section.add(formItem);
		
		formItem = new Layer(Layer.DIV);
		formItem.setStyleClass("formItem");
		label = new Label(new Span(new Text(this.iwrb.getLocalizedString("child.mobile", "Child's mobile"))), childMobile);
		formItem.add(label);
		formItem.add(childMobile);
		section.add(formItem);

		formItem = new Layer(Layer.DIV);
		formItem.setStyleClass("formItem");
		label = new Label(new Span(new Text(this.iwrb.getLocalizedString("child.email", "Child's email"))), childEmail);
		formItem.add(label);
		formItem.add(childEmail);
		section.add(formItem);

		formItem = new Layer(Layer.DIV);
		formItem.setStyleClass("formItem");
		formItem.setStyleClass("required");
		label = new Label(new Span(new Text(this.iwrb.getLocalizedString("child.gender", "Child's gender"))), childGender);
		formItem.add(label);
		formItem.add(childGender);
		section.add(formItem);

		formItem = new Layer(Layer.DIV);
		formItem.setStyleClass("formItem");
		formItem.setStyleClass("required");
		label = new Label(new Span(new Text(this.iwrb.getLocalizedString("child.religion", "Child's religion"))), childReligion);
		formItem.add(label);
		formItem.add(childReligion);
		section.add(formItem);

		formItem = new Layer(Layer.DIV);
		formItem.setStyleClass("formItem");
		formItem.setStyleClass("required");
		label = new Label(new Span(new Text(this.iwrb.getLocalizedString("child.christening_date", "Child's Christening date"))), childChristeningDate);
		formItem.add(label);
		formItem.add(childChristeningDate);
		section.add(formItem);

		formItem = new Layer(Layer.DIV);
		formItem.setStyleClass("formItem");
		formItem.setStyleClass("required");
		label = new Label(new Span(new Text(this.iwrb.getLocalizedString("mothers_name", "Mother's name"))), mothersName);
		formItem.add(label);
		formItem.add(mothersName);
		section.add(formItem);

		formItem = new Layer(Layer.DIV);
		formItem.setStyleClass("formItem");
		formItem.setStyleClass("required");
		label = new Label(new Span(new Text(this.iwrb.getLocalizedString("fathers_name", "Father's name"))), fathersName);
		formItem.add(label);
		formItem.add(fathersName);
		section.add(formItem);

		formItem = new Layer(Layer.DIV);
		formItem.setStyleClass("formItem");
		label = new Label(new Span(new Text(this.iwrb.getLocalizedString("child.school", "Child's school"))), childSchool);
		formItem.add(label);
		formItem.add(childSchool);
		section.add(formItem);

		//Courses
		Collection courses = null;
		if (courseTypePK != null) {
			courses = getCourseBusiness(iwc).getCoursesDWR(-1, schoolTypePK.intValue(), courseTypePK.intValue(), -1, iwc.getCurrentLocale().getCountry(), this.iUseSessionUser);
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
				row = group.createRow();
				if (counter++ % 2 == 0) {
					row.setStyleClass("even");
				}
				else {
					row.setStyleClass("odd");
				}
				CourseDWR course = (CourseDWR) iter.next();
				cell = row.createCell();
				cell.setStyleClass("column0");

				RadioButton radio = new RadioButton(PARAMETER_COURSE, course.getPk());
				radio.setStyleClass("checkbox");
				radio.keepStatusOnAction(true);
				cell.add(radio);

				cell = row.createCell();
				cell.setStyleClass("column1");
				cell.add(new Text(course.getProvider()));

				cell = row.createCell();
				cell.setStyleClass("column2");
				cell.add(new Text(course.getFirstDateOfCourse().getDateString("dd.MM.yyyy")));

				cell = row.createCell();
				cell.setStyleClass("column3");
				cell.add(new Text(course.getFirstDateOfCourse().getDateString("HH:mm")));
			}
		}

		heading = new Heading1(this.iwrb.getLocalizedString("available_courses", "Available courses"));
		heading.setStyleClass("subHeader");
		form.add(heading);

		section = new Layer(Layer.DIV);
		section.setStyleClass("formSection");
		section.setStyleClass("formTableSection");
		form.add(section);

		helpLayer = new Layer(Layer.DIV);
		helpLayer.setStyleClass("helperText");
		helpLayer.add(new Text(this.iwrb.getLocalizedString("application.select_course_help", "Please select the course you want to register to.")));
		section.add(helpLayer);

		formItem = new Layer(Layer.DIV);
		formItem.setStyleClass("formItem");
		formItem.add(table);
		section.add(formItem);

		//Contact 1
		TextInput contact1Name = new TextInput(PARAMETER_CONTACT1_NAME);

		TextInput contact1PersonalID = new TextInput(PARAMETER_CONTACT1_PERSONAL_ID);
		contact1PersonalID.setMaxlength(10);

		TextInput contact1Home = new TextInput(PARAMETER_CONTACT1_HOME);

		TextInput contact1PO = new TextInput(PARAMETER_CONTACT1_PO);

		TextInput contact1Place = new TextInput(PARAMETER_CONTACT1_PLACE);

		TextInput contact1Email = new TextInput(PARAMETER_CONTACT1_EMAIL);

		TextInput contact1Phone = new TextInput(PARAMETER_CONTACT1_PHONE);

		TextInput contact1Mobile = new TextInput(PARAMETER_CONTACT1_MOBILE);

		TextInput contact1WorkPhone = new TextInput(PARAMETER_CONTACT1_WORK_PHONE);

		DropdownMenu contact1Relation = new DropdownMenu(PARAMETER_CONTACT1_RELATION);
		contact1Relation.addMenuElement(-1, this.iwrb.getLocalizedString("rel.select_relation", "Please select relation"));
		contact1Relation.addMenuElement(0, this.iwrb.getLocalizedString("rel.mother", "Mother"));
		contact1Relation.addMenuElement(1, this.iwrb.getLocalizedString("rel.father", "Father"));
		contact1Relation.addMenuElement(2, this.iwrb.getLocalizedString("rel.guardian", "Guardian"));
		contact1Relation.addMenuElement(3, this.iwrb.getLocalizedString("rel.other", "Other"));

		heading = new Heading1(this.iwrb.getLocalizedString("contact1", "Contact 1"));
		heading.setStyleClass("subHeader");
		form.add(heading);

		section = new Layer(Layer.DIV);
		section.setStyleClass("formSection");
		section.setStyleClass("formTableSection");
		form.add(section);

		helpLayer = new Layer(Layer.DIV);
		helpLayer.setStyleClass("helperText");
		helpLayer.add(new Text(this.iwrb.getLocalizedString("application.contact1_help", "Help for contact 1")));
		section.add(helpLayer);

		formItem = new Layer(Layer.DIV);
		formItem.setStyleClass("formItem");
		formItem.setStyleClass("required");
		label = new Label(new Span(new Text(this.iwrb.getLocalizedString("contact1.name", "Contact1 name"))), contact1Name);
		formItem.add(label);
		formItem.add(contact1Name);
		section.add(formItem);

		formItem = new Layer(Layer.DIV);
		formItem.setStyleClass("formItem");
		formItem.setStyleClass("required");
		label = new Label(new Span(new Text(this.iwrb.getLocalizedString("contact1.ssn", "Contact1 ssn"))), contact1PersonalID);
		formItem.add(label);
		formItem.add(contact1PersonalID);
		section.add(formItem);
		
		formItem = new Layer(Layer.DIV);
		formItem.setStyleClass("formItem");
		formItem.setStyleClass("required");
		label = new Label(new Span(new Text(this.iwrb.getLocalizedString("contact1.address", "Contact1 address"))), contact1Home);
		formItem.add(label);
		formItem.add(contact1Home);
		section.add(formItem);

		formItem = new Layer(Layer.DIV);
		formItem.setStyleClass("formItem");
		formItem.setStyleClass("required");
		label = new Label(new Span(new Text(this.iwrb.getLocalizedString("contact1.po", "Contact1 po"))), contact1PO);
		formItem.add(label);
		formItem.add(contact1PO);
		section.add(formItem);

		formItem = new Layer(Layer.DIV);
		formItem.setStyleClass("formItem");
		formItem.setStyleClass("required");
		label = new Label(new Span(new Text(this.iwrb.getLocalizedString("contact1.po_place", "Contact1 PO place"))), contact1Place);
		formItem.add(label);
		formItem.add(contact1Place);
		section.add(formItem);

		formItem = new Layer(Layer.DIV);
		formItem.setStyleClass("formItem");
		formItem.setStyleClass("required");
		label = new Label(new Span(new Text(this.iwrb.getLocalizedString("contact1.email", "Contact1 email"))), contact1Email);
		formItem.add(label);
		formItem.add(contact1Email);
		section.add(formItem);

		formItem = new Layer(Layer.DIV);
		formItem.setStyleClass("formItem");
		formItem.setStyleClass("required");
		label = new Label(new Span(new Text(this.iwrb.getLocalizedString("contact1.phone", "Contact1 phone"))), contact1Phone);
		formItem.add(label);
		formItem.add(contact1Phone);
		section.add(formItem);

		formItem = new Layer(Layer.DIV);
		formItem.setStyleClass("formItem");
		label = new Label(new Span(new Text(this.iwrb.getLocalizedString("contact1.mobile", "Contact1 mobile"))), contact1Mobile);
		formItem.add(label);
		formItem.add(contact1Mobile);
		section.add(formItem);
		
		formItem = new Layer(Layer.DIV);
		formItem.setStyleClass("formItem");
		label = new Label(new Span(new Text(this.iwrb.getLocalizedString("contact1.work_phone", "Contact1 work phone"))), contact1WorkPhone);
		formItem.add(label);
		formItem.add(contact1WorkPhone);
		section.add(formItem);

		formItem = new Layer(Layer.DIV);
		formItem.setStyleClass("formItem");
		formItem.setStyleClass("required");
		label = new Label(new Span(new Text(this.iwrb.getLocalizedString("contact1.relation", "Contact1 relation"))), contact1Relation);
		formItem.add(label);
		formItem.add(contact1Relation);
		section.add(formItem);

		//Contact 2
		TextInput contact2Name = new TextInput(PARAMETER_CONTACT2_NAME);

		TextInput contact2PersonalID = new TextInput(PARAMETER_CONTACT2_PERSONAL_ID);
		contact2PersonalID.setMaxlength(10);

		TextInput contact2Home = new TextInput(PARAMETER_CONTACT2_HOME);

		TextInput contact2PO = new TextInput(PARAMETER_CONTACT2_PO);

		TextInput contact2Place = new TextInput(PARAMETER_CONTACT2_PLACE);

		TextInput contact2Email = new TextInput(PARAMETER_CONTACT2_EMAIL);

		TextInput contact2Phone = new TextInput(PARAMETER_CONTACT2_PHONE);

		TextInput contact2Mobile = new TextInput(PARAMETER_CONTACT2_MOBILE);

		TextInput contact2WorkPhone = new TextInput(PARAMETER_CONTACT2_WORK_PHONE);

		DropdownMenu contact2Relation = new DropdownMenu(PARAMETER_CONTACT2_RELATION);
		contact2Relation.addMenuElement(-1, this.iwrb.getLocalizedString("rel.select_relation", "Please select relation"));
		contact2Relation.addMenuElement(0, this.iwrb.getLocalizedString("rel.mother", "Mother"));
		contact2Relation.addMenuElement(1, this.iwrb.getLocalizedString("rel.father", "Father"));
		contact2Relation.addMenuElement(2, this.iwrb.getLocalizedString("rel.guardian", "Guardian"));
		contact2Relation.addMenuElement(3, this.iwrb.getLocalizedString("rel.other", "Other"));

		heading = new Heading1(this.iwrb.getLocalizedString("contact2", "Contact 2"));
		heading.setStyleClass("subHeader");
		form.add(heading);

		section = new Layer(Layer.DIV);
		section.setStyleClass("formSection");
		section.setStyleClass("formTableSection");
		form.add(section);

		helpLayer = new Layer(Layer.DIV);
		helpLayer.setStyleClass("helperText");
		helpLayer.add(new Text(this.iwrb.getLocalizedString("application.contact2_help", "Help for contact 2")));
		section.add(helpLayer);

		formItem = new Layer(Layer.DIV);
		formItem.setStyleClass("formItem");
		formItem.setStyleClass("required");
		label = new Label(new Span(new Text(this.iwrb.getLocalizedString("contact2.name", "Contact2 name"))), contact2Name);
		formItem.add(label);
		formItem.add(contact2Name);
		section.add(formItem);

		formItem = new Layer(Layer.DIV);
		formItem.setStyleClass("formItem");
		formItem.setStyleClass("required");
		label = new Label(new Span(new Text(this.iwrb.getLocalizedString("contact2.ssn", "Contact2 ssn"))), contact2PersonalID);
		formItem.add(label);
		formItem.add(contact2PersonalID);
		section.add(formItem);
		
		formItem = new Layer(Layer.DIV);
		formItem.setStyleClass("formItem");
		formItem.setStyleClass("required");
		label = new Label(new Span(new Text(this.iwrb.getLocalizedString("contact2.address", "Contact2 address"))), contact2Home);
		formItem.add(label);
		formItem.add(contact2Home);
		section.add(formItem);

		formItem = new Layer(Layer.DIV);
		formItem.setStyleClass("formItem");
		formItem.setStyleClass("required");
		label = new Label(new Span(new Text(this.iwrb.getLocalizedString("contact2.po", "Contact2 po"))), contact2PO);
		formItem.add(label);
		formItem.add(contact1PO);
		section.add(formItem);

		formItem = new Layer(Layer.DIV);
		formItem.setStyleClass("formItem");
		formItem.setStyleClass("required");
		label = new Label(new Span(new Text(this.iwrb.getLocalizedString("contact2.po_place", "Contact2 PO place"))), contact2Place);
		formItem.add(label);
		formItem.add(contact2Place);
		section.add(formItem);

		formItem = new Layer(Layer.DIV);
		formItem.setStyleClass("formItem");
		formItem.setStyleClass("required");
		label = new Label(new Span(new Text(this.iwrb.getLocalizedString("contact2.email", "Contact2 email"))), contact2Email);
		formItem.add(label);
		formItem.add(contact2Email);
		section.add(formItem);

		formItem = new Layer(Layer.DIV);
		formItem.setStyleClass("formItem");
		formItem.setStyleClass("required");
		label = new Label(new Span(new Text(this.iwrb.getLocalizedString("contact2.phone", "Contact2 phone"))), contact2Phone);
		formItem.add(label);
		formItem.add(contact2Phone);
		section.add(formItem);

		formItem = new Layer(Layer.DIV);
		formItem.setStyleClass("formItem");
		label = new Label(new Span(new Text(this.iwrb.getLocalizedString("contact2.mobile", "Contact2 mobile"))), contact2Mobile);
		formItem.add(label);
		formItem.add(contact2Mobile);
		section.add(formItem);
		
		formItem = new Layer(Layer.DIV);
		formItem.setStyleClass("formItem");
		label = new Label(new Span(new Text(this.iwrb.getLocalizedString("contact2.work_phone", "Contact2 work phone"))), contact2WorkPhone);
		formItem.add(label);
		formItem.add(contact2WorkPhone);
		section.add(formItem);

		formItem = new Layer(Layer.DIV);
		formItem.setStyleClass("formItem");
		formItem.setStyleClass("required");
		label = new Label(new Span(new Text(this.iwrb.getLocalizedString("contact2.relation", "Contact2 relation"))), contact2Relation);
		formItem.add(label);
		formItem.add(contact2Relation);
		section.add(formItem);

		//Other info 
		heading = new Heading1(this.iwrb.getLocalizedString("otherInfo", "Other info"));
		heading.setStyleClass("subHeader");
		form.add(heading);

		section = new Layer(Layer.DIV);
		section.setStyleClass("formSection");
		section.setStyleClass("formTableSection");
		form.add(section);

		helpLayer = new Layer(Layer.DIV);
		helpLayer.setStyleClass("helperText");
		helpLayer.add(new Text(this.iwrb.getLocalizedString("application.other_info_help", "Help for other info")));
		section.add(helpLayer);

		TextArea otherInfo = new TextArea(PARAMETER_OTHER_INFO);
		
		formItem = new Layer(Layer.DIV);
		formItem.setStyleClass("formItem");
		formItem.setStyleClass("required");
		label = new Label(new Span(new Text(this.iwrb.getLocalizedString("other_info", "Other info"))), otherInfo);
		formItem.add(label);
		formItem.add(otherInfo);
		section.add(formItem);

		//Botton
		Layer bottom = new Layer(Layer.DIV);
		bottom.setStyleClass("bottom");
		form.add(bottom);

		Link next = getButtonLink(this.iwrb.getLocalizedString("next", "Next"));
		next.setValueOnClick(PARAMETER_ACTION, String.valueOf(ACTION_SAVE));
		next.setOnClick("this.style.display='none';");
		next.setToFormSubmit(form);
		
		IWTimestamp open = new IWTimestamp();
		open.setDay(18);
		open.setMonth(5);
		open.setYear(2009);
		open.setTime(9, 0, 0);
		
		System.out.println("open = " + open.getDateString("dd.MM.yyyy hh:mm:ss"));
		IWTimestamp now = new IWTimestamp();
		System.out.println("now = " + now.getDateString("dd.MM.yyyy hh:mm:ss"));
		if (now.isLaterThan(open)) {
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

		return action;
	}

	private CourseBusiness getCourseBusiness(IWContext iwc) {
		try {
			return (CourseBusiness) IBOLookup.getServiceInstance(iwc, CourseBusiness.class);
		}
		catch (IBOLookupException e) {
			throw new IBORuntimeException(e);
		}
	}

	private void save(IWContext iwc) throws RemoteException {
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

}