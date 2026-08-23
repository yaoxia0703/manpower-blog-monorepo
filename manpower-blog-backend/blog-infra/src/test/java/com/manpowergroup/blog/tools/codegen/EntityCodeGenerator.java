package com.manpowergroup.blog.tools.codegen;

import com.baomidou.mybatisplus.generator.FastAutoGenerator;
import com.baomidou.mybatisplus.generator.config.TemplateType;
import com.baomidou.mybatisplus.generator.config.rules.DateType;
import com.baomidou.mybatisplus.generator.engine.FreemarkerTemplateEngine;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;

/**
 * MyBatis-Plus Entity generator.
 *
 * <p>Arguments: {@code <module-path> <table-name> [table-name...]}</p>
 */
public final class EntityCodeGenerator {

    private static final String MODULE_PREFIX = "blog-module-";
    private static final String BASE_PACKAGE = "com.manpowergroup.blog.module.";

    private EntityCodeGenerator() {
    }

    public static void main(String[] args) throws Exception {
        if (args == null || args.length < 2) {
            throw new IllegalArgumentException(
                    "Usage: EntityCodeGenerator <module-path> <table-name> [table-name...]"
            );
        }

        Path modulePath = Path.of(args[0]).toAbsolutePath().normalize();
        validateModulePath(modulePath);

        String moduleName = modulePath.getFileName().toString().substring(MODULE_PREFIX.length());
        String basePackage = BASE_PACKAGE + moduleName.replace('-', '.');
        List<String> tableNames = Arrays.stream(args, 1, args.length)
                .map(String::trim)
                .filter(tableName -> !tableName.isEmpty())
                .toList();
        if (tableNames.isEmpty()) {
            throw new IllegalArgumentException("At least one table name is required");
        }

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
                        .addInclude(tableNames)
                        .addTablePrefix("t_" + moduleName.replace('-', '_') + "_")
                        .entityBuilder()
                        .enableLombok()
                        .disableSerialVersionUID()
                )
                .templateConfig(builder -> builder.disable(
                        TemplateType.MAPPER,
                        TemplateType.XML,
                        TemplateType.SERVICE,
                        TemplateType.SERVICE_IMPL,
                        TemplateType.CONTROLLER
                ))
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

    private static String envOr(String key, String defaultValue) {
        String value = System.getenv(key);
        return value == null || value.isBlank() ? defaultValue : value;
    }
}
