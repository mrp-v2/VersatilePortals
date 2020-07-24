package mrp_v2.randomdimensions.random.parse;

import java.util.Random;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;

public class RandomElementFromOptions {

	private final JsonArray options;

	public RandomElementFromOptions(JsonArray options) {
		this.options = options;
	}

	public JsonElement get(Random rand) {
		return this.options.get(rand.nextInt(this.options.size()));
	}
}
