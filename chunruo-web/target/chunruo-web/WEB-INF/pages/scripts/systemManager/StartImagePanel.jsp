<%@ include file="/WEB-INF/pages/scripts/taglibs.jsp"%>

Ext.define('Model', {
	extend: 'Ext.data.Model',
    fields: [
    	{name: 'id',				mapping: 'id',				type: 'int'},
		{name: 'productId',			mapping: 'productId',		type: 'int'},
		{name: 'productName',		mapping: 'productName',		type: 'string'},
		{name: 'imagePath',	 		mapping: 'imagePath',		type: 'string'},
		{name: 'isInvitePage',		mapping: 'isInvitePage',    type: 'bool'},
		{name: 'phoneType',	 		mapping: 'phoneType',		type: 'int'},
		{name: 'width',	 			mapping: 'width',			type: 'int'},
		{name: 'height',	 		mapping: 'height',			type: 'int'},
		{name: 'beginTime',	 	    mapping: 'beginTime',		type: 'string'},
		{name: 'endTime',	 	    mapping: 'endTime',		    type: 'string'},
		{name: 'status',	 		mapping: 'status',			type: 'int'},
		{name: 'isDefault',	 		mapping: 'isDefault',		type: 'int'},
		{name: 'updateTime',	 	mapping: 'updateTime',		type: 'string'},
		{name: 'createTime',	 	mapping: 'createTime',		type: 'string'}
    ]
});

