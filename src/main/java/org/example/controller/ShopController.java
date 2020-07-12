package org.example.controller;

import java.util.List;
import org.example.dao.mapper.ShopMapper;
import org.example.dao.model.Shop;
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
 * @date 2020/7/10 16:34
 * @description
 */
@RestController
@RequestMapping("/shops")
public class ShopController {

    @Autowired
    private ShopMapper shopMapper;

    @GetMapping("/{id}")
    public Shop get(@PathVariable("id") Long id) {
        return shopMapper.selectById(id);
    }

    @PostMapping("")
    public Shop create(@RequestBody Shop shop) {
        shop.setId(111111111L);
        shopMapper.insertSelective(shop);
        return shop;
    }

    @PutMapping("/{id}")
    public Shop modify(@PathVariable("id") Long id, @RequestBody Shop shop) {
        shop.setId(id);
        shopMapper.updateSelectiveById(shop);

        return shop;
    }

    @DeleteMapping("/{id}")
    public String delete(@PathVariable("id") Long id) {
        shopMapper.deleteById(id);
        return "success";
    }
}
