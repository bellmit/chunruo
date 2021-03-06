$(function() {
    $('.usertips').hover(function() { $(this).addClass('current').find('.downmenu1').show(); }, function() { $(this).removeClass('current').find('.downmenu1').hide(); });
});

function layer_tips(msg_type, msg_content) {
    alert(msg_content);
}

function golbal_tips(msg, status) {
    var type = status == 1 ? 'error' : 'success';
    if ($("#infotips").length > 0) $("#infotips").remove();
    var html = '<div class="js-notifications notifications" id="infotips"><div class="alert in fade alert-' + type + '"><a href="javascript:;" class="close pull-right" onclick="$(\'#infotips\').remove();">×</a>' + msg + '</div></div>';
    $('body').append(html);
    $('#infotips').delay(1000).fadeOut(2000);
}
var load_page_cache = [];

function load_page(dom, url, param, cache, obj) {
    if (cache != '' && load_page_cache[cache]) {
        $(dom).html(load_page_cache[cache]);
        if (obj) obj();
    } else {
        $(dom).html('<div class="loading-more"><span></span></div>');
        $(dom).load(url, param, function(response, status, xhr) {
            if (cache != '') load_page_cache[cache] = response;
            if (obj) obj();
        });
    }
}




/////////////
/*
 * 上传图片弹出层
 *
 * param maxsize    最大上传尺寸            int 单位M
 * param showLocal  是否展示已上传图片列表  bool
 * param obj 	    回调函数                object
 * param maxnum     最多使用的图片数量      int
 */
var upload_local_result = [];

