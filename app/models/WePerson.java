package models;

import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang.StringUtils;

import japa.parser.ast.expr.ThisExpr;

@Entity
public class WePerson extends BaseModel {

	public String number;

	public String fullName;

	public String email;

	public String password;

	public String cellPhone;

	public String avatarURL;

	public String idNum;// 身份证

	public Boolean sex;// 0:男生 1:女生

	public String job;// 职位

	@Enumerated(EnumType.STRING)
	public Role role;

	public enum Role {

	}

	public void editRole(Role role) {
		this.role = role;
		this.save();
	}

	public WePerson setPassword(String password) {
		this.password = DigestUtils.md5Hex(password);
		return this.save();
	}

	public void setPhone(String phone) {
		this.cellPhone = phone;
		this.save();
	}

	public void updateInfo(String number, String fullName, String cellPhone, String job) {
		this.number = number;
		this.fullName = fullName;
		this.cellPhone = cellPhone;
		this.job = job;
		this.save();
	}

	public static WePerson createPerson(String email, String password, String job) {
		WePerson person = new WePerson();
		person.email = email;
		person.password = DigestUtils.md5Hex(password);
		person.job = job;
		person.save();
		return person;
	}

	public static WePerson regist(WePerson person) {
		person.password = DigestUtils.md5Hex("111111");
		person.save();
		return person;
	}

	public static WePerson findPerson(String email, String password) {
		return WePerson.find(getDefaultContitionSql("email=? and password=?"), email, DigestUtils.md5Hex(password))
				.first();
	}

	public static WePerson findByEmail(String email) {
		return WePerson.find(getDefaultContitionSql("email=?"), email).first();
	}

	public static WePerson findByNumber(String number) {
		return WePerson.find(getDefaultContitionSql("number=?"), number).first();
	}

	public static WePerson findByJob(String job) {
		return WePerson.find(getDefaultContitionSql(" job = ? "), job).first();
	}

	public static boolean checkEmailAvailable(String email) {
		return WePerson.count("email=? and isDeleted=false", email) == 0l;
	}

	public static List<WePerson> fetchPersons() {
		return WePerson.find("select wp from WePerson wp").fetch();
	}

	public static boolean isPhoneAvailable(String cellPhone) {
		return StringUtils.isNotBlank(cellPhone)
				&& cellPhone.matches("^((13[0-9]|15[012356789]|17[678]|18[0-9]|14[57]))\\d{8}$")
				&& WePerson.count(getDefaultContitionSql("cellPhone=?"), cellPhone) == 0;
	}

	public static boolean isEmailAvailable(String emailAddress) {
		return emailAddress.matches("[a-zA-Z0-9._%-]+@[a-zA-Z0-9]+(.[a-zA-Z]{2,4}){1,4}")
				&& WePerson.count(getDefaultContitionSql("lower(email)=lower(?)"), emailAddress) == 0;
	}

	public static List<WePerson> fetchPersonsByIds(List<Long> personIds) {
		return WePerson.find("select wp from WePerson wp where wp.id in (:personIds) ")
				.bind("personIds", personIds.toArray()).fetch();
	}

}
