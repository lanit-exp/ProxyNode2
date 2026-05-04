package lanit_exp.proxy_node.helpers;

import java.io.File;
import java.util.concurrent.TimeUnit;

public class ProcessHelper {

    public static void runScript(String filePath, Integer timeoutInSec) {

        ProcessBuilder pb = getProcessBuilder(filePath);

        try {
            Process process = pb.start();
            boolean finished = process.waitFor(timeoutInSec, TimeUnit.SECONDS);

            if (!finished) {
                throw new RuntimeException("Превышено время выполнения скрипта '%s'".formatted(filePath));
            }

        } catch (Exception e) {
            throw new RuntimeException("Ошибка при выполнении скрипта '%s': '%s'".formatted(filePath, e.getMessage()));
        }

    }

    //------------------------------------------------------------------------------------------------------------------

    private static ProcessBuilder getProcessBuilder(String filePath) {
        String extension = getFileExtension(filePath);

        return switch (extension) {
            case "bat" -> new ProcessBuilder("cmd.exe", "/c", filePath);
            case "ps1" -> new ProcessBuilder("powershell.exe",
                    "-ExecutionPolicy", "Bypass",
                    "-File", filePath);

            default -> throw new RuntimeException("Неподдерживаемый тип расширения скрипта '%s'. Поддерживаются только скрипты bat и ps1."
                    .formatted(extension));
        };
    }

    private static String getFileExtension(String filePath) {
        if (filePath == null) return "";

        File file = new File(filePath);
        String name = file.getName();

        if(name.isEmpty()) return "";

        int lastDotIndex = name.lastIndexOf('.');

        if (lastDotIndex <= 0 || lastDotIndex == name.length() - 1) {
            return "";
        }

        return name.substring(lastDotIndex + 1).toLowerCase();
    }

}
