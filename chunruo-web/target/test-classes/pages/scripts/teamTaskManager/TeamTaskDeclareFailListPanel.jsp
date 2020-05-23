<%@ include file="/WEB-INF/pages/scripts/taglibs.jsp"%>

Ext.define('TeamTaskRecord', {
	extend: 'Ext.data.Model',
	idProperty: 'recordId',
     fields: [
    	{name: 'recordId',	    mapping: 'recordId',	    type: 'int'},
		{name: 'userId',	 	mapping: 'userId',		    type: 'int'},
		{name: 'level',	        mapping: 'level',	        type: 'int'},
		{name: 'taskStatus',	mapping: 'taskStatus',      type: 'int'},
		{name: 'mobile',	    mapping: 'mobile',          type: 'string'},
		{name: 'nickName',	    mapping: 'nickName',        type: 'string'},
		{name: 'inviteNumber',	mapping: 'inviteNumber',	type: 'int'},
		{name: 'isSignContract',mapping: 'isSignContract',	type: 'bool'},
		{name: 'remarks',	    mapping: 'remarks',         type: 'string'},
		{name: 'examineTime',	mapping: 'examineTime',	    type: 'string'},
		{name: 'createTime',	mapping: 'createTime',	    type: 'string'},
		{name: 'updateTime',	mapping: 'updateTime',	    type: 'string'},
    ]
});

