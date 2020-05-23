<%@ include file="/WEB-INF/pages/scripts/taglibs.jsp"%>

Ext.define('Keywords', {
	extend: 'Ext.data.Model',
	idProperty: 'keywordsId',
    fields: [
		{name: 'keywordsId',		mapping: 'keywordsId',		type: 'int'},
		{name: 'name',				mapping: 'name',			type: 'string'},
		{name: 'seekCount',			mapping: 'seekCount',		type: 'int'},
		{name: 'isDefault',	 		mapping: 'isDefault',		type: 'bool'},
		{name: 'createTime',	 	mapping: 'createTime',		type: 'string'},
		{name: 'updateTime',	 	mapping: 'updateTime',		type: 'string'}
    ]
});

Ext.define('MyExt.dataManager.KeywordsListPanel', {
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
			model: 'Keywords',
			proxy: {
				type: 'ajax',
				url: '<c:url value="/keywords/keywordsList.json"/>',
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
		
		this.isDefaultStore = Ext.create('Ext.data.Store', {
			autoDestroy: true,
			model: 'InitModel',
			data: [
				{id: '1', name: '<fmt:message key="button.yes"/>'},
				{id: '0', name: '<fmt:message key="button.no"/>'},
			]
		});
		
		this.columns = [
			{text: '<fmt:message key="keywords.keywordsId"/>', dataIndex: 'keywordsId', width: 75, sortable: true, locked: true, filter: {xtype: 'textfield'}},
			{text: '<fmt:message key="keywords.name"/>', dataIndex: 'name', width: 200, sortable: true, locked: true, filter: {xtype: 'textfield'}},
        	{text: '<fmt:message key="keywords.seekCount"/>', dataIndex: 'seekCount', width: 90, sortable: true, locked: true, filter: {xtype: 'textfield'}},
        	{text: '<fmt:message key="keywords.isDefault"/>', dataIndex: 'isDefault', width: 90, locked: true,  align: 'center', sortable : true,
		    	renderer: this.rendererStuts,
	        	filter: {
					xtype: 'combobox',
				    displayField: 'name',
				    valueField: 'id',
				   	store: this.isDefaultStore,
				   	queryMode: 'local',
				    typeAhead: true
				}
			},
        	{text: '<fmt:message key="keywords.createTime"/>', dataIndex: 'createTime', width: 150, sortable : true, align: 'center',filter: {xtype: 'textfield'}},
        	{text: '<fmt:message key="keywords.updateTime"/>', dataIndex: 'updateTime', width: 150, sortable : true,filter: {xtype: 'textfield'}},
        ];
        
        this.tbar = [
        <jkd:haveAuthorize access="/keywords/keywordsList.json">
        {
	    	text: '<fmt:message key="button.refresh"/>', 
	        iconCls: 'refresh', 	
	       	handler: function(){this.store.reload();}, 
	       	scope: this
	  	}
	  	<c:set var="isHaveAuthorize" value="true" />
		</jkd:haveAuthorize>
		<jkd:haveAuthorize access="/keywords/saveKeywords.json">
		<c:if test="${isHaveAuthorize}">,</c:if>
	  	'-',{
        	text: '<fmt:message key="keywords.button.add"/>', 
        	iconCls: 'add', 	
        	handler: this.saveKeywords,
        	scope: this
        }
        <c:set var="isHaveAuthorize" value="true" />
		</jkd:haveAuthorize>
		<jkd:haveAuthorize access="/keywords/setKeywordsStatus.json">
		<c:if test="${isHaveAuthorize}">,</c:if>
        '-',{
        	text: '<fmt:message key="keywords.button.setDefault"/>', 
        	iconCls: 'enable', 	
        	handler: this.setDefaultKeywords,
        	scope: this
        }
        <c:set var="isHaveAuthorize" value="true" />
		</jkd:haveAuthorize>
		<jkd:haveAuthorize access="/keywords/setKeywordsStatus.json">
		<c:if test="${isHaveAuthorize}">,</c:if>
        '-',{
        	text: '<fmt:message key="keywords.button.setNotDefault"/>', 
        	iconCls: 'Cancel', 	
        	handler: this.setNotDefaultKeywords,
        	scope: this
        }
        </jkd:haveAuthorize>
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
		
		this.productList = Ext.create('Ext.grid.GridPanel', {
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
	    
    	this.gsm = this.productList.getSelectionModel();
    	this.items = [this.productList];	
		this.callParent(arguments);
	    
	    <jkd:haveAuthorize access="/keywords/keywordsList.json">
	   	this.store.on('beforeload', function(store, options) {
	        Ext.apply(store.proxy.extraParams, {
	        	filters: Ext.JSON.encode(this.productList.filters)
			});
	    }, this);
	    this.store.load(); 
	    </jkd:haveAuthorize>  
    },
		
	saveKeywords : function(){
    	var keywordsFormPanel = Ext.create('MyExt.dataManager.KeywordsFormPanel', {
			id: 'keywordsFormPanel@' + this.id,
    		viewer: this.viewer,
   	 	});
    	
    	var buttons = [{
			text: '<fmt:message key="button.save"/>',
			handler: function(){
			var name='';
			keywordsFormPanel.items.each(function(form) {
	        		if(!form.isValid()){
	        			isCheckSucc = false;
	        		}else{
	        			 name= Ext.getCmp('name').getValue();
	        		}
				}, this);
				console.log(name);
					Ext.MessageBox.confirm('<fmt:message key="ajax.confirm"/>', '<fmt:message key="save.confirm"/>', function(e){
						if(e == 'yes'){
		             		Ext.Ajax.request({
		                 		waitMsg: 'Loading...',
		                 		url: '<c:url value="/keywords/saveKeywords.json"/>',
		               			scope: this,
		               			params:{name: name},
		               			success: function(response) {
		                   			var responseObject = Ext.JSON.decode(response.responseText);
		                   			if(responseObject.error == false){
		                  				showSuccMsg(responseObject.message);
		                  				this.store.load();
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
      	openWin('<fmt:message key="keywords.button.add"/>', keywordsFormPanel, buttons, 300, 100);
    },	
		
	setDefaultKeywords : function(){
   		var records = this.gsm.getSelection();
   		if(records.length == 0){
			showMsg('<fmt:message key="ajax.confirm"/>', '<fmt:message key="ajax.no.record"/>');
			return;
		}else if(records.length > 1){
			showMsg('<fmt:message key="ajax.confirm"/>', '<fmt:message key="ajax.only.one.record"/>');
			return;
		}
		
	    Ext.MessageBox.confirm('<fmt:message key="ajax.confirm"/>', '<fmt:message key="enable.confirm"/>', function(e){
			if(e == 'yes'){
	        	Ext.Ajax.request({
		        	url: '<c:url value="/keywords/setKeywordsStatus.json"/>',
		         	method: 'post',
					scope: this,
					params:{keywordsId : records[0].data.keywordsId, status : 1},
		          	success: function(xresponse){
				    	var xresponseObject = Ext.JSON.decode(xresponse.responseText);
         				if (xresponseObject.success == true){
         					showSuccMsg(xresponseObject.message);
         					this.store.loadPage(1);
		                    this.gsm.deselectAll();
         				}else{
         					showFailMsg(xresponseObject.message, 4);
         				}
					}
		     	})
	        }
		}, this)
   	},
		
	setNotDefaultKeywords : function(){
   		var records = this.gsm.getSelection();
   		if(records.length == 0){
			showMsg('<fmt:message key="ajax.confirm"/>', '<fmt:message key="ajax.no.record"/>');
			return;
		}else if(records.length > 1){
			showMsg('<fmt:message key="ajax.confirm"/>', '<fmt:message key="ajax.only.one.record"/>');
			return;
		}
		
	    Ext.MessageBox.confirm('<fmt:message key="ajax.confirm"/>', '<fmt:message key="enable.confirm"/>', function(e){
			if(e == 'yes'){
	        	Ext.Ajax.request({
		        	url: '<c:url value="/keywords/setKeywordsStatus.json"/>',
		         	method: 'post',
					scope: this,
					params:{keywordsId : records[0].data.keywordsId, status : 0},
		          	success: function(xresponse){
				    	var xresponseObject = Ext.JSON.decode(xresponse.responseText);
         				if (xresponseObject.success == true){
         					showSuccMsg(xresponseObject.message);
         					this.store.loadPage(1);
		                    this.gsm.deselectAll();
         				}else{
         					showFailMsg(xresponseObject.message, 4);
         				}
					}
		     	})
	        }
		}, this)
   	},
	
	rendererStuts : function(val){
		if(val == true) {
            return '<b><fmt:message key="button.yes"/></b>';
        }else{
            return '<fmt:message key="button.no"/>';
        }
	},
});	
