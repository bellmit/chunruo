<%@ include file="/WEB-INF/pages/scripts/taglibs.jsp"%>

Ext.define('Channel', {
	extend: 'Ext.data.Model',
	idProperty: 'channelId',
    fields: [
    	{name: 'channelId',	 		mapping: 'channelId',			type: 'int'},
		{name: 'channelName',	 	mapping: 'channelName',			type: 'int'},
		{name: 'status',	 		mapping: 'status',				type: 'int'},
		{name: 'sort',	 			mapping: 'sort',				type: 'string'},
		{name: 'createTime',	 	mapping: 'createTime',			type: 'string',	
			convert:function(value){ 
				if(value) {
					var createTime = Ext.Date.format(new Date(value),"Y-m-d H:i:s");
					return createTime; 
				}   
			}
		},
		{name: 'updateTime',	 	mapping: 'updateTime',			type: 'string',	
			convert:function(value){ 
				if(value) {
					var createTime = Ext.Date.format(new Date(value),"Y-m-d H:i:s");
					return createTime; 
				}   
			}}
    ]
});

Ext.define('MyExt.storeManager.DistributionChannelPanel', {
    extend : 'Ext.panel.Panel',
    requires : ['Ext.ux.grid.GridHeaderFilters','Ext.ux.grid.Exporter'],
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
        
        this.channel = Ext.create('Ext.data.Store', {
        	pageSize: 50,
	        autoLoad: false,
			autoDestroy: true,
			sortOnLoad: true,
			remoteSort: true,
			model: 'Channel',
			proxy: {
				type: 'ajax',
				url: '<c:url value="/channel/list.json"/>',
				reader: {
					type : 'json',
                	root: 'data',
                	totalProperty: 'totalCount'
            	}
			},
			sorters: [{
	            property: 'sort',
	            direction: 'asc'
	        }]
		});
		
		
		
		this.columns = [
		
        	{text: '<fmt:message key="fx.channel.channelId"/>', dataIndex: 'channelId', width: 100,  sortable : true,
        		filter: {xtype: 'textfield'}
        	},
        		{text: '<fmt:message key="fx.channel.channelName"/>', dataIndex: 'channelName', width: 200,  sortable : true,
        		filter: {xtype: 'textfield'}
        	},
        	{text: '<fmt:message key="fx.channel.status"/>', dataIndex: 'status', width: 200, sortable : true,
        		filter: {xtype: 'textfield'},
		        renderer : function(val, metadata, record, rowIndex, columnIndex, store){ 
							var str =  '';
							if(val == '0'){
								str = '<fmt:message key="fx.channel.status.0"/>';
							}
							if(val == '1'){
								str = '<fmt:message key="fx.channel.status.1"/>';
							}
							if(val == '2'){
								str = '<fmt:message key="fx.channel.status.2"/>';
							}
							
							return str;
						}
        	},
         	{text: '<fmt:message key="fx.channel.sort"/>', dataIndex: 'sort', width: 200, sortable : true,
        		filter: {xtype: 'textfield'}
        	},
        	{text: '<fmt:message key="fx.channel.createTime"/>', dataIndex: 'createTime', width: 200, sortable : true,
        		filter: {xtype: 'textfield'}
        	},
        	{text: '<fmt:message key="fx.channel.updateTime"/>', dataIndex: 'updateTime', width: 200, sortable : true,
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
			store: this.channel,
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
                       	this.channel.pageSize = number;
                       	this.channel.load();
                   	}
               	}
        	}]	
		});
		this.channelListBbar = this.pagingToolbar; 
		
    	this.channelList = Ext.create('Ext.grid.GridPanel', {
	    	id: 'channelList@ChannelPanel' + this.id,
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
		    store: this.channel,
		    bbar: this.channelListBbar,
        	plugins: ['gridHeaderFilters','gridexporter'],
		    viewConfig: {
		        stripeRows: true,
		        enableTextSelection: true
		    }
	    });
	    
	    this.tbar = [{
        	text: '<fmt:message key="fx.channel.cancel"/>', 
        	iconCls: 'delete', 	
        	handler: this.cancelChannel, 
        	scope: this
        },'-',{text: '<fmt:message key="fx.channel.update"/>', 
        	iconCls: 'refresh', 	
        	handler: this.updateChannelStatus, 
        	scope: this
       	},'-',{
        	text: '<fmt:message key="button.add"/>', 
        	iconCls: 'add', 	
        	handler: this.addChannel,
        	scope: this
        }];
        
        this.east =  Ext.create('MyExt.storeManager.DistributionChannelTabPanel', {
        	storeList: this.channelList,
		 	viewer: this.viewer,
		 	layout: 'card',
	        border: false,
	       	region: 'east',
	        width: 550,
	        split: true,
	        header: false,
	        hidden: true
        });
	    
    	this.items = [this.channelList, this.east];	
		this.east.hide();
    	this.callParent(arguments);
    	
    	this.channel.on('beforeload', function(store, options) {
	        Ext.apply(store.proxy.extraParams, {
	        	filters: Ext.JSON.encode(this.channelList.filters),
				keyword: this.keywordField.getRawValue()
			});
	    }, this);
	    this.channel.load();
	    
	    this.gsm = this.channelList.getSelectionModel();
	    this.channelList.on('itemdblclick', function(view, record, item, index, e, eOpts) {
	    	this.east.transferData(this.east, record, this.body.dom.clientWidth);
	    	this.east.show();
	    }, this);
	    
	    this.channelList.on('headerfilterchange', function(e) {
	        alert('sfs');
		});
    },
    
    cleanSearch : function(){
    	this.keywordField.setRawValue();
		this.channel.loadPage(1);
    },
    addChannel : function(){
    	var channelFormPanel = Ext.create('MyExt.storeManager.DistributionChannelFormPanel', {
			id: 'channelFormPanel@' + this.id,
			isCreateChannel: true,
    		viewer: this.viewer
   	 	});
    	
    	var buttons = [{
			text: '<fmt:message key="button.save"/>',
			handler: function(){
				Ext.MessageBox.confirm('<fmt:message key="ajax.confirm"/>', '<fmt:message key="save.confirm"/>', function(e){
					if(e == 'yes'){
	                	channelFormPanel.form.submit({
	                		waitMsg: 'Loading...',
	                   		url: '<c:url value="/channel/saveChannel.json"/>',
	                   		scope: this,
	                   	 	success: function(form, action) {
	                    		var responseObject = Ext.JSON.decode(action.response.responseText);
	                       		if(responseObject.success == true){
	                       			showSuccMsg(responseObject.message);
	                        		this.channelList.store.reload();
									popWin.close();
								}else{
									showFailMsg(responseObject.message, 4);
								}
	                    	}
	        			})
	        		}
	        	}, this)
			},
			scope: this
		},{
			text: '<fmt:message key="button.cancel"/>',
			handler : function(){popWin.close();},
			scope: this
		}];
      	openWin('<fmt:message key="fx.channel.add"/>',channelFormPanel, buttons, 300, 160);
    },
    
    cancelChannel : function(){
		var rowsData = [];		
		var records = this.gsm.getSelection();
		if(records.length == 0){
			showMsg('<fmt:message key="ajax.confirm"/>', '<fmt:message key="ajax.no.record"/>');
			return;
		}	
		for(var i = 0; i < records.length; i++){			
			rowsData.push(records[i].data.channelId);	
		}
		
		Ext.Msg.confirm('<fmt:message key="ajax.confirm"/>', '<fmt:message key="order.cancel.confirm"/>', function(e){
			if(e == 'yes'){
				Ext.Ajax.request({
		        	url: '<c:url value="/channel/updateChannelStatus.json"/>',
		         	method: 'post',
					scope: this,
					params:{isDelete : true,idListGridJson: Ext.JSON.encode(rowsData)},
		          	success: function(response){
          				var responseObject = Ext.JSON.decode(response.responseText);
          				if (responseObject.success == true){
          					showSuccMsg(responseObject.message);
							this.channel.loadPage(1);
		                    this.gsm.deselectAll();
          				}else{
          					showFailMsg(responseObject.message, 4);
          				}
					}
		     	})
	     	}
	 	}, this) 
    },
     updateChannelStatus : function(){
		var rowsData = [];		
		var records = this.gsm.getSelection();
		if(records.length == 0){
			showMsg('<fmt:message key="ajax.confirm"/>', '<fmt:message key="ajax.no.record"/>');
			return;
		}	
		for(var i = 0; i < records.length; i++){			
			rowsData.push(records[i].data.channelId);	
		}
		
		Ext.Msg.confirm('<fmt:message key="ajax.confirm"/>', '<fmt:message key="order.cancel.confirm"/>', function(e){
			if(e == 'yes'){
				Ext.Ajax.request({
		        	url: '<c:url value="/channel/updateChannelStatus.json"/>',
		         	method: 'post',
					scope: this,
					params:{isDelete : false,idListGridJson: Ext.JSON.encode(rowsData)},
		          	success: function(response){
          				var responseObject = Ext.JSON.decode(response.responseText);
          				if (responseObject.success == true){
          					showSuccMsg(responseObject.message);
							this.channel.loadPage(1);
		                    this.gsm.deselectAll();
          				}else{
          					showFailMsg(responseObject.message, 4);
          				}
					}
		     	})
	     	}
	 	}, this) 
    }
    	
});