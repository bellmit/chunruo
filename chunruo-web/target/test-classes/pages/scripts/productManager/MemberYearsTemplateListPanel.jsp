<%@ include file="/WEB-INF/pages/scripts/taglibs.jsp"%>

Ext.define('MemberYearsTemplate', {
	extend: 'Ext.data.Model',
    fields: [
    	{name: 'templateId',	mapping: 'templateId',	type: 'int'},
		{name: 'status',	 	mapping: 'status',		type: 'bool'},
		{name: 'isDelete',	    mapping: 'isDelete',	type: 'bool'},
		{name: 'yearsNumber',   mapping: 'yearsNumber', type: 'string'},
		{name: 'yearsName',	    mapping: 'yearsName',   type: 'string'},
		{name: 'price',		    mapping: 'price',	    type: 'string'},
		{name: 'profit',		mapping: 'profit',	    type: 'string'},
		{name: 'sort',		    mapping: 'sort',	    type: 'int'},
		{name: 'level',		    apping: 'level',	    type: 'int'},
		{name: 'createTime',	mapping: 'createTime',	type: 'string'},
		{name: 'updateTime',	mapping: 'updateTime',	type: 'string'}
    ]
});

Ext.define('MyExt.productManager.MemberYearsTemplateListPanel', {
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
			model: 'MemberYearsTemplate',
			proxy: {
				type: 'ajax',
				url: '<c:url value="/memberYears/templateList.json"/>',
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
		
		this.rendererlevelStore = Ext.create('Ext.data.Store', {
        	autoDestroy: true,
        	model: 'InitModel',
        	data: [
	        	{id: 5, name: '<fmt:message key="user.level5"/>'},
	        	{id: 4, name: '<fmt:message key="user.level4"/>'},
        		{id: 3, name: '<fmt:message key="user.level3"/>'},
        		{id: 2, name: '<fmt:message key="user.level2"/>'},
        		{id: 1, name: '<fmt:message key="user.level1"/>'},
        		{id: 0, name: '<fmt:message key="user.level0"/>'}
        	]
        });
		
		this.columns = [
			{text: '<fmt:message key="member.years.template.templateId"/>', dataIndex: 'templateId', width: 50, sortable : true},
        	{text: '<fmt:message key="member.years.template.level"/>', dataIndex: 'level', width: 80, sortable : true, locked: true,
        		renderer : this.rendererlevelStuts,
        		filter: {
					xtype: 'combobox',
			        displayField: 'name',
			        valueField: 'id',
			        store: this.rendererlevelStore,
			        queryMode: 'local',
			        typeAhead: true
				},
        	},
        	{text: '<fmt:message key="member.years.template.price"/>', dataIndex: 'price', width: 50,sortable : true},
        	{text: '<fmt:message key="member.years.template.profit"/>', dataIndex: 'profit', width: 100,sortable : true},
        	{text: '<fmt:message key="member.years.template.yearsNumber"/>', dataIndex: 'yearsNumber', width: 100, sortable : true},
        	{text: '<fmt:message key="member.years.template.yearsName"/>', dataIndex: 'yearsName', width: 140, sortable : true},
        	{text: '<fmt:message key="member.years.template.sort"/>', dataIndex: 'sort', width: 50,sortable : true},
        	{text: '<fmt:message key="member.years.template.status"/>', dataIndex: 'status', width: 80, sortable : true,
       			renderer : function(value){
					if(value == 0){
						return '<span style="color:red;"><b><fmt:message key='button.no'/></b></span>';
					}else if(value == 1){
						return '<span style="color:blue;"><b><fmt:message key='button.yes'/></b></span>';
					}
				}
        	}
        ];
        
        this.tbar = [
        <jkd:haveAuthorize access="/memberYears/saveTemplate.json">
        {
        	text: '<fmt:message key="template.save.update"/>', 
        	iconCls: 'add', 	
        	handler: this.addTemplate,
        	scope: this
        }
        <c:set var="isHaveAuthorize" value="true" />
		</jkd:haveAuthorize>
		<jkd:haveAuthorize access="/memberYears/deleteTemplate.json">
		<c:if test="${isHaveAuthorize}">,</c:if>
        '-',{
        	text: '<fmt:message key="button.delete"/>', 
        	iconCls: 'delete',	
        	handler: this.deleteTemplate, 
        	scope: this
        }
        <c:set var="isHaveAuthorize" value="true" />
		</jkd:haveAuthorize>
		<jkd:haveAuthorize access="/memberYears/setTemplateStatus.json">
		<c:if test="${isHaveAuthorize}">,</c:if>
        '-',{
        	text: '<fmt:message key="button.enable"/>', 
        	iconCls: 'enable',	
        	handler: this.enableTemplate, 
        	scope: this
        }
        <c:set var="isHaveAuthorize" value="true" />
		</jkd:haveAuthorize>
		<jkd:haveAuthorize access="/memberYears/setTemplateStatus.json">
		<c:if test="${isHaveAuthorize}">,</c:if>
        '-',{
        	text: '<fmt:message key="button.disable"/>', 
        	iconCls: 'Cancel',	
        	handler: this.cancelTemplate, 
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
		
		this.questionList = Ext.create('Ext.grid.GridPanel', {
			region: 'west',
			header: false,
			width: 580,
			autoScroll: true,   
			closable: true,
			selType: 'checkboxmodel',
			multiSelect: true,
			columnLines: true,
			animCollapse: true,
		    enableLocking: true,
		    columns: this.columns,
		    bbar: this.pagingToolbar,
			plugins: ['gridHeaderFilters','gridexporter'],
		    store: this.store,
		    viewConfig: {
		        stripeRows: true,
		        enableTextSelection: true
		    }
	    });     
	    
	    this.east =  Ext.create('MyExt.productManager.MemberGiftPanel', {
        	region: 'center',
	        header: false,
	        autoScroll: true
        });
    	
    	this.items = [this.questionList, this.east];	
		this.east.hide();
		this.callParent(arguments);
	   
	    this.gsm = this.questionList.getSelectionModel();
	    <jkd:haveAuthorize access="/memberYears/templateList.json">
	   	this.store.on('beforeload', function(store, options) {
	        Ext.apply(store.proxy.extraParams, {
	        	filters: Ext.JSON.encode(this.questionList.filters)
			});
	    }, this);
	    this.store.load(); 
	    </jkd:haveAuthorize>  
	    
	    this.questionList.on('itemdblclick', function(view, record, item, index, e, eOpts) {
	    	<jkd:haveAuthorize access="/memberYears/getMemberGiftListByTemplateId.json">
	    	this.east.transferData( record);
	    	this.east.show();
	    	</jkd:haveAuthorize>  
	    }, this);
    },
    
     addTemplate : function(){
       	var records = this.gsm.getSelection();
       	if(records.length > 1 ){
			showMsg('<fmt:message key="ajax.confirm"/>', '<fmt:message key="user.approve.records"/>');
			return;
		}
		
		var templateId = '';
		var yearsNumber = '';
		var yearsName = '';
		var profit = '';
		var price = '';
		var level = '';
		var sort = '';
		if(records.length == 1){
			templateId = records[0].data.templateId;
			yearsNumber = records[0].data.yearsNumber;
			yearsName = records[0].data.yearsName;
			profit = records[0].data.profit;
			price = records[0].data.price;
			level = records[0].data.level;
			sort = records[0].data.sort;
		}
	
    	var memberYearsTemplateFormPanel = Ext.create('MyExt.productManager.MemberYearsTemplateFormPanel', {
			id: 'add@memberYearsTemplateFormPanel' + this.id,
    		viewer: this.viewer,
    		edit: false,
    		templateId:templateId,
    		yearsNumber:yearsNumber,
    		yearsName:yearsName,
    		profit:profit,
    		price:price,
    		level:level,
    		sort:sort
   	 	});
   	 	
    	var buttons = [{
			text: '<fmt:message key="button.save"/>',
			handler: function(){
				if(memberYearsTemplateFormPanel.isValid()){
					Ext.MessageBox.confirm('<fmt:message key="ajax.confirm"/>', '<fmt:message key="save.confirm"/>', function(e){
						if(e == 'yes'){
							memberYearsTemplateFormPanel.submit({
		                    	url: '<c:url value="/memberYears/saveTemplate.json"/>',
		                    	waitMsg: '<fmt:message key="ajax.loading"/>',
		                    	scope: this,
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
				}
			},
			scope: this
		},{
			text: '<fmt:message key="button.cancel"/>',
			handler : function(){popWin.close();},
			scope: this
		}];
      	openWin('<fmt:message key="button.add"/>', memberYearsTemplateFormPanel, buttons, 460, 220);
    },     
    
    deleteTemplate : function() {
   		var rowsData = [];		
		var records = this.gsm.getSelection();
		if(records.length == 0){
			showMsg('<fmt:message key="ajax.confirm"/>', '<fmt:message key="ajax.no.record"/>');
			return;
		}	
		for(var i = 0; i < records.length; i++){	
			rowsData.push(records[i].data.templateId);	
		}
		
	    Ext.MessageBox.confirm('<fmt:message key="ajax.confirm"/>', '<fmt:message key="delete.confirm"/>', function(e){
			if(e == 'yes'){
	        	Ext.Ajax.request({
		        	url: '<c:url value="/memberYears/deleteTemplate.json"/>',
		         	method: 'post',
					scope: this,
					params:{idListGridJson: Ext.JSON.encode(rowsData), isEnabled: true},
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
    
	enableTemplate : function() {
   		var rowsData = [];		
		var records = this.gsm.getSelection();
		if(records.length == 0){
			showMsg('<fmt:message key="ajax.confirm"/>', '<fmt:message key="ajax.no.record"/>');
			return;
		}	
		for(var i = 0; i < records.length; i++){	
			rowsData.push(records[i].data.templateId);	
		}
		
	    Ext.MessageBox.confirm('<fmt:message key="ajax.confirm"/>', '<fmt:message key="save.confirm"/>', function(e){
			if(e == 'yes'){
	        	Ext.Ajax.request({
		        	url: '<c:url value="/memberYears/setTemplateStatus.json"/>',
		         	method: 'post',
					scope: this,
					params:{idListGridJson: Ext.JSON.encode(rowsData), isEnabled: true},
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
   	
   	cancelTemplate : function() {
   		var rowsData = [];		
		var records = this.gsm.getSelection();
		if(records.length == 0){
			showMsg('<fmt:message key="ajax.confirm"/>', '<fmt:message key="ajax.no.record"/>');
			return;
		}	
		for(var i = 0; i < records.length; i++){			
			rowsData.push(records[i].data.templateId);	
		}
		
	    Ext.MessageBox.confirm('<fmt:message key="ajax.confirm"/>', '<fmt:message key="save.confirm"/>', function(e){
			if(e == 'yes'){
	        	Ext.Ajax.request({
		        	url: '<c:url value="/memberYears/setTemplateStatus.json"/>',
		         	method: 'post',
					scope: this,
					params:{idListGridJson: Ext.JSON.encode(rowsData), isEnabled: false},
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
   	
   	 rendererlevelStuts : function(val){
	     if(val == 1) {
            return '<b><fmt:message key="user.level1"/></b>';
         }else if(val == 2){
            return '<b><fmt:message key="user.level2"/></b>';
         }else if(val == 3){
            return '<b><fmt:message key="user.level3"/></b>';
        }else if(val == 4){
            return '<b><fmt:message key="user.level4"/></b>';
        }else if(val == 5){
            return '<b><fmt:message key="user.level5"/></b>';
        }else{
           return '<b><fmt:message key="user.level0"/></b>';
        }
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