package models.genericgroup;

import java.util.List;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;

@Entity
public class OrganizeGroup extends AbstractGroup {

	public String colorLogo;

	public String whiteLogo;

	public Boolean isFreeze = false;// 是否冻结

	public Boolean isPlat = false;// 是否是平台

	public static OrganizeGroup addGroup(String groupName, String colorLogo, String whiteLogo) {
		OrganizeGroup group = new OrganizeGroup();
		group.groupName = groupName;
		group.colorLogo = colorLogo;
		group.whiteLogo = whiteLogo;
		group.save();
		group.rootGroup = group;
		OrganizeGroup last = OrganizeGroup.find("isDeleted = 0 and parentGroup = null order by indexOrder desc")
				.first();
		group.indexOrder = last == null ? 0 : last.indexOrder;
		group.indexOrder++;
		return group.save();
	}

	public void edit(String groupName, String colorLogo, String whiteLogo) {
		this.groupName = groupName;
		this.colorLogo = colorLogo;
		this.whiteLogo = whiteLogo;
		this.save();
	}

	public void setPlat() {
		this.isPlat = true;
		this.save();
	}

	public static List<OrganizeGroup> list(String search) {
		search = search == null ? "" : search;
		return OrganizeGroup.find("isDeleted = 0 and groupName like ? order by indexOrder", "%" + search + "%").fetch();
	}

	public static List<OrganizeGroup> listRoot(String search, int page, int pageSize) {
		search = search == null ? "" : search;
		return OrganizeGroup
				.find("isDeleted = 0 and parentGroup = null and isPlat = 0 and groupName like ? order by indexOrder",
						"%" + search + "%")
				.fetch(page, pageSize);
	}

	public static Long count(String search) {
		search = search == null ? "" : search;
		return OrganizeGroup.count("isDeleted = 0 and groupName like ? ", "%" + search + "%");
	}

	public static Long countRoot(String search) {
		search = search == null ? "" : search;
		return OrganizeGroup.count("isDeleted = 0 and groupName like ? ", "%" + search + "%");
	}

	public static List<OrganizeGroup> fetchAll() {
		return OrganizeGroup
				.find("select org from OrganizeGroup org where org.isDeleted = false  order by createTime desc ")
				.fetch();
	}

	public static List<OrganizeGroup> fetchAllJoinOrg() {
		return OrganizeGroup
				.find("select org from OrganizeGroup org where org.isDeleted = false and org.isPlat = false  order by createTime desc ")
				.fetch();
	}

	public void updateFreeze(boolean isFreeze) {
		this.isFreeze = isFreeze;
		this.save();
	}

	public static List<OrganizeGroup> listRoot() {
		return OrganizeGroup.find("isDeleted = 0 and parentGroup = null").fetch();
	}

	public static OrganizeGroup fetchPlat() {
		return find(getDefaultContitionSql(" isPlat = true ")).first();
	}

	public static OrganizeGroup fetchByName(String groupName) {
		return find(getDefaultContitionSql(" groupName = ? "), groupName).first();
	}

}
