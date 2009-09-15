package is.idega.idegaweb.egov.church.business;

import is.idega.idegaweb.egov.accounting.business.CitizenBusiness;
import is.idega.idegaweb.egov.church.data.ChurchCourseApplicationInfo;
import is.idega.idegaweb.egov.church.data.ChurchCourseApplicationInfoHome;
import is.idega.idegaweb.egov.course.business.CourseBusiness;
import is.idega.idegaweb.egov.course.data.Course;
import is.idega.idegaweb.egov.course.data.CourseChoice;
import is.idega.idegaweb.egov.course.presentation.CourseBlock;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.Locale;

import javax.ejb.FinderException;
import javax.servlet.http.HttpServletRequest;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;

import com.idega.business.IBOLookup;
import com.idega.core.contact.data.Email;
import com.idega.core.contact.data.Phone;
import com.idega.core.file.util.MimeTypeUtil;
import com.idega.core.location.data.Address;
import com.idega.data.IDOLookup;
import com.idega.data.IDOLookupException;
import com.idega.idegaweb.IWApplicationContext;
import com.idega.idegaweb.IWResourceBundle;
import com.idega.io.DownloadWriter;
import com.idega.io.MediaWritable;
import com.idega.io.MemoryFileBuffer;
import com.idega.io.MemoryInputStream;
import com.idega.io.MemoryOutputStream;
import com.idega.presentation.IWContext;
import com.idega.user.business.NoEmailFoundException;
import com.idega.user.business.NoPhoneFoundException;
import com.idega.user.data.User;
import com.idega.util.IWTimestamp;
import com.idega.util.PersonalIDFormatter;
import com.idega.util.StringHandler;
import com.idega.util.text.Name;

public class ChurchCourseParticipantWriter extends DownloadWriter implements MediaWritable {

	private MemoryFileBuffer buffer = null;
	private CourseBusiness business;
	private CitizenBusiness userBusiness;
	private Locale locale;
	private IWResourceBundle iwrb;

	private String courseName;

	private static final String BUNDLE_IDENTIFIER = "is.idega.idegaweb.egov.church";

	
	public ChurchCourseParticipantWriter() {
	}

	private Collection getChoices(Course course, IWContext iwc) {
		try {
			return business.getCourseChoices(course, false);
		} catch (RemoteException e) {
			e.printStackTrace();
			return new ArrayList();
		}
	}

	private Collection getAllChoices(Course course, IWTimestamp fromStamp, IWTimestamp toStamp) {
		try {
			return business.getAllChoicesByCourseAndDate(course, fromStamp, toStamp);
		} catch (RemoteException e) {
			e.printStackTrace();
			return new ArrayList();
		}
	}

