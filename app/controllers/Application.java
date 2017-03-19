package controllers;

import static org.junit.Assert.fail;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutionException;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.RandomStringUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.poi.EncryptedDocumentException;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;

import com.ning.http.client.AsyncHttpClient;
import com.ning.http.client.Request;
import com.ning.http.client.RequestBuilder;

import bran.auditreports.QuarterlyMRScoreReport;
import bran.auditreports.models.OncologyWorkbook;
import bran.auditreports.models.WorkbookException;
import bran.auditreports.models.WorkbookFormatException;
import cn.bran.play.JapidController;
import cn.bran.play.JapidPlayAdapter;
import japidviews._javatags.CommonUtils;
import jobs.ImportGroupMember;
import models.WePerson;
import models.genericgroup.AbstractGroup;
import models.genericgroup.Clazz;
import models.genericgroup.GenericGroup;
import models.genericgroup.GenericGroupVO;
import models.genericgroup.GroupMember;
import models.genericgroup.OrganizeGroup;
import models.json.JsonData;
import models.json.JsonError;
import models.json.JsonJump;
import models.json.JsonSucceed;
import models.json.ResultVO;
import models.json.URLResult;
import models.stem.Task;
import models.stem.TaskPerson;
import models.stem.TaskPersonLog;
import play.Logger;
import play.mvc.Before;
import play.mvc.Scope.Session;
import utils.FileUtil;

public class Application extends JapidController {

	private static final String LOGGED_PERSON_ID = "loggedPersonId";

	@Before(unless = { "loginPage", "login" })
	private static void checkLogin() {
		if (CommonUtils.currentPerson() == null) {
			loginPage();
		}

	}

	public static void loginPage() {
		renderJapid();
	}

	// 登录
	public static void login(String userName, String password) {
		WePerson person = WePerson.findByEmail(userName);
		if (person == null) {
			renderJSON(new JsonError("账号不存在！"));
		} else if (person.password.equals(DigestUtils.md5Hex(password))) {
			Session.current().put(LOGGED_PERSON_ID, person.id);
			response.setCookie("KEEP_LOGIN_ID", person.id + "", "1h");
			String url = session.get("jump-url");
			session.remove("jump-url");
			if (url == null) {
				renderJSON(new JsonSucceed(JapidPlayAdapter.lookupAbs("Application.index")));
			} else {
				renderJSON(new JsonJump(url));
			}

		} else {
			renderJSON(new JsonError("密码不正确"));
		}
	}

	// 登出
	public static void logout() {
		CommonUtils.savePersonToSession(null);
		response.removeCookie("KEEP_LOGIN_ID");
		Session.current().remove("jump-url");
		loginPage();
	}

	public static void resetPassword(String pass, String newPass) {
		WePerson person = CommonUtils.currentPerson();
		if (person != null) {
			if (person.password.equals(DigestUtils.md5Hex(pass))) {
				person.setPasswordWithDigest(newPass);
				renderJSON(new JsonSucceed());
			} else {
				renderJSON(new JsonError("您输入的密码不正确"));
			}
		}
		renderJSON(new JsonError("请您先登陆！"));
	}

	public static void index() {
		renderJapid();
	}

	// 人员管理
	public static void memberManage(Long groupId) {
		OrganizeGroup rootGroup = OrganizeGroup.findById(groupId);
		GenericGroupVO groupVO = GenericGroupVO.buildGroupTreeByGenericGroup(rootGroup);
		renderJapid(rootGroup, groupVO);
	}

	/**
	 * 组织成员管理
	 * 
	 */
	public static void invokeGroupMemberManage(Long groupId, String tbType) {
		AbstractGroup group = AbstractGroup.findById(groupId);
		List<GroupMember> groupMembers = new ArrayList<GroupMember>();
		if (group.isOrganizeGroup()) {
			List<Long> childGroupIds = group.fetchAllChildrenGroupIds();
			groupMembers = GroupMember.fetchByGroupIds(childGroupIds);
		} else if (group.isGenericiGroup()) {
			if (group.parentGroup.id == 1l) {// stem组特殊处理
				groupMembers = GroupMember.fetchByGroup(group);
			} else {
				List<Long> childGroupIds = group.fetchChidrenGroupIds();
				groupMembers = GroupMember.fetchByGroupIds(childGroupIds);
			}
		} else {
			groupMembers = GroupMember.fetchByGroup(group);
		}
		renderJapid(group, groupMembers, tbType);
	}

	public static void switchGenericGroup(Long groupId, String tbType) {
		renderJapidWith("@jsonGroupMember", groupId, tbType);
	}

