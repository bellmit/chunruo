<%@ include file="/WEB-INF/pages/scripts/taglibs.jsp"%>

Ext.define('IdAuthenRecode', {
	extend: 'Ext.data.Model',
	idProperty: 'authenId',
    fields: [
    	{name: 'authenId',	 	mapping: 'authenId',	type: 'int'},
    	{name: 'token',	 		mapping: 'token',		type: 'string'},
		{name: 'orderNo',		mapping: 'orderNo',		type: 'string'},
		{name: 'fromShopName',	mapping: 'fromShopName',type: 'string'},
		{name: 'isAuthenSucc',	mapping: 'isAuthenSucc',type: 'bool'},
		{name: 'mobile',	 	mapping: 'mobile',		type: 'string'},
		{name: 'sentSmsTimes',	mapping: 'sentSmsTimes',type: 'int'},
		{name: 'errorMsg',	 	mapping: 'errorMsg',	type: 'string'},
		{name: 'identityName',	mapping: 'identityName',type: 'string'},
		{name: 'identityNo',	mapping: 'identityNo',	type: 'string'},
		{name: 'idCardId',		mapping: 'idCardId',	type: 'int'},
		{name: 'productNames',	mapping: 'productNames',type: 'string'},
		{name: 'notifyURL',	 	mapping: 'notifyURL',	type: 'string'},
		{name: 'downloadDate',	mapping: 'downloadDate',type: 'string'},
		{name: 'createTime',	mapping: 'createTime',	type: 'string'},
		{name: 'updateTime',	mapping: 'updateTime',	type: 'string'}
    ]
});

