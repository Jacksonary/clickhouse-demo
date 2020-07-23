package org.example.controller;

import org.example.dao.db2.mapper.TestMergeTreeMapper;
import org.example.dao.db2.model.TestMergeTree;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author liuwg-a
 * @date 2020/7/23 11:12
 * @description
 */
@RestController
@RequestMapping("/test_merge_trees")
public class TestMergeTreeController {

    @Autowired
    private TestMergeTreeMapper testMergeTreeMapper;

    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public TestMergeTree get(@PathVariable("id") Integer id) {
        return testMergeTreeMapper.selectById(id);
    }

    @RequestMapping(value = "", method = RequestMethod.POST)
    public void create() {
        TestMergeTree testMergeTree = new TestMergeTree();
        testMergeTree.setId(2L);
        testMergeTree.setEvent("test-event222");
        testMergeTreeMapper.insertSelective(testMergeTree);
    }
}
