package mrp_v2.randomdimensions.random.parse;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map.Entry;
import java.util.Random;
import java.util.function.BiFunction;
import java.util.function.Function;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSyntaxException;

public class JsonExpander {

	public static JsonArray expand(JsonArray array, JsonObject localTransformers, JsonObject globalTransformers,
			Random rand) {
		JsonArray newArray = new JsonArray();
		for (JsonElement element : array) {
			newArray.add(JsonExpander.expand(element, localTransformers, globalTransformers, rand));
		}
		return newArray;
	}

	public static JsonObject expand(JsonObject object, JsonObject localTransformers, JsonObject globalTransformers,
			Random rand) {
		JsonObject newObject = new JsonObject();
		for (Entry<String, JsonElement> entry : object.entrySet()) {
			newObject.add(entry.getKey(),
					JsonExpander.expand(entry.getValue(), localTransformers, globalTransformers, rand));
		}
		return newObject;
	}

	public static JsonElement expand(JsonPrimitive primitive, JsonObject localTransformers,
			JsonObject globalTransformers, Random rand) {
		if (primitive.isString()) {
			String str = primitive.getAsString();
			if (str.startsWith("#")) {
				str = str.substring(1);
				if (localTransformers.has(str)) {
					return localTransformers.get(str);
				}
				if (globalTransformers.has(str)) {
					return globalTransformers.get(str);
				}
				if (CODE_TRANSFORMERS.containsKey(str)) {
					return CODE_TRANSFORMERS.get(str).apply(rand);
				}
				DynamicCodeTransformer dct = JsonExpander.getDynamicCodeTransformer(str);
				if (dct != null) {
					return dct.apply(str, rand);
				}
			}
		}
		return primitive;
	}

	public static JsonElement expand(JsonElement element, JsonObject localTransformers, JsonObject globalTransformers,
			Random rand) {
		if (element.isJsonArray()) {
			return JsonExpander.expand((JsonArray) element, localTransformers, globalTransformers, rand);
		}
		if (element.isJsonObject()) {
			return JsonExpander.expand((JsonObject) element, localTransformers, globalTransformers, rand);
		}
		if (element.isJsonPrimitive()) {
			return JsonExpander.expand(element, localTransformers, globalTransformers, rand);
		}
		return element;
	}

	public static final HashMap<String, Function<Random, JsonElement>> CODE_TRANSFORMERS;

	static {
		CODE_TRANSFORMERS = new HashMap<String, Function<Random, JsonElement>>();
		CODE_TRANSFORMERS.put("rand_int", JsonExpander::randomInt);
	}

	public static JsonPrimitive randomInt(Random rand) {
		return new JsonPrimitive(rand.nextInt());
	}

	public static DynamicCodeTransformer getDynamicCodeTransformer(String key) {
		for (DynamicCodeTransformer dct : DYNAMIC_CODE_TRANSFORMERS) {
			if (key.startsWith(dct.stem)) {
				return dct;
			}
		}
		return null;
	}

	public static final HashSet<DynamicCodeTransformer> DYNAMIC_CODE_TRANSFORMERS;

	static {
		DYNAMIC_CODE_TRANSFORMERS = new HashSet<DynamicCodeTransformer>();
		DYNAMIC_CODE_TRANSFORMERS.add(new DynamicCodeTransformer("rand_int_", JsonExpander::rangedRandomInt));
	}

	private static class DynamicCodeTransformer {
		public final String stem;
		private final BiFunction<String, Random, JsonElement> function;

		public DynamicCodeTransformer(String stem, BiFunction<String, Random, JsonElement> function) {
			this.stem = stem;
			this.function = function;
		}

		public JsonElement apply(String key, Random rand) {
			return this.function.apply(key.substring(this.stem.length()), rand);
		}
	}

	public static JsonElement rangedRandomInt(String key, Random rand) {
		String[] minMax = key.split("_");
		if (minMax.length != 2) {
			throw new JsonSyntaxException("Expected a minimum and a maximum, but got " + minMax.length + " values!");
		}
		int min = minMax[0].equals("nomin") ? Integer.MIN_VALUE : Integer.parseInt(minMax[0]);
		int max = minMax[1].equals("nomax") ? Integer.MAX_VALUE : Integer.parseInt(minMax[1]);
		double percent = rand.nextDouble();
		int selected = (int) Math.round(percent * (max - min) + min);
		return new JsonPrimitive(selected);
	}
}
