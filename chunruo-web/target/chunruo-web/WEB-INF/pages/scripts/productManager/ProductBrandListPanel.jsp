<%@ include file="/WEB-INF/pages/scripts/taglibs.jsp"%>


Ext.define('ProductBrand', {
	extend: 'Ext.data.Model',
	idProperty: 'brandId',
    fields: [
		{name: 'brandId',			mapping: 'brandId',		type: 'int'},
		{name: 'name',				mapping: 'name',		type: 'string'},
		{name: 'shortName',			mapping: 'shortName',	type: 'string'},
		{name: 'image',				mapping: 'image',		type: 'string'},
		{name: 'countryImage',		mapping: 'countryImage',type: 'string'},
		{name: 'isHot',				mapping: 'isHot',		type: 'bool'},
		{name: 'initial',	        mapping: 'initial',		type: 'string'},
		{name: 'countryId',	 		mapping: 'countryId',	type: 'int'},
		{name: 'countryName',	 	mapping: 'countryName',	type: 'string'},
		{name: 'tagNames',	        mapping: 'tagNames',	type: 'string'},
		{name: 'createTime',	 	mapping: 'createTime',	type: 'string'},
		{name: 'updateTime',	 	mapping: 'updateTime',	type: 'string'},
		{name: 'isHomePage',	    mapping: 'isHomePage',  type: 'bool'},
    ]
});