function upload_pic_box(maxsize, showLocal, obj, maxnum) {
    var upload_pic = [],
        oknum = 0,
        nowImagePage = 1;
    if (!showLocal) showLocal = false;
    if (!maxnum) maxnum = 0;

    var html = '<div class="modal-backdrop fade in"></div>';
    var widgetDom = $('<div class="widget-image modal fade in" style="top:-350px;"><div class="modal-header" style="padding: 20px 15px;"><a class="close" data-dismiss="modal">×</a><ul class="module-nav modal-tab js-modal-tab" style="display:none;"><li class="js-modal-tab-item js-modal-tab-image' + (showLocal ? '' : ' hide') + '"><a href="javascript:;" data-pane="image">用过的图片</a><span>|</span></li><li class="js-modal-tab-item js-modal-tab-upload active"><a href="javascript:;" data-pane="upload">新图片</a></li></ul></div>' + (showLocal ? '<div class="tab-pane js-tab-pane js-tab-pane-image js-image-region hide"><div class="widget-list"><div class="modal-body"><div class="js-list-filter-region clearfix ui-box" style="position:relative;min-height:28px;"><div class="widget-list-filter"><div class="widget-image-refresh"><span>点击图片即可选中</span> <a href="javascript:;" class="js-refresh">刷新</a></div><div class="js-list-search ui-search-box"><input class="txt" type="text" placeholder="搜索" value=""/></div></div></div><div class="ui-box"><ul class="js-list-body-region widget-image-list"></ul><div class="js-list-empty-region"><div><div class="no-result widget-list-empty">还没有相关数据。</div></div></div></div></div><div class="modal-footer js-list-footer-region"><div class="widget-list-footer"><div class="left"><a href="javascript:;" class="ui-btn ui-btn-primary js-choose-image hide">确定使用</a></div><div class="pagenavi"></div></div></div></div></div>' : '') + '<div class="tab-pane js-tab-pane js-tab-pane-upload js-upload-region"><div>' + '<div class="js-upload-network-region"><div><div class="modal-body"  style="display:none;"><div class="get-web-img js-get-web-img"><form class="form-horizontal" onsubmit="return false;"><div class="control-group"><label class="control-label">网络图片：</label><div class="controls"><input type="text" name="attachment_url" class="get-web-img-input js-web-img-input" placeholder="请贴入网络图片地址" value=""><input type="button" class="btn js-upload-network-img" value="提取"/></div><div class="controls preview-container"></div></div></form></div></div></div></div>' + '<div class="js-upload-local-region"><div><div class="modal-body" ><div class="upload-local-img"><form class="form-horizontal"><div class="control-group"><label class="control-label">本地图片：</label><div class="controls"><div class="control-action"><ul class="js-upload-image-list upload-image-list clearfix ui-sortable"><li class="fileinput-button js-add-image" data-type="loading"><a class="fileinput-button-icon" href="javascript:;">+</a></li></ul><p class="help-desc">推荐960*960宽高等比的图片会有更好的展示效果</p><p class="help-desc">最大支持 1 MB 的图片( jpg / gif / png )，不能选中大于 1 MB 的图片</p></div></div></div></form></div></div><div class="modal-footer"><div class="modal-action right"><input type="button" class="btn btn-primary js-upload-image-btn" value="上传完成"/></div></div></div></div></div></div></div>');

    $(".js-add-image").live("click", function() {
        if ($(this).data("type") == "loading") {
            //layer_tips(1, "网速慢，加载中，请稍等");
            return;
        }
    });

    widgetDom.find('.close,.js-upload-image-btn').click(function() {
        if (!$(this).hasClass('close')) {
            //if(obj) obj(upload_pic);
            if (obj) {
                var pic_arr = [];
                $(".js-upload-image-list").find("img").each(function() {
                    pic_arr.push($(this).attr("src"));
                });
                obj(pic_arr);
            }
        }
        $('.widget-image,.modal-backdrop').animate({ 'margin-top': '-' + ($(window).scrollTop() + $(window).height()) + 'px' }, "slow", function() {
            $('.widget-image,.modal-backdrop').remove();
        });
    });
    $('.js-upload-image-list .js-remove-image').live('click', function() {
        $.post('./user.php?c=attachment&a=attachment_del', { pigcms_id: $(this).attr('file-id') });
        $(this).closest('li').remove();

    });

    //回车提交搜索
    $(window).keydown(function(event) {
        if (event.keyCode == 13 && widgetDom.find(".js-list-search input").is(':focus')) {
            var keyword = widgetDom.find(".js-list-search input").val();
            var old_keyword = widgetDom.find(".js-list-search input").data("old_keyword");

            if (typeof old_keyword == "undefined") {
                widgetDom.find(".js-list-search input").data("old_keyword", "");
                old_keyword = "";
            }

            if (old_keyword == keyword) {
                return;
            }
            widgetDom.find(".js-list-search input").data("old_keyword", keyword);
            getLocalFun(-1);
        }
    })

    var getLocalFun = function(page) {
        // if (page == -1) {
        //     upload_local_result = [];
        //     nowImagePage = 1;
        //     page = 1;
        // }
        // //var keyword = widgetDom.find(".js-list-search input").val();
        // $.post('/user/attachment/getAttachmentList', { currentPage: page, storeId: 1, limit: 18 }, function(result) {
        //     console.log(result);
        //     if (!upload_local_result[page]) {
        //         upload_local_result[page] = {};
        //     }
        //     upload_local_result[page] = result;
        //     showLocalFun();
        // });
    }
    var showLocalFun = function() {
        var page = upload_local_result[nowImagePage]
        if (page.total) {
            widgetDom.find('.js-list-empty-region').empty();
            var html = '';
            for (var i in upload_local_result[nowImagePage].pageList) {
                var nowImage = upload_local_result[nowImagePage].pageList[i];
                var selected = "";
                if (typeof upload_pic[nowImage.storeAttachmentId] != "undefined") {
                    selected = "selected";
                }

                html += '<li class="widget-image-item ' + selected + '" data-id="' + nowImage.storeAttachmentId + '" data-image="' + nowImage.filePath + '"><div class="js-choose" title="' + nowImage.name + '"><p class="image-size">' + nowImage.size + '<br>' + getSize(nowImage.size) + '</p><div class="widget-image-item-content" style="background-image:url(' + nowImage.filePath + ')"></div><div class="widget-image-meta">' + nowImage.width + 'x' + nowImage.height + '</div><div class="selected-style"><i class="icon-ok icon-white"></i></div></div></li>';
            }
            widgetDom.find('.js-list-body-region').html(html);

            var pageHtml = '<span class="total">共' + page.total + ' 条，每页 ' + page.limit + '条</span>';
            if (page.currentPage > 1) {
                pageHtml += '<a class="fetch_page prev" href="javascript:void(0);" ata-page-num="' + (page.currentPage - 1) + '">上一页</a>'
            }
            var pageIndexList = upload_local_result[nowImagePage].pageIndexList;
            for (var i in pageIndexList) {
                var index = pageIndexList[i];
                if (page.currentPage != index)
                    pageHtml += '<a class="fetch_page num" href="javascript:void(0);" data-page-num="' + index + '" >' + index + '</a>'
                else
                    pageHtml += '<a class="num active"  data-page-num="' + index + '" href="javascript:void(0);">' + index + '</a>'
            }
            if (page.totalPages > page.currentPage)
                pageHtml += ' <a class="fetch_page next" href="javascript:void(0);"  data-page-num="' + (page.currentPage + 1) + '">下一页</a>'


            widgetDom.find('.pagenavi').html(pageHtml);

            widgetDom.find('.pagenavi a').click(function() {
                nowImagePage = $(this).data('page-num');
                if (upload_local_result[nowImagePage]) {
                    showLocalFun();
                } else {
                    getLocalFun(nowImagePage);
                }
            });

            if (maxnum == 1) {
                widgetDom.find('.widget-image-item').click(function() {
                    upload_pic[$(this).data('id')] = $(this).data('image');
                    if (obj) obj(upload_pic);
                    $('.widget-image,.modal-backdrop').remove();
                });
            } else {
                widgetDom.find('.widget-image-item').click(function() {
                    if ($(this).hasClass('selected')) {
                        $(this).removeClass('selected');
                        delete upload_pic[$(this).data('id')];
                        if (widgetDom.find('.widget-image-item.selected').size() == 0) {
                            widgetDom.find('.js-choose-image').addClass('hide');
                        }
                    } else {
                        if (maxnum > 0 && widgetDom.find('.widget-image-item.selected').size() >= maxnum) {
                            layer_tips(1, '最多只能选取 ' + maxnum + ' 张');
                        } else {
                            widgetDom.find('.js-choose-image').removeClass('hide');
                            $(this).addClass('selected');
                            upload_pic[$(this).data('id')] = $(this).data('image');
                        }
                    }
                });
            }
        } else {
            widgetDom.find('.js-list-body-region').empty();
            widgetDom.find('.pagenavi').empty();
            widgetDom.find('.js-list-empty-region').html('<div><div class="no-result widget-list-empty">还没有相关数据。</div></div>');
        }
    }

    widgetDom.find('.js-choose-image').click(function() {
        if (obj) {
            var pic_arr = upload_pic.reverse();
            obj(pic_arr);
        }
        $('.widget-image,.modal-backdrop').remove();
    });

    if (showLocal) {
        if (upload_local_result.length == 0) {
            getLocalFun(nowImagePage);
        } else {
            showLocalFun();
        }
        widgetDom.find('.js-modal-tab a').click(function() {
            if (!$(this).closest('li').hasClass('active')) {
                $(this).closest('li').addClass('active').siblings('li').removeClass('active');
                $('.js-tab-pane-' + $(this).data('pane')).removeClass('hide').siblings('.js-tab-pane').addClass('hide');
            }
        });
        widgetDom.find('.js-image-region .js-refresh').click(function() {
            getLocalFun(-1);
        });
    }
    var imageBtnDom = widgetDom.find('.js-upload-network-img');
    var imageDom = widgetDom.find('.js-web-img-input');
    var imageUrlError = function(tips) {
        layer_tips(1, tips);
        imageDom.focus();
        imageBtnDom.val('提取').prop('disabled', false);
    }
    imageBtnDom.click(function() {
        $(this).val('提取中...').prop('disabled', true);
        var imageUrl = $.trim(imageDom.val());
        if (imageUrl.length == 0) {
            imageUrlError('请填写网址');
            return false;
        }
        var lastDotIndex = imageUrl.lastIndexOf('.');
        if (imageUrl.substr(0, 7) != 'http://' && imageUrl.substr(0, 8) != 'https://' && lastDotIndex == -1) {
            imageUrlError('请填写正确的网址，应以(http://或https://)开头');
            return false;
        }
        var extName = imageUrl.substr(lastDotIndex + 1);
        if (extName != 'gif' && extName != 'jpg' && extName != 'png' && extName != 'jpeg') {
            imageUrlError('为了网站安全考虑，<br/>网址应以(gif、jpg、png或jpeg)结尾');
            return false;
        }
        var image = new Image();
        image.src = imageUrl;
        image.onerror = function() {
            imageUrlError('该网址不存在，或不是一张合法的图片文件！');
            return false;
        }
        image.onload = function() {
            widgetDom.find('.preview-container').html('<img src="' + imageUrl + '"/>');
            $.post('./user.php?c=attachment&a=img_download', { url: imageUrl }, function(result) {
                if (result.err_code) {
                    imageUrlError(result.err_msg);
                } else {
                    imageBtnDom.val('提取').prop('disabled', false);
                    upload_pic[result.err_msg.pigcms_id] = result.err_msg.url;
                    if (obj) obj(upload_pic);
                    $('.widget-image,.modal-backdrop').remove();
                }
            });
        }
    });

    $('body').append(html);
    $('body').append(widgetDom);
    widgetDom.animate({
        'top': ($(window).scrollTop() + $(window).height() * 0.2) + 'px'
    }, 100);
    $.getScript('./xedit/js/webuploader.js', function() {
        $(".js-add-image").data("type", "load");
        if (!WebUploader.Uploader.support()) {
            alert('您的浏览器不支持上传功能！如果你使用的是IE浏览器，请尝试升级 flash 播放器');
            $('.widget-image,.modal-backdrop').remove();
        }
        var uploader = WebUploader.create({
            auto: true,
            swf: './xedit/js/Uploader.swf',
            server: "/widget/uploadImage",
            pick: {
                id: '.js-add-image',
                innerHTML: '<a class="fileinput-button-icon" href="javascript:;">+</a>'
            },
            accept: {
                title: 'Images',
                extensions: 'gif,jpg,jpeg,png',
                mimeTypes: 'image/*'
            },
            fileSingleSizeLimit: maxsize * 1024 * 1024,
            duplicate: true
        });
        uploader.on('fileQueued', function(file) {
            var pic_loading_dom = $('<li class="upload-preview-img sort loading uploadpic-' + file.id + '">');
            $('.js-add-image').before(pic_loading_dom);
        });
        uploader.on('uploadProgress', function(file, percentage) {

        });
        uploader.on('uploadBeforeSend', function(block, data) {
            data.maxsize = maxsize;
        });
        uploader.on('uploadSuccess', function(file, response) {
            // console.log(response);
            if (response.code == '1') {
                upload_pic[1] = response.url;
                $('.uploadpic-' + file.id).removeClass('loading').html('<img src="' + response.url + '"/><a href="javascript:;" class="close-modal small js-remove-image" file-id="' + response.id + '">×</a>');
                if (maxnum == 1 && oknum == 0 && obj) {
                    obj(upload_pic);
                    $('.widget-image,.modal-backdrop').remove();
                }
                oknum++;
            } else {
                $('.uploadpic-' + file.id).remove();
                layer_tips(1, response.msg);
            }
        });

        uploader.on('uploadError', function(file, reason) {
            $('.uploadpic-' + file.id).remove();
            layer_tips(1, '上传失败！请重试。');
        });

    });

}


