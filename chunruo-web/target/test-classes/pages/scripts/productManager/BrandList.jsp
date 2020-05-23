<%@ include file="/WEB-INF/pages/scripts/taglibs.jsp"%>

Ext.define('Brands', {
	extend: 'Ext.data.Model',
    fields: [
    	{name: 'brandId',	    mapping: 'brandId',		    type: 'int'},
    	{name: 'name',			mapping: 'name',			type: 'string'},
    ]
});

Ext.define('MyExt.productManager.BrandList', {
    extend : 'Ext.grid.GridPanel',
    requires : [ 'MyExt.productManager.BrandPicker'],
    alias: ['widget.brandList'],
	header: false,
	autoScroll: true,   
	closable: true,
	selType: 'checkboxmodel',
	multiSelect: true,
	columnLines: true,
	animCollapse: false,
    enableLocking: true,
    viewConfig: {
        stripeRows: true,
        enableTextSelection: true
    },
    
	initComponent : function(config) {
		Ext.apply(this, config);	
		
		this.store = Ext.create('Ext.data.Store', {
	    	autoDestroy: true,
	     	model: 'Brands',
	     	groupField: 'name',
	     	data: []
	    });
		 
		this.tbar = [{
        	text: '<fmt:message key="button.delete"/>', 
        	iconCls: 'delete', 	
        	handler: this.deleteProduct,
        	scope: this
        },'-',{
        	iconCls: 'add',
     		xtype: 'brandPicker',
     		isHiddenSubmit: true,
	    	fieldLabel: '<fmt:message key="productBrand.name" />',
	    	labelWidth: 60,
	    	width: 400,
			editable: false,
         	anchor: '98%',
         	typeAhead: true,
         	listeners: {
 				scope: this,
 				itemClick : function(picker, record, item, index, e, eOpts){
 					picker.setRawValue(record.data.name);
 					this.loadData(record.data);
 				}
 			}
		}];
		
		this.columns = [
			{text: '<fmt:message key="productBrand.brandId"/>', dataIndex: 'brandId', width: 65, sortable : true},
        	{text: '<fmt:message key="productBrand.name"/>', dataIndex: 'name', width: 300, sortable : true,
        		renderer : function(val, metadata, record, rowIndex, columnIndex, store){ 
					metadata.tdAttr = Ext.String.format('data-qtip="{0}"', val); 
					return val;
				}
        	}
        ];
        
        this.callParent(arguments);
        this.gsm = this.getSelectionModel();
    },
    
    getBrandRowsData : function(){
    	var productAggregatedRowsData = [];
    	this.store.each(function(record) {
	   		productAggregatedRowsData.push(record.data.brandId);    
	    }, this);
	    return productAggregatedRowsData;
    },
    
    deleteProduct : function(){
    	var rowsData = [];		
		var records = this.gsm.getSelection();
		if(records.length == 0){
			showMsg('<fmt:message key="ajax.confirm"/>', '<fmt:message key="ajax.no.record"/>');
			return;
		}	
		for(var i = 0; i < records.length; i++){
			if(!Ext.Array.contains(rowsData, records[i].data.brandId)){
				rowsData.push(records[i].data.brandId);
			}				
		}
		
		Ext.MessageBox.confirm('<fmt:message key="ajax.confirm"/>', '<fmt:message key="delete.confirm"/>', function(e){
			if(e == 'yes'){
            	for(var i = 0; i < rowsData.length; i ++){
            		this.store.remove(this.store.queryRecords('brandId', rowsData[i]));
            	}
  			}
  		}, this)
    },
    
    loadData : function(brandId){
    	Ext.Ajax.request({
       		url: '<c:url value="/brand/searchBrandById.json"/>',
        	method: 'post',
			scope: this,
			params:{brandId: brandId},
         	success: function(response){
       			var responseObject = Ext.JSON.decode(response.responseText);
       			if (responseObject.success == true && responseObject.data != null){
   					try{
   						var brands = Ext.create('Brands', responseObject.data);
       					this.store.insert(0, brands);
   					}catch(e){
					}
       			}else{
					showFailMsg(responseObject.message, 4);
				}
       		}
       	})
    }
});