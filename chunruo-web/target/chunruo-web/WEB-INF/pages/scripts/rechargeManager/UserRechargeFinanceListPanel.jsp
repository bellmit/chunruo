<%@ include file="/WEB-INF/pages/scripts/taglibs.jsp"%>

Ext.define('UserRecharge', {
	extend: 'Ext.data.Model',
	idProperty: 'recordId',
    fields: [
		{name: 'recordId',	 		mapping: 'recordId',		type: 'int'},
		{name: 'applicant',	 	    mapping: 'applicant',	    type: 'string'},
		{name: 'amount',			mapping: 'amount',			type: 'string'},
		{name: 'userId',	 		mapping: 'userId',			type: 'string'},
		{name: 'status',			mapping: 'status',			type: 'int'},
		{name: 'profitNotice',	 	mapping: 'profitNotice',	type: 'string'},
		{name: 'refuseReason',	 	mapping: 'refuseReason',	type: 'string'},
		{name: 'adminName',	 	    mapping: 'adminName',	    type: 'string'},
		{name: 'nickName',	 	    mapping: 'nickName',	    type: 'string'},
		{name: 'reason',		    mapping: 'reason',	        type: 'string'},
		{name: 'attachmentPath',    mapping: 'attachmentPath',	type: 'string'},
		{name: 'completeTime',	    mapping: 'completeTime',    type: 'string'},
		{name: 'createTime',	    mapping: 'createTime',		type: 'string'},
		{name: 'updateTime',	    mapping: 'updateTime',		type: 'string'}
	]
});

