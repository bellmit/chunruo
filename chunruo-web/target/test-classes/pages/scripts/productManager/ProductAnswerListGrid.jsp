<%@ include file="/WEB-INF/pages/scripts/taglibs.jsp"%>

Ext.define('ProductAnswerGrid', {
	extend: 'Ext.data.Model',
	idProperty: 'answerId',
    fields: [
		{name: 'answerId',	 		mapping: 'answerId',		type: 'int'},
		{name: 'questionId',	 	mapping: 'questionId',		type: 'int'},
		{name: 'userId',	 		mapping: 'userId',			type: 'int'},
		{name: 'userName',	        mapping: 'userName',		type: 'string'},
		{name: 'status',	 		mapping: 'status',			type: 'string'},
		{name: 'productName',	 	mapping: 'productName',		type: 'string'},
		{name: 'questionContent',	mapping: 'questionContent',	type: 'string'},
		{name: 'content',	 		mapping: 'content',			type: 'string'},
		{name: 'createTime',		mapping: 'createTime',		type: 'string'},
		{name: 'updateTime',	 	mapping: 'updateTime',		type: 'string'}
    ]
});

Ext.define('MyExt.productManager.ProductAnswerListGrid', {
    extend : 'Ext.grid.GridPanel',
    requires : ['Ext.ux.grid.GridHeaderFilters'],
	region: 'center',
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
    		autoLoad: false,
			autoDestroy: true,
			sortOnLoad: true,
			remoteSort: true,
			model: 'ProductAnswerGrid',
			proxy: {
				type: 'ajax',
				url: '<c:url value="/questionAndAnswer/answerList.json"/>',
				reader: {
					type : 'json',
                	root: 'data',
                	totalProperty: 'totalCount'
            	}
			}
		});
		
		this.columns = [
	    	{text: '<fmt:message key="product.answer.answerId"/>', dataIndex: 'answerId', width: 70, sortable : true, filter: {xtype: 'textfield'}},
	    	{text: '<fmt:message key="product.question.content"/>', dataIndex: 'questionContent', width: 270, sortable : true},
	    	{text: '<fmt:message key="product.answer.userId"/>', dataIndex: 'userId', width: 120, sortable : true},
        	{text: '<fmt:message key="product.answer.userName"/>', dataIndex: 'userName', width: 200, sortable : true},
        	{text: '<fmt:message key="product.answer.status"/>', dataIndex: 'status', width: 80, sortable : true,
        		renderer: this.booleanRenderer,
        		filter: {
        			xtype: 'combobox',
			        displayField: 'name',
			        valueField: 'id',
			        store: Ext.create('Ext.data.Store', {
						autoDestroy: true,
						model: 'InitModel',
						data: [
							{id: '1', name: '<fmt:message key="product.answer.status_1"/>'},
							{id: '0', name: '<fmt:message key="product.answer.status_0"/>'},
						]
					}),
			        queryMode: 'local',
			        typeAhead: true
				}
        	},
	    	{text: '<fmt:message key="product.answer.content"/>', dataIndex: 'content', flex: 1, sortable : true, filter: {xtype: 'textfield'}},
	    	{text: '<fmt:message key="product.answer.createTime"/>', dataIndex: 'createTime', width: 180, sortable : true},
	    	{text: '<fmt:message key="product.answer.updateTime"/>', dataIndex: 'updateTime', width: 180, sortable : true}
        ]; 
        
        this.tbar = [
        <jkd:haveAuthorize access="/questionAndAnswer/answerList.json">
        {
	    	text: '<fmt:message key="button.refresh"/>', 
	        iconCls: 'refresh', 	
	       	handler: function(){this.store.reload();}, 
	       	scope: this
	  	}
	  	<c:set var="isHaveAuthorize" value="true" />
		</jkd:haveAuthorize>
		<jkd:haveAuthorize access="/questionAndAnswer/checkAnswer.json">
		<c:if test="${isHaveAuthorize}">,</c:if>
	  	'-', {
        	text: '<fmt:message key="product.answer.checkAnswer"/>', 
        	iconCls: 'enable', 	
        	handler: this.checkAnswer, 
        	scope: this
        }
        <c:set var="isHaveAuthorize" value="true" />
		</jkd:haveAuthorize>
		<jkd:haveAuthorize access="/questionAndAnswer/deleteAnswer.json">
		<c:if test="${isHaveAuthorize}">,</c:if>
        '-',{
        	text: '<fmt:message key="button.delete"/>', 
        	iconCls: 'delete', 	
        	handler: this.deleteProductAnswer, 
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
		this.bbar = this.pagingToolbar;
    	this.callParent();
    	
    	this.gsm = this.getSelectionModel();
    	<jkd:haveAuthorize access="/questionAndAnswer/answerList.json">
    	this.store.on('beforeload', function(store, options) {
	        Ext.apply(store.proxy.extraParams, {
	        	filters: Ext.JSON.encode(this.filters)
			});
	    }, this);
	    this.store.load();
	    </jkd:haveAuthorize>
    },
    
    transferData : function( record){
    	this.record = record;
    	this.store.load();
    },
    
    booleanRenderer: function(value, meta, record) { 
       	if(value =="true") {
            return '<b><fmt:message key="product.question.status_1"/></b>';
        }else{
            return '<fmt:message key="product.question.status_0"/>';
        }  
   	},
    
    checkAnswer : function() {
		var rowsData = [];		
		var records = this.gsm.getSelection();
		if(records.length == 0){
			showMsg('<fmt:message key="ajax.confirm"/>', '<fmt:message key="ajax.no.record"/>');
			return;
		}	
		for(var i = 0; i < records.length; i++){	
			rowsData.push(records[i].data.answerId);	
		}
		
		Ext.Msg.confirm('<fmt:message key="ajax.confirm"/>', '<fmt:message key="save.confirm"/>', function(e){
			if(e == 'yes'){
				Ext.Ajax.request({
		        	url: '<c:url value="/questionAndAnswer/checkAnswer.json"/>',
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
	
    deleteProductAnswer : function() {
		var rowsData = [];		
		var records = this.gsm.getSelection();
		if(records.length == 0){
			showMsg('<fmt:message key="ajax.confirm"/>', '<fmt:message key="ajax.no.record"/>');
			return;
		};	
		for(var i = 0; i < records.length; i++){			
			rowsData.push(records[i].data.answerId);	
		};
		
		Ext.Msg.confirm('<fmt:message key="ajax.confirm"/>', '<fmt:message key="delete.confirm"/>', function(e){
			if(e == 'yes'){
				Ext.Ajax.request({
		        	url: '<c:url value="/questionAndAnswer/deleteAnswer.json"/>',
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
	}
});