<%@ include file="/WEB-INF/pages/scripts/taglibs.jsp"%>

Ext.define('MyExt.productManager.ProductBindGiftPanel', {
    extend : 'Ext.Panel',
    alias: ['widget.productBindGiftPanel'],
    requires : ['MyExt.productManager.ProductSpecPicker'],
 	header: false,
	closable: true,
	columnLines: true,
	animCollapse: true,
	collapsible: true,
    scroll: 'both',
    autoScroll: true,
   	items:[],
	viewConfig: {	
		stripeRows: true,
		enableTextSelection: true
	},
		    
	initComponent : function(config) {
		Ext.apply(this, config);
		
		this.expressCodeStore = Ext.create('Ext.data.Store', {
        	autoDestroy: true,
        	model: 'InitModel',
        	data: [
        		<c:forEach var="map" varStatus="cvs" items="${allExpressCodeMaps}">
        		{strId: '${map.value.expressCode}', name: '${map.value.companyName}'}<c:if test="${!cvs.last}">,</c:if>
        		</c:forEach>
        	]
        });
         	this.buttons = [{
			text: '<fmt:message key="button.save"/>', 
			style: 'font-size: 14px;background: rgba(22, 155, 213, 1) none repeat scroll 0 0;border-color: rgba(22, 155, 213, 1);',
			scope: this,  
        	handler: this.sure
    	}];
    	this.callParent(arguments);
    	this.addItems(true);
    },
    
   sure : function(combo, record, index){
   
               var isCheckSucc = true;
               var productId = '';
			   var expressMap = [];
		    	this.items.each(function(form) {
	        		if(!form.isValid()){
	        			isCheckSucc = false;
	        		}else{
	        			var expressCode = form.down('combobox').getValue();
	        		    productId = form.down('[name=productId]').getValue();
	        			var expressNo = form.down('[name=expressNo]').getValue();
	        			expressMap.push({key: expressNo, value: expressCode});
	        		}
				}, this);
				
				if(!isCheckSucc){
					showMsg('<fmt:message key="ajax.confirm"/>', '<fmt:message key="ajax.no.record"/>');
					return;
				}	
console.log(this.productId)
    	Ext.Ajax.request({
        	url: '<c:url value="/storeWithdrawal/getStoreByMobile.json"/>',
         	method: 'post',
			scope: this,
			url: '<c:url value="/giftProduct/saveGiftProduct.json"/>',
			params:{productId: this.record.data.productId, expressMap: Ext.JSON.encode(expressMap)},
          	success: function(response){
        		var responseObject = Ext.JSON.decode(response.responseText);
        		if (responseObject.success == true){
        			if(responseObject.storeList != null && responseObject.storeList.length > 0){
       					try{
       						storeCombobox.store.removeAll();
       						for(var i = 0; i < responseObject.storeList.length; i ++){
       							storeCombobox.store.insert(i, {
									id: responseObject.storeList[i].storeId,
									name: responseObject.storeList[i].name
								});
       						}
       						storeCombobox.setValue(responseObject.storeId);
       					}catch(e){
    					}
       				}
        		}else{
	        		showFailMsg(responseObject.message, 4);	
	        	}
			}
     	})
    },
    
    addItems : function(isHiddenDelete){
    	var form = Ext.create('Ext.form.Panel', {
    		header: false,
       	 	layout: 'column',  
        	border: false,  
        	items: [{
			xtype: 'hiddenfield', 
			name: 'productId', 
		},{    
            	xtype: 'container',
	            layout: 'hbox',
	            defaultType: 'textfield', 
	            style: 'padding: 2px 2px 2px;',
            	items: [{
	        		width: 190,
	        		labelWidth: 55,
					xtype: 'combobox',
					fieldLabel: '<fmt:message key="order.expressCode" />',
			        displayField: 'name',
			        valueField: 'strId',
			        store: this.expressCodeStore,
			        style: 'padding: 0px 4px',
			        editable: false,
			        allowBlank: false,
			        queryMode: 'local',
			        typeAhead: true,
			        anchor: '99%'  
				},{    
					labelWidth: 55,
		        	xtype:'textfield',
		          	fieldLabel: '<fmt:message key="order.expressNo"/>',
					name: 'expressNo',
		            allowBlank: false,
		            anchor: '99%'  
	        	},{
		     		xtype: 'productSpecPicker',
			    	fieldLabel: '<fmt:message key="discovery.product" />',
			    	labelWidth: 60,
					name: 'productName',
					editable: false,
		         	anchor: '98%',
		         	objSelectType: this.selectType,
		         	typeAhead: true,
		         	listeners: {
		 				scope: this,
		 				itemClick : function(picker, record, item, index, e, eOpts){	
		 					picker.setRawValue(record.data.name + '->'+record.data.productTags );
							this.productIdField.setValue(record.data.productId);
						    this.productSpecIdField.setValue(record.data.productSpecId);	
		 				}
		 			}
				},{    
					hideLabel: true,  
                	xtype: 'button',
			        iconCls: 'delete',
			        scope: this,
			        hidden: isHiddenDelete,
			        handler: function(){
			        	this.remove(form, false);
			        	form.hide();  
			        } 
	        	},{    
					hideLabel: true,  
                	xtype: 'button',
			        iconCls: 'add',
			        scope: this,
			        hidden:this.isEdit,
			        handler: function(){
			        	this.addItems(false);
			        } 
	        	}]
        	}]
    	});
    	this.add(form);
    },
});
