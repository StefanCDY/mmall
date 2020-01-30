package com.mmall.controller.backend;

import com.mmall.common.Const;
import com.mmall.common.ResponseCode;
import com.mmall.common.ServerResponse;
import com.mmall.pojo.Category;
import com.mmall.pojo.User;
import com.mmall.service.ICategoryService;
import com.mmall.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpSession;
import java.util.List;

import static java.util.Objects.isNull;

/**
 * create by Stefan on 2020-01-30
 */
@Controller
@RequestMapping("/manager/category")
public class CategoryManageController {

    @Autowired
    private IUserService userService;
    @Autowired
    private ICategoryService categoryService;

    @RequestMapping(value = "add_category.do", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<String> addCategory(@RequestParam(value = "parentId", defaultValue = "0") Integer parentId, String categoryName, HttpSession session) {
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if (isNull(user)) {
            return ServerResponse.createByError(ResponseCode.NEED_LOGIN.getCode(), "用户未登录,请登录");
        }
        if (userService.checkAdmin(user).isSuccess()) {
            return categoryService.addCategory(parentId, categoryName);
        }
        return ServerResponse.createByError("无权限操作,需要管理员权限");
    }

    @RequestMapping(value = "set_category_name.do", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<String> setCategoryName(Integer categoryId, String categoryName, HttpSession session) {
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if (isNull(user)) {
            return ServerResponse.createByError(ResponseCode.NEED_LOGIN.getCode(), "用户未登录,请登录");
        }
        if (userService.checkAdmin(user).isSuccess()) {
            return categoryService.updateCategoryName(categoryId, categoryName);
        }
        return ServerResponse.createByError("无权限操作,需要管理员权限");
    }

    @RequestMapping(value = "get_category.do", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<List<Category>> getChildrenParallelCategory(@RequestParam(value = "categoryId", defaultValue = "0") Integer categoryId, HttpSession session) {
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if (isNull(user)) {
            return ServerResponse.createByError(ResponseCode.NEED_LOGIN.getCode(), "用户未登录,请登录");
        }
        if (userService.checkAdmin(user).isSuccess()) {
            return categoryService.getChildrenParallelCategory(categoryId);
        }
        return ServerResponse.createByError("无权限操作,需要管理员权限");
    }

    @RequestMapping(value = "get_deep_category.do", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<List<Integer>> getCategoryAndDeepChildrenCategory(@RequestParam(value = "categoryId", defaultValue = "0") Integer categoryId, HttpSession session) {
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if (isNull(user)) {
            return ServerResponse.createByError(ResponseCode.NEED_LOGIN.getCode(), "用户未登录,请登录");
        }
        if (userService.checkAdmin(user).isSuccess()) {
            return categoryService.selectCategoryAndChildrenById(categoryId);
        }
        return ServerResponse.createByError("无权限操作,需要管理员权限");
    }
}
