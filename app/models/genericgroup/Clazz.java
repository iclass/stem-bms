/**
 * @author lil
 * @ data 2016年6月1日-下午3:22:55
 * Description:
 * 
 */
package models.genericgroup;

import models.WePerson;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import java.util.Collections;
import java.util.List;
import java.util.TreeSet;

/**
 * 班级
 * 
 * @author daikai
 *
 */
@Entity
public class Clazz extends AbstractGroup {

	public static Clazz addClazz(String groupName, AbstractGroup parentGroup) {
		Clazz clazz = new Clazz();
		clazz.groupName = groupName;
		clazz.parentGroup = parentGroup;
		clazz.rootGroup = (parentGroup == null ? null : parentGroup.rootGroup);
		clazz.indexOrder = countByParent(parentGroup).intValue();
		return clazz.save();
	}

	// 获取年级下的所有班级Id
	public static List<Long> getClazzByParentGroup(List<Long> gradeIds) {
		return find("select c.id from Clazz c where c.isDeleted = false and c.parentGroup.id in(:gradeIds)")
				.bind("gradeIds", gradeIds.toArray()).fetch();
	}

	public static List<Long> getClazzByParentGroup(Long gradeId) {
		return find("select c.id from Clazz c where c.isDeleted = false and c.parentGroup.id = ? ", gradeId).fetch();
	}

	public static List<Clazz> getClazzsByParentGroup(Long gradeId) {
		return find("select c from Clazz c where c.isDeleted = false and c.parentGroup.id = ? ", gradeId).fetch();
	}

}