	@Override
	public void init(HttpServletRequest req, IWContext iwc) {
		try {
			this.locale = iwc.getApplicationSettings().getApplicationLocale();
			this.business = getCourseBusiness(iwc);
			this.userBusiness = getUserBusiness(iwc);
			this.iwrb = iwc.getIWMainApplication().getBundle(BUNDLE_IDENTIFIER).getResourceBundle(this.locale);

			if (req.getParameter(CourseBlock.PARAMETER_COURSE_PK) != null && !"".equals(req.getParameter(CourseBlock.PARAMETER_COURSE_PK))) {
				Course course = business.getCourse(iwc.getParameter(CourseBlock.PARAMETER_COURSE_PK));
				IWTimestamp date = new IWTimestamp(course.getStartDate());
				StringBuffer name = new StringBuffer(course.getProvider().getName());
				name.append(" ");
				name.append(date.getDateString("dd.MM.yyyy HH:mm"));
				courseName = name.toString();
				
				Collection choices = getChoices(course, iwc);

				this.buffer = writeXLS(iwc, choices);
				String fileName = "participants.xls";
				setAsDownload(iwc, fileName, this.buffer.length());
			} else {
				courseName = this.iwrb.getLocalizedString("all_courses", "All courses");
				
				Collection choices = getAllChoices(null, null, null);

				this.buffer = writeAllXLS(iwc, choices);
				String fileName = "participants.xls";
				setAsDownload(iwc, fileName, this.buffer.length());				
			}
		} catch(Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public String getMimeType() {
		if (this.buffer != null) {
			return this.buffer.getMimeType();
		}
		return super.getMimeType();
	}

	@Override
	public void writeTo(OutputStream out) throws IOException {
		if (this.buffer != null) {
			MemoryInputStream mis = new MemoryInputStream(this.buffer);
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			while (mis.available() > 0) {
				baos.write(mis.read());
			}
			baos.writeTo(out);
		}
		else {
			System.err.println("buffer is null");
		}
	}

	public MemoryFileBuffer writeAllXLS(IWContext iwc, Collection choices) throws Exception {
		MemoryFileBuffer buffer = new MemoryFileBuffer();
		MemoryOutputStream mos = new MemoryOutputStream(buffer);

		HSSFWorkbook wb = new HSSFWorkbook();
		HSSFSheet sheet = wb.createSheet(StringHandler.shortenToLength(this.courseName, 30));
		sheet.setColumnWidth((short) 0, (short) (14 * 256));
		sheet.setColumnWidth((short) 1, (short) (14 * 256));
		sheet.setColumnWidth((short) 2, (short) (30 * 256));
		sheet.setColumnWidth((short) 3, (short) (14 * 256));
		sheet.setColumnWidth((short) 4, (short) (30 * 256));
		sheet.setColumnWidth((short) 5, (short) (14 * 256));
		sheet.setColumnWidth((short) 6, (short) (14 * 256));
		sheet.setColumnWidth((short) 7, (short) (14 * 256));
		sheet.setColumnWidth((short) 8, (short) (14 * 256));
		sheet.setColumnWidth((short) 9, (short) (14 * 256));
		sheet.setColumnWidth((short) 10, (short) (14 * 256));
		sheet.setColumnWidth((short) 11, (short) (14 * 256));
		sheet.setColumnWidth((short) 12, (short) (14 * 256));
		sheet.setColumnWidth((short) 13, (short) (14 * 256));
		sheet.setColumnWidth((short) 14, (short) (14 * 256));
		sheet.setColumnWidth((short) 15, (short) (14 * 256));
		sheet.setColumnWidth((short) 16, (short) (14 * 256));
		sheet.setColumnWidth((short) 17, (short) (14 * 256));
		sheet.setColumnWidth((short) 18, (short) (14 * 256));
		sheet.setColumnWidth((short) 19, (short) (14 * 256));
		sheet.setColumnWidth((short) 20, (short) (14 * 256));
		sheet.setColumnWidth((short) 21, (short) (14 * 256));
		sheet.setColumnWidth((short) 22, (short) (14 * 256));
		sheet.setColumnWidth((short) 23, (short) (14 * 256));
		sheet.setColumnWidth((short) 24, (short) (14 * 256));
		sheet.setColumnWidth((short) 25, (short) (14 * 256));
		sheet.setColumnWidth((short) 26, (short) (14 * 256));
		sheet.setColumnWidth((short) 27, (short) (14 * 256));
		sheet.setColumnWidth((short) 28, (short) (14 * 256));
		sheet.setColumnWidth((short) 29, (short) (14 * 256));
		sheet.setColumnWidth((short) 30, (short) (14 * 256));
		sheet.setColumnWidth((short) 31, (short) (14 * 256));
		sheet.setColumnWidth((short) 32, (short) (14 * 256));
		HSSFFont font = wb.createFont();
		font.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);
		font.setFontHeightInPoints((short) 12);
		HSSFCellStyle style = wb.createCellStyle();
		style.setFont(font);

		HSSFFont bigFont = wb.createFont();
		bigFont.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);
		bigFont.setFontHeightInPoints((short) 13);
		HSSFCellStyle bigStyle = wb.createCellStyle();
		bigStyle.setFont(bigFont);

		int cellRow = 0;
		HSSFRow row = sheet.createRow(cellRow++);
		HSSFCell cell = row.createCell((short) 0);
		cell.setCellValue(this.courseName);
		cell.setCellStyle(bigStyle);
		
		row = sheet.createRow(cellRow++);

		
		short iCell = 0;
		row = sheet.createRow(cellRow++);
		cell = row.createCell(iCell++);
		cell.setCellValue(this.iwrb.getLocalizedString("church", "Church"));
		cell.setCellStyle(style);
		cell = row.createCell(iCell++);
		cell.setCellValue(this.iwrb.getLocalizedString("date", "Date"));
		cell.setCellStyle(style);
		cell = row.createCell(iCell++);
		cell.setCellValue(this.iwrb.getLocalizedString("name", "Name"));
		cell.setCellStyle(style);
		cell = row.createCell(iCell++);
		cell.setCellValue(this.iwrb.getLocalizedString("personal_id", "Personal ID"));
		cell.setCellStyle(style);
		cell = row.createCell(iCell++);
		cell.setCellValue(this.iwrb.getLocalizedString("address", "Address"));
		cell.setCellStyle(style);
		cell = row.createCell(iCell++);
		cell.setCellValue(this.iwrb.getLocalizedString("postal_code", "Postal code"));
		cell.setCellStyle(style);
		cell = row.createCell(iCell++);
		cell.setCellValue(this.iwrb.getLocalizedString("mobile_phone", "Mobile phone"));
		cell.setCellStyle(style);
		cell = row.createCell(iCell++);
		cell.setCellValue(this.iwrb.getLocalizedString("email", "E-mail"));
		cell.setCellStyle(style);
		cell = row.createCell(iCell++);
		cell.setCellValue(this.iwrb.getLocalizedString("gender", "Gender"));
		cell.setCellStyle(style);
		cell = row.createCell(iCell++);
		cell.setCellValue(this.iwrb.getLocalizedString("religion", "Religion"));
		cell.setCellStyle(style);
		cell = row.createCell(iCell++);
		cell.setCellValue(this.iwrb.getLocalizedString("christening_date", "Christening date"));
		cell.setCellStyle(style);
		cell = row.createCell(iCell++);
		cell.setCellValue(this.iwrb.getLocalizedString("mothers_name", "Mother's name"));
		cell.setCellStyle(style);
		cell = row.createCell(iCell++);
		cell.setCellValue(this.iwrb.getLocalizedString("fathers_name", "Father's name"));
		cell.setCellStyle(style);
		cell = row.createCell(iCell++);
		cell.setCellValue(this.iwrb.getLocalizedString("school", "School"));
		cell.setCellStyle(style);

		cell = row.createCell(iCell++);
		cell.setCellValue(this.iwrb.getLocalizedString("name", "Name"));
		cell.setCellStyle(style);
		cell = row.createCell(iCell++);
		cell.setCellValue(this.iwrb.getLocalizedString("personal_id", "Personal ID"));
		cell.setCellStyle(style);
		cell = row.createCell(iCell++);
		cell.setCellValue(this.iwrb.getLocalizedString("address", "Address"));
		cell.setCellStyle(style);
		cell = row.createCell(iCell++);
		cell.setCellValue(this.iwrb.getLocalizedString("postal_code", "Postal code"));
		cell.setCellStyle(style);
		cell = row.createCell(iCell++);
		cell.setCellValue(this.iwrb.getLocalizedString("email", "E-mail"));
		cell.setCellStyle(style);
		cell = row.createCell(iCell++);
		cell.setCellValue(this.iwrb.getLocalizedString("phone", "Phone"));
		cell.setCellStyle(style);
		cell = row.createCell(iCell++);
		cell.setCellValue(this.iwrb.getLocalizedString("mobile_phone", "Mobile phone"));
		cell.setCellStyle(style);
		cell = row.createCell(iCell++);
		cell.setCellValue(this.iwrb.getLocalizedString("work_phone", "work phone"));
		cell.setCellStyle(style);
		cell = row.createCell(iCell++);
		cell.setCellValue(this.iwrb.getLocalizedString("relation", "Relation"));
		cell.setCellStyle(style);

		cell = row.createCell(iCell++);
		cell.setCellValue(this.iwrb.getLocalizedString("name", "Name"));
		cell.setCellStyle(style);
		cell = row.createCell(iCell++);
		cell.setCellValue(this.iwrb.getLocalizedString("personal_id", "Personal ID"));
		cell.setCellStyle(style);
		cell = row.createCell(iCell++);
		cell.setCellValue(this.iwrb.getLocalizedString("address", "Address"));
		cell.setCellStyle(style);
		cell = row.createCell(iCell++);
		cell.setCellValue(this.iwrb.getLocalizedString("postal_code", "Postal code"));
		cell.setCellStyle(style);
		cell = row.createCell(iCell++);
		cell.setCellValue(this.iwrb.getLocalizedString("email", "E-mail"));
		cell.setCellStyle(style);
		cell = row.createCell(iCell++);
		cell.setCellValue(this.iwrb.getLocalizedString("phone", "Phone"));
		cell.setCellStyle(style);
		cell = row.createCell(iCell++);
		cell.setCellValue(this.iwrb.getLocalizedString("mobile_phone", "Mobile phone"));
		cell.setCellStyle(style);
		cell = row.createCell(iCell++);
		cell.setCellValue(this.iwrb.getLocalizedString("work_phone", "work phone"));
		cell.setCellStyle(style);
		cell = row.createCell(iCell++);
		cell.setCellValue(this.iwrb.getLocalizedString("relation", "Relation"));
		cell.setCellStyle(style);

		cell = row.createCell(iCell++);
		cell.setCellValue(this.iwrb.getLocalizedString("other_info", "Other info"));
		cell.setCellStyle(style);
		
		CourseChoice choice;
		Course course;

		Iterator iter = choices.iterator();
		while (iter.hasNext()) {
			row = sheet.createRow(cellRow++);
			choice = (CourseChoice) iter.next();
			course = choice.getCourse();
			
			User child = choice.getUser();
			Address childAddressDef = userBusiness.getUsersMainAddress(child);
			Phone childMobileDef = null;
			try {
				childMobileDef = userBusiness.getUsersMobilePhone(child);
			} catch (NoPhoneFoundException e2) {
			}
			Email childEmailDef = null;
			try {
				childEmailDef = userBusiness.getUsersMainEmail(child);
			} catch (NoEmailFoundException e1) {
			}
			User relative1 = choice.getApplication().getOwner();
			Address relAddress1 = userBusiness.getUsersMainAddress(relative1);
			Phone relPhone1 = null;
			try {
				relPhone1 = userBusiness.getUsersHomePhone(relative1);
			} catch (NoPhoneFoundException e2) {
			}
			Phone relMobile1 = null;
			try {
				relMobile1 = userBusiness.getUsersMobilePhone(relative1);
			} catch (NoPhoneFoundException e2) {
			}
			Phone relWorkPhone1 = null;
			try {
				relWorkPhone1 = userBusiness.getUsersWorkPhone(relative1);
			} catch (NoPhoneFoundException e2) {
			}
			Email relEmail1 = null;
			try {
				relEmail1 = userBusiness.getUsersMainEmail(relative1);
			} catch (NoEmailFoundException e1) {
			}
			
			ChurchCourseApplicationInfo extraInfo = null;
			User relative2 = null;
			try {
				extraInfo = getChurchCourseApplicationInfoHome().findByApplication(choice.getApplication());
				relative2 = extraInfo.getExtraContact();
			} catch (FinderException e) {
			}

			iCell = 0;
			IWTimestamp date = new IWTimestamp(course.getStartDate());

			row.createCell((short) iCell++).setCellValue(course.getProvider().getName());
			row.createCell((short) iCell++).setCellValue(date.getDateString("dd.MM.yyyy HH:mm"));
			
			Name name = new Name(child.getFirstName(), child.getMiddleName(), child.getLastName());
			row.createCell((short) iCell++).setCellValue(name.getName(this.locale, true));
			row.createCell((short) iCell++).setCellValue(PersonalIDFormatter.format(child.getPersonalID(), this.locale));
			row.createCell((short) iCell++).setCellValue(childAddressDef.getStreetAddress());
			row.createCell((short) iCell++).setCellValue(childAddressDef.getPostalCode().getPostalAddress());
			if (childMobileDef != null) {
				row.createCell((short) iCell++).setCellValue(childMobileDef.getNumber());
			} else {
				iCell++;
			}
			if (childEmailDef != null) {
				row.createCell((short) iCell++).setCellValue(childEmailDef.getEmailAddress());				
			} else {
				iCell++;
			}
			row.createCell((short) iCell++).setCellValue(this.iwrb.getLocalizedString(child.getGender().getName(), child.getGender().getName()));
			if (extraInfo.getReligion() != null) {
				if (extraInfo.getReligion().equals("0")) {
					row.createCell((short) iCell++).setCellValue(this.iwrb.getLocalizedString(
							"religion.national_church", "National church"));									
				} else if (extraInfo.getReligion().equals("1")) {
					row.createCell((short) iCell++).setCellValue(this.iwrb.getLocalizedString(
							"religion.other", "Other"));									
				} else {
					row.createCell((short) iCell++).setCellValue(extraInfo.getReligion());									
				}
			} else {
				iCell++;
			}
			row.createCell((short) iCell++).setCellValue(extraInfo.getChristeningDate());
			row.createCell((short) iCell++).setCellValue(extraInfo.getMothersName());			
			row.createCell((short) iCell++).setCellValue(extraInfo.getFathersName());
			if (extraInfo.getSchool() != null) {
				row.createCell((short) iCell++).setCellValue(extraInfo.getSchool().getName());
			} else {
				iCell++;
			}
			
			name = new Name(relative1.getFirstName(), relative1.getMiddleName(), relative1.getLastName());
			row.createCell((short) iCell++).setCellValue(name.getName(this.locale, true));
			row.createCell((short) iCell++).setCellValue(PersonalIDFormatter.format(relative1.getPersonalID(), this.locale));
			row.createCell((short) iCell++).setCellValue(relAddress1.getStreetAddress());
			row.createCell((short) iCell++).setCellValue(relAddress1.getPostalCode().getPostalAddress());
			row.createCell((short) iCell++).setCellValue(relEmail1.getEmailAddress());
			row.createCell((short) iCell++).setCellValue(relPhone1.getNumber());
			if (relMobile1 != null) {
				row.createCell((short) iCell++).setCellValue(relMobile1.getNumber());
			} else {
				iCell++;
			}
			if (relWorkPhone1 != null) {
				row.createCell((short) iCell++).setCellValue(relWorkPhone1.getNumber());
			} else {
				iCell++;
			}
			row.createCell((short) iCell++).setCellValue(this.iwrb.getLocalizedString("relation." + extraInfo.getContactRelation(), extraInfo.getContactRelation()));
			
			if (relative2 != null) {
				Address relAddress2 = userBusiness.getUsersMainAddress(relative2);
				Phone relPhone2 = null;
				try {
					relPhone2 = userBusiness.getUsersHomePhone(relative2);
				} catch (NoPhoneFoundException e2) {
				}
				Phone relMobile2 = null;
				try {
					relMobile2 = userBusiness.getUsersMobilePhone(relative2);
				} catch (NoPhoneFoundException e2) {
				}
				Phone relWorkPhone2 = null;
				try {
					relWorkPhone2 = userBusiness.getUsersWorkPhone(relative2);
				} catch (NoPhoneFoundException e2) {
				}
				Email relEmail2 = null;
				try {
					relEmail2 = userBusiness.getUsersMainEmail(relative2);
				} catch (NoEmailFoundException e1) {
				}

				name = new Name(relative2.getFirstName(), relative2.getMiddleName(), relative2.getLastName());
				row.createCell((short) iCell++).setCellValue(name.getName(this.locale, true));
				row.createCell((short) iCell++).setCellValue(PersonalIDFormatter.format(relative2.getPersonalID(), this.locale));
				row.createCell((short) iCell++).setCellValue(relAddress2.getStreetAddress());
				row.createCell((short) iCell++).setCellValue(relAddress2.getPostalCode().getPostalAddress());
				row.createCell((short) iCell++).setCellValue(relEmail2.getEmailAddress());
				row.createCell((short) iCell++).setCellValue(relPhone2.getNumber());
				if (relMobile2 != null) {
					row.createCell((short) iCell++).setCellValue(relMobile2.getNumber());
				} else {
					iCell++;
				}
				if (relWorkPhone2 != null) {
					row.createCell((short) iCell++).setCellValue(relWorkPhone2.getNumber());
				} else {
					iCell++;
				}
				row.createCell((short) iCell++).setCellValue(this.iwrb.getLocalizedString("relation." + extraInfo.getExtraContactRelation(), extraInfo.getExtraContactRelation()));
			} else {
				iCell += 9;
			}
			
			if (extraInfo.getInfo() != null) {
				row.createCell((short) iCell++).setCellValue(extraInfo.getInfo());				
			}
		}
		wb.write(mos);
		buffer.setMimeType(MimeTypeUtil.MIME_TYPE_EXCEL_2);
		return buffer;

	}
	