Ext.define('MyExt.orderManager.OrderIdAuthenRecodeList', {
    extend : 'Ext.grid.GridPanel',
    requires : ['Ext.ux.grid.GridHeaderFilters'],
	autoScroll: true,   
	closable: true,
	selType: 'checkboxmodel',
	multiSelect: true,
	columnLines: true,
	animCollapse: false,
    enableLocking: true,
    plugins: ['gridHeaderFilters'],
    viewConfig: {
        stripeRows: true,
        enableTextSelection: true
    },

	initComponent : function(config) {
		Ext.apply(this, config);
        
        this.store = Ext.create('Ext.data.Store', {
        	pageSize: 50,
	        autoLoad: false,
			autoDestroy: true,
			sortOnLoad: true,
			remoteSort: true,
			model: 'IdAuthenRecode',
			proxy: {
				type: 'ajax',
				url: '<c:url value="/idAuthen/idCardList.json"/>',
				reader: {
					type : 'json',
                	root: 'data',
                	totalProperty: 'totalCount'
            	}
			},
			sorters: [{
	            property: 'updateTime',
	            direction: 'desc'
	        }]
		});
		
		this.rendererStutsStore = Ext.create('Ext.data.Store', {
        	autoDestroy: true,
        	model: 'InitModel',
        	data: [
        		{id: 1, name: '<fmt:message key="button.yes"/>'},
        		{id: 0, name: '<fmt:message key="button.no"/>'}
        	]
        });
		
		this.columns = [
	    	{text: '<fmt:message key="idAuthen.authenId"/>', dataIndex: 'authenId', width: 80, sortable : true,
        		filter: {xtype: 'textfield'},
        		renderer: this.fontRenderer
        	},
        	{text: '<fmt:message key="idAuthen.orderNo"/>', dataIndex: 'orderNo', width: 140, sortable : true,
        		filter: {xtype: 'textfield'},
        		renderer: this.fontRenderer
        	},
        	{text: '<fmt:message key="idAuthen.fromShopName"/>', dataIndex: 'fromShopName', width: 140, sortable : true,
        		filter: {xtype: 'textfield'},
        		renderer : function(val, metadata, record, rowIndex, columnIndex, store){ 
					metadata.tdAttr = Ext.String.format('data-qtip="{0}"', val); 
					return val;
				}
        	},
        	{text: '<fmt:message key="idAuthen.isAuthenSucc"/>', dataIndex: 'isAuthenSucc', width: 90, sortable : true,
        		align: 'center',
        		filter: {xtype: 'textfield'},
        		renderer: this.rendererStuts,
        		filter: {
					xtype: 'combobox',
			        displayField: 'name',
			        valueField: 'id',
			        store: this.rendererStutsStore,
			        queryMode: 'local',
			        typeAhead: true
				}
        	},
        	{text: '<fmt:message key="idAuthen.mobile"/>', dataIndex: 'mobile', width: 110, sortable : true,
        		filter: {xtype: 'textfield'},
        		renderer: this.fontRenderer
        	},
        	{text: '<fmt:message key="idAuthen.sentSmsTimes"/>', dataIndex: 'sentSmsTimes', width: 90, sortable : true,
        		filter: {xtype: 'textfield'},
        		renderer: this.fontRenderer
        	},
        	{text: '<fmt:message key="idAuthen.errorMsg"/>', dataIndex: 'errorMsg', width: 150, sortable : true,
        		filter: {xtype: 'textfield'},
        		renderer : function(val, metadata, record, rowIndex, columnIndex, store){ 
					metadata.tdAttr = Ext.String.format('data-qtip="{0}"', val); 
					return val;
				}
        	},
        	{text: '<fmt:message key="idAuthen.identityName"/>', dataIndex: 'identityName', width: 100, sortable : true,
        		filter: {xtype: 'textfield'},
        		renderer : function(val, metadata, record, rowIndex, columnIndex, store){ 
					metadata.tdAttr = Ext.String.format('data-qtip="{0}"', val); 
					return val;
				}
        	},
        	{text: '<fmt:message key="idAuthen.identityNo"/>', dataIndex: 'identityNo', width: 140, sortable : true,
        		filter: {xtype: 'textfield'},
        		renderer: this.fontRenderer
        	},
        	{text: '<fmt:message key="idAuthen.idCardId"/>', dataIndex: 'idCardId', width: 80, sortable : true,
        		filter: {xtype: 'textfield'},
        		renderer: this.fontRenderer
        	},
        	{text: '<fmt:message key="idAuthen.productNames"/>', dataIndex: 'productNames', width: 140, sortable : true,
        		filter: {xtype: 'textfield'},
        		renderer : function(val, metadata, record, rowIndex, columnIndex, store){ 
					metadata.tdAttr = Ext.String.format('data-qtip="{0}"', val); 
					return val;
				}
        	},
        	{text: '<fmt:message key="idAuthen.address"/>', dataIndex: 'address', width: 240, sortable : true,
        		filter: {xtype: 'textfield'},
        		renderer : function(val, metadata, record, rowIndex, columnIndex, store){ 
					metadata.tdAttr = Ext.String.format('data-qtip="{0}"', val); 
					return val;
				}
        	},
        	{text: '<fmt:message key="idAuthen.notifyURL"/>', dataIndex: 'notifyURL', width: 240, sortable : true,
        		filter: {xtype: 'textfield'},
        		renderer : function(val, metadata, record, rowIndex, columnIndex, store){ 
					metadata.tdAttr = Ext.String.format('data-qtip="{0}"', val); 
					return val;
				}
        	},
        	{text: '<fmt:message key="idAuthen.downloadDate"/>', dataIndex: 'downloadDate', width: 140, sortable : true,
        		filter: {xtype: 'textfield'},
        		renderer: this.fontRenderer
        	},
        	{text: '<fmt:message key="idAuthen.createTime"/>', dataIndex: 'createTime', width: 140, sortable : true,
        		filter: {xtype: 'textfield'},
        		renderer: this.fontRenderer
        	},
        	{text: '<fmt:message key="idAuthen.updateTime"/>', dataIndex: 'updateTime', width: 140, sortable : true,
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
        this.bbar = this.pagingToolbar;      
    	this.callParent();
  
  		<jkd:haveAuthorize access="/idAuthen/restSendIdAuthenSms.json">
	    this.tbar = [{
        	text: '<fmt:message key="idAuthen.handler.sendIdAuthenSms"/>', 
        	iconCls: 'Packageadd', 	
        	handler: this.restSendIdAuthenSms, 
        	scope: this
        }];  
        </jkd:haveAuthorize>
    	this.callParent();
  
  		this.gsm = this.getSelectionModel();
  		<jkd:haveAuthorize access="/idAuthen/idCardList.json">
  		this.store.on('beforeload', function(store, options) {
	        Ext.apply(store.proxy.extraParams, {
	        	filters: Ext.JSON.encode(this.filters)
			});
	    }, this);
    	this.store.load();
    	</jkd:haveAuthorize>
    },
    
    restSendIdAuthenSms : function(){
    	var rowsData = [];		
		var records = this.gsm.getSelection();
		if(records.length == 0){
			showMsg('<fmt:message key="ajax.confirm"/>', '<fmt:message key="ajax.no.record"/>');
			return;
		}	
		for(var i = 0; i < records.length; i++){			
			rowsData.push(records[i].data.authenId);	
		}
		
		Ext.Msg.confirm('<fmt:message key="ajax.confirm"/>', '<fmt:message key="submit.confirm"/>', function(e){
			if(e == 'yes'){
				Ext.Ajax.request({
		        	url: '<c:url value="/idAuthen/restSendIdAuthenSms.json"/>',
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
	 	}, this);
    },
    
    rendererStuts : function(val){
		if(val == true) {
            return '<b><fmt:message key="button.yes"/></b>';
        }else{
            return '<fmt:message key="button.no"/>';
        }
	}
});