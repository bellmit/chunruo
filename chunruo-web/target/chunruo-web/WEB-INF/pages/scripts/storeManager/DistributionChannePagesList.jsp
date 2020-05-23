<%@ include file="/WEB-INF/pages/scripts/taglibs.jsp"%>

Ext.define('ChannelPage', {
	extend: 'Ext.data.Model',
	idProperty: 'pageId',
    fields: [
		{name: 'pageId',		mapping: 'pageId',			type: 'int'},
		{name: 'channelId',		mapping: 'channelId',		type: 'string'},
		{name: 'pageName',		mapping: 'pageName',		type: 'string'},
		{name: 'categoryType',	mapping: 'categoryType',	type: 'string'},
		{name: 'createTime',	mapping: 'createTime',		type: 'string',	
			convert:function(value){ 
				if(value) {
					var createTime = Ext.Date.format(new Date(value),"Y-m-d H:i:s");
					return createTime; 
				}   
			}},
		{name: 'updateTime',	mapping: 'updateTime',		type: 'string',	
			convert:function(value){ 
				if(value) {
					var createTime = Ext.Date.format(new Date(value),"Y-m-d H:i:s");
					return createTime; 
				}   
			}}
    ]
});


Ext.define('MyExt.storeManager.DistributionChannePagesList', {
    extend : 'Ext.grid.GridPanel',
    alias: ['widget.channelPageList'],
    <!-- requires : [], -->
    id:'pageId',
	region: 'center',
	autoScroll: true,   
	multiSelect: true,
	columnLines: true,
	animCollapse: false,
	stripeRows:true, 
    enableLocking: true,
    selType: 'checkboxmodel',
    viewConfig: {
        stripeRows: true,
        enableTextSelection: true
    },
    plugins:[  
             Ext.create('Ext.grid.plugin.CellEditing',{  
                 clicksToEdit:2 
              })  
        ],
         
	initComponent : function(config) {
		Ext.apply(this, config);
		
		var businessId;
    	this.store = Ext.create('Ext.data.Store', {
			autoDestroy: true,
			model: 'ChannelPage'
		});
		
		this.columns = [
	    	{text: '<fmt:message key="fx.page.pageId"/>', dataIndex: 'pageId', width: 65, sortable : true},
			{text: '<fmt:message key="fx.page.channelId"/>', dataIndex: 'channelId', width: 80, sortable : true},
			{text: '<fmt:message key="fx.page.pageName"/>', dataIndex: 'pageName', flex: 1, sortable : true},
			{text: '<fmt:message key="fx.page.categoryType"/>', dataIndex: 'categoryType', width: 120, sortable : true,
				renderer : function(val, metadata, record, rowIndex, columnIndex, store){ 
							var str =  "";
							if(val== '0'){
								str = '<fmt:message key="fx.page.categoryType.0"/>';
							}else{
							str = '<fmt:message key="fx.page.categoryType.1"/>';
							}
							return str;
						}
			},
			{text: '<fmt:message key="fx.page.createTime"/>', dataIndex: 'createTime', flex: 1, sortable : true},
			{text: '<fmt:message key="fx.page.updateTime"/>', dataIndex: 'updateTime', width: 120, sortable : true}
        ]; 
        
        this.tbar = [{
        	text: '<fmt:message key="fx.page.add"/>', 
        	iconCls: 'add', 	
        	handler: this.addPage, 
        	scope: this
        },'-',{
        	text: '<fmt:message key="fx.page.delete"/>', 
        	iconCls: 'delete', 	
        	handler: this.deletePage, 
        	scope: this
        },'->',{
        	text: '<fmt:message key="fx.page.set.frist.page"/>', 
        	iconCls: '', 	
        	handler: this.setFristPage, 
        	scope: this
        }];
        
    	this.callParent();
    	this.on('itemdblclick', this.onDbClick, this);
    	this.gsm = this.getSelectionModel();
    },
    
    
 
    
   
   
    deletePage : function() {
    	channelId = 0; 
		var rowsData = [];
		var records = this.gsm.getSelection();
		if(records.length == 0){
			showMsg('<fmt:message key="ajax.confirm"/>', '<fmt:message key="ajax.no.record"/>');
			return;
		}
		for(var i = 0; i < records.length; i++){
			rowsData.push(records[i].data.pageId);	
			channelId = records[0].data.channelId
		}
		
		Ext.Msg.confirm('<fmt:message key="ajax.confirm"/>', '<fmt:message key="delete.confirm"/>', function(e){
			if(e == 'yes'){
				Ext.Ajax.request({
		        	url: '<c:url value="/channel/deleteChannelPageById.json"/>',
		         	method: 'post',
					scope: this,
					params:{idListGridJson: Ext.JSON.encode(rowsData)},
		          	success: function(response){
          				var responseObject = Ext.JSON.decode(response.responseText);
                        if(responseObject.success){
                       		showSuccMsg(responseObject.message);
                        	this.loadData();
		                    this.gsm.deselectAll();
						}else{
							showFailMsg(responseObject.message, 4);
						}
					}
		     	})
	     	}
	 	}, this)  
	},
	
	
	
    addPage : function(){
    	var pageFormPanel = Ext.create('MyExt.storeManager.DistributionPageFormPanel', {
			id: 'pageFormPanel@DistributionPageFormPanel',
			isCreateChannel: true,
    		viewer: this.viewer
   	 	});
    	
    	var buttons = [{
			text: '<fmt:message key="button.save"/>',
			handler: function(){
				Ext.MessageBox.confirm('<fmt:message key="ajax.confirm"/>', '<fmt:message key="save.confirm"/>', function(e){
					if(e == 'yes'){
					 	var formValues=pageFormPanel.getForm().getValues();
                		channelId = formValues["channelId"];
                		
	                	pageFormPanel.form.submit({
	                		waitMsg: 'Loading...',
	                   		url: '<c:url value="/channel/savePage.json"/>',
	                   		scope: this,
	                   	 	success: function(form, action) {
	                    		var responseObject = Ext.JSON.decode(action.response.responseText);
	                       		if(responseObject.success == true){
	                       			showSuccMsg(responseObject.message);
	                        		this.loadData();
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
      	openWin('<fmt:message key="fx.page.creat"/>',pageFormPanel, buttons, 300, 300);
    },
    
    setFristPage : function() {
    	channelId = 0; 
    	var pageId = 0;
		var rowsData = [];
		var records = this.gsm.getSelection();
		if(records.length == 0 || records.length > 1){
			showMsg('<fmt:message key="ajax.confirm"/>', '<fmt:message key="ajax.no.record"/>');
			return;
		}
		
		pageId = records[0].data.pageId;	
		channelId = records[0].data.channelId;
		Ext.Msg.confirm('<fmt:message key="ajax.confirm"/>', '<fmt:message key="delete.confirm"/>', function(e){
			if(e == 'yes'){
				Ext.Ajax.request({
		        	url: '<c:url value="/channel/setFristPageById.json"/>',
		         	method: 'post',
					scope: this,
					params:{pageId: pageId},
		          	success: function(response){
          				var responseObject = Ext.JSON.decode(response.responseText);
                        if(responseObject.success){
                       		showSuccMsg(responseObject.message);
                        	this.loadData();
		                    this.gsm.deselectAll();
						}else{
							showFailMsg(responseObject.message, 4);
						}
					}
		     	})
	     	}
	 	}, this)  
	},
	
     onDbClick : function(view, record, item, index, e, eOpts){
    	var editChannelPagePanel = Ext.create('MyExt.storeManager.EditChannelPagePanel', {
			id: 'editChannelPagePanel@editChannelPagePanel',
			viewer: this.viewer
   
   	 	});
   	
    	
    	var buttons = [{
			text: '<fmt:message key="button.save"/>',
			handler : function(){popWin.show();},
			scope: this
		},{
			text: '<fmt:message key="button.cancel"/>',
			handler : function(){popWin.close();},
			scope: this
		}];
		
      	openWin('<fmt:message key="fx.page.creat"/>',editChannelPagePanel, buttons, 400, 700);
      	
      	Ext.Ajax.request({
       		url: '<c:url value="/channel/getimageListByPageId.json"/>',
        	method: 'post',
			scope: this,
			params:{pageId: record.data.pageId},
         	success: function(response){
       			var responseObject = Ext.JSON.decode(response.responseText);
       			if (responseObject.success == true){
       				Ext.getCmp('editChannelPagePanel@editChannelPagePanel').body.update(responseObject.table);
  				  
       				
       			}
			}
    	});
    },
    
    loadData : function(){
    	Ext.Ajax.request({
       		url: '<c:url value="/channel/getChannelById.json"/>',
        	method: 'post',
			scope: this,
			params:{channelId: channelId},
         	success: function(response){
       			var responseObject = Ext.JSON.decode(response.responseText);
       			if (responseObject.success == true){
       				this.store.removeAll();
       				
       				if(responseObject.channelPageList != null && responseObject.channelPageList.length > 0){
       					for(var i = 0; i < responseObject.channelPageList.length; i ++){
       						var channelPageData = Ext.create('ChannelPage', responseObject.channelPageList[i]);
       						this.store.insert(i, channelPageData);
       					}
       				}
       			}
			}
    	})
    },
   
    
    addModular : function(pageId,typeId){
    	Ext.Ajax.request({
       		url: '<c:url value="/channel/addModular.json"/>',
        	method: 'post',
			scope: this,
			params:{pageId: pageId, typeId: typeId},
         	success: function(response){
       			var responseObject = Ext.JSON.decode(response.responseText);
       			if (responseObject.success == true){
       				this.store.removeAll();
       					alert(responseObject.message);
       				if(responseObject.channelPageList != null && responseObject.channelPageList.length > 0){
       					for(var i = 0; i < responseObject.channelPageList.length; i ++){
       						var channelPageData = Ext.create('ChannelPage', responseObject.channelPageList[i]);
       						this.store.insert(i, channelPageData);
       					}
       				}
       			}
			}
    	})
    }

});

