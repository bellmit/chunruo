<%@ include file="/WEB-INF/pages/scripts/taglibs.jsp"%>

Ext.define('FeedBack', {
	extend: 'Ext.data.Model',
	idProperty: 'feedbackId',
    fields: [
		{name: 'feedbackId',	 	mapping: 'feedbackId',	       type: 'int'},
    	{name: 'userId',	 		mapping: 'userId',	           type: 'int'},
    	{name: 'userName',	 		mapping: 'userName',	       type: 'string'},
    	{name: 'mobile',	 		mapping: 'mobile',		       type: 'string'},
		{name: 'ftype',	 			mapping: 'ftype',		       type: 'string'},
		{name: 'content',	 		mapping: 'content',	           type: 'string'},
		{name: 'uuid',	 		    mapping: 'uuid',	           type: 'string'},
		{name: 'userIp',	 	    mapping: 'userIp',		       type: 'string'},
		{name: 'isReply',	 	    mapping: 'isReply',            type: 'string'},
		{name: 'isPushUser',	    mapping: 'isPushUser',		   type: 'boolean'},
		{name: 'replyMsg',	 		mapping: 'replyMsg',	       type: 'string'},
		{name: 'createTime',	 	mapping: 'createTime',		    type: 'string',	
			convert:function(value){ 
				if(value) {
					var createTime = Ext.Date.format(new Date(value),"Y-m-d H:i:s");
					return createTime; 
				}   
			}},
		{name: 'updateTime',	 		mapping: 'updateTime',		type: 'string',	
			convert:function(value){ 
				if(value) {
					var createTime = Ext.Date.format(new Date(value),"Y-m-d H:i:s");
					return createTime; 
				}   
			}},   
    ]
});

Ext.define('MyExt.userManager.FeedbackPanel', {
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
			model: 'FeedBack',
			proxy: {
				type: 'ajax',
				url: '<c:url value="/feedback/list.json"/>',
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
        	{text: '<fmt:message key="user.feedbackId"/>', dataIndex: 'feedbackId', width: 70, locked: true, sortable : true,
        		filter: {xtype: 'textfield'}
        	},	
        	{text: '<fmt:message key="user.userId"/>', dataIndex: 'userId', width: 140, sortable : true,
        		filter: {xtype: 'textfield'}
        	},
        	{text: '<fmt:message key="user.userName"/>', dataIndex: 'userName', width: 140, sortable : true,
        		filter: {xtype: 'textfield'}
        	},
        	{text: '<fmt:message key="user.mobile"/>', dataIndex: 'mobile', width: 90, locked: true, sortable : true,
        		filter: {xtype: 'textfield'}
        	},
        	{text: '<fmt:message key="user.ftype"/>', dataIndex: 'ftype', width: 70, locked: true, sortable : true,
        		filter: {xtype: 'textfield'}
        	},
        	{text: '<fmt:message key="user.uuid"/>', dataIndex: 'uuid', width: 70, sortable : true,
        		filter: {xtype: 'textfield'}
        	},
        	{text: '<fmt:message key="user.content"/>', dataIndex: 'content', width: 100, sortable : true,
        		filter: {xtype: 'textfield'}
        	},
        	{text: '<fmt:message key="user.userIp"/>', dataIndex: 'userIp', width: 90, sortable : true,
        	filter: {xtype: 'textfield'}
        	},
        	{text: '<fmt:message key="user.isReply"/>', dataIndex: 'isReply', width: 70, sortable : true,
        		renderer: this.rendererStuts,
        		filter: {
        		xtype: 'combobox',
        		displayField: 'name',
			    valueField: 'id',
        		store: this.rendererPaymentSuccStore,
        		queryMode: 'local',
			    typeAhead: true},
        	},
        	{text: '<fmt:message key="user.isPushUser"/>', dataIndex: 'isPushUser', width: 140, sortable : true,
        		renderer: this.rendererRecordType,
        		filter: {
        		xtype: 'combobox',
        		displayField: 'name',
			    valueField: 'id',
        		store: this.rendererPaymentSuccStore,
        		queryMode: 'local',
			    typeAhead: true},
        	},
        	{text: '<fmt:message key="user.replyMsg"/>', dataIndex: 'replyMsg', width: 80, sortable : true,
        		filter: {xtype: 'textfield'}
        	},
        	{text: '<fmt:message key="user.createTime"/>', dataIndex: 'createTime', width: 140, sortable : true,
        		filter: {xtype: 'textfield'}
        	},
        	{text: '<fmt:message key="user.updateTime"/>', dataIndex: 'updateTime', width: 140, sortable : true,
        		filter: {xtype: 'textfield'}
        	},
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
		this.feedbackListBbar = this.pagingToolbar; 
		
    	this.feedbackList = Ext.create('Ext.grid.GridPanel', {
    	    id: 'feedbackList@FeedbackPanel' + this.id,
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
		    bbar: this.feedbackListBbar,
        	plugins: ['gridHeaderFilters'],
		    viewConfig: {
		        stripeRows: true,
		        enableTextSelection: true
		    }
	    });
	    
        this.east =  Ext.create('MyExt.userManager.FeedbackTabPanel', {
        	feedbackList: this.feedbackList,
		 	viewer: this.viewer,
		 	layout: 'card',
	        border: false,
	       	region: 'east',
	        width: 550,
	        split: true,
	        header: false,
	        hidden: true
        });
	    
    	this.items = [this.feedbackList, this.east];	
		this.east.hide();
    	this.callParent(arguments);
    	
    	<jkd:haveAuthorize access="/feedback/list.json">
    	this.store.on('beforeload', function(store, options) {
	        Ext.apply(store.proxy.extraParams, {
	        	filters: Ext.JSON.encode(this.feedbackList.filters),
	        	keyword: this.keywordField.getRawValue()
			});
	    }, this);
	    this.store.load();
	    </jkd:haveAuthorize>
	    
	    this.gsm = this.feedbackList.getSelectionModel();
	    this.feedbackList.on('itemdblclick', function(view, record, item, index, e, eOpts) {
	    	<jkd:haveAuthorize access="/feedback/getFeedbackById.json">
	    	this.east.transferData(this.east, record, this.body.dom.clientWidth);
	    	this.east.show();
	    	</jkd:haveAuthorize>
	    }, this);
    },
    
    cleanSearch : function(){
    	this.keywordField.setRawValue();
		this.store.loadPage(1);
    },
     
       rendererStuts : function(val){
		if(val == 1) {
            return '<b><fmt:message key="button.yes"/></b>';
        }else{
            return '<b><fmt:message key="button.no"/></b>';
        }
	},
	
	rendererRecordType  : function(val){
		if(val == true) {
            return '<b><fmt:message key="button.yes"/></b>';
        }else{
            return '<b><fmt:message key="button.no"/></b>';
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