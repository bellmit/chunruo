<%@ include file="/WEB-INF/pages/scripts/taglibs.jsp"%>


Ext.define('InviteImage', {
	extend: 'Ext.data.Model',
	idProperty: 'imageId',
    fields: [
		{name: 'imageId',			mapping: 'imageId',			type: 'int'},
		{name: 'imageContent',		mapping: 'imageContent',	type: 'string'},
		{name: 'imagePath',			mapping: 'imagePath',		type: 'string'},
		{name: 'typeName',			mapping: 'typeName',		type: 'string'},
		{name: 'imageType',			mapping: 'imageType',		type: 'int'},
		{name: 'createTime',	 	mapping: 'createTime',		type: 'string'},
		{name: 'updateTime',	 	mapping: 'updateTime',		type: 'string'},
    ]
});

Ext.define('MyExt.couponManager.InviteImageListPanel', {
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
		
    	this.store = Ext.create('Ext.data.Store', {
    		pageSize: 50,
	        autoLoad: false,
			autoDestroy: true,
			sortOnLoad: true,
			remoteSort: true,
			model: 'InviteImage',
			proxy: {
				type: 'ajax',
				url: '<c:url value="/inviteImage/imageList.json"/>',
				reader: {
					type : 'json',
                	root: 'data',
                	totalProperty: 'totalCount'
            	}
			},
			sorters: [{
	            property: 'imageId',
	            direction: 'desc'
	        }]
		});
		
		this.columns = [
        	{
            	text: '<fmt:message key="invite.image"/>',
                menuDisabled: true,
                sortable: false,
                xtype: 'actioncolumn',
                align: 'center',
                width: 100,
                higth: 100,
                locked: true,
                dataIndex: 'imagePath',
                renderer: function(value, metadata, record) {
        			return Ext.String.format('<img height="60" width="60" src="{0}"></img>', value);
    			}
            }, 
			{text: '<fmt:message key="invite.typeName"/>', dataIndex: 'typeName', width: 100, align: 'center',sortable: true, locked: true},
        	{text: '<fmt:message key="invite.imageContent"/>', dataIndex: 'imageContent', align: 'center',width: 250, sortable: true, locked: true},
        	{text: '<fmt:message key="invite.createTime"/>', dataIndex: 'createTime', width: 140, sortable : true, align: 'center'},
        	{text: '<fmt:message key="invite.updateTime"/>', dataIndex: 'updateTime', width: 140, sortable : true, align: 'center'},
        ];
		
		<jkd:haveAuthorize access="/inviteImage/imageList.json">
		this.tbar = [{
	    	text: '<fmt:message key="button.refresh"/>', 
	        iconCls: 'refresh', 	
	       	handler: function(){this.store.reload();}, 
	       	scope: this
	  	}];
	  	</jkd:haveAuthorize>
		
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
	    
	   	this.east =  Ext.create('MyExt.couponManager.InviteImageTabPanel', {
        	productList: this.productList,
		 	viewer: this.viewer,
		 	layout: 'card',
	        border: false,
	       	region: 'east',
	        width: 550,
	        split: true,
	        header: false,
	        hidden: true,
	        store: this.store
        });
        
	    this.gsm = this.productList.getSelectionModel();
	    this.items = [this.productList, this.east];	
	    this.east.hide();
	    this.callParent(arguments);
	    
	    <jkd:haveAuthorize access="/inviteImage/imageList.json">
	    this.store.on('beforeload', function(store, options) {
	        Ext.apply(store.proxy.extraParams, {
	        	filters: Ext.JSON.encode(this.productList.filters)
			});
	    }, this);
	    this.store.load();  
	    </jkd:haveAuthorize>
	    
	    this.productList.on('itemdblclick', function(view, record, item, index, e, eOpts) {
	    	<jkd:haveAuthorize access="/inviteImage/getInviteImageByType.json">
	    	this.east.transferData(this.east, record, this.body.dom.clientWidth);
	    	this.east.show();
	    	</jkd:haveAuthorize>
	    }, this); 
    },
	    
	saveBrand : function(){
    	var inviteImageFormPanel = Ext.create('MyExt.couponManager.InviteImageFormPanel', {
			id: 'add@inviteImageFormPanel' + this.id,
    		viewer: this.viewer,
   	 	});
    	
    	var buttons = [{
			text: '<fmt:message key="button.save"/>',
			handler: function(){
		    	var rowsDatas = [];    
		       	inviteImageFormPanel.down('imagepanel').store.each(function(record) {
		       		record.data.input_file = null;
		            rowsDatas.push(record.data);    
		      	}, this);
		    	if(inviteImageFormPanel.form.isValid()){
					Ext.MessageBox.confirm('<fmt:message key="ajax.confirm"/>', '<fmt:message key="save.confirm"/>', function(e){
						if(e == 'yes'){
		             		inviteImageFormPanel.form.submit({
		                 		waitMsg: 'Loading...',
		                 		url: '<c:url value="/brand/saveBrand.json"/>',
		               			scope: this,
		               			params:{recodeGridJson: Ext.JSON.encode(rowsDatas)},
		               			success: function(form, action) {
		                   			var responseObject = Ext.JSON.decode(action.response.responseText);
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
		 		}
			},
			scope: this
		},{
			text: '<fmt:message key="button.cancel"/>',
			handler : function(){popWin.close();},
			scope: this
		}];
      	openWin('<fmt:message key="button.add"/>', inviteImageFormPanel, buttons, 800, 400);
    },    
	    
   	fontRenderer: function(value, meta, record) {
    	meta.style = 'overflow:auto;padding: 3px 6px;text-overflow: ellipsis;white-space: nowrap;white-space:normal;line-height:24px;font-weight:bold;';      
       	return value;     
   	},
   	
});	    
	    
	    
		