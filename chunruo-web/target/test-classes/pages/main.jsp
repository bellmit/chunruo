<%@ include file="/WEB-INF/pages/scripts/taglibs.jsp"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="en">
<head>
	<meta http-equiv="Content-type" content="text/html;charset=utf-8" />
	<link rel="stylesheet" type="text/css" href="<c:url value='/styles/main.css'/>" />
	<link rel="stylesheet" type="text/css" href="<c:url value='/styles/uzen.css'/>" />
	<link rel="stylesheet" type="text/css" href="<c:url value='/styles/icon.css'/>" />
	<link rel="stylesheet" type="text/css" href="<c:url value='/ext/classic/theme-classic/resources/theme-classic-all.css'/>" />
	<link rel="stylesheet" type="text/css" href="<c:url value='/ext/packages/ux/classic/crisp/resources/ux-all.css'/>" />
	<link rel="stylesheet" type="text/css" href="<c:url value='/xedit/css/base.css'/>" />
	<link rel="stylesheet" type="text/css" href="<c:url value='/xedit/css/jquery.ui.css'/>" />
	<link rel="stylesheet" type="text/css" href="<c:url value='/xedit/css/swiper-3.3.1.min.css'/>" />
</head>
<body>
<div id="loading-mask" style=""></div>
<div id="loading">
    <div class="loading-indicator">
        <img src="<c:url value='/ximages/extanim32.gif'/>" width="32" height="32" style="margin-right:8px;float:left;vertical-align:top;"/><a href="http://www.ibengda.cn"><fmt:message key="webapp.name" /></a>
        <br /><span id="loading-msg">Loading styles and images...</span>
    </div>
</div>
<script type="text/javascript">document.getElementById('loading-msg').innerHTML = 'Loading Core API...';</script>
<script type="text/javascript" src="<c:url value='/ext/ext-all.js'/>"></script>
<script type="text/javascript">document.getElementById('loading-msg').innerHTML = 'Loading UI Components...';</script>
<script type="text/javascript" src="<c:url value='/ext/classic/theme-classic/theme-classic.js'/>"></script>
<script type="text/javascript" src="<c:url value='/ext/packages/ux/classic/ux.js'/>"></script>
<script type="text/javascript" charset="utf-8" src="<c:url value='/jquery/ueditor/ueditor.config.js'/>"></script>
<script type="text/javascript" charset="utf-8" src="<c:url value='/jquery/ueditor/ueditor.all.js'/>"></script>
<script type="text/javascript" charset="utf-8" src="<c:url value='/jquery/ueditor/lang/zh-cn/zh-cn.js'/>"></script>
<script type="text/javascript" charset="utf-8" src="<c:url value='/xedit/js/jquery.min.js'/>"></script>
<script type="text/javascript" charset="utf-8" src="<c:url value='/xedit/js/jquery-ui.js'/>"></script>
<script type="text/javascript" charset="utf-8" src="<c:url value='/xedit/js/customField.js'/>"></script>
<script type="text/javascript" charset="utf-8" src="<c:url value='/xedit/js/base.js'/>"></script>
<script type="text/javascript" charset="utf-8" src="<c:url value='/xedit/js/util.js'/>"></script>
<script type="text/javascript" charset="utf-8" src="<c:url value='/xedit/js/swiper.min.js'/>"></script>
<script type="text/javascript" src="<c:url value='/common/initModel.js'/>"></script>
<script type="text/javascript">document.getElementById('loading-msg').innerHTML = 'Initializing...';</script>
<div id="header" >
	<div class="logo"><img height="32" src="/ximages/sencha_logo_thumb.png"/></div>
    <div class="logo_version">
    	<span class="fa fa-bars" id="mobile-main-nav-menu-btn"></span>
    	<h2 class="product-header">
    		<span style="font-size: 21px;"><fmt:message key="webapp.name"/></span>
    	</h2>
    	<span class="toolkit-switch"><fmt:message key="webapp.version"/></span>
    	<span class="fa fa-cog" id="mobile-context-menu-btn"></span>
    </div>
   	<div class="loginSection">
   		<span>
		<fmt:message key="user.current.status"/>${pageContext.request.remoteUser}&nbsp;
			|&nbsp;<a href="#" onclick="openEditForm()" style="cursor:pointer;color: #ffffff;"><fmt:message key="login.edit.user"/></a>
			|&nbsp;<a href="<c:url value='/logout'/>"  style="cursor:pointer;color: #ffffff;"><fmt:message key="user.logout"/></a>
			&nbsp;&nbsp;
		</span>
   </div>
