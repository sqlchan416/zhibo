package com.sqlchan.zhibo.controller;

import com.sqlchan.zhibo.model.HostHolder;
import com.sqlchan.zhibo.model.Question;
import com.sqlchan.zhibo.model.ViewObject;
import com.sqlchan.zhibo.service.FollowService;
import com.sqlchan.zhibo.service.LikeService;
import com.sqlchan.zhibo.service.QuestionService;
import com.sqlchan.zhibo.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2017/5/17.
 */
@Controller
public class HomeController {
    private static final Logger logger = LoggerFactory.getLogger(HomeController.class);

    @Autowired
    QuestionService questionService;

    @Autowired
    UserService userService;
    @Autowired
    LikeService likeService;
    @Autowired
    HostHolder hostHolder;
    @Autowired
    FollowService followService;

    private List<ViewObject> getQuestions(int userId, int offset, int limit) {
        List<Question> questionList = questionService.getLatestQuestions(userId, offset, limit);
        List<ViewObject> vos = new ArrayList<>();
        //boolean isfollower=false;
        for (Question question : questionList) {
            ViewObject vo = new ViewObject();
            vo.set("question", question);
            vo.set("user", userService.getUser(question.getUserId()));
//            if (hostHolder.getUser() == null) {
//                vo.set("liked", 0);
//            } else {
//                vo.set("liked", likeService.getLikeStatus(hostHolder.getUser().getId(), EntityType.ENTITY_QUESTION, question.getId()));
//            }
//            vo.set("likeCount", likeService.getLikeCount(EntityType.ENTITY_QUESTION, question.getId()));
            ////////////////////////
//            int isfollowru=0;
//            if(hostHolder.getUser()!=null){
//                isfollowru=followService.isFollower(hostHolder.getUser().getId(), EntityType.ENTITY_USER,userService.getUser(question.getUserId()).getId())?1:0;
//
//            }
//            int isfollowrq=0;
//            if(hostHolder.getUser()!=null) {
//                isfollowrq=followService.isFollower(hostHolder.getUser().getId(), EntityType.ENTITY_QUESTION, question.getId())?1:0;
//            }
//            vo.set("isfollowru",isfollowru);
//            vo.set("isfollowrq",isfollowrq);
            //////////////////////////
            vos.add(vo);
        }
        return vos;
    }

    @RequestMapping(path = {"/", "/index"})
    public String index(Model model) {
        model.addAttribute("vos", getQuestions(0, 0, 10));
        return "home";
    }

    @RequestMapping(path = {"/user/{userId}"})
    public String userIndex(Model model, @PathVariable("userId") int userId) {
        model.addAttribute("vos", getQuestions(userId, 0, 10));
        return "home";
    }
}


