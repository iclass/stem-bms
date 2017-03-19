package models.genericgroup;

import java.util.List;
import javax.persistence.Entity;

/**
 * 年级
 * 
 * @author daikai
 *
 */

@Entity
public class GenericGroup extends AbstractGroup {

	public static GenericGroup addGroup(String groupName, AbstractGroup parentGroup) {
		GenericGroup group = new GenericGroup();
		group.groupName = groupName;
		group.parentGroup = parentGroup;
		group.rootGroup = (parentGroup == null ? null : parentGroup.rootGroup);
		group.indexOrder = countByParent(parentGroup).intValue();
		return group.save();
	}

	// 获取机构下的所有年级
	public static List<GenericGroup> fetchGenericGroups(AbstractGroup rootGroup) {
		return GenericGroup.find(getDefaultContitionSql(" parentGroup = ? and rootGroup = ? "), rootGroup, rootGroup)
				.fetch();
	}

}
