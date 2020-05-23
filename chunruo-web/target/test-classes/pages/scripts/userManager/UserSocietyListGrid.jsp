<%@ include file="/WEB-INF/pages/scripts/taglibs.jsp"%>

Ext.define('UserProduct', {
	extend: 'Ext.data.Model',
	idProperty: 'productId',
    fields: [
    	{name: 'productId',			mapping: 'productId',		type: 'string'},
    	{name: 'productNumber',		mapping: 'productNumber',	type: 'string'},
		{name: 'productName',		mapping: 'productName',		type: 'string'},
		{name: 'skuNumber',			mapping: 'skuNumber',		type: 'string'},
		{name: 'name',				mapping: 'name',			type: 'string'},
		{name: 'price',				mapping: 'price',			type: 'string'},
		{name: 'inventoryRatio',	mapping: 'inventoryRatio',	type: 'string'},
		{name: 'businessId',		mapping: 'businessId',		type: 'string'},
		{name: 'id',		 		mapping: 'id',				type: 'string'}
    ]

});

Ext.define('MyExt.userManager.UserSocietyListGrid', {
    extend : 'Ext.grid.GridPanel',
    alias: ['widget.userProduct'],
    requires : [],
    id:'userProductId',
	region: 'center',
	autoScroll: true,   
	multiSelect: true,
	columnLines: true,
	animCollapse: false,
	stripeRows:true, 
    enableLocking: true,
    selType: 'checkboxmodel',
    viewConfig: {
        stripeRows: true,
        enableTextSelection: true
    },
    plugins:[  
             Ext.create('Ext.grid.plugin.CellEditing',{  
                 clicksToEdit:2 
              })  
        ],
         
	initComponent : function(config) {
		Ext.apply(this, config);
		
		var userId;
    	this.store = Ext.create('Ext.data.Store', {
			autoDestroy: true,
			model: 'UserProduct'
		});
		
		this.columns = [
	    	{text: '<fmt:message key="userProduct.productId"/>', dataIndex: 'productId', width: 55, sortable : true,hidden:true },
			{text: '<fmt:message key="userProduct.productNumber"/>', dataIndex: 'productNumber', width: 100, sortable : true},
			{text: '<fmt:message key="userProduct.productName"/>', dataIndex: 'productName', width: 100, sortable : true },
			{text: '<fmt:message key="userProduct.skuNumber"/>', dataIndex: 'skuNumber', width: 100, sortable : true},
			{text: '<fmt:message key="userProduct.name"/>', dataIndex: 'name', width: 100, sortable : true},
			{text: '<fmt:message key="userProduct.percentage"/>', dataIndex: 'inventoryRatio', width: 100, sortable : true},
			{text: '<fmt:message key="userProduct.price"/>', dataIndex: 'price', width: 100, sortable : true,editor: 'textfield'},
			{text: '<fmt:message key="userProduct.businessId"/>', dataIndex: 'businessId', width: 100, sortable : true,hidden:true},
			{text: '<fmt:message key="userProduct.id"/>', dataIndex: 'id', width: 100, sortable : true,hidden:true}
        ]; 
        
     <%--    this.tbar = [{
        	text: '<fmt:message key="product.import"/>', 
        	iconCls: 'excel', 	
        	handler: this.importBusinessProductList, 
        	scope: this
        },'-',{
        	text: '<fmt:message key="product.add"/>', 
        	iconCls: 'add', 	
        	handler: this.addProduct, 
        	scope: this
        },'-',{
        	text: '<fmt:message key="button.delete"/>', 
        	iconCls: 'delete', 	
        	handler: this.deleteProduct, 
        	scope: this
        }]; --%>
        
    	this.callParent();
    	this.on('itemdblclick', this.onDbClick, this);
    	this.gsm = this.getSelectionModel();
    },
    
<%--     onDbClick : function(view, record, item, index, e, eOpts) {
    	var businessProductFormPanel = Ext.create('MyExt.productManager.BusinessProductFormPanel', {id: 'modifyBusinessProductFormPanel@BusinessProductFormPanel', isEditor: true, title: '<fmt:message key="business.product.edit"/>'});
    	businessProductFormPanel.load({   
    		waitMsg: '<fmt:message key="ajax.waitMsg"/>',   
    		waitTitle: '<fmt:message key="ajax.waitTitle"/>', 
    		url: '<c:url value="/business/getProductBusinessById.json"/>', 
    		params: {id: record.data.id}, 
    		failure : function (form, action) {   
     			showMsg('<fmt:message key="ajax.waitTitle"/>', '<fmt:message key="ajax.load.failure"/>', 4);   
    		}
   		});
   		
    	var buttons = [{
			text: '<fmt:message key="button.save"/>',
			handler: function(){
				if(businessProductFormPanel.form.isValid()){
					Ext.MessageBox.confirm('<fmt:message key="ajax.confirm"/>', '<fmt:message key="save.confirm"/>', function(e){
						if(e == 'yes'){
			                businessProductFormPanel.form.submit({
			                    waitMsg: 'Loading...',
			                    url: '<c:url value="/business/updateProductPrice.json"/>',
			                    scope: this,
			                    success: function(form, action) {
			                        var responseObject = Ext.JSON.decode(action.response.responseText);
			                        if(responseObject.error == false){
			                       		showSuccMsg(responseObject.message);
										popWin.close();
										this.loadData();
									}else{
										showFailMsg(responseObject.message, 4);
									}
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
		openWin('<fmt:message key="business.product.edit"/>', businessProductFormPanel, buttons, 400, 300);
    }, --%>
    
    importBusinessProductList : function(){
		var formPanel = Ext.create('Ext.form.Panel', {
		    width: 400,
		    header: false,
		    labelHidder: true,
		    items: [{
		        xtype: 'filefield',
		        name: 'file',
		        msgTarget: 'side',
		        allowBlank: false,
		        anchor: '100%',
		        buttonText: '<fmt:message key="product.import.select.file"/>'
		    }]
		});
		
		var buttons = [{
			text: '<fmt:message key="button.confirm"/>',
			handler : function(){
	            if(formPanel.isValid()){
	            	formPanel.submit({
	                    url: '<c:url value="/baseImportFile.json"/>',
	                    waitMsg: '<fmt:message key="ajax.loading"/>',
	                    scope: this,
	                    success: function(form, action) {
	                    	var responseObject = Ext.JSON.decode(action.response.responseText);
			                if(responseObject.error == false || responseObject.error == 'false'){
	                        	popFormWin.close();
								var testPanel = Ext.create('MyExt.BaseImportFileGrid');
								testPanel.setObject(responseObject);
								
								var importButtons = [{
									text: '<fmt:message key="button.confirm"/>',
									handler : function(){
										var rowsData = [];		
										if(testPanel.store.getCount() == 0){
											showFailMsg('<fmt:message key="errors.noRecord"/>', 4);
											return;
										}	
										for(var i = 0; i < testPanel.store.getCount(); i++){	
											rowsData.push(testPanel.store.getAt(i).data);			
										}
		
							            Ext.MessageBox.confirm('<fmt:message key="ajax.confirm"/>', '<fmt:message key="ajax.import.confirm"/>', function(e){
											if(e == 'yes'){
									        	Ext.Ajax.request({
										        	url: '<c:url value="/business/businessProductImport.json"/>',
										         	method: 'post',
													scope: this,
													params:{dataGridJson: Ext.JSON.encode(rowsData), headerGridJson: Ext.JSON.encode(testPanel.keyValueHeaderData), businessId : this.businessId},
										          	success: function(xresponse){
												    	var xresponseObject = Ext.JSON.decode(xresponse.responseText);
												    	this.exportErrorRecord(xresponseObject);
								          				if (xresponseObject.success == true){
								          					showSuccMsg(xresponseObject.message);
								          					popWin.close();
								          					this.loadData();
								          				}else{
								          					showFailMsg(xresponseObject.message, 4);
								          				}
													}
										     	})
									        }
							 			}, this)
									},
									scope: this
								},{
									text: '<fmt:message key="button.cancel"/>',
									handler : function(){popWin.close();},
									scope: this
								}];
								openWin('<fmt:message key="product.import"/>', testPanel, importButtons, 750, 450);
	                        }else{
								showFailMsg(responseObject.message, 4);
							}
	                    }
	                });
	            }
			},
			scope: this
		},{
			text: '<fmt:message key="button.cancel"/>',
			handler : function(){popFormWin.close();},
			scope: this
		}];
		openFormWin('<fmt:message key="product.import"/>', formPanel, buttons, 420, 120);
    },
    
    deleteProduct : function() {
		var rowsData = [];
		var records = this.gsm.getSelection();
		if(records.length == 0){
			showMsg('<fmt:message key="ajax.confirm"/>', '<fmt:message key="ajax.no.record"/>');
			return;
		}
		for(var i = 0; i < records.length; i++){
			rowsData.push(records[i].data.id);	
		}
		
		Ext.Msg.confirm('<fmt:message key="ajax.confirm"/>', '<fmt:message key="delete.confirm"/>', function(e){
			if(e == 'yes'){
				Ext.Ajax.request({
		        	url: '<c:url value="/business/deleteBuinessProduct.json"/>',
		         	method: 'post',
					scope: this,
					params:{idListGridJson: Ext.JSON.encode(rowsData)},
		          	success: function(response){
          				var responseObject = Ext.JSON.decode(response.responseText);
                        if(responseObject.error == false){
                       		showSuccMsg(responseObject.message);
		                    this.gsm.deselectAll();
                        	this.loadData();
						}else{
							showFailMsg(responseObject.message, 4);
						}
					}
		     	})
	     	}
	 	}, this)  
	},
	
	exportErrorRecord : function(xresponseObject) {
		if(xresponseObject.data == null || xresponseObject.data.length == 0){
			return;
		}
		var exportStore = Ext.create('Ext.data.Store', {
			autoDestroy: true,
			model: 'InitModel',
			data: []
		});
 		
		for(var i = 0; i < xresponseObject.data.length; i ++){
			var model = Ext.create('InitModel');
			model.set('productNumber', xresponseObject.data[i].productNumber);
			model.set('productName', xresponseObject.data[i].productName);
			model.set('skuNumber', xresponseObject.data[i].skuNumber);
			model.set('skuName', xresponseObject.data[i].skuName);
			model.set('inventoryRatio', xresponseObject.data[i].inventoryRatio);
			model.set('price', xresponseObject.data[i].price);
			model.set('failureReason', xresponseObject.data[i].failureReason);
			model.set('importTime', xresponseObject.data[i].importTime);
			exportStore.insert(i, model);
		}
 		exportColumns = [
			{text: '<fmt:message key="businessProduct.productNumber"/>', dataIndex: 'productNumber', width: 100, sortable : true},
			{text: '<fmt:message key="businessProduct.productName"/>', dataIndex: 'productName', width: 100, sortable : true },
			{text: '<fmt:message key="businessProduct.skuNumber"/>', dataIndex: 'skuNumber', width: 100, sortable : true},
			{text: '<fmt:message key="businessProduct.name"/>', dataIndex: 'skuName', width: 100, sortable : true},
			{text: '<fmt:message key="businessProduct.percentage"/>', dataIndex: 'inventoryRatio', width: 100, sortable : true},
			{text: '<fmt:message key="businessProduct.price"/>', dataIndex: 'price', width: 100, sortable : true,editor: 'textfield'},
			{text: '<fmt:message key="failure.reason"/>', dataIndex: 'failureReason', width: 100, sortable : true},
			{text: '<fmt:message key="import.time"/>', dataIndex: 'importTime', width: 100, sortable : true}
        ]; 
 		errorRecordList = Ext.create('Ext.grid.GridPanel', {
		   	id: 'errorRecordList@OrderPanel' + this.id,
			region: 'center',
			header: false,
			autoScroll: true,   
			closable: true,
			selType: 'checkboxmodel',
			multiSelect: true,
			columnLines: true,
			animCollapse: false,
		    enableLocking: true,
		    columns: exportColumns,
		    store: exportStore,
      		plugins: ['gridexporter'],
    		viewConfig: {
        		stripeRows: true,
        		enableTextSelection: true
    		}
   		});
	   	errorRecordList.saveDocumentAs({
           	type: 'excel',
            title: 'meta.fiddleHeader',
            fileName: '<fmt:message key="bussines.product.import.fail"/>'
	    });
	},
	
    addProduct : function(){
    	var productSimpleList = Ext.create('MyExt.productManager.ProductSimpleListPanel', {
    		id: 'selectProductListPanel@ProductSimpleListPanel',
    		businessProductPanel: this,
    		header: false,
    		isChildSelectModel: true,
    		scope: this,
    		onDbClick : function(view, record, item, index, e, eOpts){
    			console.log(record);
    			popWin.close();
    		}
    	});
    	
 		var buttons = [{
			text: '<fmt:message key="button.confirm"/>',
			handler : function(){
				var records = productSimpleList.gsm.getSelection();
				if(records.length == 0){
					showMsg('<fmt:message key="ajax.confirm"/>', '<fmt:message key="ajax.no.record"/>');
					return;
				}
				
				var rowsData = [];
				for(var i = 0; i < records.length; i++){
					rowsData.push(records[i].data.productId);	
				}
				
				Ext.Msg.confirm('<fmt:message key="ajax.confirm"/>', '<fmt:message key="bussines.addProduct.confirm"/>', function(e){
					if(e == 'yes'){
						Ext.Ajax.request({
				        	url: '<c:url value="/business/addProductTobusiness.json"/>',
				         	method: 'post',
							scope: this,
							params:{idListGridJson: Ext.JSON.encode(rowsData), businessId : this.businessId},
				          	success: function(response){
		          				var responseObject = Ext.JSON.decode(response.responseText);
		          				if (responseObject.error == false){
		          					showSuccMsg(responseObject.message);
		          					popWin.close();
		          					this.loadData();
		          				}else{
		          					showFailMsg(responseObject.message, 4);
		          				}
							}
				     	})
			     	}
			 	}, this)
			},
			scope: this
		}];
		openWin('<fmt:message key="product.select"/>', productSimpleList, buttons, 700, 500);
    },
    
    loadData : function(){
    	Ext.Ajax.request({
       		url: '<c:url value="/business/getBusinessById.json"/>',
        	method: 'post',
			scope: this,
			params:{businessId: this.businessId},
         	success: function(response){
       			var responseObject = Ext.JSON.decode(response.responseText);
       			if (responseObject.success == true){
       				this.store.removeAll();
       				if(responseObject.businessProduct != null && responseObject.businessProduct.length > 0){
       					for(var i = 0; i < responseObject.businessProduct.length; i ++){
       						var businessProductData = Ext.create('BusinessProduct', responseObject.businessProduct[i]);
       						this.store.insert(i, businessProductData);
       					}
       				}
       			}
			}
    	})
    }
});

