package mrp_v2.randomdimensions.random.parse;

import com.google.gson.JsonObject;

public class ExpanderParser {

	public static JsonObject expanders(JsonObject root) {
		if (root.has("randoms")) {
			JsonObject randoms = root.getAsJsonObject("randoms");
			randoms.entrySet().forEach((entry) -> {
				switch (entry.getKey()) {
				case "generated_template_reference":
					// TODO
					break;
				case "random_from_options":
					// TODO
					break;
				}
			});
		}
		return new JsonObject();
	}
}
