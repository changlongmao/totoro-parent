import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.totoro.generator.processor.MavenPathProcessor;

import java.io.File;
import java.nio.charset.StandardCharsets;

/**
 * 根据当前项目替换生成新的项目
 *
 * @author ChangLF 2023/84
 */
@Slf4j
public class Division {

    public static void main(String[] args) {
        /*------------------------------------------ 只需修改以下内容 ------------------------------------------*/

        // 新项目名称
        final String projectName = "kuka-common";

        // 项目中文名
        final String cnModuleName = "kuka 公用系统";

        // 基础包名
        final String packageName = "com.kuka";

        // 启动类名称
        final String applicationName = "HumanApplication";

        // 生成的服务web端口号，需在zdwp-document项目中web端口分配维护
        final String serverPort = "9919";

        /*------------------------------------------ 截止 ------------------------------------------*/


        final File mavenDirectory = MavenPathProcessor.findMavenProject(Division.class).toFile();

        String diskPath = mavenDirectory.getPath().replace("totoro-parent", "" + projectName);
        if (new File(diskPath).exists()) {
            log.error("project [{}] is already exist. [{}]", projectName, diskPath);
            return;
        }

        FileUtils.iterateFiles(mavenDirectory, null, true).forEachRemaining(file -> {
            String fileName = file.getName();
            String fileExtension = FilenameUtils.getExtension(fileName);
            if ("Division.java".equals(fileName)) {
                log.info("ignore [" + file.getPath() + "]");
                return;
            }
            if (".DS_Store".equals(fileName) || "class".equals(fileExtension) || "iml".equals(fileExtension)) {
                log.info("ignore [" + file.getPath() + "]");
                return;
            }
            if (file.getPath().contains(File.separator + ".idea" + File.separator)
                    || file.getPath().contains(File.separator + "target" + File.separator)
                    || file.getPath().contains(File.separator + ".git" + File.separator)
                    || file.getPath().contains(File.separator + "logs" + File.separator)) {
                log.info("ignore [" + file.getPath() + "]");
                return;
            }

            try {
                // replace content
                String content = FileUtils.readFileToString(file, StandardCharsets.UTF_8);
                content = content.replace("totoro-parent", projectName);
                content = content.replace("org.totoro", packageName);
                content = content.replace("DivisionApplication", applicationName);
                content = content.replace("9111", serverPort);
                if ("README.md".equals(fileName)) {
                    content = String.format("# *%s*\n%s", projectName, cnModuleName);
                }

                // replace path
                String path = file.getPath();
                path = path.replace("totoro-parent", projectName);
                path = path.replace("DivisionApplication", applicationName);
                path = path.replace("org.totoro".replace('.', File.separatorChar),
                        packageName.replace('.', File.separatorChar));

                FileUtils.writeStringToFile(new File(path), content, StandardCharsets.UTF_8);
                log.info("create file [{}]", file);
            } catch (Exception e) {
                log.error("fail to create file", e);
            }
        });

        log.info("Division fully completed. [{}]", diskPath);
    }


}