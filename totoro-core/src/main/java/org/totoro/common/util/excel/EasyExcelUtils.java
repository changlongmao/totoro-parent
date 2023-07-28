package org.totoro.common.util.excel;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.event.AnalysisEventListener;
import com.alibaba.excel.metadata.CellData;
import com.alibaba.excel.metadata.Head;
import com.alibaba.excel.util.StringUtils;
import com.alibaba.excel.write.handler.AbstractCellWriteHandler;
import com.alibaba.excel.write.metadata.holder.WriteSheetHolder;
import com.alibaba.excel.write.metadata.holder.WriteTableHolder;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddressList;
import org.apache.poi.ss.util.SheetUtil;
import org.springframework.util.CollectionUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.multipart.MultipartFile;
import org.totoro.common.exception.CommonApiException;
import org.totoro.common.util.StringUtil;

import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.lang.reflect.Field;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.*;

/**
 * EasyExcel工具类
 * @author changlf 2023-05-16
 **/
@Slf4j
public class EasyExcelUtils {

    /**
     * 导出文件的后缀名
     */
    private static final String EXCEL_XLSX = ".xlsx";
    private static final String EXCEL_XLS = "xls";

    private static final String XLSX_CONTENT_TYPE = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";

    // 最大导出条数
    public static final Integer TWENTY_THOUSAND = 20000;

    // 最大导入条数
    public static final Integer MAX_IMPORT_NUMBER = 2000;

    /**
     * 导出输出到文件
     *
     * @param file 文件对象
     * @date 2022/8/3 18:18
     **/
    public static <T> void exportToFile(File file, List<T> data, Class<T> clazz, String sheetName) {
        try {
            EasyExcel.write(file, clazz)
                    .registerWriteHandler(new CustomCellWriteHandler(data.size()))
                    .sheet(sheetName).doWrite(data);
        } catch (Exception e) {
            log.error("生成excel发生异常", e);
        }
    }

