package org.example.dao.mapper;

import org.example.dao.model.Shop;

public interface ShopMapper {
    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table shop
     *
     * @mbg.generated Fri Jul 10 16:31:26 CST 2020
     */
    int insert(Shop record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table shop
     *
     * @mbg.generated Fri Jul 10 16:31:26 CST 2020
     */
    int insertSelective(Shop record);

    Shop selectById(Long id);

    void updateSelectiveById(Shop shop);

    void deleteById(Long id);
}