</div>
<script type="text/javascript">
Ext.Loader.setConfig({enabled: true});
Ext.Loader.setPath('Ext.ux', '<c:url value="/ext/ux"/>');
Ext.Loader.setPath('MyExt', '<c:url value="/scripts"/>');
Ext.globalEval = Ext.global.execScript ? function(code) {
	if(code != null && code != ''){
		if($$code.indexOf("id=\"loginForm\"") != -1){
			document.location="<c:url value='/logout'/>";
		}
	}
    execScript(code);
} : function($$code) {
    (function(){
    	if($$code != null && $$code != ''){
			if($$code.indexOf("id=\"loginForm\"") != -1){
				document.location="<c:url value='/logout'/>";
			}
		}
        var Ext = this.Ext;
        eval($$code);
    }());
};
Ext.onReady(function() {
	Ext.QuickTips.init();
	
	Ext.Ajax.addListener("requestexception", function(conn, response, options, eOpts){
		var json = Ext.decode(response.responseText);
		if(json != null 
				&& json["success"] != undefined 
				&& json.success == false){
			if(json["isLogin"] != undefined && json.isLogin == true){
				// 已经另一台机器登录，您被迫下线
				Ext.Msg.confirm('<fmt:message key="ajax.confirm"/>', json.message, function(e){
					document.location="<c:url value='/logout'/>";
				});
			}else{
				// 无访问权限提示
				showFailMsg(json.message, 4);	
			}
		}
	}, this);
	
	Ext.Ajax.addListener("requestcomplete", function(conn, response, opt) {
		var json = response.responseText;
		if(json != null && json != ''){
			if(json.indexOf("/j_security_check") != -1){
				document.location="<c:url value='/logout'/>";
			}
		}
	});
      
   	//header --------------------------------
  	this.header = Ext.create('Ext.Panel', { 
		region: 'north',
		margins: '0 0 3 0',
		height: 82,
		style: {background: '#eeeeee none repeat scroll 0 0'},
		bbar: Ext.create('MyExt.HeaderToolbar', {
			height: 50,
			scope: this,
			xtype: 'button',
			dock: 'bottom', 
			viewer: this,
			layout: { type: 'hbox', pack: 'left' },
      		viewer: this
     	}),
		items:[{
			xtype: 'container',
			region: 'north',
			el: 'header',
			height: 35,
			layout: 'table',
			margins: '0 0 3 0',
			layoutConfig: {
		    	padding: '0',
		    	align: 'form'
			},
		}]
	});
    	
  	//mainPanel ------------------------
 	this.mainPanel = Ext.create('MyExt.MainPanel', {
    	id: 'main-tabs',
    	viewer: this,
    	region: 'center', 
    	resizeTabs: true,
    	margins: '0 2 0 2',
    	minTabWidth: 120	
    });
      
    //footer --------------------------------
  	this.footer = Ext.create('Ext.Toolbar', {
       id: 'footer',
       region: 'south',
       frame: true,        
       height: 30,
       margins: '2 1 0 1',
       items:[
           '<fmt:message key="webapp.version"/>',
           new Ext.toolbar.Fill(),
           "&copy; <fmt:message key='copyright.year'/> <a href='<fmt:message key='company.url'/>' target='_blank'><fmt:message key='company.name'/></a>"
       ]
  	});
    
  	var viewport = Ext.create('Ext.container.Viewport', {
     	layout: 'border',
       	rtl: true,
        items: [
        	this.header, 
        	this.mainPanel, 
        	this.footer
       	]
   	});
  	
  	openEditForm = function(){
  		var userFormPanel = Ext.create('MyExt.systemManager.UserFormPanel', {id: 'userFormPanel@' + this.id, isEditor: true, isMyEditor: true});
  		userFormPanel.load({   
    		waitMsg: '<fmt:message key="ajax.waitMsg"/>',   
    		waitTitle: '<fmt:message key="ajax.waitTitle"/>',
    		url: '<c:url value="/userSys/myUser.json"/>', 
    		params: {userId: 1}, 
    		failure : function (form, action) {
     			showMsg('<fmt:message key="ajax.waitTitle"/>', '<fmt:message key="ajax.load.failure"/>', 4);   
    		}   
   		});
   		
    	var buttons = [{
			text: '<fmt:message key="button.save"/>',
			handler: function(){
				if(userFormPanel.form.isValid()){
					userFormPanel.form.submit({
	                    waitMsg: 'Loading...',
	                    url: '<c:url value="/userSys/updateMyUser.json"/>',
	                    scope: this,
	                    success: function(form, action) {
	                        var responseObject = Ext.JSON.decode(action.response.responseText);
	                        if(responseObject.error == false){
	                       		showSuccMsg(responseObject.message);
								popWin.close();
							}else{
								showFailMsg(responseObject.message, 4);
							}
	                    }
	        		})
	        	}
			},
			scope: this
		},{
			text: '<fmt:message key="button.cancel"/>',
			handler : function(){popWin.close();},
			scope: this
		}]
		openWin('<fmt:message key="login.edit.user"/>', userFormPanel, buttons, 400, 400);
    }
    
    openWin = function(title, tabs, button, width, height){
        var wd = (width == null) ? 600 : width;
        var hg = (height == null) ? 600 : height;
        popWin = Ext.getCmp("popWin");
        if(popWin){	popWin.close(); popWin = null; }
        popWin = Ext.create('widget.window', {
        	id: 'popWin',
            title: title,
            layout: 'fit',
            width: wd,
            height: hg,
            plain: true,
            modal: true,
        	shim: true,
            buttonAlign: 'center',  
            minimizable: false,
            maximizable: true,                      
            constrainHeader: true,
            collapsible: false,
            autoDestroy: true,
		    items: tabs,
		    buttons: button
        }); 
        popWin.show();
    };
    
    openOtherWin = function(title, tabs, button, width, height){
        var wd = (width == null) ? 600 : width;
        var hg = (height == null) ? 600 : height;
        popOtherWin = Ext.getCmp("popOtherWin");
        if(popOtherWin){popOtherWin.close(); popOtherWin = null; }
        popOtherWin = Ext.create('widget.window', {
        	id: 'popOtherWin',
            title: title,
            layout: 'fit',
            width: wd,
            height: hg,
            plain: true,
            modal: true,
        	shim: true,
            buttonAlign: 'center',  
            minimizable: false,
            maximizable: true,                      
            constrainHeader: true,
            collapsible: false,
            autoDestroy: true,
		    items: tabs,
		    buttons: button
        }); 
        popOtherWin.show();
    };
   
    openFormWin = function(title, tabs, button, width, height){
        var wd = (width == null) ? 600 : width;
        var hg = (height == null) ? 600 : height;
        popFormWin = Ext.getCmp("popFormWin");
        if(popFormWin){	popFormWin.close(); popFormWin = null; }
        popFormWin = Ext.create('widget.window', {
        	id: 'tagPopWin',
            title: title,
            layout: 'fit',
            width: wd,
            height: hg,
            plain: true,
            modal: true,
        	shim: true,
            buttonAlign: 'center',  
            minimizable: false,
            maximizable: true,                      
            constrainHeader: true,
            collapsible: false,
            autoDestroy: true,
		    buttons: button,
		    items: Ext.create('Ext.form.Panel',{
            	labelAlign: 'top',
            	border: false,
                bodyBorder: false,
                bodyPadding: '5 5 0',
            	bodyStyle: {zoom:'1',position:'relative'}, 
       			header: false,
            	items: tabs
        	})
        }); 
        popFormWin.show();
    };
  
  	var msgCt;
    createBox =  function (t, s){
        return '<div class="msg"><h3>' + t + '</h3><p>' + s + '</p></div>';
    }
    
    showSuccMsg = function(format, secs){
    	showMsg('<fmt:message key="ajax.request.success"/>', format, secs);
    }
    showFailMsg = function(format, secs){
    	showMsg('<fmt:message key="ajax.request.failure"/>', format, secs);
    }
    
    showWarnMsg = function(format, secs){
    	showMsg('<fmt:message key="ajax.confirm"/>', format, secs);
    }
    
    showMsg = function(title, format, secs){
        if(!msgCt){
            msgCt = Ext.DomHelper.insertFirst(document.body, {id:'msg-div'}, true);
        }
        var s = Ext.String.format.apply(String, Array.prototype.slice.call(arguments, 1));
        var m = Ext.DomHelper.append(msgCt, createBox(title, s), true);
        m.hide();
        var pause_secs = 2;
        if(secs != undefined && secs != null && secs > 0)
        	pause_secs = secs;
        m.slideIn('t').ghost("t", { delay: pause_secs * 1000, remove: true});
    }
    
    var hideMask = function () {
        Ext.get('loading').remove();
        Ext.fly('loading-mask').animate({
            opacity: 0,
            remove: true
        });
    };
    Ext.defer(hideMask, 250);
});
</script>
</body>
</html>