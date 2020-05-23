<%@ include file="/WEB-INF/pages/scripts/taglibs.jsp"%>

Ext.define('MyExt.orderManager.OrderFormPanel', {
    extend : 'Ext.form.Panel',
    alias: ['widget.orderForm'],
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
            name: 'orderId',
            allowBlank: true
        },{
        	xtype: 'fieldset',
            title: Ext.String.format(this.fontStyle, '<fmt:message key="order.info"/>'),
            bodyPadding: '5 5 0',
            anchor: '99%',
            layout: 'hbox',
            items:[{
                xtype: 'container',
                flex: 1,
                layout: 'anchor',
                items: [{
				    xtype:'textfield',
				    fieldLabel: '<fmt:message key="order.orderId"/>',
				    labelWidth: 85,
				    readOnly: true,
				    name: 'orderId',
				    anchor:'97%'
				},{
				    xtype:'textfield',
				    fieldLabel: '<fmt:message key="order.orderNo"/>',
				    labelWidth: 85,
				    readOnly: true,
				    name: 'orderNo',
				    anchor:'97%'
				},{
				    xtype:'textfield',
				    fieldLabel: '<fmt:message key="order.tradeNo"/>',
				    labelWidth: 85,
				    readOnly: true,
				    name: 'tradeNo',
				    anchor:'97%'
				},{
				    xtype:'textfield',
				    fieldLabel: '<fmt:message key="order.postage"/>',
				    labelWidth: 85,
				    readOnly: true,
				    name: 'postage',
				    anchor:'97%'
				},{
				    xtype:'textfield',
				    fieldLabel: '<fmt:message key="order.productNumber"/>',
				    labelWidth: 85,
				    readOnly: true,
				    name: 'productNumber',
				    anchor:'97%'
				},{
				    xtype:'textfield',
				    fieldLabel: '<fmt:message key="order.orderTotal"/>',
				    labelWidth: 85,
				    readOnly: true,
				    name: 'orderTotal',
				    anchor:'97%'
				},{
				    xtype:'textfield',
				    fieldLabel: '<fmt:message key="order.payAmount"/>',
				    labelWidth: 85,
				    readOnly: true,
				    name: 'payAmount',
				    anchor:'97%'
				},{
				    xtype:'textfield',
				    fieldLabel: '<fmt:message key="order.isPaymentSucc"/>',
				    labelWidth: 85,
				    readOnly: true,
				    name: 'isPaymentSucc',
				    anchor:'97%'
				},{
				    xtype:'textfield',
				    fieldLabel: '<fmt:message key="order.cancelMethod"/>',
				    labelWidth: 85,
				    readOnly: true,
				    name: 'cancelMethod',
				    anchor:'97%'
				},{
				    xtype:'textfield',
				    fieldLabel: '<fmt:message key="order.isCheck"/>',
				    labelWidth: 85,
				    readOnly: true,
				    name: 'isCheck',
				    anchor:'97%'
				},{
				    xtype:'textfield',
				    fieldLabel: '<fmt:message key="order.updateTime"/>',
				    labelWidth: 85,
				    readOnly: true,
				    name: 'updateTime',
				    anchor:'97%'
				 
				},{
				    xtype:'textfield',
				    fieldLabel: '<fmt:message key="order.acceptPayName"/>',
				    labelWidth: 85,
				    readOnly: true,
				    name: 'acceptPayName',
				    anchor:'97%'
				 
				}]
			   },{
			       xtype: 'container',
			       flex: 1,
			       layout: 'anchor',
			       items: [{
				    xtype:'textfield',
				    fieldLabel: '<fmt:message key="order.status"/>',
				    labelWidth: 85,
				    readOnly: true,
				    name: 'status',
				    anchor:'97%'
				},{
				    xtype:'textfield',
				    fieldLabel: '<fmt:message key="order.storeTopId"/>',
				    labelWidth: 85,
				    readOnly: true,
				    name: 'storeTopId',
				    anchor:'97%'
				},{
				    xtype:'textfield',
				    fieldLabel: '<fmt:message key="order.profitTop"/>',
				    labelWidth: 85,
				    readOnly: true,
				    name: 'profitTop',
				    anchor:'97%'
				},{
				    xtype:'textfield',
				    fieldLabel: '<fmt:message key="order.isSyncExpress"/>',
				    labelWidth: 85,
				    readOnly: true,
				    name: 'isSyncExpress',
				    anchor:'97%'
				},{
				    xtype:'textfield',
				    fieldLabel: '<fmt:message key="order.payTime"/>',
				    labelWidth: 85,
				    readOnly: true,
				    name: 'payTime',
				    anchor:'97%'
				},{
				    xtype:'textfield',
				    fieldLabel: '<fmt:message key="order.sentTime"/>',
				    labelWidth: 85,
				    readOnly: true,
				    name: 'sentTime',
				    anchor:'97%'
				},{
				    xtype:'textfield',
				    fieldLabel: '<fmt:message key="order.deliveryTime"/>',
				    labelWidth: 85,
				    readOnly: true,
				    name: 'deliveryTime',
				    anchor:'97%'
				},{
				    xtype:'textfield',
				    fieldLabel: '<fmt:message key="order.cancelTime"/>',
				    labelWidth: 85,
				    readOnly: true,
				    name: 'cancelTime',
				    anchor:'97%'
				},{
				    xtype:'textfield',
				    fieldLabel: '<fmt:message key="order.complateTime"/>',
				    labelWidth: 85,
				    readOnly: true,
				    name: 'complateTime',
				    anchor:'97%'
				},{
				    xtype:'textfield',
				    fieldLabel: '<fmt:message key="order.refundTime"/>',
				    labelWidth: 85,
				    readOnly: true,
				    name: 'refundTime',
				    anchor:'97%'
				},{
				    xtype:'textfield',
				    fieldLabel: '<fmt:message key="order.createTime"/>',
				    labelWidth: 85,
				    readOnly: true,
				    name: 'createTime',
				    anchor:'97%'
				},  {
				    xtype:'textfield',
				    fieldLabel: '<fmt:message key="order.interuptedStatus"/>',
				    labelWidth: 85,
				    readOnly: true,
				    name: 'interuptedStatus',
				    anchor:'97%'
				}, {
				    xtype:'textfield',
				    fieldLabel: '<fmt:message key="order.interuptedReason"/>',
				    labelWidth: 85,
				    readOnly: true,
				    name: 'interuptedReason',
				    anchor:'97%'
				}]
			   }]
        },{
            xtype: 'fieldset',
            title: Ext.String.format(this.fontStyle, '<fmt:message key="member.info"/>'),
            bodyPadding: '5 5 0',
            anchor: '99%',
            items:[{
			    xtype:'textfield',
			    fieldLabel: '<fmt:message key="order.userId"/>',
			    labelWidth: 85,
			    readOnly: true,
			    name: 'userId',
			    anchor:'97%'
			},{
            	xtype: 'container',
            	layout: 'hbox',
            	items: [{
	                xtype: 'container',
	                flex: 1,
	                layout: 'anchor',
	                items: [{
					    xtype:'textfield',
					    fieldLabel: '<fmt:message key="order.consigneePhone"/>',
					    labelWidth: 85,
					    readOnly: false,
					    name: 'consigneePhone',
					    anchor:'97%'
					}]
	            },{
	                xtype: 'container',
	                flex: 1,
	                layout: 'anchor',
	                items: [{
					    xtype:'textfield',
					    fieldLabel: '<fmt:message key="order.consignee"/>',
					    labelWidth: 85,
					    readOnly: false,
					    name: 'consignee',
					    anchor:'97%'
					}]
				}]
            },{
	            xtype: 'container',
	            layout: 'hbox',
	            bodyPadding: 10,
	            defaultType: 'textfield',
	            defaults: {labelWidth: 40},
	            anchor: '100%',
	            items: [{
	            	xtype: 'textfield',
			    	fieldLabel: '<fmt:message key="order.province" />',	
			    	style: 'padding: 0 25px 5px;',
					name: 'province',
		         	readOnly: true,
		         	anchor: '99%',
		         	width: 160
				},{
					xtype: 'textfield',
			    	fieldLabel: '<fmt:message key="order.city" />',
					style: 'padding: 0 25px 5px;',
					width: 160,
					name: 'city',
		         	readOnly: true,
		         	anchor: '99%'
				},{
					xtype: 'textfield',
			    	fieldLabel: '<fmt:message key="order.cityarea" />',
					style: 'padding: 0 25px 5px;',
					width: 160,
					name: 'cityarea',
		         	readOnly: true,
		         	anchor: '99%'
				}]
	        },{
				xtype:'textarea',
				fieldLabel: '<fmt:message key="order.address"/>',
				labelWidth: 85,
				readOnly: false,
				name: 'address',
				anchor: '99%'
			}]
        }];
       
        <jkd:haveAuthorize access="/order/editBuyertInfo.json">
        this.buttons = [{ 	
			text: '<fmt:message key="button.save"/>', 
			style: 'font-size: 14px;background: rgba(22, 155, 213, 1) none repeat scroll 0 0;border-color: rgba(22, 155, 213, 1);',
			scope: this,  
	        handler: function(){	
	        	var formValues = this.form.getValues();
	        	var orderId = formValues["orderId"];
	        	var identityNo = formValues["identityNo"];
	        	var identityName = formValues["identityName"];
	        	var consigneePhone = formValues["consigneePhone"];
	        	var consignee = formValues["consignee"];
	        	var address = formValues["address"];
	        	if(this.form.isValid()){
					Ext.MessageBox.confirm('<fmt:message key="ajax.confirm"/>', '<fmt:message key="save.confirm"/>', function(e){
						if(e == 'yes'){
			                this.form.submit({
			                    waitMsg: 'Loading...',
			                    url: '<c:url value="/order/editBuyertInfo.json"/>',
			                    scope: this,
			                    params:{
			                    	identityName : identityName, 
			                    	identityNo : identityNo,
									consigneePhone : consigneePhone, 
									consignee : consignee, 
									address : address
								},
			                    success: function(form, action) {
			                        var responseObject = Ext.JSON.decode(action.response.responseText);
			                        if(responseObject.error == false){
			                       		showSuccMsg(responseObject.message);
			                       		this.tabPanel.loadData(1);
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
	    </jkd:haveAuthorize>
    	this.callParent();
    }
});