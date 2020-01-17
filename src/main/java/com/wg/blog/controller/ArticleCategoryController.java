package com.wg.blog.controller;

import com.wg.blog.model.ArticleCategory;
import com.wg.blog.service.ArticleCategoryService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
@Slf4j
@RestController
@RequestMapping("/api/category")
@Api(tags = "文章类别相关接口")
public class ArticleCategoryController {
    @Autowired
    private ArticleCategoryService articleCategoryService;

    /**
     * @return
     */
    @ApiOperation("获取所有类别信息")
    @GetMapping("/list")
    public List<ArticleCategory> listAllCategories(){
        return null;
    }
    @ApiOperation("获取某一条分类信息")
    @ApiImplicitParam(name = "id",value = "分类id",required=true,dataType = "Long")
    @GetMapping("/{id}")

    public ArticleCategory getCategoryById(){
        return null;
    }
}
