var customField = {
    list: function() {
        return [
            { type: "image_ad", val: "图片/广告" },
            { type: "goods", val: "商品" },
            { type: "image_nav", val: "图片/导航" },
            { type: "image_theme", val: "图片/专题" },
            { type: "image_seckill", val: "集限抢" },
            { type: "image_module", val: "图片/模块" },
            { type: "text_module", val: "文本" }
            
        ];
    },
    listHtml: function(dom,categoryType,discoveryType) {
        $("#" + dom).html('<div class="app__content js-app-main" style="background: #fff;">' +
            '<div class="app-design clearfix">' +
            '<div class="app-preview">' +
            '<div class="app-entry" style="margin-top: 30px;">' +
            '<div class="app-fields js-fields-region">' +
            '<div class="app-fields ui-sortable"></div>' +
            '</div></div></div>' +
            '<div class="app-sidebars">' +
            '<div class="app-sidebar" style="margin-top:100px;display:none">' +
            '<div class="arrow"></div>' +
            '<div class="app-sidebar-inner js-sidebar-region" id="sidebarRegio"></div>' +
            '</div></div>' +
            '<div class="app-actions" style="display:block;bottom:0px;width: 350px;">' +
            '</div></div></div>')
        var arr = customField.list();
        var html = '<div class="js-add-region" style="margin-bottom: 50px;"><div><div class="app-add-field"><h4>添加内容</h4><ul>';
        if(categoryType == 2){
    		html += '<li><a class="js-new-field rich-text" data-field-type="image_theme">头部图片</a></li>';
    		html += '<li><a class="js-new-field rich-text" data-field-type="image_module">模块</a></li>';
    	}else if(categoryType == 3){
    		html += '<li><a class="js-new-field rich-text" data-field-type="image_ad">图片</a></li>';
    	    if(discoveryType == 2 ){
        		html += '<li><a class="js-new-field rich-text" data-field-type="text_module">文本</a></li>';
    	    }
    	}else{
    		 for (var i in arr) {
    			 if(i != 5 && i != 6){
     	            html += '<li><a class="js-new-field rich-text" data-field-type="' + arr[i].type + '">' + arr[i].val + '</a></li>';
    			 }
    	        }
    	}
        html += '</ul></div></div></div>';
        $('.app-preview').append(html);
    },
    init: function(domob,categoryType,discoveryType) {
        customField.listHtml(domob,categoryType,discoveryType);
        $('.js-add-region .js-new-field').click(function() {
            if ($(this).attr("data-field-type") == 'tpl_shop' || $(this).attr("data-field-type") == 'tpl_shop1') {
                if ($('.tpl-shop').length > 0 || $('.tpl-shop1').length > 0) {
                    layer_tips(1, '一个模板只可拥有一个模板头部！');
                    return false;
                }
            }
            var app_field = $('<div class="app-field clearfix"><div class="control-group"><div class="component-border"></div></div><div class="actions"><div class="actions-wrap"><span class="action">编辑</span><span class="action add">加内容</span><span class="action delete">删除</span></div></div><div class="sort"><i class="sort-handler"></i></div></div>');
            app_field.data('field-type', $(this).data('field-type'));
            $('.js-fields-region .app-fields').append(app_field);
            app_field.trigger('click');
        });
        var doMouseDownTimmer = null;
        $('.js-fields-region .app-field').live('click', function() {
            clearTimeout(doMouseDownTimmer);
            if (!$(this).hasClass('editing')) {
                customField.clickEvent($(this));
            }
        }).live('mousedown', function(ee) {
            var preview_top = $('.app-preview').offset().top;
            var dom = $(this);
            var moveCssDom = $('<style>*{cursor:move!important;}</style>');
            var newTop = 0;
            var fieldTop = ee.pageY - dom.offset().top;
            doMouseDownTimmer = setTimeout(function() {
                $('body').bind('mousemove mouseup', function(e) {
                    if (e.type == 'mousemove') {
                        if (dom.data('noFirst') == '1') {
                            newTop = e.pageY - preview_top - fieldTop;
                            dom.css('top', newTop);
                            if (newTop > ($('.ui-sortable-placeholder').offset().top - preview_top + ($('.ui-sortable-placeholder').height() * 1))) {
                                $('.ui-sortable-placeholder').insertAfter($('.ui-sortable-placeholder').next());
                            } else if ($('.ui-sortable-placeholder').index() > 0 && newTop < ($('.ui-sortable-placeholder').prev().offset().top - preview_top + ($('.ui-sortable-placeholder').prev().height() * 0.1))) {
                                $('.ui-sortable-placeholder').insertBefore($('.ui-sortable-placeholder').prev());
                            }
                        } else {
                            $('body').bind("selectstart", function() {
                                return false;
                            }).css({ 'cursor': 'move', '-moz-user-select': 'none', '-khtml-user-select': 'none', 'user-select': 'none' }).append(moveCssDom);
                            dom.css({ position: 'absolute', width: '320px', height: (dom.height()) + 'px', 'z-index': '1000', 'top': (dom.offset().top - preview_top - 1) + 'px' }).data('noFirst', '1').after('<div class="app-field clearfix editing ui-sortable-placeholder" style="visibility:hidden;height:' + (dom.height()) + 'px;"></div>');
                        }
                    } else {
                        $('body').css({ 'cursor': 'auto', '-moz-user-select': '', '-khtml-user-select': '', 'user-select': '' }).unbind('mousemove mouseup selectstart');
                        dom.data({ 'mousedown': false, 'noFirst': '0' }).attr('style', 'position:relative');
                        $('.ui-sortable-placeholder').replaceWith(dom);
                        //moveCssDom.remove();
                        if (dom.hasClass('editing')) {
                            //customField.clickEvent(dom);
                        }
                    }
                });
            }, 200);
        });
        $('.js-fields-region .action.add').live('click', function(event) {
            clearTimeout(doMouseDownTimmer);
            var dom = $(this).closest('.app-field');
            var arr = customField.list();
            var rightContent = '<div><div class="app-add-field"><h4>添加内容</h4><ul>';
            for (var i in arr) {
                if (arr[i].type != 'coupons') {
                    rightContent += '<li><a class="js-new-field rich-text" data-field-type="' + arr[i].type + '">' + arr[i].val + '</a></li>';
                }
            }
            rightContent += '</ul></div></div>';
            rightHtml = $(rightContent);
            rightHtml.find('.js-new-field').click(function() {
                var app_field = $('<div class="app-field clearfix"><div class="control-group"><div class="component-border"></div></div><div class="actions"><div class="actions-wrap"><span class="action edit">编辑</span><span class="action add">加内容</span><span class="action delete">删除</span></div></div><div class="sort"><i class="sort-handler"></i></div></div>');
                app_field.data('field-type', $(this).data('field-type'));
                dom.after(app_field);
                app_field.trigger('click');
            });
            $('#sidebarRegio').empty().html(rightHtml);
            $('.app-sidebar').css('margin-top', dom.offset().top - $('.app-preview').offset().top);
            event.stopPropagation();
            return false;
        });
        $('.js-fields-region .action.delete').live('click', function(event) {
           
        	clearTimeout(doMouseDownTimmer);
            var nowDom = $(this);
            button_box($(this), event, 'left', 'delete', '确定删除？', function() {
                nowDom.closest('.app-field').remove();
                if (nowDom.closest('.app-field').hasClass('editing')) {
                    $('.js-config-region .app-field').eq(0).trigger('click');
                }
                close_button_box();
            });
            event.stopPropagation();
            return false;
        });
    },
    clickEvent: function(dom) {
        $('.app-entry .app-field').removeClass('editing');
        dom.addClass('editing');
        var clickArr = [],
            domHtml = '',
            rightHtml = '',
            defaultHtml = '';  
        
        //模块
         clickArr['image_module'] = function() {
        	var moduleTopHtml='';
        	var modulebottomHtml='';
//        	console.log(dom);
            if (dom.find('.control-group .sc-goods-list').size() == 0) {
                domHtml = dom.find('.control-group');
            //    domHtml.data({ 'navList': [], 'type': '1', 'size': '0', 'max_height': 0, 'max_width': 0 });
                domHtml.html('<div class="module_top"><div class="custom-image-swiper"><div class="swiper-container" style="height:85px"><div class="swiper-wrapper"><img style="display:block;" src=""/></div></div></div></div><div class="title" style=" font-size:20px;height:20px; line-height:20px; text-align:center; display:block;"><span style=" "></span></div><div class="module_bottom"><ul class="sc-goods-list clearfix size-2 card pic " ></ul></div>');
                domHtml.data({ 'image_module': [], 'size': '1', 'size_type': '0', 'buy_btn': '1', 'buy_btn_type': '1', 'show_title': '0', 'price': '1','picture':'','title':'' });
                moduleTopHtml=domHtml.find('.module_top');
                modulebottomHtml=domHtml.find('.module_bottom');
            } else {
                domHtml = dom.find('.control-group');
            }
            if(wcategoryType == 2){
                var varHtml = '<div>';
                varHtml += '<div class="form-horizontal">';
                varHtml += '<div class="js-meta-region" style="margin-bottom:20px;">';
                varHtml += '<div>';
                varHtml += '<div class="control-group">';
                varHtml += '<label class="control-label">选择商品：</label>';
                varHtml += '<div class="controls">';
                varHtml += '<ul class="module-goods-list clearfix ui-sortable" name="goods">';
                varHtml += '<li><a href="javascript:void(0);" class="js-add-goods add-goods">';
                varHtml += '<i class="icon-add"></i></a></li></ul>';
                varHtml += '</div></div>';
                varHtml += '<div class="control-group">';
                varHtml += '<label class="control-label">列表样式：</label>';
                varHtml += '<div class="controls">';
                varHtml += '<label class="radio inline"><input type="radio" name="dir" value="1"   checked/>一排两列</label>';
                varHtml += '</div></div><div class="control-group"></div></div></div></div></div>';
                rightHtml = $('<div><form class="form-horizontal"><div class="control-group"><li class="choice" data-id="images"><div class="choice-image"><a class="add-image js-trigger-image" href="javascript: void(0);"><i class="icon-add"></i>  添加图片</a></div><div class="choice-content"><div class="control-group"><label class="control-label">文字：</label><div class="controls"><input class="" type="text" name="title" value="" maxlength="15"/></div></div></div></div><div class="actions"></div></li>'+varHtml+'</div></form></div>');
            }
            var good_data = domHtml.data('image_module');
            var picture = domHtml.data('picture');
            var title = domHtml.data('title');
            console.log(title);
            //console.log(good_data);
            var html = '';
            for (var i in good_data) {
                var item = good_data[i];
                html += '<li class="sort"><a href="javascript:void(0);"><img src="' + item.image + '" alt="' + item.title + '" title="' + item.title + '" width="50" height="50"></a><a class="close-modal js-delete-goods small hide" data-id="' + i + '" title="删除">×</a></li>';
            }
            if (typeof(picture) != "undefined" && picture !=""){ 
                rightHtml.find('.js-trigger-image').removeClass('add-image').addClass('modify-image').html('重新上传').before('<img src="' + picture + '" width="118" height="118" class="thumb-image"/>');;
            }
            if(typeof(title) != "undefined" && title != ""){
            	rightHtml.find('input[name="title"]').val(title);
            }
            rightHtml.find('.module-goods-list').prepend(html);
            //上传图片
            rightHtml.find('.js-trigger-image').click(function() {
                var imageDom = $(this);
                upload_pic_box(1, true, function(pic_list) {
                    if (pic_list.length > 0) {
                        for (var i in pic_list) {
                            var image = new Image();
                            image.src = pic_list[i];
                            //第一次上传图片是将图片路径存储在domHtml的data中的picture中
                            domHtml.data('picture', pic_list[i]);
                            imageDom.siblings('.thumb-image').remove();
                            imageDom.removeClass('add-image').addClass('modify-image').html('重新上传').before('<img src="' + pic_list[i] + '" width="118" height="118" class="thumb-image"/>');
                            var html='';
                            html +='<div class="custom-image-swiper"><div class="swiper-container" style="height:85px"><div class="swiper-wrapper"><img style="display:block;" src="' + pic_list[i] + '"/></div></div></div></div>';
                            domHtml.find('.module_top').addClass('moduleCutoff');
                            domHtml.find('.module_top').html(html);
                        }
                    }
                }, 1);
            });
            
            
          //  rightHtml.find('.module-goods-list').prepend(html);
            rightHtml.find('.module-goods-list .sort .js-delete-goods').click(function() {
                var zindex = $(this).parents("li").index();
                var tdom = $(".editing").find("li");
                tdom[zindex].remove();
                $(this).closest('.sort').remove();

                var good_data = domHtml.data('image_module');
                delete good_data[$(this).data('id')];
                moduleBottomHtml.data('image_module', good_data);
            });
            

            //上传商品
            widget_link_box(rightHtml.find('.js-add-goods'), 'good', function(result) {
            	console.log(result);
                var good_data = domHtml.data('image_module');
                console.log(good_data);
                if (good_data) {
                    $.merge(good_data, result);
                } else {
                    good_data = result;
                }
                domHtml.data('image_module', good_data);
                rightHtml.find('.module-goods-list .sort').remove();
                var html = '';
                var value = $('input[name="size"]:checked').val();
                for (var i in good_data) {
                    var item = good_data[i];
                    console.log(item);
                    html += '<li class="sort"><a href="javascript:void(0);" ><img src="' + item.image + '" alt="' + item.title + '" title="' + item.title + '" width="50" height="50"></a><a class="close-modal js-delete-goods small hide" data-id="' + item.id + '" title="删除">×</a></li>';
                }
                rightHtml.find('.module-goods-list').prepend(html);
                rightHtml.find('.module-goods-list .sort .js-delete-goods').click(function() {
                    var zindex = $(this).parents("li").index();
                    console.log(zindex);
                    var tdom = $(".editing").find("li");
                    tdom[zindex].remove();
                    $(this).closest('.sort').remove();
                    var good_data = domHtml.data('image_module');
                    delete good_data[$(this).data('id')];
                    domHtml.data('image_module', good_data);
                });
            });
            
            //列表样式
            rightHtml.find('input[name="size"]').change(function() {
                domHtml.data('size', $(this).val());
                changeStyleContent();
            }).each(function(i, item) {
                if ($(item).val() == domHtml.data('size')) {
                    $(item).prop('checked', true).change();
                }
            });

            function changeStyleContent() {
                var html = '';
                switch (domHtml.data('size')) {
                    case '0':
                        html = '<ul class="sc-goods-list clearfix size-2 card pic">';
                        for (var i in good_data) {
                            var item = good_data[i];
                            html += '<li class="goods-card big-pic card"><a href="javascript:void(0);" class="link js-goods clearfix"><div class="photo-block"><img class="goods-photo js-goods-lazy" src="' + item.image + '"/></div>';
                            html += '<div class="info clearfix  info-price"><p class="goods-title">' + item.title + '</p><p class="goods-price goods-price-icon"><em>￥' + item.price + '</em></p><p class="goods-price-taobao"></p></div>';
                            html += '</a></li>';
                        }
                        html += '</ul>';
                        break;
                    case '1':
                        html = '<ul class="sc-goods-list clearfix size-1 card pic">';
                        for (var i in good_data) {
                            var item = good_data[i];
                            html += '<li class="goods-card small-pic card"><a href="javascript:void(0);" class="link js-goods clearfix"><div class="photo-block"><img class="goods-photo js-goods-lazy" src="' + item.image + '"/></div>';
                            html += '<div class="info clearfix  info-price"><p class="goods-title">' + item.title + '</p><p class="goods-price goods-price-icon"><em>￥' + item.price + '</em></p><p class="goods-price-taobao"></p></div>';
                            html += '</a></li>';
                        }

                        html += '</ul>';
                        break;
                }
                domHtml.find('.sc-goods-list').replaceWith(html);
            }
            
            
            //模块标题
            var titleDom = rightHtml.find('input[name="title"]');
            titleDom.blur(function() {
                domHtml.data('title', titleDom.val());
                buildContent();
            });
            var buildContent = function() {
                var html = '';
                html += domHtml.data('title') != '' ? '<span class="title">' + domHtml.data('title') + '</span>' : '';
                domHtml.find('.title').css('background-color','grey');
                domHtml.find('.title').html(html);
            };
            
            $('#sidebarRegio').empty().html(rightHtml);   
            var mySwiper = new Swiper('.swiper-container', {
                pagination: '.swiper-pagination',
                paginationClickable: true,
                loop: true,
                autoplay: 3000,
            });
        };
        
        //文本
        clickArr['text_module'] = function() {
       	var moduleTopHtml='';
       	var modulebottomHtml='';
//       	console.log(dom);
           if (dom.find('.control-group .sc-goods-list').size() == 0) {
               domHtml = dom.find('.control-group');
           //    domHtml.data({ 'navList': [], 'type': '1', 'size': '0', 'max_height': 0, 'max_width': 0 });
//               domHtml.html('<div class="text_module"><div class="module_top"></div></div>');
               domHtml.html('<div class="module_top"><div class="custom-image-swiper"><div class="swiper-container" style="height:85px"><div class="swiper-wrapper"><img style="display:block;" src=""/></div></div></div></div><div class="module_bottom"><ul class="sc-goods-list clearfix size-2 card pic " ></ul></div>');

               domHtml.data({ 'text_module': [], 'size': '1', 'size_type': '0', 'buy_btn': '1', 'buy_btn_type': '1', 'show_title': '0', 'price': '1','picture':'','title':'' });
               moduleTopHtml=domHtml.find('.module_top');
               modulebottomHtml=domHtml.find('.module_bottom');
           } else {
               domHtml = dom.find('.control-group');
           }
           rightHtml = $('<div><form class="form-horizontal"><div class="control-group"><li class="choice" data-id="images"><div class="choice-content"><div class="control-group"><label class="control-label">文字：</label><div class="controls"><textarea class="text_area"  name="title" value="" /></div></div></div></div><div class="actions"></div></li></div></form></div>');

           
           var title = domHtml.data('title');
           console.log(title);
           //console.log(good_data);
           var html = '';
//           for (var i in good_data) {
//               var item = good_data[i];
//               html += '<li class="sort"><a href="javascript:void(0);"><img src="' + item.image + '" alt="' + item.title + '" title="' + item.title + '" width="50" height="50"></a><a class="close-modal js-delete-goods small hide" data-id="' + i + '" title="删除">×</a></li>';
//           }
//           if (typeof(picture) != "undefined" && picture !=""){ 
//               rightHtml.find('.js-trigger-image').removeClass('add-image').addClass('modify-image').html('重新上传').before('<img src="' + picture + '" width="118" height="118" class="thumb-image"/>');;
//           }
           if(typeof(title) != "undefined" && title != ""){
           	rightHtml.find('.text_area').val(title);
           }
           rightHtml.find('.module-goods-list').prepend(html);
           //上传图片
//           rightHtml.find('.js-trigger-image').click(function() {
//               var imageDom = $(this);
//               upload_pic_box(1, true, function(pic_list) {
//                   if (pic_list.length > 0) {
//                       for (var i in pic_list) {
//                           var image = new Image();
//                           image.src = pic_list[i];
//                           //第一次上传图片是将图片路径存储在domHtml的data中的picture中
//                           domHtml.data('picture', pic_list[i]);
//                           imageDom.siblings('.thumb-image').remove();
//                           imageDom.removeClass('add-image').addClass('modify-image').html('重新上传').before('<img src="' + pic_list[i] + '" width="118" height="118" class="thumb-image"/>');
//                           var html='';
//                           html +='<div class="custom-image-swiper"><div class="swiper-container" style="height:85px"><div class="swiper-wrapper"><img style="display:block;" src="' + pic_list[i] + '"/></div></div></div></div>';
//                           domHtml.find('.module_top').addClass('moduleCutoff');
//                           domHtml.find('.module_top').html(html);
//                       }
//                   }
//               }, 1);
//           });
           
           
         //  rightHtml.find('.module-goods-list').prepend(html);
//           rightHtml.find('.module-goods-list .sort .js-delete-goods').click(function() {
//               var zindex = $(this).parents("li").index();
//               var tdom = $(".editing").find("li");
//               tdom[zindex].remove();
//               $(this).closest('.sort').remove();
//
//               var good_data = domHtml.data('image_module');
//               delete good_data[$(this).data('id')];
//               moduleBottomHtml.data('image_module', good_data);
//           });
           

           //上传商品
//           widget_link_box(rightHtml.find('.js-add-goods'), 'good', function(result) {
//           	console.log(result);
//               var good_data = domHtml.data('image_module');
//               console.log(good_data);
//               if (good_data) {
//                   $.merge(good_data, result);
//               } else {
//                   good_data = result;
//               }
//               domHtml.data('image_module', good_data);
//               rightHtml.find('.module-goods-list .sort').remove();
//               var html = '';
//               var value = $('input[name="size"]:checked').val();
//               for (var i in good_data) {
//                   var item = good_data[i];
//                   console.log(item);
//                   html += '<li class="sort"><a href="javascript:void(0);" ><img src="' + item.image + '" alt="' + item.title + '" title="' + item.title + '" width="50" height="50"></a><a class="close-modal js-delete-goods small hide" data-id="' + item.id + '" title="删除">×</a></li>';
//               }
//               rightHtml.find('.module-goods-list').prepend(html);
//               rightHtml.find('.module-goods-list .sort .js-delete-goods').click(function() {
//                   var zindex = $(this).parents("li").index();
//                   console.log(zindex);
//                   var tdom = $(".editing").find("li");
//                   tdom[zindex].remove();
//                   $(this).closest('.sort').remove();
//                   var good_data = domHtml.data('image_module');
//                   delete good_data[$(this).data('id')];
//                   domHtml.data('image_module', good_data);
//               });
//           });
           
           //列表样式
//           rightHtml.find('input[name="size"]').change(function() {
//               domHtml.data('size', $(this).val());
//               changeStyleContent();
//           }).each(function(i, item) {
//               if ($(item).val() == domHtml.data('size')) {
//                   $(item).prop('checked', true).change();
//               }
//           });

           function changeStyleContent() {
               var html = '';
               switch (domHtml.data('size')) {
                   case '0':
                       html = '<ul class="sc-goods-list clearfix size-2 card pic">';
                       for (var i in good_data) {
                           var item = good_data[i];
                           html += '<li class="goods-card big-pic card"><a href="javascript:void(0);" class="link js-goods clearfix"><div class="photo-block"><img class="goods-photo js-goods-lazy" src="' + item.image + '"/></div>';
                           html += '<div class="info clearfix  info-price"><p class="goods-title">' + item.title + '</p><p class="goods-price goods-price-icon"><em>￥' + item.price + '</em></p><p class="goods-price-taobao"></p></div>';
                           html += '</a></li>';
                       }
                       html += '</ul>';
                       break;
                   case '1':
                       html = '<ul class="sc-goods-list clearfix size-1 card pic">';
                       for (var i in good_data) {
                           var item = good_data[i];
                           html += '<li class="goods-card small-pic card"><a href="javascript:void(0);" class="link js-goods clearfix"><div class="photo-block"><img class="goods-photo js-goods-lazy" src="' + item.image + '"/></div>';
                           html += '<div class="info clearfix  info-price"><p class="goods-title">' + item.title + '</p><p class="goods-price goods-price-icon"><em>￥' + item.price + '</em></p><p class="goods-price-taobao"></p></div>';
                           html += '</a></li>';
                       }

                       html += '</ul>';
                       break;
               }
               domHtml.find('.sc-goods-list').replaceWith(html);
           }
           
           
           //模块标题
           var titleDom = rightHtml.find('.text_area');
           titleDom.blur(function() {
               domHtml.data('title', titleDom.val());
               buildContent();
           });
           var buildContent = function() {
               var html = '';
               html += domHtml.data('title') != '' ? '<span class="title">' + domHtml.data('title') + '</span>' : '';
               domHtml.find('.title').css('background-color','grey');
               domHtml.find('.module_top').html(html);
           };
           
           $('#sidebarRegio').empty().html(rightHtml);   
           var mySwiper = new Swiper('.swiper-container', {
               pagination: '.swiper-pagination',
               paginationClickable: true,
               loop: true,
               autoplay: 3000,
           });
       };
        
        //图片专题
        clickArr['image_theme'] = function() {
        	
            defaultHtml = '<div class="custom-image-swiper"><div class="swiper-container" style="height:85px"><div class="swiper-wrapper"><img style="max-height:80px;display:block;" src=""/></div></div></div>';
            domHtml = dom.find('.control-group');
            if (domHtml.html() == '<div class="component-border"></div>') {
                domHtml.prepend(defaultHtml);
                domHtml.data({ 'navList': [], 'type': '1', 'size': '0', 'max_height': 0, 'max_width': 0 });
            }
            rightHtml = $('<div><form class="form-horizontal"><div class="control-group"><div class="controls"><label class="control-label">显示方式：</label><div class="controls"><label class="radio inline"><input type="radio" name="type" value="1" checked="checked"' + (domHtml.data('type') == '1' ? ' checked="checked"' : '') + '/>专题</label></div></div><div class="control-group"  style="visibility: hidden;"><label class="control-label">显示大小：</label><div class="controls"><label class="radio inline"><input type="radio" name="size" value="0" ' + (domHtml.data('size') == '0' ? ' checked="checked"' : '') + '/>大图</label><label class="radio inline size_1_label" ' + (domHtml.data('type') == '0' ? 'style="display:none;"' : '') + '><input type="radio" name="size" value="1"  ' + (domHtml.data('size') == '1' ? ' checked="checked"' : '') + '/>小图</label></div></div><div class="control-group js-choices-region" style="margin-top: -30px;"><ul class="choices ui-sortable"></ul></div><div class="control-group options"><a href="javascript:void(0);" class="add-option js-add-option"><i class="icon-add"></i> 添加专题</a></div></form></div>');
            rightHtml.find('input[name="type"]').change(function() {
                domHtml.data('type', $(this).val());
                if ($(this).val() == '1') {
                    rightHtml.find('.size_1_label').show();
                } else {
                    domHtml.data('type', '0');
                    rightHtml.find('input[name="size"][value="0"]').prop('checked', true);
            //        rightHtml.find('.size_1_label').hide();
                }
                buildContent();
            });
            rightHtml.find('input[name="type"][value="2"]').change(function() {
                domHtml.data('type', $(this).val());
                buildContent();
            });
            var rightUl = rightHtml.find('.js-choices-region .ui-sortable');
            var addContent = function(num, dom) {
                if (num >= 0) {
                    var navList = domHtml.data('navList');
                    var liContent = '<li class="choice" data-id="' + num + '">';
                    liContent += '<div class="choice-image">';
                    if (navList[num].image) {
                    	if(wcategoryType == 2){
                            liContent += '<img src="' + navList[num].image + '" class="thumb-image"/><a class="modify-image js-trigger-image" href="javascript: void(0);">重新上传</a>';
                    	}
                    } else {
                    	if(wcategoryType == 2){
                            liContent += '<a class="add-image js-trigger-image" href="javascript:void(0);"><i class="icon-add"></i>添加图片</a>';
                    	}
                    }
                    liContent += '</div>';
                    if(wcategoryType != 2){
                        liContent += '<div class="choice-content"><div class="control-group"><label class="control-label">链接：</label><div class="control-action clearfix">';
                    }
                    if (navList[num].name != '') {
                    	if(wcategoryType != 2){
                            liContent += '<div class="left js-link-to link-to"><a href="javascript:void(0);" class="new-window link-to-title"><span class="label label-success">' + navList[num].prefix + ' <em class="link-to-title-text">' + navList[num].name + '</em></span></a><a href="javascript:;" class="js-delete-link link-to-title close-modal" title="删除">×</a></div><div class="dropdown hover right"><a class="dropdown-toggle" href="javascript:void(0);">修改 <i class="caret"></i></a></div>';
                    	}
                    } else {
                        if(wcategoryType != 2){
                        liContent += '<div class="dropdown hover"><a class="js-dropdown-toggle dropdown-toggle" href="javascript:void(0);">设置链接到的专题地址 <i class="caret"></i></a></div>';
                        }
                    }
                    liContent += '</div></div></div><div class="actions"></div></li>';
                    var liHtml = $(liContent);
                  //  liHtml.find("input[name='title']").val(navList[num].title);
                    if (navList[num].name != '') {
                        liHtml.find('.js-delete-link').click(function() {
                            var fDom = $(this).closest('.control-action');
                            fDom.find('.js-link-to').remove();
                            fDom.find('.dropdown').removeClass('right').children('a').attr('class', 'js-dropdown-toggle dropdown-toggle').html('设置链接到的专题地址 <i class="caret">');
                            var navList = domHtml.data('navList');
                            navList[liHtml.data('id')] = { 'title': titleDom.val(), 'prefix': '', 'url': '', 'name': '', 'image': navList[liHtml.data('id')].image };
                            //domHtml.data('navList',navList);
                        });
                    }
                    rightHtml.find('.js-add-option').remove(); //删除添加专题元素
                } else {
                    var randNumber = getRandNumber();
                    var navList = domHtml.data('navList');
                    navList[randNumber] = { 'title': '', 'prefix': '', 'url': '', 'name': '', 'image': '' };
                    domHtml.data('navList', navList);
                    var themeTopHtml='';
                    var imageHtml='';
                    if(wcategoryType != 2){
                    	themeTopHtml +='<div class="choice-content"><div class="control-group"><label class="control-label">链接：</label><div class="control-action clearfix"><div class="dropdown hover"><a class="js-dropdown-toggle dropdown-toggle" href="javascript:void(0);">设置链接到的专题地址 <i class="caret"></i></a></div></div></div></div><div class="actions"></div>';
                    }else{
                    	imageHtml +='<div class="choice-image"><a class="add-image js-trigger-image" href="javascript: void(0);"><i class="icon-add"></i>  添加图片</a></div>';
                    }
                    var liHtml = $('<li class="choice" data-id="' + randNumber + '">'+imageHtml+''+themeTopHtml+'</li>');
                }
             //   var titleDom = liHtml.find('input[name="title"]');
                var nowDom = liHtml.find('.dropdown');
//                titleDom.blur(function() {
//                    var navList = domHtml.data('navList');
//                   navList[liHtml.data('id')].title = titleDom.val();
//                    domHtml.data('navList', navList);
//                    buildContent();
//                });
                liHtml.find('.js-trigger-image').click(function() {
                    var imageDom = $(this);
                    upload_pic_box(1, true, function(pic_list) {
                        if (pic_list.length > 0) {
                            for (var i in pic_list) {
                                var image = new Image();
                                image.src = pic_list[i];
                                imageDom.siblings('.thumb-image').remove();
                                imageDom.removeClass('add-image').addClass('modify-image').html('重新上传').before('<img src="' + pic_list[i] + '" width="118" height="118" class="thumb-image"/>');
                                //var navList = domHtml.data('navList');
                                if (image.height > domHtml.data('max_height')) {
                                    domHtml.data('max_height', image.height);
                                }
                                if (image.width > domHtml.data('max_width')) {
                                    domHtml.data('max_width', image.width);
                                }
                                //console.log(navList);
                                navList[liHtml.data('id')].image = pic_list[i];
                                domHtml.data('navList', navList);
                                buildContent();
                            }
                        }
                    }, 1);
                });
                link_box(nowDom,2, [], function(type, prefix, title, href,image) {
                    nowDom.siblings('.js-link-to').remove();
                    var beforeDom = $('<div class="left js-link-to link-to"><a href="javascript:void(0);" class="new-window link-to-title"><span class="label label-success">' + prefix + ' <em class="link-to-title-text">' + title + '</em></span></a><a href="javascript:;" class="js-delete-link link-to-title close-modal" title="删除">×</a></div>');
                    var navList = domHtml.data('navList');
                    var liHtmlId = liHtml.data('id');
                    navList[liHtmlId].prefix = prefix;
                    navList[liHtmlId].url = href;
                    navList[liHtmlId].name = title;
                    navList[liHtmlId].image = image;
                    beforeDom.find('.js-delete-link').click(function() {
                        var fDom = $(this).closest('.control-action');
                        fDom.find('.js-link-to').remove();
                        fDom.find('.dropdown').removeClass('right').children('a').attr('class', 'js-dropdown-toggle dropdown-toggle').html('设置链接到的专题地址 <i class="caret">');
                        var navList = domHtml.data('navList');
                        navList[liHtmlId].prefix = '';
                        navList[liHtmlId].url = '';
                        navList[liHtmlId].name = '';
                        navList[liHtmlId].image = '';
                        domHtml.data('navList', navList);
                        buildContent();
                    });
                    domHtml.data('navList', navList);
                    buildContent();
                    nowDom.before(beforeDom);
                    nowDom.addClass('right').children('a').attr('class', 'dropdown-toggle').html('修改 <i class="caret"></i>');
                });
                liHtml.find('span.add').click(function() {
                    addContent(-1, liHtml);
                });
                liHtml.find('span.delete').click(function() {
                    var navList = domHtml.data('navList');
                    delete navList[liHtml.data('id')];
                    domHtml.data('navList', navList);
                    $(this).closest('li.choice').remove();
                    buildContent();
                });
                if (dom) {
                    dom.after(liHtml);
                    var navList = domHtml.data('navList');
                    var newNavList = [];
                    $.each(rightHtml.find('.js-collection-region .ui-sortable > li'), function(i, item) {
                        newNavList[i] = navList[$(item).data('id')];
                        $(item).data('id', i);
                    });
                    domHtml.data('navList', newNavList);
                } else {
                    rightUl.append(liHtml);
                }
            };
            var buildContent = function() {
                var navList = domHtml.data('navList');
                if (getObjLength(navList) == 0) {
                    domHtml.find('.component-border').siblings('div').remove();
                    domHtml.prepend(defaultHtml);
                } else {
                    var html = '';
                    if (domHtml.data('type') == '0') {
                        html += '<div class="custom-image-swiper"><div class="swiper-container"><div class="swiper-wrapper">';
                        for (var i in navList) {
                            html += '<div class="swiper-slide"><a href="javascript:void(0);" >' + (navList[i].title != '' ? '<h3 class="title"></h3>' : '') + '<img src="' + navList[i].image + '"></a></div>';
                        }
                        html += '</div></div></div>';
                        if (getObjLength(navList) > 1) {
                            html += '<div class="swiper-pagination">';
                            var num = 0;
                            for (var i in navList) {
                                html += '<span class="swiper-pagination-switch' + (num == 0 ? ' swiper-active-switch' : '') + '"></span>';
                                num++;
                            }
                            html += '</div>';
                        }
                    } else {
                        html += '<ul class="custom-image clearfix">';
//                        if(wcategoryType != 2){
//                            html += '<li' + (domHtml.data('type') == '2' ? ' class="custom-image-small"' : '') + '><span>此处已配专题</span></li>';
//                        }else{
                        	for (var i in navList) {
                            	// size = 1 -> type = 2
                                html += '<li' + (domHtml.data('type') == '2' ? ' class="custom-image-small"' : '') + '>' + (navList[i].title != '' ? '<h3 class="title">' + navList[i].title + '</h3>' : '') + '<img src="' + navList[i].image + '"/></li>';
                            }
//                        }
                        
                        html += '</ul>';
                    }
                    domHtml.html(html);
                }
            };
            var navList = domHtml.data('navList');
            for (var num in navList) {
                addContent(num);
            }
            rightHtml.find('.js-add-option').click(function() {
                addContent(-1);
                $(this).remove('.js-add-option'); //删除添加专题元素
            });
            $('#sidebarRegio').empty().html(rightHtml);   
            var mySwiper = new Swiper('.swiper-container', {
                pagination: '.swiper-pagination',
                paginationClickable: true,
                loop: true,
                autoplay: 3000,
            });
        };
        
        
        
      //图片专题
        clickArr['image_seckill'] = function() {
        	
            defaultHtml = '<div class="custom-image-swiper"><div class="swiper-container" style="height:85px"><div class="swiper-wrapper"><img style="max-height:80px;display:block;" src=""/></div></div></div>';
            domHtml = dom.find('.control-group');
            if (domHtml.html() == '<div class="component-border"></div>') {
                domHtml.prepend(defaultHtml);
                domHtml.data({ 'navList': [], 'type': '1', 'size': '0', 'max_height': 0, 'max_width': 0 });
            }
            rightHtml = $('<div><form class="form-horizontal"><div class="control-group"><div class="controls"><label class="control-label">显示方式：</label><div class="controls"><label class="radio inline"><input type="radio" name="type" value="1" checked="checked"' + (domHtml.data('type') == '1' ? ' checked="checked"' : '') + '/>秒杀</label></div></div><div class="control-group"  style="visibility: hidden;"><label class="control-label">显示大小：</label><div class="controls"><label class="radio inline"><input type="radio" name="size" value="0" ' + (domHtml.data('size') == '0' ? ' checked="checked"' : '') + '/>大图</label><label class="radio inline size_1_label" ' + (domHtml.data('type') == '0' ? 'style="display:none;"' : '') + '><input type="radio" name="size" value="1"  ' + (domHtml.data('size') == '1' ? ' checked="checked"' : '') + '/>小图</label></div></div><div class="control-group js-choices-region" style="margin-top: -30px;"><ul class="choices ui-sortable"></ul></div><div class="control-group options"><a href="javascript:void(0);" class="add-option js-add-option"><i class="icon-add"></i> 添加图片</a></div></form></div>');
            rightHtml.find('input[name="type"]').change(function() {
                domHtml.data('type', $(this).val());
                if ($(this).val() == '1') {
                    rightHtml.find('.size_1_label').show();
                } else {
                    domHtml.data('type', '0');
                    rightHtml.find('input[name="size"][value="0"]').prop('checked', true);
            //        rightHtml.find('.size_1_label').hide();
                }
                buildContent();
            });
            rightHtml.find('input[name="type"][value="2"]').change(function() {
                domHtml.data('type', $(this).val());
                buildContent();
            });
            var rightUl = rightHtml.find('.js-choices-region .ui-sortable');
            var addContent = function(num, dom) {
                if (num >= 0) {
                    var navList = domHtml.data('navList');
                    var liContent = '<li class="choice" data-id="' + num + '">';
                    liContent += '<div class="choice-image">';
                    if (navList[num].image) {
//                    	if(wcategoryType == 2){
                            liContent += '<img src="' + navList[num].image + '" class="thumb-image"/><a class="modify-image js-trigger-image" href="javascript: void(0);">重新上传</a>';
//                    	}
                    } else {
//                    	if(wcategoryType == 2){
                            liContent += '<a class="add-image js-trigger-image" href="javascript:void(0);"><i class="icon-add"></i>添加图片</a>';
//                    	}
                    }
                    liContent += '</div>';
//                    if(wcategoryType != 2){
//                        liContent += '<div class="choice-content"><div class="control-group"><label class="control-label">链接：</label><div class="control-action clearfix">';
//                    }
                    if (navList[num].name != '') {
//                    	if(wcategoryType != 2){
//                            liContent += '<div class="left js-link-to link-to"><a href="javascript:void(0);" class="new-window link-to-title"><span class="label label-success">' + navList[num].prefix + ' <em class="link-to-title-text">' + navList[num].name + '</em></span></a><a href="javascript:;" class="js-delete-link link-to-title close-modal" title="删除">×</a></div><div class="dropdown hover right"><a class="dropdown-toggle" href="javascript:void(0);">修改 <i class="caret"></i></a></div>';
//                    	}
                    } else {
//                        if(wcategoryType != 2){
//                        liContent += '<div class="dropdown hover"><a class="js-dropdown-toggle dropdown-toggle" href="javascript:void(0);">设置链接到的专题地址 <i class="caret"></i></a></div>';
//                        }
                    }
                    liContent += '</div></div></div><div class="actions"></div></li>';
                    var liHtml = $(liContent);
                  //  liHtml.find("input[name='title']").val(navList[num].title);
                    if (navList[num].name != '') {
                        liHtml.find('.js-delete-link').click(function() {
                            var fDom = $(this).closest('.control-action');
                            fDom.find('.js-link-to').remove();
                            fDom.find('.dropdown').removeClass('right').children('a').attr('class', 'js-dropdown-toggle dropdown-toggle').html('设置链接到的专题地址 <i class="caret">');
                            var navList = domHtml.data('navList');
                            navList[liHtml.data('id')] = { 'title': titleDom.val(), 'prefix': '', 'url': '', 'name': '', 'image': navList[liHtml.data('id')].image };
                            //domHtml.data('navList',navList);
                        });
                    }
                    rightHtml.find('.js-add-option').remove(); //删除添加专题元素
                } else {
                    var randNumber = getRandNumber();
                    var navList = domHtml.data('navList');
                    navList[randNumber] = { 'title': '', 'prefix': '', 'url': '', 'name': '', 'image': '' };
                    domHtml.data('navList', navList);
                    var themeTopHtml='';
                    var imageHtml='';
//                    if(wcategoryType != 2){
//                    	themeTopHtml +='<div class="choice-content"><div class="control-group"><label class="control-label">链接：</label><div class="control-action clearfix"><div class="dropdown hover"><a class="js-dropdown-toggle dropdown-toggle" href="javascript:void(0);">设置链接到的专题地址 <i class="caret"></i></a></div></div></div></div><div class="actions"></div>';
//                    }else{
                    	imageHtml +='<div class="choice-image"><a class="add-image js-trigger-image" href="javascript: void(0);"><i class="icon-add"></i>  添加图片</a></div>';
//                    }
                    var liHtml = $('<li class="choice" data-id="' + randNumber + '">'+imageHtml+''+themeTopHtml+'</li>');
                }
             //   var titleDom = liHtml.find('input[name="title"]');
                var nowDom = liHtml.find('.dropdown');
//                titleDom.blur(function() {
//                    var navList = domHtml.data('navList');
//                   navList[liHtml.data('id')].title = titleDom.val();
//                    domHtml.data('navList', navList);
//                    buildContent();
//                });
                liHtml.find('.js-trigger-image').click(function() {
                    var imageDom = $(this);
                    upload_pic_box(1, true, function(pic_list) {
                        if (pic_list.length > 0) {
                            for (var i in pic_list) {
                                var image = new Image();
                                image.src = pic_list[i];
                                imageDom.siblings('.thumb-image').remove();
                                imageDom.removeClass('add-image').addClass('modify-image').html('重新上传').before('<img src="' + pic_list[i] + '" width="118" height="118" class="thumb-image"/>');
                                //var navList = domHtml.data('navList');
                                if (image.height > domHtml.data('max_height')) {
                                    domHtml.data('max_height', image.height);
                                }
                                if (image.width > domHtml.data('max_width')) {
                                    domHtml.data('max_width', image.width);
                                }
                                //console.log(navList);
                                navList[liHtml.data('id')].image = pic_list[i];
                                domHtml.data('navList', navList);
                                buildContent();
                            }
                        }
                    }, 1);
                });
                link_box(nowDom,2, [], function(type, prefix, title, href,image) {
                    nowDom.siblings('.js-link-to').remove();
                    var beforeDom = $('<div class="left js-link-to link-to"><a href="javascript:void(0);" class="new-window link-to-title"><span class="label label-success">' + prefix + ' <em class="link-to-title-text">' + title + '</em></span></a><a href="javascript:;" class="js-delete-link link-to-title close-modal" title="删除">×</a></div>');
                    var navList = domHtml.data('navList');
                    var liHtmlId = liHtml.data('id');
                    navList[liHtmlId].prefix = prefix;
                    navList[liHtmlId].url = href;
                    navList[liHtmlId].name = title;
                    navList[liHtmlId].image = image;
                    beforeDom.find('.js-delete-link').click(function() {
                        var fDom = $(this).closest('.control-action');
                        fDom.find('.js-link-to').remove();
                        fDom.find('.dropdown').removeClass('right').children('a').attr('class', 'js-dropdown-toggle dropdown-toggle').html('设置链接到的专题地址 <i class="caret">');
                        var navList = domHtml.data('navList');
                        navList[liHtmlId].prefix = '';
                        navList[liHtmlId].url = '';
                        navList[liHtmlId].name = '';
                        navList[liHtmlId].image = '';
                        domHtml.data('navList', navList);
                        buildContent();
                    });
                    domHtml.data('navList', navList);
                    buildContent();
                    nowDom.before(beforeDom);
                    nowDom.addClass('right').children('a').attr('class', 'dropdown-toggle').html('修改 <i class="caret"></i>');
                });
                liHtml.find('span.add').click(function() {
                    addContent(-1, liHtml);
                });
                liHtml.find('span.delete').click(function() {
                    var navList = domHtml.data('navList');
                    delete navList[liHtml.data('id')];
                    domHtml.data('navList', navList);
                    $(this).closest('li.choice').remove();
                    buildContent();
                });
                if (dom) {
                    dom.after(liHtml);
                    var navList = domHtml.data('navList');
                    var newNavList = [];
                    $.each(rightHtml.find('.js-collection-region .ui-sortable > li'), function(i, item) {
                        newNavList[i] = navList[$(item).data('id')];
                        $(item).data('id', i);
                    });
                    domHtml.data('navList', newNavList);
                } else {
                    rightUl.append(liHtml);
                }
            };
            var buildContent = function() {
                var navList = domHtml.data('navList');
                if (getObjLength(navList) == 0) {
                    domHtml.find('.component-border').siblings('div').remove();
                    domHtml.prepend(defaultHtml);
                } else {
                    var html = '';
                    if (domHtml.data('type') == '0') {
                        html += '<div class="custom-image-swiper"><div class="swiper-container"><div class="swiper-wrapper">';
                        for (var i in navList) {
                            html += '<div class="swiper-slide"><a href="javascript:void(0);" >' + (navList[i].title != '' ? '<h3 class="title"></h3>' : '') + '<img src="' + navList[i].image + '"></a></div>';
                        }
                        html += '</div></div></div>';
                        if (getObjLength(navList) > 1) {
                            html += '<div class="swiper-pagination">';
                            var num = 0;
                            for (var i in navList) {
                                html += '<span class="swiper-pagination-switch' + (num == 0 ? ' swiper-active-switch' : '') + '"></span>';
                                num++;
                            }
                            html += '</div>';
                        }
                    } else {
                        html += '<ul class="custom-image clearfix">';
//                        if(wcategoryType != 2){
//                            html += '<li' + (domHtml.data('type') == '2' ? ' class="custom-image-small"' : '') + '><span>此处已配专题</span></li>';
//                        }else{
                        	for (var i in navList) {
                            	// size = 1 -> type = 2
                                html += '<li' + (domHtml.data('type') == '2' ? ' class="custom-image-small"' : '') + '>' + (navList[i].title != '' ? '<h3 class="title">' + navList[i].title + '</h3>' : '') + '<img src="' + navList[i].image + '"/></li>';
                            }
//                        }
                        
                        html += '</ul>';
                    }
                    domHtml.html(html);
                }
            };
            var navList = domHtml.data('navList');
            for (var num in navList) {
                addContent(num);
            }
            rightHtml.find('.js-add-option').click(function() {
                addContent(-1);
                $(this).remove('.js-add-option'); //删除添加专题元素
            });
            $('#sidebarRegio').empty().html(rightHtml);   
            var mySwiper = new Swiper('.swiper-container', {
                pagination: '.swiper-pagination',
                paginationClickable: true,
                loop: true,
                autoplay: 3000,
            });
        };
        
        
        
        
        
        clickArr['image_nav'] = function() {
            rightHtml = $('<div><form class="form-horizontal"><div class="js-collection-region"><ul class="choices ui-sortable"></ul></div></form></div>');
            if (dom.find('.control-group .custom-nav-4').size() == 0) {
                domHtml = $('<ul class="custom-nav-4 clearfix"></ul>');
                domHtml.data({ 'navList': [{ 'title': '', 'prefix': '', 'url': '', 'name': '', 'image': '','webUrl':'' }, { 'title': '', 'prefix': '', 'url': '', 'name': '', 'image': '','webUrl':'' }, { 'title': '', 'prefix': '', 'url': '', 'name': '', 'image': '','webUrl':'' }, { 'title': '', 'prefix': '', 'url': '', 'name': '', 'image': '','webUrl':'' }] });
                dom.find('.control-group').prepend(domHtml);
            } else {
                domHtml = dom.find('.control-group .custom-nav-4');
            }
            var rightUl = rightHtml.find('.js-collection-region .ui-sortable');
            var navList = domHtml.data('navList');
            for (var i in navList) {
                (function() {
                    var liContent = '<li class="choice" data-id="' + i + '">';
                    liContent += '<div class="choice-image">';
                    if (navList[i].image) {
                        liContent += '<img src="' + navList[i].image + '" width="118" height="118" class="thumb-image"/><a class="modify-image js-trigger-image" href="javascript: void(0);">重新上传</a>';
                    } else {
                        liContent += '<a class="add-image js-trigger-image" href="javascript: void(0);"><i class="icon-add"></i> 添加图片</a>';
                    }
                    liContent += '</div>';
                    liContent += '<div class="choice-content"><div class="control-group"  style="diaplay:none;"><label class="control-label">文字：</label><div class="controls"><input class="" type="textarea" name="title" value="' + (navList[i].title != '' ? navList[i].title : '') +
                        '" maxlength="15"/></div></div><div class="control-group"  style="diaplay:none;"><label class="control-label">H5：</label><div class="controls"><input class="" type="textarea" name="webUrl" value="' + (navList[i].webUrl != '' ? navList[i].webUrl : '') +
                        '" maxlength="200"/></div></div><div class="control-group"><label class="control-label">链接：</label><div class="control-action clearfix">';
                    if (navList[i].name != '') {
                        liContent += '<div class="left js-link-to link-to"><a href="javascript:void(0);" class="new-window link-to-title"><span class="label label-success">' + navList[i].prefix + ' <em class="link-to-title-text">' + navList[i].name + '</em></span></a><a href="javascript:;" class="js-delete-link link-to-title close-modal" title="删除">×</a></div><div class="dropdown hover right"><a class="dropdown-toggle" href="javascript:void(0);">修改 <i class="caret"></i></a></div>';
                    } else {
                        liContent += '<div class="dropdown hover"><a class="js-dropdown-toggle dropdown-toggle" href="javascript:void(0);">设置链接到的页面地址 <i class="caret"></i></a></div>';
                    }
                    liContent += '</div></div></div></li>';
                    var liHtml = $(liContent);
                    liHtml.find("input[name='title']").val(navList[i].title);
                    liHtml.find("input[name='webUrl']").val(navList[i].webUrl);

                    var liHtmlId = liHtml.data('id');
                    if (navList[i].name != '') {
                        liHtml.find('.js-delete-link').click(function() {
                            var fDom = $(this).closest('.control-action');
                            fDom.find('.js-link-to').remove();
                            fDom.find('.dropdown').removeClass('right').children('a').attr('class', 'js-dropdown-toggle dropdown-toggle').html('设置链接到的页面地址 <i class="caret">');
                            var navList = domHtml.data('navList');
                            navList[liHtmlId].prefix = '';
                            navList[liHtmlId].url = '';
                            navList[liHtmlId].name = '';
                            domHtml.data('navList', navList);
                        });
                    }
                    var titleDom = liHtml.find('input[name="title"]');
                    var webUrlDom = liHtml.find('input[name="webUrl"]');

                    var nowDom = liHtml.find('.dropdown');
                    //失去焦点动态设置模板内内容
                    titleDom.blur(function() {
                        var navList = domHtml.data('navList');
                        navList[liHtml.data('id')].title = titleDom.val();
                        domHtml.data('navList', navList);
                        buildContent();
                    });
                  //失去焦点动态设置模板内内容
                    webUrlDom.blur(function() {
                        var navList = domHtml.data('navList');
                        navList[liHtml.data('id')].webUrl = webUrlDom.val();
                        domHtml.data('navList', navList);
                        buildContent();
                    });
                    liHtml.find('.js-trigger-image').click(function() {
                        var imageDom = $(this);
                        upload_pic_box(1, true, function(pic_list) {
                            if (pic_list.length > 0) {
                                for (var i in pic_list) {
                                    imageDom.siblings('.thumb-image').remove();
                                    imageDom.removeClass('add-image').addClass('modify-image').html('重新上传').before('<img src="' + pic_list[i] + '" width="118" height="118" class="thumb-image"/>');
                                    var navList = domHtml.data('navList');
                                    navList[liHtml.data('id')].image = pic_list[i];
                                    domHtml.data('navList', navList);
                                    buildContent();
                                }
                            }
                        }, 1);
                    });
                    link_box(nowDom,1, [], function(type, prefix, title, href) {
                        nowDom.siblings('.js-link-to').remove();
                        var beforeDom = $('<div class="left js-link-to link-to"><a href="javascript:void(0);" class="new-window link-to-title"><span class="label label-success">' + prefix + ' <em class="link-to-title-text">' + title + '</em></span></a><a href="javascript:;" class="js-delete-link link-to-title close-modal" title="删除">×</a></div>');
                        var navList = domHtml.data('navList');
                        var liHtmlId = liHtml.data('id');
                        navList[liHtmlId].prefix = prefix;
                        navList[liHtmlId].url = href;
                        navList[liHtmlId].name = title;
                        beforeDom.find('.js-delete-link').click(function() {
                            var fDom = $(this).closest('.control-action');
                            fDom.find('.js-link-to').remove();
                            fDom.find('.dropdown').removeClass('right').children('a').attr('class', 'js-dropdown-toggle dropdown-toggle').html('设置链接到的页面地址 <i class="caret">');
                            var navList = domHtml.data('navList');
                            navList[liHtmlId].prefix = '';
                            navList[liHtmlId].url = '';
                            navList[liHtmlId].name = '';
                            domHtml.data('navList', navList);
                        });
                        domHtml.data('navList', navList);
                        buildContent();
                        nowDom.before(beforeDom);
                        nowDom.addClass('right').children('a').attr('class', 'dropdown-toggle').html('修改 <i class="caret"></i>');
                    });
                    rightUl.append(liHtml);
                })();
            }
            var buildContent = function() {
                var navList = domHtml.data('navList');
                var html = '';
                for (var i in navList) {
                    html += '<li><span class="nav-img-wap">' + (navList[i].image != '' ? '<img src="' + navList[i].image + '"/>' : '&nbsp;') + '</span>' + (navList[i].title != '' ? '<span class="title">' + navList[i].title + '</span>' : '')  + '</li>';
                }
                domHtml.html(html);
            };
            var navList = domHtml.data('navList');
            $('#sidebarRegio').empty().html(rightHtml);
        };
        //图片广告
        clickArr['image_ad'] = function() {
            defaultHtml = '<div class="custom-image-swiper"><div class="swiper-container" style="height:85px"><div class="swiper-wrapper"><img style="max-height:80px;display:block;" src=""/></div></div></div>';
            domHtml = dom.find('.control-group');
            if (domHtml.html() == '<div class="component-border"></div>') {
                domHtml.prepend(defaultHtml);
                var nType = 0;
                if(wcategoryType == 3){
                	nType =1 ;
                }
                domHtml.data({ 'navList': [], 'type': nType, 'size': '0', 'max_height': 0, 'max_width': 0 });
            }
            if(wcategoryType == 3){
            	if(wdiscoveryType == 1 || wdiscoveryType == 3 ){
            		rightHtml = $('<div><form class="form-horizontal"><div class="control-group"><label class="control-label">显示方式：</label><div class="controls"><label class="radio inline"><input type="radio" name="type" value="1"' + (domHtml.data('type') == '1' ? ' checked="checked"' : '') + '/>单图显示</label></div></div><div class="control-group"  style="visibility: hidden;"><label class="control-label">显示大小：</label><div class="controls"><label class="radio inline"><input type="radio" name="size" value="0" ' + (domHtml.data('size') == '0' ? ' checked="checked"' : '') + '/>大图</label><label class="radio inline size_1_label" ' + (domHtml.data('type') == '0' ? 'style="display:none;"' : '') + '><input type="radio" name="size" value="1"  ' + (domHtml.data('size') == '1' ? ' checked="checked"' : '') + '/>小图</label></div></div><div class="control-group js-choices-region" style="margin-top: -30px;"><ul class="choices ui-sortable"></ul></div><div class="control-group options"><a href="javascript:void(0);" class="add-option js-add-option"><i class="icon-add"></i> 添加一个广告</a></div></form></div>');
            	}else{
            		rightHtml = $('<div><form class="form-horizontal"><div class="control-group"><label class="control-label">显示方式：</label><div class="controls"><label class="radio inline"><input type="radio" name="type" value="1"' + (domHtml.data('type') == '1' ? ' checked="checked"' : '') + '/>单图显示</label><label class="radio inline size_1_label"><input type="radio" name="type" value="2"  ' + 
            				(domHtml.data('type') == '2' ? ' checked="checked"' : '') + '/>双排显示</label><label class="radio inline size_1_label"><input type="radio" name="type" value="3"  ' + (domHtml.data('type') == '3' ? ' checked="checked"' : '') + '/>三排显示</label></div></div><div class="control-group"  style="visibility: hidden;"><label class="control-label">显示大小：</label><div class="controls"><label class="radio inline"><input type="radio" name="size" value="0" ' + (domHtml.data('size') == '0' ? ' checked="checked"' : '') + '/>大图</label><label class="radio inline size_1_label" ' + (domHtml.data('type') == '0' ? 'style="display:none;"' : '') + '><input type="radio" name="size" value="1"  ' + (domHtml.data('size') == '1' ? ' checked="checked"' : '') + '/>小图</label></div></div><div class="control-group js-choices-region" style="margin-top: -30px;"><ul class="choices ui-sortable"></ul></div><div class="control-group options"><a href="javascript:void(0);" class="add-option js-add-option"><i class="icon-add"></i> 添加一个广告</a></div></form></div>');
            	}
            }else{
            	rightHtml = $('<div><form class="form-horizontal"><div class="control-group"><label class="control-label">显示方式：</label><div class="controls"><label class="radio inline"><input type="radio" name="type" value="0"' + (domHtml.data('type') == '0' ? ' checked="checked"' : '') + '/>轮播显示</label><label class="radio inline"><input type="radio" name="type" value="1"' + (domHtml.data('type') == '1' ? ' checked="checked"' : '') + '/>单图显示</label><label class="radio inline size_1_label"><input type="radio" name="type" value="2"  ' + 
                		(domHtml.data('type') == '2' ? ' checked="checked"' : '') + '/>双排显示</label><label class="radio inline size_1_label"><input type="radio" name="type" value="3"  ' + (domHtml.data('type') == '3' ? ' checked="checked"' : '') + '/>三排显示</label></div></div><div class="control-group"  style="visibility: hidden;"><label class="control-label">显示大小：</label><div class="controls"><label class="radio inline"><input type="radio" name="size" value="0" ' + (domHtml.data('size') == '0' ? ' checked="checked"' : '') + '/>大图</label><label class="radio inline size_1_label" ' + (domHtml.data('type') == '0' ? 'style="display:none;"' : '') + '><input type="radio" name="size" value="1"  ' + (domHtml.data('size') == '1' ? ' checked="checked"' : '') + '/>小图</label></div></div><div class="control-group js-choices-region" style="margin-top: -30px;"><ul class="choices ui-sortable"></ul></div><div class="control-group options"><a href="javascript:void(0);" class="add-option js-add-option"><i class="icon-add"></i> 添加一个广告</a></div></form></div>');
            }
            
            rightHtml.find('input[name="type"]').change(function() {
                domHtml.data('type', $(this).val());
                if ($(this).val() == '1') {
                    rightHtml.find('.size_1_label').show();
                } else {
                    domHtml.data('type', '0');
                    rightHtml.find('input[name="size"][value="0"]').prop('checked', true);
            //        rightHtml.find('.size_1_label').hide();
                }
                buildContent();
            });
            rightHtml.find('input[name="type"][value="2"]').change(function() {
                domHtml.data('type', $(this).val());
                buildContent();
            });
            rightHtml.find('input[name="type"][value="3"]').change(function() {
                domHtml.data('type', $(this).val());
                buildContent();
            });
            var rightUl = rightHtml.find('.js-choices-region .ui-sortable');
            var addContent = function(num, dom) {
                if (num >= 0) {
                    var navList = domHtml.data('navList');
                    var liContent = '<li class="choice" data-id="' + num + '">';
                    liContent += '<div class="choice-image">';
                    if (navList[num].image) {
                        liContent += '<img src="' + navList[num].image + '" class="thumb-image"/><a class="modify-image js-trigger-image" href="javascript: void(0);">重新上传</a>';
                    } else {
                        liContent += '<a class="add-image js-trigger-image" href="javascript:void(0);"><i class="icon-add"></i>添加图片</a>';
                    }
                    liContent += '</div>';
                    if(wdiscoveryType != 2){
                        liContent += '<div class="choice-content"><div class="control-group"  style="diaplay:none;"><label class="control-label">色值：</label><div class="controls"><input class="" type="textarea" name="title" value="' + (navList[num].title != '' ? navList[num].title : '') +
                        '" maxlength="15"/></div></div><div class="control-group"  style="diaplay:none;"><label class="control-label">H5：</label><div class="controls"><input class="" type="textarea" name="webUrl" value="' + (navList[num].webUrl != '' ? navList[num].webUrl : '') +
                        '" maxlength="200"/></div></div><div class="control-group"><label class="control-label">链接：</label><div class="control-action clearfix">';

                    	 if (navList[num].name != '') {
                             liContent += '<div class="left js-link-to link-to"><a href="javascript:void(0);" class="new-window link-to-title"><span class="label label-success">' + navList[num].prefix + ' <em class="link-to-title-text">' + navList[num].name + '</em></span></a><a href="javascript:;" class="js-delete-link link-to-title close-modal" title="删除">×</a></div><div class="dropdown hover right"><a class="dropdown-toggle" href="javascript:void(0);">修改 <i class="caret"></i></a></div>';
                         } else {
                             liContent += '<div class="dropdown hover"><a class="js-dropdown-toggle dropdown-toggle" href="javascript:void(0);">设置链接到的页面地址 <i class="caret"></i></a></div>';
                         }
                    	 liContent += '</div></div></div>';
                    }
                   
                    liContent += '<div class="actions"><span class="action add close-modal" title="添加">+</span><span class="action delete close-modal" title="删除">×</span></div></li>';
                    var liHtml = $(liContent);
                    liHtml.find("input[name='title']").val(navList[num].title);
                    liHtml.find("input[name='webUrl']").val(navList[num].webUrl);
                    if (navList[num].name != '') {
                        liHtml.find('.js-delete-link').click(function() {
                            var fDom = $(this).closest('.control-action');
                            fDom.find('.js-link-to').remove();
                            fDom.find('.dropdown').removeClass('right').children('a').attr('class', 'js-dropdown-toggle dropdown-toggle').html('设置链接到的页面地址 <i class="caret">');
                            var navList = domHtml.data('navList');
                            navList[liHtml.data('id')] = { 'title': titleDom.val(), 'prefix': '', 'url': '', 'name': '', 'image': navList[liHtml.data('id')].image,'webUrl':webUrlDom.val() };
                            //domHtml.data('navList',navList);
                        });
                    }
                } else {
                    var randNumber = getRandNumber();
                    var navList = domHtml.data('navList');
                    navList[randNumber] = { 'title': '', 'prefix': '', 'url': '', 'name': '', 'image': '','webUrl': '' };
                    domHtml.data('navList', navList);
                    if(wdiscoveryType != 2){
                        var liHtml = $('<li class="choice" data-id="' + randNumber + '"><div class="choice-image"><a class="add-image js-trigger-image" href="javascript: void(0);"><i class="icon-add"></i>  添加图片</a></div><div class="choice-content"><div class="control-group"  style="diaplay:none;"><label class="control-label">色值：</label><div class="controls"><input class="" type="textarea" name="title" value="" maxlength="15"/></div></div><div class="control-group"  style="diaplay:none;"><label class="control-label">H5：</label><div class="controls"><input class="" type="textarea" name="webUrl" value="" maxlength="200"/></div></div><div class="control-group"><label class="control-label">链接：</label><div class="control-action clearfix"><div class="dropdown hover"><a class="js-dropdown-toggle dropdown-toggle" href="javascript:void(0);">设置链接到的页面地址 <i class="caret"></i></a></div></div></div></div><div class="actions"><span class="action add close-modal" title="添加">+</span><span class="action delete close-modal" title="删除">×</span></div></li>');
                    }else{
                        var liHtml = $('<li class="choice" data-id="' + randNumber + '"><div class="choice-image"><a class="add-image js-trigger-image" href="javascript: void(0);"><i class="icon-add"></i>  添加图片</a></div><div class="actions"><span class="action add close-modal" title="添加">+</span><span class="action delete close-modal" title="删除">×</span></div></li>');
                    }
                }
                var titleDom = liHtml.find('input[name="title"]');
                var webUrlDom = liHtml.find('input[name="webUrl"]');
                var nowDom = liHtml.find('.dropdown');
                titleDom.blur(function() {
                    var navList = domHtml.data('navList');
                    navList[liHtml.data('id')].title = titleDom.val();
                    navList[liHtml.data('id')].webUrl = webUrlDom.val();
                    domHtml.data('navList', navList);
                    buildContent();
                });
                webUrlDom.blur(function() {
                    var navList = domHtml.data('navList');
                    navList[liHtml.data('id')].webUrl = webUrlDom.val();
                    domHtml.data('navList', navList);
                    buildContent();
                });
                liHtml.find('.js-trigger-image').click(function() {
                    var imageDom = $(this);
                    upload_pic_box(1, true, function(pic_list) {
                        if (pic_list.length > 0) {
                            for (var i in pic_list) {
                                var image = new Image();
                                image.src = pic_list[i];
                                imageDom.siblings('.thumb-image').remove();
                                imageDom.removeClass('add-image').addClass('modify-image').html('重新上传').before('<img src="' + pic_list[i] + '" width="118" height="118" class="thumb-image"/>');
                                //var navList = domHtml.data('navList');
                                if (image.height > domHtml.data('max_height')) {
                                    domHtml.data('max_height', image.height);
                                }
                                if (image.width > domHtml.data('max_width')) {
                                    domHtml.data('max_width', image.width);
                                }
                                //console.log(navList);
                                navList[liHtml.data('id')].image = pic_list[i];
                                domHtml.data('navList', navList);
                                buildContent();
                            }
                        }
                    }, 1);
                });
                var searchType = 0;
                if(wdiscoveryType == 1){
                	searchType = 3;
                }
                link_box(nowDom, searchType,[], function(type, prefix, title, href) {
                    nowDom.siblings('.js-link-to').remove();
                    var beforeDom = $('<div class="left js-link-to link-to"><a href="javascript:void(0);" class="new-window link-to-title"><span class="label label-success">' + prefix + ' <em class="link-to-title-text">' + title + '</em></span></a><a href="javascript:;" class="js-delete-link link-to-title close-modal" title="删除">×</a></div>');
                    var navList = domHtml.data('navList');
                    var liHtmlId = liHtml.data('id');
                    navList[liHtmlId].prefix = prefix;
                    navList[liHtmlId].url = href;
                    navList[liHtmlId].name = title;
                    beforeDom.find('.js-delete-link').click(function() {
                        var fDom = $(this).closest('.control-action');
                        fDom.find('.js-link-to').remove();
                        fDom.find('.dropdown').removeClass('right').children('a').attr('class', 'js-dropdown-toggle dropdown-toggle').html('设置链接到的页面地址 <i class="caret">');
                        var navList = domHtml.data('navList');
                        navList[liHtmlId].prefix = '';
                        navList[liHtmlId].url = '';
                        navList[liHtmlId].name = '';
                        domHtml.data('navList', navList);
                    });
                    domHtml.data('navList', navList);
                    buildContent();
                    nowDom.before(beforeDom);
                    nowDom.addClass('right').children('a').attr('class', 'dropdown-toggle').html('修改 <i class="caret"></i>');
                });
                liHtml.find('span.add').click(function() {
                    addContent(-1, liHtml);
                });
                liHtml.find('span.delete').click(function() {
                    var navList = domHtml.data('navList');
                    delete navList[liHtml.data('id')];
                    domHtml.data('navList', navList);
                    $(this).closest('li.choice').remove();
                    buildContent();
                });
                if (dom) {
                    dom.after(liHtml);
                    var navList = domHtml.data('navList');
                    var newNavList = [];
                    $.each(rightHtml.find('.js-collection-region .ui-sortable > li'), function(i, item) {
                        newNavList[i] = navList[$(item).data('id')];
                        $(item).data('id', i);
                    });
                    domHtml.data('navList', newNavList);
                } else {
                    rightUl.append(liHtml);
                }
            };
            var buildContent = function() {
                var navList = domHtml.data('navList');
                if (getObjLength(navList) == 0) {
                    domHtml.find('.component-border').siblings('div').remove();
                    domHtml.prepend(defaultHtml);
                } else {
                    var html = '';
                    if (domHtml.data('type') == '0') {
                        html += '<div class="custom-image-swiper"><div class="swiper-container"><div class="swiper-wrapper">';
                        for (var i in navList) {
                            html += '<div class="swiper-slide"><a href="javascript:void(0);" >' + (navList[i].title != '' ? '<h3 class="title"></h3>' : '') + '<img src="' + navList[i].image + '"></a></div>';
                        }
                        html += '</div></div></div>';
                        if (getObjLength(navList) > 1) {
                            html += '<div class="swiper-pagination">';
                            var num = 0;
                            for (var i in navList) {
                                html += '<span class="swiper-pagination-switch' + (num == 0 ? ' swiper-active-switch' : '') + '"></span>';
                                num++;
                            }
                            html += '</div>';
                        }
                    }else if(domHtml.data('type') == '3'){
                    	html += '<ul class="custom-image clearfix">';
                        for (var i in navList) {
                        	// size = 1 -> type = 2
                            html += '<li class="custom-image-smalls">' + (navList[i].title != '' ? '<h3 class="title">' + navList[i].title + '</h3>' : '') + '<img src="' + navList[i].image + '"/></li>';
                        }
                        html += '</ul>';
                    } else {
                        html += '<ul class="custom-image clearfix">';
                        for (var i in navList) {
                        	// size = 1 -> type = 2
                            html += '<li' + (domHtml.data('type') == '2' ? ' class="custom-image-small"' : '') + '>' + (navList[i].title != '' ? '<h3 class="title">' + navList[i].title + '</h3>' : '') + '<img src="' + navList[i].image + '"/></li>';
                        }
                        html += '</ul>';
                    }
                    domHtml.html(html);
                }
            };
            var navList = domHtml.data('navList');
            for (var num in navList) {
                addContent(num);
            }
            rightHtml.find('.js-add-option').click(function() {
                addContent(-1);
            });
            $('#sidebarRegio').empty().html(rightHtml);
            var mySwiper = new Swiper('.swiper-container', {
                pagination: '.swiper-pagination',
                paginationClickable: true,
                loop: true,
                autoplay: 3000,
            });
        };
        clickArr['goods'] = function() {
            //console.log(dom);
            if (dom.find('.control-group .sc-goods-list').size() == 0) {
                domHtml = dom.find('.control-group');
                domHtml.html('<ul class="sc-goods-list clearfix size-2 card pic"></ul>').data({ 'goods': [], 'size': '1', 'size_type': '0', 'buy_btn': '1', 'buy_btn_type': '1', 'show_title': '0', 'price': '1' });
            } else {
                domHtml = dom.find('.control-group');
            }
            var varHtml = '<div>';
            varHtml += '<div class="form-horizontal">';
            varHtml += '<div class="js-meta-region" style="margin-bottom:20px;">';
            varHtml += '<div>';
            varHtml += '<div class="control-group">';
            varHtml += '<label class="control-label">选择商品：</label>';
            varHtml += '<div class="controls">';
            varHtml += '<ul class="module-goods-list clearfix ui-sortable" name="goods">';
            varHtml += '<li><a href="javascript:void(0);" class="js-add-goods add-goods">';
            varHtml += '<i class="icon-add"></i></a></li></ul>';
            varHtml += '</div></div>';
            varHtml += '<div class="control-group">';
            varHtml += '<label class="control-label">列表样式：</label>';
            varHtml += '<div class="controls">';
            varHtml += '<label class="radio inline"><input type="radio" name="size" value="1"/>一排两列</label>';
            varHtml += '<label class="radio inline">';
            varHtml += '<input type="radio" name="size" value="0"/>一排一列</label>'; //此处原value值为0：真正的一排一列
            // varHtml += '<label class="radio inline"><input type="radio" name="size" value="2"/>一大两小</label>';	
            // varHtml += '<label class="radio inline"><input type="radio" name="size" value="3"/>详细列表</label>';
            varHtml += '</div></div><div class="control-group"></div></div></div></div></div>';
            rightHtml = $(varHtml);
            var good_data = domHtml.data('goods');
            //console.log(good_data);
            var html = '';
            for (var i in good_data) {
                var item = good_data[i];
                html += '<li class="sort"><a href="javascript:void(0);"><img src="' + item.image + '" alt="' + item.title + '" title="' + item.title + '" width="50" height="50"></a><a class="close-modal js-delete-goods small hide" data-id="' + i + '" title="删除">×</a></li>';
            }
            rightHtml.find('.module-goods-list').prepend(html);
            rightHtml.find('.module-goods-list .sort .js-delete-goods').click(function() {
                var zindex = $(this).parents("li").index();
                var tdom = $(".editing").find("li");
                tdom[zindex].remove();
                $(this).closest('.sort').remove();
                var good_data = domHtml.data('goods');
                delete good_data[$(this).data('id')];
                domHtml.data('goods', good_data);
            });
            //上传商品
            widget_link_box(rightHtml.find('.js-add-goods'), 'good', function(result) {
                var good_data = domHtml.data('goods');
                if (good_data) {
                    $.merge(good_data, result);
                } else {
                    good_data = result;
                }
                domHtml.data('goods', good_data);
                rightHtml.find('.module-goods-list .sort').remove();
                var html = '';
                var value = $('input[name="size"]:checked').val();
                for (var i in good_data) {
                    var item = good_data[i];
                    html += '<li class="sort"><a href="javascript:void(0);" ><img src="' + item.image + '" alt="' + item.title + '" title="' + item.title + '" width="50" height="50"></a><a class="close-modal js-delete-goods small hide" data-id="' + item.id + '" title="删除">×</a></li>';
                }
                rightHtml.find('.module-goods-list').prepend(html);
                rightHtml.find('.module-goods-list .sort .js-delete-goods').click(function() {
                    var zindex = $(this).parents("li").index();
                    var tdom = $(".editing").find("li");
                    tdom[zindex].remove();
                    $(this).closest('.sort').remove();
                    var good_data = domHtml.data('goods');
                    delete good_data[$(this).data('id')];
                    domHtml.data('goods', good_data);
                });
            });
            //列表样式
            rightHtml.find('input[name="size"]').change(function() {
                //修改商品排列样式：一排一列、一排两列；防止后续又要变更回来
            	domHtml.data('size', $(this).val());
//            	domHtml.data('size', '1');
                changeStyleContent();
            }).each(function(i, item) {
                if ($(item).val() == domHtml.data('size')) {
                    $(item).prop('checked', true).change();
                }
            });
            function changeStyleContent() {
                var html = '';
                switch (domHtml.data('size')) {
                    case '0':
                        html = '<ul class="sc-goods-list clearfix size-2 card pic">';
                        for (var i in good_data) {
                            var item = good_data[i];
                            html += '<li class="goods-card big-pic card"><a href="javascript:void(0);" class="link js-goods clearfix"><div class="photo-block"><img class="goods-photo js-goods-lazy" src="' + item.image + '"/></div>';
                            html += '<div class="info clearfix  info-price"><p class="goods-title">' + item.title + '</p><p class="goods-price goods-price-icon"><em>￥' + item.price + '</em></p><p class="goods-price-taobao"></p></div>';
                            html += '</a></li>';
                        }
                        html += '</ul>';
                        break;
                    case '1':
                        html = '<ul class="sc-goods-list clearfix size-1 card pic">';
                        for (var i in good_data) {
                            var item = good_data[i];
                            html += '<li class="goods-card small-pic card"><a href="javascript:void(0);" class="link js-goods clearfix"><div class="photo-block"><img class="goods-photo js-goods-lazy" src="' + item.image + '"/></div>';
                            html += '<div class="info clearfix  info-price"><p class="goods-title">' + item.title + '</p><p class="goods-price goods-price-icon"><em>￥' + item.price + '</em></p><p class="goods-price-taobao"></p></div>';
                            html += '</a></li>';
                        }
                        html += '</ul>';
                        break;
                }
                domHtml.find('.sc-goods-list').replaceWith(html);
            }
            $('#sidebarRegio').empty().html(rightHtml);
        };
        if (dom.offset().top == 0) {
            $('.app-sidebar').css('margin-top', 120);
            $('.app-sidebar').css('display', 'none');
        } else {
            $('.app-sidebar').css('margin-top', dom.offset().top - $('.app-preview').offset().top);
            $('.app-sidebar').css('display', 'block');
        }
        var fieldType = dom.data('field-type');
        clickArr[fieldType]();
    },
    setEvent: function(obj) {
        var clickArr = [];
        var show_deletes = "";
        var app_field = $('<div class="app-field clearfix"><div class="control-group"><div class="component-border"></div></div><div class="actions"><div class="actions-wrap"><span class="action edit">编辑</span><span class="action add">加内容</span><span class="action delete">删除</span></div></div><div class="sort"><i class="sort-handler"></i></div></div>');
        app_field.data('field-type', obj.field_type);
        clickArr['image_nav'] = function() {
            var html = '<ul class="custom-nav-4 clearfix">';
            for (var i in obj.content) {
                obj.content[i].image = obj.content[i].image != '' ? obj.content[i].image : '';
                html += '<li><span class="nav-img-wap">' + (obj.content[i].image != '' ? '<img src="' + obj.content[i].image + '"/>' : '&nbsp;') + '</span>' + (obj.content[i].title != '' ? '<span class="title">' + obj.content[i].title + '</span>' : '') + '</li>';
            }
            html += '</ul>';
            app_field.find('.control-group').prepend(html);
            app_field.find('.custom-nav-4').data('navList', obj.content);
        };
        clickArr['image_ad'] = function() {
            var html = '';
            if (getObjLength(obj.content.nav_list) == 0) {
                html += '<div class="custom-image-swiper"><div class="swiper-container" style="height: 80px"><div class="swiper-wrapper"><img style="max-height:80px;display:block;" src=""/></div></div></div>';
                obj.content.nav_list = {};
            } else {
                if (!obj.content.image_type) {
                    obj.content.image_type = 0;
                }
                if (!obj.content.image_size) {
                    obj.content.image_size = 0;
                }
                var html = '';
                if (obj.content.image_type == '0') {
                    html += '<div class="custom-image-swiper"><div class="swiper-container" ><div class="swiper-wrapper">';
                    for (var i in obj.content.nav_list) {
                        obj.content.nav_list[i].image = obj.content.nav_list[i].image != '' ? obj.content.nav_list[i].image : '';
                        html += '<div  class="swiper-slide"><a href="javascript:void(0);" >' + (obj.content.nav_list[i].title != '' ? '<h3 class="title"></h3>' : '') + '<img src="' + obj.content.nav_list[i].image + '" style="max-height:' + obj.content.max_height + 'px;"/></a></div>';
                    }
                    html += '</div></div></div>';
                    if (getObjLength(obj.content.nav_list) > 1) {
                        html += '<div class="swiper-pagination">';
                        var num = 0;
                        for (var i in obj.content.nav_list) {
                            html += '<span class="swiper-pagination-switch' + (num == 0 ? ' swiper-active-switch' : '') + '"></span>';
                            num++;
                        }
                        html += '</div>';
                    }
                } else if(obj.content.image_type == '1'){
                    html += '<ul class="custom-image clearfix">';
                    for (var i in obj.content.nav_list) {
                        //obj.content.nav_list[i].image = obj.content.nav_list[i].image!='' ? './upload/'+obj.content.nav_list[i].image : '';
                        obj.content.nav_list[i].image = obj.content.nav_list[i].image != '' ? obj.content.nav_list[i].image : '';
                        html += '<li' + (obj.content.image_size == '1' ? ' class="custom-image-small"' : '') + 'style="height:95px;">' + (obj.content.nav_list[i].title != '' ? '<h3 class="title">' + obj.content.nav_list[i].title + '</h3>' : '') + '<img src="' + obj.content.nav_list[i].image + '"/></li>';
                    }
                    html += '</ul>';
                }else if(obj.content.image_type == '3'){
                    html += '<ul class="custom-image clearfix">';
                    for (var i in obj.content.nav_list) {
                    	// size = 1 -> type = 2
                        html += '<li class="custom-image-smalls">' + (obj.content.nav_list[i].title != '' ? '<h3 class="title">' + obj.content.nav_list[i].title + '</h3>' : '') + '<img src="' + obj.content.nav_list[i].image + '"/></li>';
                    }
                    html += '</ul>';
                }else {
                    html += '<ul class="custom-image clearfix">';
                    for (var i in obj.content.nav_list) {
                    	// size = 1 -> type = 2
                        html += '<li' + (obj.content.image_type == '2' ? ' class="custom-image-small"' : '') + '>' + (obj.content.nav_list[i].title != '' ? '<h3 class="title">' + obj.content.nav_list[i].title + '</h3>' : '') + '<img src="' + obj.content.nav_list[i].image + '"/></li>';
                    }
                    html += '</ul>';
                }
            }
            app_field.find('.control-group').prepend(html).data({ 'navList': obj.content.nav_list, 'type': (obj.content.image_type ? obj.content.image_type : 0), 'size': (obj.content.image_size ? obj.content.image_size : 0), 'max_height': (obj.content.max_height ? obj.content.max_height : 0), 'max_width': (obj.content.max_width ? obj.content.max_width : 0) });
        };
        clickArr['image_theme'] = function() {
            var html = '';
            if (getObjLength(obj.content.nav_list) == 0) {
                html += '<div class="custom-image-swiper"><div class="swiper-container" style="height: 80px"><div class="swiper-wrapper"><img style="max-height:80px;display:block;" src=""/></div></div></div>';
                obj.content.nav_list = {};
            } else {
                if (!obj.content.image_type) {
                    obj.content.image_type = 0;
                }
                if (!obj.content.image_size) {
                    obj.content.image_size = 0;
                }
                var html = '';
                if (obj.content.image_type == '0') {
                    html += '<div class="custom-image-swiper"><div class="swiper-container" ><div class="swiper-wrapper">';
                    for (var i in obj.content.nav_list) {
                        obj.content.nav_list[i].image = obj.content.nav_list[i].image != '' ? obj.content.nav_list[i].image : '';
                        html += '<div  class="swiper-slide"><a href="javascript:void(0);" >' + (obj.content.nav_list[i].title != '' ? '<h3 class="title"></h3>' : '') + '<img src="' + obj.content.nav_list[i].image + '" style="max-height:' + obj.content.max_height + 'px;"/></a></div>';
                    }
                    html += '</div></div></div>';
                    if (getObjLength(obj.content.nav_list) > 1) {
                        html += '<div class="swiper-pagination">';
                        var num = 0;
                        for (var i in obj.content.nav_list) {
                            html += '<span class="swiper-pagination-switch' + (num == 0 ? ' swiper-active-switch' : '') + '"></span>';
                            num++;
                        }
                        html += '</div>';
                    }
                } else if(obj.content.image_type == '1'){
                    html += '<ul class="custom-image clearfix">';
                    for (var i in obj.content.nav_list) {
                        //obj.content.nav_list[i].image = obj.content.nav_list[i].image!='' ? './upload/'+obj.content.nav_list[i].image : '';
                        obj.content.nav_list[i].image = obj.content.nav_list[i].image != '' ? obj.content.nav_list[i].image : '';
                        html += '<li' + (obj.content.image_size == '1' ? ' class="custom-image-small"' : '') + 'style="height:95px;">' + (obj.content.nav_list[i].title != '' ? '<h3 class="title">' + obj.content.nav_list[i].title + '</h3>' : '') + '<img src="' + obj.content.nav_list[i].image + '"/></li>';
                    }
                    html += '</ul>';
                }else {
                    html += '<ul class="custom-image clearfix">';
                    for (var i in obj.content.nav_list) {
                    	// size = 1 -> type = 2
                        html += '<li' + (obj.content.image_type == '2' ? ' class="custom-image-small"' : '') + '>' + (obj.content.nav_list[i].title != '' ? '<h3 class="title">' + obj.content.nav_list[i].title + '</h3>' : '') + '<img src="' + obj.content.nav_list[i].image + '"/></li>';
                    }
                    html += '</ul>';
                }
            }
            app_field.find('.control-group').prepend(html).data({ 'navList': obj.content.nav_list, 'type': (obj.content.image_type ? obj.content.image_type : 0), 'size': (obj.content.image_size ? obj.content.image_size : 0), 'max_height': (obj.content.max_height ? obj.content.max_height : 0), 'max_width': (obj.content.max_width ? obj.content.max_width : 0) });
        };
        
        clickArr['image_seckill'] = function() {
            var html = '';
            if (getObjLength(obj.content.nav_list) == 0) {
                html += '<div class="custom-image-swiper"><div class="swiper-container" style="height: 80px"><div class="swiper-wrapper"><img style="max-height:80px;display:block;" src=""/></div></div></div>';
                obj.content.nav_list = {};
            } else {
                if (!obj.content.image_type) {
                    obj.content.image_type = 0;
                }
                if (!obj.content.image_size) {
                    obj.content.image_size = 0;
                }
                var html = '';
                if (obj.content.image_type == '0') {
                    html += '<div class="custom-image-swiper"><div class="swiper-container" ><div class="swiper-wrapper">';
                    for (var i in obj.content.nav_list) {
                        obj.content.nav_list[i].image = obj.content.nav_list[i].image != '' ? obj.content.nav_list[i].image : '';
                        html += '<div  class="swiper-slide"><a href="javascript:void(0);" >' + (obj.content.nav_list[i].title != '' ? '<h3 class="title"></h3>' : '') + '<img src="' + obj.content.nav_list[i].image + '" style="max-height:' + obj.content.max_height + 'px;"/></a></div>';
                    }
                    html += '</div></div></div>';
                    if (getObjLength(obj.content.nav_list) > 1) {
                        html += '<div class="swiper-pagination">';
                        var num = 0;
                        for (var i in obj.content.nav_list) {
                            html += '<span class="swiper-pagination-switch' + (num == 0 ? ' swiper-active-switch' : '') + '"></span>';
                            num++;
                        }
                        html += '</div>';
                    }
                } else if(obj.content.image_type == '1'){
                    html += '<ul class="custom-image clearfix">';
                    for (var i in obj.content.nav_list) {
                        //obj.content.nav_list[i].image = obj.content.nav_list[i].image!='' ? './upload/'+obj.content.nav_list[i].image : '';
                        obj.content.nav_list[i].image = obj.content.nav_list[i].image != '' ? obj.content.nav_list[i].image : '';
                        html += '<li' + (obj.content.image_size == '1' ? ' class="custom-image-small"' : '') + 'style="height:95px;">' + (obj.content.nav_list[i].title != '' ? '<h3 class="title">' + obj.content.nav_list[i].title + '</h3>' : '') + '<img src="' + obj.content.nav_list[i].image + '"/></li>';
                    }
                    html += '</ul>';
                }else {
                    html += '<ul class="custom-image clearfix">';
                    for (var i in obj.content.nav_list) {
                    	// size = 1 -> type = 2
                        html += '<li' + (obj.content.image_type == '2' ? ' class="custom-image-small"' : '') + '>' + (obj.content.nav_list[i].title != '' ? '<h3 class="title">' + obj.content.nav_list[i].title + '</h3>' : '') + '<img src="' + obj.content.nav_list[i].image + '"/></li>';
                    }
                    html += '</ul>';
                }
            }
            app_field.find('.control-group').prepend(html).data({ 'navList': obj.content.nav_list, 'type': (obj.content.image_type ? obj.content.image_type : 0), 'size': (obj.content.image_size ? obj.content.image_size : 0), 'max_height': (obj.content.max_height ? obj.content.max_height : 0), 'max_width': (obj.content.max_width ? obj.content.max_width : 0) });
        };
        
        clickArr['goods'] = function() {
            //console.log(obj);
            if (obj.content.goods) {
                for (var i in obj.content.goods) {
                    obj.content.goods[i].image = obj.content.goods[i].image;
                }
            }
            //此处给已经添加的商品赋值给data
            app_field.find('.control-group').html('<ul class="sc-goods-list clearfix size-2 card pic"></ul>').data({ 'goods': obj.content.goods, 'size': (obj.content.size ? obj.content.size : '0'), 'size_type': (obj.content.size_type ? obj.content.size_type : '0'), 'buy_btn': (obj.content.buy_btn ? obj.content.buy_btn : '0'), 'buy_btn_type': (obj.content.buy_btn_type ? obj.content.buy_btn_type : '0'), 'show_title': (obj.content.show_title ? obj.content.show_title : '0'), 'price': (obj.content.price ? obj.content.price : '0') });
            customField.clickEvent(app_field);
            app_field.removeClass('editing');
            $('.js-config-region .app-field').eq(0).trigger('click');
        };
        
      //模块元素的值
        clickArr['image_module'] = function() {
            //console.log(obj);
            if (obj.content.image_module) {
                for (var i in obj.content.image_module) {
                    obj.content.image_module[i].image = obj.content.image_module[i].image;
                }
            }
            //此处给已经添加的商品赋值给data
            var goodshtml='';
            var html='';
            for (var i in obj.content.image_module) {
                var item = obj.content.image_module[i];
                goodshtml += '<li class="goods-card small-pic card"><a href="javascript:void(0);" class="link js-goods clearfix"><div class="photo-block"><img class="goods-photo js-goods-lazy" src="' + item.image + '"/></div>';
                goodshtml += '<div class="info clearfix  info-price"><p class="goods-title">' + item.title + '</p><p class="goods-price goods-price-icon"><em>￥' + item.price + '</em></p><p class="goods-price-taobao"></p></div>';
                goodshtml += '</a></li>';
            }
            html +='<div class="module_top"><div class="custom-image-swiper"><div class="swiper-container" style="height:85px"><div class="swiper-wrapper"><img style="display:block;" src="'+obj.content.picture+'"/></div></div></div></div><div class="title" style=" background-color:grey;font-size:20px;height:20px; line-height:20px; text-align:center; display:block;"><span style="">'+ obj.content.title +'</span></div><div class="module_bottom"><ul class="sc-goods-list clearfix size-2 card pic">';
            html +=goodshtml;
            html +='</ul></div>';
            app_field.find('.control-group').html(html).data({ 'image_module': obj.content.image_module, 'size': (obj.content.size ? obj.content.size : '0'), 'size_type': (obj.content.size_type ? obj.content.size_type : '0'), 'buy_btn': (obj.content.buy_btn ? obj.content.buy_btn : '0'), 'buy_btn_type': (obj.content.buy_btn_type ? obj.content.buy_btn_type : '0'), 'show_title': (obj.content.show_title ? obj.content.show_title : '0'), 'price': (obj.content.price ? obj.content.price : '0'), 'picture': (obj.content.picture ? obj.content.picture : '0'), 'title': (obj.content.title ? obj.content.title : '0') });
            customField.clickEvent(app_field);
            app_field.removeClass('editing');
            $('.js-config-region .app-field').eq(0).trigger('click');
        };
        
        //文本元素的值
        clickArr['text_module'] = function() {
            //console.log(obj);
            if (obj.content.text_module) {
                for (var i in obj.content.text_module) {
                    obj.content.text_module[i].image = obj.content.text_module[i].image;
                }
            }
            //此处给已经添加的商品赋值给data
            var goodshtml='';
            var html='';
            for (var i in obj.content.text_module) {
                var item = obj.content.text_module[i];
                goodshtml += '<li class="goods-card small-pic card"><a href="javascript:void(0);" class="link js-goods clearfix"><div class="photo-block"></div>';
                goodshtml += '<div class="info clearfix  info-price"><p class="goods-title">' + item.title + '</p><p class="goods-price goods-price-icon"><em>￥' + item.price + '</em></p><p class="goods-price-taobao"></p></div>';
                goodshtml += '</a></li>';
            }
            html +='<div class="module_top"><span class="title">'+obj.content.title+'</span><div class="custom-image-swiper"><div class="swiper-container" style="height:85px"><div class="swiper-wrapper"><img style="display:block;" src="'+obj.content.picture+'"/></div></div></div></div><div class="module_bottom"><ul class="sc-goods-list clearfix size-2 card pic">';
            html +=goodshtml;
            html +='</ul></div>';
            app_field.find('.control-group').html(html).data({ 'text_module': obj.content.image_module, 'size': (obj.content.size ? obj.content.size : '0'), 'size_type': (obj.content.size_type ? obj.content.size_type : '0'), 'buy_btn': (obj.content.buy_btn ? obj.content.buy_btn : '0'), 'buy_btn_type': (obj.content.buy_btn_type ? obj.content.buy_btn_type : '0'), 'show_title': (obj.content.show_title ? obj.content.show_title : '0'), 'price': (obj.content.price ? obj.content.price : '0'), 'picture': (obj.content.picture ? obj.content.picture : '0'), 'title': (obj.content.title ? obj.content.title : '0') });
            customField.clickEvent(app_field);
            app_field.removeClass('editing');
            $('.js-config-region .app-field').eq(0).trigger('click');
        };
        try {
            clickArr[obj.field_type]();
        } catch (e) {}
        $('.js-fields-region .app-fields').append(app_field);
    },
    checkEvent: function() {
        var returnArr = [];
        $.each($('.js-fields-region .app-field'), function(i, item) {
            returnArr[i] = customField.getContent($(item));
        });
        return returnArr;
    },
    getContent: function(dom) {
        var returnArr = [],
            returnObj = {},
            domHtml = {};
        returnArr['image_nav'] = function() {
            returnObj.type = 'image_nav';
            var navList = dom.find('.custom-nav-4').data('navList');
            var num = 10;
            for (var i in navList) {
                returnObj[num] = { title: navList[i].title, name: navList[i].name, prefix: navList[i].prefix, url: navList[i].url, image: navList[i].image.replace('./upload/', ''),webUrl: navList[i].webUrl };
                num++;
            }
        };
        returnArr['image_ad'] = function() {
            var domHtml = dom.find('.control-group');
            returnObj.type = 'image_ad';
            returnObj.image_type = domHtml.data('type');
            returnObj.image_size = domHtml.data('size');
            returnObj.max_height = domHtml.data('max_height');
            returnObj.max_width = domHtml.data('max_width');
            returnObj.nav_list = {};
            var navList = domHtml.data('navList');
            var num = 10;
            for (var i in navList) {
                returnObj.nav_list[num] = { title: navList[i].title, name: navList[i].name, prefix: navList[i].prefix, url: navList[i].url, image: navList[i].image.replace('./upload/', ''),webUrl: navList[i].webUrl };
                num++;
            }
        };
        returnArr['image_theme'] = function() {
            var domHtml = dom.find('.control-group');
            returnObj.type = 'image_theme';
            returnObj.image_type = domHtml.data('type');
            returnObj.image_size = domHtml.data('size');
            returnObj.max_height = domHtml.data('max_height');
            returnObj.max_width = domHtml.data('max_width');
            returnObj.nav_list = {};
            var navList = domHtml.data('navList');
            var num = 10;
            for (var i in navList) {
                returnObj.nav_list[num] = { title: navList[i].title, name: navList[i].name, prefix: navList[i].prefix, url: navList[i].url, image: navList[i].image.replace('./upload/', '') };
                num++;
            }
        };
        returnArr['image_seckill'] = function() {
            var domHtml = dom.find('.control-group');
            returnObj.type = 'image_seckill';
            returnObj.image_type = domHtml.data('type');
            returnObj.image_size = domHtml.data('size');
            returnObj.max_height = domHtml.data('max_height');
            returnObj.max_width = domHtml.data('max_width');
            returnObj.nav_list = {};
            var navList = domHtml.data('navList');
            var num = 10;
            for (var i in navList) {
                returnObj.nav_list[num] = { title: navList[i].title, name: navList[i].name, prefix: navList[i].prefix, url: navList[i].url, image: navList[i].image.replace('./upload/', '') };
                num++;
            }
        };
        returnArr['goods'] = function() {
            var domHtml = dom.find('.control-group');
            returnObj.type = 'goods';
            returnObj.size = domHtml.data('size');
            //returnObj.size_type = domHtml.data('size_type');
            // returnObj.buy_btn = domHtml.data('buy_btn');
            // returnObj.buy_btn_type = domHtml.data('buy_btn_type');
            // returnObj.show_title = domHtml.data('show_title');
            returnObj.price = domHtml.data('price');
            returnObj.goods = {};
            var goods = domHtml.data('goods');
            //alert(goods)
            var num = 0;
            //alert(obj2String(goods));
            // console.log(goods);
            for (var i in goods) {
                var image = goods[i].image;
                if (image) {
                    image = image.replace('./upload/', '');
                }
                returnObj.goods[num] = { id: goods[i].id, title: goods[i].title, price: goods[i].price, url: goods[i].url, image: image };
                num++;
            }
            //alert(obj2String(returnObj));
        };
        
      //得到模块元素的内容
        returnArr['image_module'] = function() {
            var domHtml = dom.find('.control-group');
            returnObj.type = 'image_module';
            returnObj.size = domHtml.data('size');
            returnObj.picture = domHtml.data('picture');
            returnObj.title = domHtml.data('title');
            //returnObj.size_type = domHtml.data('size_type');
            // returnObj.buy_btn = domHtml.data('buy_btn');
            // returnObj.buy_btn_type = domHtml.data('buy_btn_type');
            // returnObj.show_title = domHtml.data('show_title');
            returnObj.goods = {};
            var goods = domHtml.data('image_module');
            //alert(goods)
            var num = 0;
            //alert(obj2String(goods));
            // console.log(goods);
            for (var i in goods) {
                var image = goods[i].image;
                if (image) {
                    image = image.replace('./upload/', '');
                }
                returnObj.goods[num] = { id: goods[i].id, title: goods[i].title, price: goods[i].price, url: goods[i].url, image: image };
                num++;
            }
            //alert(obj2String(returnObj));
        };
        
      //得到文本元素的内容
        returnArr['text_module'] = function() {
            var domHtml = dom.find('.control-group');
            returnObj.type = 'text_module';
            returnObj.size = domHtml.data('size');
            returnObj.picture = domHtml.data('picture');
            returnObj.title = domHtml.data('title');
            //returnObj.size_type = domHtml.data('size_type');
            // returnObj.buy_btn = domHtml.data('buy_btn');
            // returnObj.buy_btn_type = domHtml.data('buy_btn_type');
            // returnObj.show_title = domHtml.data('show_title');
            returnObj.goods = {};
            var goods = domHtml.data('text_module');
            //alert(goods)
            var num = 0;
            //alert(obj2String(goods));
            // console.log(goods);
            for (var i in goods) {
                var image = goods[i].image;
                if (image) {
                    image = image.replace('./upload/', '');
                }
                returnObj.goods[num] = { id: goods[i].id, title: goods[i].title, price: goods[i].price, url: goods[i].url, image: image };
                num++;
            }
            //alert(obj2String(returnObj));
        }
        var fieldType = dom.data('field-type');
        returnArr[fieldType]();
        return returnObj;
    },
    setHtml: function(json,channelId,pageId,categoryType,discoveryType) {
    	window.wchannelId=channelId;
    	window.wpageId=pageId;
        window.wcategoryType=categoryType;
        window.wdiscoveryType = discoveryType;
    	var arr = json;
        for (var i in arr) {
            customField.setEvent(arr[i]);
        }
    },
    getData: function (){
      custom = JSON.stringify(customField.checkEvent());
      return custom;
    }
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