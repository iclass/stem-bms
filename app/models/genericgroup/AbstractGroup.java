package models.genericgroup;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.persistence.Cacheable;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import models.BaseModel;

@Entity
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
@Cacheable
public abstract class AbstractGroup extends BaseModel {

	public String groupName;

	@ManyToOne
	public OrganizeGroup rootGroup;

	@ManyToOne
	public AbstractGroup parentGroup;// 上级组织

	public int indexOrder;// 显示顺序

	public <T extends AbstractGroup> T toSubClass() {
		return (T) this;
	}

	public static AbstractGroup getGroupById(long id) {
		return AbstractGroup.find(getDefaultContitionSql("id=?"), id).first();
	}

	public static Long countByParent(AbstractGroup parentGroup) {
		return AbstractGroup.count(getDefaultContitionSql(" parentGroup = ? "), parentGroup);
	}

	/**
	 * 根据ids查询
	 * 
	 */
	public static List<AbstractGroup> fetchByIDs(List<Long> genericGroupIdList) {
		if (genericGroupIdList.isEmpty()) {
			return Collections.EMPTY_LIST;
		}
		return GenericGroup.find("select gg from AbstractGroup gg where gg.isDeleted=false and gg.id in(:groupIds)")
				.bind("groupIds", genericGroupIdList.toArray()).fetch();
	}

	/**
	 * 查询一个节点的根节点
	 *
	 */
	public AbstractGroup findRootGroup() {
		if (this.rootGroup != null) {
			return this.rootGroup;
		}
		return this;
	}

	/**
	 * 查询根节点树下的所有子节点
	 * 
	 */
	public List<AbstractGroup> fetchAllChildrenGroup() {
		List<AbstractGroup> list = GenericGroup
				.find("select gg from AbstractGroup gg where gg.isDeleted=false and gg.rootGroup=?", this).fetch();
		list.add(this);
		return list;

	}

	public List<Long> fetchAllChildrenGroupIds() {
		List<Long> list = GenericGroup
				.find("select gg.id from AbstractGroup gg where gg.isDeleted=false and gg.rootGroup=?", this).fetch();
		return list;

	}

	public List<Long> fetchChidrenGroupIds() {
		List<Long> list = GenericGroup
				.find("select gg.id from AbstractGroup gg where gg.isDeleted=false and gg.parentGroup=?", this).fetch();
		return list;

	}

	/**
	 * 查询所有子节点
	 * 
	 */
	public List<AbstractGroup> fetchChild() {
		return AbstractGroup
				.find("select gg from AbstractGroup gg where gg.isDeleted=false and gg.parentGroup = ? order by gg.indexOrder",
						this)
				.fetch();
	}

	public boolean isFirst() {
		if (this.findLast() == null)
			return true;
		else
			return false;
	}

	public boolean isLast() {
		if (this.findNext() == null)
			return true;
		else
			return false;
	}

	/**
	 * 上移节点
	 * 
	 */
	public void up() {
		AbstractGroup lastGenericGroup = this.findLast();
		lastGenericGroup.setIndexOrder(++lastGenericGroup.indexOrder);
		this.setIndexOrder(--this.indexOrder);
	}

	/**
	 * 下移节点
	 * 
	 */
	public void down() {
		AbstractGroup lastGenericGroup = this.findNext();
		lastGenericGroup.setIndexOrder(--lastGenericGroup.indexOrder);
		this.setIndexOrder(++this.indexOrder);
	}

	public void setIndexOrder(int indexOrder) {
		this.indexOrder = indexOrder;
		this.save();
	}

	/**
	 * 查询同级中上一个节点
	 * 
	 */
	public AbstractGroup findLast() {
		if (this.parentGroup != null)
			return AbstractGroup
					.find("select gg from AbstractGroup gg where gg.isDeleted=false and gg.parentGroup = ? and gg.indexOrder=?",
							this.parentGroup, this.indexOrder - 1)
					.first();
		else
			return AbstractGroup
					.find("select gg from AbstractGroup gg where gg.isDeleted=false and gg.parentGroup is null and gg.indexOrder=?",
							this.indexOrder - 1)
					.first();
	}

	/**
	 * 查询同级中下一个节点
	 * 
	 */
	public AbstractGroup findNext() {
		if (this.parentGroup != null)
			return AbstractGroup
					.find("select gg from AbstractGroup gg where gg.isDeleted=false and gg.parentGroup = ? and gg.indexOrder=?",
							this.parentGroup, this.indexOrder + 1)
					.first();
		else
			return AbstractGroup
					.find("select gg from AbstractGroup gg where gg.isDeleted=false and gg.parentGroup is null and gg.indexOrder=?",
							this.indexOrder + 1)
					.first();
	}

	/**
	 * 当本节点增加或减少缩进以及删除时，上移每个在自己后面的兄弟节点
	 * 
	 */
	public void upTheDown() {
		List<AbstractGroup> genericGroupList = this.parentGroup.fetchChild();
		for (AbstractGroup abstractGroup : genericGroupList) {
			if (abstractGroup.indexOrder > this.indexOrder) {
				abstractGroup.setIndexOrder(--abstractGroup.indexOrder);
			}
		}
	}

	/**
	 * 查询某节点及其子节点
	 * 
	 */
	public List<AbstractGroup> fetchUpToDown() {
		OrganizeGroup rootGroup = (OrganizeGroup) this.findRootGroup();
		GenericGroupVO genericGroupVOs = GenericGroupVO.buildGroupTreeByGenericGroup(rootGroup);
		List<Long> idList = new ArrayList<Long>();
		getGenericGroupIds(genericGroupVOs, this, idList);
		return GenericGroup.fetchByIDs(idList);

	}

	/*
	 * 递归查询
	 */
	public static void getGenericGroupIds(GenericGroupVO genericGroupVOs, AbstractGroup genericGroup,
			List<Long> idList) {
		for (GenericGroupVO genericGroupVO : genericGroupVOs.childGenericGroupVOList) {
			if (genericGroupVO.id == genericGroup.id) {
				idList.add(genericGroupVO.id);
				addIdtoList(genericGroupVO, idList);
				break;
			} else {
				getGenericGroupIds(genericGroupVO, genericGroup, idList);
			}

		}

	}

	/*
	 * 递归添加
	 */
	public static void addIdtoList(GenericGroupVO genericGroupVO, List<Long> idList) {
		for (GenericGroupVO groupVO : genericGroupVO.childGenericGroupVOList) {
			idList.add(groupVO.id);
			addIdtoList(groupVO, idList);
		}
	}

	/**
	 * 删除组织
	 * 
	 */
	public void del() {
		List<AbstractGroup> childGenericGroupList = this.fetchChild();
		for (AbstractGroup abstractGroup : childGenericGroupList) {
			abstractGroup.del(); // 孩子组织删除
		}
		// 删除组织内成员关系
		GroupMember.delByGenericGroup(this);
		// 先上移在自己后面的兄弟节点
		this.upTheDown();
		// 再删除自己
		this.isDeleted = true;
		this.save();
	}

	public void updateName(String groupName) {
		this.groupName = groupName;
		this.save();
	}

	public boolean isOrganizeGroup() {
		return this instanceof OrganizeGroup;
	}

	public boolean isGenericiGroup() {
		return this instanceof GenericGroup;
	}

	public boolean isClazzGroup() {
		return this instanceof Clazz;
	}

}
