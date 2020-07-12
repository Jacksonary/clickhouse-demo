package org.example.controller;

import java.util.Date;
import org.example.dao.mapper.ComponentOperationMapper;
import org.example.dao.model.ComponentOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author liuwg-a
 * @date 2020/7/9 14:37
 * @description
 */
@RestController
@RequestMapping("/component_operations")
public class ComponentOperationController {

    @Autowired
    private ComponentOperationMapper componentOperationMapper;

    @GetMapping("/{id}")
    public ComponentOperation select(@PathVariable("id") Long id) {
        return componentOperationMapper.selectByPrimaryKey(id);
    }

    @PostMapping("")
    public ComponentOperation add(@RequestBody ComponentOperation componentOperation) {
        componentOperation.setId(0L);
        componentOperation.setCreateTime(new Date());
        componentOperationMapper.insertSelective(componentOperation);

        return componentOperation;
    }

    @PutMapping("/{id}")
    public ComponentOperation modify(@PathVariable("id") Long id, @RequestBody ComponentOperation componentOperation) {
        componentOperation.setId(id);
        componentOperationMapper.updateByPrimaryKeySelective(componentOperation);

        return componentOperation;
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable("id") Long id) {
        componentOperationMapper.deleteByPrimaryKey(id);
    }

}
