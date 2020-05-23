<%@ include file="/WEB-INF/pages/scripts/taglibs.jsp"%>

Ext.define('UserVipRecord', {
	extend: 'Ext.data.Model',
	idProperty: 'recordId',
    fields: [
		{name: 'recordId',	 			mapping: 'recordId',	    type: 'int'},
    	{name: 'recordNo',	 			mapping: 'recordNo',	    type: 'string'},
    	{name: 'mobile',	 			mapping: 'mobile',	        type: 'string'},
    	{name: 'orderNo',	 			mapping: 'orderNo',		    type: 'string'},
		{name: 'userId',	 			mapping: 'userId',		    type: 'int'},
		{name: 'topUserId',	 		    mapping: 'topUserId',	    type: 'int'},
		{name: 'recordType',	 		mapping: 'recordType',	    type: 'string'},
		{name: 'vipType',	 	        mapping: 'vipType',		    type: 'int'},
		{name: 'isPaymentSucc',	 	    mapping: 'isPaymentSucc',   type: 'string'},
		{name: 'costAmount',	 		mapping: 'costAmount',		type: 'string'},
		{name: 'profitAmount',	 		mapping: 'profitAmount',	type: 'string'},
		{name: 'tradeNo',	 		    mapping: 'tradeNo',			type: 'string'},
		{name: 'paymentType',	 		mapping: 'paymentType',		type: 'string'},
		{name: 'startDate',	 			mapping: 'startDate',		type: 'string'},
		{name: 'endDate',	 		    mapping: 'endDate',			type: 'string'},
		{name: 'createTime',	 		mapping: 'createTime',		type: 'string',	
			convert:function(value){ 
				if(value) {
					var createTime = Ext.Date.format(new Date(value),"Y-m-d H:i:s");
					return createTime; 
				}   
			}
		},
		{name: 'updateTime',	 		mapping: 'updateTime',		type: 'string',	
			convert:function(value){ 
				if(value) {
					var createTime = Ext.Date.format(new Date(value),"Y-m-d H:i:s");
					return createTime; 
				}   
			}
		}
    ]
});