Ext.define('MyExt.productManager.ProductBrandListPanel', {
   	extend : 'Ext.panel.Panel',
    requires : ['Ext.ux.grid.GridHeaderFilters','Ext.ux.grid.Exporter'],
    header: false,
	closable: true,
	columnLines: true,
	animCollapse: true,
	layout: 'border',
   	defaults: {  
    	split: true,    
        collapsible: false
    },
    
	initComponent : function(config) {
		
    	this.store = Ext.create('Ext.data.Store', {
    		pageSize: 50,
	        autoLoad: false,
			autoDestroy: true,
			sortOnLoad: true,
			remoteSort: true,
			model: 'ProductBrand',
			proxy: {
				type: 'ajax',
				url: '<c:url value="/brand/brandList.json"/>',
				reader: {
					type : 'json',
                	root: 'data',
                	totalProperty: 'totalCount'
            	}
			},
			sorters: [{
	            property: 'brandId',
	            direction: 'desc'
	        }]
		});
		
		this.isHotStore= Ext.create('Ext.data.Store', {
			autoDestroy: true,
			model: 'InitModel',
			data: [
        		{id: 1, name: '<fmt:message key="button.yes"/>'},
        		{id: 0, name: '<fmt:message key="button.no"/>'}
        	]
		});
		
		this.columns = [
        	{
            	text: '<fmt:message key="productBrand.image"/>',
                menuDisabled: true,
                sortable: false,
                xtype: 'actioncolumn',
                align: 'center',
                width: 80,
                higth: 80,
                locked: true,
                dataIndex: 'image',
                renderer: function(value, metadata, record) {
        			return Ext.String.format('<img height="60" width="60" src="{0}"></img>', value);
    			}
            }, 
            {
            	text: '<fmt:message key="productBrand.country.image"/>',
                menuDisabled: true,
                sortable: false,
                xtype: 'actioncolumn',
                align: 'center',
                width: 80,
                higth: 80,
                locked: true,
                dataIndex: 'countryImage',
                renderer: function(value, metadata, record) {
        			return Ext.String.format('<img height="60" width="60" src="{0}"></img>', value);
    			}
            }, 
			{text: '<fmt:message key="productBrand.brandId"/>', dataIndex: 'brandId', width: 65, sortable: true, locked: true, filter: {xtype: 'textfield'}},
			{text: '<fmt:message key="productBrand.name"/>', dataIndex: 'name', width: 250, sortable: true, locked: true, filter: {xtype: 'textfield'}},
			{text: '<fmt:message key="productBrand.shortName"/>', dataIndex: 'shortName', width: 100, sortable: true, locked: true, filter: {xtype: 'textfield'}},
        	{text: '<fmt:message key="productBrand.initial"/>', dataIndex: 'initial', width: 80, sortable: true, locked: true, filter: {xtype: 'textfield'}},
        	{text: '<fmt:message key="productBrand.tag"/>', dataIndex: 'tagNames', width: 200, sortable: true, locked: true, filter: {xtype: 'textfield'}},
		    {text: '<fmt:message key="productBrand.isHot"/>', dataIndex: 'isHot', width: 90, locked: true,  align: 'center', sortable : true,
		    	renderer: this.rendererStuts,
	        	filter: {
					xtype: 'combobox',
				    displayField: 'name',
				    valueField: 'id',
				   	store: this.isHotStore,
				   	queryMode: 'local',
				    typeAhead: true
				}
			},
			{text: '<fmt:message key="product.brand.countryId"/>', dataIndex: 'countryId', width: 75, sortable : true,
        		filter: {xtype: 'textfield'}
        	},
        	{text: '<fmt:message key="product.brand.countryName"/>', dataIndex: 'countryName', width: 75, sortable : true,
        		filter: {xtype: 'textfield'}
        	},
			{text: '<fmt:message key="productBrand.createTime"/>', dataIndex: 'createTime', width: 140, sortable : true, align: 'center',filter: {xtype: 'textfield'}},
        	{text: '<fmt:message key="productBrand.updateTime"/>', dataIndex: 'updateTime', width: 140, sortable : true,filter: {xtype: 'textfield'}},
			{text: '<fmt:message key="productBrand.isHomePage"/>', dataIndex: 'isHomePage', width: 90, locked: true,  align: 'center', sortable : true,renderer: this.rendererStuts},
        ];
		
		this.tbar = [{
	    	text: '<fmt:message key="button.refresh"/>', 
	        iconCls: 'refresh', 	
	       	handler: function(){this.store.reload();}, 
	       	scope: this
	  	},'-',{
        	text: '<fmt:message key="button.add"/>', 
        	iconCls: 'add', 	
        	handler: this.saveBrand,
        	scope: this
        },'-',{
        	text: '<fmt:message key="button.delete"/>', 
        	iconCls: 'delete',	
        	handler: this.deleteBrand, 
        	scope: this
        },'-',{
        	text: '<fmt:message key="productBrand.setHomePage"/>', 
        	iconCls: 'Packageadd',	
        	handler: this.setHomePage, 
        	scope: this
        },'-',{
        	text: '<fmt:message key="productBrand.cancelHomePage"/>', 
        	iconCls: 'disabled',	
        	handler: this.cancelHomePage, 
        	scope: this
        }];
		
		this.pagingToolbar = new Ext.PagingToolbar({
        	pageSize: 50,
			store: this.store,
			autoheigth: true,
			displayInfo: true,
			displayMsg: '<fmt:message key="ajax.record"/>',
			emptyMsg: '<fmt:message key="ajax.no.record"/>',
			scope: this,
			items: ['-',{ 
				xtype: 'numberfield', 
				width: 120, 
				labelWidth: 65,
				value: 50, 
				minValue: 1, 
				fieldLabel: '<fmt:message key="ajax.record.size"/>',
                allowBlank: false,
               	scope: this,
                listeners:{
                	scope: this,
               		change: function (field, newValue, oldValue) {
                    	var number = parseInt(newValue);
                        if (isNaN(number) || !number || number < 1) {
                        	number = 50;
                           	Field.setValue(number);
                        }
                       	this.store.pageSize = number;
                       	this.store.load();
                   	}
               	}
        	}]	
		});
		
		this.productList = Ext.create('Ext.grid.GridPanel', {
			region: 'center',
			header: false,
			autoScroll: true,   
			closable: true,
			selType: 'checkboxmodel',
			multiSelect: true,
			columnLines: true,
			animCollapse: false,
	        enableLocking: true,
		    columns: this.columns,
		    store: this.store,
		    bbar: this.pagingToolbar,
		    plugins: ['gridHeaderFilters','gridexporter'],
		    viewConfig: {
		        stripeRows: true,
		        enableTextSelection: true
		    }
	    });
	    
	    this.east =  Ext.create('MyExt.productManager.ProductBrandTabPanel', {
        	productList: this.productList,
		 	viewer: this.viewer,
		 	layout: 'card',
	        border: false,
	       	region: 'east',
	        width: 550,
	        split: true,
	        header: false,
	        hidden: true,
	        store: this.store
        });
        
	    this.gsm = this.productList.getSelectionModel();
	    this.items = [this.productList, this.east];	
	    this.east.hide();
	    this.callParent(arguments);
	    
	    this.store.on('beforeload', function(store, options) {
	        Ext.apply(store.proxy.extraParams, {
	        	filters: Ext.JSON.encode(this.productList.filters)
			});
	    }, this);
	    this.store.load();  
	    
	    this.productList.on('itemdblclick', function(view, record, item, index, e, eOpts) {
	    	this.east.transferData(this.east, record, this.body.dom.clientWidth);
	    	this.east.show();
	    }, this); 
    },
	    
	saveBrand : function(){
    	var productBrandFormPanel = Ext.create('MyExt.productManager.ProductBrandFormPanel', {
			id: 'add@productBrandFormPanel' + this.id,
    		viewer: this.viewer,
   	 	});
    	
    	var buttons = [{
			text: '<fmt:message key="button.save"/>',
			handler: function(){
			    var rowsData = [];    
		    	var tagNameDatas = [];
		       	productBrandFormPanel.down('imagepanel').store.each(function(record) {
		       		record.data.input_file = null;
		            rowsData.push(record.data);    
		      	}, this);
		      	var rowsDatas = [];    
		       	productBrandFormPanel.down('imagepanels').store.each(function(record) {
		       		record.data.input_file = null;
		            rowsDatas.push(record.data);    
		      	}, this);
		      	
		      	var adRowsData = [];
		      	productBrandFormPanel.down('adImagePanel').store.each(function(record) {
		       		record.data.input_file = null;
		            adRowsData.push(record.data);    
		      	}, this);
		      	
		      	var backRowsData = [];    
		      	productBrandFormPanel.down('backGroundImagePanel').store.each(function(record) {
		       		record.data.input_file = null;
		            backRowsData.push(record.data);    
		      	}, this);
		      
		    	if(productBrandFormPanel.form.isValid()){
					Ext.MessageBox.confirm('<fmt:message key="ajax.confirm"/>', '<fmt:message key="save.confirm"/>', function(e){
						if(e == 'yes'){
		             		productBrandFormPanel.form.submit({
		                 		waitMsg: 'Loading...',
		                 		url: '<c:url value="/brand/saveBrand.json"/>',
		               			scope: this,
		               			params:{recodeGridJson: Ext.JSON.encode(rowsData),
		               			        countryImageJson: Ext.JSON.encode(rowsDatas),
		               			        adImageJson: Ext.JSON.encode(adRowsData),
               			                backImageJson: Ext.JSON.encode(backRowsData)
		               			        },
		               			success: function(form, action) {
		                   			var responseObject = Ext.JSON.decode(action.response.responseText);
		                   			if(responseObject.error == false){
		                  				showSuccMsg(responseObject.message);
		                  				this.store.load();
		                  				popWin.close();
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
      	openWin('<fmt:message key="button.add"/>', productBrandFormPanel, buttons, 800, 400);
    },    
	    
	deleteBrand : function() {
		var records = this.gsm.getSelection();
   		if(records.length == 0){
			showMsg('<fmt:message key="ajax.confirm"/>', '<fmt:message key="ajax.no.record"/>');
			return;
		}else if(records.length > 1){
			showMsg('<fmt:message key="ajax.confirm"/>', '<fmt:message key="ajax.only.one.record"/>');
			return;
		}
		
	    Ext.MessageBox.confirm('<fmt:message key="ajax.confirm"/>', '<fmt:message key="enable.confirm"/>', function(e){
			if(e == 'yes'){
	        	Ext.Ajax.request({
		        	url: '<c:url value="/brand/deleteBrand.json"/>',
		         	method: 'post',
					scope: this,
					params:{brandId : records[0].data.brandId},
		          	success: function(xresponse){
				    	var xresponseObject = Ext.JSON.decode(xresponse.responseText);
         				if (xresponseObject.success == true){
         					showSuccMsg(xresponseObject.message);
         					this.store.loadPage(1);
		                    this.gsm.deselectAll();
         				}else{
         					showFailMsg(xresponseObject.message, 4);
         				}
					}
		     	})
	        }
		}, this)
   	},
   	
   	setHomePage : function() {
	    Ext.MessageBox.confirm('<fmt:message key="ajax.confirm"/>', '<fmt:message key="enable.confirm"/>', function(e){
			if(e == 'yes'){
	        	Ext.Ajax.request({
		        	url: '<c:url value="/brand/homePageStatus.json"/>',
		         	method: 'post',
					scope: this,
					params:{status : 1},
		          	success: function(xresponse){
				    	var xresponseObject = Ext.JSON.decode(xresponse.responseText);
	         				if (xresponseObject.success == true){
	         					showSuccMsg(xresponseObject.message);
	         					this.store.loadPage(1);
	         				}else{
	         					showFailMsg(xresponseObject.message, 4);
	         				}
					}
		     	})
	        }
		}, this)
   	},
   	
   	cancelHomePage : function() {
	    Ext.MessageBox.confirm('<fmt:message key="ajax.confirm"/>', '<fmt:message key="enable.confirm"/>', function(e){
			if(e == 'yes'){
	        	Ext.Ajax.request({
		        	url: '<c:url value="/brand/homePageStatus.json"/>',
		         	method: 'post',
					scope: this,
					params:{status : 0},
		          	success: function(xresponse){
				    	var xresponseObject = Ext.JSON.decode(xresponse.responseText);
	         				if (xresponseObject.success == true){
	         					showSuccMsg(xresponseObject.message);
	         					this.store.loadPage(1);
	         				}else{
	         					showFailMsg(xresponseObject.message, 4);
	         				}
					}
		     	})
	        }
		}, this)
   	},
   	
   	fontRenderer: function(value, meta, record) {
    	meta.style = 'overflow:auto;padding: 3px 6px;text-overflow: ellipsis;white-space: nowrap;white-space:normal;line-height:24px;font-weight:bold;';      
       	return value;     
   	},
   	
   	rendererStuts : function(val){
		if(val == true) {
            return '<b><fmt:message key="button.yes"/></b>';
        }else{
            return '<fmt:message key="button.no"/>';
        }
	},   
	    
});	    
	    
	    
		