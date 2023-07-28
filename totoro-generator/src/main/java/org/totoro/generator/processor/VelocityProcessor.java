package org.totoro.generator.processor;

import com.google.common.io.Files;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;

import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.util.Objects;
import java.util.Properties;

/**
 * Velocity模版引擎执行器
 * @author ChangLF 2023/07/21
 */
@Slf4j
public class VelocityProcessor {

    static {
        Properties prop = new Properties();
        prop.put("resource.loader.file.class", "org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader");
        Velocity.init(prop);
    }

    /**
     * 是否文件覆盖，默认为true
     */
    private static boolean FILE_OVERRIDE = true;

    /**
     * Velocity替换模版
     * @param velocityContext 替换的内容
	 * @param templateName 模版名称
     * @author ChangLF 2023/7/24 08:37
     **/
    public static void process(VelocityContext velocityContext, String templateName, String pathname) {
        if (Objects.isNull(velocityContext) || StringUtils.isBlank(templateName) || StringUtils.isBlank(pathname)) {
            return;
        }

        StringWriter sw = new StringWriter();
        Template template = Velocity.getTemplate(templateName, "UTF-8");
        template.merge(velocityContext, sw);
        File file = new File(pathname);
        // 创建父级文件夹
        file.getParentFile().mkdirs();
        if (file.exists() && !FILE_OVERRIDE) {
            log.warn("{}文件已存在，跳过生成", pathname);
            return;
        }
        try {
            Files.write(sw.toString().getBytes(StandardCharsets.UTF_8), file);
        } catch (IOException e) {
            log.warn("{}生成失败", pathname, e);
        }
    }

    public static boolean isFileOverride() {
        return FILE_OVERRIDE;
    }

    public static void setFileOverride(boolean fileOverride) {
        FILE_OVERRIDE = fileOverride;
    }
}