	public static void updateGroupName(Long groupId, String groupName) {
		AbstractGroup group = AbstractGroup.findById(groupId);
		group.updateName(groupName);
		renderJSON(new JsonSucceed());
	}

	// 新增二级节点
	public static void addGenericGroup(String groupName, Long parentGroupId) {
		AbstractGroup parentGroup = AbstractGroup.findById(parentGroupId);
		if (!parentGroup.isOrganizeGroup()) {
			renderJSON(new JsonError("请选中根节点后再添加！"));
		}

		GenericGroup group = GenericGroup.addGroup(groupName, parentGroup);
		renderJSON(new JsonData(group.id + ""));
	}

	// 新增三级节点
	public static void addClazzGroup(String groupName, Long parentGroupId) {
		AbstractGroup parentGroup = AbstractGroup.findById(parentGroupId);
		if (!parentGroup.isGenericiGroup())
			renderJSON(new JsonError("请选中二级节点后再添加！"));

		Clazz clazz = Clazz.addClazz(groupName, parentGroup);
		renderJSON(new JsonData(clazz.id + ""));

	}

	public static void upGroup(Long groupId) {
		AbstractGroup group = AbstractGroup.findById(groupId);
		if (group.findLast() == null)
			renderJSON(new JsonError("组织前面无同级组织，不能上移"));
		group.up();
		renderJSON(new JsonSucceed());
	}

	public static void downGroup(Long groupId) {
		AbstractGroup group = AbstractGroup.findById(groupId);
		if (group.findNext() == null)
			renderJSON(new JsonError("组织后面无同级组织，不能下移"));
		group.down();
		renderJSON(new JsonSucceed());
	}

	public static void delGroup(Long groupId) {
		AbstractGroup group = AbstractGroup.findById(groupId);
		if (group.isOrganizeGroup()) {
			renderJSON(new JsonError("初始组织，不能删除"));
		}
		group.del();
		renderJSON(new JsonSucceed());
	}

	public static void addGroupMember(Long groupId, WePerson person) {
		AbstractGroup group = AbstractGroup.findById(groupId);
		if (!WePerson.isPhoneAvailable(person.cellPhone)) {
			renderJSON(new JsonError("手机号重复！"));
		}

		/*
		 * if (!WePerson.isEmailAvailable(person.email)) { renderJSON(new
		 * JsonError("邮箱格式不对或已被注册！")); }
		 */

		WePerson member = WePerson.regist(person);
		if (member == null) {
			renderJSON(new JsonError("邮箱已被注册！"));
		}
		GroupMember.createAssociation(group, member);
		renderJSON(new JsonSucceed());
	}

	public static void deleteGroupMembers(Long groupId, Long[] memberIds) {
		AbstractGroup group = AbstractGroup.findById(groupId);
		List<WePerson> persons = WePerson.fetchPersonsByIds(Arrays.asList(memberIds));
		for (WePerson person : persons) {
			person.logicDelete();
		}
		GroupMember.delUp2DownByGenericGroupAndMembers(group, persons);

		renderJSON(new JsonSucceed());
	}

	public static void resetPwdByMembers(Long[] memberIds, String password) {
		List<WePerson> persons = WePerson.fetchPersonsByIds(Arrays.asList(memberIds));
		for (WePerson person : persons) {
			person.setPasswordWithDigest(password);
		}
		renderJSON(new JsonSucceed());
	}

	public static void addGroupMembers(Long groupId, Long[] memberIds) {
		AbstractGroup group = AbstractGroup.findById(groupId);
		List<WePerson> persons = WePerson.fetchPersonsByIds(Arrays.asList(memberIds));
		GroupMember.createAssociations(group, persons);
		renderJSON(new JsonSucceed());
	}

	public static void downloadTemplate(String templateName) {
		String path = System.getProperty("user.dir") + "/document/template/" + templateName;
		renderBinary(new File(path), templateName);
	}

	// 导入成员
	public static void importGroupMember(Long groupId, File file) throws Exception {
		AbstractGroup group = AbstractGroup.findById(groupId);
		String fileName = System.getProperty("user.dir") + "/tmp/" + RandomStringUtils.randomNumeric(8)
				+ file.getName();
		File newfile = new File(fileName);
		FileUtils.copyFile(file, newfile);
		ImportGroupMember importJob = new ImportGroupMember(newfile, group);
		importJob.doJob();
		String message = "";
		if (StringUtils.isNotBlank(importJob.repeatEamil + "")) {
			message += importJob.repeatEamil + "等邮箱已被注册，请修改后重新引入";
		}
		if (StringUtils.isNotBlank(importJob.repeatCellPhone + "")) {
			message += importJob.repeatCellPhone + "手机号已被使用，请修改后重新引入";
		}
		renderJSON(ResultVO.succeed(message));
	}

