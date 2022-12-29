package com.itheima.reggie.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.itheima.reggie.dto.SetmealDto;
import com.itheima.reggie.entity.Setmeal;

import java.util.List;

public interface SetmealService extends IService<Setmeal> {
    /**
     * 新增套餐，保存套餐和菜品的关系
     * @param setmealDto
     */
    public void saveWithDish(SetmealDto setmealDto);

    /**
     * 删除套餐，删除套餐和菜品的关系
     * @param ids
     */
    public void removeWithDish(List<Long> ids);

    /**
     * 修改套餐状态
     * @param status
     * @param ids
     */
    void updateStatus(int status, List<Long> ids);

    /**
     * 根据id查询套餐和菜品的关系
     * @param id
     * @return
     */
    SetmealDto getByIdWithDish(Long id);

    /**
     * 修改套餐，修改套餐和菜品的关系
     * @param setmealDto
     */
    void updateWithDish(SetmealDto setmealDto);
}
