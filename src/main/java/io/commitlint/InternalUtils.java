package io.commitlint;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class InternalUtils {

    private InternalUtils() {
    }

    public static List<LintRule> getLintRules() {
        try (InputStream is = InternalUtils.class.getClassLoader().getResourceAsStream(DefaultConstants.RULES_FILE);) {
            String json = read(is);
            return new Gson().fromJson(json, new TypeToken<List<LintRule>>() {
            }.getType());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static String read(InputStream is) {
        StringBuilder sb = new StringBuilder();
        try (BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8));) {
            while (true) {
                String line = bufferedReader.readLine();
                if (line == null) {
                    break;
                }
                sb.append(line).append("\n");
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return sb.toString();
    }
}
