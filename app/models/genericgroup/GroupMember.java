package models.genericgroup;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.TreeSet;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;

import play.db.jpa.GenericModel.JPAQuery;
import models.BaseModel;
import models.WePerson;
import models.WePerson.Role;
import models.json.JsonModel;

/**
 * 机构成员
 * 
 * @author daikai
 *
 */
@Entity
public class GroupMember extends BaseModel {
	@ManyToOne
	public WePerson person;
	@ManyToOne
	public AbstractGroup abstractGroup;

	// json对象
	public static class JsonGroupMember extends JsonModel {
		public Long personId;
		public String personName;

		public JsonGroupMember(GroupMember gm) {
			this.personId = gm.person.id;
			this.personName = gm.person.fullName;
		}
	}

	public static GroupMember createAssociation(AbstractGroup group, WePerson person) {
		GroupMember gm = findByGroupAndPerson(group, person);
		if (gm == null) {
			gm = new GroupMember();
			gm.abstractGroup = group;
			gm.person = person;
			gm.save();
		}
		return gm;
	}

	public static void createAssociations(AbstractGroup group, List<WePerson> persons) {
		for (WePerson person : persons) {
			createAssociation(group, person);
		}
	}

	public static GroupMember findByGroupAndPerson(AbstractGroup group, WePerson person) {
		String hql = getDefaultContitionSql("select gm from GroupMember gm where gm.abstractGroup=? and gm.person = ?");
		return GroupMember.find(hql, group, person).first();
	}

	// 精确到机构
	public static GroupMember findByOrgAndPerson(OrganizeGroup group, WePerson person) {
		String hql = getDefaultContitionSql(
				"select gm from GroupMember gm where gm.abstractGroup.rootGroup=? and gm.person = ?");
		return GroupMember.find(hql, group, person).first();
	}

	public static List<WePerson> getAllMembersByGroup(AbstractGroup group) {
		String hql = getDefaultContitionSql("select gm.person from GroupMember gm where gm.abstractGroup=? ");
		return GroupMember.find(hql, group).fetch();
	}

	public static List<GroupMember> fetchByGroup(AbstractGroup group) {
		String hql = getDefaultContitionSql("select gm from GroupMember gm where gm.abstractGroup=? ");
		return GroupMember.find(hql, group).fetch();
	}

	public static List<GroupMember> fetchByGroupIds(List<Long> groupIds) {
		String hql = getDefaultContitionSql("select gm from GroupMember gm where gm.abstractGroup.id in(:groupIds) ");
		return GroupMember.find(hql).bind("groupIds", groupIds.toArray()).fetch();
	}

	public static List<WePerson> fetchREPByGroupIds(List<Long> groupIds) {
		String hql = getDefaultContitionSql(
				"select gm.person from GroupMember gm where gm.abstractGroup.id in(:groupIds) and gm.person.job = 'Rep' ");
		return GroupMember.find(hql).bind("groupIds", groupIds.toArray()).fetch();
	}

	public static List<WePerson> fetchByGroupAndJob(AbstractGroup group, String job) {
		String hql = getDefaultContitionSql(
				"select gm.person from GroupMember gm where gm.person.job = ?  and gm.abstractGroup= ?  ");
		return GroupMember.find(hql, job, group).fetch();
	}

	public static List<WePerson> fetchByGroupIdsAndJob(List<Long> groupIds, String job) {
		String hql = getDefaultContitionSql(
				"select gm.person from GroupMember gm where gm.person.job = ?  and gm.abstractGroup.id in(:groupIds) ");
		return GroupMember.find(hql, job).bind("groupIds", groupIds.toArray()).fetch();
	}

	public static long count(long groupId) {
		String hql = "select count(gm.id) from GroupMember gm where gm.abstractGroup.id=? and gm.isDeleted=false and gm.abstractGroup.isDeleted=false";
		return GroupMember.count(hql, groupId);
	}

	public static AbstractGroup fetchOrganizeByMember(WePerson person) {
		return GroupMember
				.find(" select gm.abstractGroup from GroupMember gm where  gm.abstractGroup.class='OrganizeGroup' and gm.person=?",
						person)
				.first();
	}

