<%@ include file="/WEB-INF/pages/scripts/taglibs.jsp"%>

Ext.define('FxPage', {
	extend: 'Ext.data.Model',
	idProperty: 'pageId',
    fields: [
    	{name: 'pageId',		mapping: 'pageId',			type: 'int'},
		{name: 'channelId',	 	mapping: 'channelId',		type: 'int'},
		{name: 'channelName',	mapping: 'channelName',		type: 'string'},
		{name: 'pageName',	 	mapping: 'pageName',		type: 'string'},
		{name: 'categoryType',	mapping: 'categoryType',	type: 'int'},
		{name: 'categoryName',	mapping: 'categoryName',	type: 'string'},
		{name: 'createTime',	mapping: 'createTime',		type: 'string'},
		{name: 'updateTime',	mapping: 'updateTime',		type: 'string'},
    ]
});

Ext.define('MyExt.productManager.FxPageList', {
    extend : 'Ext.grid.GridPanel',
	region: 'center',
	autoScroll: true,   
	closable: true,
	selType: 'checkboxmodel',
	multiSelect: true,
	columnLines: true,
	animCollapse: false,
    enableLocking: true,
    viewConfig: {
        stripeRows: true,
        enableTextSelection: true
    },

	initComponent : function(config) {
		Ext.apply(this, config);
		
        this.store = Ext.create('Ext.data.Store', {
	        autoLoad: false,
			autoDestroy: true,
			sortOnLoad: true,
			remoteSort: true,
			model: 'FxPage',
			proxy: {
				type: 'ajax',
				url: '<c:url value="/channel/pageListByChannelId.json"/>',
				reader: {
					type : 'json',
                	root: 'data',
                	totalProperty: 'totalCount'
            	}
			}
		});
		
		this.columns = [
			{text: '<fmt:message key="fx.page.pageId"/>', dataIndex: 'pageId', width: 70, sortable : true},
        	{text: '<fmt:message key="fx.page.pageName"/>', dataIndex: 'pageName', width: 220, sortable : true},
        	{text: '<fmt:message key="fx.page.channelName"/>', dataIndex: 'channelName', width: 80, sortable : true},
        	{text: '<fmt:message key="fx.page.categoryType"/>', dataIndex: 'categoryName', width: 80, sortable : true},
        	{text: '<fmt:message key="fx.page.createTime"/>', dataIndex: 'createTime', width: 140, sortable : true},
        	{text: '<fmt:message key="fx.page.updateTime"/>', dataIndex: 'updateTime', width: 140, sortable : true}
        ];
	    
	    this.tbar = [
	    <jkd:haveAuthorize access="/channel/pageListByChannelId.json">
	    {
	    	text: '<fmt:message key="button.refresh"/>', 
	        iconCls: 'refresh', 	
	       	handler: function(){this.store.reload();}, 
	       	scope: this
	  	}
	  	<c:set var="isHaveAuthorize" value="true" />
		</jkd:haveAuthorize>
		<jkd:haveAuthorize access="/channel/savePage.json">
		<c:if test="${isHaveAuthorize}">,</c:if>
	  	{
        	text: '<fmt:message key="fx.page.add"/>', 
        	iconCls: 'add', 	
        	handler: this.addPage,
        	scope: this
        }
        <c:set var="isHaveAuthorize" value="true" />
		</jkd:haveAuthorize>
		<jkd:haveAuthorize access="/channel/editPage.json">
		<c:if test="${isHaveAuthorize}">,</c:if>
        {
        	text: '<fmt:message key="button.edit"/>', 
        	iconCls: 'add', 	
        	handler: this.editPage,
        	scope: this
        }
        <c:set var="isHaveAuthorize" value="true" />
		</jkd:haveAuthorize>
		<jkd:haveAuthorize access="/channel/setHomePage.json">
		<c:if test="${isHaveAuthorize}">,</c:if>
        {
        	text: '<fmt:message key="fx.page.set.frist.page"/>', 
        	iconCls: 'Applicationosxhome', 	
        	handler: this.setHome,
        	scope: this
        }
        <c:set var="isHaveAuthorize" value="true" />
		</jkd:haveAuthorize>
		<jkd:haveAuthorize access="/channel/deletePage.json">
		<c:if test="${isHaveAuthorize}">,</c:if>
        {
        	text: '<fmt:message key="fx.page.delete"/>', 
        	iconCls: 'delete', 	
        	handler: this.deletePage,
        	scope: this
        }
        </jkd:haveAuthorize>
        ];
    	this.callParent();
    	
    	this.store.on('beforeload', function(store, options) {
	        Ext.apply(store.proxy.extraParams, {
	        	channelId: this.record.data.channelId
			});
	    }, this);
    	this.gsm = this.getSelectionModel();
    	
    	<jkd:haveAuthorize access="/widget/savePage.msp">
    	this.on('itemdblclick', this.onDbClick, this);
    	</jkd:haveAuthorize>
    },
    
    onDbClick : function(view, record, item, index, e, eOpts){
    	var ueditorId = Ext.String.format('ueditor-{0}', this.id);
    	var componentEdit = Ext.create('Ext.Component', {
    		autoScroll: true,
            region: 'east',
            border: true,
            header: false,
            scope : this,  
            style:"background: #fff;",
            html: Ext.String.format('<div id="{0}" sytle="background: #fff;"></div>', ueditorId),
            listeners: {
            	scope : this,  
                boxready: function (t, layout, opts) {
                	try{
                	    var categoryType = record.data.categoryType;
                	    if(record.data.channelId == 9){
                	       categoryType = 3;
                	    }
                		customField.init(ueditorId,categoryType,0);
                		sendAjax.get('widget/getPageById.msp', {
             				pageId: record.data.pageId
         				}, function(res) {
             				if (res.code == 1) {
                 				customField.setHtml(res.data, record.data.channelId,record.data.pageId,categoryType,3);
             				}
        				});
					}catch(e){}
				}
            }
    	});
    	
    	var buttons = [{
			text: '<fmt:message key="button.save"/>',
			handler: function(){
				Ext.MessageBox.confirm('<fmt:message key="ajax.confirm"/>', '<fmt:message key="save.confirm"/>', function(e){
					if(e == 'yes'){
						Ext.Ajax.request({
				        	url: '<c:url value="/widget/savePage.msp"/>',
				         	method: 'post',
							scope: this,
							params:{pageId: record.data.pageId, custom: customField.getData()},
				          	success: function(response){
		          				var responseObject = Ext.JSON.decode(response.responseText);
		                        if(responseObject.code == 1){
		                       		showSuccMsg(responseObject.msg);
		                       		popWin.close();
		                        	this.store.loadPage(1);
								}else{
									showFailMsg(responseObject.msg, 4);
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
		var height = document.body.clientHeight;
      	openWin(record.data.title, componentEdit, buttons, 870, height);
    },
    
    setHome : function(){
    	var pageId ;		
		var records = this.gsm.getSelection();
		if(records.length == 0){
			showMsg('<fmt:message key="ajax.confirm"/>', '<fmt:message key="ajax.no.record"/>');
			return;
		}
		if(records.length >1){
			showMsg('<fmt:message key="ajax.confirm"/>', '<fmt:message key="fx.page.set.home.onlyone.error"/>');
			return;
		}
		
		pageId = records[0].data.pageId
		Ext.Msg.confirm('<fmt:message key="ajax.confirm"/>', '<fmt:message key="fx.page.set.frist.page"/>', function(e){
			if(e == 'yes'){
				Ext.Ajax.request({
		        	url: '<c:url value="/channel/setHomePage.json"/>',
		         	method: 'post',
					scope: this,
					params:{pageId: pageId},
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
    
    deletePage : function(){
    	var rowsData = [];				
		var records = this.gsm.getSelection();
		if(records.length == 0){
			showMsg('<fmt:message key="ajax.confirm"/>', '<fmt:message key="ajax.no.record"/>');
			return;
		}
		for(var i = 0; i < records.length; i++){			
			rowsData.push(records[i].data.pageId);	
		}
		
		Ext.Msg.confirm('<fmt:message key="ajax.confirm"/>', '<fmt:message key="fx.page.delete"/>', function(e){
			if(e == 'yes'){
				Ext.Ajax.request({
		        	url: '<c:url value="/channel/deletePage.json"/>',
		         	method: 'post',
					scope: this,
					params:{pageIds: Ext.JSON.encode(rowsData)},
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
    
    addPage : function(){
		var pageFormPanel = Ext.create('MyExt.productManager.FxPageFormPanel', {
			id: 'addPage@ProductManager',
			title: '<fmt:message key="fx.page.add"/>',
			channelId : this.record.data.channelId,
			channelName : this.record.data.channelName
		});
		
		
		var buttons = [{
			text: '<fmt:message key="button.save"/>',
			handler: function(){
	            if(pageFormPanel.form.isValid()){
	            	Ext.MessageBox.confirm('<fmt:message key="ajax.confirm"/>', '<fmt:message key="save.confirm"/>', function(e){
						if(e == 'yes'){
			                pageFormPanel.form.submit({
			                    waitMsg: 'Loading...',
			                    url: '<c:url value="/channel/savePage.json"/>',
			                    scope: this,
			                    success: function(form, action) {
			                        var responseObject = Ext.JSON.decode(action.response.responseText);
			                        if(responseObject.success == true){
			                       		showSuccMsg(responseObject.message);
			                        	this.store.loadPage(1);
										popWin.close();
									}else{
										showFailMsg(responseObject.message, 4);
									}
			                    },
			                    failure: function(form,action){
			                       var responseObject = Ext.JSON.decode(action.response.responseText);
			                       showFailMsg(responseObject.message,4);
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
		openWin('<fmt:message key="fx.page.add"/>', pageFormPanel, buttons, 450, 200);
	},
	
	
	 editPage : function(){
		var pageId ;		
		var records = this.gsm.getSelection();
		if(records.length == 0){
			showMsg('<fmt:message key="ajax.confirm"/>', '<fmt:message key="ajax.no.record"/>');
			return;
		}
		if(records.length >1){
			showMsg('<fmt:message key="ajax.confirm"/>', '<fmt:message key="fx.page.set.home.onlyone.error"/>');
			return;
		}
		
		pageId = records[0].data.pageId
		var pageFormPanel = Ext.create('MyExt.productManager.FxPageFormPanel', {
			id: 'addPage@ProductManager',
			title: '<fmt:message key="fx.page.add"/>',
			channelId : this.record.data.channelId,
			channelName : this.record.data.channelName,
			isEditPage:true,
			pageName:records[0].data.pageName,
			categoryType:records[0].data.categoryType
		});
		
		
		var buttons = [{
			text: '<fmt:message key="button.save"/>',
			handler: function(){
	            if(pageFormPanel.form.isValid()){
	            	Ext.MessageBox.confirm('<fmt:message key="ajax.confirm"/>', '<fmt:message key="save.confirm"/>', function(e){
						if(e == 'yes'){
			                pageFormPanel.form.submit({
			                    waitMsg: 'Loading...',
			                    url: '<c:url value="/channel/editPage.json"/>',
			                    scope: this,
			                    params: {pageId : pageId},
			                    success: function(form, action) {
			                        var responseObject = Ext.JSON.decode(action.response.responseText);
			                        if(responseObject.success == true){
			                       		showSuccMsg(responseObject.message);
			                        	this.store.loadPage(1);
		                    			this.gsm.deselectAll();
										popWin.close();
									}else{
										showFailMsg(responseObject.message, 4);
									}
			                    },
			                    failure: function(form,action){
			                       var responseObject = Ext.JSON.decode(action.response.responseText);
			                       showFailMsg(responseObject.message,4);
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
		openWin('<fmt:message key="fx.page.add"/>', pageFormPanel, buttons, 450, 200);
	},
	
	
    transferData : function(record){
    	this.record = record;
    	this.store.load();
    }
});