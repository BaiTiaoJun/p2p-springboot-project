package com.example.vo;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @类名 ReturnVo
 * @描述 TODO
 * @作者 白条君
 * @创建日期 2022/5/2 11:49
 * @版本 1.0
 */

@Data
public class ReturnVoByPage<T> implements Serializable {
    private static final long serialVersionUID = -7651996292218110281L;
    private List<T> list;
    private Integer totalSize;
    private Integer totalPage;
    private Integer currentPage;
    private String pType;
}