Ext.define('MyExt.systemManager.StartImagePanel', {
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
			model: 'Model',
			proxy: {
				type: 'ajax',
				url: '<c:url value="/startImage/getStartImageListByTemplateId.json"/>',
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
			{
            	text: '<fmt:message key="start.image"/>',
                menuDisabled: true,
                sortable: false,
                xtype: 'actioncolumn',
                align: 'center',
                width: 80,
                higth: 80,
                locked: true,
                dataIndex: 'imagePath',
                renderer: function(value, metadata, record) {
        			return Ext.String.format('<img height="60" width="60" src="{0}"></img>', value);
    			}
            },
            {text: '<fmt:message key="start.image.id"/>', dataIndex: 'id', width: 70, sortable : false,
        		filter: {xtype: 'textfield'},
        	},
        	{text: '<fmt:message key="product.wholesale.imagePath"/>', dataIndex: 'imagePath', width: 300, sortable : true,
        		filter: {xtype: 'textfield'},
        	},
        	{text: '<fmt:message key="start.image.phoneType"/>', dataIndex: 'phoneType',  width: 80, sortable : true,
        		renderer: this.rendererStatus, 
        		filter: {xtype: 'textfield'},
        
        	},
        	{text: '<fmt:message key="start.image.height"/>', dataIndex: 'height',  width: 80, sortable : true,
        		filter: {xtype: 'textfield'},
        	},
        	{text: '<fmt:message key="start.image.width"/>', dataIndex: 'width', width: 80, sortable : true,
        		filter: {xtype: 'textfield'},
        	},
        	{text: '<fmt:message key="start.image.isDefault"/>', dataIndex: 'isDefault', width: 80, sortable : true,
				filter: {xtype: 'textfield'},
					renderer : function(value){
						if(value == 0){
							return '<span style="color:red;"><b><fmt:message key='button.no'/></b></span>';
						}else if(value == 1){
							return '<span style="color:blue;"><b><fmt:message key='button.yes'/></b></span>';
						}
				}
        	},
        	{text: '<fmt:message key="sellers.status"/>', dataIndex: 'status', width: 80,  sortable : true,
				filter: {xtype: 'textfield'},
					renderer : function(value){
						if(value == 0){
							return '<span style="color:red;"><b><fmt:message key='sellers.status_0'/></b></span>';
						}else if(value == 1){
							return '<span style="color:blue;"><b><fmt:message key='sellers.status_1'/></b></span>';
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
	    <jkd:haveAuthorize access="/startImage/saveOrUpdateStartImage.json">
	    {
        	text: '<fmt:message key="button.add"/>', 
        	iconCls: 'add', 	
        	handler: this.addStartImage, 
        	scope: this
        }
        <c:set var="isHaveAuthorize" value="true" />
		</jkd:haveAuthorize>
		<jkd:haveAuthorize access="/startImage/delete.json">
		<c:if test="${isHaveAuthorize}">,</c:if>
        '-',{
        	text: '<fmt:message key="button.delete"/>', 
        	iconCls: 'delete', 	
        	handler:  this.deleteStartImage, 
        	scope: this
        }
        <c:set var="isHaveAuthorize" value="true" />
		</jkd:haveAuthorize>
		<jkd:haveAuthorize access="/startImage/changeStatus.jsonn">
		<c:if test="${isHaveAuthorize}">,</c:if>
        '-',{
        	text: '<fmt:message key="button.enable"/>', 
        	iconCls: 'enable', 	
        	handler: this.enable, 
        	scope: this
        }
        <c:set var="isHaveAuthorize" value="true" />
		</jkd:haveAuthorize>
		<jkd:haveAuthorize access="/userSys/editUser.json,/userSys/saveAdminUser.json">
		<c:if test="${isHaveAuthorize}">,</c:if>
        '-',{
        	text: '<fmt:message key="button.disable"/>', 
        	iconCls: 'Cancel', 	
        	handler: this.disable, 
        	scope: this
        }
        <c:set var="isHaveAuthorize" value="true" />
		</jkd:haveAuthorize>
		<jkd:haveAuthorize access="/startImage/changeStatus.json">
		<c:if test="${isHaveAuthorize}">,</c:if>
        '-',{
        	text: '<fmt:message key="button.update.default"/>', 
        	iconCls: 'delete', 	
        	handler: this.changeDefault, 
        	scope: this
        }
        </jkd:haveAuthorize>
        ];
	    
	    this.east =  Ext.create('MyExt.systemManager.StartImageTabPanel', {
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
	    
	    <jkd:haveAuthorize access="/startImage/getStartImageListByTemplateId.json">
	   	this.store.on('beforeload', function(store, options) {
	        Ext.apply(store.proxy.extraParams, {
	        	filters: Ext.JSON.encode(this.list.filters),
	        	templateId: this.record.data.templateId
			});
	    }, this);
	    </jkd:haveAuthorize>
	    
	    this.list.on('itemdblclick', function(view, record, item, index, e, eOpts) {
	    	<jkd:haveAuthorize access="/startImage/getStartImageById.json">
	    	this.east.transferData(this.east, record, this.body.dom.clientWidth);
	    	this.east.show();
	    	</jkd:haveAuthorize>
	    }, this);
    },
    
    transferData : function(record){
    	this.record = record;
    	console.log(this.record.data.templateId)
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
			rowsData.push(records[i].data.id);	
		}
		
		Ext.Msg.confirm('<fmt:message key="ajax.confirm"/>', '<fmt:message key="enable.confirm"/>', function(e){
			if(e == 'yes'){
				Ext.Ajax.request({
		        	url: '<c:url value="/startImage/changeStatus.json"/>',
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
			rowsData.push(records[i].data.id);	
		}
		
		Ext.Msg.confirm('<fmt:message key="ajax.confirm"/>', '<fmt:message key="disabled.confirm"/>', function(e){
			if(e == 'yes'){
				Ext.Ajax.request({
		        	url: '<c:url value="/startImage/changeStatus.json"/>',
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
    
    deleteStartImage : function(){
    	var rowsData = [];		
		var records = this.gsm.getSelection();
		var isDefault = [];
		var phoneType='';
		var iosCount=0;
		var andCount=0;
		if(records.length == 0){
			showMsg('<fmt:message key="ajax.confirm"/>', '<fmt:message key="ajax.no.record"/>');
			return;
		}	
		
		for(var i = 0; i < records.length; i++){			
			rowsData.push(records[i].data.id);	
		    isDefault.push(records[i].data.isDefault);
		    if(records[i].data.isDefault==1 && records[i].data.phoneType==0){
		        phoneType=0;
		    }else if(records[i].data.isDefault==1 && records[i].data.phoneType==1){
		        phoneType=1;
		    }
		    if(records[i].data.phoneType==1){
		          iosCount++;
		    }else if(records[i].data.phoneType==0){
		          andCount++;
		    }
		}
		
		Ext.Msg.confirm('<fmt:message key="ajax.confirm"/>', '<fmt:message key="delete.confirm"/>', function(e){
			if(e == 'yes'){
				Ext.Ajax.request({
		        	url: '<c:url value="/startImage/delete.json"/>',
		         	method: 'post',
					scope: this,
					params:{idListGridJson: Ext.JSON.encode(rowsData), status: 0,isDefaultGridJson: Ext.JSON.encode(isDefault),phoneType: phoneType,iosCount: iosCount,andCount:andCount},
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

     addStartImage : function(){
		var startImageAddFormPanel = Ext.create('MyExt.systemManager.StartImageFormPanel', {id: 'startImageFormPanel@StartImageFormPanel',store: this.store,templateId: this.record.data.templateId, title: '<fmt:message key="button.add"/>'});
		openWin('<fmt:message key="button.add"/>', startImageAddFormPanel, 400, 500);
	}
});