package models.stem;

import java.util.List;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;

import models.BaseModel;
import models.WePerson;
import models.genericgroup.AbstractGroup;
import models.genericgroup.GroupMember;

@Entity
public class Task extends BaseModel {

	@ManyToOne
	public AbstractGroup group;

	public String name;

	public Long updateTotalTime;// 总表更新时间

	public String updateTotalUrl;// 总表地址

	public static Task add(AbstractGroup group, String name) {
		Task task = new Task();
		task.group = group;
		task.name = name;
		task.save();
		// 同时引入REP
		List<Long> childGroupIds = group.fetchChidrenGroupIds();
		List<WePerson> repMembers = GroupMember.fetchREPByGroupIds(childGroupIds);
		for (WePerson repMember : repMembers) {
			TaskPerson.add(repMember, task);
		}
		return task;

	}

	public void updateTotal(String updateTotalUrl) {
		this.updateTotalTime = System.currentTimeMillis();
		this.updateTotalUrl = updateTotalUrl;
		this.save();
	}

	public static List<Task> fetchByGroup(AbstractGroup group) {
		return find(getDefaultContitionSql(" group = ? "), group).fetch();
	}

}
