package org.jooq.example.gradle.db;

import org.jooq.codegen.GenerationTool;
import org.jooq.meta.jaxb.*;

public class JooqGenerator {
    public static void main(String[] args) throws Exception {
        String dbUsername = "postgres";
        String dbPassword = "postgres";
        String dbName = "dair";
        String host = "localhost";
        String port = "5432";
        String destinationFolder = "../qrk-app/src/main/java";
        String destinationPackage = "jooq";
        boolean java8date = true;
        boolean includeRoutines = false;

        Generator generator = new Generator()
                .withGenerate(new Generate().withJavaTimeTypes(java8date))
                .withStrategy(new Strategy()
                        .withName("org.jooq.example.gradle.db.HappyGeneratorStrategy")
                )
                .withDatabase(new Database()
                                .withName("org.jooq.meta.postgres.PostgresDatabase")
                                .withIncludes(".*")
                                .withIncludeRoutines(includeRoutines)
                                .withExcludes("tmp_.*")
                                .withIncludeSequences(false)
                                .withSchemata(new SchemaMappingType().withInputSchema("public"))
//				.withForcedTypes(new ForcedType()
//					.withUserType(jsonType)
//					.withBinding(jsonBinding)
//					.withTypes(".*JSON.*")
//				)
                )
                .withTarget(new Target()
                        .withPackageName(destinationPackage)
                        .withDirectory(destinationFolder));

        Configuration courtConfiguration = new Configuration()
                .withJdbc(new Jdbc()
                        .withDriver("org.postgresql.Driver")
                        .withUrl("jdbc:postgresql://" + host + ":" + port + "/" + dbName)
                        .withUser(dbUsername)
                        .withPassword(dbPassword))
                .withGenerator(generator);

        GenerationTool.generate(courtConfiguration);
    }
}
