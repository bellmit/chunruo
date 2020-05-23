<%@ include file="/WEB-INF/pages/scripts/taglibs.jsp"%>

Ext.define('Sign', {
	extend: 'Ext.data.Model',
	idProperty: 'signId',
     fields: [
    	{name: 'signId',	    mapping: 'signId',	    type: 'int'},
		{name: 'userId',	 	mapping: 'userId',		    type: 'int'},
		{name: 'mobile',	    mapping: 'mobile',	        type: 'string'},
		{name: 'signIntegral',	mapping: 'signIntegral',      type: 'int'},
		{name: 'nickName',	    mapping: 'nickName',        type: 'string'},
		{name: 'createTime',	mapping: 'createTime',	    type: 'string'},
		{name: 'updateTime',	mapping: 'updateTime',	    type: 'string'},
    ]
});

Ext.define('MyExt.userManager.SignListPanel', {
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
			model: 'Sign',
			proxy: {
				type: 'ajax',
				url: '<c:url value="/sign/signList.json"/>',
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
			{text: '<fmt:message key="sign.signId"/>', dataIndex: 'signId', width: 140, sortable : true,
        		filter: {xtype: 'textfield'},
        		renderer: this.fontRenderer
        	},
        	{text: '<fmt:message key="sign.userId"/>', dataIndex: 'userId', width: 140, sortable : true,
        		filter: {xtype: 'textfield'},
        		renderer: this.fontRenderer
        	},
        	{text: '<fmt:message key="sign.mobile"/>', dataIndex: 'mobile', width: 140, sortable : true,
        		filter: {xtype: 'textfield'},
        		renderer: this.fontRenderer
        	},
        	{text: '<fmt:message key="sign.signIntegral"/>', dataIndex: 'signIntegral', width: 140, sortable : true,
        		filter: {xtype: 'textfield'},
        		renderer: this.fontRenderer
        	},
        	{text: '<fmt:message key="sign.user.nickName"/>', dataIndex: 'nickName', width: 140, sortable : true,
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
	   <jkd:haveAuthorize access="/sign/signList.json">
	   {
	    	text: '<fmt:message key="button.refresh"/>', 
	        iconCls: 'refresh', 	
	       	handler: function(){this.store.reload();}, 
	       	scope: this
	  	}
	  	<c:set var="isHaveAuthorize" value="true" />
		</jkd:haveAuthorize>
		<jkd:haveAuthorize access="/sign/editSignIntegral.json">
		<c:if test="${isHaveAuthorize}">,</c:if>
	  	'-',{
        	text: '<fmt:message key="sign.signIntegral.edit"/>', 
        	iconCls: 'enable', 	
        	handler: this.edit,
        	scope: this
        }
        <c:set var="isHaveAuthorize" value="true" />
		</jkd:haveAuthorize>
		<jkd:haveAuthorize access="/sign/exportSignRecord.json">
		<c:if test="${isHaveAuthorize}">,</c:if>
        '->',{
        	text: '<fmt:message key="sign.record.xls"/>', 
        	iconCls: 'excel', 	
        	handler: this.export,
        	scope: this
        }
        </jkd:haveAuthorize>
        ];

    	this.items = [this.productWarehouseList];	
    	this.callParent(arguments);
    	
    	this.gsm = this.productWarehouseList.getSelectionModel();
    	<jkd:haveAuthorize access="/sign/signList.json">
    	this.store.on('beforeload', function(store, options) {
	        Ext.apply(store.proxy.extraParams, {
	        	filters: Ext.JSON.encode(this.productWarehouseList.filters)
			});
	    }, this);
	    this.store.load();
	    </jkd:haveAuthorize>
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
	
	export:function(){	
     	Ext.MessageBox.confirm('<fmt:message key="ajax.confirm"/>', '<fmt:message key="order.export.confirm"/>', function(e){
			if(e == 'yes'){
				window.location.href = "/sign/exportSignRecord.json";
              	popWin.close();
        	}
        }, this)
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
    
    edit: function(){
   		var rowsData = [];
		var records = this.gsm.getSelection();
		if(records.length == 0 ){
			showMsg('<fmt:message key="ajax.confirm"/>', '<fmt:message key="ajax.no.record"/>');
			return;
		}else if(records.length > 1 ){
			showMsg('<fmt:message key="ajax.confirm"/>', '<fmt:message key="user.approve.records"/>');
			return;
		}
	
    	var signFormPanel = Ext.create('MyExt.userManager.SignFormPanel', {
			id: 'signFormPanel@' + this.id,
    		viewer: this.viewer,	
    		signId: records[0].data.signId,
    		signIntegral:records[0].data.signIntegral,
   	 	});
    	
    	var buttons = [{
			text: '<fmt:message key="button.save"/>',
			handler: function(){	
			    if(signFormPanel.form.isValid()){
					Ext.Msg.confirm('<fmt:message key="ajax.confirm"/>', '<fmt:message key="save.confirm"/>', function(e){
						if(e == 'yes'){
					     	signFormPanel.form.submit({
		                 		waitMsg: 'Loading...',
		                 		url: '<c:url value="/sign/editSignIntegral.json"/>',
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
      	openWin(Ext.String.format('<fmt:message key="team.task.user.manager"/>'), signFormPanel, buttons, 300, 120);
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
    		recordId: records[0].data.recordId,	
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