/*
 * 小的弹出层
 *
 * param dom	  弹出层的ID 				使用 $(this);
 * param e	      弹出层的ID点击返回事件 	使用 event;
 * param position 方向  					left,top,right,bottom
 * param type     弹出层的类别  			copy,edit_txt,delete,confirm,multi_txt,radio,input,url,module
 * param content  内容
 * param ok_obj   点击确认键的回调方法
 * param placeholder 点位符
 */
function button_box(dom, event, position, type, content, ok_obj, placeholder) {
    event.stopPropagation();
    var left = 0,
        top = 0,
        width = 0,
        height = 0;
    var dom_offset = dom.offset();
    $('.popover').remove();
    if (type == 'copy') {
        $.getScript('./js/plugin/jquery.zclip.min.js', function() {
            $('body').append('<div class="popover ' + position + '" style="left:-' + ($(window).width() * 5) + 'px;top:' + $(window).scrollTop() + ($(window).height() / 2) + 'px;"><div class="arrow"></div><div class="popover-inner"><div class="popover-content"><div class="form-inline"><div class="input-append"><input type="text" class="txt js-url-placeholder url-placeholder" readonly="" value="' + content + '"/><button type="button" class="btn js-btn-copy">复制</button></div></div></div></div></div>');
            $('.popover .js-btn-copy').zclip({
                path: './js/plugin/ZeroClipboard.swf',
                copy: function() {
                    return content;
                },
                afterCopy: function() {
                    $('.popover').remove();
                    layer_tips(0, '复制成功');
                }
            });
            button_box_after();
        });
    } else if (type == 'edit_txt') {
        $('body').append('<div class="popover ' + position + '" style="left:-' + ($(window).width() * 5) + 'px;top:' + $(window).scrollTop() + 'px;"><div class="arrow"></div><div class="popover-inner popover-rename"><div class="popover-content"><div class="form-horizontal"><div class="control-group"><div class="controls"><input type="text" class="js-rename-placeholder" maxlength="256"/> <button type="button" class="btn btn-primary js-btn-confirm">确定</button> <button type="reset" class="btn js-btn-cancel">取消</button></div></div></div></div></div></div>');
        $('.js-rename-placeholder').attr('placeholder', content).focus();
        button_box_after();
    } else if (type == 'input') {
        $('body').append('<div class="popover ' + position + '" style="left:-' + ($(window).width() * 5) + 'px;top:' + $(window).scrollTop() + 'px;"><div class="arrow"></div><div class="popover-inner popover-rename"><div class="popover-content"><div class="form-horizontal"><div class="control-group"><div class="controls"><input type="text" class="js-rename-placeholder" maxlength="256"/> <button type="button" class="btn btn-primary js-btn-confirm">确定</button> <button type="reset" class="btn js-btn-cancel">取消</button></div></div></div></div></div></div>');
        if (placeholder) {
            $('.js-rename-placeholder').attr('placeholder', placeholder);
        }
        $('.js-rename-placeholder').val(content).focus();
        button_box_after();
    } else if (type == 'multi_txt') {
        $('body').append('<div class="popover ' + position + '" style="left:-' + ($(window).width() * 5) + 'px;top:' + $(window).scrollTop() + 'px;"><div class="arrow"></div><div class="popover-inner popover-chosen"><div class="popover-content"><div class="select2-container select2-container-multi js-select2 select2-dropdown-open" style="width:242px;display:inline-block;"><ul class="select2-choices"><li class="select2-search-field">    <input type="text" autocomplete="off" autocorrect="off" autocapitalize="off" spellcheck="false" class="select2-input" id="s2id_autogen26" tabindex="-1" style="width:192px;"></li></ul></div> <button type="button" class="btn btn-primary js-btn-confirm" data-loading-text="确定">确定</button> <button type="reset" class="btn js-btn-cancel">取消</button></div></div></div>');
        $('.popover-chosen .select2-input').attr('placeholder', content).focus();
        multi_choose_obj();
        button_box_after();
    } else if (type == 'multi_txt2') {
        var cccat_id = content.cats_id;
        $('body').append('<div class="popover ' + position + '" style="left:-' + ($(window).width() * 5) + 'px;top:' + $(window).scrollTop() + 'px;"><div class="arrow"></div><div class="popover-inner popover-chosen"><div class="popover-content"><div class="select2-container select2-container-multi js-select2 select2-dropdown-open" style="width:242px;display:inline-block;"><ul class="select2-choices"><li class="select2-search-field">    <input type="text" autocomplete="off" autocorrect="off" autocapitalize="off" spellcheck="false" class="select2-input" id="s2id_autogen26" tabindex="-1" style="width:192px;"></li></ul></div> <button type="button" data-button-cat-id="' + cccat_id + '"  class="btn btn-primary js-btn-confirm" data-loading-text="确定">确定</button> <button type="reset" class="btn js-btn-cancel">取消</button></div></div></div>');
        $('.popover-chosen .select2-input').attr('placeholder', content.contents).focus();
        // multi_choose_obj();
        multi_choose_obj2(content.arr, content.has_atom_id);
        button_box_after();
    } else if (type == 'radio') {
        $('body').append('<div class="popover ' + position + '" style="top: ' + $(window).scrollTop() + 'px; left: -' + ($(window).width() * 5) + 'px;"><div class="arrow"></div><div class="popover-inner popover-change"><div class="popover-content text-center"><form class="form-inline"><label class="radio"><input type="radio" name="discount" value="1" checked="">参与</label><label class="radio"><input type="radio" name="discount" value="0">不参与</label><button type="button" class="btn btn-primary js-btn-confirm" data-loading-text="确定">确定</button><button type="reset" class="btn js-btn-cancel">取消</button></form></div></div></div>');
        button_box_after();
    } else if (type == 'url') {
        var yinxiao_btn = '';
        if (is_show_activity == 1) {
            yinxiao_btn = '<button type="button" class="btn js-btn-link">营销活动</button>';
        }
        var button_h = $('<div class="popover ' + position + '" style="left:-' + ($(window).width() * 5) + 'px;top:' + $(window).scrollTop() + 'px;"><div class="arrow"></div><div class="popover-inner popover-rename"><div class="popover-content"><div class="form-horizontal"><div class="control-group"><div class="controls"><input type="text" class="link-placeholder js-link-placeholder" placeholder="' + content + '" /> ' + yinxiao_btn + ' <button type="button" class="btn btn-primary js-btn-confirm">确定</button> <button type="reset" class="btn js-btn-cancel">取消</button></div></div></div></div></div></div>');
        button_h.find('.js-btn-link').click(function() {
            $.layer({
                type: 2,
                title: '插入功能库链接',
                shadeClose: true,
                maxmin: true,
                fix: false,
                area: ['600px', '450px'],
                iframe: {
                    src: '?c=link&a=index'
                }
            });
        });
        $('body').append(button_h);
        $('.js-rename-placeholder').val(content).focus();
        button_box_after();
    } else if (type == 'module') {
        $('body').append('<div class="popover ' + position + '"z-index: 9999999; style="left:' + (dom_offset.left - 178) + 'px;top:' + (dom_offset.top - 500) + 'px;"><div class="arrow"></div><div class="popover-inner popover-text"><div class="popover-content"><form class="form-horizontal"><div class="control-group"><label class="control-label">请设置模块名称：</label><div class="controls"><input type="text" class="text-placeholder js-text-placeholder"></div></div><div class="form-actions"><button type="button" class="btn btn-primary js-btn-confirm" data-loading-text="确定"> 确定</button><button type="reset" class="btn js-btn-cancel">取消</button></div></form></div></div></div>');
        $('.js-text-placeholder').focus();
        $('.js-text-placeholder').val(content);
        button_box_after();
        $('.popover').css({ top: (dom_offset.top - dom.height() - 115), left: dom_offset.left - ($('.popover').width() / 2) + 20 });
    } else if (type == 'tips') {
        $('body').append('<div class="popover ' + position + '" style="z-index: 9999999;display:block;left:-' + ($(window).width() * 5) + 'px;top:' + $(window).scrollTop() + 'px;"><div class="arrow"></div><div class="popover-inner popover-' + type + '"><div class="popover-content text-center"><div class="form-inline"><span class="help-inline item-delete">' + content + '</span><button type="button" class="btn btn-primary js-btn-confirm">确定</button> </div></div></div></div>');
        button_box_after();
    } else {
        $('body').append('<div class="popover ' + position + '" style="z-index: 9999999;display:block;left:-' + ($(window).width() * 5) + 'px;top:' + $(window).scrollTop() + 'px;"><div class="arrow"></div><div class="popover-inner popover-' + type + '"><div class="popover-content text-center"><div class="form-inline"><span class="help-inline item-delete">' + content + '</span><button type="button" class="btn btn-primary js-btn-confirm">确定</button> <button type="reset" class="btn js-btn-cancel">取消</button></div></div></div></div>');
        button_box_after();
    }

    function button_box_after() {
        $('.popover .js-btn-cancel').one('click', function() {
            close_button_box();
        });
        $('.popover .js-btn-confirm').one('click', function() {
            if (ok_obj) {
                ok_obj();
            } else {
                close_button_box();
            }
        });
        $('.popover').click(function(e) {
            e.stopPropagation();
        });
        $('body').bind('click', function() {
            close_button_box();
        });

        var popover_height = $('.popover').height();
        var popover_width = $('.popover').width();
        switch (position) {
            case 'left':
            	
                $('.popover').css({ top: dom_offset.top - (popover_height + 10 - dom.height()) / 2, left: dom_offset.left - popover_width - 14 });
                break;
            case 'right':
                $('.popover').css({ top: dom_offset.top - (popover_height + 10 - dom.height()) / 2, left: dom_offset.left + dom.width() + 27 });
                $('.popover-confirm').css('margin-left', '0');
                break;
            case 'top':
                $('.popover').css({ top: (dom_offset.top - dom.height() - 40), left: dom_offset.left - (popover_width / 2) + (dom.width() / 2) });
                break;
            case 'bottom':
                $('.popover').css({ top: dom_offset.top + dom.height() - 3, left: dom_offset.left - (popover_width / 2) + (dom.width() / 2) });
                break;
        }
    }
    //添加商品添加规格专用方法
    function multi_choose_obj() {
        $('.popover-chosen .select2-input').keyup(function(event) {
            var input_select2 = $.trim($(this).val());
            if (event.keyCode == 13 && input_select2.length != 0) {
                var html = $('<li class="select2-search-choice"><div>' + input_select2 + '</div><a href="#" class="select2-search-choice-close" tabindex="-1" onclick="$(this).closest(\'li\').remove();$(\'.popover-chosen .select2-input\').focus();"></a></li>');
                if ($('.popover-chosen .select2-choices .select2-search-choice').size() > 0) {
                    var has_li = false;
                    $.each($('.popover-chosen .select2-choices .select2-search-choice'), function(i, item) {
                        if ($(item).find('div').html() == input_select2) {
                            has_li = true;
                            return false;
                        }
                    });
                    if (has_li === false) {
                        $('.popover-chosen .select2-choices .select2-search-choice:last').after(html);
                    } else {
                        layer_tips(1, '已经存在相同的规格');
                        $(this).val('').focus();
                        return;
                    }
                } else {
                    $('.popover-chosen .select2-choices').prepend(html);
                }

                var r = getRandNumber();
                html.attr('data-vid', r);
                html.attr('check-data-vid', r);


                $(this).removeAttr('placeholder').val('').focus();
            }
        });
    }
    //查询商品属性规格专用方法  array(1,2,3)
    function multi_choose_obj2(strss, arr_has_atom_id) {

        var html;
        $('.popover-chosen .select2-choices .select2-search-choice').detach('');
        for (var i in strss) {
            // html +=  '<li class="select2-search-choice"  onclick="$(this).addClass(\'choice\');"  data-vid='+strss[i].pid+'"><div>'+strss[i].value+'</div><a href="#" class="select2-search-choice-select" tabindex="-1"  onclick="$(\'.popover-chosen .select2-input\').focus();"></a></li>';
            if (jQuery.inArray(strss[i].vid, arr_has_atom_id) == '-1') {
                html += '<li class="select2-search-choice cursor"  onclick="javascript:if($(this).attr(\'idd\')==\'choice\'){ $(this).removeClass(\'choice\').attr(\'idd\',\'\'); } else{$(this).addClass(\'choice\').attr(\'idd\',\'choice\');}"  data-vid=' + strss[i].vid + '"><div>' + strss[i].value + '</div><a href="javascript:" class="select2-search-choice-select" tabindex="-1"  onclick="$(\'.popover-chosen .select2-input\').focus();"></a></li>';
            }
        }
        var htmls = $(html);

        $('.popover-chosen .select2-choices').prepend(htmls);
        //包所有属性值 放入 容器中
        $('.popover-chosen .select2-input').keyup(function(event) {


        })
    }
}

