package com.dantu.software.tool;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.List;

public class GenerateAst {
    public static void main(String[] args) throws IOException {
        if (args.length != 1) {
            System.err.println("Usage: generate_ast <output directory>");
            System.exit(64);
        }
        String outDir = args[0];
        defineAst(outDir, "Expr", Arrays.asList(
                "Binary : Expr left, Token operator, Expr right",
                "Grouping : Expr expression",
                "Literal: Object value",
                "Unary : Token operator, Expr right"));
    }

    private static void defineAst(String outDir, String baseName, List<String> types) throws IOException {
        String path = String.format("%s/%s.java", outDir, baseName);
        PrintWriter writer = new PrintWriter(path, "UTF-8");

        writer.println("package com.dantu.software.lox;");
        writer.println();
        writer.println("import java.util.List;");
        writer.println();
        writer.println(String.format("abstract class %s {", baseName));

        defineVisitor(writer, baseName, types);

        for (final String type : types) {
            String className = type.split(":")[0].trim();
            String fields = type.split(":")[1].trim();
            defineType(writer, baseName, className, fields);
        }

        writer.println();
        writer.println("    abstract <R> R accept(Visitor<R> visitor);");

        writer.println("}");
        writer.close();
    }

    private static void defineType(PrintWriter writer, String baseName, String className, String fieldList) {
        writer.println(String.format("    public static class %s extends %s {", className, baseName));

        // Field definitions
        String[] fields = fieldList.split(", ");
        for (final String field : fields) {
            writer.println(String.format("        final %s;", field));
        }
        writer.println();

        // Constructor
        writer.println(String.format("        %s(%s) {", className, fieldList));
        for (final String field : fields) {
            String name = field.split(" ")[1];
            writer.println(String.format("            this.%s = %s;", name, name));
        }
        writer.println("        }");

        // Visitor pattern
        writer.println();
        writer.println("        @Override");
        writer.println("        <R> R accept(Visitor<R> visitor) {");
        writer.println(String.format("            return visitor.visit%s%s(this);", className, baseName));
        writer.println("        }");
        writer.println("    }");
    }

    private static void defineVisitor(PrintWriter writer, String baseName, List<String> types) {
        writer.println("    interface Visitor<R> {");

        for (String type : types) {
            String typeName = type.split(":")[0].trim();
            writer.println(
                    String.format("        R visit%s%s(%s %s);", typeName, baseName, typeName, baseName.toLowerCase()));
        }

        writer.println("    }");
    }
}
