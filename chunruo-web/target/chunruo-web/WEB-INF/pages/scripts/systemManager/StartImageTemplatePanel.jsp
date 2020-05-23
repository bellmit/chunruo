<%@ include file="/WEB-INF/pages/scripts/taglibs.jsp"%>

Ext.define('StartImageTemplate', {
	extend: 'Ext.data.Model',
    fields: [
    	{name: 'templateId',	mapping: 'templateId',	type: 'int'},
		{name: 'status',	 	mapping: 'status',		type: 'bool'},
		{name: 'isDelete',	    mapping: 'isDelete',	type: 'bool'},
		{name: 'updateTime',	mapping: 'updateTime',	type: 'string'},
		{name: 'productId',		mapping: 'productId',   type: 'string'},
		{name: 'isInvitePage',	mapping: 'isInvitePage',type: 'bool'},
		{name: 'beginTime',		mapping: 'beginTime',	type: 'string'},
		{name: 'endTime',		mapping: 'endTime',	    type: 'string'},
		{name: 'createTime',	mapping: 'createTime',	type: 'string'}
    ]
});

Ext.define('MyExt.systemManager.StartImageTemplatePanel', {
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
			model: 'StartImageTemplate',
			proxy: {
				type: 'ajax',
				url: '<c:url value="/startImage/templateList.json"/>',
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
			{text: '<fmt:message key="start.image.template.templateId"/>', dataIndex: 'templateId', width: 50, sortable : true},
        	{text: '<fmt:message key="start.image.template.beginTime"/>', dataIndex: 'beginTime', width: 140, sortable : true},
        	{text: '<fmt:message key="start.image.template.endTime"/>', dataIndex: 'endTime', width: 140, sortable : true},
        	{text: '<fmt:message key="start.image.template.isInvitePage"/>', dataIndex: 'isInvitePage', width: 80, sortable : true,
        		renderer : function(value){
					if(value == 0){
						return '<span style="color:red;"><b><fmt:message key='button.no'/></b></span>';
					}else if(value == 1){
						return '<span style="color:blue;"><b><fmt:message key='button.yes'/></b></span>';
					}
				}
			},
        	{text: '<fmt:message key="start.image.template.productId"/>', dataIndex: 'productId', width: 60,sortable : true},
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
        <jkd:haveAuthorize access="/startImage/saveTemplate.json">
        {
        	text: '<fmt:message key="template.save.update"/>', 
        	iconCls: 'add', 	
        	handler: this.addTemplate,
        	scope: this
        }
        <c:set var="isHaveAuthorize" value="true" />
		</jkd:haveAuthorize>
		<jkd:haveAuthorize access="/startImage/deleteTemplate.json">
		<c:if test="${isHaveAuthorize}">,</c:if>
        '-',{
        	text: '<fmt:message key="button.delete"/>', 
        	iconCls: 'delete',	
        	handler: this.deleteTemplate, 
        	scope: this
        }
        <c:set var="isHaveAuthorize" value="true" />
		</jkd:haveAuthorize>
		<jkd:haveAuthorize access="/startImage/setTemplateStatus.json">
		<c:if test="${isHaveAuthorize}">,</c:if>
        '-',{
        	text: '<fmt:message key="button.enable"/>', 
        	iconCls: 'enable',	
        	handler: this.enableTemplate, 
        	scope: this
        }
        <c:set var="isHaveAuthorize" value="true" />
		</jkd:haveAuthorize>
		<jkd:haveAuthorize access="/startImage/setTemplateStatus.json">
		<c:if test="${isHaveAuthorize}">,</c:if>
        '-',{
        	text: '<fmt:message key="button.disable"/>', 
        	iconCls: 'Cancel',	
        	handler: this.cancelTemplate, 
        	scope: this
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
		
		this.questionList = Ext.create('Ext.grid.GridPanel', {
			region: 'west',
			header: false,
			width: 580,
			autoScroll: true,   
			closable: true,
			selType: 'checkboxmodel',
			multiSelect: true,
			columnLines: true,
			animCollapse: true,
		    enableLocking: true,
		    columns: this.columns,
		    bbar: this.pagingToolbar,
			plugins: ['gridHeaderFilters','gridexporter'],
		    store: this.store,
		    viewConfig: {
		        stripeRows: true,
		        enableTextSelection: true
		    }
	    });     
	    
	    this.east =  Ext.create('MyExt.systemManager.StartImagePanel', {
        	region: 'center',
	        header: false,
	        autoScroll: true
        });
    	
    	this.items = [this.questionList, this.east];	
		this.east.hide();
		this.callParent(arguments);
	    
	    this.gsm = this.questionList.getSelectionModel();
	    <jkd:haveAuthorize access="/startImage/templateList.json">
	   	this.store.on('beforeload', function(store, options) {
	        Ext.apply(store.proxy.extraParams, {
	        	filters: Ext.JSON.encode(this.questionList.filters)
			});
	    }, this);
	    this.store.load();  
	    </jkd:haveAuthorize> 
	    
	    this.questionList.on('itemdblclick', function(view, record, item, index, e, eOpts) {
	    	<jkd:haveAuthorize access="/startImage/getStartImageListByTemplateId.json">
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
		var beginTime = '';
		var endTime = '';
		var productId = '';
		var isInvitePage = '';
		if(records.length == 1){
			templateId = records[0].data.templateId;
			beginTime = records[0].data.beginTime;
			endTime = records[0].data.endTime;
			productId = records[0].data.productId;
			isInvitePage = records[0].data.isInvitePage;
		}
	
    	var startImageTemplateFormPanel = Ext.create('MyExt.systemManager.StartImageTemplateFormPanel', {
			id: 'add@startImageTemplateFormPanel' + this.id,
    		viewer: this.viewer,
    		edit: false,
    		templateId:templateId,
    		beginTime:beginTime,
    		endTime:endTime,
    		productId:productId,
    		isInvitePage:isInvitePage
   	 	});
   	 	
    	var buttons = [{
			text: '<fmt:message key="button.save"/>',
			handler: function(){
				if(startImageTemplateFormPanel.isValid()){
					Ext.MessageBox.confirm('<fmt:message key="ajax.confirm"/>', '<fmt:message key="save.confirm"/>', function(e){
						if(e == 'yes'){
							startImageTemplateFormPanel.submit({
		                    	url: '<c:url value="/startImage/saveTemplate.json"/>',
		                    	waitMsg: '<fmt:message key="ajax.loading"/>',
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
      	openWin('<fmt:message key="button.add"/>', startImageTemplateFormPanel, buttons, 380, 200);
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
		        	url: '<c:url value="/startImage/setTemplateStatus.json"/>',
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
		        	url: '<c:url value="/startImage/setTemplateStatus.json"/>',
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