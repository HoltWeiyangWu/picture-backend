package holt.picture.common;

import lombok.Data;

/**
 * Paging request
 * @author Weiyang Wu
 * @date 2025/3/31 14:25
 */
@Data
public class PageRequest {
    /**
     * Current page number
     */
    private int current = 1;

    /**
     * The number of data to display on a page
     */
    private int pageSize = 10;

    /**
     * Sort base on this field
     */
    private String sortField;

    /**
     * Sort order (default descend)
     */
    private String sortOrder = "descend";
}
