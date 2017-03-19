package models.genericgroup;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 组织节点封装类
 * 
 */
public class GenericGroupVO {

	public long id;// id

	public long parentId;// 父节点id

	public String name;// 组织名称

	public int indexOrder;// 显示顺序

	public boolean isClazz;

	public GenericGroupVO parentGenericGroupVO;// 父节点
	public List<GenericGroupVO> childGenericGroupVOList = new ArrayList<GenericGroupVO>();// 子节点们

	public GenericGroupVO() {

	}

	public GenericGroupVO(AbstractGroup genericGroup) {
		this.id = genericGroup.id;
		this.parentId = genericGroup.parentGroup == null ? 0l
				: genericGroup.parentGroup.id;
		this.indexOrder = genericGroup.indexOrder;
		this.name = genericGroup.groupName;
		this.isClazz = genericGroup.isClazzGroup();
	}

	/**
	 * 添加子结点
	 * 
	 */
	public void addChildGroup(GenericGroupVO groupVO) {
		this.childGenericGroupVOList.add(groupVO);
	}

	/**
	 * 各级节点排序
	 * @modify by daikai
	 * 按照节点的中午名来排序
	 * 
	 */
	public void sort() {
		if (this.childGenericGroupVOList.size() != 0) {
			// 对本层节点进行排序
			Collections.sort(this.childGenericGroupVOList, new Comparator() {
				@Override
				public int compare(Object o1, Object o2) {
					return 
							((GenericGroupVO) o1).name.compareTo(((GenericGroupVO) o2).name);
				}
			});
			// 对每个节点的下一层节点进行排序
			for (GenericGroupVO cGenericGroupVO : this.childGenericGroupVOList) {
				cGenericGroupVO.sort();
			}
		}
	}

	/**
	 * 根据组织获取节点树
	 * 
	 */
	public static GenericGroupVO buildGroupTreeByGenericGroup(
			OrganizeGroup rootGroup) {
		GenericGroupVO rootGenericGroupVO = new GenericGroupVO();
		Map<Long, GenericGroupVO> treeMap = new HashMap<Long, GenericGroupVO>();
		List<AbstractGroup> genericGroupList = rootGroup
				.fetchAllChildrenGroup();
		for (AbstractGroup genericGroup : genericGroupList) {
			treeMap.put(genericGroup.id, new GenericGroupVO(genericGroup));
		}
		for (GenericGroupVO genericGroupVO : treeMap.values()) {
			long parentId = genericGroupVO.parentId;
			if (parentId == 0) {
				genericGroupVO.parentGenericGroupVO = rootGenericGroupVO;
				rootGenericGroupVO.addChildGroup(genericGroupVO);
			} else {
				genericGroupVO.parentGenericGroupVO = treeMap.get(parentId);
				treeMap.get(parentId).addChildGroup(genericGroupVO);
			}
		}
		rootGenericGroupVO.sort();
		return rootGenericGroupVO;
	}

}