Ext.define('MyExt.teamTaskManager.TeamTaskDeclareFailListPanel', {
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
			model: 'TeamTaskRecord',
			proxy: {
				type: 'ajax',
				url: '<c:url value="/teamTask/list.json?level=2&taskStatus=3"/>',
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
		
		this.taskStatusStore= Ext.create('Ext.data.Store', {
        	autoDestroy: true,
        	model: 'InitModel',
        	data: [
        	    {id: '0', name: '<fmt:message key="team.task.record.status0"/>'},
        		{id: '2', name: '<fmt:message key="team.task.record.status2"/>'},
        		{id: '3', name: '<fmt:message key="team.task.record.status3"/>'},
        		{id: '4', name: '<fmt:message key="team.task.record.status4"/>'}
        		
        	]
        });
		
		this.columns = [
			{text: '<fmt:message key="product.intro.introId"/>', dataIndex: 'recordId', width: 140, sortable : true,
        		filter: {xtype: 'textfield'},
        		renderer: this.fontRenderer
        	},
        	{text: '<fmt:message key="team.task.record.userId"/>', dataIndex: 'userId', width: 140, sortable : true,
        		filter: {xtype: 'textfield'},
        		renderer: this.fontRenderer
        	},
        	{text: '<fmt:message key="team.task.record.mobile"/>', dataIndex: 'mobile', width: 140, sortable : true,
        		filter: {xtype: 'textfield'},
        		renderer: this.fontRenderer
        	},
        	{text: '<fmt:message key="team.task.record.nickName"/>', dataIndex: 'nickName', width: 140, sortable : true,
        		filter: {xtype: 'textfield'},
        		renderer: this.fontRenderer
        	},
        	{text: '<fmt:message key="team.task.record.inviteNumber"/>', dataIndex: 'inviteNumber', width: 140, sortable : true,
        		filter: {xtype: 'textfield'},
        		renderer: this.fontRenderer
        	},
        	{text: '<fmt:message key="team.task.record.taskStatus"/>', dataIndex: 'taskStatus', width: 140, sortable : true,
        		renderer: this.rendererTaskStatusType,
        	    filter: {
					xtype: 'combobox',
			        displayField: 'name',
			        valueField: 'id',
			        store: this.taskStatusStore,
			        queryMode: 'local',
			        typeAhead: true
				}
        	},
        	{text: '<fmt:message key="team.task.record.remarks"/>', dataIndex: 'remarks', width: 500, sortable : true,
        		filter: {xtype: 'textfield'},
        		renderer: this.fontRenderer
        	},
        	{text: '<fmt:message key="team.task.record.examineTime"/>', dataIndex: 'examineTime', width: 140, sortable : true,
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
	    
	   this.tbar = [{
	    	text: '<fmt:message key="button.refresh"/>', 
	        iconCls: 'refresh', 	
	       	handler: function(){this.store.reload();}, 
	       	scope: this
	  	},'-',{
        	text: '<fmt:message key="team.task.record.agree"/>', 
        	iconCls: 'enable', 	
        	handler: this.promotion,
        	scope: this
        }];

    	this.items = [this.productWarehouseList];	
    	this.callParent(arguments);
    	
    	this.store.on('beforeload', function(store, options) {
	        Ext.apply(store.proxy.extraParams, {
	        	filters: Ext.JSON.encode(this.productWarehouseList.filters)
			});
	    }, this);
	    this.store.load();
	    
	    this.gsm = this.productWarehouseList.getSelectionModel();
	    this.productWarehouseList.on('itemdblclick', function(view, record, item, index, e, eOpts) {
	    	
	    
	    }, this);
    },
    
    cleanSearch : function(){
    	this.keywordField.setRawValue();
		this.store.loadPage(1);
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
    
    promotion: function(){
   		var rowsData = [];
		var records = this.gsm.getSelection();
		if(records.length == 0 ){
			showMsg('<fmt:message key="ajax.confirm"/>', '<fmt:message key="ajax.no.record"/>');
			return;
		}else if(records.length > 1 ){
			showMsg('<fmt:message key="ajax.confirm"/>', '<fmt:message key="user.approve.records"/>');
			return;
		}
	
    	var agreeFormPanel = Ext.create('MyExt.teamTaskManager.AgreeFormPanel', {
			id: 'agreeFormPanel@' + this.id,
    		viewer: this.viewer,	
    		recordId: records[0].data.recordId,
    		isAgree:true
   	 	});
    	
    	var buttons = [{
			text: '<fmt:message key="button.save"/>',
			handler: function(){	
			    if(agreeFormPanel.form.isValid()){
					Ext.Msg.confirm('<fmt:message key="ajax.confirm"/>', '<fmt:message key="save.confirm"/>', function(e){
						if(e == 'yes'){
					     	agreeFormPanel.form.submit({
		                 		waitMsg: 'Loading...',
		                 		url: '<c:url value="/teamTask/promotion.json"/>',
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
      	openWin(Ext.String.format('<fmt:message key="team.task.user.manager"/>'), agreeFormPanel, buttons, 300, 120);
    },
    
    unpromotion: function(){
   		var rowsData = [];
		var records = this.gsm.getSelection();
		if(records.length == 0 ){
			showMsg('<fmt:message key="ajax.confirm"/>', '<fmt:message key="ajax.no.record"/>');
			return;
		}else if(records.length > 1 ){
			showMsg('<fmt:message key="ajax.confirm"/>', '<fmt:message key="user.approve.records"/>');
			return;
		}
	
    	var agreeFormPanel = Ext.create('MyExt.teamTaskManager.AgreeFormPanel', {
			id: 'agreeFormPanel@' + this.id,
    		viewer: this.viewer,	
    		isAgree:false
   	 	});
    	
    	var buttons = [{
			text: '<fmt:message key="button.save"/>',
			handler: function(){	
			    if(agreeFormPanel.form.isValid()){
					Ext.Msg.confirm('<fmt:message key="ajax.confirm"/>', '<fmt:message key="save.confirm"/>', function(e){
						if(e == 'yes'){
					     	agreeFormPanel.form.submit({
		                 		waitMsg: 'Loading...',
		                 		url: '<c:url value="/teamTask/promotion.json"/>',
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
      	openWin(Ext.String.format('<fmt:message key="team.task.record.refuse"/>'), agreeFormPanel, buttons, 400, 200);
    },
   
   	rendererProductTypeStuts : function(val){
		if(val == 1) {
            return '<b><fmt:message key="product.wareHouse.productType1"/></b>';
        }else if(val == 2){
            return '<fmt:message key="product.wareHouse.productType2"/>';
        }else {
           return '<fmt:message key="product.wareHouse.productType3"/>';
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
	
	rendererTaskStatusType : function(val){
	    if(val == 0) {
            return '<b><fmt:message key="team.task.record.status0"/></b>';
        }else if(val == 1) {
            return '<b><fmt:message key="team.task.record.status1"/></b>';
        }else if(val == 2) {
            return '<b><fmt:message key="team.task.record.status2"/></b>';
        }else if(val == 3) {
            return '<b><fmt:message key="team.task.record.status3"/></b>';
        }else{
            return '<fmt:message key="team.task.record.status4"/>';
        }
	},
});