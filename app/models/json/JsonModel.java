package models.json;

import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.annotate.JsonSerialize.Inclusion;
import org.codehaus.jackson.map.ser.CustomSerializerFactory;

public class JsonModel {
	protected static final ObjectMapper mapper = new ObjectMapper();
	static {

		// 使Jackson JSON支持Unicode编码非ASCII字符
		CustomSerializerFactory serializerFactory = new CustomSerializerFactory();
		serializerFactory.addSpecificMapping(String.class,
				new StringUnicodeSerializer());
		mapper.setSerializerFactory(serializerFactory);
		mapper.getSerializationConfig().setSerializationInclusion(
				Inclusion.NON_NULL);
	}
}