	public static List<Long> findByPersonId(List<Long> personIds) {
		if (personIds.isEmpty()) {
			return Collections.EMPTY_LIST;
		}
		TreeSet<Long> personId = new TreeSet<>(personIds);
		List<Long> groupId = GroupMember
				.find(" select gm.abstractGroup.id from GroupMember gm where gm.person.id in(:personIds) and gm.abstractGroup.class='Clazz' and gm.isDeleted = false")
				.bind("personIds", personId.toArray()).fetch();
		return groupId;
	}

	public static Map<Long, String> findClazzByPersonId(List<Long> personIds) {
		if (personIds.isEmpty()) {
			return Collections.EMPTY_MAP;
		}
		TreeSet<Long> personId = new TreeSet<>(personIds);
		List<Object[]> groupList = GroupMember
				.find(" select gm.abstractGroup.id ,gm.abstractGroup.groupName from GroupMember gm where gm.person.id in(:personIds) and gm.abstractGroup.class='Clazz' and gm.isDeleted = false")
				.bind("personIds", personId.toArray()).fetch();
		Map<Long, String> groupMap = new TreeMap<>();
		for (Object[] obj : groupList) {
			groupMap.put((Long) obj[0], (String) obj[1]);
		}
		return groupMap;
	}

	public static List<Long> findByPersonId(Long personId) {
		List<Long> groupId = GroupMember
				.find(" select gm.abstractGroup.id from GroupMember gm where gm.person.id = ? and gm.abstractGroup.class='Clazz' and gm.isDeleted = false",
						personId)
				.fetch();
		return groupId;
	}

	public static List<Long> getPersonIds(List<Long> groupId) {
		if (groupId.isEmpty()) {
			return Collections.EMPTY_LIST;
		}
		List<Long> personIds = GroupMember
				.find("select g.person.id from GroupMember g where g.person.class='Student' and  g.abstractGroup.id in(:groupId) and g.isDeleted = false"
						+ " and g.person.isDeleted=false and g.abstractGroup.isDeleted = false")
				.bind("groupId", groupId.toArray()).fetch();
		return personIds;
	}

	public static List<WePerson> getRoleMembersByGroup(AbstractGroup group, Role role) {
		String hql = getDefaultContitionSql(
				"select gm.person from GroupMember gm where gm.abstractGroup=? and gm.person.role = ? ");
		return GroupMember.find(hql, group, role).fetch();
	}

	public static AbstractGroup getGroupByAdminRole(WePerson person, Role role) {
		String hql = getDefaultContitionSql(
				"select gm.abstractGroup from GroupMember gm where gm.person=? and gm.person.role = ? ");
		return GroupMember.find(hql, person, role).first();
	}

	/**
	 * 查询多个用户和多个组织的关联
	 * 
	 */
	public static List<GroupMember> fetchByGenericGroupsAndMembers(List<AbstractGroup> genericGroupList,
			List<WePerson> memberList) {
		JPAQuery query = GroupMember.find(
				"select gm from GroupMember gm where gm.isDeleted=false and gm.abstractGroup in(:genericGroups) and gm.person in(:members) ");
		query.bind("genericGroups", genericGroupList.toArray());
		query.bind("members", memberList.toArray());
		return query.fetch();
	}

	/**
	 * 删除组织内所有关联，并未自上而下删除，组织本身会自上而下删除
	 * 
	 */
	public static void delByGenericGroup(AbstractGroup abstractGroup) {
		for (GroupMember genericGroupMember : GroupMember.fetchByGroup(abstractGroup)) {
			genericGroupMember.logicDelete();
		}
	}

	/**
	 * 自上而下删除批量用户关联
	 * 
	 */
	public static void delUp2DownByGenericGroupAndMembers(AbstractGroup genericGroup, List<WePerson> memberList) {
		List<AbstractGroup> genericGroupList = genericGroup.fetchUpToDown();
		List<GroupMember> genericGroupMemberList = fetchByGenericGroupsAndMembers(genericGroupList, memberList);
		for (GroupMember genericGroupMember : genericGroupMemberList) {
			genericGroupMember.logicDelete();
		}

	}
}
