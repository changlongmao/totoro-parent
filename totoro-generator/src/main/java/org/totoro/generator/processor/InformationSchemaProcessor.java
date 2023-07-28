package org.totoro.generator.processor;

import com.google.common.io.Resources;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.text.WordUtils;
import org.springframework.util.CollectionUtils;
import org.totoro.generator.config.BaseConfig;
import org.totoro.generator.enums.JdbcTypeEnum;
import org.totoro.generator.javabean.dto.ColumnDTO;
import org.totoro.generator.javabean.dto.TableDTO;

import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.*;

/**
 * 获取数据库详情
 *
 * @author ChangLF 2023/07/21
 */
@Slf4j
public class InformationSchemaProcessor {

    /**
     * 获取数据库表以及字段信息
     * @param baseConfig 数据库配置
     * @author ChangLF 2023/7/21 11:36
     * @return java.util.Map<org.totoro.generator.javabean.dto.TableDTO,java.util.List<org.totoro.generator.javabean.dto.ColumnDTO>>
     **/
    public static Map<TableDTO, List<ColumnDTO>> getTableColumn(BaseConfig baseConfig) {
        try {
            // 拼接sql
            String sql = Resources.toString(Resources.getResource("information_schema.sql"), StandardCharsets.UTF_8);
            Set<String> tables = baseConfig.getTables();
            // 若为空查询所有表，若不为空则拼接sql
            if (!CollectionUtils.isEmpty(tables)) {
                StringJoiner tableConcat = new StringJoiner(",", "t1.table_name in (", ")");
                tables.stream().map(t -> "'" + t + "'").forEach(tableConcat::add);
                sql = sql.replace("${tableNameQuery}", tableConcat.toString());
            } else {
                sql = sql.replace("${tableNameQuery}", "true");
            }
            // 加载驱动
            Class.forName("com.mysql.cj.jdbc.Driver");
            // 获取数据库连接，try-with-resource关闭资源
            try (Connection conn = DriverManager.getConnection(baseConfig.getJdbcUrl(), baseConfig.getUserName(), baseConfig.getPassword());
                 Statement statement = conn.createStatement();
                 ResultSet resultSet = statement.executeQuery(sql)) {
                Map<TableDTO, List<ColumnDTO>> tableDTOListMap = new HashMap<>();
                while (resultSet.next()) {
                    TableDTO tableDTO = new TableDTO()
                            .setTableName(resultSet.getString("table_name"))
                            .setTableComment(resultSet.getString("table_comment"));
                    List<ColumnDTO> columnDTOList = tableDTOListMap.computeIfAbsent(tableDTO, k -> new ArrayList<>());
                    ColumnDTO columnDTO = new ColumnDTO()
                            .setColumnName(resultSet.getString("column_name"))
                            .setDataType(resultSet.getString("data_type"))
                            .setColumnType(resultSet.getString("column_type"))
                            .setColumnComment(resultSet.getString("column_comment"))
                            .setColumnKey(resultSet.getString("column_key"))
                            .setIsNullable(resultSet.getString("is_nullable"))
                            .setColumnDefault(resultSet.getString("column_default"));
                    columnDTOList.add(columnDTO);
                }
                return tableDTOListMap;
            }
        } catch (Exception e) {
            throw new RuntimeException("获取数据库信息失败", e);
        }
    }

    /**
     * 初始化表参数
     *
     * @param tableColumnMap 表字段
     * @author ChangLF 2023/7/21 16:05
     **/
    public static void decorate(Map<TableDTO, List<ColumnDTO>> tableColumnMap) {
        tableColumnMap.forEach((tableDTO, columnDTOList) -> {
            // 大驼峰命名
            tableDTO.setClassName(underscoreToCamelCase(tableDTO.getTableName()));
            String className = tableDTO.getClassName();
            tableDTO.setAttrName(StringUtils.uncapitalize(className));
            for (ColumnDTO columnDTO : columnDTOList) {
                if (StringUtils.isBlank(columnDTO.getColumnComment())) {
                    columnDTO.setColumnComment(columnDTO.getColumnName());
                }

                // 小驼峰命名
                String attrName = StringUtils.uncapitalize(underscoreToCamelCase(columnDTO.getColumnName()));
                columnDTO.setAttrName(attrName);
                columnDTO.setAttrType("tinyint(1)".equals(columnDTO.getColumnType()) ?
                        "Boolean" : JdbcTypeEnum.getTitleByCode(columnDTO.getDataType()));
                if ("PRI".equalsIgnoreCase(columnDTO.getColumnKey())) {
                    tableDTO.setPk(columnDTO);
                }
            }
        });
    }

    /**
     * 下划线转大驼峰
     *
     * @param name 字段名
     * @return java.lang.String
     * @author ChangLF 2023/7/20 08:57
     **/
    private static String underscoreToCamelCase(String name) {
        return WordUtils.capitalize(name, new char[]{'_'}).replace("_", "");
    }
}
