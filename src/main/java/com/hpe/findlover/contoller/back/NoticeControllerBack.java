package com.hpe.findlover.contoller.back;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.hpe.findlover.model.Notice;
import com.hpe.findlover.model.UserBasic;
import com.hpe.findlover.service.NoticeService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;


/**
 * @author sinnamm
 * @Date Create in  2017/11/2.
 */
@Controller
@RequestMapping("admin/notice")
public class NoticeControllerBack {

    @Autowired
    private NoticeService noticeService;
    private Logger logger = LogManager.getLogger(NoticeControllerBack.class);

    @GetMapping("send_notice")
    public String sentNotice(){
        return "back/notice/send_notice";
    }

    @PostMapping("send_notice")
    public String sentNoticeP(Notice notice, @RequestParam(required = false)Integer userId, Model model){
        if (userId!=null){
            logger.info("向用户"+userId+"发送消息");
            notice.setPubObj(userId);
        }
        notice.setPubTime(new Date());
        logger.info(notice);
        boolean result = noticeService.insert(notice);
        if (result){
            return "back/notice/notice_list";
        }else {
            model.addAttribute("message","发布失败");
            return "back/notice/send_notice";
        }

    }

    @PostMapping("warning_notice")
    @ResponseBody
    public boolean sentNoticeP(Notice notice){
        notice.setPubTime(new Date());
        logger.info(notice);
        boolean result = noticeService.insert(notice);
        if (result){
            return true;
        }else {
            return false;
        }

    }

    @GetMapping("notice_list")
    public String noticeList(){
        return "back/notice/notice_list";
    }

    @GetMapping("getNotice")
    @ResponseBody
    public PageInfo getNotice(Page<UserBasic> page, @RequestParam String identity, @RequestParam String column, @RequestParam String keyword){
        logger.info("接收参数：identity=" + identity + ",pageNum=" + page.getPageNum() + ",pageSize=" + page.getPageSize() + ",column=" + column + ",keyword=" + keyword);
        PageHelper.startPage(page.getPageNum(), page.getPageSize());
        List<Notice> notices = noticeService.selectAllByIdentity(identity,column,"%"+keyword+"%");
        notices.forEach(logger::info);
        PageInfo pageInfo = new PageInfo(notices);
        return pageInfo;
    }

    @DeleteMapping("delete/{id}")
    @ResponseBody
    public String deleteNotice(@PathVariable int id){
        logger.info("删除id="+id);
        int result = noticeService.deleteByPrimaryKey(id);
        if (result>0){
            return "success";
        }else {
            return "error";
        }
    }

}
