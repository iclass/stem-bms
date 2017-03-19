package models.json;

public class JsonError extends JsonModel {
	public String errorMsg;
	public boolean resultStatus = false;

	public JsonError() {
	}

	public JsonError(String errorMsg) {
		this.errorMsg = errorMsg;
	}

}
