package models.json;

import java.io.IOException;
import java.util.Collection;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.ObjectMapper;

import play.Logger;

/**
 * 网络请求结果vo
 *
 * @author Yan
 *
 */
public class ResultVO {
	/**
	 * 返回状态：“true”表示成功；“false”表示失败
	 */
	public String status;

	public String data;

	/**
	 * 返回状态码
	 */
	public int statusCode;

	/**
	 * 结果
	 */

	public Collection<? extends Object> result;

	public Object oneResult;

	public static final ObjectMapper mapper = new ObjectMapper();
	static {
		mapper.setSerializationInclusion(Include.NON_NULL);
	}

	public static String failed() {
		ResultVO vo = new ResultVO();
		vo.status = "false";
		try {
			return mapper.writeValueAsString(vo);
		} catch (IOException e) {
			Logger.info("vo failed : " + e.getMessage());
			return null;
		}
	}

	public static String failed(String data) {
		ResultVO vo = new ResultVO();
		vo.status = "false";
		vo.data = data;
		try {
			return mapper.writeValueAsString(vo);
		} catch (IOException e) {
			Logger.info("vo failed : " + e.getMessage());
			return null;
		}
	}

	public static String succeed() {
		ResultVO vo = new ResultVO();
		vo.status = "true";
		try {
			return mapper.writeValueAsString(vo);
		} catch (IOException e) {
			Logger.info("vo failed : " + e.getMessage());
			return null;
		}
	}

	public static String succeed(String data) {
		ResultVO vo = new ResultVO();
		vo.status = "true";
		vo.data = data;
		try {
			return mapper.writeValueAsString(vo);
		} catch (IOException e) {
			Logger.info("vo failed : " + e.getMessage());
			return null;
		}
	}

	public static String failed(int statusCode) {
		ResultVO vo = new ResultVO();
		vo.status = "false";
		vo.statusCode = statusCode;
		try {
			return mapper.writeValueAsString(vo);
		} catch (IOException e) {
			Logger.info("vo failed : " + e.getMessage());
			return null;
		}
	}

	public static String succeed(Object result) {
		ResultVO vo = new ResultVO();
		vo.status = "true";
		vo.oneResult = result;
		try {
			return mapper.writeValueAsString(vo);
		} catch (IOException e) {
			Logger.info("vo failed : " + e.getMessage());
			return null;
		}
	}

	public static String succeed(Collection<? extends Object> result) {
		ResultVO vo = new ResultVO();
		vo.status = "true";
		vo.result = result;
		try {
			return mapper.writeValueAsString(vo);
		} catch (IOException e) {
			Logger.info("vo failed : " + e.getMessage());
			return null;
		}
	}

}
