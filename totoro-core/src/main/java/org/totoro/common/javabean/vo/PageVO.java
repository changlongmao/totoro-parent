package org.totoro.common.javabean.vo;

import lombok.Data;
import lombok.experimental.Accessors;
import org.totoro.common.enums.OrderDirectionEnum;
import org.totoro.common.javabean.reqDto.PageReqDTO;
import org.totoro.common.util.ObjectUtil;

import java.util.List;


/**
 * 列表返回基类
 *
 * @author ChangLF 2023/07/06
 */
@Data
@Accessors(chain = true)
public class PageVO<T> {

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
     * 构造PageVO对象
     *
     * @param pageReqDTO page请求基类
     * @param total         记录总数
     * @param record         分页记录
     * @param vClass       返回Vo的class类型
     * @return org.totoro.generator.javabean.vo.PageVO<V>
     * @author ChangLF 2023/7/27 11:44
     **/
    public static <V> PageVO<V> constructPage(PageReqDTO pageReqDTO, long total, List<?> record,
                                              Class<V> vClass) {
        PageVO<V> pageVO = new PageVO<>();
        pageVO.setPageIndex(pageReqDTO.getPageIndex());
        pageVO.setPageSize(pageReqDTO.getPageSize());
        pageVO.setSortColumn(pageReqDTO.getSortColumn());
        pageVO.setSortDirection(pageReqDTO.getSortDirection());
        pageVO.setTotal((int) total);
        pageVO.setPageCount((int) (total / pageReqDTO.getPageSize() + (total % pageReqDTO.getPageSize() == 0 ? 0 : 1)));
        pageVO.setRows(ObjectUtil.copy(record, vClass));
        return pageVO;
    }

}
