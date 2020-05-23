<%@ include file="/WEB-INF/pages/scripts/taglibs.jsp"%>

Ext.define('ProductAggregated', {
	extend: 'Ext.data.Model',
    fields: [
    	{name: 'productId',	mapping: 'productId',		type: 'int'},
    	{name: 'name',			mapping: 'name',			type: 'string'},
    	{name: 'status',		mapping: 'status',			type: 'bool'},
    	{name: 'isSoldout',		mapping: 'isSoldout',		type: 'bool'},
    	{name: 'templateId',	mapping: 'templateId',	type: 'int'},
    	{name: 'wareHouseName',	 	mapping: 'wareHouseName',		type: 'string'}
    ]
});

Ext.define('MyExt.productManager.ProductAggregatedList', {
    extend : 'Ext.grid.GridPanel',
    requires : [ 'MyExt.couponManager.ProductPicker'],
    alias: ['widget.productAggregatedList'],
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
	     	model: 'ProductAggregated',
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
     		xtype: 'couponProductPicker',
     		isHiddenSubmit: true,
	    	fieldLabel: '<fmt:message key="product.wholesale.name" />',
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
			{text: '<fmt:message key="product.wholesale.wholesaleId"/>', dataIndex: 'productId', width: 65, sortable : true},
        	{text: '<fmt:message key="product.wholesale.name"/>', dataIndex: 'name', width: 300, sortable : true,
        		renderer : function(val, metadata, record, rowIndex, columnIndex, store){ 
					metadata.tdAttr = Ext.String.format('data-qtip="{0}"', val); 
					return val;
				}
        	},
        	{text: '<fmt:message key="product.wholesale.status"/>', dataIndex: 'status', width: 65, sortable : true,
        		align: 'center',
        		renderer: function(value, meta, record) {    
			       	if(value == false) {
			            return '<span style="color:green;"><fmt:message key="button.no"/></span>';
			        }else{
			            return '<span style="color:red;"><b><fmt:message key="button.yes"/></b></span>';
			        }  
			   	}
        	},
        	{text: '<fmt:message key="product.wholesale.isSoldout"/>', dataIndex: 'isSoldout', width: 65, sortable : true,
        		align: 'center',
        		renderer: function(value, meta, record) {    
			       	if(value == true) {
			            return '<span style="color:green;"><fmt:message key="button.no"/></span>';
			        }else{
			            return '<span style="color:red;"><b><fmt:message key="button.yes"/></b></span>';
			        }  
			   	}
        	},
        	{text: '<fmt:message key="product.wholesale.wareHouseName"/>', dataIndex: 'wareHouseName', width: 140, sortable : true},
        	{text: '<fmt:message key="product.wholesale.postageTemplate"/>', dataIndex: 'templateId', width: 160, sortable : true,
        		align: 'center',
				renderer: function(val){
			    	<c:forEach var="template" varStatus="status" items="${allNoFreePostageTemplateMaps}" >
			    	if(val == ${template.value.templateId}){
			    		return '${template.value.name}';
			    	}
					</c:forEach>
					return val;
			    }
        	}
        ];
        
        this.callParent(arguments);
        this.gsm = this.getSelectionModel();
    },
    
    getProductAggregatedRowsData : function(){
    	var productAggregatedRowsData = [];
    	this.store.each(function(record) {
	   		productAggregatedRowsData.push(record.data.productId);    
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
			if(!Ext.Array.contains(rowsData, records[i].data.productId)){
				rowsData.push(records[i].data.productId);
			}				
		}
		
		Ext.MessageBox.confirm('<fmt:message key="ajax.confirm"/>', '<fmt:message key="delete.confirm"/>', function(e){
			if(e == 'yes'){
            	for(var i = 0; i < rowsData.length; i ++){
            		this.store.remove(this.store.queryRecords('productId', rowsData[i]));
            	}
  			}
  		}, this)
    },
    
    loadData : function(productId){
    	Ext.Ajax.request({
       		url: '<c:url value="/product/searchProductById.json"/>',
        	method: 'post',
			scope: this,
			params:{productId: productId},
         	success: function(response){
       			var responseObject = Ext.JSON.decode(response.responseText);
       			if (responseObject.success == true && responseObject.data != null){
   					try{
   						var productAggregated = Ext.create('ProductAggregated', responseObject.data);
       					this.store.insert(0, productAggregated);
   					}catch(e){
					}
       			}else{
					showFailMsg(responseObject.message, 4);
				}
       		}
       	})
    }
});