package com.sqlchan.zhibo.controller;

import com.sqlchan.zhibo.model.*;
import com.sqlchan.zhibo.service.*;
import com.sqlchan.zhibo.util.WendaUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by Administrator on 2017/5/17.
 */
@Controller
public class QuestionController {
    private static final Logger logger = LoggerFactory.getLogger(HomeController.class);

    @Autowired
    FollowService followService;
    @Autowired
    QuestionService questionService;

    @Autowired
    CommentService commentService;

    @Autowired
    HostHolder hostHolder;

    @Autowired
    UserService userService;
    @Autowired
    LikeService likeService;

    @RequestMapping(value = "/question/{qid}", method = {RequestMethod.GET})
    public String questionDetail(Model model, @PathVariable("qid") int qid) {
        Question question = questionService.getById(qid);
        User owner=userService.getUser(question.getUserId());
        model.addAttribute("question", question);
        model.addAttribute("owner", owner);
        if (hostHolder.getUser() == null) {
            model.addAttribute("liked", 0);
        } else {
            model.addAttribute("liked", likeService.getLikeStatus(hostHolder.getUser().getId(), EntityType.ENTITY_QUESTION, question.getId()));
        }
        model.addAttribute("likeCount", likeService.getLikeCount(EntityType.ENTITY_QUESTION, question.getId()));
        List<Comment> commentList = commentService.getCommentsByEntity(qid, EntityType.ENTITY_QUESTION);
        List<ViewObject> vos = new ArrayList<>();
        for (Comment comment : commentList) {
            ViewObject vo = new ViewObject();
            vo.set("comment", comment);

            vo.set("user", userService.getUser(comment.getUserId()));
            vos.add(vo);
        }
        model.addAttribute("comments", vos);

        /////////////////
        if (hostHolder.getUser() == null) {
            return "redirect:loginpage";
        }
        int   countfollow= (int) followService.getFollowerCount(EntityType.ENTITY_QUESTION,question.getId());
        //logger.info("wen ti :"+countfollow);
        List<Integer> followees=followService.getFollowers(EntityType.ENTITY_QUESTION,question.getId(),10);
        //logger.info("wen ti ren :"+followees.size());
        List<ViewObject> voss = new ArrayList<>();
        for(Integer followee:followees){
            ViewObject vo = new ViewObject();
            vo.set("userr",userService.getUser(followee));
            vo.set("username",userService.getUser(followee).getName());
            //logger.info(""+userService.getUser(followee).getName());
            voss.add(vo);
        }

        model.addAttribute("voss",voss);
        model.addAttribute("countfollow",countfollow);
        ///////////////////

        return "detail";
    }

    @RequestMapping(value = "/question/add", method = {RequestMethod.POST})
    //@ResponseBody
    public String addQuestion(@RequestParam("title") String title, @RequestParam("content") String content) {
        try {
            Question question = new Question();
            question.setContent(content);
            question.setCreatedDate(new Date());
            question.setTitle(title);
            if (hostHolder.getUser() == null) {
                question.setUserId(WendaUtil.ANONYMOUS_USERID);
                // return WendaUtil.getJSONString(999);
            } else {
                question.setUserId(hostHolder.getUser().getId());
            }
            if (questionService.addQuestion(question) > 0) {
                return "redirect:/";
                //return WendaUtil.getJSONString(0);
            }
        } catch (Exception e) {
            logger.error("增加题目失败" + e.getMessage());
        }
        return "redirect:/";
        //return WendaUtil.getJSONString(1, "失败");
    }

    @RequestMapping("/addquestionpage")
    public String addquestion(){
        return "addquestionpage";
    }
}