function copy_txt(dom, txt, after_obj) {
    $.getScript('./static/js/plugin/jquery.zclip.min.js', function() {
        dom.zclip({
            path: './static/js/plugin/ZeroClipboard.swf',
            copy: function() {
                return txt;
            },
            afterCopy: function() {
                if (after_obj) after_obj();
            }
        });
    });
}

function close_button_box() {
    $('.popover').remove();
    setTimeout(function() {
        $('.notifications').remove();
    }, 2000)
}

/**
 * 链接弹出层
 */
var link_save_box = {};

function link_box(dom,type, typeArr, after_obj) {
    var domHtml;
    dom.hover(function() {
        if (dom.find('.dropdown-menu').size() == 0) {
            if (typeArr.length == 0) {
                //domHtml = $('<ul class="dropdown-menu" style="display:block;"><li><a data-type="page" href="javascript:;">微页面及分类</a></li><li><a data-type="good" href="javascript:;">商品及分组</a></li><li><a data-type="home" href="javascript:;">店铺主页</a></li><li> <a data-type="link" href="javascript:;">自定义外链</a></li></ul>');
                  if(type == 2){
                  	domHtml = $('<ul class="dropdown-menu" style="display:block;"><li><a data-type="theme_only" href="javascript:;">专题</a></li></ul>');
                  }else if(type == 3){
                  	domHtml = $('<ul class="dropdown-menu" style="display:block;"><li><a data-type="good_only" href="javascript:;">商品</a></li></ul>');
                  }else{
                  	domHtml = $('<ul class="dropdown-menu" style="display:block;"><li><a data-type="good_only" href="javascript:;">商品</a></li><li><a data-type="category_only" href="javascript:;">分类</a></li></ul>');
                  }
                
            } else {
                var domContent = '<ul class="dropdown-menu" style="display:block;">';
                for (var i in typeArr) {
                    domContent += '<li><a data-type="' + typeArr[i] + '" href="javascript:;">';
                    switch (typeArr[i]) {
                        case 'page':
                        case 'pagecat':
                            domContent += '页面';
                            break;
                        case 'page_only':
                            domContent += '页面';
                            break;
                        case 'pagecat_only':
                            domContent += '页面';
                            break;
                        case 'good':
                        case 'goodcat':
                            domContent += '商品';
                            break;
                        case 'good_only':
                            domContent += '商品';
                            break;
                        case 'goodcat_only':
                            domContent += '商品';
                            break;
                        case 'theme':
                        case 'theme_only':
                        	domContent += '专题';
                            break;
                        case 'package':
                        case 'package_only':
                        	domContent += '礼包';
                            break;
                        case 'category':
                        case 'category_only':
                        	domContent += '分类';
                            break;
                        case 'award':
                        case 'award_only':
                        	domContent += '奖励中心';
                            break;
                        case 'mini':
                        case 'mini_only':
                        	domContent += '小程序';
                            break;
                        case 'web':
                        case 'web_only':
                        	domContent += 'H5';
                            break;
                        case 'discovery':
                        case 'discovery_only':
                        	domContent += '发现详情';
                            break;
                        case 'discoveryModule':
                        case 'discoveryModule_only':
                        	domContent += '发现话题标签';
                            break;
                        case 'discoveryCreater':
                        case 'discoveryCreater_only':
                        	domContent += '发现主体';
                            break;
                        case 'brand':
                        case 'brand_only':
                        	domContent += '品牌详情';
                            break;
                        case 'invite':
                        case 'invite_only':
                        	domContent += '邀请有礼';
                            break;
                        case 'home':
                            domContent += '店铺主页';
                            break;
                        case 'ucenter':
                            domContent += '会员主页';
                            break;
                        case 'link':
                            domContent += '自定义外链';
                            break;
                    }
                    domContent += '</a></li>';
                }
                domContent += '</ul>';
                domHtml = $(domContent);
            }
            dom.append(domHtml);
        } else {
            domHtml = dom.find('.dropdown-menu');
            domHtml.show();
        }
        var modalDom = {};
        domHtml.find('a').bind('click', function() {
            var type = $(this).data('type');
            if (type == 'home') {
                after_obj('home', '店铺主页', '店铺主页', wap_home_url);
                domHtml.trigger('mouseleave');
            } else if (type == 'ucenter') {
                after_obj('home', '会员主页', '会员主页', wap_ucenter_url);
                domHtml.trigger('mouseleave');
            } else if (type == 'link') {
                button_box(dom, event, 'bottom', 'url', '链接地址：http://example.com', function() {
                    var url = $('.js-link-placeholder').val();
                    if (url != '') {
                        if (!check_url(url)) {
                            url = 'http://' + url;
                        }
                        after_obj('link', '外链', url, url);
                        close_button_box();
                    } else {
                        return false;
                    }
                });
                domHtml.trigger('mouseleave');
            } else {
                $('.modal-backdrop,.modal').remove();
                $('body').append('<div class="modal-backdrop fade in widget_link_back"></div>');
                randNum = getRandNumber();
                if (type.substr(-4, 4) == 'only') {
                    var load_url =  '/widget/' + type.replace('_only', '') + '.html?only=1&channelId='+window.wchannelId+'&pageId='+window.wpageId;
                } else {
                    var  load_url = '/widget/' + type + '.html?only=1&channelId='+window.wchannelId+'&pageId='+window.wpageId;
                }
                widget_link_save_box[randNum] = after_obj;
	                modalDom = $('<div class="modal fade hide js-modal in widget_link_box" aria-hidden="false" style="margin-top:0px;display:block;z-index: 9999999;"><iframe src="' + load_url + '" style="width:100%;height:200px;border:0;-webkit-border-radius:6px;-moz-border-radius:6px;border-radius:6px;"></iframe></div>');
                $('body').append(modalDom);
                //modalDom.animate({'margin-top': ($(window).scrollTop() + $(window).height() * 0.05) + 'px'}, "slow");
                $('.modal-backdrop').click(function() {
                    login_box_close();
                });
            }
        });
    }, function(e) {
        domHtml.hide().find('a').unbind('click');
    });
}

