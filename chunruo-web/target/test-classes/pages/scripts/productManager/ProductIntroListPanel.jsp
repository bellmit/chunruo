<%@ include file="/WEB-INF/pages/scripts/taglibs.jsp"%>

Ext.define('ProductIntro', {
	extend: 'Ext.data.Model',
	idProperty: 'introId',
     fields: [
    	{name: 'introId',	    mapping: 'introId',	    type: 'int'},
		{name: 'title',	 		mapping: 'title',		type: 'string'},
		{name: 'description',	mapping: 'description',	type: 'string'},
		{name: 'introduction',	mapping: 'introduction',type: 'string'},
		{name: 'sort',	        mapping: 'sort',	    type: 'int'},
		{name: 'createTime',	mapping: 'createTime',	type: 'string'},
		{name: 'updateTime',	mapping: 'updateTime',	type: 'string'},
    ]
});

Ext.define('MyExt.productManager.ProductIntroListPanel', {
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
			model: 'ProductIntro',
			proxy: {
				type: 'ajax',
				url: '<c:url value="/productIntro/list.json"/>',
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
        		{id: '4', name: '<fmt:message key="product.wareHouse.productType4"/>'},
        	]
        });
		
		this.rendererwarehouseTypeStore= Ext.create('Ext.data.Store', {
        	autoDestroy: true,
        	model: 'InitModel',
        	data: [
        		{id: '1', name: '<fmt:message key="product.wareHouse.warehouseType1"/>'},
        		{id: '2', name: '<fmt:message key="product.wareHouse.warehouseType2"/>'}
        	]
        });
		
		this.columns = [
			{text: '<fmt:message key="product.intro.introId"/>', dataIndex: 'introId', width: 140, sortable : true,
        		filter: {xtype: 'textfield'},
        		renderer: this.fontRenderer
        	},
        	{text: '<fmt:message key="product.intro.sort"/>', dataIndex: 'sort', width: 140, sortable : true,
        		filter: {xtype: 'textfield'},
        		renderer: this.fontRenderer
        	},
        	{text: '<fmt:message key="product.intro.title"/>', dataIndex: 'title', width: 140, sortable : true,
        		filter: {xtype: 'textfield'},
        		renderer: this.fontRenderer
        	},
        	{text: '<fmt:message key="product.intro.description"/>', dataIndex: 'description', width: 140, sortable : true,
        	filter: {xtype: 'textfield'},
        		renderer: this.fontRenderer
        	},
        	{text: '<fmt:message key="product.intro.introduction"/>', dataIndex: 'introduction', width: 500, sortable : true,
        		filter: {xtype: 'textfield'},
        		renderer: this.fontRenderer
        	},
        	{text: '<fmt:message key="product.intro.createTime"/>', dataIndex: 'createTime', width: 140, sortable : true,
        		filter: {xtype: 'textfield'},
        		renderer: this.fontRenderer
        	},
        	{text: '<fmt:message key="product.intro.updateTime"/>', dataIndex: 'updateTime', width: 140, sortable : true,
        		filter: {xtype: 'textfield'},
        		renderer: this.fontRenderer
        	}
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
		this.productWarehouseListBbar = this.pagingToolbar; 
		
    	this.productWarehouseList = Ext.create('Ext.grid.GridPanel', {
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
		    bbar: this.productWarehouseListBbar,
        	plugins: ['gridHeaderFilters'],
		    viewConfig: {
		        stripeRows: true,
		        enableTextSelection: true
		    }
	    });
	    
	    this.tbar = [
	    <jkd:haveAuthorize access="/productIntro/saveOrUpdateIntro.json">
	    {
        	text: '<fmt:message key="product.intro.add"/>', 
            iconCls: 'add', 
            handler: this.adds, 
            scope: this
        }
        <c:set var="isHaveAuthorize" value="true" />
		</jkd:haveAuthorize>
		<jkd:haveAuthorize access="/productIntro/saveOrUpdateIntro.json">
		<c:if test="${isHaveAuthorize}">,</c:if>
        {
        	text: '<fmt:message key="product.intro.edit"/>', 
            iconCls: 'Chartpieadd',
        	handler: this.edit, 
        	scope: this
        }
        <c:set var="isHaveAuthorize" value="true" />
		</jkd:haveAuthorize>
		<jkd:haveAuthorize access="/productIntro/deleteIntro.json">
		<c:if test="${isHaveAuthorize}">,</c:if>
       	{
        	text: '<fmt:message key="product.intro.delete"/>', 
            iconCls: 'delete',
        	handler: this.delete, 
        	scope: this
        }
        </jkd:haveAuthorize>
        ];

    	this.items = [this.productWarehouseList];	
    	this.callParent(arguments);
    	
    	this.gsm = this.productWarehouseList.getSelectionModel();
    	<jkd:haveAuthorize access="/productIntro/list.json">
    	this.store.on('beforeload', function(store, options) {
	        Ext.apply(store.proxy.extraParams, {
	        	filters: Ext.JSON.encode(this.productWarehouseList.filters)
			});
	    }, this);
	    this.store.load();
	    </jkd:haveAuthorize>
    },
    
    rendererStuts : function(val){
		if(val == true) {
            return '<b><fmt:message key="button.yes"/></b>';
        }else{
            return '<fmt:message key="button.no"/>';
        }
	},
    
    approves : function(){
		var rowsData = [];		
		var records = this.gsm.getSelection();
		if(records.length == 0){
			showMsg('<fmt:message key="ajax.confirm"/>', '<fmt:message key="ajax.no.record"/>');
			return;
		}	
		for(var i = 0; i < records.length; i++){			
			rowsData.push(records[i].data.applyId);	
		}
		
		Ext.Msg.confirm('<fmt:message key="ajax.confirm"/>', '<fmt:message key="user.applyAgent.approveConfirm"/>', function(e){
			if(e == 'yes'){
				Ext.Ajax.request({
		        	url: '<c:url value="/applyAgent/approve.json"/>',
		         	method: 'post',
					scope: this,
					params:{idListGridJson: Ext.JSON.encode(rowsData),tag:1},
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
    
    adds : function(){
        var rowsData = [];
		var records = this.gsm.getSelection();
		var productIntroFormPanel = Ext.create('MyExt.productManager.ProductIntroFormPanel', {
			id: 'productIntroFormPanel@' + this.id,
    		viewer: this.viewer,	
    		edit: false,
   	 	});
   	 	
    	var buttons = [{
			text: '<fmt:message key="button.save"/>',
			handler: function(){
			if(productIntroFormPanel.form.isValid()){
				Ext.Msg.confirm('<fmt:message key="ajax.confirm"/>', '<fmt:message key="save.confirm"/>', function(e){
					if(e == 'yes'){
				     	productIntroFormPanel.form.submit({
		                 		waitMsg: 'Loading...',
		                 		url: '<c:url value="/productIntro/saveOrUpdateIntro.json"/>',
		               			scope: this,
		               			success: function(form, action) {
		                   			var responseObject = Ext.JSON.decode(action.response.responseText);
		                   			if(responseObject.error == false){
		                  				showSuccMsg(responseObject.message);
		                  				this.store.loadPage(1);
		                    			this.gsm.deselectAll();
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
      	openWin(Ext.String.format('<fmt:message key="product.intro.add"/>'), productIntroFormPanel, buttons, 400, 320);
    },
    
    edit : function(){
        var rowsData = [];
		var records = this.gsm.getSelection();
		if(records.length == 0 ){
			showMsg('<fmt:message key="ajax.confirm"/>', '<fmt:message key="ajax.no.record"/>');
			return;
		}else if(records.length > 1 ){
			showMsg('<fmt:message key="ajax.confirm"/>', '<fmt:message key="user.approve.records"/>');
			return;
		}	
		
		var introId=records[0].data.introId;
		var title=records[0].data.title;
		var sort=records[0].data.sort;
		var description=records[0].data.description;
		var introduction=records[0].data.introduction;
		var productIntroFormPanel = Ext.create('MyExt.productManager.ProductIntroFormPanel', {
			id: 'productIntroFormPanel@' + this.id,
    		viewer: this.viewer,
    		title:title,
    		sort:sort,
    		description:description,	
    		introduction:introduction,
   	 	});

    	var buttons = [{
			text: '<fmt:message key="button.save"/>',
			handler: function(){	
			    if(productIntroFormPanel.form.isValid()){
					Ext.Msg.confirm('<fmt:message key="ajax.confirm"/>', '<fmt:message key="save.confirm"/>', function(e){
						if(e == 'yes'){
					     	productIntroFormPanel.form.submit({
		                 		waitMsg: 'Loading...',
		                 		url: '<c:url value="/productIntro/saveOrUpdateIntro.json"/>',
		               			scope: this,
		               			params:{introId:introId},
		               			success: function(form, action) {
		                   			var responseObject = Ext.JSON.decode(action.response.responseText);
		                   			if(responseObject.error == false){
		                  				showSuccMsg(responseObject.message);
		                  				this.store.loadPage(1);
		                    			this.gsm.deselectAll();
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
      	openWin(Ext.String.format('<fmt:message key="product.wareHouse.edit"/>'), productIntroFormPanel, buttons, 400, 320);
    },
    
   delete : function(){
		var rowsData = [];		
		var records = this.gsm.getSelection();
		if(records.length == 0){
			showMsg('<fmt:message key="ajax.confirm"/>', '<fmt:message key="ajax.no.record"/>');
			return;
		}	
		for(var i = 0; i < records.length; i++){			
			rowsData.push(records[i].data.introId);	
		}
		
		Ext.Msg.confirm('<fmt:message key="ajax.confirm"/>', '<fmt:message key="product.confirm.delete"/>', function(e){
			if(e == 'yes'){
				Ext.Ajax.request({
		        	url: '<c:url value="/productIntro/deleteIntro.json"/>',
		         	method: 'post',
					scope: this,
					params:{idListGridJson: Ext.JSON.encode(rowsData)},
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
	}
});