Ext.define('MyExt.userManager.VipRecordPanel', {
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
			model: 'UserVipRecord',
			proxy: {
				type: 'ajax',
				url: '<c:url value="/user/vipRecordList.json"/>',
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
		
		this.rendererStutsStore = Ext.create('Ext.data.Store', {
        	autoDestroy: true,
        	model: 'InitModel',
        	data: [
        	    {id: 3, name: '<fmt:message key="user.level1"/>'},
        		{id: 2, name: '<fmt:message key="user.level3"/>'},
        		{id: 1, name: '<fmt:message key="user.level2"/>'}
        	]
        });
        
        this.rendererlevelStore = Ext.create('Ext.data.Store', {
        	autoDestroy: true,
        	model: 'InitModel',
        	data: [
        		{id: 3, name: '<fmt:message key="user.level3"/>'},
        		{id: 2, name: '<fmt:message key="user.level2"/>'},
        		{id: 1, name: '<fmt:message key="user.level1"/>'},
        		{id: 0, name: '<fmt:message key="user.level0"/>'}
        	]
        });
        
        this.rendererPaymentSuccStore = Ext.create('Ext.data.Store', {
            autoDestroy: true,
        	model: 'InitModel',
        	data: [
        		{id: 1, name: '<fmt:message key="button.yes"/>'},
        		{id: 0, name: '<fmt:message key="button.no"/>'}
        	]
        });
        
        this.rendererRecordTypeStore= Ext.create('Ext.data.Store', {
            autoDestroy: true,
        	model: 'InitModel',
        	data: [
        		{id: 2, name: '<fmt:message key="user.recordType2"/>'},
        		{id: 1, name: '<fmt:message key="user.recordType1"/>'}
        	]
        });
        
        this.rendererPaymentTypeStore= Ext.create('Ext.data.Store', {
            autoDestroy: true,
        	model: 'InitModel',
        	data: [
        		{id: 1, name: '<fmt:message key="user.paymentType1"/>'},
        		{id: 0, name: '<fmt:message key="user.paymentType0"/>'}
        	]
        });
        
		this.columns = [
        	{text: '<fmt:message key="user.recordId"/>', dataIndex: 'recordId', width: 70, locked: true, sortable : true,
        		filter: {xtype: 'textfield'}
        	},
        	{text: '<fmt:message key="user.orderNo"/>', dataIndex: 'orderNo', width: 180, locked: true, sortable : true,
        		filter: {xtype: 'textfield'}
        	},
        	{text: '<fmt:message key="user.userId"/>', dataIndex: 'userId', width: 80, locked: true, sortable : true,
        		filter: {xtype: 'textfield'}
        	},
        	{text: '<fmt:message key="user.mobile"/>', dataIndex: 'mobile', width: 100, locked: true, sortable : true,
        		filter: {xtype: 'textfield'}
        	},
        	{text: '<fmt:message key="user.isPaymentSucc"/>', dataIndex: 'isPaymentSucc', width: 70, sortable : true,
        		 filter: {
					xtype: 'combobox',
			        displayField: 'name',
			        valueField: 'id',
			        store: this.rendererPaymentSuccStore,
			        queryMode: 'local',
			        typeAhead: true
				},
		        renderer : function(val, metadata, record, rowIndex, columnIndex, store){ 
					var str =  "";
					if(val == 'true'){
						str = '<fmt:message key="public.yes"/>';
					}else{
						str = '<fmt:message key="public.no"/>';
					}
					return str;
				}
        	},
        	{text: '<fmt:message key="user.costAmount"/>', dataIndex: 'costAmount', width: 140, sortable : true,
        		filter: {xtype: 'textfield'}
        	},
        	
        	{text: '<fmt:message key="user.tradeNo"/>', dataIndex: 'tradeNo', width: 60, sortable : true,
        		filter: {xtype: 'textfield'}
        	},
        	{text: '<fmt:message key="user.topUserId"/>', dataIndex: 'topUserId', width: 140, sortable : true,
        		filter: {xtype: 'textfield'}
        	},
        	{text: '<fmt:message key="user.recordNo"/>', dataIndex: 'recordNo', width: 220, sortable : true,
        		filter: {xtype: 'textfield'}
        	},
        	{text: '<fmt:message key="user.createTime"/>', dataIndex: 'createTime', width: 140, sortable : true,
        		filter: {xtype: 'textfield'}
        	},
        	{text: '<fmt:message key="user.updateTime"/>', dataIndex: 'updateTime', width: 140, sortable : true,
        		filter: {xtype: 'textfield'}
        	}
        ];
        
        this.keywordField = new Ext.create('Ext.form.TextField', {
			width: 200,
			emptyText:'<fmt:message key="app.user.search" />',
        	scope: this
        });
       
        	
     	this.pagingToolbar = new Ext.PagingToolbar({
        	pageSize: 50,
			store: this.store,
			autoScroll: true,   
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
		this.vipRecordListBbar = this.pagingToolbar; 
		
    	this.vipRecordList = Ext.create('Ext.grid.GridPanel', {
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
		    bbar: this.vipRecordListBbar,
        	plugins: ['gridHeaderFilters'],
		    viewConfig: {
		        stripeRows: true,
		        enableTextSelection: true
		    }
	    });
	    
    	this.items = [this.vipRecordList];
    	this.callParent(arguments);
    	
    	this.gsm = this.vipRecordList.getSelectionModel();
    	<jkd:haveAuthorize access="/user/vipRecordList.json">
    	this.store.on('beforeload', function(store, options) {
	        Ext.apply(store.proxy.extraParams, {
	        	filters: Ext.JSON.encode(this.vipRecordList.filters),
	        	keyword: this.keywordField.getRawValue()
			});
	    }, this);
	    this.store.load();
	    </jkd:haveAuthorize>
	},
    
    cleanSearch : function(){
    	this.keywordField.setRawValue();
		this.store.loadPage(1);
    },
    
    exportVip : function(){
		var userVipExportExcel = Ext.create('MyExt.userManager.UserVipExportExcel', {id: 'UserVipExportExcel@UserVipExportExcel', title: '<fmt:message key="button.add"/>'});
		var buttons = [{
			text: '<fmt:message key="user.exporter.vip.xls"/>',
			handler: function(){
	            if(userVipExportExcel.form.isValid()){
	            	Ext.MessageBox.confirm('<fmt:message key="ajax.confirm"/>', '<fmt:message key="user.export.team.confirm"/>', function(e){
						if(e == 'yes'){
						var formValues=userVipExportExcel.getForm().getValues();
							var begin = Ext.util.Format.date(Ext.getCmp('beginTime').getValue(), 'Y-m-d H:i:s');
							var end = Ext.util.Format.date(Ext.getCmp('endTime').getValue(), 'Y-m-d H:i:s');
							window.location.href = "/user/exportUserVipRecord.json?beginTime="+begin+"&endTime="+end;
			              	popWin.close();
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
		openWin('<fmt:message key="user.exporter.vip.xls"/>', userVipExportExcel, buttons, 400, 200);
	},
	
   	rendererStuts : function(val){
		if(val == 2) {
            return '<b><fmt:message key="user.level3"/></b>';
        }if(val == 3) {
            return '<b><fmt:message key="user.level1"/></b>';
        }else{
            return '<b><fmt:message key="user.level2"/></b>';
        }
	},
	
	rendererRecordType  : function(val){
		if(val == 2) {
            return '<b><fmt:message key="user.recordType2"/></b>';
        }else{
            return '<b><fmt:message key="user.recordType1"/></b>';
        }
	},  
	
	rendererPaymentType  : function(val){
		if(val == 1) {
            return '<b><fmt:message key="user.paymentType1"/></b>';
        }else{
            return '<b><fmt:message key="user.paymentType0"/></b>';
        }
	},  
});