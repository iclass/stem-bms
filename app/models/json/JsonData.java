package models.json;

public class JsonData extends JsonModel {
	public String data;
	public boolean resultStatus = true;

	public JsonData() {
	}

	public JsonData(String data) {
		this.data = data;
	}
}