	public MemoryFileBuffer writeXLS(IWContext iwc, Collection choices) throws Exception {
		MemoryFileBuffer buffer = new MemoryFileBuffer();
		MemoryOutputStream mos = new MemoryOutputStream(buffer);

		HSSFWorkbook wb = new HSSFWorkbook();
		HSSFSheet sheet = wb.createSheet(StringHandler.shortenToLength(this.courseName, 30));
		sheet.setColumnWidth((short) 0, (short) (30 * 256));
		sheet.setColumnWidth((short) 1, (short) (14 * 256));
		sheet.setColumnWidth((short) 2, (short) (30 * 256));
		sheet.setColumnWidth((short) 3, (short) (14 * 256));
		sheet.setColumnWidth((short) 4, (short) (14 * 256));
		sheet.setColumnWidth((short) 5, (short) (14 * 256));
		sheet.setColumnWidth((short) 6, (short) (14 * 256));
		sheet.setColumnWidth((short) 7, (short) (14 * 256));
		sheet.setColumnWidth((short) 8, (short) (14 * 256));
		sheet.setColumnWidth((short) 9, (short) (14 * 256));
		sheet.setColumnWidth((short) 10, (short) (14 * 256));
		sheet.setColumnWidth((short) 11, (short) (14 * 256));
		sheet.setColumnWidth((short) 12, (short) (14 * 256));
		sheet.setColumnWidth((short) 13, (short) (14 * 256));
		sheet.setColumnWidth((short) 14, (short) (14 * 256));
		sheet.setColumnWidth((short) 15, (short) (14 * 256));
		sheet.setColumnWidth((short) 16, (short) (14 * 256));
		sheet.setColumnWidth((short) 17, (short) (14 * 256));
		sheet.setColumnWidth((short) 18, (short) (14 * 256));
		sheet.setColumnWidth((short) 19, (short) (14 * 256));
		sheet.setColumnWidth((short) 20, (short) (14 * 256));
		sheet.setColumnWidth((short) 21, (short) (14 * 256));
		sheet.setColumnWidth((short) 22, (short) (14 * 256));
		sheet.setColumnWidth((short) 23, (short) (14 * 256));
		sheet.setColumnWidth((short) 24, (short) (14 * 256));
		sheet.setColumnWidth((short) 25, (short) (14 * 256));
		sheet.setColumnWidth((short) 26, (short) (14 * 256));
		sheet.setColumnWidth((short) 27, (short) (14 * 256));
		sheet.setColumnWidth((short) 28, (short) (14 * 256));
		sheet.setColumnWidth((short) 29, (short) (14 * 256));
		sheet.setColumnWidth((short) 30, (short) (14 * 256));
		HSSFFont font = wb.createFont();
		font.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);
		font.setFontHeightInPoints((short) 12);
		HSSFCellStyle style = wb.createCellStyle();
		style.setFont(font);

