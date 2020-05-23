<%@ page language="java" import="java.util.*" pageEncoding="utf-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>  
<!DOCTYPE html>
<html>

<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <title>专题</title>
    <link href="${ctx }/xedit/css/base.css" type="text/css" rel="stylesheet">
</head>

<body style="background-color: #ffffff;">
    <div class="modal-header" style="padding: 20px 15px;">
        <a class="close js-news-modal-dismiss" id="js-news-modal-dismiss-goods">×</a>
    </div>
    <div class="modal-body">
        <div class="tab-content">
            <div id="js-module-feature" class="tab-pane module-feature active">
                <table class="table">
                    <colgroup>
                        <col class="modal-col-title">
                        <col class="modal-col-time" span="2">
                        <col class="modal-col-action">
                    </colgroup>
                    <!-- 表格头部 -->
                    <thead>
                        <tr>
                            <th class="title" style="background-color: #f5f5f5;">
                                <div class="td-cont">
                                    <span>标题</span> <a class="js-update" href="javascript:window.location.reload();">刷新</a>
                                </div>
                            </th>
                            <th class="time" style="background-color: #f5f5f5;">
                                <div class="td-cont">
                                    <span>创建时间</span>
                                </div>
                            </th>
                            <th class="opts" style="background-color: #f5f5f5;">
                                <!--<div class="td-cont" style="padding: 7px 0 3px 10px;">
                                    <form class="form-search" onsubmit="return false;">
                                        <div class="input-append">
                                            <input class="input-small js-modal-search-input" type="text"><a href="javascript:void(0);" class="btn js-fetch-page js-modal-search">搜</a>
                                        </div>
                                    </form>
                                </div>-->
                            </th>
                        </tr>
                    </thead>
                    <!-- 表格数据区 -->
                    <tbody id="pagetListd">

                    </tbody>
                </table>
            </div>
        </div>
    </div>
    <div class="modal-footer">
        <div style="display: none;" class="js-confirm-choose pull-left">
            <input type="button" class="btn btn-primary" value="确定使用">
        </div>
        <div class="pagenavi js-page-list" style="margin-top: 0;" id="pagediv">

        </div>
    </div>
</body>
<script type="text/javascript" src="${ctx }/xedit/js/jquery.min.js"></script>
<script type="text/javascript" charset="utf-8" src="${ctx }/xedit/js/util.js"></script>
<script type="text/javascript">
var currentPage = 1;
var channelId = commFun.getUrlPara("channelId");
var pageId = commFun.getUrlPara("pageId");
    $(function() {
    	var channelId = commFun.getUrlPara("channelId");
    	var pageId = commFun.getUrlPara("pageId");
        sendAjax.get("widget/getThemePageList", {
            channelId: channelId,
            pageId: pageId,
            currentPage: currentPage,
            limit: 10
        }, function(res) {
            if (res.code == 1) {
                setHtml(res.page);
            }else{
            	alert(res.msg);
            }
        });
        $('.js-modal iframe', parent.document).height($('body').height());
        $('.modal-header .close').live('click', function() {
            parent.login_box_close();
        });
        url = "";
        $('button.js-choose').live('click', function() {

            parent.login_box_after(parent.randNum, 'theme', $(this).data('title'), $(this).data('id'), $(this).data('image'));
        });
     /*    $('.js-page-list a').live('click', function(e) {
            if (!$(this).hasClass('active')) {
                var input_val = $('.js-modal-search-input').val();
                $('body').html('<div class="loading-more"><span></span></div>');
                $('body').load('/user/widget/page.html', {
                    currentPage: $(this).data('page-num'),
                    'keyword': input_val
                }, function() {
                    $('.js-modal iframe', parent.document).height($('body').height());
                });
            }
        }); */
        $('.js-page-list a').live('click', function(e) {
            if (!$(this).hasClass('active')) {
                currentPage = $(this).data('page-num');
                LoadData();
            }
        });
        $('.js-modal-search').live('click', function(e) {
            var input_val = $('.js-modal-search-input').val();
            $('body').html('<div class="loading-more"><span></span></div>');
            $('body').load('/user/widget/page.html', {
                'keyword': input_val
            }, function() {
                $('.js-modal iframe', parent.document).height($('body').height());
            });
            return false;
        });
        $('#js-news-modal-dismiss-goods').on('click', function() {
            $('.widget_link_back', parent.document).remove();
            $('.widget_link_box', parent.document).remove();
        });
    });
    
    function LoadData() {
        sendAjax.get("widget/getThemePageList", {
            currentPage: currentPage,
            limit: 10,
            channelId: channelId,
            pageId: pageId,
        }, function(res) {
            if (res.code == 1) {
                setHtml(res.page);
            }
        })
    }
    function setHtml(page) {
        //console.log(pageList)
        $("#pagetListd").html("");
        $("#pagediv").html("");
        var pageList = page.pageList;
        if (pageList && pageList.length > 0) {
            var html = "";
            var pageHtml = "";
            for (var i = 0, len = pageList.length; i < len; i++) {
                html += '<tr>' +
                    '<td class="title" style="max-width: 300px;">' +
                    '<div class="td-cont">' +
                    '<a class="new_window" href="javascript:void(0);">' + pageList[i].pageName + '</a>' +
                    '</div>' +
                    '</td>' +
                    '<td class="time">' +
                    '<div class="td-cont">' +
                    '<span>' + pageList[i].createTime + '</span>' +
                    '</div>' +
                    '</td>' +
                    '<td class="opts">' +
                    '<div class="td-cont">' +
                    '<button class="btn js-choose" data-id="' + pageList[i].pageId + '" data-title="' + pageList[i].pageName + '" data-image="' + pageList[i].image + '">选取</button>' +
                    '</div></td></tr>'
            }
            $("#pagetListd").html(html);
            pageHtml += '<span class="total">共' + page.total + ' 条，每页 ' + page.limit + '条</span>';
            if (Number(page.currentPage) > 1) {
                pageHtml += '<a class="fetch_page prev" href="javascript:void(0);" data-page-num="' + (page.currentPage - 1) + '">上一页</a>'
            }
            var pageIndexList = page.pageIndexList;
            for (var i = 0, len = pageIndexList.length; i < len; i++) {
                if (page.currentPage != pageIndexList[i]) {
                    pageHtml += '<a class="fetch_page num" href="javascript:void(0);" data-page-num="' + pageIndexList[i] + '">' + pageIndexList[i] + '</a>';
                } else {
                    pageHtml += '<a class="num active" data-page-num="' + pageIndexList[i] + '" href="javascript:void(0);">' + pageIndexList[i] + '</a>';
                }
            }
            if (Number(page.currentPage) < Number(page.totalPages)) {
                pageHtml += '<a class="fetch_page next" href="javascript:void(0);" data-page-num="' + (page.currentPage + 1) + '">下一页</a>'
            }

            $("#pagediv").html(pageHtml);
        }
    }
</script>

</html>