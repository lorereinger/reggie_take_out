package com.itheima.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.itheima.reggie.common.R;
import com.itheima.reggie.dto.DishDto;
import com.itheima.reggie.entity.Category;
import com.itheima.reggie.entity.Dish;
import com.itheima.reggie.entity.DishFlavor;
import com.itheima.reggie.service.CategoryService;
import com.itheima.reggie.service.DishFlavorService;
import com.itheima.reggie.service.DishService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * 菜品管理
 */
@RestController
@RequestMapping("/dish")
@Slf4j
public class DishController {
    @Autowired
    private DishService dishService;

    @Autowired
    private DishFlavorService dishFlavorService;

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private RedisTemplate redisTemplate;
    /**
     * 新增菜品
     * @param dishDto
     * @return
     */
    @PostMapping
    public R<String> save(@RequestBody DishDto dishDto){
        log.info("添加菜品:{}",dishDto);
        dishService.saveWithFlavor(dishDto);
        //dishService.save(dishDto);
        //清理指定菜品的缓存
        String key = "dish_"+dishDto.getCategoryId()+"_1";
        redisTemplate.delete(key);
        return R.success("新增菜品成功");
    }


    /**
     * 菜品信息分页查询
     * @param page
     * @param pageSize
     * @param name
     * @return
     */
    @GetMapping("/page")
    public R<Page> page(int page,int pageSize,String name){
        //构造分页构造器对象
        Page<Dish> pageInfo = new Page<>(page,pageSize);
        Page<DishDto> dishDtoPage = new Page<>();
        //条件构造器
        LambdaQueryWrapper<Dish> queryWrapper = new LambdaQueryWrapper<>();
        //添加过滤条件
        queryWrapper.like(name != null , Dish::getName , name);
        //拖俺家排序条件
        queryWrapper.orderByDesc(Dish::getCreateTime);
        //执行分页查询
        dishService.page(pageInfo,queryWrapper);

        //对象拷贝
        BeanUtils.copyProperties(pageInfo,dishDtoPage,"records");

        List<Dish> records = pageInfo.getRecords();
        List<DishDto> list = records.stream().map((item)->{
            DishDto dishDto = new DishDto();
            BeanUtils.copyProperties(item,dishDto);
            Long categoryId = item.getCategoryId();
            //根据id查询分类对象
            Category category = categoryService.getById(categoryId);
            if (category!=null){
                dishDto.setCategoryName(category.getName());
            }
            /*String categoryName = category.getName();
            dishDto.setCategoryName(categoryName);*/

            return dishDto;
        }).collect(Collectors.toList());

        dishDtoPage.setRecords(list);

        return R.success(dishDtoPage);
    }

    /**
     * 根据id查询菜品和口味信息
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    public R<DishDto> getById(@PathVariable Long id){
        DishDto dishDto = dishService.getByIdWithFlavor(id);

        return R.success(dishDto);
    }

    /**
     * 修改菜品
     * @param dishDto
     * @return
     */
    @PutMapping
    public R<String> update(@RequestBody DishDto dishDto){
        log.info("修改菜品:{}",dishDto);
        dishService.updateWithFlavor(dishDto);
        //清理所有菜品的缓存
        /*Set keys = redisTemplate.keys("dish_*");
        redisTemplate.delete(keys);*/
        //清理指定菜品的缓存
        String key = "dish_"+dishDto.getCategoryId()+"_1";
        redisTemplate.delete(key);
        return R.success("修改菜品成功");
    }

    /**
     * 根据id批量删除菜品
     * @param ids
     * @return
     */
    @DeleteMapping
    public R<String> delete(String ids){
        String[] split = ids.split(","); //将每个id分开
        //每个id还是字符串，转成Long
        List<Long> idList = Arrays.stream(split).map(s -> Long.parseLong(s.trim())).collect(Collectors.toList());
        dishService.removeByIds(idList);//执行批量删除
        log.info("删除的ids: {}",ids);
        return R.success("删除成功"); //返回成功
    }

    /**
     * 根据id批量停售菜品
     * @param ids
     * @return
     */
    @PostMapping("/status/{status}")
    public R<String> updateStatus(@PathVariable int status,String ids){
        String[] split = ids.split(","); //将每个id分开
        //每个id还是字符串，转成Long
        List<Long> idList = Arrays.stream(split).map(s -> Long.parseLong(s.trim())).collect(Collectors.toList());
        //将每个id new出来一个Dish对象，并设置状态
        List<Dish> dishes = idList.stream().map((item) -> {
            Dish dish = new Dish();
            dish.setId(item);
            dish.setStatus(status);
            return dish;
        }).collect(Collectors.toList()); //Dish集合

        dishService.updateBatchById(dishes);//执行批量停售
        log.info("停售的ids: {}",ids);
        return R.success("停售成功"); //返回成功
    }

    /**
     * 根据条件查询菜品
     * @return
     */
    /*@GetMapping("/list")
    public R<List<Dish>> list(Dish dish){
        //构造查询条件
        LambdaQueryWrapper<Dish> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(dish.getCategoryId() != null , Dish::getCategoryId , dish.getCategoryId());
        //状态为1（起售）的菜品
        queryWrapper.eq(Dish::getStatus , 1);
        //添加排序条件
        queryWrapper.orderByAsc(Dish::getSort).orderByDesc(Dish::getCreateTime);

        List<Dish> list = dishService.list(queryWrapper);
        return R.success(list);
    }*/
    @GetMapping("/list")
    public R<List<DishDto>> list(Dish dish){
         List<DishDto> dishDtoList=null;
        String key = "dish_"+dish.getCategoryId()+"_"+dish.getStatus();
        //从redis中获取菜品列表
        dishDtoList =(List<DishDto>)redisTemplate.opsForValue().get(key);
        if(dishDtoList!=null){
            //如果存在，直接返回
            return R.success(dishDtoList);
        }
        //如果不存在，从数据库中查询，并存入redis中

        //构造查询条件
        LambdaQueryWrapper<Dish> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(dish.getCategoryId() != null , Dish::getCategoryId , dish.getCategoryId());
        //状态为1（起售）的菜品
        queryWrapper.eq(Dish::getStatus , 1);
        //添加排序条件
        queryWrapper.orderByAsc(Dish::getSort).orderByDesc(Dish::getCreateTime);

        List<Dish> list = dishService.list(queryWrapper);

        dishDtoList =  list.stream().map((item)-> {
            DishDto dishDto = new DishDto();
            BeanUtils.copyProperties(item, dishDto);
            Long categoryId = item.getCategoryId();
            //根据id查询分类对象
            Category category = categoryService.getById(categoryId);
            if (category != null) {
                dishDto.setCategoryName(category.getName());
            }
            Long dishId = item.getId();
            LambdaQueryWrapper<DishFlavor> lambdaQueryWrapper = new LambdaQueryWrapper<>();
            lambdaQueryWrapper.eq(DishFlavor::getDishId,dishId);

            List<DishFlavor> dishFlavorList = dishFlavorService.list(lambdaQueryWrapper);
            dishDto.setFlavors(dishFlavorList);
            return dishDto;
        }).collect(Collectors.toList());
        //如果不存在，从数据库中查询，并存入redis中
        redisTemplate.opsForValue().set(key,dishDtoList,60, TimeUnit.MINUTES);
        return R.success(dishDtoList);
    }
}
