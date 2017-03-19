package models.json;

public class JsonSucceed extends JsonModel {
	public boolean resultStatus = true;

	public String url;

	public JsonSucceed() {
	}

	public JsonSucceed(String url) {
		this.url = url;
	}
}
