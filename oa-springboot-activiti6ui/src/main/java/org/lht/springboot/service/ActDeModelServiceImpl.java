package org.lht.springboot.service;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.lht.springboot.entity.ActDeModel;
import org.lht.springboot.mapper.ActDeModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @Description: TODO
 * @author: lhtao
 * @date: 2024年01月02日 16:54
 */
@Service
@DS("slave")
public class ActDeModelServiceImpl extends ServiceImpl<ActDeModelMapper, ActDeModel> {

    public List<ActDeModel> listModels() {
        return baseMapper.selectList(new LambdaQueryWrapper<ActDeModel>());
    }

    public ActDeModel listModelById(String id) {
        return baseMapper.selectOne(new LambdaQueryWrapper<ActDeModel>().eq(ActDeModel::getId, id));
    }
}
