<%@ include file="/WEB-INF/pages/scripts/taglibs.jsp"%>

Ext.define('ProductCategory', {
	extend: 'Ext.data.TreeModel',
	idProperty: 'categoryId',
   	fields: [
    	{name: 'id',     		mapping: 'id',				type: 'string'},
        {name: 'parentId',      mapping: 'parentId',  	    type: 'int'},
        {name: 'name',          mapping: 'name',	        type: 'string'},
        {name: 'imagePath',     mapping: 'imagePath', 		type: 'string'},
        {name: 'text',     		mapping: 'text',			type: 'string'},
        {name: 'expanded',     	mapping: 'expanded',		type: 'bool'},
        {name: 'description',   mapping: 'description',		type: 'string'},
        {name: 'status',        mapping: 'status',			type: 'string'},
        {name: 'leaf',     		mapping: 'leaf',			type: 'bool'},
        {name: 'tagNames',     	mapping: 'tagNames',		type: 'string'}
    ]
});

Ext.define('MyExt.productManager.ProductCategoryPanel', {
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
		Ext.apply(this, config);
		
	    this.booleanStore = Ext.create('Ext.data.Store', {
			autoDestroy: true,
			model: 'InitModel',
			data: [
				{id: '1', name: '<fmt:message key="button.yes"/>'},
				{id: '0', name: '<fmt:message key="button.no"/>'},
			]
		});
		
		this.store = Ext.create('Ext.data.TreeStore', {
			model: 'ProductCategory',
        	proxy: {
           	 	type: 'ajax',
            	url: '<c:url value="/category/getCategoryList.json"/>'
        	},
        	root: {
            	text: 'ROOT',
            	expanded: true
        	},
        	sorters: [{
	            property: 'id',
	            direction: 'asc'
	        }]
    	});
    	
		this.menuTreePanel = Ext.create('Ext.TreePanel', {
			categoryId: 0,
			region: 'west',
	        header: false,
	        width: 2000,
        	collapsible: true,
        	columnLines: true,
        	scroll: 'both',
            lines: true,
            rowLines: true,
        	useArrows: true,
        	rootVisible: false,
        	selModel : { 
            selType : 'checkboxmodel', 
            mode : 'SIMPLE', 
            checkOnly : true, 
            }, 
        	store: this.store,
	        multiSelect: true,
	        columns: [{
	            xtype: 'treecolumn', 
	            columnLines: true,
	            text: '<fmt:message key="product.category.name"/>',
	            width: 220,
	            sortable: false,
	            dataIndex: 'text',
	            locked: true
	        },{
	            text: '<fmt:message key="product.category.categoryId"/>',
           	 	width: 70,
            	dataIndex: 'id',
            	sortable: false,
                locked: true
	        },{
	            text: '<fmt:message key="product.category.status"/>',
           	 	width: 70,
            	dataIndex: 'status',
            	sortable: false,
            	locked: true,
            	renderer: this.booleanRenderer,
	        		filter: {
						xtype: 'combobox',
				        displayField: 'name',
				        valueField: 'id',
				        store: this.booleanStore,
				        queryMode: 'local',
				        typeAhead: true
					}
          
	        },{
	            text: '<fmt:message key="product.category.sort"/>',
           	 	width: 70,
            	dataIndex: 'sort',
            	sortable: false,
                locked: true
	        },{
	            text: '<fmt:message key="product.category.level"/>',
           	 	width: 70,
            	dataIndex: 'level',
            	sortable: false,
                locked: true
	        },{
	            text: '<fmt:message key="product.category.profit"/>',
           	 	width: 250,
            	dataIndex: 'profit',
            	sortable: false,
                locked: true
	        },{
	            text: '<fmt:message key="product.category.tag"/>',
           	 	width: 200,
            	dataIndex: 'tagNames',
             	sortable: false,
                locked: true
	        },{
	            text: '<fmt:message key="product.category.wapImage"/>',
           	 	width: 200,
            	dataIndex: 'imagePath',
             	sortable: false,
                locked: true
	        },{
	            text: '<fmt:message key="product.category.createTime"/>',
           	 	width: 140,
            	dataIndex: 'createTime',
            	sortable: false,
            	locked: true
	        },{
	            text: '<fmt:message key="product.category.updateTime"/>',
           	 	width: 140,
            	dataIndex: 'updateTime',
            	sortable: false,
            	locked: true
            	
	        }]
        });
        
        this.tbar =[
        <jkd:haveAuthorize access="/userSys/saveAdminUser.json">
        {
	       	iconCls: 'refresh',
	       	type: 'root',
			text: '<fmt:message key="system.menu.refresh"/>',
			handler: function(){this.store.reload()},
	    	scope: this
	    }
	    <c:set var="isHaveAuthorize" value="true" />
		</jkd:haveAuthorize>
		<jkd:haveAuthorize access="/userSys/editUser.json,/userSys/saveAdminUser.json">
		<c:if test="${isHaveAuthorize}">,</c:if>
	    '-',{
       	  	text: '<fmt:message key="product.category.addFirst"/>', 
       	  	iconCls: 'enable', 	
       	  	handler: this.addFirst
     	}
     	<c:set var="isHaveAuthorize" value="true" />
		</jkd:haveAuthorize>
		<jkd:haveAuthorize access="//category/deleteCategory.json">
		<c:if test="${isHaveAuthorize}">,</c:if>
     	{
       	  	text: '<fmt:message key="product.category.delete"/>', 
       	  	iconCls: 'delete', 	
       	  	scope:this,
       	  	handler: this.deleteCategory
        }
        <c:set var="isHaveAuthorize" value="true" />
		</jkd:haveAuthorize>
		<jkd:haveAuthorize access="/category/setCategoryDisEnabled.json">
		<c:if test="${isHaveAuthorize}">,</c:if>
        '-',{
        	text: '<fmt:message key="product.category.status1"/>', 
        	iconCls: 'Packageadd', 	
        	scope: this,
        	handler: this.modifyEnabledCategory,
   		}
   		<c:set var="isHaveAuthorize" value="true" />
		</jkd:haveAuthorize>
		<jkd:haveAuthorize access="/category/setCategoryDisEnabled.json">
		<c:if test="${isHaveAuthorize}">,</c:if>
   		{
       	  text: '<fmt:message key="product.category.status0"/>', 
       	  iconCls: 'disabled', 	
       	  scope: this,
       	  handler: this.modifyDisabledCategory,
        }
        </jkd:haveAuthorize>
        ];
        
        this.menu = Ext.create('Ext.menu.Menu', {
        	items:[{
		       	iconCls: 'add',
				text: '<fmt:message key="product.category.addSecond"/>',
				handler: this.addSecond,
		    	scope: this
		    }]
    	});
        
      	this.east =  Ext.create('MyExt.productManager.CategoryTabPanel', {
      		viewer: this.viewer,
		 	layout: 'card',
	        border: false,
	       	region: 'east',
	        width: this.xWidth/2,
	        minWidth: this.xWidth/2,
	        split: true,
	        header: false,
	        hidden: true,
	        store: this.store
        });
        
    	this.items = [this.menuTreePanel, this.east];	
    	this.east.hide();
    	this.callParent(arguments);
    	
    	this.store.on('beforeload', function(store, options) {
	        Ext.apply(store.proxy.extraParams, {
	        	categoryId: this.menuTreePanel.categoryId
			});
	    }, this);
	    
    	this.gsm = this.menuTreePanel.getSelectionModel();
	    this.menuTreePanel.on('itemclick', function(treePanel, record, item, index, e){
	    	e.stopEvent();
	    	
	    	<jkd:haveAuthorize access="/category/getCategoryById.json">
	    	this.east.transferData(this.east, record, this.body.dom.clientWidth);
	    	this.east.show();
	    	</jkd:haveAuthorize>
	    }, this);
	    
    	this.menuTreePanel.on('itemcontextmenu', function(view, record, item, index, e){
    		e.stopEvent();
    		
    		<jkd:haveAuthorize access="/category/saveCategory.json">
    		this.menu.items.each(function(m){
				m.hide();
				if(!record.raw.leaf && m.iconCls == 'add'){
					m.show();
				}else if(!record.raw.leaf && m.iconCls == 'Chartpieerror'){
				    m.show();
				}else if(record.raw.leaf && m.iconCls == 'delete'){
					m.show();
				}else if(record.raw.leaf && m.iconCls == 'Chartpieadd'){
					m.show();
				}
			});
			
			this.menu.record = record;
	    	this.menu.showAt(e.getXY());
	    	</jkd:haveAuthorize>		
		}, this);
    },
    
    modifyEnabledCategory: function(status){
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
	        		url: '<c:url value="/category/setCategoryDisEnabled.json"/>',
	         		method: 'post',
					scope: this,
					params:{categoryId : records[0].data.id, status: 1},
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
    
    modifyDisabledCategory: function(status){
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
	        		url: '<c:url value="/category/setCategoryDisEnabled.json"/>',
	         		method: 'post',
					scope: this,
					params:{categoryId : records[0].data.id, status: 0},
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
    
    deleteCategory: function(){
      	var records = this.gsm.getSelection();
 	    if(records.length == 0){
	  		showMsg('<fmt:message key="ajax.confirm"/>', '<fmt:message key="ajax.no.record"/>');
	  		return;
   		}else if(records.length > 1){
	  		showMsg('<fmt:message key="ajax.confirm"/>', '<fmt:message key="ajax.only.one.record"/>');
	  		return;
   		}
   
   		Ext.Msg.confirm('<fmt:message key="ajax.confirm"/>', '<fmt:message key="delete.confirm"/>', function(e){
			if(e == 'yes'){
				Ext.Ajax.request({
        			url: '<c:url value="/category/deleteCategory.json"/>',
         			method: 'post',
					scope: this,
					params:{categoryId: records[0].data.id,level:records[0].data.level},
          			success: function(response){
        				var responseObject = Ext.JSON.decode(response.responseText);
                      	if(responseObject.success == true){
                     		showSuccMsg(responseObject.message);
                     		this.menuTreePanel.warehouseId = responseObject.warehouseId;
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
    
    addFirst : function(){
    	var productCategoryFormPanel = Ext.create('MyExt.productManager.ProductCategoryFormPanel', {
    		store: this.store,
    		id:0,
	    	scope: this,
        	title: '<fmt:message key="button.add"/>'
        });
	    openWin('<fmt:message key="button.add"/>', productCategoryFormPanel, [], 500, 450);
    },
    
    editFirst: function(){
   		var textNameMsg = Ext.create('Ext.form.TextField', {
 			fieldLabel: '<fmt:message key="product.category.name"/>',
    		allowBlank: false,
    		labelWidth: 60,
    		value: this.menu.record.data.text,
       		anchor: '100%'
 		});	
	
 		var sortMsg = Ext.create('Ext.form.TextField', {
 			fieldLabel: '<fmt:message key="product.category.sort"/>',
    		allowBlank: false,
    		labelWidth: 60,
    		value: this.menu.record.data.sort,
       		anchor: '100%'
 		});	
	
 		var tagNamesMsg = Ext.create('Ext.form.TextField', {
 			fieldLabel: '<fmt:message key="product.category.tagNames"/>',
    		allowBlank: false,
    		labelWidth: 60,
    		value: this.menu.record.data.tagNames,
       		anchor: '100%'
 		});	
	 	
	 	var buttons = [{ 	
			text: '<fmt:message key="button.save"/>', 
			scope: this,  
	        handler: function(){
				if(textNameMsg.getValue() == null || textNameMsg.getValue().length == 0){ 
					showWarnMsg('<fmt:message key="order.close.confirm"/>', 8);
					return;
				}
				
		     	Ext.MessageBox.confirm('<fmt:message key="ajax.confirm"/>', '<fmt:message key="save.confirm"/>', function(e){
					if(e == 'yes'){
						Ext.Ajax.request({
				        	url: '<c:url value="/category/editProductCategory.json"/>',
				         	method: 'post',
							scope: this,
							params:{categoryId: this.menu.record.data.id, name: textNameMsg.getValue(),sort:sortMsg.getValue(),tagNames:tagNamesMsg.getValue()},
				          	success: function(response){
		          				var responseObject = Ext.JSON.decode(response.responseText);
		          				if(responseObject.error == false){
		                       		showSuccMsg(responseObject.message);
		                        	this.menuTreePanel.id = responseObject.categoryId;
	                        		this.store.reload();
	                        		popFormWin.close();
								}else{
									this.show();
									showFailMsg(responseObject.message, 4);
								}
							}
				     	})
	        		}
	        	}, this)
	      	}
		},{
			text: '<fmt:message key="button.cancel"/>',
			handler : function(){popFormWin.close();},
			scope: this
		}];
      	openFormWin('<fmt:message key="postage.template.edit"/>', [textNameMsg, sortMsg], buttons, 500, 450);
    },
    
   	addSecond : function(){
     	var productCategoryFormPanel = Ext.create('MyExt.productManager.ProductCategoryFormPanel', {
			store: this.store,
			id:this.menu.record.data.id,
			level:this.menu.record.data.level,
			text : this.menu.record.data.text,
			isAddSecond: true,
			scope: this,
			title: '<fmt:message key="button.add"/>'
		});
		openWin('<fmt:message key="button.add"/>', productCategoryFormPanel, [], 500, 450);
   	},

	booleanRenderer: function(value, meta, record) {  
		if(value == 0) {
			return '<span style="color:green;"><fmt:message key="button.no"/></span>';
		}else if(value == 1){
			return '<span style="color:red;"><b><fmt:message key="button.yes"/></b></span>';
		} 
		return value;
   	},
   	
   	fontRenderer: function(value, meta, record) {
    	meta.style = 'overflow:auto;padding: 3px 6px;text-overflow: ellipsis;white-space: nowrap;white-space:normal;line-height:24px;font-weight:bold;';      
       	return value;     
   	} 
});