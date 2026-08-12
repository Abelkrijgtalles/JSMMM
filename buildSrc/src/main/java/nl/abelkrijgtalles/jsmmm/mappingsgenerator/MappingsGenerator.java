package nl.abelkrijgtalles.jsmmm.mappingsgenerator;

import net.fabricmc.mappingio.MappedElementKind;
import net.fabricmc.mappingio.MappingWriter;
import net.fabricmc.mappingio.format.MappingFormat;
import net.fabricmc.mappingio.tree.MemoryMappingTree;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

public class MappingsGenerator {
    private final static Map<String, MappingType> MAPPING_STRING_TO_TYPE = Map.of("classes", MappingType.CLASS, "fields", MappingType.FIELD, "methods", MappingType.METHOD);

    public static void generateMappings(Path mapsFilePath, Path outputPath) throws IOException {
        Scanner scanner = new Scanner(System.in);
        MemoryMappingTree tree = new MemoryMappingTree();

        tree.visitHeader();
        System.out.println("Using " + mapsFilePath.toAbsolutePath() + " as maps file.");
        List<String> mapsFile = Files.readAllLines(mapsFilePath);

        String[] namespaces = mapsFile.getFirst().split(",");
        System.out.println("Found the following namespaces: " + Arrays.toString(namespaces) + ".");
        System.out.println(namespaces[0] + " will be used as reference/source namespace.");

        List<String> targetNamespaces = Arrays.stream(namespaces).toList().subList(1, namespaces.length);
        tree.visitNamespaces(namespaces[0], targetNamespaces);
        tree.visitContent();

        int namespaceLineSize = mapsFile.indexOf(namespaces[1] + ":") - 1;
        System.out.println("Number of things to be remapped: " + (namespaceLineSize - 4) + ".");

        if (namespaceLineSize * namespaces.length + 1 != mapsFile.size()) {
            throw new IllegalArgumentException("Amount of lines doesn't add up. Expected: " + (namespaceLineSize * namespaces.length + 1));
        }

        for (int i = 0; i < targetNamespaces.size(); i++) {
            String namespace = namespaces[i + 1];
            MappingType mappingType = null;
            for (int j = 0; j < namespaceLineSize; j++) {
                String line = mapsFile.get(namespaceLineSize * (i + 1) + j + 1).trim();
                if (j == 0) {
                    if (!line.equals(namespace + ":")) {
                        throw new IllegalArgumentException("Expected: " + namespace + ": got: " + line);
                    }
                    continue;
                }

                if (MAPPING_STRING_TO_TYPE.containsKey(line.replace(":", ""))) {
                    mappingType = MAPPING_STRING_TO_TYPE.get(line.replace(":", ""));
                    continue;
                }

                switch (mappingType) {
                    case CLASS -> handleClasses(tree, mapsFile, line, j, i);
                    case FIELD -> handleFields(tree, mapsFile, line, j, i);
                    case METHOD -> handleMethods(tree, mapsFile, line, j, i);
                    case null, default -> {
                        System.out.println("Ignoring this line: " + line);
                        continue;
                    }
                }
            }
        }

        tree.visitEnd();

        outputPath.toFile().getParentFile().mkdirs();
        MappingWriter writer = MappingWriter.create(outputPath, MappingFormat.TINY_2_FILE);
        tree.accept(writer);

        System.out.println("Wrote mappings to " + outputPath.toFile().getAbsolutePath() + ".");
    }

    private static void handleClasses(MemoryMappingTree tree, List<String> lines, String line, int lineIndex, int namespaceIndex) throws IOException {
        String srcClass = lines.get(lineIndex + 1).trim().replaceFirst("import ", "").replace(";", "").replace(".", "/");
        String destClass = line.replaceFirst("import ", "").replace(";", "").replace(".", "/");

        tree.visitClass(srcClass);
        tree.visitDstName(MappedElementKind.CLASS, namespaceIndex, destClass);
        tree.visitElementContent(MappedElementKind.CLASS);
    }

    private static void handleFields(MemoryMappingTree tree, List<String> lines, String line, int lineIndex, int namespaceIndex) throws IOException {
        String srcClass = lines.get(lineIndex + 1).trim().split(" ")[1].split("#")[0].replace(".", "/");
        String srcField = lines.get(lineIndex + 1).trim().split(" ")[1].split("#")[1];
        String destField = line.split("#")[1];

        String fieldDescriptor = getFieldDescriptor(lines.get(lineIndex + 1).trim().split(" ")[0]);

        tree.visitClass(srcClass);
        tree.visitField(srcField, fieldDescriptor);
        tree.visitDstName(MappedElementKind.FIELD, namespaceIndex, destField);
        tree.visitElementContent(MappedElementKind.FIELD);

    }

    private static void handleMethods(MemoryMappingTree tree, List<String> lines, String line, int lineIndex, int namespaceIndex) throws IOException {
        String[] sourceLineSplit = lines.get(lineIndex + 1).trim().split(" ");
        String srcClass = sourceLineSplit[1].split("#")[0].replace(".", "/");
        String srcMethod = sourceLineSplit[1].split("#")[1];
        String destMethod = line.split("#")[1];
        List<String> parametersRaw = new ArrayList<>();
        if (sourceLineSplit.length > 2) {
            parametersRaw = Arrays.stream(sourceLineSplit).toList().subList(2, sourceLineSplit.length);
        }

        String methodDescriptor = getMethodDescriptor(sourceLineSplit[0], parametersRaw);

        tree.visitClass(srcClass);
        tree.visitMethod(srcMethod, methodDescriptor);
        tree.visitDstName(MappedElementKind.METHOD, namespaceIndex, destMethod);
        tree.visitElementContent(MappedElementKind.METHOD);
    }

    private static String getFieldDescriptor(String descriptorRaw) {
        return switch (descriptorRaw) {
            case "byte" -> "B";
            case "char" -> "C";
            case "double" -> "D";
            case "float" -> "F";
            case "int" -> "I";
            case "long" -> "J";
            case "short" -> "S";
            case "boolean" -> "Z";
            case "void" -> "V";
            default -> "L" + descriptorRaw.replace(".", "/") + ";";
        };
    }

    private static String getMethodDescriptor(String descriptorRaw, SequencedCollection<String> parametersRaw) {
        StringBuilder parameters = new StringBuilder();
        for (String parameter : parametersRaw) {
            parameters.append(getFieldDescriptor(parameter));
        }
        return "(" + parameters + ")" + getFieldDescriptor(descriptorRaw);
    }
}
