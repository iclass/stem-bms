package models.json;

public class JsonJump extends JsonModel {
	public String url;
	public String msg;
	public boolean resultStatus = true;

	public JsonJump() {
	}

	public JsonJump(String url) {
		this.url = url;
	}

	public JsonJump(String msg, String url) {
		this.url = url;
		this.msg = msg;
	}
}
