<%@ include file="/WEB-INF/pages/scripts/taglibs.jsp"%>

Ext.define('MyExt.storeManager.DistributionChannelFormPanel', {
    extend : 'Ext.form.Panel',
    alias: ['widget.channelForm'],
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
		
	    if(this.isCreateChannel){
			 this.items = [{
				    xtype:'textfield',
				    fieldLabel: '<fmt:message key="fx.channel.channelName"/>',
				    labelWidth: 85,
				    readOnly: false,
				    name: 'channelName',
				    anchor:'97%'
				},{
				    xtype:'textfield',
				    fieldLabel: '<fmt:message key="fx.channel.sort"/>',
				    labelWidth: 85,
				    readOnly: false,
				    name: 'sort',
				    anchor:'97%'
				}];
		}else{
	    this.items = [{
			xtype: 'fieldset',
            title: Ext.String.format(this.fontStyle, '<fmt:message key="fx.channel.info"/>'),
            bodyPadding: '5 5 0',
            anchor: '99%',
            layout: 'hbox',
            items:[{
                xtype: 'container',
                flex: 1,
                layout: 'anchor',
                items: [{
				    xtype:'textfield',
				    fieldLabel: '<fmt:message key="fx.channel.channelId"/>',
				    labelWidth: 85,
				    readOnly: true,
				    name: 'channelId',
				    anchor:'97%'
				},{
				    xtype:'textfield',
				    fieldLabel: '<fmt:message key="fx.channel.channelName"/>',
				    labelWidth: 85,
				    readOnly: false,
				    name: 'channelName',
				    anchor:'97%'
				},{
				    xtype:'textfield',
				    fieldLabel: '<fmt:message key="fx.channel.createTime"/>',
				    labelWidth: 85,
				    readOnly: true,
				    name: 'createTime',
				    anchor:'97%'
				}]
				},{
			       xtype: 'container',
			       flex: 1,
			       layout: 'anchor',
			       items: [{
				    xtype:'textfield',
				    fieldLabel: '<fmt:message key="fx.channel.status"/>',
				    labelWidth: 85,
				    readOnly: true,
				    name: 'status',
				    anchor:'97%'
				},{
				    xtype:'textfield',
				    fieldLabel: '<fmt:message key="fx.channel.sort"/>',
				    labelWidth: 85,
				    readOnly: false,
				    name: 'sort',
				    anchor:'97%'
				},{
				    xtype:'textfield',
				    fieldLabel: '<fmt:message key="fx.channel.updateTime"/>',
				    labelWidth: 85,
				    readOnly: true,
				    name: 'updateTime',
				    anchor:'97%'
				}]
			   }]
        }];
       }
    	
    
     this.buttons = [{
     		hidden: this.isCreateChannel,
			text: '<fmt:message key="button.save"/>', 
			style: 'font-size: 14px;background: rgba(22, 155, 213, 1) none repeat scroll 0 0;border-color: rgba(22, 155, 213, 1);',
			scope: this,  
	        handler: function(){
	        	if(this.form.isValid()){
					Ext.MessageBox.confirm('<fmt:message key="ajax.confirm"/>', '<fmt:message key="save.confirm"/>', function(e){
						if(e == 'yes'){
			                this.form.submit({
			                    waitMsg: 'Loading...',
			                    url: '<c:url value="/channel/saveChannel.json"/>',
			                    scope: this,
			                   	success: function(form, action) {
			                        var responseObject = Ext.JSON.decode(action.response.responseText);
			                        if(responseObject.error == false){
			                        	this.channelId = responseObject.channelId;
			                       		showSuccMsg(responseObject.message);
									}else{
										showFailMsg(responseObject.message, 4);
									}
			                    }
			        		})
			        	}
			        }, this)
	        	}
	        }
	    }];
    	this.callParent();
    	
    	
    }
});