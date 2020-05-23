<%@ include file="/WEB-INF/pages/scripts/taglibs.jsp"%>

Ext.define('ProductAnswer', {
	extend: 'Ext.data.Model',
	idProperty: 'answerId',
    fields: [
		{name: 'answerId',	 		mapping: 'answerId',		type: 'int'},
		{name: 'questionId',	 	mapping: 'questionId',		type: 'int'},
		{name: 'userId',	 		mapping: 'userId',			type: 'int'},
		{name: 'userName',	        mapping: 'userName',		type: 'string'},
		{name: 'status',	 		mapping: 'status',			type: 'string'},
		{name: 'content',	 		mapping: 'content',			type: 'string'},
		{name: 'createTime',		mapping: 'createTime',		type: 'string'},
		{name: 'updateTime',	 	mapping: 'updateTime',		type: 'string'}
    ]
});

Ext.define('MyExt.productManager.ProductAnswerListPanel', {
    extend : 'Ext.grid.GridPanel',
    alias: ['widget.productAnswerListPanel'],
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
			model: 'ProductAnswer',
			proxy: {
				type: 'ajax',
				url: '<c:url value="/questionAndAnswer/getAnswerListByQuestionId.json"/>',
				reader: {
					type : 'json',
                	root: 'data',
                	totalProperty: 'totalCount'
            	}
			}
		});
		
		this.columns = [
	    	{text: '<fmt:message key="product.answer.answerId"/>', dataIndex: 'answerId', width: 70, sortable : true},
	    	{text: '<fmt:message key="product.answer.questionId"/>', dataIndex: 'questionId', width: 70, sortable : true},
	    	{text: '<fmt:message key="product.answer.userId"/>', dataIndex: 'userId', width: 120, sortable : true},
        	{text: '<fmt:message key="product.answer.userName"/>', dataIndex: 'userName', width: 100, sortable : true},
        	{text: '<fmt:message key="product.answer.status"/>', dataIndex: 'status', width: 80, sortable : true , renderer: this.booleanRenderer},
	    	{text: '<fmt:message key="product.answer.content"/>', dataIndex: 'content', flex:1, sortable : true},
	    	{text: '<fmt:message key="product.answer.createTime"/>', dataIndex: 'createTime', width: 140, sortable : true},
	    	{text: '<fmt:message key="product.answer.updateTime"/>', dataIndex: 'updateTime', width: 140, sortable : true}
        ]; 
        
        this.tbar = [
        <jkd:haveAuthorize access="/questionAndAnswer/getAnswerListByQuestionId.json">
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
        <c:set var="isHaveAuthorize" value="true" />
		</jkd:haveAuthorize>
		<jkd:haveAuthorize access="/questionAndAnswer/saveAnswer.json">
		<c:if test="${isHaveAuthorize}">,</c:if>
        '-',{
        	text: '<fmt:message key="button.add"/>', 
        	iconCls: 'add', 	
        	handler: this.addProductAnswer, 
        	scope: this
        }
        </jkd:haveAuthorize>
        ];
    	this.callParent();
    	
    	this.gsm = this.getSelectionModel();
    	this.store.on('beforeload', function(store, options) {
	        Ext.apply(store.proxy.extraParams, {
	        	questionId: this.record.data.questionId
			});
	    }, this);
    },
    
    transferData : function(record){
    	this.record = record;
    	this.store.load();
    },
   	
   	addProductAnswer : function(){
		var answerFormPanel = Ext.create('MyExt.productManager.ProductAnswerFormPanel', {
			id: 'addProductAnswer@ProductManager',
			title: '<fmt:message key="product.answer.addAnswer"/>',
			questionId : this.record.data.questionId,
		});
		
		var buttons = [{
			text: '<fmt:message key="button.save"/>',
			handler: function(){
	            if(answerFormPanel.form.isValid()){
	            	Ext.MessageBox.confirm('<fmt:message key="ajax.confirm"/>', '<fmt:message key="save.confirm"/>', function(e){
						if(e == 'yes'){
			                answerFormPanel.form.submit({
			                    waitMsg: 'Loading...',
			                    url: '<c:url value="/questionAndAnswer/saveAnswer.json"/>',
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
		openWin('<fmt:message key="product.answer.addAnswer"/>', answerFormPanel, buttons, 450, 200);
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
	},
	
    booleanRenderer: function(value, meta, record) { 
       	if(value =="true") {
            return '<b><fmt:message key="product.question.status_1"/></b>';
        }else{
            return '<fmt:message key="product.question.status_0"/>';
        }  
   	}
});