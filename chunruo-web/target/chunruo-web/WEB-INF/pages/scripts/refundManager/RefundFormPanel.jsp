<%@ include file="/WEB-INF/pages/scripts/taglibs.jsp"%>
Ext.define('MyExt.refundManager.RefundFormPanel', {
    extend : 'Ext.form.Panel',
    alias: ['widget.refundForm'],
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
	        	{id: '12', name: '<fmt:message key="refund.remarkd.reason12"/>'},
	        	{id: '11', name: '<fmt:message key="refund.remarkd.reason11"/>'},
	        	{id: '10', name: '<fmt:message key="refund.remarkd.reason10"/>'},
	        	{id: '9', name: '<fmt:message key="refund.remarkd.reason9"/>'},
	        	{id: '8', name: '<fmt:message key="refund.remarkd.reason8"/>'},
	        	{id: '7', name: '<fmt:message key="refund.remarkd.reason7"/>'},
        	    {id: '6', name: '<fmt:message key="refund.remarkd.reason6"/>'},
        	    {id: '5', name: '<fmt:message key="refund.remarkd.reason5"/>'},
        	    {id: '4', name: '<fmt:message key="refund.remarkd.reason4"/>'},
        		{id: '3', name: '<fmt:message key="refund.remarkd.reason3"/>'},
        		{id: '2', name: '<fmt:message key="refund.remarkd.reason2"/>'},
        		{id: '1', name: '<fmt:message key="refund.remarkd.reason1"/>'},
        	]});
	    
	    this.items = [{
            xtype: 'hidden',
            name: 'refundId',
            allowBlank: true
        },{
        	xtype: 'fieldset',
            title: Ext.String.format(this.fontStyle, '<fmt:message key="order.refund.info"/>'),
            bodyPadding: '5 5 0',
            anchor: '99%',
            layout: 'hbox',
            items:[{
            	xtype: 'container',
                flex: 1,
                layout: 'anchor',
                items: [{
				    xtype:'textfield',
				    fieldLabel: '<fmt:message key="order.refund.refundNumber"/>',
				    labelWidth: 85,
				    readOnly: true,
				    name: 'refundNumber',
				    anchor:'97%'
				},{
				    xtype:'textfield',
				    fieldLabel: '<fmt:message key="order.refund.orderId"/>',
				    labelWidth: 85,
				    readOnly: true,
				    name: 'orderId',
				    anchor:'97%'
				},{
				    xtype:'textfield',
				    fieldLabel: '<fmt:message key="order.refund.orderItemId"/>',
				    labelWidth: 85,
				    readOnly: true,
				    name: 'orderItemId',
				    anchor:'97%'
				},{
				    xtype:'textfield',
				    fieldLabel: '<fmt:message key="user.mobile"/>',
				    labelWidth: 85,
				    readOnly: true,
				    name: 'userMobile',
				    anchor:'97%'
				},{
				    xtype:'textfield',
				    fieldLabel: '<fmt:message key="order.refund.productId"/>',
				    labelWidth: 85,
				    readOnly: true,
				    name: 'productName',
				    anchor:'97%'
				},{
				    xtype:'textfield',
				    fieldLabel: '<fmt:message key="order.refund.refundCount"/>',
				    labelWidth: 85,
				    readOnly: true,
				    name: 'refundCount',
				    anchor:'97%'
				},{
				    xtype:'textfield',
				    fieldLabel: '<fmt:message key="order.refund.productPrice"/>',
				    labelWidth: 85,
				    readOnly: true,
				    name: 'productPrice',
				    anchor:'97%'
				},{
				    xtype:'textfield',
				    fieldLabel: '<fmt:message key="order.refund.refundAmount"/>',
				    labelWidth: 85,
				    readOnly: true,
				    name: 'refundAmount',
				    anchor:'97%'
				},{
				    xtype:'textfield',
				    fieldLabel: '<fmt:message key="order.refund.refundType"/>',
				    labelWidth: 85,
				    readOnly: true,
				    name: 'refundType',
				    setValue : function(value){  
					        if(value==1){  
					            this.setRawValue('<fmt:message key="order.refund.refundType.1"/>');  
					        } else if(value == 2){  
					            this.setRawValue('<fmt:message key="order.refund.refundType.2"/>');  
					        } else {
					        this.setRawValue('<fmt:message key="order.refund.refundType.3"/>');
					        } 
					    },  
				    anchor:'97%'
				},{
				    xtype:'textfield',
				    fieldLabel: '<fmt:message key="order.refund.createdAt"/>',
				    labelWidth: 85,
				    readOnly: true,
				    name: 'createTime',
				    anchor:'97%'
				}
			]},{
		       xtype: 'container',
		       flex: 1,
		       layout: 'anchor',
		       items: [{
			    xtype:'textfield',
			    fieldLabel: '<fmt:message key="order.orderNo"/>',
			    labelWidth: 85,
			    readOnly: true,
			    name: 'orderNo',
			    anchor:'97%'
			},{
			    xtype:'textfield',
			    fieldLabel: '<fmt:message key="order.refund.reasonId"/>',
			    labelWidth: 85,
			    readOnly: true,
			    name: 'reason',
			    anchor:'97%'
			},{
			    xtype:'textfield',
			    fieldLabel: '<fmt:message key="order.refund.userId"/>',
			    labelWidth: 85,
			    readOnly: true,
			    name: 'userId',
			    anchor:'97%'
			},{
			    xtype:'textfield',
			    fieldLabel: '<fmt:message key="order.refund.refundStatus"/>',
			    labelWidth: 85,
			    readOnly: true,
			    name: 'refundStatus',
			    setValue : function(val){  
				        if(val == 1){
							 this.setRawValue('<fmt:message key="order.refund.refundStatus_1"/>');
						}else if(val == 2 ){
							 this.setRawValue('<fmt:message key="order.refund.refundStatus_2"/>');
						}else if(val == 3){
							 this.setRawValue('<fmt:message key="order.refund.refundStatus_3"/>');
						}else if(val == 4){
							 this.setRawValue('<fmt:message key="order.refund.refundStatus_4"/>');
						}else if(val == 5){
							 this.setRawValue('<fmt:message key="order.refund.refundStatus_5"/>');
						}else if(val == 6){
							 this.setRawValue('<fmt:message key="order.refund.refundStatus_6"/>');
						}
				    },
			    anchor:'97%'
			},{
			    xtype:'textfield',
			    fieldLabel: '<fmt:message key="order.refund.expressNumber"/>',
			    labelWidth: 85,
			    readOnly: true,
			    name: 'expressNumber',
			    anchor:'97%'
			},{
			    xtype:'textfield',
			    fieldLabel: '<fmt:message key="order.refund.expressCompany"/>',
			    labelWidth: 85,
			    readOnly: true,
			    name: 'expressCompany',
			    anchor:'97%'
			},{
			    xtype:'textfield',
			    fieldLabel: '<fmt:message key="order.refund.isReceive"/>',
			    labelWidth: 85,
			    readOnly: true,
			    name: 'isReceive',
			    setValue : function(value){  
				        if(value == '1'){  
				            this.setRawValue('<fmt:message key="button.yes"/>');  
				        } else {  
				            this.setRawValue('<fmt:message key="button.no"/>');  
				        }  
				    },
			    anchor:'97%'
			},{
			    xtype:'textfield',
			    fieldLabel: '<fmt:message key="order.refund.updatedAt"/>',
			    labelWidth: 85,
			    readOnly: true,
			    name: 'updateTime',
			    anchor:'97%'
			},{
			    xtype:'textfield',
			    fieldLabel: '<fmt:message key="order.refund.completedAt"/>',
			    labelWidth: 85,
			    readOnly: true,
			    name: 'completedTime',
			    anchor:'97%'
			}]
		   }]
        },{
            xtype: 'fieldset',
            title: Ext.String.format(this.fontStyle, '<fmt:message key="order.refund.remarks"/>'),
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
				    	fieldLabel: '<fmt:message key="order.refund.refundExplain"/>',
				    	readOnly: true,
				    	name: 'refundExplain',
				    	anchor:'97%'
					},{
				    	xtype:'textarea',
				    	labelAlign: 'top',
				    	fieldLabel: '<fmt:message key="order.refund.expressExplain"/>',
				    	readOnly: true,
				    	name: 'expressExplain',
				    	anchor:'97%'
					},{
					xtype: 'combobox',
					fieldLabel: '<fmt:message key="button.remark.reason" />',
			        displayField: 'name',
			        valueField: 'id',
			        store: this.rendererApply,
			        editable: false,
			        allowBlank: false,
			        queryMode: 'local',
			        typeAhead: true,
			        anchor: '97%' ,
			        name: 'remarkReasonId'
				}]
	            },{
	                xtype: 'container',
	                flex: 1,
	                layout: 'anchor',
	                items: [{
					    xtype:'textarea',
					    labelAlign: 'top',
					    fieldLabel: '<fmt:message key="order.refund.refusalReason"/>',
					    readOnly: true,
					    name: 'refusalReason',
					    anchor:'97%'
					},{
					    xtype:'textarea',
					    labelAlign: 'top',
					    fieldLabel: '<fmt:message key="refund.remarks"/>',
					    name: 'remarks',
					    anchor:'97%',
					}]
				}]
            }]
        },{
            xtype: 'fieldset',
            title: Ext.String.format(this.fontStyle, '<fmt:message key="order.refund.image"/>'),
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
					xtype: 'box', 
					   	width: 400, 
					    height: 300, 
						html:'<img  height="300" width="400"  name="image1"  src="">'
					},{ 
						xtype: 'box', 
					   	width: 400, 
					    height: 300, 
						html:'<img  height="300" width="400"  name="image2"  src="">'
					},{ 
						xtype: 'box', 
					   	width: 400, 
					    height: 300, 
						html:'<img  height="300" width="400"  name="image3"  src="">'
					}]
	            }]
            }]	
        },{
            xtype: 'fieldset',
            title: Ext.String.format(this.fontStyle, '<fmt:message key="order.refund.expressImage"/>'),
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
					xtype: 'box', 
					   	width: 400, 
					    height: 300, 
						html:'<img  height="300" width="400"  name="expressImage1"  src="">'
					},{ 
						xtype: 'box', 
					   	width: 400, 
					    height: 300, 
						html:'<img  height="300" width="400"  name="expressImage2"  src="">'
					},{ 
						xtype: 'box', 
					   	width: 400, 
					    height: 300, 
						html:'<img  height="300" width="400"  name="expressImage3"  src="">'
					}]
	            }]
            }]
        }];
        
        var isCheck = Ext.util.Cookies.get('isCheck');
      	if(isCheck == '1'){
	     	this.buttons = [
	     	<jkd:haveAuthorize access="/refund/checkRefund.json">
	     	{ 	
				text: '<fmt:message key="refund.agree"/>', 
				scope: this,  
		        handler: this.refundAgree
		    }
		    <c:set var="isHaveAuthorize" value="true" />
			</jkd:haveAuthorize>
			<jkd:haveAuthorize access="/refund/checkRefund.json">
			<c:if test="${isHaveAuthorize}">,</c:if>
		    '-',{ 	
				text: '<fmt:message key="refund.unagree"/>', 
				scope: this,  
		        handler: this.refundUnagree
		    }
		    <c:set var="isHaveAuthorize" value="true" />
			</jkd:haveAuthorize>
			<jkd:haveAuthorize access="/refund/editRemarks.json">
			<c:if test="${isHaveAuthorize}">,</c:if>
		    {
				text: '<fmt:message key="button.remarks"/>',
				style: 'font-size: 14px;background: rgba(173, 56, 99, 1) none repeat scroll 0 0;border-color: rgba(173, 56, 99, 1);',
				scope: this,  
		        handler: function(){   
	                var refundId=this.getForm().findField('refundId').getValue();
	                var remarks=this.getForm().findField('remarks').getValue();
					Ext.Msg.confirm('<fmt:message key="ajax.confirm"/>', '<fmt:message key="save.confirm"/>', function(e){
						if(e == 'yes'){
					     	Ext.Ajax.request({
					        	url: '<c:url value="/refund/editRemarks.json"/>',
					         	method: 'post',
								scope: this,
								params:{refundId:refundId,remarks:remarks},
					          	success: function(response){
			          				var responseObject = Ext.JSON.decode(response.responseText);
			          				if(responseObject.success == true){
			                       		showSuccMsg(responseObject.message);
			                       		this.tabPanel.loadData();
	                  				    parent.store.load();
			                       		popWin.close();
			                       		
									}else{
										showFailMsg(responseObject.message, 4);
									}
								}
					     	})
					     }
					}, this)
				}
		    }
		    </jkd:haveAuthorize>
		    <jkd:haveAuthorize access="/refund/editRemarkReason.json">
			<c:if test="${isHaveAuthorize}">,</c:if>
		    {
				text: '<fmt:message key="button.remark.reason"/>',
				style: 'font-size: 14px;background: rgba(173, 56, 99, 1) none repeat scroll 0 0;border-color: rgba(173, 56, 99, 1);',
				scope: this,  
		        handler: function(){   
	                var refundId=this.getForm().findField('refundId').getValue();
	               var remarkReasonId =this.getForm().findField('remarkReasonId').getValue();
					Ext.Msg.confirm('<fmt:message key="ajax.confirm"/>', '<fmt:message key="save.confirm"/>', function(e){
						if(e == 'yes'){
					     	Ext.Ajax.request({
					        	url: '<c:url value="/refund/editRemarkReason.json"/>',
					         	method: 'post',
								scope: this,
								params:{refundId:refundId,remarkReasonId:remarkReasonId},
					          	success: function(response){
			          				var responseObject = Ext.JSON.decode(response.responseText);
			          				if(responseObject.success == true){
			                       		showSuccMsg(responseObject.message);
			                       		this.tabPanel.loadData();
	                  				    parent.store.load();
			                       		popWin.close();
			                       		
									}else{
										showFailMsg(responseObject.message, 4);
									}
								}
					     	})
					     }
					}, this)
				}
		    }
		    <c:set var="isHaveAuthorize" value="true" />
			</jkd:haveAuthorize>
		    ]
        }else{
        	
        	this.buttons = [
        	<jkd:haveAuthorize access="/refund/editRemarks.json">
        	{
				text: '<fmt:message key="button.remarks"/>', 
				style: 'font-size: 14px;background: rgba(173, 56, 99, 1) none repeat scroll 0 0;border-color: rgba(173, 56, 99, 1);',
				scope: this,  
		        handler: function(){   
	                var refundId=this.getForm().findField('refundId').getValue();
	                var remarks=this.getForm().findField('remarks').getValue();
					Ext.Msg.confirm('<fmt:message key="ajax.confirm"/>', '<fmt:message key="save.confirm"/>', function(e){
						if(e == 'yes'){
					     	Ext.Ajax.request({
					        	url: '<c:url value="/refund/editRemarks.json"/>',
					         	method: 'post',
								scope: this,
								params:{refundId:refundId,remarks:remarks},
					          	success: function(response){
			          				var responseObject = Ext.JSON.decode(response.responseText);
			          				if(responseObject.success == true){
			                       		showSuccMsg(responseObject.message);
			                       		this.tabPanel.loadData();
	                  				    parent.store.load();
			                       		popWin.close();
			                       		
									}else{
										showFailMsg(responseObject.message, 4);
									}
								}
					     	})
					     }
					}, this)
				}
		    }
		    </jkd:haveAuthorize>
		    <jkd:haveAuthorize access="/refund/editRemarkReason.json">
			<c:if test="${isHaveAuthorize}">,</c:if>
		    {
				text: '<fmt:message key="button.remark.reason"/>',
				style: 'font-size: 14px;background: rgba(173, 56, 99, 1) none repeat scroll 0 0;border-color: rgba(173, 56, 99, 1);',
				scope: this,  
		        handler: function(){   
	                var refundId=this.getForm().findField('refundId').getValue();
	                var remarkReasonId =this.getForm().findField('remarkReasonId').getValue();
					Ext.Msg.confirm('<fmt:message key="ajax.confirm"/>', '<fmt:message key="save.confirm"/>', function(e){
						if(e == 'yes'){
					     	Ext.Ajax.request({
					        	url: '<c:url value="/refund/editRemarkReason.json"/>',
					         	method: 'post',
								scope: this,
								params:{refundId:refundId,remarkReasonId:remarkReasonId},
					          	success: function(response){
			          				var responseObject = Ext.JSON.decode(response.responseText);
			          				if(responseObject.success == true){
			                       		showSuccMsg(responseObject.message);
			                       		this.tabPanel.loadData();
	                  				    parent.store.load();
			                       		popWin.close();
			                       		
									}else{
										showFailMsg(responseObject.message, 4);
									}
								}
					     	})
					     }
					}, this)
				}
		    }
		    <c:set var="isHaveAuthorize" value="true" />
			</jkd:haveAuthorize>
		    ]
		    
        }
    	this.callParent();
    },
    
    refundAgree : function(){
    	var refundType = this.getForm().findField('refundType').getValue(); 
    	var productName =  this.getForm().findField('productName').getValue();
    	var refundAmount =  this.getForm().findField('refundAmount').getValue();
    	var refundStatus =  this.getForm().findField('refundStatus').getValue();
    	var remarks= this.getForm().findField('remarks').getValue();
    	var agreePanel = Ext.create('MyExt.refundManager.RefundAgreeFormPanel', {
			productName:productName,
			refundAmount:refundAmount,
			refundType:refundType,
			refundStatus:refundStatus,
			title: '<fmt:message key="button.add"/>'
     	});
     				
		var buttons = [{
			text: '<fmt:message key="button.save"/>',
			handler: function(){
				this.checkRefund(agreePanel, true, '<fmt:message key="save.confirm"/>',remarks);
			},
			scope: this
		},{
			text: '<fmt:message key="button.cancel"/>',
			handler : function(){popWin.close();},
			scope: this
		}];
		openWin('<fmt:message key="button.check"/>', agreePanel, buttons, 550, 260);
	},
    
    checkRefund : function(agreePanel, isRefundTips, saveConfirm,remarks){
    	if(agreePanel.form.isValid()){
           	Ext.MessageBox.confirm('<fmt:message key="ajax.confirm"/>', saveConfirm, function(e){
				if(e == 'yes'){
					var refundId = this.getForm().findField('refundId').getValue();
				 	agreePanel.form.submit({
	                    waitMsg: 'Loading...',
	                    url: '<c:url value="/refund/checkRefund.json"/>',
	                    scope: this,
	                    params:{refundId: refundId, remarks:remarks, isRefundTips: isRefundTips},
	                    success: function(form, action) {
	                        var responseObject = Ext.JSON.decode(action.response.responseText);
	                       	if (responseObject.success == true){
	                       		if(responseObject.isRefundTips != null && responseObject.isRefundTips == true){
	                       			this.checkRefund(agreePanel, false, responseObject.msg,remarks);
	                       		}else{
	                       			showSuccMsg(responseObject.msg);
          							Ext.StoreMgr.get('uncheckRefundStore').reload();
          							popWin.close();
	                       		}
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
    
  	refundUnagree : function(){
  		var remarks= this.getForm().findField('remarks').getValue();
    	var unagreePanel = Ext.create('MyExt.refundManager.RefundUnagreeFormPanel', {
     		title: '<fmt:message key="button.add"/>'
     	});
     	
		var buttons = [{
			text: '<fmt:message key="button.save"/>',
			handler: function(){
	            if(unagreePanel.form.isValid()){
	            	Ext.MessageBox.confirm('<fmt:message key="ajax.confirm"/>', '<fmt:message key="save.confirm"/>', function(e){
						if(e == 'yes'){
							var refundId = this.getForm().findField('refundId').getValue();
						  	unagreePanel.form.submit({
			                    waitMsg: 'Loading...',
			                    url: '<c:url value="/refund/checkRefund.json"/>',
			                    scope: this,
			                    params:{refundId: refundId,remarks:remarks},
			                    success: function(form, action) {
			                        var responseObject = Ext.JSON.decode(action.response.responseText);
			                       	if (responseObject.success == true){
		          						showSuccMsg(responseObject.msg);
		          						Ext.StoreMgr.get('uncheckRefundStore').reload();
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
		openWin('<fmt:message key="button.check"/>', unagreePanel, buttons, 500, 200);
    }
});