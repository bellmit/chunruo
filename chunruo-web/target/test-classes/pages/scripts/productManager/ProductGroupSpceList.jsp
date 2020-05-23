<%@ include file="/WEB-INF/pages/scripts/taglibs.jsp"%>

Ext.define('MyExt.productManager.ProductGroupSpceList', {
    extend : 'Ext.grid.GridPanel',
    requires : [ 'MyExt.productManager.ProductPicker'],
	region: 'center',
	autoScroll: true,   
	closable: true,
	multiSelect: true,
	columnLines: true,
	animCollapse: false,
    enableLocking: true,
    isEditor: false,
    productId: null,
    productGroupId: null,
    selModel: 'cellmodel',
    plugins: {
    	ptype: 'cellediting',
        clicksToEdit: 1
    },
    viewConfig: {
        stripeRows: true,
        enableTextSelection: true
    },
    
	initComponent : function(config) {
		Ext.apply(this, config);	
		
		this.store = Ext.create('Ext.data.Store', {
	    	autoDestroy: true,
	     	model: 'ProductGroup',
	     	data: []
	    });
		 
		if(!this.isEditor){
			this.tbar = [{
	     		xtype: 'productPicker',
		    	fieldLabel: '<fmt:message key="product.wholesale.name" />',
		    	labelWidth: 60,
		    	width: 500,
				name: 'name',
				editable: false,
	         	allowBlank: false,
	         	anchor: '98%',
	         	typeAhead: true,
	         	listeners: {
	 				scope: this,
	 				itemClick : function(picker, record, item, index, e, eOpts){
	 					picker.setRawValue(record.data.name);
	 					this.productId = record.data.productId;
	 					this.loadData();
	 				}
	 			}
			}];
		}
		
		this.columns = [
			{text: '<fmt:message key="product.wholesale.wholesaleId"/>', dataIndex: 'objectId', width: 65, sortable : true,
        		filter: {xtype: 'textfield'}
        	},
        	{text: '<fmt:message key="product.wholesale.productCode"/>', dataIndex: 'productCode', width: 140, sortable : true,
        		filter: {xtype: 'textfield'}
        	},
        	{text: '<fmt:message key="product.wholesale.groupPriceWholesale"/>', dataIndex: 'groupPriceWholesale', width: 85, sortable : true,
        		filter: {xtype: 'textfield'},
        		editor: 'textfield'
        	},
        	{text: '<fmt:message key="product.wholesale.groupPriceRecommend"/>', dataIndex: 'groupPriceRecommend', width: 85, sortable : true,
        		filter: {xtype: 'textfield'},
        		editor: 'textfield'
        	},
        	{text: '<fmt:message key="product.wholesale.saleTimes"/>', dataIndex: 'saleTimes', width: 85, sortable : true,
        		filter: {xtype: 'textfield'},
        		editor: 'textfield'
        	},
        	{text: '<fmt:message key="product.wholesale.priceWholesale"/>', dataIndex: 'priceWholesale', width: 65, sortable : true,
        		filter: {xtype: 'textfield'}
        	},
        	{text: '<fmt:message key="product.wholesale.priceRecommend"/>', dataIndex: 'priceRecommend', width: 65, sortable : true,
        		filter: {xtype: 'textfield'}
        	},
        	{text: '<fmt:message key="product.wholesale.priceCost"/>', dataIndex: 'priceCost', width: 65, sortable : true,
        		filter: {xtype: 'textfield'}
        	},
        	{text: '<fmt:message key="product.wholesale.quantity"/>', dataIndex: 'stockNumber', width: 60, sortable : true,
        		filter: {xtype: 'textfield'}
        	}
        ];
        this.callParent(arguments);
    },
    
    loadData : function(){
    	Ext.Ajax.request({
       		url: '<c:url value="/group/getGroupContByProductId.json"/>',
        	method: 'post',
			scope: this,
			params:{productId: this.productId, productGroupId: this.productGroupId},
         	success: function(response){
         		this.store.removeAll();
       			var responseObject = Ext.JSON.decode(response.responseText);
       			if (responseObject.success == true){
       				if(responseObject.productGroupList != null && responseObject.productGroupList.length > 0){
       					try{
       						this.store.removeAll();
       						for(var i = 0; i < responseObject.productGroupList.length; i ++){
       							this.store.insert(i, {
       								objectId: responseObject.productGroupList[i].objectId,
									productId: responseObject.productGroupList[i].productId,
									productSpecId: responseObject.productGroupList[i].productSpecId,
									productCode: responseObject.productGroupList[i].productCode,
									name: responseObject.productGroupList[i].name,
									stockNumber: responseObject.productGroupList[i].stockNumber,
									priceWholesale: responseObject.productGroupList[i].priceWholesale,
									priceRecommend: responseObject.productGroupList[i].priceRecommend,
									priceCost: responseObject.productGroupList[i].priceCost,
									groupPriceRecommend: responseObject.productGroupList[i].groupPriceRecommend,
									groupPriceWholesale: responseObject.productGroupList[i].groupPriceWholesale,
									saleTimes: responseObject.productGroupList[i].saleTimes
								});
       						}
       					}catch(e){
    					}
       				}
       			}else{
					showFailMsg(responseObject.message, 4);
				}
       		}
       	})
    }
});
