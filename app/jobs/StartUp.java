package jobs;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import javax.persistence.EntityManager;

import org.apache.commons.io.FileUtils;
import org.hibernate.Session;

import bran.auditreports.QuarterlyMRScoreReport;
import models.WePerson;
import models.WePerson.Role;
import models.genericgroup.OrganizeGroup;
import play.Play;
import play.db.jpa.JPA;
import play.jobs.Job;
import play.jobs.OnApplicationStart;

@OnApplicationStart
public class StartUp extends Job {

	public final static String MYSQL_FUNCTION = Play.applicationPath.getPath() + "/conf/mysqlFunction.txt";

	@Override
	public void doJob() throws Exception {
		// 启动时的系统操作
		super.doJob();
		createAdminAndGroups();
		initQuarterlyMRScoreReport();
	}

	public static void createAdminAndGroups() {
		Session session = (Session) JPA.em().getDelegate();
		if (!session.getTransaction().isActive()) {
			session.getTransaction().begin();
		}
		WePerson person = WePerson.findByJob("总监");
		if (person == null) {
			WePerson.createPerson("admin@iclass.com", "111111", "总监");
		}
		List<OrganizeGroup> groups = OrganizeGroup.fetchAll();
		if (groups == null || groups.size() == 0) {
			OrganizeGroup.addGroup("STEM顾问", null, null);
			OrganizeGroup.addGroup("BMS代表", null, null);
		}
		session.getTransaction().commit();
	}

	public static void initQuarterlyMRScoreReport() throws Exception {
		QuarterlyMRScoreReport.init(new File(QuarterlyMRScoreReport.FINAL_FILE_NAME));
	}

}