    /**
     * 输出到InputStream，可拿着InputStream上传到云服务器
     *
     * @date 2021/8/4 12:35
     * @return java.io.InputStream
     **/
    public static <T> InputStream exportInputStream(List<T> data, Class<T> clazz, String sheetName) {
        try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            EasyExcel.write(out, clazz)
                    .registerWriteHandler(new CustomCellWriteHandler(data.size()))
                    .sheet(sheetName).doWrite(data);
            return new ByteArrayInputStream(out.toByteArray());
        } catch (Exception e) {
            log.error("生成excel发生异常", e);
        }
        return null;
    }

    /**
     * 导出到web页面，可不传HttpServletResponse，自动从ThreadLocal获取（若不是http请求则获取不到）
     * @param data 数据来源
     * @param clazz 泛型的class对象
     * @param fileName 文件名，同时也是表单名称，不需要带后缀名，默认'.xlsx'
     * @author Chang
     * @date 2021/8/4 13:44
     **/
    public static <T> void exportToHttp(List<T> data, Class<T> clazz, String fileName) {
        exportWithResponse(data, clazz, fileName, null, null, null);
    }

    /**
     * exportToHttp重载方法，可设置表头对齐方式
     *
     * @param headAlign    设置表头水平对齐方式，不设置默认居中
     */
    public static <T> void exportToHttp(List<T> data, Class<T> clazz, String fileName, HorizontalAlignment headAlign) {
        exportWithResponse(data, clazz, fileName, headAlign, null, null);
    }

    /**
     * exportToHttp重载方法，可设置单元格下拉选
     *
	 * @param dropDownMap 设置单元格下拉选值，key为columnIndex，value为字符串集合
     * @param dataRow 数据行起始行，不设置默认为1，即0为表头行
     * @date 2022/2/18 14:01
     **/
    public static <T> void exportToHttp(List<T> data, Class<T> clazz, String fileName, Map<Integer, List<String>> dropDownMap, Integer dataRow) {
        exportWithResponse(data, clazz, fileName, null, dropDownMap, dataRow);
    }

    /**
     * 导出excel文件到HttpServletResponse方法
	 * @param data 数据来源
	 * @param clazz 泛型的class对象
	 * @param fileName 文件名，同时也是表单名称
	 * @param headAlign 表头水平对齐方式
	 * @param dropDownMap 单元格下拉选集合
     * @author Chang
     * @date 2022/2/18 14:06
     **/
    private static <T> void exportWithResponse(List<T> data, Class<T> clazz,
                                               String fileName, HorizontalAlignment headAlign,
                                               Map<Integer, List<String>> dropDownMap, Integer dataRow) {
        if (CollectionUtils.isEmpty(data)) {
            return;
        }
        if (data.size() > TWENTY_THOUSAND) {
            throw new CommonApiException("101001001", "导出数据条数不能超过20000条");
        }
        HttpServletResponse response =
                ((ServletRequestAttributes) Objects.requireNonNull(RequestContextHolder.getRequestAttributes())).getResponse();
        if (response == null) {
            return;
        }
        try {
            OutputStream out = response.getOutputStream();
            response.setContentType(XLSX_CONTENT_TYPE);
            response.setCharacterEncoding("UTF-8");
            response.setHeader("Content-Disposition",
                    "attachment;filename=" + URLEncoder.encode(fileName + EXCEL_XLSX, "UTF-8"));
            // 这里需要指定写用哪个class去写，然后写到第一个sheet，名字为文件名,然后文件流会自动关闭
            EasyExcel.write(out, clazz)
                    .registerWriteHandler(new CustomCellWriteHandler(data.size(), headAlign, dropDownMap, dataRow))
                    .sheet(fileName)
                    .doWrite(data);
        } catch (Exception e) {
            log.error("生成excel发生异常", e);
            response.setContentType("application/json;charset=UTF-8");
            throw new CommonApiException("101001001", e.getMessage());
        }
    }

    /**
     * easyExcel自定义策略
     * 1. 对列宽进行自适应，并处于最大最小之间
     * 2. 若表头有'\n'换行符，进行换行并设置标题高度
     * 3. HorizontalAlignment设置首行水平对齐方式，默认居中
     * 4. dropDownMap设置单元格下拉选值，key为columnIndex，value为字符串集合
     * 会设置除标题外后两万行的单元格下拉选
     *
     * @author Chang
     * @date 2023/5/9 9:49
     **/
    private static class CustomCellWriteHandler extends AbstractCellWriteHandler {

        /**
         * 最大最小宽度
         */
        private static final int MIN_COLUMN_WIDTH = 10;
        private static final int MAX_COLUMN_WIDTH = 50;
        // 因EasyExcel是每500条清理缓存一次，故计算列宽时无法对所有列宽自适应，故做缓存记录并取最大值
        private final Map<Integer, Double> cache = new HashMap<>();
        // 数据条数，方便找到最后一行
        private final int size;
        // 表头对齐方式
        private final HorizontalAlignment headAlign;
        // 下拉框值
        private final Map<Integer, List<String>> dropDownMap;
        // 数据起始行，即除去表头的数据行，默认为1
        private Integer dataRow = 1;

        public CustomCellWriteHandler(int size) {
            this.size = size;
            headAlign = null;
            dropDownMap = null;
        }

        public CustomCellWriteHandler(int size, HorizontalAlignment headAlign, Map<Integer, List<String>> dropDownMap, Integer dataRow) {
            this.size = size;
            this.headAlign = headAlign;
            this.dropDownMap = dropDownMap;
            if (dataRow != null) {
                this.dataRow = dataRow;
            }
        }

        /**
         * 先计算表头自适应列宽，再在最后一行计算自适应列宽，设置更大的那个；
         * 因为easyExcel有缓存，sheet只能拿到500条数据，只计算了最后500条的自适应列宽；
         * 基本能满足场景需求
         *
         * @author Chang
         * @date 2021/10/9 9:56
         **/
        @Override
        public void afterCellDispose(WriteSheetHolder writeSheetHolder, WriteTableHolder writeTableHolder,
                                     List<CellData> cellDataList, Cell cell, Head head, Integer relativeRowIndex, Boolean isHead) {
            Sheet sheet = writeSheetHolder.getSheet();
            if (isHead) {
                // 表头自动换行高度设置
                int maxHeight = 1;
                if (cell.getCellTypeEnum() == CellType.STRING) {
                    if (cell.getStringCellValue().contains("\n")) {
                        int length = cell.getStringCellValue().split("\n").length;
                        maxHeight = Math.max(maxHeight, length);
                    }
                }
                cell.getRow().setHeightInPoints((float) (maxHeight * 25));

                // 首行居左对齐
                if (cell.getRowIndex() == 0 && headAlign != null) {
                    CellStyle style = sheet.getWorkbook().createCellStyle();
                    style.cloneStyleFrom(cell.getCellStyle());
                    style.setAlignment(headAlign);
                    cell.setCellStyle(style);
                }

                // 先对表头计算自适应列宽并缓存
                cache.put(cell.getColumnIndex(), SheetUtil.getColumnWidth(sheet, cell.getColumnIndex()
                        , false));

                // 设置使用单元格下拉选
                if (!CollectionUtils.isEmpty(dropDownMap) && dropDownMap.containsKey(cell.getColumnIndex())) {
                    List<String> dataArr = dropDownMap.get(cell.getColumnIndex());
                    DataValidationHelper dvHelper = sheet.getDataValidationHelper();
                    DataValidationConstraint dvConstraint = dvHelper.createExplicitListConstraint(dataArr.toArray(new String[0]));
                    // 除去表头之后的最大行设置单元格下拉框
                    CellRangeAddressList addressList = new CellRangeAddressList(dataRow, TWENTY_THOUSAND + dataRow - 1, cell.getColumnIndex(), cell.getColumnIndex());
                    DataValidation validation = dvHelper.createValidation(dvConstraint, addressList);
                    sheet.addValidationData(validation);
                }
            }

            // 当处于最后一行的时候，计算一下列宽并设置，其他情况返回
            if (size - 1 != relativeRowIndex) {
                return;
            }

            double columnWidth = Math.max(SheetUtil.getColumnWidth(sheet, cell.getColumnIndex()
                    , false), cache.get(cell.getColumnIndex()));

            // xlsx文档计算列表不准确，进行适当加宽
            columnWidth *= 1.7;
            columnWidth += 5;
            if (columnWidth < 0) {
                return;
            }
            if (columnWidth > MAX_COLUMN_WIDTH) {
                columnWidth = MAX_COLUMN_WIDTH;
            } else if (columnWidth < MIN_COLUMN_WIDTH) {
                columnWidth = MIN_COLUMN_WIDTH;
            }
            // 设置每列自适应列宽
            sheet.setColumnWidth(cell.getColumnIndex(), (int) (columnWidth * 256));
        }

    }

    /**
     * Excel导入，默认读取第一个sheet。
     * 默认使用fileUrl导入方式，两行表头，数据行在第三行，默认会将表头行读为第一行数据，用来校验模板标题，导出结果数据会去除标题行
     * @param excelImport 具体参数详见类内部
     * @author Chang
     * @date 2022/5/11 16:20
     * @return java.util.List<T>
     **/
    public static <T> List<T> importExcel(ExcelImportDto<T> excelImport) {
        log.info("----------EasyExcel导入解析开始----------");
        InputStream inputStream;
        ExcelImportTypeEnum importTypeEnum = excelImport.getImportTypeEnum();
        switch (importTypeEnum == null ? ExcelImportTypeEnum.FILE_URL : importTypeEnum) {
            case INPUT_STREAM:
                inputStream = excelImport.getInputStream();
                break;
            case MULTIPART_FILE:
                MultipartFile file = excelImport.getFile();
                if (null == file) {
                    throw new CommonApiException("101001007");
                }

                String filename = file.getOriginalFilename();
                checkFile(filename);
                try {
                    inputStream = file.getInputStream();
                } catch (IOException e) {
                    log.warn("获取输入流失败", e);
                    return null;
                }
                break;
            default:
                // 不填枚举默认为fileUrl
                String fileUrl = excelImport.getFileUrl();
                checkFile(fileUrl);
                inputStream = urlToInputStream(fileUrl);
        }
        excelImport.setInputStream(inputStream);
        excelImport.setHeadRowNumber(excelImport.getHeadRowNumber() == null ? 2 : (excelImport.getHeadRowNumber() < 0 ? 0 : excelImport.getHeadRowNumber()));
        return parseExcel(excelImport);
    }

    private static <T> List<T> parseExcel(ExcelImportDto<T> excelImport) {
        EasyExcelListener<T> easyExcelListener = new EasyExcelListener<>();
        // headRowNumber不填默认为2，此处减1是从表头开始读，将表头行读入，用来校验模板标题是否被修改，返回结果会去除标题行
        try {
            EasyExcel.read(excelImport.getInputStream(), excelImport.getClazz(), easyExcelListener)
                    .headRowNumber(excelImport.getHeadRowNumber() - 1)
                    .sheet().doRead();
        } catch (Exception e) {
            throw new CommonApiException("101001009");
        }
        List<T> dataList = easyExcelListener.getDataList();

        // 校验模板标题是否被修改，返回结果去除标题行
        checkExcelTemplate(dataList, excelImport.getClazz(), excelImport.getHeadRowNumber() - 1);

        if (CollectionUtils.isEmpty(dataList)) {
            throw new CommonApiException("101001009");
        }
        if (dataList.size() > (excelImport.getMaxRowNumber() == null ? MAX_IMPORT_NUMBER : excelImport.getMaxRowNumber())) {
            throw new CommonApiException("101001009");
        }
        log.info("----------EasyExcel导入解析结束----------");
        return dataList;
    }

    /**
     * 校验导入模板表头数据是否正确
     */
    private static <T> void checkExcelTemplate(List<T> tList, Class<T> clazz, Integer titleIndex) {
        if (CollectionUtils.isEmpty(tList) || Objects.isNull(tList.get(0))) {
            throw new CommonApiException("101001009");
        }
        T obj = tList.get(0);
        Field[] entryFields = clazz.getDeclaredFields();
        for (Field field : entryFields) {
            ExcelProperty excelProperty = field.getAnnotation(ExcelProperty.class);
            boolean notCheck = excelProperty == null || excelProperty.value().length <= 0 || (excelProperty.value().length == 1
                            && StringUtils.isEmpty((excelProperty.value())[0]));
            if (notCheck) {
                continue;
            }
            field.setAccessible(true);
            Object fieldValue;
            try {
                fieldValue = field.get(obj);
            } catch (IllegalAccessException e) {
                throw new CommonApiException("101001009");
            }
            if (Objects.isNull(fieldValue)) {
                throw new CommonApiException("101001009");
            }
            String[] template = excelProperty.value();
            if (!template[titleIndex].equals(fieldValue)) {
                throw new CommonApiException("101001009");
            }
        }
        // 移除 标题行，得到导入数据
        tList.remove(0);
    }

    private static void checkFile(String filename) {
        if (StringUtil.isEmpty(filename) || (!filename.endsWith(EXCEL_XLSX) && !filename.endsWith(EXCEL_XLS))) {
            throw new CommonApiException("101001009");
        }
    }

    private static InputStream urlToInputStream(String fileUrl) {
        try {
            if (StringUtil.isEmpty(fileUrl)) {
                throw new CommonApiException("101001009");
            }
            URL url = new URL(fileUrl);
            URLConnection conn = url.openConnection();
            conn.setConnectTimeout(30 * 1000);
            conn.setReadTimeout(30 * 1000);
            conn.connect();
            // 创建输入流读取文件
            return conn.getInputStream();
        } catch (IOException e) {
            log.error("读取文件失败：", e);
        }
        return null;
    }

    private static class EasyExcelListener<T> extends AnalysisEventListener<T> {

        private final List<T> dataList = new ArrayList<>();

        public List<T> getDataList() {
            return dataList;
        }

        @Override
        public void invoke(T data, AnalysisContext context) {
            dataList.add(data);
        }

        @Override
        public void doAfterAllAnalysed(AnalysisContext context) {
            log.info("excel所有数据解析完成！");
        }
    }


}