	// *******************任务分配管理**************************

	// 新建任务弹框
	public static void jsonAddTask() {
		renderJapid();
	}

	// 创建任务
	public static void addTask(Long groupId, String name) {
		AbstractGroup group = AbstractGroup.findById(groupId);
		Task.add(group, name);
		renderJSON(ResultVO.succeed());
	}

	// 点击左侧任务
	public static void jsonTask(Long taskId, String orderType, String sortType) {
		Task task = Task.findById(taskId);
		WePerson curPerson = CommonUtils.currentPerson();
		if (StringUtils.isBlank(orderType)) {
			orderType = "tp.repMember.number";
		}
		if (StringUtils.isBlank(sortType)) {
			sortType = "asc";
		}
		List<TaskPerson> tps = TaskPerson.fetchByCondition(task, curPerson, orderType, sortType, 1, Integer.MAX_VALUE);
		renderJapid(task, tps, curPerson, orderType, sortType);
	}

	// 点击右侧tab页
	public static void jsonTaskTab(Long taskId, String type, String orderType, String sortType) {
		Task task = Task.findById(taskId);
		List<TaskPerson> tps = null;
		if ("tab1".equals(type)) {
			WePerson curPerson = CommonUtils.currentPerson();
			if (StringUtils.isBlank(orderType)) {
				orderType = "tp.repMember.number";
			}
			if (StringUtils.isBlank(sortType)) {
				sortType = "asc";
			}
			tps = TaskPerson.fetchByCondition(task, curPerson, orderType, sortType, 1, Integer.MAX_VALUE);
		}
		renderJapid(task, type, tps, orderType, sortType);
	}

	// 任务分配人员弹框
	public static void jsonTaskPerson(String type) {
		renderJapid(type);
	}

	// 分配人员切换区域
	public static void jsonChooseDept(Long groupId, String type) {
		AbstractGroup group = AbstractGroup.findById(groupId);
		List<WePerson> persons = GroupMember.fetchByGroupAndJob(group, type);
		renderJapid(persons);
	}

	// 关联经理
	public static void relateManager(Long tpId, Long personId) {
		TaskPerson tp = TaskPerson.findById(tpId);
		WePerson manager = WePerson.findById(personId);
		tp.setManager(manager);
		renderJSON(ResultVO.succeed());
	}

	// 关联顾问
	public static void relateAdviser(Long tpId, Long personId) {
		TaskPerson tp = TaskPerson.findById(tpId);
		WePerson adviser = WePerson.findById(personId);
		tp.setAdviser(adviser);
		renderJSON(ResultVO.succeed());
	}

	// 上传访谈记录
	public static void uploadInterviewRecord(Long tpId, String url, String name) {
		TaskPerson tp = TaskPerson.findById(tpId);
		tp.setExcelUrl(url, name);
		renderJSON(ResultVO.succeed());
	}

	// 上传公式表格
	public static void uploadFormula(Long tpId, String url, String name) {
		TaskPerson tp = TaskPerson.findById(tpId);
		tp.setFormulaUrl(url, name);
		renderJSON(ResultVO.succeed());
	}

	// 上传评估报告
	public static void uploadEvaluateRecord(Long tpId, String url, String name) {
		TaskPerson tp = TaskPerson.findById(tpId);
		tp.setPdfUrl(url, name);
		renderJSON(ResultVO.succeed());
	}

	public static void evaluteRecord(Long tpId, boolean state) {
		TaskPerson tp = TaskPerson.findById(tpId);
		tp.setTalkResult(state);
		renderJSON(ResultVO.succeed());
	}

	public static void evaluteReport(Long tpId, boolean state) throws Exception {
		WePerson person = CommonUtils.currentPerson();
		if (!person.job.equals("总监")) {
			renderJSON(ResultVO.failed());
		}
		TaskPerson tp = TaskPerson.findById(tpId);
		tp.setCheckResult(state);
		renderJSON(ResultVO.succeed());
	}