function login_box_after(number, type, title, url,image) {
    var prefix = '';
    switch (type) {
        case 'page':
            prefix = '页面';
            break;
        case 'pagecat':
            prefix = '页面';
            break;
        case 'goodcat':
            prefix = '商品分组';
            break;
        case 'good':
            prefix = '商品';
            break;
        case 'theme':
            prefix = '专题';
            break;
        case 'package':
            prefix = '礼包';
            break;
        case 'category':
            prefix = '分类';
            break;
        case 'award':
            prefix = '奖励中心';
            break;
        case 'mini':
            prefix = '小程序';
            break;
        case 'web':
            prefix = 'H5';
            break;
        case 'discovery':
            prefix = '发现详情';
            break;
        case 'discoveryModule':
            prefix = '发现话题标签';
            break;
        case 'discoveryCreater':
            prefix = '发现主体';
            break;
        case 'brand':
            prefix = '品牌详情';
            break;
        case 'invite':
            prefix = '邀请有礼';
            break;
    }
    widget_link_save_box[number](type, prefix, title, url,image);
    login_box_close();
}

function login_box_close() {
    $('.widget_link_box').animate({ 'margin-top': '-' + ($(window).scrollTop() + $(window).height()) + 'px' }, "slow", function() {
        $('.widget_link_back,.widget_link_box').remove();
    });
}

