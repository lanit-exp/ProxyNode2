package lanit_exp.proxy_node.helpers;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Properties;

public class FileHelper {


    public static void checkExistsAndExecutableFile(String filePath) {
        Path path = Paths.get(filePath);

        if (!Files.exists(path))
            throw new RuntimeException("Файл '%s' - не найден.".formatted(filePath));

        if (!Files.isExecutable(path))
            throw new RuntimeException("Файл '%s' - не является исполняемым.".formatted(filePath));
    }


    public static void writeStringToFile(String filePath, String data) {
        try {
            Files.writeString(Paths.get(filePath), data);
        } catch (IOException e) {
            throw new RuntimeException("Не удалось создать файл '%s': '%s'".formatted(filePath, e.getMessage()));
        }
    }


    public static Properties readPropertiesFromFile(String filePath) {
        try (Reader reader = new InputStreamReader(new FileInputStream(filePath), StandardCharsets.UTF_8)) {

            Properties properties = new Properties();
            properties.load(reader);

            return properties;

        } catch (IOException e) {
            throw new RuntimeException("Ошибка чтения файла конфигурации '%s': '%s'".formatted(filePath, e.getMessage()));
        }
    }


}
