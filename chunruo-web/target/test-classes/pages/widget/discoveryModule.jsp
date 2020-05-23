<%@ page language="java" import="java.util.*" pageEncoding="utf-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>  
<!doctype html>
<html>

<head>
    <meta charset="utf-8" />
    <title>发现话题标签</title>
    <link href="${ctx }/xedit/css/base.css" type="text/css" rel="stylesheet" />
</head>

<body style="background-color: #ffffff;">
    <div class="modal-header" style="padding: 20px 15px;">
        <a class="close js-news-modal-dismiss">×</a>
        <!-- 顶部tab -->
        <!--<ul class="module-nav modal-tab">
            <li class="active"><a href="javascript:void(0);" class="js-modal-tab">已上架商品</a> |</li>
            <li><a href="goodcat.html">商品分组</a></li>
        </ul>-->
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
                                <div class="td-cont" style="padding: 7px 0 3px 10px;">
                                    <form class="form-search" onsubmit="return false;">
                                        <div class="input-append">
                                            <input class="input-small js-modal-search-input" type="text" style="border-radius: 4px 0px 0px 4px;height:28px;line-height:28px;width:110px;"><a href="javascript:void(0);" class="btn js-fetch-page js-modal-search" style="color: white; border-radius: 0 4px 4px 0; margin-left: 0px;">搜索</a>
                                        </div>
                                    </form>
                                </div>
                            </th>
                        </tr>
                    </thead>
                    <!-- 表格数据区 -->
                    <tbody id="productListd">


                    </tbody>
                </table>
            </div>
        </div>
    </div>
    <div class="modal-footer">
        <div style="display: none;" class="js-confirm-choose left">
            <input type="button" class="btn btn-primary" value="确定使用">
        </div>
        <div class="pagenavi js-page-list" style="margin-top: 0; padding-top: 2px;" id="pagediv">

        </div>
    </div>

</body>
<script type="text/javascript" src="${ctx }/xedit/js/jquery.min.js"></script>
<script type="text/javascript" src="${ctx }/xedit/js/util.js" charset="utf-8"></script>
<script type="text/javascript">
    var currentPage = 1,
        keyword = '';
    $(function() {
        sendAjax.get("widget/getDiscoveryModuleList", {
            currentPage: currentPage,
            limit: 10
        }, function(res) {
            if (res.code == 1) {
                setHtml(res.page);
            }
        });
        $('.js-modal iframe', parent.document).height($('body').height());
        $('.modal-header .close').click(function() {
            parent.login_box_close();
        });
        $('.js-confirm-choose').live('click', function() {
            var data_arr = [];
            $.each($('.js-choose.btn-primary'), function(i, item) {
                data_arr[i] = {
                    'id': $(item).data('id'),
                    'title': $(item).data('title'),
                    'image': $(item).data('image'),
                    'price': $(item).data('price'),
                    'url': $(this).data('id')
                };


            });
            parent.widget_box_after(parent.randNum, data_arr);
        });
        $('.js-page-list a').live('click', function(e) {
            if (!$(this).hasClass('active')) {
                var input_val = $('.js-modal-search-input').val();
                keyword = input_val;
                
                currentPage = $(this).data('page-num');
                LoadData();
            }
        });
        $('.js-modal-search').live('click', function(e) {
            var input_val = $('.js-modal-search-input').val();
            keyword = input_val;
            currentPage = 1;
            LoadData();
            return false;
        });
        $('#js-news-modal-dismiss-goods').on('click', function() {
            $('.widget_link_back', parent.document).remove();
            $('.widget_link_box', parent.document).remove();
        });
    });

    function LoadData() {
        sendAjax.get("widget/getDiscoveryModuleList", {
            currentPage: currentPage,
            limit: 10,
            keyword: keyword,
        }, function(res) {
            if (res.code == 1) {
                setHtml(res.page);
            }
        })
    }

    function setHtml(page) {
        $("#productListd").html("");
        $("#pagediv").html("");
        var pageList = page.pageList;

        if (pageList && pageList.length > 0) {
            var html = "";
            var pageHtml = "";
            for (var i = 0, len = pageList.length; i < len; i++) {
                html += '<tr><td class="title" style="max-width: 300px;">' +
                    '<div class="td-cont">' +
                    '<a class="new_window" href="javascript:void(0);">' + pageList[i].name + '</a>' +
                    '</div>' +
                    '</td> ' +
                    '<td class="time"> ' +
                    '<div class="td-cont"> ' +
                    '<span>' + pageList[i].createTime + '</span>' +
                    '<span></span>' +
                    '</div>' +
                    '</td>' +
                    '<td class="opts">' +
                    '<div class="td-cont">' +
                    '<button class="btn js-choose" onclick="selectProduct(this)" data-id="' + pageList[i].moduleId + '" data-title="' + pageList[i].name + '" data-price="' + pageList[i].price + '" data-image="/upload/' + pageList[i].image + '">选取</button>' +
                    '</div></td></tr>'
            }
            $("#productListd").html(html);
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

    function selectProduct(obj) {
        var type = commFun.getUrlPara("only");
        if (type && type != "null" && type == 1) {
            parent.login_box_after(parent.randNum, 'discoveryModule', $(obj).data('title'), $(obj).data('id'));
        } else {
            if ($(obj).hasClass('btn-primary')) {
                $(obj).removeClass('btn-primary').html('选取');
            } else {
                $(obj).addClass('btn-primary').html('取消');
            }
            if ($('.js-choose.btn-primary').size() > 0) {
                $('.js-confirm-choose').show();
            } else {
                $('.js-confirm-choose').hide();
            }
        }
    }
</script>
</html>