/**
 * 挂件选择弹出层
 */
var widget_link_save_box = {};

function widget_link_box(dom, type, after_obj) {
    dom.click(function() {
        $('.modal-backdrop,.modal').remove();
        $('body').append('<div class="modal-backdrop fade in widget_link_back"></div>');
        randNum = getRandNumber();
        var load_url = '/widget/' + type + '.html';
        widget_link_save_box[randNum] = after_obj;
        var html = '<div class="modal fade hide js-modal in widget_link_box" aria-hidden="false" style="margin-top:0px;display:block;"> <iframe src="' + load_url + '" style="width:100%;height:200px;border:0;-webkit-border-radius:6px;-moz-border-radius:6px;border-radius:6px;"></iframe></div>';
        modalDom = $(html);
        $('body').append(modalDom);
        //modalDom.animate({'margin-top': ($(window).scrollTop() + $(window).height() * 0.05) + 'px'}, "slow");
        $('.modal-backdrop').click(function() {
            login_box_close();
        });
    });
}

/**
 * 挂件选择弹出层2 非传递 仅仅显示自营的 未售完de商品
 */
var widget_link_save_box = {};
//
//function widget_link_box1(dom, type, after_obj) {
//    dom.live("click", function() {
//        $('.modal-backdrop,.modal').remove();
//        $('body').append('<div class="modal-backdrop fade in widget_link_back"></div>');
//        var randNum = getRandNumber();
//        var load_url = 'user/widget/' + type + '.html';
//        widget_link_save_box[randNum] = after_obj;
//        modalDom = $('<div class="modal fade hide js-modal in widget_link_box" aria-hidden="false" style="margin-top:0px;display:block;"><iframe src="' + load_url + '" style="width:100%;height:200px;border:0;-webkit-border-radius:6px;-moz-border-radius:6px;border-radius:6px;"></iframe></div>');
//        $('body').append(modalDom);
//        //  modalDom.animate({'margin-top': ($(window).scrollTop() + $(window).height() * 0.05) + 'px'}, "slow");
//        $('.modal-backdrop').click(function() {
//            login_box_close();
//        });
//    });
//}

