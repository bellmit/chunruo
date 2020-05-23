<%@ include file="/WEB-INF/pages/scripts/taglibs.jsp"%>

Ext.define('NoviceGuide', {
	extend: 'Ext.data.Model',
    fields: [
    	{name: 'guideId',			mapping: 'guideId',			type: 'int'},
		{name: 'imagePath',	 		mapping: 'imagePath',		type: 'string'},
		{name: 'phoneType',	 		mapping: 'phoneType',		type: 'int'},
		{name: 'width',	 			mapping: 'width',			type: 'int'},
		{name: 'height',	 		mapping: 'height',			type: 'int'},
		{name: 'status',	 		mapping: 'status',			type: 'bool'},
		{name: 'updateTime',	 	mapping: 'updateTime',		type: 'string'},
		{name: 'createTime',	 	mapping: 'createTime',		type: 'string'}
    ]
});

Ext.define('MyExt.systemManager.NoviceGuideListPanel', {
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
			model: 'NoviceGuide',
			proxy: {
				type: 'ajax',
				url: '<c:url value="/noviceGuide/list.json"/>',
				reader: {
					type : 'json',
                	root: 'data',
                	totalProperty: 'totalCount'
            	}
			},
			sorters: [{
	            property: 'id',
	            direction: 'desc'
	        }]
		});
		
		this.rendererStutsStore = Ext.create('Ext.data.Store', {
        	autoDestroy: true,
        	model: 'InitModel',
        	data: [
        		{id: 1, name: '<fmt:message key="start.image.phoneType1"/>'},
        		{id: 0, name: '<fmt:message key="start.image.phoneType0"/>'}
        	]
        });
        
        this.isEnableStore= Ext.create('Ext.data.Store', {
			autoDestroy: true,
			model: 'InitModel',
			data: [
        		{id: 1, name: '<fmt:message key="button.yes"/>'},
        		{id: 0, name: '<fmt:message key="button.no"/>'}
        	]
		});
		
		this.columns = [
            {text: '<fmt:message key="start.image.id"/>', dataIndex: 'guideId', width: 70, sortable : false,
        		filter: {xtype: 'textfield'},
        	},
        	{text: '<fmt:message key="start.image.phoneType"/>', dataIndex: 'phoneType',  width: 210, sortable : true,
        		renderer: this.rendererStatus, 
        		filter: {xtype: 'textfield'},
        
        	},
        	{text: '<fmt:message key="start.image.height"/>', dataIndex: 'height',  width: 210, sortable : true,
        		filter: {xtype: 'textfield'},
        	},
        	{text: '<fmt:message key="start.image.width"/>', dataIndex: 'width', width: 210, sortable : true,
        		filter: {xtype: 'textfield'},
        	},
        	{text: '<fmt:message key="sellers.status"/>', dataIndex: 'status', width: 260,  sortable : true,
				filter: {xtype: 'textfield'},
				renderer : function(value){
					if(value == 0){
						return '<span style="color:red;"><b><fmt:message key='button.no'/></b></span>';
					}else if(value == 1){
						return '<span style="color:blue;"><b><fmt:message key='button.yes'/></b></span>';
					}
				}
	    	},
	    	{text: '<fmt:message key="product.wholesale.createTime"/>', dataIndex: 'createTime', width: 140, sortable : true,
        		filter: {xtype: 'textfield'}
        	},
        	{text: '<fmt:message key="product.wholesale.updateTime"/>', dataIndex: 'updateTime', width: 140, sortable : true,
        		filter: {xtype: 'textfield'}
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
		
		this.list = Ext.create('Ext.grid.GridPanel', {
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
	    
	    this.tbar = [
	    <jkd:haveAuthorize access="/noviceGuide/saveOrUpdateNoviceGuide.json">
	    {
        	text: '<fmt:message key="button.add"/>', 
        	iconCls: 'add', 	
        	handler: this.addNoviceGuide, 
        	scope: this
        }
        <c:set var="isHaveAuthorize" value="true" />
		</jkd:haveAuthorize>
		<jkd:haveAuthorize access="/noviceGuide/delete.json">
		<c:if test="${isHaveAuthorize}">,</c:if>
        '-',{
        	text: '<fmt:message key="button.delete"/>', 
        	iconCls: 'delete', 	
        	handler:  this.deleteNoviceGuide, 
        	scope: this
        }
        <c:set var="isHaveAuthorize" value="true" />
		</jkd:haveAuthorize>
		<jkd:haveAuthorize access="/noviceGuide/changeStatus.json">
		<c:if test="${isHaveAuthorize}">,</c:if>
        '-',{
        	text: '<fmt:message key="button.enable"/>', 
        	iconCls: 'enable', 	
        	handler: this.enable, 
        	scope: this
        }
        <c:set var="isHaveAuthorize" value="true" />
		</jkd:haveAuthorize>
		<jkd:haveAuthorize access="/noviceGuide/changeStatus.json">
		<c:if test="${isHaveAuthorize}">,</c:if>
        '-',{
        	text: '<fmt:message key="button.disable"/>', 
        	iconCls: 'Cancel', 	
        	handler: this.disable, 
        	scope: this
        }
        </jkd:haveAuthorize>
        ];
	    
	    this.east =  Ext.create('MyExt.systemManager.NoviceGuideTabPanel', {
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
    	
    	this.gsm = this.list.getSelectionModel();
    	this.items = [this.list, this.east];	
		this.east.hide();
		this.callParent(arguments);
	    
	    <jkd:haveAuthorize access="/noviceGuide/list.json">
	   	this.store.on('beforeload', function(store, options) {
	        Ext.apply(store.proxy.extraParams, {
	        	filters: Ext.JSON.encode(this.list.filters),
			});
	    }, this);
	    this.store.load(); 
	    </jkd:haveAuthorize>  
	    
	    this.list.on('itemdblclick', function(view, record, item, index, e, eOpts) {
	    	<jkd:haveAuthorize access="/noviceGuide/getNoviceGuideByGuideId.json,/noviceGuide/saveOrUpdateNoviceGuide.json">
	    	this.east.transferData(this.east, record, this.body.dom.clientWidth);
	    	this.east.show();
	    	</jkd:haveAuthorize>  
	    }, this);
    },
    
    transferData : function( record){
    	this.record = record;
    	this.store.load();
    },
    
    rendererStatus : function(val){
		var str =  "";
		if(val == 0){
			str = '<b><fmt:message key="start.image.phoneType0"/></b>';
		}else if(val == 1 ){
			str = '<b><fmt:message key="start.image.phoneType1"/></b>';
		}
		return str;
	},
	
	inviteStus : function(val){
		if(val == true) {
            return '<b><fmt:message key="button.yes"/></b>';
        }else{
            return '<fmt:message key="button.no"/>';
        }
	},
	
    enable : function(){
    	var rowsData = [];		
		var records = this.gsm.getSelection();
		if(records.length == 0){
			showMsg('<fmt:message key="ajax.confirm"/>', '<fmt:message key="ajax.no.record"/>');
			return;
		}	
		for(var i = 0; i < records.length; i++){			
			rowsData.push(records[i].data.guideId);	
		}
		
		Ext.Msg.confirm('<fmt:message key="ajax.confirm"/>', '<fmt:message key="enable.confirm"/>', function(e){
			if(e == 'yes'){
				Ext.Ajax.request({
		        	url: '<c:url value="/noviceGuide/changeStatus.json"/>',
		         	method: 'post',
					scope: this,
					params:{idListGridJson: Ext.JSON.encode(rowsData), status: 1},
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
    
    disable : function(){
    	var rowsData = [];		
		var records = this.gsm.getSelection();
		if(records.length == 0){
			showMsg('<fmt:message key="ajax.confirm"/>', '<fmt:message key="ajax.no.record"/>');
			return;
		}	
		for(var i = 0; i < records.length; i++){			
			rowsData.push(records[i].data.guideId);	
		}
		
		Ext.Msg.confirm('<fmt:message key="ajax.confirm"/>', '<fmt:message key="disabled.confirm"/>', function(e){
			if(e == 'yes'){
				Ext.Ajax.request({
		        	url: '<c:url value="/noviceGuide/changeStatus.json"/>',
		         	method: 'post',
					scope: this,
					params:{idListGridJson: Ext.JSON.encode(rowsData), status: 0},
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
    
    changeDefault : function(){
    	var rowsData = [];		
		var records = this.gsm.getSelection();
		if(records.length == 0){
			showMsg('<fmt:message key="ajax.confirm"/>', '<fmt:message key="ajax.no.record"/>');
			return;
		}	
		
		for(var i = 0; i < records.length; i++){			
			rowsData.push(records[i].data.id);	
		} 
	    var phoneType=records[0].data.phoneType;
		Ext.Msg.confirm('<fmt:message key="ajax.confirm"/>', '<fmt:message key="submit.confirm"/>', function(e){
			if(e == 'yes'){
				Ext.Ajax.request({
		        	url: '<c:url value="/startImage/changeDefault.json"/>',
		         	method: 'post',
					scope: this,
					params:{idListGridJson: Ext.JSON.encode(rowsData), isDefault: 1,phoneType: phoneType},
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
    
    deleteNoviceGuide : function(){
    	var rowsData = [];		
		var records = this.gsm.getSelection();
		if(records.length == 0){
			showMsg('<fmt:message key="ajax.confirm"/>', '<fmt:message key="ajax.no.record"/>');
			return;
		}	
		for(var i = 0; i < records.length; i++){			
			rowsData.push(records[i].data.guideId);	
		    
		}
		Ext.Msg.confirm('<fmt:message key="ajax.confirm"/>', '<fmt:message key="delete.confirm"/>', function(e){
			if(e == 'yes'){
				Ext.Ajax.request({
		        	url: '<c:url value="/noviceGuide/delete.json"/>',
		         	method: 'post',
					scope: this,
					params:{idListGridJson: Ext.JSON.encode(rowsData)},
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

     addNoviceGuide : function(){
		var noviceGuideFormPanel = Ext.create('MyExt.systemManager.NoviceGuideFormPanel', {id: 'noviceGuideFormPanel@NoviceGuideFormPanel',store: this.store, title: '<fmt:message key="button.add"/>'});
		openWin('<fmt:message key="button.add"/>', noviceGuideFormPanel, 400, 500);
	}
});