package models.stem;

import java.util.List;

import javax.persistence.Entity;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;

import japidviews._javatags.CommonUtils;
import models.BaseModel;
import models.WePerson;

@Entity
public class TaskPersonLog extends BaseModel {

	@ManyToOne
	public TaskPerson tp;

	@ManyToOne
	public WePerson creator;

	@Lob
	public String content;

	public String type;// interview:访谈 evaluate 审查

	public static TaskPersonLog add(TaskPerson tp, String content, String type) {
		TaskPersonLog tpl = new TaskPersonLog();
		tpl.tp = tp;
		tpl.content = content;
		tpl.type = type;
		tpl.creator = CommonUtils.currentPerson();
		return tpl.save();
	}

	public static List<TaskPersonLog> fetchByTpAndType(TaskPerson tp, String type) {
		return find(getDefaultContitionSql(" tp = ? and type = ? "), tp, type).fetch();
	}

}