/**
 * 挂件选择弹出层优惠券 非传递 仅仅显示优惠券
 */
var widget_link_save_box = {};

//function widget_link_yhq(dom, type, after_obj) {
//    dom.click(function() {
//        $('.modal-backdrop,.modal').remove();
//        $('body').append('<div class="modal-backdrop fade in widget_link_back"></div>');
//        var randNum = getRandNumber();
//        var load_url = 'user.html?a=widget/' + type + '&type=more&number=' + randNum;
//        widget_link_save_box[randNum] = after_obj;
//        //modalDom = $('<div class="modal fade hide js-modal in widget_link_box" aria-hidden="false" style="margin-top:0px;display:block;"><iframe src="'+load_url+'" style="width:100%;height:200px;border:0;-webkit-border-radius:6px;-moz-border-radius:6px;border-radius:6px;"></iframe></div>');
//        $('body').append(modalDom);
//        //modalDom.animate({'margin-top': ($(window).scrollTop() + $(window).height() * 0.05) + 'px'}, "slow");
//        $('.modal-backdrop').click(function() {
//            login_box_close();
//        });
//    });
//}




function widget_box_after(number, data) {
    var value = $('input[name="size"]:checked').val();
    var html = '';
    if (value == 0) {
        for (item in data) {

            html += '<li class="goods-card big-pic card"><a href="javascript:void(0);" class="link js-goods clearfix"><div class="photo-block"><img class="goods-photo js-goods-lazy" src="' + data[item].image + '"/></div>';
            html += '<div class="info clearfix  info-price"><p class="goods-title">' + data[item].title + '</p><p class="goods-price goods-price-icon"><em>￥' + data[item].price + '</em></p><p class="goods-price-taobao"></p></div>';
            html += '</a></li>';
        }
        $(".editing").find("ul").eq(0).append(html);
    } else {
        for (item in data) {
            // console.log(item);
            html += '<li class="goods-card small-pic card"><a href="javascript:void(0);" class="link js-goods clearfix"><div class="photo-block"><img class="goods-photo js-goods-lazy" src="' + data[item].image + '"/></div>';
            html += '<div class="info clearfix  info-price"><p class="goods-title">' + data[item].title + '</p><p class="goods-price goods-price-icon"><em>￥' + data[item].price + '</em></p><p class="goods-price-taobao"></p></div>';
            html += '</a></li>';
        }
        $(".editing").find("ul").eq(0).append(html);
    }
    widget_link_save_box[number](data);
    login_box_close();
}



