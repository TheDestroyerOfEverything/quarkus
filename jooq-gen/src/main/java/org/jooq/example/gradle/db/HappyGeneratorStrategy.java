package org.jooq.example.gradle.db;

import org.jooq.codegen.DefaultGeneratorStrategy;
import org.jooq.meta.Definition;
import org.jooq.tools.StringUtils;

import java.util.HashMap;
import java.util.Map;

public class HappyGeneratorStrategy extends DefaultGeneratorStrategy {

    private static final Map<String, String> singularMapping = new HashMap<>();

    static {
        singularMapping.put("st_addresses", "StAddress");
    }

    @Override
    public String getJavaIdentifier(Definition definition) {
        String outputName = definition.getInputName();

        if (outputName.endsWith("_")) {
            outputName = outputName.substring(0, outputName.length() - 1);
        }
        return outputName.toUpperCase();
    }

    @Override
    public String getJavaSetterName(Definition definition, Mode mode) {
        String outputName = super.getJavaSetterName(definition, mode);
        if (outputName.endsWith("_")) {
            outputName = outputName.substring(0, outputName.length() - 1);
        }
        return outputName;
    }

    @Override
    public String getJavaGetterName(Definition definition, Mode mode) {
        String outputName = super.getJavaGetterName(definition, mode);
        if (outputName.endsWith("_")) {
            outputName = outputName.substring(0, outputName.length() - 1);
        }
        return outputName;
    }

    @Override
    public String getJavaClassName(Definition definition, Mode mode) {
        StringBuilder result = new StringBuilder();

        result.append(StringUtils.toCamelCase(
                definition.getOutputName()
                        .replace(' ', '_')
                        .replace('-', '_')
                        .replace('.', '_')
        ));

        if (mode == Mode.RECORD) {
            if (singularMapping.containsKey(definition.getOutputName())) {
                result.setLength(0);
                result.append(singularMapping.get(definition.getOutputName()));
                result.append("Record");
            } else {
                // [#4562] Some characters should be treated like underscore
                if (result.charAt(result.length() - 1) == 's') {
                    result.deleteCharAt(result.length() - 1);
                }
                result.append("Record");
            }
        } else if (mode == Mode.DAO) {
            result.append("Dao");
        } else if (mode == Mode.INTERFACE) {
            result.insert(0, "I");
        }

        return result.toString();
    }
}