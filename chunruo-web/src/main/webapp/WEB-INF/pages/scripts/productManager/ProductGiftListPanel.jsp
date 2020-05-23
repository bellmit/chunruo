<%@ include file="/WEB-INF/pages/scripts/taglibs.jsp"%>

Ext.define('ProdcutGift', {
	extend: 'Ext.data.Model',
	idProperty: 'giftId',
     fields: [
    	{name: 'giftId',	        mapping: 'giftId',	         type: 'int'},
		{name: 'productSpecId',	    mapping: 'productSpecId',	 type: 'int'},
		{name: 'wareHouseId',	    mapping: 'wareHouseId',	     type: 'int'},
		{name: 'headerImage',	    mapping: 'headerImage',      type: 'string'},
		{name: 'productSpecDesc',	mapping: 'productSpecDesc',	 type: 'string'},
		{name: 'productName',	    mapping: 'productName',	     type: 'string'},
		{name: 'yearNumber',	    mapping: 'yearNumber',	     type: 'string'},
		{name: 'productTags',	    mapping: 'productTags',	     type: 'string'},
		{name: 'createTime',	    mapping: 'createTime',	     type: 'string'},
		{name: 'updateTime',	    mapping: 'updateTime',	     type: 'string'}
    ]
});

Ext.define('MyExt.productManager.ProductGiftListPanel', {
    extend : 'Ext.panel.Panel',
    requires : ['Ext.ux.grid.GridHeaderFilters','MyExt.DateSelectorPicker'],
    header: false,
	closable: true,
	columnLines: true,
	animCollapse: true,
	layout: 'border',
   	defaults: {  
    	split: true,    
        collapsible: true,
        collapseDirection: 'left'
    },

	initComponent : function(config) {
		Ext.apply(this, config);
		
		this.store = Ext.create('Ext.data.Store', {
        	pageSize: 50,
	        autoLoad: false,
			autoDestroy: true,
			sortOnLoad: true,
			remoteSort: true,
			model: 'ProdcutGift',
			proxy: {
				type: 'ajax',
				url: '<c:url value="/product/giftProductInfoList.json"/>',
				reader: {
					type : 'json',
                	root: 'data',
                	totalProperty: 'totalCount'
            	}
			},
			sorters: [{
	            property: 'createTime',
	            direction: 'desc'
	        }]
		});
        
	    this.rendererStore= Ext.create('Ext.data.Store', {
        	autoDestroy: true,
        	model: 'InitModel',
        	data: [
        		{id: '1', name: '<fmt:message key="button.yes"/>'},
        		{id: '0', name: '<fmt:message key="button.no"/>'}
        	]
        });
	        
       	this.rendererProductTypeStore= Ext.create('Ext.data.Store', {
        	autoDestroy: true,
        	model: 'InitModel',
        	data: [
        		{id: '1', name: '<fmt:message key="product.wareHouse.productType1"/>'},
        		{id: '2', name: '<fmt:message key="product.wareHouse.productType2"/>'},
        		{id: '3', name: '<fmt:message key="product.wareHouse.productType3"/>'},
        		{id: '4', name: '<fmt:message key="product.wareHouse.productType4"/>'}
        		
        	]
        });
		
		this.levelStore= Ext.create('Ext.data.Store', {
        	autoDestroy: true,
        	model: 'InitModel',
        	data: [
        	    {id: '1', name: '<fmt:message key="team.rule.question.level1"/>'},
        	 	{id: '2', name: '<fmt:message key="team.rule.question.level2"/>'},
        		{id: '3', name: '<fmt:message key="team.rule.question.level3"/>'},
        		
        	]
        });
		
		this.columns = [{
           	text: '<fmt:message key="product.spec.headerimage"/>',
            menuDisabled: true,
           	sortable: false,
            xtype: 'actioncolumn',
            align: 'center',
           	width: 30,
            higth: 30,
           	dataIndex: 'headerImage',
           	renderer: function(value, metadata, record) {
      			return Ext.String.format('<img height="25" width="25" src="{0}"></img>', value);
  			}
        },
		{text: '<fmt:message key="product.gift.infoId"/>', dataIndex: 'giftId', width: 80, sortable : true,
       		filter: {xtype: 'textfield'},
       		renderer: this.fontRenderer
       	},
       	{text: '<fmt:message key="product.gift.productSpecId"/>', dataIndex: 'productSpecId', width: 80, sortable : true,
       		filter: {xtype: 'textfield'},
       		renderer: this.fontRenderer
       	},
       	{text: '<fmt:message key="product.gift.wareHouseId"/>', dataIndex: 'wareHouseId', width: 80, sortable : true,
       		filter: {xtype: 'textfield'},
       		renderer: this.fontRenderer
       	},
       	{text: '<fmt:message key="product.gift.yearNumber"/>', dataIndex: 'yearNumber', width: 120, sortable : true,
       		filter: {xtype: 'textfield'},
       		renderer: this.fontRenderer
       	},
       	{text: '<fmt:message key="product.gift.productName"/>', dataIndex: 'productName', flex: 1, sortable : true,
       		filter: {xtype: 'textfield'},
       		renderer: this.fontRenderer
       	},
       	{text: '<fmt:message key="product.gift.productTags"/>', dataIndex: 'productTags', width: 350, sortable : true,
       		filter: {xtype: 'textfield'},
       		renderer: this.fontRenderer
       	}];
        
        this.tbar = [
        <jkd:haveAuthorize access="/product/saveGiftProductInfo.json">
        {
        	text: '<fmt:message key="product.gift.add"/>', 
            iconCls: 'add', 
            scope: this,
        	handler: this.addProductGiftInfo
        }
        <c:set var="isHaveAuthorize" value="true" />
		</jkd:haveAuthorize>
		<jkd:haveAuthorize access="/product/deleteProductGiftById.json">
		<c:if test="${isHaveAuthorize}">,</c:if>
        {
        	text: '<fmt:message key="product.gift.delete"/>', 
            iconCls: 'delete', 
            scope: this,
        	handler: this.deleteProductGiftInfo
        }
        </jkd:haveAuthorize>
        ];
        
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
        	plugins: ['gridHeaderFilters'],
		    viewConfig: {
		        stripeRows: true,
		        enableTextSelection: true
		    }
	    });     
	    
	    this.east =  Ext.create('MyExt.productManager.ProductGiftTabPanel', {
        	productList: this.productList,
        	viewer: this.viewer,
		 	layout: 'card',
	        border: false,
	       	region: 'east',
	        width: this.xWidth/2,
	        minWidth: 850,
	        split: true,
	        header: false,
	        hidden: true
        });
    	
    	this.items = [this.productList, this.east];	
		this.east.hide();
		this.callParent(arguments);
	    
	    this.gsm = this.productList.getSelectionModel();
	    <jkd:haveAuthorize access="/product/giftProductInfoList.json">
	   	this.store.on('beforeload', function(store, options) {
	        Ext.apply(store.proxy.extraParams, {
	        	filters: Ext.JSON.encode(this.productList.filters)
			});
	    }, this);
	    this.store.load();
	    </jkd:haveAuthorize>   
	    
	    this.productList.on('itemdblclick', function(view, record, item, index, e, eOpts) {
	    	<jkd:haveAuthorize access="/product/getProductGiftInfoById.json">
	    	this.east.transferData(this.east, record, this.body.dom.clientWidth);
	    	this.east.show();
	    	</jkd:haveAuthorize>
	    }, this);
    },
    
    addProductGiftInfo : function(){
    	var productGiftFormPanel = Ext.create('MyExt.productManager.ProductGiftFormPanel', {
			id: 'add@productGiftFormPanel' + this.id,
    		viewer: this.viewer
   	 	});
    			  
    	var buttons = [{
			text: '<fmt:message key="button.save"/>',
			handler: function(){
				productGiftFormPanel.saveProductWholesale(true, this.store);
			},
			scope: this
		},{
			text: '<fmt:message key="button.cancel"/>',
			handler : function(){popWin.close();},
			scope: this
		}];
      	openWin('<fmt:message key="product.wholesale.add"/>', productGiftFormPanel, buttons, 600, 450);
    },
    
   	deleteProductGiftInfo : function(){
		var rowsData = [];		
		var records = this.gsm.getSelection();
		if(records.length == 0){
			showMsg('<fmt:message key="ajax.confirm"/>', '<fmt:message key="ajax.no.record"/>');
			return;
		}else if(records.length > 1 ){
			showMsg('<fmt:message key="ajax.confirm"/>', '<fmt:message key="user.approve.records"/>');
			return;
		}
		
		Ext.Msg.confirm('<fmt:message key="ajax.confirm"/>', '<fmt:message key="product.gift.confirm.delete"/>', function(e){
			if(e == 'yes'){
				Ext.Ajax.request({
		        	url: '<c:url value="/product/deleteProductGiftById.json"/>',
		         	method: 'post',
					scope: this,
					params:{giftId:records[0].data.giftId},
		          	success: function(response){
          				var responseObject = Ext.JSON.decode(response.responseText);
          				if (responseObject.success == true){
          					showSuccMsg(responseObject.message);
							this.store.loadPage(1);
		                    this.gsm.deselectAll();
          				}else{
          					showFailMsg(responseObject.message, 4);
          				}
					}
		     	})
	     	}
	 	}, this) 
    },
   
   	rendererProductTypeStuts : function(val){
		if(val == 1) {
            return '<b><fmt:message key="product.wareHouse.productType1"/></b>';
        }else if(val == 2){
            return '<fmt:message key="product.wareHouse.productType2"/>';
        }else if(val == 3){
            return '<fmt:message key="product.wareHouse.productType3"/>';
        }else {
           return '<fmt:message key="product.wareHouse.productType4"/>';
        }
	},
	
	rendererStuts : function(val){
		if(val == true) {
            return '<b><fmt:message key="button.yes"/></b>';
        }else{
            return '<b><fmt:message key="button.no"/></b>';
        }
	},
    
   	rendererWarehouseTypeStuts : function(val){
		if(val == 1) {
            return '<b><fmt:message key="product.wareHouse.warehouseType1"/></b>';
        }else{
            return '<fmt:message key="product.wareHouse.warehouseType2"/>';
        }
	},
	
	rendererLevelType : function(val){
		if(val == 1) {
            return '<b><fmt:message key="team.rule.question.level1"/></b>';
        }else if(val == 2) {
            return '<b><fmt:message key="team.rule.question.level2"/></b>';
        }else{
            return '<fmt:message key="team.rule.question.level3"/>';
        }
	},
	
	rendererStuts : function(val){
		if(val == true) {
            return '<b><fmt:message key="button.yes"/></b>';
        }else{
            return '<fmt:message key="button.no"/>';
        }
	}
});