		HSSFFont bigFont = wb.createFont();
		bigFont.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);
		bigFont.setFontHeightInPoints((short) 13);
		HSSFCellStyle bigStyle = wb.createCellStyle();
		bigStyle.setFont(bigFont);

		int cellRow = 0;
		HSSFRow row = sheet.createRow(cellRow++);
		HSSFCell cell = row.createCell((short) 0);
		cell.setCellValue(this.courseName);
		cell.setCellStyle(bigStyle);
		
		row = sheet.createRow(cellRow++);

		
		short iCell = 0;
		row = sheet.createRow(cellRow++);
		cell = row.createCell(iCell++);
		cell.setCellValue(this.iwrb.getLocalizedString("name", "Name"));
		cell.setCellStyle(style);
		cell = row.createCell(iCell++);
		cell.setCellValue(this.iwrb.getLocalizedString("personal_id", "Personal ID"));
		cell.setCellStyle(style);
		cell = row.createCell(iCell++);
		cell.setCellValue(this.iwrb.getLocalizedString("address", "Address"));
		cell.setCellStyle(style);
		cell = row.createCell(iCell++);
		cell.setCellValue(this.iwrb.getLocalizedString("postal_code", "Postal code"));
		cell.setCellStyle(style);
		cell = row.createCell(iCell++);
		cell.setCellValue(this.iwrb.getLocalizedString("mobile_phone", "Mobile phone"));
		cell.setCellStyle(style);
		cell = row.createCell(iCell++);
		cell.setCellValue(this.iwrb.getLocalizedString("email", "E-mail"));
		cell.setCellStyle(style);
		cell = row.createCell(iCell++);
		cell.setCellValue(this.iwrb.getLocalizedString("gender", "Gender"));
		cell.setCellStyle(style);
		cell = row.createCell(iCell++);
		cell.setCellValue(this.iwrb.getLocalizedString("religion", "Religion"));
		cell.setCellStyle(style);
		cell = row.createCell(iCell++);
		cell.setCellValue(this.iwrb.getLocalizedString("christening_date", "Christening date"));
		cell.setCellStyle(style);
		cell = row.createCell(iCell++);
		cell.setCellValue(this.iwrb.getLocalizedString("mothers_name", "Mother's name"));
		cell.setCellStyle(style);
		cell = row.createCell(iCell++);
		cell.setCellValue(this.iwrb.getLocalizedString("fathers_name", "Father's name"));
		cell.setCellStyle(style);
		cell = row.createCell(iCell++);
		cell.setCellValue(this.iwrb.getLocalizedString("school", "School"));
		cell.setCellStyle(style);

		cell = row.createCell(iCell++);
		cell.setCellValue(this.iwrb.getLocalizedString("name", "Name"));
		cell.setCellStyle(style);
		cell = row.createCell(iCell++);
		cell.setCellValue(this.iwrb.getLocalizedString("personal_id", "Personal ID"));
		cell.setCellStyle(style);
		cell = row.createCell(iCell++);
		cell.setCellValue(this.iwrb.getLocalizedString("address", "Address"));
		cell.setCellStyle(style);
		cell = row.createCell(iCell++);
		cell.setCellValue(this.iwrb.getLocalizedString("postal_code", "Postal code"));
		cell.setCellStyle(style);
		cell = row.createCell(iCell++);
		cell.setCellValue(this.iwrb.getLocalizedString("email", "E-mail"));
		cell.setCellStyle(style);
		cell = row.createCell(iCell++);
		cell.setCellValue(this.iwrb.getLocalizedString("phone", "Phone"));
		cell.setCellStyle(style);
		cell = row.createCell(iCell++);
		cell.setCellValue(this.iwrb.getLocalizedString("mobile_phone", "Mobile phone"));
		cell.setCellStyle(style);
		cell = row.createCell(iCell++);
		cell.setCellValue(this.iwrb.getLocalizedString("work_phone", "work phone"));
		cell.setCellStyle(style);
		cell = row.createCell(iCell++);
		cell.setCellValue(this.iwrb.getLocalizedString("relation", "Relation"));
		cell.setCellStyle(style);

		cell = row.createCell(iCell++);
		cell.setCellValue(this.iwrb.getLocalizedString("name", "Name"));
		cell.setCellStyle(style);
		cell = row.createCell(iCell++);
		cell.setCellValue(this.iwrb.getLocalizedString("personal_id", "Personal ID"));
		cell.setCellStyle(style);
		cell = row.createCell(iCell++);
		cell.setCellValue(this.iwrb.getLocalizedString("address", "Address"));
		cell.setCellStyle(style);
		cell = row.createCell(iCell++);
		cell.setCellValue(this.iwrb.getLocalizedString("postal_code", "Postal code"));
		cell.setCellStyle(style);
		cell = row.createCell(iCell++);
		cell.setCellValue(this.iwrb.getLocalizedString("email", "E-mail"));
		cell.setCellStyle(style);
		cell = row.createCell(iCell++);
		cell.setCellValue(this.iwrb.getLocalizedString("phone", "Phone"));
		cell.setCellStyle(style);
		cell = row.createCell(iCell++);
		cell.setCellValue(this.iwrb.getLocalizedString("mobile_phone", "Mobile phone"));
		cell.setCellStyle(style);
		cell = row.createCell(iCell++);
		cell.setCellValue(this.iwrb.getLocalizedString("work_phone", "work phone"));
		cell.setCellStyle(style);
		cell = row.createCell(iCell++);
		cell.setCellValue(this.iwrb.getLocalizedString("relation", "Relation"));
		cell.setCellStyle(style);

		cell = row.createCell(iCell++);
		cell.setCellValue(this.iwrb.getLocalizedString("other_info", "Other info"));
		cell.setCellStyle(style);
		
		CourseChoice choice;
