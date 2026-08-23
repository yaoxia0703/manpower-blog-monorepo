package com.manpowergroup.blog.tools.codegen;

import com.baomidou.mybatisplus.generator.FastAutoGenerator;
import com.baomidou.mybatisplus.generator.config.rules.DateType;
import com.baomidou.mybatisplus.generator.engine.FreemarkerTemplateEngine;

import java.nio.file.Files;
import java.nio.file.Path;

/**
 * MyBatis-Plus Entity generator.
 *
 * <p>Arguments: {@code <module-path> <table-name> <entity-name>}</p>
 */
public final class EntityCodeGenerator {

    private static final String MODULE_PREFIX = "blog-module-";
    private static final String BASE_PACKAGE = "com.manpowergroup.blog.module.";

    private EntityCodeGenerator() {
    }

    public static void main(String[] args) throws Exception {
        if (args == null || args.length != 3) {
            throw new IllegalArgumentException(
                    "Usage: EntityCodeGenerator <module-path> <table-name> <entity-name>"
            );
        }

        Path modulePath = Path.of(args[0]).toAbsolutePath().normalize();
        validateModulePath(modulePath);

        String moduleName = modulePath.getFileName().toString().substring(MODULE_PREFIX.length());
        String basePackage = BASE_PACKAGE + moduleName.replace('-', '.');
        String tableName = requireText(args[1], "Table name");
        String entityName = requireText(args[2], "Entity name");
        validateEntityName(entityName);

        Path javaOutput = modulePath.resolve("src/main/java");
        Files.createDirectories(javaOutput);

        FastAutoGenerator.create(
                        envOr("DB_URL", "jdbc:mysql://localhost:3306/blog_db"
                                + "?useUnicode=true&characterEncoding=utf8&serverTimezone=Asia/Tokyo"),
                        envOr("DB_USER", "root"),
                        envOr("DB_PWD", "Yx19900703.")
                )
                .globalConfig(builder -> builder
                        .author("YAOXIA")
                        .outputDir(javaOutput.toString())
                        .dateType(DateType.TIME_PACK)
                        .disableOpenDir()
                )
                .packageConfig(builder -> builder
                        .parent(basePackage)
                        .entity("entity")
                )
                .strategyConfig(builder -> builder
                        .addInclude(tableName)
                        .entityBuilder()
                        .formatFileName(entityName)
                        .enableLombok()
                        .disableSerialVersionUID()
                        .mapperBuilder()
                        .disable()
                        .serviceBuilder()
                        .disable()
                        .controllerBuilder()
                        .disable()
                )
                .templateEngine(new FreemarkerTemplateEngine())
                .execute();
    }

    private static void validateModulePath(Path modulePath) {
        if (!Files.isDirectory(modulePath)) {
            throw new IllegalArgumentException("Module path does not exist: " + modulePath);
        }
        if (!modulePath.getFileName().toString().startsWith(MODULE_PREFIX)) {
            throw new IllegalArgumentException(
                    "Module directory must start with '" + MODULE_PREFIX + "': " + modulePath
            );
        }
        if (!Files.isRegularFile(modulePath.resolve("pom.xml"))) {
            throw new IllegalArgumentException("Module pom.xml does not exist: " + modulePath);
        }
    }

    private static String requireText(String value, String fieldName) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(fieldName + " is required");
        }
        return value.trim();
    }

    private static void validateEntityName(String entityName) {
        if (!entityName.matches("[A-Z][A-Za-z0-9]*")) {
            throw new IllegalArgumentException(
                    "Entity name must be an UpperCamelCase Java class name: " + entityName
            );
        }
    }

    private static String envOr(String key, String defaultValue) {
        String value = System.getenv(key);
        return value == null || value.isBlank() ? defaultValue : value;
    }
}