	public static void uploadResource(File file)
			throws EncryptedDocumentException, InvalidFormatException, IOException {
		String pathname = file.getAbsolutePath().replaceAll(" ", "_");
		File newFile = new File(pathname);
		file.renameTo(newFile);

		if (newFile.getName().endsWith(".xlsx")) {// 如果上传文件是excel则进行格式检查
			try {
				OncologyWorkbook wb = new OncologyWorkbook(file);
			} catch (WorkbookException e) {
				renderJSON(new ResultVO().failed(e.getMessage()));
			}
		}

		URLResult result = FileUtil.uploadResource(newFile);
		result.name = file.getName();
		DecimalFormat df = new DecimalFormat("#0");
		result.size = df.format(file.length() / 1024);
		FileUtil.deleteFile(newFile);
		renderJSON(new ResultVO().succeed(result));
	}

	// 合并总表
	public static void MergeFile(Long tpId, boolean state) throws Exception {
		TaskPerson tp = TaskPerson.findById(tpId);
		File tmpFile = new File("/tmp/" + tp.formulaName);
		FileUtils.copyURLToFile(new URL(tp.formulaUrl), tmpFile);

		// 这个地方需要catch exception， 如果是#WorkbookException, 需要显示给用户
		if (state) {// 新增
			try {
				QuarterlyMRScoreReport.insert(tmpFile);
			} catch (WorkbookException e) {
				renderJSON(new ResultVO().failed(e.getMessage()));
			}
		} else {
			QuarterlyMRScoreReport.deleteById(String.valueOf(tp.repMember.number));
		}

		File outFile = new File("/tmp/out.xlsx");
		QuarterlyMRScoreReport.dumpTo(outFile);
		// 上传
		URLResult result = FileUtil.uploadResource(outFile);
		tp.task.updateTotal(result.html);
		renderJSON(new ResultVO().succeed());
	}

	// 刷新总表
	// TODO: 这里需要catch WorkbookException
	public static void batchMergeFiles(Long taskId) throws Exception {
		Task task = Task.findById(taskId);
		List<TaskPerson> tps = TaskPerson.fetchByPassEvaluate(task);
		List<File> tempFiles = new ArrayList<>();
		QuarterlyMRScoreReport.clear();
		tps.parallelStream().forEach(tp -> {
			File tmpFile = new File("/tmp/compose/" + tp.formulaName);
			try {
				FileUtils.copyURLToFile(new URL(tp.formulaUrl), tmpFile);
				tempFiles.add(tmpFile);
			} catch (Exception e) {
				e.printStackTrace();
				Logger.error(e.getMessage());
			}
		});
		if (!tempFiles.isEmpty()) {
			try {
				QuarterlyMRScoreReport.insert(tempFiles);
				File outFile = new File("/tmp/out.xlsx");
				QuarterlyMRScoreReport.dumpTo(outFile);
				// 上传
				URLResult result = FileUtil.uploadResource(outFile);
				task.updateTotal(result.html);
				renderJSON(new ResultVO().succeed());
			} catch (Exception e) {
				renderJSON(new ResultVO().failed(e.getMessage()));
			}
		}

	}

	public static void download(String fileUrl, String fileName) {
		AsyncHttpClient asyncHttpClient = new AsyncHttpClient();
		if (fileUrl == null || fileName == null) {
			notFound("您所访问的资源已被删除或不存在。");
		}
		if (!fileUrl.startsWith("http:")) {
			return;
		}
		Request request = new RequestBuilder("GET").setUrl(fileUrl).setFollowRedirects(true).build();
		try {
			renderBinary(asyncHttpClient.executeRequest(request).get().getResponseBodyAsStream(), fileName);
		} catch (IOException e) {
			Logger.error(e, e.getMessage());
		} catch (InterruptedException e) {
			Logger.error(e, e.getMessage());
		} catch (ExecutionException e) {
			Logger.error(e, e.getMessage());
		} finally {
			if (!asyncHttpClient.isClosed()) {
				asyncHttpClient.close();
			}
		}
		notFound("您所访问的资源已被删除或不存在。");
	}

	// 日志页面
	public static void logPage(Long tpId, String type) {
		TaskPerson tp = TaskPerson.findById(tpId);
		renderJapid(tp, type);
	}

	// 增加日志
	public static void addLog(Long tpId, String content, String type) {
		TaskPerson tp = TaskPerson.findById(tpId);
		TaskPersonLog.add(tp, content, type);
		renderJSON(ResultVO.succeed());
	}

	public static void updatePersonInfo(Long personId, String number, String fullName, String cellPhone, String job) {
		WePerson p = WePerson.findByNumber(number);
		if (p != null && !p.id.equals(personId)) {
			renderJSON(new JsonError("工号已被使用！"));
		}
		WePerson person = WePerson.findById(personId);

		person.updateInfo(number, fullName, cellPhone, job);
		renderJSON(new JsonSucceed());
	}
}