//		CourseApplication application;

		Iterator iter = choices.iterator();
		while (iter.hasNext()) {
			row = sheet.createRow(cellRow++);
			choice = (CourseChoice) iter.next();
//			application = choice.getApplication();

			
			User child = choice.getUser();
			Address childAddressDef = userBusiness.getUsersMainAddress(child);
			Phone childMobileDef = null;
			try {
				childMobileDef = userBusiness.getUsersMobilePhone(child);
			} catch (NoPhoneFoundException e2) {
			}
			Email childEmailDef = null;
			try {
				childEmailDef = userBusiness.getUsersMainEmail(child);
			} catch (NoEmailFoundException e1) {
			}
			User relative1 = choice.getApplication().getOwner();
			Address relAddress1 = userBusiness.getUsersMainAddress(relative1);
			Phone relPhone1 = null;
			try {
				relPhone1 = userBusiness.getUsersHomePhone(relative1);
			} catch (NoPhoneFoundException e2) {
			}
			Phone relMobile1 = null;
			try {
				relMobile1 = userBusiness.getUsersMobilePhone(relative1);
			} catch (NoPhoneFoundException e2) {
			}
			Phone relWorkPhone1 = null;
			try {
				relWorkPhone1 = userBusiness.getUsersWorkPhone(relative1);
			} catch (NoPhoneFoundException e2) {
			}
			Email relEmail1 = null;
			try {
				relEmail1 = userBusiness.getUsersMainEmail(relative1);
			} catch (NoEmailFoundException e1) {
			}
			
			ChurchCourseApplicationInfo extraInfo = null;
			User relative2 = null;
			try {
				extraInfo = getChurchCourseApplicationInfoHome().findByApplication(choice.getApplication());
				relative2 = extraInfo.getExtraContact();
			} catch (FinderException e) {
			}

			iCell = 0;
			Name name = new Name(child.getFirstName(), child.getMiddleName(), child.getLastName());
			row.createCell((short) iCell++).setCellValue(name.getName(this.locale, true));
			row.createCell((short) iCell++).setCellValue(PersonalIDFormatter.format(child.getPersonalID(), this.locale));
			row.createCell((short) iCell++).setCellValue(childAddressDef.getStreetAddress());
			row.createCell((short) iCell++).setCellValue(childAddressDef.getPostalCode().getPostalAddress());
			if (childMobileDef != null) {
				row.createCell((short) iCell++).setCellValue(childMobileDef.getNumber());
			} else {
				iCell++;
			}
			if (childEmailDef != null) {
				row.createCell((short) iCell++).setCellValue(childEmailDef.getEmailAddress());				
			} else {
				iCell++;
			}
			row.createCell((short) iCell++).setCellValue(this.iwrb.getLocalizedString(child.getGender().getName(), child.getGender().getName()));
			if (extraInfo.getReligion() != null) {
				if (extraInfo.getReligion().equals("0")) {
					row.createCell((short) iCell++).setCellValue(this.iwrb.getLocalizedString(
							"religion.national_church", "National church"));									
				} else if (extraInfo.getReligion().equals("1")) {
					row.createCell((short) iCell++).setCellValue(this.iwrb.getLocalizedString(
							"religion.other", "Other"));									
				} else {
					row.createCell((short) iCell++).setCellValue(extraInfo.getReligion());									
				}
			} else {
				iCell++;
			}
			row.createCell((short) iCell++).setCellValue(extraInfo.getChristeningDate());
			row.createCell((short) iCell++).setCellValue(extraInfo.getMothersName());			
			row.createCell((short) iCell++).setCellValue(extraInfo.getFathersName());
			if (extraInfo.getSchool() != null) {
				row.createCell((short) iCell++).setCellValue(extraInfo.getSchool().getName());
			}
			
			name = new Name(relative1.getFirstName(), relative1.getMiddleName(), relative1.getLastName());
			row.createCell((short) iCell++).setCellValue(name.getName(this.locale, true));
			row.createCell((short) iCell++).setCellValue(PersonalIDFormatter.format(relative1.getPersonalID(), this.locale));
			row.createCell((short) iCell++).setCellValue(relAddress1.getStreetAddress());
			row.createCell((short) iCell++).setCellValue(relAddress1.getPostalCode().getPostalAddress());
			row.createCell((short) iCell++).setCellValue(relEmail1.getEmailAddress());
			row.createCell((short) iCell++).setCellValue(relPhone1.getNumber());
			if (relMobile1 != null) {
				row.createCell((short) iCell++).setCellValue(relMobile1.getNumber());
			} else {
				iCell++;
			}
			if (relWorkPhone1 != null) {
				row.createCell((short) iCell++).setCellValue(relWorkPhone1.getNumber());
			} else {
				iCell++;
			}
			row.createCell((short) iCell++).setCellValue(this.iwrb.getLocalizedString(extraInfo.getContactRelation(), extraInfo.getContactRelation()));
			
			if (relative2 != null) {
				Address relAddress2 = userBusiness.getUsersMainAddress(relative2);
				Phone relPhone2 = null;
				try {
					relPhone2 = userBusiness.getUsersHomePhone(relative2);
				} catch (NoPhoneFoundException e2) {
				}
				Phone relMobile2 = null;
				try {
					relMobile2 = userBusiness.getUsersMobilePhone(relative2);
				} catch (NoPhoneFoundException e2) {
				}
				Phone relWorkPhone2 = null;
				try {
					relWorkPhone2 = userBusiness.getUsersWorkPhone(relative2);
				} catch (NoPhoneFoundException e2) {
				}
				Email relEmail2 = null;
				try {
					relEmail2 = userBusiness.getUsersMainEmail(relative2);
				} catch (NoEmailFoundException e1) {
				}

				name = new Name(relative2.getFirstName(), relative2.getMiddleName(), relative2.getLastName());
				row.createCell((short) iCell++).setCellValue(name.getName(this.locale, true));
				row.createCell((short) iCell++).setCellValue(PersonalIDFormatter.format(relative2.getPersonalID(), this.locale));
				row.createCell((short) iCell++).setCellValue(relAddress2.getStreetAddress());
				row.createCell((short) iCell++).setCellValue(relAddress2.getPostalCode().getPostalAddress());
				row.createCell((short) iCell++).setCellValue(relEmail2.getEmailAddress());
				row.createCell((short) iCell++).setCellValue(relPhone2.getNumber());
				if (relMobile2 != null) {
					row.createCell((short) iCell++).setCellValue(relMobile2.getNumber());
				} else {
					iCell++;
				}
				if (relWorkPhone2 != null) {
					row.createCell((short) iCell++).setCellValue(relWorkPhone2.getNumber());
				} else {
					iCell++;
				}
				row.createCell((short) iCell++).setCellValue(this.iwrb.getLocalizedString(extraInfo.getExtraContactRelation(), extraInfo.getExtraContactRelation()));
			} else {
				iCell += 9;
			}
			
			if (extraInfo.getInfo() != null) {
				row.createCell((short) iCell++).setCellValue(extraInfo.getInfo());				
			}
		}
		wb.write(mos);
		buffer.setMimeType(MimeTypeUtil.MIME_TYPE_EXCEL_2);
		return buffer;
	}

	
	
	private CourseBusiness getCourseBusiness(IWApplicationContext iwc) throws RemoteException {
		return (CourseBusiness) IBOLookup.getServiceInstance(iwc, CourseBusiness.class);
	}

	private CitizenBusiness getUserBusiness(IWApplicationContext iwc) throws RemoteException {
		return (CitizenBusiness) IBOLookup.getServiceInstance(iwc, CitizenBusiness.class);
	}
	
	private ChurchCourseApplicationInfoHome getChurchCourseApplicationInfoHome() throws IDOLookupException {
		return (ChurchCourseApplicationInfoHome) IDOLookup.getHome(ChurchCourseApplicationInfo.class);
	}
}