Ext.define('MyExt.rechargeManager.UserRechargeFinanceListPanel', {
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
		
		var now = new Date();
   		var expiry = new Date(now.getTime() + 10 * 60 * 1000);
   		Ext.util.Cookies.set('isCheck','0',expiry);
   		
		
		this.store = Ext.create('Ext.data.Store', {
        	pageSize: 50,
	        autoLoad: false,
			autoDestroy: true,
			sortOnLoad: true,
			remoteSort: true,
			model: 'UserRecharge',
			proxy: {
				type: 'ajax',
				url: '<c:url value="/userRecharge/list.json?status=2"/>',
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
      
		this.refundTypeStore = Ext.create('Ext.data.Store', {
        	autoDestroy: true,
        	model: 'InitModel',
        	data: [
        		{id: 1, name: '<fmt:message key="order.refund.refundType.1"/>'},
        		{id: 2, name: '<fmt:message key="order.refund.refundType.2"/>'}
        	]
        });
       
        
        	this.statusStore = Ext.create('Ext.data.Store', {
        	autoDestroy: true,
        	model: 'InitModel',
        	data: [
        		{id: 1, name: '<fmt:message key="user.recharge.status1"/>'},
        		{id: 2, name: '<fmt:message key="user.recharge.status2"/>'},
        		{id: 3, name: '<fmt:message key="user.recharge.status3"/>'},
        		{id: 4, name: '<fmt:message key="user.recharge.status4"/>'}
        	]
        });
        
		this.columns = [
			{text: '<fmt:message key="user.recharge.recordId"/>', dataIndex: 'recordId', width: 60, locked: true, sortable : true,
        		align: 'center',filter: {xtype: 'textfield'}
        	},
        	{text: '<fmt:message key="user.recharge.status"/>', dataIndex: 'status', width: 100,  sortable : true,locked: true,
        		align: 'center',
        		renderer: this.rendererStatus,
        		filter: {
					xtype: 'combobox',
			        displayField: 'name',
			        valueField: 'id',
			        store: this.statusStore,
			        queryMode: 'local',
			        typeAhead: true
				}
        	},
        	{text: '<fmt:message key="user.recharge.userId"/>', dataIndex: 'userId', width:60,  sortable : true,locked: true,
        		align: 'center',filter: {xtype: 'textfield'}
        	},
        	{text: '<fmt:message key="user.recharge.mobile"/>', dataIndex: 'mobile', width: 100,  sortable : true,locked: true,
        		align: 'center',filter: {xtype: 'textfield'}
        	},
        	{text: '<fmt:message key="user.recharge.amount"/>', dataIndex: 'amount', width: 80,  sortable : true,locked: true,
        		align: 'center',filter: {xtype: 'textfield'}
        	},
        	{text: '<fmt:message key="user.recharge.nickName"/>', dataIndex: 'nickName', width: 100,  sortable : true,locked: true,
        		align: 'center',filter: {xtype: 'textfield'}
        	},
        	{text: '<fmt:message key="user.recharge.applicant"/>', dataIndex: 'applicant', width: 100,  sortable : true,locked: true,
        		align: 'center',filter: {xtype: 'textfield'}
        	},
        	{text: '<fmt:message key="user.recharge.profitNotice"/>', dataIndex: 'profitNotice', width: 160,  sortable : true,locked: true,
        		align: 'center',filter: {xtype: 'textfield'}
        	},
        	{text: '<fmt:message key="user.recharge.reason"/>', dataIndex: 'reason', width: 160,  sortable : true,locked: true,
        		align: 'center',filter: {xtype: 'textfield'}
        	},
        	{text: '<fmt:message key="user.recharge.refuseReason"/>', dataIndex: 'refuseReason', width: 100,  sortable : true,locked: true,
        		align: 'center',filter: {xtype: 'textfield'}
        	},
        	{text: '<fmt:message key="user.recharge.adminName"/>', dataIndex: 'adminName', width: 100,  sortable : true,locked: true,
        		align: 'center',filter: {xtype: 'textfield'}
        	},
        	{text: '<fmt:message key="user.recharge.completeTime"/>', dataIndex: 'completeTime', width: 160, sortable : true,locked: true,
        		align: 'center',filter: {xtype: 'textfield'}
        	},
        	{text: '<fmt:message key="user.recharge.createTime"/>', dataIndex: 'createTime', width: 160, sortable : true,locked: true,
        		align: 'center',filter: {xtype: 'textfield'}
        	},
        	{text: '<fmt:message key="user.recharge.updateTime"/>', dataIndex: 'updateTime', width: 160,  sortable : true,locked: true,
        		align: 'center',filter: {xtype: 'textfield'}
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
		this.refundAllListBbar = this.pagingToolbar; 
		
    	this.refundAllList = Ext.create('Ext.grid.GridPanel', {
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
		    bbar: this.refundAllListBbar,
        	plugins: ['gridHeaderFilters'],
		    viewConfig: {
		        stripeRows: true,
		        enableTextSelection: true
		    }
	    });
	    
	    this.tbar = [
	    <jkd:haveAuthorize access="/userRecharge/updateStatusByManager.json">
	    {
        	text: '<fmt:message key="user.recharge.agree"/>', 
        	iconCls: 'enable', 	
        	handler: this.agree, 
        	scope: this
        }
        <c:set var="isHaveAuthorize" value="true" />
		</jkd:haveAuthorize>
		<jkd:haveAuthorize access="/userRecharge/updateStatusByManager.json">
		<c:if test="${isHaveAuthorize}">,</c:if>
        {
        	text: '<fmt:message key="user.recharge.refuse"/>', 
        	iconCls: 'Cancel', 	
        	handler: this.refuse, 
        	scope: this
        }
        </jkd:haveAuthorize>
        ];
        
        this.east =  Ext.create('MyExt.rechargeManager.UserRechargeTabPanel', {
        	refundList: this.refundAllList,
		 	viewer: this.viewer,
		 	layout: 'card',
	        border: false,
	       	region: 'east',
	        width: 550,
	        split: true,
	        header: false,
	        hidden: true
        });
	    
    	this.items = [this.refundAllList, this.east];	
		this.east.hide();
    	this.callParent(arguments);
    	
    	<jkd:haveAuthorize access="/userRecharge/list.json">
    	this.store.on('beforeload', function(store, options) {
	        Ext.apply(store.proxy.extraParams, {
	        	filters: Ext.JSON.encode(this.refundAllList.filters)
			});
	    }, this);
	    this.store.load();
	    </jkd:haveAuthorize>
	    
	    this.gsm = this.refundAllList.getSelectionModel();
	    this.refundAllList.on('itemdblclick', function(view, record, item, index, e, eOpts) {
	    	<jkd:haveAuthorize access="/userRecharge/getUserRechargeById.json">
	    	this.east.transferData(this.east, record, this.body.dom.clientWidth);
	    	this.east.show();
	    	</jkd:haveAuthorize>
	    }, this);
    },
    
    cleanSearch : function(){
    	this.keywordField.setRawValue();
		this.store.loadPage(1);
    },
    
    refuse : function(){
		var rowsData = [];		
		var records = this.gsm.getSelection();
		if(records.length == 0){
			showMsg('<fmt:message key="ajax.confirm"/>', '<fmt:message key="ajax.no.record"/>');
			return;
		}	
		for(var i = 0; i < records.length; i++){			
			rowsData.push(records[i].data.recordId);	
		}
		
		var textAreaMsg = Ext.create('Ext.form.TextArea', {
 			fieldLabel: '<fmt:message key="user.recharge.refuseReason"/>',
 			labelAlign: 'top',
    		allowBlank: false,
       		anchor: '100%'
 		});	
	 	
	 	var buttons = [{ 	
			text: '<fmt:message key="button.save"/>', 
			scope: this,  
	        handler: function(){
				if(textAreaMsg.getValue() == null || textAreaMsg.getValue().length == 0){ 
					showWarnMsg('<fmt:message key="user.recharge.refuseReason"/>', 8);
					return;
				}
				
		     	Ext.Msg.confirm('<fmt:message key="ajax.confirm"/>', '<fmt:message key="save.confirm"/>', function(e){
					if(e == 'yes'){
				     	Ext.Ajax.request({
				        	url: '<c:url value="/userRecharge/updateStatusByManager.json"/>',
				         	method: 'post',
							scope: this,
							params:{idListGridJson: Ext.JSON.encode(rowsData), level: 3,status:4,refuseReason: textAreaMsg.getValue()},
				          	success: function(response){
		          				var responseObject = Ext.JSON.decode(response.responseText);
		          				if(responseObject.error == false){
		                        	popFormWin.close();
		                       		showSuccMsg(responseObject.message);
		                        	this.store.loadPage(1);
		                    		this.gsm.deselectAll();
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
 		openFormWin('<fmt:message key="ajax.waitTitle"/>', [textAreaMsg], buttons, 300, 170);
    },
    
    agree : function() {
		var rowsData = [];		
		var records = this.gsm.getSelection();
		if(records.length == 0){
			showMsg('<fmt:message key="ajax.confirm"/>', '<fmt:message key="ajax.no.record"/>');
			return;
		}	
		for(var i = 0; i < records.length; i++){			
			rowsData.push(records[i].data.recordId);	
		}
		
		Ext.Msg.confirm('<fmt:message key="ajax.confirm"/>', '<fmt:message key="save.confirm"/>', function(e){
			if(e == 'yes'){
				Ext.Ajax.request({
		        	url: '<c:url value="/userRecharge/updateStatusByManager.json"/>',
		         	method: 'post',
					scope: this,
					params:{idListGridJson: Ext.JSON.encode(rowsData), level: 3, status:3},
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
    
	rendererStatus : function(val){
		var str =  "";
		if(val == 1){
			str = '<b><fmt:message key="user.recharge.status1"/></b>';
		}else if(val == 2 ){
			str = '<b><fmt:message key="user.recharge.status2"/></b>';
		}else if(val == 3){
			str = '<b><fmt:message key="user.recharge.status3"/></b>';
		}else if(val == 4){
			str = '<b><fmt:message key="user.recharge.status4"/></b>';
		}
		return str;
	},
	
	rendererReceipt : function(val){
		var str =  "";
		if(val){
			str = '<b><fmt:message key="order.refund.receipt"/></b>';
		}
		return str;
	},
	
	rendererRefundType : function(val){
		var str =  "";
		if(val == '1'){
			str = '<b><fmt:message key="order.refund.refundType.1"/></b>';
		}else{
			str = '<b><fmt:message key="order.refund.refundType.2"/></b>';
		}
		return str;
	}
});