<%@ include file="/WEB-INF/pages/scripts/taglibs.jsp"%>
Ext.define('MyExt.userManager.FeedbackFormPanel', {
    extend : 'Ext.form.Panel',
    alias: ['widget.feedbackForm'],
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
	    
	    this.items = [{
            xtype: 'hidden',
            name: 'feedbackId',
            allowBlank: true
        },{
        	xtype: 'fieldset',
            title: Ext.String.format(this.fontStyle, '<fmt:message key="user.feedback.info"/>'),
            bodyPadding: '5 5 0',
            anchor: '99%',
            layout: 'hbox',
            items:[{
                xtype: 'container',
                flex: 1,
                layout: 'anchor',
                items: [{
				    xtype:'textfield',
				    fieldLabel: '<fmt:message key="user.feedbackId"/>',
				    labelWidth: 85,
				    readOnly: true,
				    name: 'feedbackId',
				    anchor:'97%'
				},{
				    xtype:'textfield',
				    fieldLabel: '<fmt:message key="user.userId"/>',
				    labelWidth: 85,
				    readOnly: true,
				    name: 'userId',
				    anchor:'97%'
				},{
				    xtype:'textfield',
				    fieldLabel: '<fmt:message key="user.userName"/>',
				    labelWidth: 85,
				    readOnly: true,
				    name: 'userName',
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
				    fieldLabel: '<fmt:message key="user.ftype"/>',
				    labelWidth: 85,
				    readOnly: true,
				    name: 'ftype',
				    anchor:'97%'
				},{
				    xtype:'textfield',
				    fieldLabel: '<fmt:message key="user.uuid"/>',
				    labelWidth: 85,
				    readOnly: true,
				    name: 'uuid',
				    anchor:'97%'
				}]
            },{
                xtype: 'container',
                flex: 1,
                layout: 'anchor',
                items: [{
				    xtype:'textfield',
				    fieldLabel: '<fmt:message key="user.userIp"/>',
				    labelWidth: 85,
				    readOnly: true,
				    name: 'userIp',
				    anchor:'97%'
				},{
				    xtype:'textfield',
				    fieldLabel: '<fmt:message key="user.isReply"/>',
				    labelWidth: 85,
				    readOnly: true,
				    name: 'isReply',
				    anchor:'97%'
				},{
				    xtype:'textfield',
				    fieldLabel: '<fmt:message key="user.isPushUser"/>',
				    labelWidth: 85,
				    readOnly: true,
				    name: 'isPushUser',
				    anchor:'97%'
				},{
				    xtype:'textfield',
				    fieldLabel: '<fmt:message key="user.createTime"/>',
				    labelWidth: 85,
				    readOnly: true,
				    name: 'createTime',
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
        },{
            xtype: 'fieldset',
            title: Ext.String.format(this.fontStyle, '<fmt:message key="user.feedback.remarks"/>'),
            bodyPadding: '5 5 0',
            anchor: '99%',
            items:[{
            	xtype: 'container',
            	layout: 'hbox',
            	items: [{
	                xtype: 'container',
	                flex: 1,
	                layout: 'anchor',
	                items: [{
				    	xtype:'textarea',
				    	labelAlign: 'top',
				    	fieldLabel: '<fmt:message key="user.content"/>',
				    	readOnly: true,
				    	name: 'content',
				    	anchor:'97%',
				    	height:100
					}]
	            },{
	                xtype: 'container',
	                flex: 1,
	                layout: 'anchor',
	                items: [{
					    xtype:'textarea',
					    labelAlign: 'top',
					    fieldLabel: '<fmt:message key="user.replyMsg"/>',
					    readOnly: true,
					    name: 'replyMsg',
					    anchor:'97%',
					    height:100
					}]
				}]
            }]
        }];
        
     
	        	this.buttons = [{ 	
				text: Ext.String.format(this.fontStyle, '<fmt:message key="user.feedback.reply"/>'),
				scope: this,  
		        handler: this.reply
		    }];
        
        
    	this.callParent();
    },
    
  reply : function(){
    	var replyPanel = Ext.create('MyExt.userManager.FeedbackReplyFormPanel', {
 //   				id: 'feedbackReplyFormPanel@FeedbackReplyFormPanel',
     				title: '<fmt:message key="button.add"/>'});
        
		var buttons = [{
			text: '<fmt:message key="button.save"/>',
			handler: function(){
	            if(replyPanel.form.isValid()){
	            	Ext.MessageBox.confirm('<fmt:message key="ajax.confirm"/>', '<fmt:message key="save.confirm"/>', function(e){
						if(e == 'yes'){
							var feedbackId = this.getForm().findField('feedbackId').getValue();
						  	replyPanel.form.submit({
			                    waitMsg: 'Loading...',
			                    url: '<c:url value="/feedback/replyMsg.json"/>',
			                    scope: this,
			                    params:{feedbackId: feedbackId},
			                    success: function(form, action) {
			                        var responseObject = Ext.JSON.decode(action.response.responseText);
			                       	if (responseObject.success == true){
		          						showSuccMsg(responseObject.msg);
		          						this.tabPanel.loadData();
		          						popWin.close();
		          					}else{
		          						showFailMsg(responseObject.msg, 4);
		          					}
			                    },
			                    failure: function(form, action) {
				                    var responseObject = Ext.JSON.decode(action.response.responseText);
				                    showFailMsg(responseObject.msg, 4);
			                    }
			        		})
			        		
			        	}
			        }, this)
	        	}
			},
			scope: this
		},{
			text: '<fmt:message key="button.cancel"/>',
			handler : function(){popWin.close();},
			scope: this
		}];
		openWin('<fmt:message key="user.feedback.reply"/>', replyPanel, buttons, 500, 180);
    
    }
});