package com.cc.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cc.domain.pojo.Menu;
import com.cc.mapper.MenuMapper;
import com.cc.service.MenuService;
import com.cc.utils.MyTreeUtils;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

/**
 * 菜单ServiceImpl层
 */
@Service
public class MenuServiceImpl extends ServiceImpl<MenuMapper, Menu> implements MenuService {

    //父级菜单的查询
    @Override
    public List<Menu> getParentList() {
        // 构造查询条件，查询类型为 "0" 和 "1" 的菜单，并按 orderNum 升序排列
        QueryWrapper<Menu> wrapper = new QueryWrapper<>();
        wrapper.lambda()
                .in(Menu::getType, Arrays.asList("0", "1"))
                .orderByAsc(Menu::getOrderNum);
        List<Menu> menus = baseMapper.selectList(wrapper);

        // 添加顶级的菜单
        Menu rootMenu = new Menu();
        rootMenu.setMenuId(0);
        rootMenu.setParentId(-1);
        rootMenu.setMenuLabel("顶级菜单");

        // 构造树形数据
        return buildMenuTree(menus, -1);
    }

    //查询菜单列表
    @Override
    public List<Menu> getMenuList() {
      // 构造查询条件，按 orderNum 升序排列
        QueryWrapper<Menu> wrapper = new QueryWrapper<>();
        wrapper.lambda().orderByAsc(Menu::getOrderNum);
        List<Menu> menus = baseMapper.selectList(wrapper);

        //构造树形数据
        return buildMenuTree(menus,0);
    }

    //删除菜单
    @Override
    public boolean deleteMenu(Integer menuId) {
        //构造查询条件
        QueryWrapper<Menu> wrapper = new QueryWrapper<>();
        wrapper.lambda().eq(Menu::getParentId,menuId);
        List<Menu> menuList = baseMapper.selectList(wrapper);
        if (menuList.size() > 0){
            return false;
        }
        return true;
    }

    //根据用户id查询权限
    @Override
    public List<Menu> getMenuByUserId(Integer userId) {
        if(userId == null){
            throw new IllegalArgumentException("用户ID不能为空");
        }
        return this.baseMapper.getMenuByUserId(userId);
    }

    //根据业主id查询权限
    @Override
    public List<Menu> getMenuByLiverId(Integer liverId) {
        if (liverId == null) {
            throw new IllegalArgumentException("业主ID不能为空");
        }
        return baseMapper.getMenuByLiverId(liverId);
    }

    //根据角色id查询权限
    @Override
    public List<Menu> getMenuByRoleId(Integer roleId) {
        if (roleId == null) {
            throw new IllegalArgumentException("角色ID不能为空");
        }
        return this.baseMapper.getMenuByRoleId(roleId);
    }


    // 检查是否存在子菜单
    private boolean existsChildMenu(Integer menuId) {
        QueryWrapper<Menu> wrapper = new QueryWrapper<>();
        wrapper.lambda().eq(Menu::getParentId,menuId);
        return baseMapper.selectCount(wrapper) > 0 ;
    }


    // 构造树形数据的公共方法
    private List<Menu> buildMenuTree(List<Menu> menus, Integer parentId) {
        return MyTreeUtils.makeTree(menus, parentId);
    }

}
