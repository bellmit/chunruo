<%@ include file="/WEB-INF/pages/scripts/taglibs.jsp"%>

Ext.define('FxChannel', {
	extend: 'Ext.data.Model',
	idProperty: 'channelId',
    fields: [
		{name: 'channelId',		mapping: 'channelId',		type: 'int'},
		{name: 'channelName',	mapping: 'channelName',		type: 'string'},
		{name: 'status',	 	mapping: 'status',			type: 'int'},
		{name: 'sort',	 		mapping: 'sort',			type: 'int'},
		{name: 'createTime',	mapping: 'createTime',		type: 'string'},
		{name: 'updateTime',	mapping: 'updateTime',		type: 'string'}	
    ]
});

Ext.define('MyExt.productManager.FxChannelPanel', {
   	extend : 'Ext.panel.Panel',
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
	        autoLoad: false,
			autoDestroy: true,
			sortOnLoad: true,
			remoteSort: true,
			model: 'FxChannel',
			proxy: {
				type: 'ajax',
				url: '<c:url value="/channel/list.json"/>',
				reader: {
					type : 'json',
                	root: 'data',
                	totalProperty: 'totalCount'
            	}
			}
		});
		
		this.columns = [
			{text: '<fmt:message key="fx.channel.channelId"/>', dataIndex: 'channelId', width: 65, sortable : true},
        	{text: '<fmt:message key="fx.channel.channelName"/>', dataIndex: 'channelName', width: 210, sortable : true},
        	{text: '<fmt:message key="fx.channel.status"/>', dataIndex: 'status', width: 70, sortable : true, align: 'center', renderer: this.booleanRenderer},
        	{text: '<fmt:message key="fx.channel.sort"/>', dataIndex: 'sort', width: 70, sortable : true}
        ];
        
        this.tbar = [
        <jkd:haveAuthorize access="/channel/list.json">
        {
	    	text: '<fmt:message key="button.refresh"/>', 
	        iconCls: 'refresh', 	
	       	handler: function(){this.store.reload();}, 
	       	scope: this
	  	}
	  	<c:set var="isHaveAuthorize" value="true" />
		</jkd:haveAuthorize>
		<jkd:haveAuthorize access="/channel/saveChannel.json">
		<c:if test="${isHaveAuthorize}">,</c:if>
	  	{
        	text: '<fmt:message key="button.add"/>', 
        	iconCls: 'add', 	
        	handler: this.addFxChannel,
        	scope: this
        }
        <c:set var="isHaveAuthorize" value="true" />
		</jkd:haveAuthorize>
		<jkd:haveAuthorize access="/channel/saveChannel.json">
		<c:if test="${isHaveAuthorize}">,</c:if>
        {
        	text: '<fmt:message key="button.edit"/>', 
        	iconCls: 'add', 	
        	handler: this.edtFxChannel,
        	scope: this
        }
        <c:set var="isHaveAuthorize" value="true" />
		</jkd:haveAuthorize>
		<jkd:haveAuthorize access="/channel/updateChannelStatus.json">
		<c:if test="${isHaveAuthorize}">,</c:if>
        '-',{
        	text: '<fmt:message key="button.enable"/>', 
        	iconCls: 'enable', 	
        	handler: this.enableFxChannel,
        	scope: this
        }
        <c:set var="isHaveAuthorize" value="true" />
		</jkd:haveAuthorize>
		<jkd:haveAuthorize access="/channel/updateChannelStatus.json">
		<c:if test="${isHaveAuthorize}">,</c:if>
        '-',{
        	text: '<fmt:message key="button.disable"/>', 
        	iconCls: 'Cancel', 	
        	handler: this.disableFxChannel,
        	scope: this
        }
        <c:set var="isHaveAuthorize" value="true" />
		</jkd:haveAuthorize>
		<jkd:haveAuthorize access="/channel/updateChannelStatus.json">
		<c:if test="${isHaveAuthorize}">,</c:if>
        '-',{
        	text: '<fmt:message key="button.delete"/>', 
        	iconCls: 'delete', 	
        	handler: this.deleteFxChannel,
        	scope: this
        }
        </jkd:haveAuthorize>
        ];
		
		this.productList = Ext.create('Ext.grid.GridPanel', {
			region: 'west',
			header: false,
			width: 450,
			autoScroll: true,   
			closable: true,
			selType: 'checkboxmodel',
			multiSelect: true,
			columnLines: true,
			animCollapse: false,
		    enableLocking: true,
		    columns: this.columns,
		    store: this.store,
		    viewConfig: {
		        stripeRows: true,
		        enableTextSelection: true
		    }
	    });     
	    
	    this.east =  Ext.create('MyExt.productManager.FxPageList', {
	       	region: 'center',
	        header: false,
	        autoScroll: true
        });
    	
    	this.gsm = this.productList.getSelectionModel();
    	this.items = [this.productList, this.east];	
		this.callParent(arguments);
		
		<jkd:haveAuthorize access="/channel/list.json">
    	this.store.load();   
    	</jkd:haveAuthorize>
	    
	    this.productList.on('itemdblclick', function(view, record, item, index, e, eOpts) {
	    	<jkd:haveAuthorize access="/channel/pageListByChannelId.json">
	    	this.east.transferData(record);
	    	</jkd:haveAuthorize>
	    }, this);
    },
    
    addFxChannel : function(){
    	var fxChannelFormPanel = Ext.create('MyExt.productManager.FxChannelFormPanel', {
			id: 'addFxChannelFormPanel@' + this.id,
    		viewer: this.viewer
   	 	});
    	
    	var buttons = [{
			text: '<fmt:message key="button.save"/>',
			handler: function(){
				Ext.MessageBox.confirm('<fmt:message key="ajax.confirm"/>', '<fmt:message key="save.confirm"/>', function(e){
					if(e == 'yes'){
	                	fxChannelFormPanel.form.submit({
	                		waitMsg: 'Loading...',
	                   		url: '<c:url value="/channel/saveChannel.json"/>',
	                   		scope: this,
	                   	 	success: function(form, action) {
	                    		var responseObject = Ext.JSON.decode(action.response.responseText);
	                       		if(responseObject.error == false){
	                       			showSuccMsg(responseObject.message);
	                        		this.store.reload();
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
      	openWin('<fmt:message key="fx.channel.add"/>', fxChannelFormPanel, buttons, 350, 140);
    },
    
    edtFxChannel : function(){
		var records = this.gsm.getSelection();
		if(records.length == 0){
			showMsg('<fmt:message key="ajax.confirm"/>', '<fmt:message key="ajax.no.record"/>');
			return;
		}
		if(records.length >1){
			showMsg('<fmt:message key="ajax.confirm"/>', '<fmt:message key="fx.page.set.home.onlyone.error"/>');
			return;
		}
					
		var channelId = records[0].data.channelId;	
    	var fxChannelFormPanel = Ext.create('MyExt.productManager.FxChannelFormPanel', {
			id: 'editFxChannelFormPanel@' + this.id,
    		viewer: this.viewer,
    		isEditChannel:true,
    		channelName:records[0].data.channelName,
    		channelSort:records[0].data.sort,
   	 	});
    	
    	var buttons = [{
			text: '<fmt:message key="button.save"/>',
			handler: function(){
				Ext.MessageBox.confirm('<fmt:message key="ajax.confirm"/>', '<fmt:message key="save.confirm"/>', function(e){
					if(e == 'yes'){
	                	fxChannelFormPanel.form.submit({
	                		waitMsg: 'Loading...',
	                   		url: '<c:url value="/channel/saveChannel.json"/>',
	                   		scope: this,
	                   		params: {channelId : channelId},
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
			},
			scope: this
		},{
			text: '<fmt:message key="button.cancel"/>',
			handler : function(){popWin.close();},
			scope: this
		}];
      	openWin('<fmt:message key="fx.channel.add"/>', fxChannelFormPanel, buttons, 350, 140);
    },
    
    enableFxChannel : function(){
    	var rowsData = [];		
		var records = this.gsm.getSelection();
		if(records.length == 0){
			showMsg('<fmt:message key="ajax.confirm"/>', '<fmt:message key="ajax.no.record"/>');
			return;
		}	
		for(var i = 0; i < records.length; i++){			
			rowsData.push(records[i].data.channelId);	
		}
		
		Ext.Msg.confirm('<fmt:message key="ajax.confirm"/>', '<fmt:message key="enable.confirm"/>', function(e){
			if(e == 'yes'){
				Ext.Ajax.request({
		        	url: '<c:url value="/channel/updateChannelStatus.json"/>',
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
    
    disableFxChannel : function(){
    	var rowsData = [];		
		var records = this.gsm.getSelection();
		if(records.length == 0){
			showMsg('<fmt:message key="ajax.confirm"/>', '<fmt:message key="ajax.no.record"/>');
			return;
		}	
		for(var i = 0; i < records.length; i++){			
			rowsData.push(records[i].data.channelId);	
		}
		
		Ext.Msg.confirm('<fmt:message key="ajax.confirm"/>', '<fmt:message key="disabled.confirm"/>', function(e){
			if(e == 'yes'){
				Ext.Ajax.request({
		        	url: '<c:url value="/channel/updateChannelStatus.json"/>',
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
    
    deleteFxChannel : function(){
    	var rowsData = [];		
		var records = this.gsm.getSelection();
		if(records.length == 0){
			showMsg('<fmt:message key="ajax.confirm"/>', '<fmt:message key="ajax.no.record"/>');
			return;
		}	
		for(var i = 0; i < records.length; i++){			
			rowsData.push(records[i].data.channelId);	
		}
		
		Ext.Msg.confirm('<fmt:message key="ajax.confirm"/>', '<fmt:message key="delete.confirm"/>', function(e){
			if(e == 'yes'){
				Ext.Ajax.request({
		        	url: '<c:url value="/channel/updateChannelStatus.json"/>',
		         	method: 'post',
					scope: this,
					params:{idListGridJson: Ext.JSON.encode(rowsData), status: 2},
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
		if(value == 0) {
			return '<span style="color:green;"><fmt:message key="button.no"/></span>';
		}else if(value == 1){
			return '<span style="color:red;"><b><fmt:message key="button.yes"/></b></span>';
		} 
		return value;
   	},
   	
   	fontRenderer: function(value, meta, record) {
    	meta.style = 'overflow:auto;padding: 3px 6px;text-overflow: ellipsis;white-space: nowrap;white-space:normal;line-height:24px;font-weight:bold;';      
       	return value;     
   	} 
});