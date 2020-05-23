<%@ include file="/WEB-INF/pages/scripts/taglibs.jsp"%>

Ext.define('MyExt.userManager.UserFormPanel', {
    extend : 'Ext.form.Panel',
    alias: ['widget.userForm'],
    requires : [],
 	header: false,
 	buttonAlign: 'center',
 	labelAlign: 'right',
 	labelWidth: 40,
 	bodyPadding: '5 5 0',
    defaultType: 'textfield',
    fontStyle: '<span style="font-size:14px;font-weight:bold;">{0}</span>',
    autoScroll: true,
    viewConfig: {
        stripeRows: true,
        enableTextSelection: true
    },
    
	initComponent : function(config) {
		Ext.apply(this, config);
		
	   	this.rendererApply= Ext.create('Ext.data.Store', {
        	autoDestroy: true,
        	model: 'InitModel',
        	data: [
        		{id: '2', name: '<fmt:message key="user.level2"/>'},
        		{id: '0', name: '<fmt:message key="user.level0"/>'}
        	]
        });
        
	    this.items = [{
            xtype: 'hidden',
            name: 'orderId',
            allowBlank: true
        },{
            xtype: 'fieldset',
            title: Ext.String.format(this.fontStyle, '<fmt:message key="user.info"/>'),
            bodyPadding: '5 5 0',
            anchor: '99%',
            layout: 'hbox',
            handler: this.userManagement,
            items:[{
                xtype: 'container',
                flex: 1,
                layout: 'anchor',
                items: [{
				    xtype:'textfield',
				    fieldLabel: '<fmt:message key="user.userId"/>',
				    labelWidth: 85,
				    readOnly: true,
				    name: 'userId',
				    anchor:'97%'
				},{
				    xtype:'textfield',
				    fieldLabel: '<fmt:message key="user.nickname"/>',
				    labelWidth: 85,
				    readOnly: true,
				    name: 'nickname',
				    anchor:'97%'
				},{
				    xtype:'textfield',
				    fieldLabel: '<fmt:message key="user.mobile"/>',
				    labelWidth: 85,
				    readOnly: true,
				    name: 'mobile',
				    anchor:'97%'
				},{
				    xtype:'textfield',
				    fieldLabel: '<fmt:message key="user.headerImage"/>',
				    labelWidth: 85,
				    readOnly: true,
				    name: 'headerImage',
				    anchor:'97%'
				},{
				    xtype:'textfield',
				    fieldLabel: '<fmt:message key="user.level"/>',
				    labelWidth: 85,
				    readOnly: true,
				    name: 'level',
				    anchor:'97%',
				    setValue : function(value){  
				            if(value==2){  
					            this.setRawValue('<fmt:message key="user.level2"/>');  
					        } else{
					            this.setRawValue('<fmt:message key="user.level0"/>');  
					        }
					    },
					id: 'grade'
				}]
            },{
                xtype: 'container',
                flex: 1,
                layout: 'anchor',
                items: [{
				    xtype:'textfield',
				    fieldLabel: '<fmt:message key="user.sex"/>',
				    labelWidth: 85,
				    readOnly: true,
				    name: 'sex',
				    anchor:'97%'
				},{
				    xtype:'textfield',
				    fieldLabel: '<fmt:message key="user.provinceName"/>',
				    labelWidth: 85,
				    readOnly: true,
				    name: 'provinceName',
				    anchor:'97%'
				},{
				    xtype:'textfield',
				    fieldLabel: '<fmt:message key="user.cityName"/>',
				    labelWidth: 85,
				    readOnly: true,
				    name: 'cityName',
				    anchor:'97%'
				},{
				    xtype:'textfield',
				    fieldLabel: '<fmt:message key="user.areaName"/>',
				    labelWidth: 85,
				    readOnly: true,
				    name: 'areaName',
				    anchor:'97%'
				},{
				    xtype:'textfield',
				    fieldLabel: '<fmt:message key="user.topUserId"/>',
				    labelWidth: 85,
				    readOnly: true,
				    name: 'topUserId',
				    anchor:'97%'
				},{
				    xtype:'textfield',
				    fieldLabel: '<fmt:message key="user.registerTime"/>',
				    labelWidth: 85,
				    readOnly: true,
				    name: 'registerTime',
				    anchor:'97%'
				},{
				    xtype:'textfield',
				    fieldLabel: '<fmt:message key="user.updateTime"/>',
				    labelWidth: 85,
				    readOnly: true,
				    name: 'updateTime',
				    anchor:'97%'
				}]
            }]
        }]; 
        
        
    	this.callParent();
	},
    
    editUserInfo : function(){
    	var level=Ext.getCmp('grade').getRawValue();
    	var pushLevel=Ext.getCmp('push').getRawValue();
    	var expireEndDate=Ext.getCmp('expireEndDate').getValue();
    	var v1ExpireEndDate=Ext.getCmp('v1ExpireEndDate').getValue();
    	var v2ExpireEndDate=Ext.getCmp('v2ExpireEndDate').getValue();
    	var v3ExpireEndDate=Ext.getCmp('v3ExpireEndDate').getValue();
    	var inviteCount=0;
    	var userEditPanel = Ext.create('MyExt.userManager.UserEditPanel', {
			id: 'userEditPanel@' + this.id,
    		viewer: this.viewer,
    		level: level,
    		pushLevel:pushLevel,
    		expireEndDate:expireEndDate,
    		v1ExpireEndDate:v1ExpireEndDate,
    		v2ExpireEndDate:v2ExpireEndDate,
    		v3ExpireEndDate:v3ExpireEndDate
   	 	});
   	 	
    	var buttons = [{
			text: '<fmt:message key="button.save"/>',
			handler: function(){
				var isCheckSucc = true;
				var expressMap = [];
				var level='';
				var expireTime='';
				var v2ExpireTime='';
				var v3ExpireTime='';
				var pushLevel = '';
		    	userEditPanel.items.each(function(form) {
	        		if(!form.isValid()){
	        			isCheckSucc = false;
	        		}else{
	        		     level=form.down('[name=level]').getValue();
	        		     pushLevel=form.down('[name=pushLevel]').getValue();
	        			 expireTime =Ext.util.Format.date(form.down('[name=expireEndDate]').getValue(),'Y-m-d H:i:s');
	        			 v2ExpireTime =Ext.util.Format.date(form.down('[name=v2ExpireEndDate]').getValue(),'Y-m-d H:i:s');
	        			 v3ExpireTime =Ext.util.Format.date(form.down('[name=v3ExpireEndDate]').getValue(),'Y-m-d H:i:s');
	        		}
				}, this);
				if(!isCheckSucc){
					showMsg('<fmt:message key="ajax.confirm"/>', '<fmt:message key="ajax.no.record"/>');
					return;
				}	

				Ext.Msg.confirm('<fmt:message key="ajax.confirm"/>', '<fmt:message key="save.confirm"/>', function(e){
					if(e == 'yes'){
				     	Ext.Ajax.request({
				        	url: '<c:url value="/user/updateUserLevel.json"/>',
				         	method: 'post',
							scope: this,
							params:{userId: this.record.data.userId,level: level,expireTime:expireTime,pushLevel:pushLevel,v2ExpireTime:v2ExpireTime,v3ExpireTime:v3ExpireTime},
				          	success: function(response){
		          				var responseObject = Ext.JSON.decode(response.responseText);
		          				if(responseObject.success == true){
		                       		showSuccMsg(responseObject.message);
		                       		this.tabPanel.loadData();
		                       		popWin.close();
								}else{
									showFailMsg(responseObject.message, 4);
								}
							}
				     	})
				     }
				}, this)
			},
			scope: this
		},{
			text: '<fmt:message key="button.cancel"/>',
			handler : function(){popWin.close();},
			scope: this
		}];
      	openWin(Ext.String.format('<fmt:message key="user.level.management"/>',this.record.data.userId), userEditPanel, buttons, 280, 250);
    },   
    
    editTopUser : function(){
    	var topMobile=this.record.data.topMobile;
    	console.log(topMobile+"=========");
    	var userEditPanel = Ext.create('MyExt.userManager.UserTopEditPanel', {
			id: 'userEditPanel@' + this.id,
    		viewer: this.viewer,
    		topMobile: topMobile
   	 	});
    	var buttons = [{
			text: '<fmt:message key="button.save"/>',
			handler: function(){
				var isCheckSucc = true;
				var newMobile='';
		    	userEditPanel.items.each(function(form) {
	        		if(!form.isValid()){
	        			isCheckSucc = false;
	        		}else{
	        		     newMobile=form.down('[name=newMobile]').getValue();
	        		}
				}, this);
				if(!isCheckSucc){
					showMsg('<fmt:message key="ajax.confirm"/>', '<fmt:message key="ajax.no.record"/>');
					return;
				}	

				Ext.Msg.confirm('<fmt:message key="ajax.confirm"/>', '<fmt:message key="save.confirm"/>', function(e){
					if(e == 'yes'){
				     	Ext.Ajax.request({
				        	url: '<c:url value="/user/updateTopUser.json"/>',
				         	method: 'post',
							scope: this,
							params:{userId: this.record.data.userId,newMobile:newMobile},
				          	success: function(response){
		          				var responseObject = Ext.JSON.decode(response.responseText);
		          				if(responseObject.success == true){
		                       		showSuccMsg(responseObject.message);
		                       		this.tabPanel.loadData();
		                       		popWin.close();
								}else{
									showFailMsg(responseObject.message, 4);
								}
							}
				     	})
				     }
				}, this)
			},
			scope: this
		},{
			text: '<fmt:message key="button.cancel"/>',
			handler : function(){popWin.close();},
			scope: this
		}];
      	openWin(Ext.String.format('<fmt:message key="user.level.management"/>',this.record.data.userId), userEditPanel, buttons, 280, 170);
    } 
});