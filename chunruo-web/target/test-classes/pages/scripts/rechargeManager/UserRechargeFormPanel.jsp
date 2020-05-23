<%@ include file="/WEB-INF/pages/scripts/taglibs.jsp"%>

Ext.define('MyExt.rechargeManager.UserRechargeFormPanel', {
    extend : 'Ext.form.Panel',
    alias: ['widget.userRechargeForm'],
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
            name: 'recordId',
            allowBlank: true
        },{
        	xtype: 'fieldset',
            title: Ext.String.format(this.fontStyle, '<fmt:message key="user.recharge.info"/>'),
            bodyPadding: '5 5 0',
            anchor: '99%',
            layout: 'hbox',
            items:[{
            	xtype: 'container',
                flex: 1,
                layout: 'anchor',
                items: [{
				    xtype:'textfield',
				    fieldLabel: '<fmt:message key="user.recharge.applicant"/>',
				    labelWidth: 85,
				    name: 'applicant',
				    anchor:'97%'
				},{
				    xtype:'textfield',
				    fieldLabel: '<fmt:message key="user.recharge.userId"/>',
				    labelWidth: 85,
				    name: 'userId',
				    anchor:'97%',
				    hidden: this.isSave,
				},{
				    xtype:'textfield',
				    fieldLabel: '<fmt:message key="user.recharge.mobile"/>',
				    labelWidth: 85,
				    name: 'mobile',
				    anchor:'97%',
				    hidden: this.isSave,
				},{
				    xtype:'textfield',
				    fieldLabel: '<fmt:message key="user.recharge.amount"/>',
				    labelWidth: 85,
				    name: 'amount',
				    anchor:'97%'
				},{
				    xtype:'textfield',
				    fieldLabel: '<fmt:message key="user.recharge.nickName"/>',
				    labelWidth: 85,
				    name: 'nickName',
				    hidden: this.isSave,
				    anchor:'97%'
				}]
			},{
		       xtype: 'container',
		       flex: 1,
		       layout: 'anchor',
		       items: [{
				    xtype:'textfield',
				    fieldLabel: '<fmt:message key="user.recharge.status"/>',
				    labelWidth: 85,
				    name: 'status',
				    hidden: this.isSave,
				    anchor:'97%',
				    setValue : function(val){  
				        if(val == 1){
							 this.setRawValue('<fmt:message key="user.recharge.status1"/>');
						}else if(val == 2 ){
							 this.setRawValue('<fmt:message key="user.recharge.status2"/>');
						}else if(val == 3){
							 this.setRawValue('<fmt:message key="user.recharge.status3"/>');
						}else if(val == 4){
							 this.setRawValue('<fmt:message key="user.recharge.status4"/>');
						}
					}
				},{
				    xtype:'textfield',
				    fieldLabel: '<fmt:message key="user.recharge.createTime"/>',
				    labelWidth: 85,
				    name: 'createTime',
				    anchor:'97%',
				    hidden: this.isSave,
				},{
				    xtype:'textfield',
				    fieldLabel: '<fmt:message key="user.recharge.updateTime"/>',
				    labelWidth: 85,
				    name: 'updateTime',
				    anchor:'97%',
				    hidden: this.isSave,
				},{
				    xtype:'textfield',
				    fieldLabel: '<fmt:message key="user.recharge.completeTime"/>',
				    labelWidth: 85,
				    name: 'completeTime',
				    hidden: this.isSave,
				    anchor:'97%'
				}]
			}]
        },{
            xtype: 'fieldset',
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
				        xtype: 'filefield',
				        name: 'mobileFile',
				        msgTarget: 'side',
				        allowBlank: false,
				        anchor: '100%',
				        buttonText: '<fmt:message key="order.import.mobile.file"/>',
				        hidden: !this.isSave
			    	},{
				    	xtype:'textarea',
				    	labelAlign: 'top',
				    	fieldLabel: '<fmt:message key="user.recharge.reason"/>',
				    	name: 'reason',
				    	anchor:'97%'
					},{
				    	xtype:'textarea',
				    	labelAlign: 'top',
				    	fieldLabel: '<fmt:message key="user.recharge.profitNotice"/>',
				    	name: 'profitNotice',
				    	anchor:'97%'
					},{
				        xtype: 'filefield',
				        name: 'attachmentFile',
				        msgTarget: 'side',
				        allowBlank: false,
				        anchor: '100%',
				        hidden: !this.isSave,
				        buttonText: '<fmt:message key="order.import.attachment.file"/>'
			    	}]
	            }]
            }]
        }];
        
        if(this.isCheck != '0'){
        	<jkd:haveAuthorize access="/userRecharge/downloadFile.msp">
        	this.buttons = [{ 	
				text: '<fmt:message key="user.recharge.downloadFile"/>', 
				scope: this,  
		        handler: function(){
			    	var recordId = this.getForm().findField('recordId').getValue();
			    	window.location.href  = '<c:url value="/userRecharge/downloadFile.msp?recordId="/>' + recordId;
				}
		    }];
		    </jkd:haveAuthorize>
		}
    	this.callParent();
    }
});