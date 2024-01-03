package org.lht.springboot.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import java.util.Date;

/**
 * @Description: TODO
 * @author: lhtao
 * @date: 2024年01月02日 16:49
 */
@Data
public class ActDeModel {

    @TableId(type = IdType.ASSIGN_UUID)
    private String id;

    private String name;

    private String modelKey;

    private String description;

    private String modelComment;

    private Date created;

    private String createdBy;

    private Date lastUpdated;

    private String lastUpdatedBy;

    private Integer version;

    private String modelEditorJson;

    private byte[] thumbnail;

    private Integer modelType;
}
