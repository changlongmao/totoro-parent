package org.totoro.generator.javabean.vo;

import lombok.Data;
import lombok.experimental.Accessors;
import org.totoro.generator.enums.OrderDirectionEnum;
import org.totoro.generator.javabean.reqDto.SPPageReqDTO;
import org.totoro.generator.util.ObjectUtil;

import java.util.List;


/**
 * 列表返回基类
 *
 * @author ChangLF 2023/07/06
 */
@Data
@Accessors(chain = true)
public class SPPageVO<T> {

    /**
     * 行对象
     */
    private List<T> rows;

    /**
     * 页码
     */
    private Integer pageIndex;

    /**
     * 每页记录数
     */
    private Integer pageSize;

    /**
     * 总数
     */
    private Integer total;

    /**
     * 总页码
     */
    private Integer pageCount;

    /**
     * 排序字段
     */
    private String sortColumn;

    /**
     * 排序方向
     */
    private OrderDirectionEnum sortDirection;

    /**
     * 构造SPPageVO对象
     *
     * @param spPageReqDTO page请求基类
     * @param total         记录总数
     * @param record         分页记录
     * @param vClass       返回Vo的class类型
     * @return org.totoro.generator.javabean.vo.SPPageVO<V>
     * @author ChangLF 2023/7/27 11:44
     **/
    public static <V> SPPageVO<V> constructPage(SPPageReqDTO spPageReqDTO, long total, List<?> record,
                                                Class<V> vClass) {
        SPPageVO<V> spPageVO = new SPPageVO<>();
        spPageVO.setPageIndex(spPageReqDTO.getPageIndex());
        spPageVO.setPageSize(spPageReqDTO.getPageSize());
        spPageVO.setSortColumn(spPageReqDTO.getSortColumn());
        spPageVO.setSortDirection(spPageReqDTO.getSortDirection());
        spPageVO.setTotal((int) total);
        spPageVO.setPageCount((int) (total / spPageReqDTO.getPageSize() + (total % spPageReqDTO.getPageSize() == 0 ? 0 : 1)));
        spPageVO.setRows(ObjectUtil.copy(record, vClass));
        return spPageVO;
    }

}
