<%@ include file="/WEB-INF/pages/scripts/taglibs.jsp"%>

Ext.define('HelpQuestion', {
	extend: 'Ext.data.Model',
	idProperty: 'questionId',
    fields: [
		{name: 'questionId',	mapping: 'questionId',		type: 'int'},
		{name: 'sort',	 		mapping: 'sort',		    type: 'int'},
		{name: 'type',	 		mapping: 'type',		    type: 'int'},
		{name: 'name',	 		mapping: 'name',			type: 'string'},
		{name: 'questionDesc',	mapping: 'questionDesc',	type: 'string'},
		{name: 'isNoteRed',		mapping: 'isNoteRed',		type: 'bool'},
		{name: 'createTime',    mapping: 'createTime',		type: 'string'},
		{name: 'updateTime',    mapping: 'updateTime',		type: 'string'}
	]
});

Ext.define('MyExt.systemManager.HelpQuestionListPanel', {
    extend : 'Ext.grid.GridPanel',
    requires : ['Ext.ux.grid.GridHeaderFilters','Ext.ux.grid.Exporter'],
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
        	pageSize: 50,
	        autoLoad: false,
			autoDestroy: true,
			sortOnLoad: true,
			remoteSort: true,
			model: 'HelpQuestion',
			proxy: {
				type: 'ajax',
				url: '<c:url value="/helpQuestion/list.json"/>',
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
      
		this.recommendStore = Ext.create('Ext.data.Store', {
        	autoDestroy: true,
        	model: 'InitModel',
        	data: [
        		{id: 0, name: '<fmt:message key="button.no"/>'},
        		{id: 1, name: '<fmt:message key="button.yes"/>'}
        	]
        });
        
        
		this.columns = [
			{text: '<fmt:message key="help.question.questionId"/>', dataIndex: 'questionId', width: 70,  sortable : true,
        		align: 'center'
        	},
        	{text: '<fmt:message key="help.question.type" />', dataIndex: 'type', width: 100, sortable: true,
            	filter: {
            		xtype: 'combobox',
			        displayField: 'name',
			        valueField: 'id',
			        store: Ext.create('Ext.data.Store', {
						autoDestroy: true,
						model: 'InitModel',
						data: [
							{id: '1', name: '<fmt:message key="help.question.type1"/>'},
							{id: '2', name: '<fmt:message key="help.question.type2"/>'},
						]
					}),
			        queryMode: 'local',
			        typeAhead: true
            	},
            	renderer: this.typeRenderer
            },
            {text: '<fmt:message key="help.question.isNoteRed"/>', dataIndex: 'isNoteRed', width: 80,  sortable : true,
        		align: 'center',
        		renderer: this.rendererStatus,
        		filter: {
					xtype: 'combobox',
			        displayField: 'name',
			        valueField: 'id',
			        store: this.recommendStore,
			        queryMode: 'local',
			        typeAhead: true
				}
        	},
        	{text: '<fmt:message key="help.question.sort"/>', dataIndex: 'sort', width: 80,  sortable : true,
        		align: 'center'
        	},
        	{text: '<fmt:message key="help.question.name"/>', dataIndex: 'name', width: 200,  sortable : true,
        		filter: {xtype: 'textfield'}
        	},
        	{text: '<fmt:message key="help.question.questionDesc"/>', dataIndex: 'questionDesc',   width: 300, sortable : true,
        		filter: {xtype: 'textfield'}
        	},
        	{text: '<fmt:message key="help.question.createTime"/>', dataIndex: 'createTime', width: 180,  sortable : true,
        		align: 'center'
        	},
        	{text: '<fmt:message key="help.question.updateTime"/>', dataIndex: 'updateTime', width: 180,  sortable : true,
        		align: 'center'
			}
        ];
        
        this.bbar = new Ext.PagingToolbar({
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
		
	    this.tbar = [
	    <jkd:haveAuthorize access="/helpQuestion/saveHelpQuestion.json">
	    {
        	text: '<fmt:message key="button.add"/>', 
        	iconCls: 'add', 
        	handler: this.saveHelpQuestion, 
        	scope: this
        }
        <c:set var="isHaveAuthorize" value="true" />
		</jkd:haveAuthorize>
		<jkd:haveAuthorize access="/helpQuestion/deleteHelpQuestion.json">
		<c:if test="${isHaveAuthorize}">,</c:if>
        '-',{
        	text: '<fmt:message key="button.delete"/>', 
        	iconCls: 'delete',	
        	handler: this.deleteHelpQuestion, 
        	scope: this
        }
        </jkd:haveAuthorize>
        ];
    	this.callParent(arguments);
    	
    	<jkd:haveAuthorize access="/helpQuestion/list.json">
    	this.store.on('beforeload', function(store, options) {
	        Ext.apply(store.proxy.extraParams, {
	        	filters: Ext.JSON.encode(this.filters)
			});
	    }, this);
	    this.store.load();
	    </jkd:haveAuthorize>
	    
	    this.gsm = this.getSelectionModel();
	    <jkd:haveAuthorize access="/helpQuestion/getHelpQuestionById.json">
	    this.on('itemdblclick', this.onDbClick, this);
	    </jkd:haveAuthorize>
    },
    
  	saveHelpQuestion : function(){
		var helpQuestionForm = Ext.create('MyExt.systemManager.HelpQuestionFormPanel', {id: 'helpQuestionForm@SystemManager', title: '<fmt:message key="discovery.hot.title.add"/>'});
		var buttons = [{
			text: '<fmt:message key="button.save"/>',
			handler: function(){
	            if(helpQuestionForm.form.isValid()){
	            	Ext.MessageBox.confirm('<fmt:message key="ajax.confirm"/>', '<fmt:message key="save.confirm"/>', function(e){
						if(e == 'yes'){
			                helpQuestionForm.form.submit({
			                    waitMsg: 'Loading...',
			                    url: '<c:url value="/helpQuestion/saveHelpQuestion.json"/>',
			                    scope: this,
			                    success: function(form, action) {
			                        var responseObject = Ext.JSON.decode(action.response.responseText);
			                        if(responseObject.error == false){
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
		openWin('<fmt:message key="discovery.hot.title.add"/>', helpQuestionForm, buttons, 450, 300);
	},
	
	deleteHelpQuestion : function() {
		var rowsData = [];		
		var records = this.gsm.getSelection();
		if(records.length == 0){
			showMsg('<fmt:message key="ajax.confirm"/>', '<fmt:message key="ajax.no.record"/>');
			return;
		}	
		for(var i = 0; i < records.length; i++){			
			rowsData.push(records[i].data.questionId);	
		}
		
		Ext.Msg.confirm('<fmt:message key="ajax.confirm"/>', '<fmt:message key="delete.confirm"/>', function(e){
			if(e == 'yes'){
				Ext.Ajax.request({
		        	url: '<c:url value="/helpQuestion/deleteHelpQuestion.json"/>',
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
	
    onDbClick : function(view, record, item, index, e, eOpts) {
    
    	var helpQuestionFormPanel = Ext.create('MyExt.systemManager.HelpQuestionFormPanel', {id: 'helpQuestionForm@SystemManager', isEditor: true, title: '<fmt:message key="help.question.modify"/>'});
    	helpQuestionFormPanel.load({   
    		waitMsg: '<fmt:message key="ajax.waitMsg"/>',   
    		waitTitle: '<fmt:message key="ajax.waitTitle"/>', 
    		url: '<c:url value="/helpQuestion/getHelpQuestionById.json"/>', 
    		params: {questionId: record.data.questionId}, 
    		success : function(form, action) {
             	var data = action.result.data;
               	if(data.isNoteRed == 1){
                  	Ext.getCmp("qy").setValue(true);
                }else{
                 	Ext.getCmp("ty").setValue(true);
                }
            },
    		failure : function (form, action) {   
     			showMsg('<fmt:message key="ajax.waitTitle"/>', '<fmt:message key="ajax.load.failure"/>', 4);   
    		}   
   		});
   		
    	var buttons = [
    	<jkd:haveAuthorize access="/helpQuestion/saveHelpQuestion.json">
    	{
			text: '<fmt:message key="button.save"/>',
			handler: function(){
				if(helpQuestionFormPanel.form.isValid()){
					Ext.MessageBox.confirm('<fmt:message key="ajax.confirm"/>', '<fmt:message key="save.confirm"/>', function(e){
						if(e == 'yes'){
			                helpQuestionFormPanel.form.submit({
			                    waitMsg: 'Loading...',
			                    url: '<c:url value="/helpQuestion/saveHelpQuestion.json"/>',
			                    scope: this,
			                    success: function(form, action) {
			                        var responseObject = Ext.JSON.decode(action.response.responseText);
			                        if(responseObject.error == false){
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
		},
		</jkd:haveAuthorize>
		{
			text: '<fmt:message key="button.cancel"/>',
			handler : function(){popWin.close();},
			scope: this
		}];
		openWin('<fmt:message key="help.question.modify"/>', helpQuestionFormPanel, buttons, 450, 300);
    },

	rendererStatus : function(val){
		var str =  "";
		if(val == 1){
			str = '<b><fmt:message key="button.yes"/></b>';
		}else{
			str = '<b><fmt:message key="button.no"/></b>';
		}
		return str;
	},
	typeStatus : function(val){
		var str =  "";
		if(val == 1){
			str = '<b><fmt:message key="help.question.type1"/></b>';
		}else{
			str = '<b><fmt:message key="help.question.type2"/></b>';
		}
		return str;
	}
	
});