package jobs;

import java.io.File;

import org.apache.commons.codec.digest.DigestUtils;

import models.WePerson;
import models.genericgroup.AbstractGroup;
import models.genericgroup.GroupMember;
import play.jobs.Job;
import utils.ExcelUtil;

public class ImportGroupMember extends Job {
	public File file;
	public AbstractGroup group;
	public StringBuffer repeatCellPhone;
	public StringBuffer repeatEamil;

	public ImportGroupMember(File file, AbstractGroup group) {
		super();
		this.file = file;
		this.group = group;
		this.repeatCellPhone = new StringBuffer();
		this.repeatEamil = new StringBuffer();
	}

	@Override
	public void doJob() throws Exception {
		String[][] data = ExcelUtil.getData(file, 1).get(0);
		for (int i = 0; i < data.length; i++) {
			WePerson member = new WePerson();
			member.number = data[i][0];
			member.fullName = data[i][1];
			member.email = data[i][2];
			member.cellPhone = data[i][3];
			member.password = DigestUtils.md5Hex("111111");
			member.job = data[i][4];

			/*
			 * if (!WePerson.isPhoneAvailable(member.cellPhone)) {
			 * repeatCellPhone.append(member.cellPhone).append(","); continue; }
			 * if (!WePerson.isEmailAvailable(member.email)) {
			 * repeatEamil.append(member.email).append(","); continue; }
			 */
			WePerson person = WePerson.regist(member);
			GroupMember.createAssociation(group, person);
		}

	}
}
