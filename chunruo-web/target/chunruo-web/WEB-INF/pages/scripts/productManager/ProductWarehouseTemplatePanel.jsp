<%@ include file="/WEB-INF/pages/scripts/taglibs.jsp"%>

Ext.define('ProductWarehouseTemplate', {
	extend: 'Ext.data.Model',
    fields: [
    	{name: 'templateId',	mapping: 'templateId',	type: 'int'},
    	{name: 'name',	 	    mapping: 'name',		type: 'string'},
    	{name: 'imagePath',	 	mapping: 'imagePath',	type: 'string'},
		{name: 'status',	    mapping: 'status',	    type: 'bool'},
		{name: 'updateTime',	mapping: 'updateTime',	type: 'string'},
		{name: 'createTime',	mapping: 'createTime',	type: 'string'}
    ]
});

Ext.define('MyExt.productManager.ProductWarehouseTemplatePanel', {
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
		 
    	this.store = Ext.create('Ext.data.Store', {
    		pageSize: 50,
	        autoLoad: false,
			autoDestroy: true,
			sortOnLoad: true,
			remoteSort: true,
			model: 'ProductWarehouseTemplate',
			proxy: {
				type: 'ajax',
				url: '<c:url value="/productWarehouse/templateList.json"/>',
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
		
		this.columns = [
			{text: '<fmt:message key="product.warehouse.template.imagePath"/>', dataIndex: 'imagePath', width: 35, sortable : false,
		        renderer : function(val, metadata, record, rowIndex, columnIndex, store){ 
					return '<img height="16" width="16" src="' + val + '">';
				}
        	},
			{text: '<fmt:message key="product.warehouse.template.templateId"/>', dataIndex: 'templateId', width: 70, sortable : true},
        	{text: '<fmt:message key="product.warehouse.template.name"/>', dataIndex: 'name', width: 120, sortable : true,},
            {text: '<fmt:message key="start.image.template.status"/>', dataIndex: 'status', width: 80, sortable : true,
	       		renderer : function(value){
					if(value == 0){
						return '<span style="color:red;"><b><fmt:message key='button.no'/></b></span>';
					}else if(value == 1){
						return '<span style="color:blue;"><b><fmt:message key='button.yes'/></b></span>';
					}
				}
        	}
        ];
        
        this.tbar = [
        <jkd:haveAuthorize access="/productWarehouse/saveTemplate.json">
        {
        	text: '<fmt:message key="template.save.update"/>', 
        	iconCls: 'add', 	
        	handler: this.addTemplate,
        	scope: this
        }
        <c:set var="isHaveAuthorize" value="true" />
		</jkd:haveAuthorize>
		<jkd:haveAuthorize access="/productWarehouse/setTemplateStatus.json">
		<c:if test="${isHaveAuthorize}">,</c:if>
        '-',{
        	text: '<fmt:message key="button.enable"/>', 
        	iconCls: 'enable',	
        	handler: this.enableTemplate, 
        	scope: this
        }
        <c:set var="isHaveAuthorize" value="true" />
		</jkd:haveAuthorize>
		<jkd:haveAuthorize access="/productWarehouse/setTemplateStatus.json">
		<c:if test="${isHaveAuthorize}">,</c:if>
        '-',{
        	text: '<fmt:message key="button.disable"/>', 
        	iconCls: 'Cancel',	
        	handler: this.cancelTemplate, 
        	scope: this
        }
        </jkd:haveAuthorize>
        ];
		
		this.questionList = Ext.create('Ext.grid.GridPanel', {
			region: 'west',
			header: false,
			width: 350,
			autoScroll: true,   
			closable: true,
			selType: 'checkboxmodel',
			multiSelect: true,
			columnLines: true,
			animCollapse: true,
		    enableLocking: true,
		    columns: this.columns,
			plugins: ['gridHeaderFilters','gridexporter'],
		    store: this.store,
		    viewConfig: {
		        stripeRows: true,
		        enableTextSelection: true
		    }
	    });     
	    
	    this.east =  Ext.create('MyExt.productManager.ProductWarehouseList', {
        	region: 'center',
	        header: false,
	        autoScroll: true
        });
    	
    	this.items = [this.questionList, this.east];	
		this.east.hide();
		this.callParent(arguments);
	    
	    this.gsm = this.questionList.getSelectionModel();
	    <jkd:haveAuthorize access="/productWarehouse/templateList.json">
	   	this.store.on('beforeload', function(store, options) {
	        Ext.apply(store.proxy.extraParams, {
	        	filters: Ext.JSON.encode(this.questionList.filters)
			});
	    }, this);
	    this.store.load();   
	    </jkd:haveAuthorize>
	    
	    this.questionList.on('itemdblclick', function(view, record, item, index, e, eOpts) {
	    	<jkd:haveAuthorize access="/productWarehouse/getProductWarehouseListByTemplateId.json">
	    	this.east.transferData(record);
	    	this.east.show();
	    	</jkd:haveAuthorize>
	    }, this);
    },
    
     addTemplate : function(){
       var records = this.gsm.getSelection();
       if(records.length > 1 ){
			showMsg('<fmt:message key="ajax.confirm"/>', '<fmt:message key="user.approve.records"/>');
			return;
		}
		var templateId = '';
		var name = '';
		if(records.length == 1){
			templateId = records[0].data.templateId;
			name = records[0].data.name;
		}
	
    	var productWarehouseTemplateFormPanel = Ext.create('MyExt.productManager.ProductWarehouseTemplateFormPanel', {
			id: 'add@productWarehouseTemplateFormPanel' + this.id,
    		viewer: this.viewer,
    		edit: false,
    		templateId:templateId,
    		name:name,
   	 	});
   	 	
   	 	if(records.length == 1){
   	 		var imageObj = productWarehouseTemplateFormPanel.down('[xtype=imagepanel]');
    		imageObj.store.removeAll();
    		if(records[0].data.imagePath != null && records[0].data.imagePath != ''){
    			try{
    				imageObj.store.removeAll();
    				imageObj.store.insert(0, {
						fileId: 1,
						fileName: records[0].data.imagePath,
						fileType: 'png',
						filePath: records[0].data.imagePath,
						fileState: 200
					});
    			}catch(e){}
    		}
   	 	}
   	 	
    	var buttons = [{
			text: '<fmt:message key="button.save"/>',
			handler: function(){
				if(productWarehouseTemplateFormPanel.isValid()){
				var rowsDatas = [];    
		       	productWarehouseTemplateFormPanel.down('imagepanel').store.each(function(record) {
		       		record.data.input_file = null;
		            rowsDatas.push(record.data);    
		      	}, this);
					Ext.MessageBox.confirm('<fmt:message key="ajax.confirm"/>', '<fmt:message key="save.confirm"/>', function(e){
						if(e == 'yes'){
							productWarehouseTemplateFormPanel.submit({
		                    	url: '<c:url value="/productWarehouse/saveTemplate.json"/>',
		                    	waitMsg: '<fmt:message key="ajax.loading"/>',
		                    	scope: this,
		                    	params:{recodeGridJson: Ext.JSON.encode(rowsDatas)},
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
      	openWin('<fmt:message key="button.add"/>', productWarehouseTemplateFormPanel, buttons, 380, 320);
    },     
    
    
    deleteTemplate : function() {
   		var rowsData = [];		
		var records = this.gsm.getSelection();
		if(records.length == 0){
			showMsg('<fmt:message key="ajax.confirm"/>', '<fmt:message key="ajax.no.record"/>');
			return;
		}	
		for(var i = 0; i < records.length; i++){	
			rowsData.push(records[i].data.templateId);	
		}
		
	    Ext.MessageBox.confirm('<fmt:message key="ajax.confirm"/>', '<fmt:message key="delete.confirm"/>', function(e){
			if(e == 'yes'){
	        	Ext.Ajax.request({
		        	url: '<c:url value="/startImage/deleteTemplate.json"/>',
		         	method: 'post',
					scope: this,
					params:{idListGridJson: Ext.JSON.encode(rowsData), isEnabled: true},
					success: function(response){
          				var responseObject = Ext.JSON.decode(response.responseText);
                        if(responseObject.success == true){
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
    
	enableTemplate : function() {
   		var rowsData = [];		
		var records = this.gsm.getSelection();
		if(records.length == 0){
			showMsg('<fmt:message key="ajax.confirm"/>', '<fmt:message key="ajax.no.record"/>');
			return;
		}	
		for(var i = 0; i < records.length; i++){	
			rowsData.push(records[i].data.templateId);	
		}
		
	    Ext.MessageBox.confirm('<fmt:message key="ajax.confirm"/>', '<fmt:message key="save.confirm"/>', function(e){
			if(e == 'yes'){
	        	Ext.Ajax.request({
		        	url: '<c:url value="/productWarehouse/setTemplateStatus.json"/>',
		         	method: 'post',
					scope: this,
					params:{idListGridJson: Ext.JSON.encode(rowsData), isEnabled: true},
					success: function(response){
          				var responseObject = Ext.JSON.decode(response.responseText);
                        if(responseObject.success == true){
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
   	
   	cancelTemplate : function() {
   		var rowsData = [];		
		var records = this.gsm.getSelection();
		if(records.length == 0){
			showMsg('<fmt:message key="ajax.confirm"/>', '<fmt:message key="ajax.no.record"/>');
			return;
		}	
		for(var i = 0; i < records.length; i++){			
			rowsData.push(records[i].data.templateId);	
		}
		
	    Ext.MessageBox.confirm('<fmt:message key="ajax.confirm"/>', '<fmt:message key="save.confirm"/>', function(e){
			if(e == 'yes'){
	        	Ext.Ajax.request({
		        	url: '<c:url value="/productWarehouse/setTemplateStatus.json"/>',
		         	method: 'post',
					scope: this,
					params:{idListGridJson: Ext.JSON.encode(rowsData), isEnabled: false},
					success: function(response){
          				var responseObject = Ext.JSON.decode(response.responseText);
                        if(responseObject.success == true){
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
	
	booleanRenderer: function(value, meta, record) { 
       	if(value =="true") {
            return '<b><fmt:message key="product.question.status_1"/></b>';
        }else{
            return '<fmt:message key="product.question.status_0"/>';
        }  
   	},
   	
   	fontRenderer: function(value, meta, record) {
    	meta.style = 'overflow:auto;padding: 3px 6px;text-overflow: ellipsis;white-space: nowrap;white-space:normal;line-height:24px;font-weight:bold;';      
       	return value;     
   	} 
});