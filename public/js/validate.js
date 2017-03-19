

/**
 * 是否为手机号码
 * @param tel 手机号码
 * @returns
 */
function isMobil(tel){
	var telReg = !!tel.match(/^(0|86|17951)?(13[0-9]|15[012356789]|17[678]|18[0-9]|14[57])[0-9]{8}$/);
	return telReg;
}
/**
 * 是否为邮箱格式
 * @param email 邮箱
 * @returns
 */
function isEmail(email){
	var reg = /^([a-zA-Z0-9_-])+@([a-zA-Z0-9_-])+(.[a-zA-Z0-9_-])+/;
	return reg.test(email);
} 

//判断字符是否有中文字符  
function isHasChn(obj){ 
	if(/.*[\u4e00-\u9fa5]+.*$/.test(obj)){ 
		return true; 
	} 
	return false;
} 


function isEmpty(item,errorTip){
	if(item.length==0){
		layer.msg(errorTip);
		return true;
	}
	return false;
}