function check_url(url) {
    var reg = new RegExp();
    reg.compile("^(http|https)://.*?$");
    if (!reg.test(url)) {
        return false;
    }
    return true;
}
/**
 * 得到对象的长度
 */
function getObjLength(obj) {
    var number = 0;
    for (var i in obj) {
        number++;
    }
    return number;
}
/**
 * 得到文件的大小
 */
function getSize(size) {
    var kb = 1024;
    var mb = 1024 * kb;
    var gb = 1024 * mb;
    var tb = 1024 * gb;
    if (size < mb) {
        return (size / kb).toFixed(2) + " KB";
    } else if (size < gb) {
        return (size / mb).toFixed(2) + " MB";
    } else if (size < tb) {
        return (size / gb).toFixed(2) + " GB";
    } else {
        return (size / tb).toFixed(2) + " TB";
    }
}
/**
 * 生成一个唯一数
 */
function getRandNumber() {
    var myDate = new Date();
    return myDate.getTime() + '' + Math.floor(Math.random() * 10000);
}

var obj2String = function(_obj) {
    var t = typeof(_obj);
    if (t != 'object' || _obj === null) {
        // simple data type
        if (t == 'string') {
            _obj = '"' + _obj + '"';
        }
        return String(_obj);
    } else {
        if (_obj instanceof Date) {
            return _obj.toLocaleString();
        }
        // recurse array or object
        var n, v, json = [],
            arr = (_obj && _obj.constructor == Array);
        for (n in _obj) {
            v = _obj[n];
            t = typeof(v);
            if (t == 'string') {
                v = '"' + v + '"';
            } else if (t == "object" && v !== null) {
                v = this.obj2String(v);
            }
            json.push((arr ? '' : '"' + n + '":') + String(v));
        }
        return (arr ? '[' : '{') + String(json) + (arr ? ']' : '}');
    }
};

function twoDecimal(num) {
    return num.toFixed(2)
}