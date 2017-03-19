package models.stem;

import java.util.List;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;

import models.BaseModel;
import models.WePerson;

/**
 * XXX setter 的写法都有问题， 不能用save。
 * 
 * @author bran
 *
 */
@Entity
public class TaskPerson extends BaseModel {

	@ManyToOne
	public WePerson repMember;

	@ManyToOne
	public Task task;

	@ManyToOne
	public WePerson manager;

	@ManyToOne
	public WePerson adviser;

	public String interviewUrl;// excel地址

	public String excelName;

	public Long createExcelTime;

	public String formulaUrl;// 公式表格

	public String formulaName;

	public String evaluateUrl;// pdf地址

	public String pdfName;

	public Long createPdfTime;

	public Boolean talkResult;

	public Boolean checkResult;

	public static TaskPerson add(WePerson repMember, Task task) {
		TaskPerson tp = new TaskPerson();
		tp.repMember = repMember;
		tp.task = task;
		return tp.save();
	}

	public void setManager(WePerson manager) {
		this.manager = manager;
		this.save();
	}

	public void setAdviser(WePerson adviser) {
		this.adviser = adviser;
		this.save();
	}

	public void setExcelUrl(String url, String name) {
		this.interviewUrl = url;
		this.excelName = name;
		this.createExcelTime = System.currentTimeMillis();
		this.talkResult = null;
		this.save();
	}

	public void setFormulaUrl(String url, String name) {
		this.formulaUrl = url;
		this.formulaName = name;
		this.save();
	}

	public void setPdfUrl(String url, String name) {
		this.evaluateUrl = url;
		this.pdfName = name;
		this.createPdfTime = System.currentTimeMillis();
		this.checkResult = null;
		this.save();
	}

	public void setTalkResult(boolean state) {
		this.talkResult = state;
		this.save();
	}

	public void setCheckResult(boolean state) {
		this.checkResult = state;
		this.save();
	}

	public static List<TaskPerson> fetchByTask(Task task) {
		return find(getDefaultContitionSql(" task = ? "), task).fetch();
	}

	public static List<TaskPerson> fetchByPerson(Task task, WePerson person) {
		if (person.job.equals("总监")) {
			return fetchByTask(task);
		} else if (person.job.equals("经理")) {
			return find(getDefaultContitionSql(" task = ? and manager = ?"), task, person).fetch();
		} else if (person.job.equals("顾问")) {
			return find(getDefaultContitionSql(" task = ? and adviser = ?"), task, person).fetch();
		} else {
			return null;
		}

	}

	public static List<TaskPerson> fetchByCondition(Task task, WePerson person, String orderType, String sortType,
			int page, int pageSize) {
		StringBuffer sb = new StringBuffer();
		sb.append(" select tp from TaskPerson tp where tp.isDeleted = 0  and " + " tp.task.id = ").append(task.id);

		if (person.job.equals("经理")) {
			sb.append(" and tp.manager.id = ").append(person.id);
		}

		if (person.job.equals("顾问")) {
			sb.append(" and tp.adviser.id = ").append(person.id);
		}

		sb.append(" order by ").append(orderType).append(" ").append(sortType);
		return TaskPerson.find(sb.toString()).fetch(page, pageSize);

	}

	public static List<TaskPerson> fetchByInterview(Task task) {
		return find(getDefaultContitionSql(" task = ? and interviewUrl <> null order by createExcelTime desc"), task)
				.fetch();
	}

	public static List<TaskPerson> fetchByEvaluate(Task task) {
		return find(getDefaultContitionSql(" task = ? and evaluateUrl <> null order by createPdfTime desc"), task)
				.fetch();
	}

	public static List<TaskPerson> fetchByPassEvaluate(Task task) {
		return find(getDefaultContitionSql(
				" task = ? and formulaUrl <> null and checkResult = true order by createPdfTime desc"), task).fetch();
	}

}
