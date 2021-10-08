package io.commitlint;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;
import org.apache.commons.io.IOUtils;

public class InternalUtils {

    private InternalUtils() {
    }

    public static List<LintRule> getLintRules() {
        try {
            InputStream is = InternalUtils.class.getClassLoader().getResourceAsStream(DefaultConstants.RULES_FILE);
            String json = IOUtils.toString(is, StandardCharsets.UTF_8.name());
            return new Gson().fromJson(json, new TypeToken<List<LintRule>>(){}.getType());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
