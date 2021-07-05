<%@ include file="/WEB-INF/pages/scripts/taglibs.jsp"%>

Ext.define('MyExt.storeManager.StoreWithdrawalFormPanel', {
    extend : 'Ext.form.Panel',
    alias: ['widget.storeWithdrawalForm'],
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
		
		this.withdrawalTypeStore = Ext.create('Ext.data.Store', {
        	autoDestroy: true,
        	model: 'InitModel',
        	data: [
        		{id: 0, name: '<fmt:message key="store.withdrawal.withdrawalType.0"/>'},
        		{id: 1, name: '<fmt:message key="store.withdrawal.withdrawalType.1"/>'}
        	]
        });
        
        this.bankStore = Ext.create('Ext.data.Store', {
        	autoDestroy: true,
        	model: 'InitModel',
        	data: [
				<c:forEach var="bank" varStatus="status" items="${allBankMaps}" >
				{id: ${bank.value.bankId}, name: '${bank.value.name}'}<c:if test="${!vs.last}">,</c:if>
				</c:forEach>
			]
		});
        
        
        
        this.withdrawalStatusStore = Ext.create('Ext.data.Store', {
        	autoDestroy: true,
        	model: 'InitModel',
        	data: [
        		{id: 1, name: '<fmt:message key="store.withdrawal.status.1"/>'},
        		{id: 2, name: '<fmt:message key="store.withdrawal.status.2"/>'},
        		{id: 3, name: '<fmt:message key="store.withdrawal.status.3"/>'},
        		{id: 4, name: '<fmt:message key="store.withdrawal.status.4"/>'}
        	]
        });
	    
	    this.items = [{
            xtype: 'hidden',
            name: 'storeId',
            allowBlank: true
        },{
           	xtype: 'container',
           	layout: 'hbox',
           	items: [{
                xtype: 'container',
                flex: 1,
                layout: 'anchor',
                items: [{
				    xtype:'textfield',
				    fieldLabel: '<fmt:message key="store.withdrawal.amount"/>',
				    labelWidth: 85,
				    readOnly: true,
				    name: 'amount',
				    anchor:'97%'
				},{
				    xtype:'textfield',
				    fieldLabel: '<fmt:message key="store.withdrawal.userName"/>',
				    labelWidth: 85,
				    readOnly: true,
				    name: 'userName',
				    anchor:'97%'
				}]
       		}]
       	}];
        
   
        
        
        this.buttons = [{
			text: '<fmt:message key="store.withdrawal.status.3"/>', 
			style: 'font-size: 14px;background: rgba(254, 82, 2, 1) none repeat scroll 0 0;border-color: rgba(254, 82, 2, 1);',
			scope: this,  
	        handler: function(){
	        	Ext.Msg.confirm('<fmt:message key="ajax.confirm"/>', '<fmt:message key="store.withdrawal.succ.confirm"/>', function(e){
					if(e == 'yes'){
						Ext.Ajax.request({
				        	url: '<c:url value="/storeWithdrawal/withdrawalSucc.json"/>',
				         	method: 'post',
							scope: this,
							params:{recordId: this.record.data.recordId},
				          	success: function(response){
		          				var responseObject = Ext.JSON.decode(response.responseText);
		          				if (responseObject.success == true){
		          					showSuccMsg(responseObject.message);
									this.tabPanel.loadData();
		          				}else{
		          					showFailMsg(responseObject.message, 4);
		          				}
							}
				     	})
			     	}
			 	}, this)
	        }
	    },{
			text: '<fmt:message key="store.withdrawal.status.4"/>', 
			style: 'font-size: 14px;background: rgba(153, 153, 153, 1) none repeat scroll 0 0;border-color: rgba(153, 153, 153, 1);',
			scope: this,  
	        handler: this.failureReason
	    }];
    	this.callParent();
    },
      
    failureReason : function(){
    	var replyPanel = Ext.create('MyExt.storeManager.StoreWithdrawalFailureReasonFormPanel', {title: '<fmt:message key="button.add"/>'});
		var buttons = [{
			text: '<fmt:message key="button.save"/>',
			handler: function(){
	            if(replyPanel.form.isValid()){
	            	Ext.MessageBox.confirm('<fmt:message key="ajax.confirm"/>', '<fmt:message key="save.confirm"/>', function(e){
						if(e == 'yes'){
							var recordId = this.record.data.recordId;
						  	replyPanel.form.submit({
			                    waitMsg: 'Loading...',
			                    url: '<c:url value="/storeWithdrawal/withdrawalFail.json"/>',
			                    scope: this,
			                    params:{recordId: recordId},
			                    success: function(form, action) {
			                        var responseObject = Ext.JSON.decode(action.response.responseText);
			                       	if (responseObject.success == true){
		          						showSuccMsg(responseObject.message);
		          						this.tabPanel.loadData();
		          						popWin.close();
		          					}else{
		          						showFailMsg(responseObject.messsage, 4);
		          					}
			                    },
			                    failure: function(form, action) {
				                    var responseObject = Ext.JSON.decode(action.response.responseText);
				                    showFailMsg(responseObject.message, 4);
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
		openWin('<fmt:message key="store.withdrawal.failure.remarks"/>', replyPanel, buttons, 500, 180);
    
    },
    
    edit : function(){
          Ext.ComponentQuery.query('button[name="btn"]')[0].show();
          this.getForm().findField('bankCardUser').setReadOnly(false);
          this.getForm().findField('bankId').setReadOnly(false);
          this.getForm().findField('openingBank').setReadOnly(false);
          this.getForm().findField('bankCard').setReadOnly(false);
          this.getForm().findField('aliAccount').setReadOnly(false);
          this.getForm().findField('aliRealName').setReadOnly(false);
    }
    
    
});