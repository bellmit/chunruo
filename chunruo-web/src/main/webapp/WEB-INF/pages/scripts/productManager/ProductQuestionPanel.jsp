<%@ include file="/WEB-INF/pages/scripts/taglibs.jsp"%>

Ext.define('Question', {
	extend: 'Ext.data.Model',
    fields: [
    	{name: 'questionId',	mapping: 'questionId',	type: 'int'},
		{name: 'content',	 	mapping: 'content',		type: 'string'},
		{name: 'createTime',	mapping: 'createTime',	type: 'string'},
		{name: 'status',	 	mapping: 'status',		type: 'string'},
		{name: 'updateTime',	mapping: 'updateTime',	type: 'string'},
		{name: 'userId',		mapping: 'userId',		type: 'string'},
		{name: 'productId',		mapping: 'productId',	type: 'string'},
		{name: 'userName',		mapping: 'userName',	type: 'string'},
		{name: 'image',			mapping: 'image',	type: 'string'},
		{name: 'productName',	mapping: 'productName',	type: 'string'}
    ]
});

Ext.define('MyExt.productManager.ProductQuestionPanel', {
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
			model: 'Question',
			proxy: {
				type: 'ajax',
				url: '<c:url value="/questionAndAnswer/questionList.json"/>',
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
		
		this.columns = [
			{
            	text: '<fmt:message key="product.wholesale.image"/>',
                menuDisabled: true,
                sortable: false,
                xtype: 'actioncolumn',
                align: 'center',
                width: 40,
                higth: 30,
                dataIndex: 'image',
                locked: true,
                renderer: function(value, metadata, record) {
        			return Ext.String.format('<img height="25" width="25" src="{0}"></img>', value);
    			}
            }, 
			{text: '<fmt:message key="product.question.questionId"/>', dataIndex: 'questionId', width: 70, sortable: true, locked: true,
        		filter: {xtype: 'textfield'}
        	},
        	{text: '<fmt:message key="product.question.productName"/>', dataIndex: 'productName', width: 250, sortable: true, locked: true,
        		filter: {xtype: 'textfield'}
        	},
        	{text: '<fmt:message key="order.item.productId"/>', dataIndex: 'productId', width: 80, sortable: true, locked: true,
        		filter: {xtype: 'textfield'}
        	},
        	{text: '<fmt:message key="product.question.userName"/>', dataIndex: 'userName', width: 140, sortable: true,
        		filter: {xtype: 'textfield'}
        	},
        	{text: '<fmt:message key="product.question.content"/>', dataIndex: 'content', flex: 1, sortable : true,
        		filter: {xtype: 'textfield'}
        	},
        	{text: '<fmt:message key="product.question.status"/>', dataIndex: 'status', width: 70, sortable : true,
        		filter: {
        			xtype: 'combobox',
			        displayField: 'name',
			        valueField: 'id',
			        store: Ext.create('Ext.data.Store', {
						autoDestroy: true,
						model: 'InitModel',
						data: [
							{id: '1', name: '<fmt:message key="product.question.status_1"/>'},
							{id: '0', name: '<fmt:message key="product.question.status_0"/>'},
						]
					}),
			        queryMode: 'local',
			        typeAhead: true
				}, renderer: this.booleanRenderer
        	},
        	{text: '<fmt:message key="product.question.createtime"/>', dataIndex: 'createTime', width: 140, sortable : true},
        	{text: '<fmt:message key="product.question.updatetime"/>', dataIndex: 'updateTime', width: 140, sortable : true}
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
		
		this.questionList = Ext.create('Ext.grid.GridPanel', {
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
        	plugins: ['gridHeaderFilters'],
		    viewConfig: {
		        stripeRows: true,
		        enableTextSelection: true
		    },
		    tbar:[
	        <jkd:haveAuthorize access="/questionAndAnswer/checkQuestion.json">
	        {
	        	text: '<fmt:message key="product.question.checkQuestion"/>', 
	        	iconCls: 'enable', 	
	        	handler: this.checkQuestion, 
	        	scope: this
	        }
	        <c:set var="isHaveAuthorize" value="true" />
			</jkd:haveAuthorize>
			<jkd:haveAuthorize access="/questionAndAnswer/deleteQuestion.json">
			<c:if test="${isHaveAuthorize}">,</c:if>
	        '-',{
	        	text: '<fmt:message key="button.delete"/>', 
	        	iconCls: 'delete', 	
	        	handler: this.deleteProductQuestion, 
	        	scope: this
	        }
	        </jkd:haveAuthorize>
	        ]
	    });     
	    
	    this.east =  Ext.create('MyExt.productManager.ProductQuestionTabPanel', {
	    	questionList: this.questionList,
        	viewer: this.viewer,
	        border: false,
	       	region: 'east',
	        width: 600,
	        split: true,
	        header: false,
	        hidden: true
        });
        
    	this.items = [this.questionList, this.east];	
		this.east.hide();
		this.callParent(arguments);
	    
	    this.gsm = this.questionList.getSelectionModel();
	    <jkd:haveAuthorize access="/questionAndAnswer/questionList.json">
	   	this.store.on('beforeload', function(store, options) {
	        Ext.apply(store.proxy.extraParams, {
	        	filters: Ext.JSON.encode(this.questionList.filters)
			});
	    }, this);
	    this.store.load();   
	    </jkd:haveAuthorize>
	    
	    this.questionList.on('itemdblclick', function(view, record, item, index, e, eOpts) {
	    	<jkd:haveAuthorize access="/questionAndAnswer/getAnswerListByQuestionId.json">
	    	this.east.transferData(this.east, record, this.body.dom.clientWidth);
	    	this.east.show();
	    	</jkd:haveAuthorize>
	    }, this);
    },
    
    checkQuestion : function() {
		var rowsData = [];		
		var records = this.gsm.getSelection();
		if(records.length == 0){
			showMsg('<fmt:message key="ajax.confirm"/>', '<fmt:message key="ajax.no.record"/>');
			return;
		}	

		for(var i = 0; i < records.length; i++){	
			rowsData.push(records[i].data.questionId);	
		}
		
		Ext.Msg.confirm('<fmt:message key="ajax.confirm"/>', '<fmt:message key="save.confirm"/>', function(e){
			if(e == 'yes'){
				Ext.Ajax.request({
		        	url: '<c:url value="/questionAndAnswer/checkQuestion.json"/>',
		         	method: 'post',
					scope: this,
					params:{idListGridJson: Ext.JSON.encode(rowsData)},
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
	
    deleteProductQuestion : function() {
		var rowsData = [];		
		var records = this.gsm.getSelection();
		if(records.length == 0){
			showMsg('<fmt:message key="ajax.confirm"/>', '<fmt:message key="ajax.no.record"/>');
			return;
		};	
		for(var i = 0; i < records.length; i++){			
			rowsData.push(records[i].data.questionId);	
		};
		
		Ext.Msg.confirm('<fmt:message key="ajax.confirm"/>', '<fmt:message key="delete.confirm"/>', function(e){
			if(e == 'yes'){
				Ext.Ajax.request({
		        	url: '<c:url value="/questionAndAnswer/deleteQuestion.json"/>',
		         	method: 'post',
					scope: this,
					params:{idListGridJson: Ext.JSON.encode(rowsData)},
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
       	if(value =="true") {
            return '<b><fmt:message key="product.question.status_1"/></b>';
        }else{
            return '<fmt:message key="product.question.status_0"/>';
        }  
   	},
   	
   	fontRenderer: function(value, meta, record) {
    	meta.style = 'overflow:auto;padding: 3px 6px;text-overflow: ellipsis;white-space: nowrap;white-space:normal;line-height:24px;font-weight:bold;';      
       	return value;     
   	} 
});