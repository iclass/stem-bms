package models.json;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.core.Version;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;

public class JsonModel implements Serializable{
	protected static final ObjectMapper mapper = new ObjectMapper();
	static {
//		SimpleModule SerializeModule = new SimpleModule("SerializeModule", new Version(1, 0, 0, "", "", ""));
//		SerializeModule.addSerializer(new StringUnicodeSerializer());
//		mapper.registerModule(SerializeModule);

		// // 使Jackson JSON支持Unicode编码非ASCII字符
		// CustomSerializerFactory serializerFactory = new
		// CustomSerializerFactory();
		// serializerFactory.addSpecificMapping(String.class,
		// new StringUnicodeSerializer());
		// mapper.setSerializerFactory(serializerFactory);
		mapper.setSerializationInclusion(Include.NON_NULL);
	}
}
